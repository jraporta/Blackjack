package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.exceptions.custom.GameNotFoundException;
import com.cat.itacademy.s05.blackjack.model.*;
import com.cat.itacademy.s05.blackjack.repositories.GameRepository;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerService playerService;
    private final DeckService deckService;

    public GameService(GameRepository gameRepository, PlayerService playerService, DeckService deckService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
        this.deckService = deckService;
    }

    public Mono<String> createGame(String playerName) {
        return Mono.empty()
                .then(Mono.defer(() -> {
                    Game game = new Game();
                    game.setDeck(deckService.generateDeck(new Deck()));
                    return Mono.just(game);
                }))
                .flatMap(game -> addPlayer(game, playerName))
                .flatMap(gameRepository::save)
                .map(Game::getId);
    }

    private Mono<Game> addPlayer(Game game, String playerName) {
        return playerService.getPlayer(playerName)
                .switchIfEmpty(playerService.createPlayer(playerName))
                .flatMap(player -> {
                    PlayerInGame playerInGame = new PlayerInGame(player.getId(),player.getName());
                    game.getPlayers().add(playerInGame);
                    return Mono.just(game);
                });
    }

    public Mono<Game> getGame(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(() -> new GameNotFoundException("No game with id: " + gameId)));
    }

    public Flux<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Mono<Game> saveGame(Game game) {
        return gameRepository.save(game);
    }

    public Mono<String> deleteGame(String id) {
        return getGame(id).then(Mono.defer(() -> gameRepository.deleteById(id).then(Mono.just(id))));
    }

    public Mono<Player> updatePlayerNameInGames(Player player) {
        return getAllGames()
                .flatMap(game -> updatePlayerNameInGame(game, player))
                .then(Mono.just(player));
    }

    private Mono<Game> updatePlayerNameInGame(Game game, Player player) {
        boolean isChanged = game.getPlayers().stream()
                .filter(playerInGame -> playerInGame.getId().equals(player.getId()))
                .peek(playerInGame -> playerInGame.setName(player.getName()))
                .findFirst().isPresent();
        return isChanged ? gameRepository.save(game) : Mono.empty();
    }
}
