package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.enums.Play;
import com.cat.itacademy.s05.blackjack.exceptions.InvalidPlayException;
import com.cat.itacademy.s05.blackjack.exceptions.NoBetPlacedException;
import com.cat.itacademy.s05.blackjack.exceptions.NotActivePlayerException;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PlayService {

    GameService gameService;

    public PlayService(GameService gameService) {
        this.gameService = gameService;
    }

    //TODO
    public Mono<Game> executePlay(String gameId, PlayDTO play) {
        return gameService.getGame(gameId)
                .flatMap(game -> validatePlay(game, play))
                .flatMap(game -> executePlayLogic(game, play))
                .flatMap(game -> {
                    game.changeActivePlayer();
                    return gameService.saveGame(game);
                });
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

    private Mono<Game> playInitialBet(Game game, PlayDTO play) {
        PlayerInGame player = game.getPlayers().get(game.getActivePlayer());
        if (player.getBet() != 0) {
            return Mono.error(new InvalidPlayException("'INITIAL_BET' is an invalid play: player already has a bet."));
        }
        player.setBet(play.bet());
        gameService.dealCard(game.getDeck(), player.getCards());
        gameService.dealCard(game.getDeck(), player.getCards());
        gameService.dealCard(game.getDeck(), game.getCroupier().getCards());
        gameService.dealCard(game.getDeck(), game.getCroupier().getCards());
        return  Mono.just(game);
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
