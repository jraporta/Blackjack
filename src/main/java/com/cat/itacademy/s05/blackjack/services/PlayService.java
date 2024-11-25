package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.enums.Play;
import com.cat.itacademy.s05.blackjack.enums.PlayerStatus;
import com.cat.itacademy.s05.blackjack.exceptions.custom.*;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import com.cat.itacademy.s05.blackjack.utils.BlackjackHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PlayService {

    private final PlayerService playerService;
    private final DeckService deckService;
    private final BlackjackHelper helper;

    public PlayService(PlayerService playerService, DeckService deckService, BlackjackHelper helper) {
        this.playerService = playerService;
        this.deckService = deckService;
        this.helper = helper;
    }

    public Mono<Game> executePlay(Game game1, PlayDTO play) {
        return validatePlay(game1, play)
                .flatMap(game -> executePlayLogic(game, play))
                .flatMap(game -> {
                    if (allPassed(game)) {
                        game.setConcluded(true);
                    }else {
                        switchToNextActivePlayer(game);
                    }
                    return Mono.just(game);
                });
    }

    private Mono<Game> validatePlay(Game game, PlayDTO play) {
        //Check game is not concluded
        if (game.isConcluded()) return Mono.error(new InvalidPlayException("Game is over, no more plays accepted."));
        PlayerInGame activePlayer = game.getActivePlayer();
        //Check active player equals play player
        if (!activePlayer.getId().equals(play.playerId())){
            return Mono.error(new InvalidPlayException("It's the turn of the player with id: " + activePlayer.getId()));
        }
        //Check player has a bet or play is INITIAL_BET
        if (activePlayer.getStatus().equals(PlayerStatus.PENDING_BET) && !play.play().equals(Play.INITIAL_BET)){
            return Mono.error(new InvalidPlayException("Player has no bet. First play must be 'INITIAL_BET'."));
        }
        //Check play contains valid bet if play is INITIAL_BET
        if (play.play().equals(Play.INITIAL_BET) && play.bet() <= 0) {
            return Mono.error(new InvalidPlayException("Initial bet play must have a valid bet."));
        }
        return Mono.just(game);
    }

    private Mono<Game> executePlayLogic(Game game, PlayDTO play) {
        return switch(play.play()){
            case Play.INITIAL_BET -> playInitialBet(game,play.bet());
            case Play.HIT -> playHit(game);
            case Play.DOUBLE -> playDouble(game);
            case Play.SPLIT -> playSplit(game);
            case Play.STAND -> playStand(game);
            case Play.SURRENDER -> playSurrender(game);
        };
    }

    //TODO limit bets to specific amounts?
    private Mono<Game> playInitialBet(Game game, int bet) {
        PlayerInGame player = game.getActivePlayer();
        if (player.getStatus() != PlayerStatus.PENDING_BET) {
            return Mono.error(new InvalidPlayException("'INITIAL_BET' is an invalid play: player already has a bet."));
        }
        player.setBet(bet);
        player.setStatus(PlayerStatus.WAITING_FOR_DEAL);
        return playerService.subtractMoney(player.getId(), player.getBet())
                .flatMap(unused -> {
                    if (allWaitingForDeal(game)) {
                        return dealCards(game);
                    }
                    return Mono.just(game);
                });
    }

    private Mono<Game> playHit(Game game) {
        PlayerInGame player = game.getActivePlayer();
        deckService.dealCard(game.getDeck(), player.getCards());
        if (helper.isBust(player.getCards())) {
            player.setStatus(PlayerStatus.BUST);
        } if (helper.getHandValue(player.getCards()) == 21) {
            player.setStatus(PlayerStatus.STAND);
        }
        return Mono.just(game);
    }

    private Mono<Game> playDouble(Game game) {
        PlayerInGame player = game.getActivePlayer();
        if (player.getCards().size() > 2) {
            return Mono.error(new InvalidPlayException("Double is only allowed immediately after the initial deal."));
        }
        int initialBet = player.getBet();
        player.setBet(initialBet * 2);
        deckService.dealCard(game.getDeck(), player.getCards());
        if (helper.isBust(player.getCards())) {
            player.setStatus(PlayerStatus.BUST);
        }else {
            player.setStatus(PlayerStatus.STAND);
        }
        return playerService.subtractMoney(player.getId(), initialBet)
                .then(Mono.defer(() -> Mono.just(game)));
    }

    //TODO cards with the same value (f.e. King, Queen) should allow split?
    private Mono<Game> playSplit(Game game) {
        PlayerInGame player = game.getActivePlayer();
        if (player.getCards().size() > 2) {
            return Mono.error(new InvalidPlayException("Split is only allowed immediately after the initial deal."));
        }
        if (player.getCards().get(0).rank() != player.getCards().get(1).rank()) {
            return Mono.error(new InvalidPlayException("Split is only allowed if the two cards have the same rank."));
        }
        PlayerInGame splitPlayer = new PlayerInGame(player.getId(),player.getName());
        game.getPlayers().add(game.getActivePlayerIndex() + 1 , splitPlayer);
        splitPlayer.setBet(player.getBet());
        splitPlayer.setStatus(PlayerStatus.PLAYING);
        splitPlayer.getCards().add(player.getCards().removeLast());
        deckService.dealCard(game.getDeck(), player.getCards());
        deckService.dealCard(game.getDeck(), splitPlayer.getCards());
        switchToNextActivePlayer(game);
        return playerService.subtractMoney(player.getId(), player.getBet())
                .then(Mono.defer(() -> Mono.just(game)));
    }

    private Mono<Game> playStand(Game game) {
        PlayerInGame player = game.getActivePlayer();
        player.setStatus(PlayerStatus.STAND);
        return Mono.just(game);
    }

    private Mono<Game> playSurrender(Game game) {
        PlayerInGame player = game.getActivePlayer();
        if (player.getCards().size() > 2) {
            return Mono.error(new InvalidPlayException("Surrender is only allowed immediately after the initial deal."));
        }
        player.setStatus(PlayerStatus.SURRENDER);
        return Mono.just(game);
    }

    private boolean allPassed(Game game) {
        return game.getPlayers()
                .stream()
                .allMatch(player -> player.getStatus() != PlayerStatus.PENDING_BET
                        && player.getStatus() != PlayerStatus.WAITING_FOR_DEAL
                        && player.getStatus() != PlayerStatus.PLAYING);
    }

    private boolean allWaitingForDeal(Game game) {
        return game.getPlayers()
                .stream()
                .allMatch(player -> player.getStatus() == PlayerStatus.WAITING_FOR_DEAL);
    }

    private Mono<Game> dealCards(Game game) {
        deckService.dealCard(game.getDeck(), game.getCroupier().getCards());
        deckService.dealCard(game.getDeck(), game.getCroupier().getCards());
        game.getPlayers().forEach(player -> {
            deckService.dealCard(game.getDeck(), player.getCards());
            deckService.dealCard(game.getDeck(), player.getCards());
            player.setStatus(PlayerStatus.PLAYING);
        });
        return Mono.just(game);
    }

    private void switchToNextActivePlayer(Game game) {
        do {
            increaseActivePlayerIndex(game);
        } while (game.getActivePlayer().getStatus() != PlayerStatus.PENDING_BET
                && game.getActivePlayer().getStatus() != PlayerStatus.WAITING_FOR_DEAL
                && game.getActivePlayer().getStatus() != PlayerStatus.PLAYING);
    }

    private void increaseActivePlayerIndex(Game game) {
        game.setActivePlayerIndex(game.getActivePlayerIndex() + 1);
        if (game.getActivePlayerIndex() >= game.getPlayers().size()) game.setActivePlayerIndex(0);
    }

}
