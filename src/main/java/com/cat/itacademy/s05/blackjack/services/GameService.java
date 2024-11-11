package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayerDTO;
import com.cat.itacademy.s05.blackjack.enums.Play;
import com.cat.itacademy.s05.blackjack.exceptions.GameNotFoundException;
import com.cat.itacademy.s05.blackjack.exceptions.NoBetPlacedException;
import com.cat.itacademy.s05.blackjack.exceptions.NotActivePlayerException;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.Player;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import com.cat.itacademy.s05.blackjack.repositories.GameRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerService playerService;

    public GameService(GameRepository gameRepository, PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
    }

    public Mono<String> createGame(String playerName) {
        return playerService.getPlayer(playerName)
                .switchIfEmpty(playerService.createPlayer(playerName))
                .map(player -> new PlayerInGame(player.getId(),player.getName()))
                .flatMap(playerInGame -> {
                    List<PlayerInGame> players = new ArrayList<>();
                    players.add(playerInGame);
                    Game game = new Game();
                    game.setPlayers(players);
                    return gameRepository.save(game);
                })
                .map(Game::getId);
    }

    public Mono<Game> getGame(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(() -> new GameNotFoundException("No game with id " + gameId)));
    }

    public Flux<Game> getAllGames() {
        return gameRepository.findAll();
    }

    //TODO
    public Mono<Game> executePlay(String gameId, PlayDTO play) {
        return getGame(gameId)
                .flatMap(game -> validatePlay(game, play))
                .flatMap(game -> executePlayLogic(game, play))
                .flatMap(game -> {
                    game.changeActivePlayer();
                    return gameRepository.save(game);
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
        game.getPlayers().get(game.getActivePlayer()).setBet(play.bet());
        return  Mono.just(game);
    }

    private Mono<Game> validatePlay(Game game, PlayDTO play) {
        long activePlayerId = game.getPlayers().get(game.getActivePlayer()).getId();
        if (activePlayerId != play.playerId()){
            return Mono.error(new NotActivePlayerException("It's the turn of the player with id: " + activePlayerId));
        }
        if (game.getPlayers().get(game.getActivePlayer()).getBet() == 0
                && play.play() != Play.INITIAL_BET){
            return Mono.error(new NoBetPlacedException("Player has no bet. First play must be 'INITIAL_BET'."));
        }
        return Mono.just(game);
    }

    //TODO
    public List<PlayerDTO> getRanking() {
        return new ArrayList<PlayerDTO>();
    }

    //TODO
    public Player updatePlayerName(String playerId, String playerName) {
        return new Player();
    }
}
