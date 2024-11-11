package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.enums.Play;
import com.cat.itacademy.s05.blackjack.enums.PlayerStatus;
import com.cat.itacademy.s05.blackjack.enums.Rank;
import com.cat.itacademy.s05.blackjack.exceptions.InvalidPlayException;
import com.cat.itacademy.s05.blackjack.exceptions.NoBetPlacedException;
import com.cat.itacademy.s05.blackjack.exceptions.NotActivePlayerException;
import com.cat.itacademy.s05.blackjack.model.Card;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import org.springframework.stereotype.Service;
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
                    if (allPassed(game)) {
                        resolveBets(game);
                    } else {
                        game.changeActivePlayer();
                    }
                    return gameService.saveGame(game);
                });
    }

    //TODO make profit dependant on the properties file
    private void resolveBets(Game game) {
        boolean croupierHasBlackjack = isBlackjack(game.getCroupier().getCards());
        int croupierScore = getHandValue(game.getCroupier().getCards());
        game.getPlayers().forEach(player -> {
            if (getHandValue(player.getCards()) > 21) {
                player.setStatus(PlayerStatus.BUST);
            }else if (isBlackjack(player.getCards())) {
                if (croupierHasBlackjack) {
                    player.setStatus(PlayerStatus.BLACKJACK_TIE);
                    playerService.addMoney(player.getId(), player.getBet());
                }else {
                    player.setStatus(PlayerStatus.BLACKJACK);
                    playerService.addMoney(player.getId(), (int) (player.getBet() * 1.5));
                }
            }else if (croupierHasBlackjack) {
                player.setStatus(PlayerStatus.LOOSE);
            }else if (croupierScore > 21 || getHandValue(player.getCards()) > croupierScore) {
                player.setStatus(PlayerStatus.WIN);
                playerService.addMoney(player.getId(), player.getBet() * 2);
            }else if (getHandValue(player.getCards()) == croupierScore){
                player.setStatus(PlayerStatus.TIE);
                playerService.addMoney(player.getId(), player.getBet());
            }else if (croupierScore > getHandValue(player.getCards())) {
                player.setStatus(PlayerStatus.LOOSE);
            }
        });
    }

    private boolean isBlackjack(List<Card> cards) {
        return cards.size() == 2 && getHandValue(cards) == 21;
    }

    private boolean allPassed(Game game) {
        boolean allPassed = true;
        int i = 0;
        while (allPassed && i < game.getPlayers().size()){
            allPassed = !game.getPlayers().get(i).isPassed();
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

    //TODO
    private Mono<Game> playSurrender(Game game, PlayDTO play) {
        return Mono.just(game);
    }

    //TODO
    private Mono<Game> playStand(Game game, PlayDTO play) {
        return Mono.just(game);
    }

    //TODO
    private Mono<Game> playSplit(Game game, PlayDTO play) {
        return Mono.just(game);
    }

    //TODO
    private Mono<Game> playDouble(Game game, PlayDTO play) {
        return Mono.just(game);
    }

    //TODO
    private Mono<Game> playHit(Game game, PlayDTO play) {
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
                    return  Mono.just(game);
                }));
    }

    //TODO validate player has not passed
    private Mono<Game> validatePlay(Game game, PlayDTO play) {
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
