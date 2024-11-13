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

    GameService gameService;
    PlayerService playerService;
    DeckService deckService;
    BlackjackHelper helper;
    PrizesService prizesService;
    CroupierService croupierService;

    public PlayService(GameService gameService, PlayerService playerService, DeckService deckService,
                       BlackjackHelper helper, PrizesService prizesService, CroupierService croupierService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.deckService = deckService;
        this.helper = helper;
        this.prizesService = prizesService;
        this.croupierService = croupierService;
    }

    public Mono<Game> executePlay(String gameId, PlayDTO play) {
        return gameService.getGame(gameId)
                .flatMap(game -> validatePlay(game, play))
                .flatMap(game -> executePlayLogic(game, play))
                .flatMap(game -> {
                    if (allWaitingForDeal(game)) {
                        return dealCards(game).flatMap(gameService::saveGame);
                    }
                    if (allPassed(game)) {
                        game.setConcluded(true);
                        return croupierService.resolveCroupierHand(game)
                                .flatMap(prizesService::resolveBets)
                                .flatMap(gameService::saveGame);
                    }
                    getNextActivePlayer(game);
                    return gameService.saveGame(game);
                });
    }

    private Mono<Game> validatePlay(Game game, PlayDTO play) {
        //Check game is not concluded
        if (game.isConcluded()) return Mono.error(new GameIsOverException("Game is over, no more plays accepted."));
        PlayerInGame activePlayer = game.getActivePlayer();
        //Check active player equals play player
        if (!activePlayer.getId().equals(play.playerId())){
            return Mono.error(new NotActivePlayerException("It's the turn of the player with id: " + activePlayer.getId()));
        }
        //Check player has a bet
        if (activePlayer.getStatus() == PlayerStatus.PENDING_BET && play.play() != Play.INITIAL_BET){
            return Mono.error(new NoBetPlacedException("Player has no bet. First play must be 'INITIAL_BET'."));
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
                .then(Mono.defer(() -> Mono.just(game)));
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
        return playerService.subtractMoney(player.getId(), player.getBet())
                .then(Mono.defer(() -> {
                    PlayerInGame splitPlayer = new PlayerInGame(player.getId(),player.getName());
                    game.getPlayers().add(game.getActivePlayerId() + 1 , splitPlayer);
                    splitPlayer.setBet(player.getBet());
                    splitPlayer.setStatus(PlayerStatus.PLAYING);
                    splitPlayer.getCards().add(player.getCards().removeLast());
                    deckService.dealCard(game.getDeck(), player.getCards());
                    deckService.dealCard(game.getDeck(), splitPlayer.getCards());
                    getNextActivePlayer(game);
                    return Mono.just(game);
                }));
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
                .allMatch(player -> player.getStatus() == PlayerStatus.STAND
                        || player.getStatus() == PlayerStatus.BUST
                        || player.getStatus() == PlayerStatus.SURRENDER);
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

    private void getNextActivePlayer(Game game) {
        game.setActivePlayerId(game.getActivePlayerId() + 1);
        while (game.getActivePlayer().getStatus() == PlayerStatus.STAND
                || game.getActivePlayer().getStatus() == PlayerStatus.BUST
                || game.getActivePlayer().getStatus() == PlayerStatus.SURRENDER) {
            game.setActivePlayerId(game.getActivePlayerId() + 1);
            if (game.getActivePlayerId() >= game.getPlayers().size()) game.setActivePlayerId(0);
        }
    }

}
