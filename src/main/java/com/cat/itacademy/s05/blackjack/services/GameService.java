package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayResponseDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayerDTO;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.Player;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import com.cat.itacademy.s05.blackjack.repositories.GameRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    //TODO: read https://projectreactor.io/docs/core/release/reference/#reactive.subscribe
    public Mono<String> createGame(Long playerId) {
        List<PlayerInGame> players = new ArrayList<>();
        players.add(new PlayerInGame(playerId, "some name"));
        Game game = new Game(null, players);
        return gameRepository.save(game).map(Game::id);
    }

    //TODO
    public Game getGame(String gameId) {
        return new Game(null, null);
    }

    //TODO
    public PlayResponseDTO executePlay(String gameId, PlayDTO play) {
        return new PlayResponseDTO();
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
