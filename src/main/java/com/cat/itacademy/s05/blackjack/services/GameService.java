package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.dto.PlayerDTO;
import com.cat.itacademy.s05.blackjack.exceptions.GameNotFoundException;
import com.cat.itacademy.s05.blackjack.model.Deck;
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
    private final DeckService deckService;

    public GameService(GameRepository gameRepository, PlayerService playerService, DeckService deckService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
        this.deckService = deckService;
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
                    Deck deck = new Deck();
                    deckService.generateDeck(deck);
                    game.setDeck(deck);
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
    public List<PlayerDTO> getRanking() {
        return new ArrayList<PlayerDTO>();
    }

    //TODO
    public Player updatePlayerName(String playerId, String playerName) {
        return new Player();
    }

    public Mono<Game> saveGame(Game game) {
        return gameRepository.save(game);
    }
}
