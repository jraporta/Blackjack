package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.dto.GameDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.Player;
import reactor.core.publisher.Mono;

public interface GameService {
    Mono<String> createGame(String playerName);

    Mono<Game> getGame(String gameId);

    Mono<GameDTO> getGameDTO(String gameId);

    Mono<Game> saveGame(Game game);

    Mono<Void> deleteGame(String id);

    Mono<Player> updatePlayerNameInGames(Player player);

    Mono<Void> executePlay(String game, PlayDTO play);
}
