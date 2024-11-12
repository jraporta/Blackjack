package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayerDTO;
import com.cat.itacademy.s05.blackjack.enums.Play;
import com.cat.itacademy.s05.blackjack.enums.PlayerStatus;
import com.cat.itacademy.s05.blackjack.enums.Rank;
import com.cat.itacademy.s05.blackjack.exceptions.custom.GameIsOverException;
import com.cat.itacademy.s05.blackjack.exceptions.custom.InvalidPlayException;
import com.cat.itacademy.s05.blackjack.exceptions.custom.NoBetPlacedException;
import com.cat.itacademy.s05.blackjack.exceptions.custom.NotActivePlayerException;
import com.cat.itacademy.s05.blackjack.model.Card;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PlayService {

    GameService gameService;
    PlayerService playerService;

    public PlayService(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
    }

    //TODO
    public Mono<Game> executePlay(String gameId, PlayDTO play) {
        return gameService.getGame(gameId)
                .flatMap(game -> validatePlay(game, play))
                .flatMap(game -> executePlayLogic(game, play))
                .flatMap(this::checkOutcome)
                .flatMap(game -> {
                    if (allPassed(game)) return resolveBets(game).flatMap(gameService::saveGame);
                    game.changeActivePlayer();
                    return gameService.saveGame(game);
                });
    }

    private Mono<Game> resolveBets(Game game) {
        game.setConcluded(true);
        boolean croupierHasBlackjack = isBlackjack(game.getCroupier().getCards());
        int croupierScore = getHandValue(game.getCroupier().getCards());
        return Flux.fromIterable(game.getPlayers())
                .flatMap(playerInGame -> resolveBet(playerInGame, croupierHasBlackjack, croupierScore))
                .then(Mono.defer(() -> Mono.just(game)));
    }

    //TODO make winnings dependant on the properties file
    private Publisher<?> resolveBet(PlayerInGame player, boolean croupierHasBlackjack, int croupierScore) {
        int winnings = 0;
        if (player.getStatus() == PlayerStatus.SURRENDER) {
            winnings = player.getBet() / 2;
        }else if (getHandValue(player.getCards()) > 21) {
            player.setStatus(PlayerStatus.BUST);
        }else if (isBlackjack(player.getCards())) {
            if (croupierHasBlackjack) {
                player.setStatus(PlayerStatus.BLACKJACK_TIE);
                winnings = player.getBet();
            }else {
                player.setStatus(PlayerStatus.BLACKJACK);
                winnings = (int) (player.getBet() * (1 + 1.5));
            }
        }else if (croupierHasBlackjack) {
            player.setStatus(PlayerStatus.LOOSE);
        }else if (croupierScore > 21 || getHandValue(player.getCards()) > croupierScore) {
            player.setStatus(PlayerStatus.WIN);
            winnings = player.getBet() * 2;
        }else if (getHandValue(player.getCards()) == croupierScore){
            player.setStatus(PlayerStatus.TIE);
            winnings = player.getBet();
        }else if (croupierScore > getHandValue(player.getCards())) {
            player.setStatus(PlayerStatus.LOOSE);
        }
        return playerService.addMoney(player.getId(), winnings);
    }

    private boolean isBlackjack(List<Card> cards) {
        return cards.size() == 2 && getHandValue(cards) == 21;
    }

    private boolean allPassed(Game game) {
        boolean allPassed = true;
        int i = 0;
        while (allPassed && i < game.getPlayers().size()){
            allPassed = game.getPlayers().get(i).isPassed();
            i++;
        }
        return allPassed;
    }

    private Mono<Game> checkOutcome(Game game) {
        List<Card> cards = game.getPlayers().get(game.getActivePlayer()).getCards();
        if (getHandValue(cards) >= 21) game.getPlayers().get(game.getActivePlayer()).setPassed(true);
        return Mono.just(game);
    }

    private int getHandValue(List<Card> cards) {
        AtomicInteger handValue = new AtomicInteger();
        AtomicInteger numberOfAces = new AtomicInteger();
        cards.forEach(card -> {
            handValue.addAndGet(card.rank().getValue());
            if (card.rank() == Rank.ACE) numberOfAces.getAndIncrement();
        });
        while (handValue.get() > 21 && numberOfAces.get() >0) {
            handValue.getAndAdd(-10);
            numberOfAces.decrementAndGet();
        }
        return handValue.get();
    }

    private Mono<Game> executePlayLogic(Game game, PlayDTO play) {
        return switch(play.play()){
            case Play.INITIAL_BET -> playInitialBet(game,play);
            case Play.HIT -> playHit(game, play);
            case Play.DOUBLE -> playDouble(game, play);
            case Play.SPLIT -> playSplit(game, play);
            case Play.STAND -> playStand(game, play);
            case Play.SURRENDER -> playSurrender(game, play);
        };
    }

    private Mono<Game> playSurrender(Game game, PlayDTO play) {
        PlayerInGame player = game.getPlayers().get(game.getActivePlayer());
        if (player.getCards().size() > 2) {
            return Mono.error(new InvalidPlayException("Surrender is only allowed immediately after the initial deal."));
        }
        player.setStatus(PlayerStatus.SURRENDER);
        player.setPassed(true);
        return Mono.just(game);
    }

    private Mono<Game> playStand(Game game, PlayDTO play) {
        PlayerInGame player = game.getPlayers().get(game.getActivePlayer());
        player.setPassed(true);
        return Mono.just(game);
    }

    //TODO cards with the same value (f.e. King, Queen) should allow split?
    //TODO who should be the next player?
    private Mono<Game> playSplit(Game game, PlayDTO play) {
        PlayerInGame player = game.getPlayers().get(game.getActivePlayer());
        if (player.getCards().size() > 2) {
            return Mono.error(new InvalidPlayException("Split is only allowed immediately after the initial deal."));
        }
        if (player.getCards().get(0).rank() != player.getCards().get(1).rank()) {
            return Mono.error(new InvalidPlayException("Split is only allowed if the two cards have the same rank."));
        }
        return playerService.subtractMoney(player.getId(), player.getBet())
                .then(Mono.defer(() -> gameService.addPlayer(game, player.getName())))
                .flatMap(game1 -> {
                    PlayerInGame oldHand = game1.getPlayers().get(game1.getActivePlayer());
                    PlayerInGame newHand = game1.getPlayers().getLast();
                    newHand.setBet(player.getBet());
                    newHand.setStatus(PlayerStatus.PLAYING);
                    newHand.getCards().add(oldHand.getCards().removeLast());
                    gameService.dealCard(game1.getDeck(), oldHand.getCards());
                    gameService.dealCard(game1.getDeck(), newHand.getCards());
                    return Mono.just(game1);
                });
    }

    private Mono<Game> playDouble(Game game, PlayDTO play) {
        PlayerInGame player = game.getPlayers().get(game.getActivePlayer());
        if (player.getCards().size() > 2) {
            return Mono.error(new InvalidPlayException("Double is only allowed immediately after the initial deal."));
        }
        int initialBet = player.getBet();
        player.setBet(initialBet * 2);
        gameService.dealCard(game.getDeck(), player.getCards());
        player.setPassed(true);
        return playerService.subtractMoney(player.getId(), initialBet)
                .then(Mono.defer(() -> Mono.just(game)));
    }

    private Mono<Game> playHit(Game game, PlayDTO play) {
        PlayerInGame player = game.getPlayers().get(game.getActivePlayer());
        gameService.dealCard(game.getDeck(), player.getCards());
        return Mono.just(game);
    }

    //TODO limit bets to specific amounts?
    private Mono<Game> playInitialBet(Game game, PlayDTO play) {
        PlayerInGame player = game.getPlayers().get(game.getActivePlayer());
        if (player.getBet() != 0) {
            return Mono.error(new InvalidPlayException("'INITIAL_BET' is an invalid play: player already has a bet."));
        }
        player.setBet(play.bet());
        return playerService.subtractMoney(player.getId(), player.getBet())
                .then(Mono.defer(() -> {
                    gameService.dealCard(game.getDeck(), player.getCards());
                    gameService.dealCard(game.getDeck(), player.getCards());
                    gameService.dealCard(game.getDeck(), game.getCroupier().getCards());
                    gameService.dealCard(game.getDeck(), game.getCroupier().getCards());
                    player.setStatus(PlayerStatus.PLAYING);
                    return Mono.just(game);
                }));
    }

    //TODO validate player has not passed? changeActivePlayer should skip players that have passed
    private Mono<Game> validatePlay(Game game, PlayDTO play) {
        //Check game is not concluded
        if (game.isConcluded()) return Mono.error(new GameIsOverException("Game is over, no more plays accepted."));
        long activePlayerId = game.getPlayers().get(game.getActivePlayer()).getId();
        //Check active player equals play player
        if (activePlayerId != play.playerId()){
            return Mono.error(new NotActivePlayerException("It's the turn of the player with id: " + activePlayerId));
        }
        //Check player has a bet
        if (game.getPlayers().get(game.getActivePlayer()).getBet() == 0
                && play.play() != Play.INITIAL_BET){
            return Mono.error(new NoBetPlacedException("Player has no bet. First play must be 'INITIAL_BET'."));
        }
        return Mono.just(game);
    }

}
