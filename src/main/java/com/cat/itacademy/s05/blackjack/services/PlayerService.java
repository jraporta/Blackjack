package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.model.Player;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PlayerService {
    Mono<Player> getPlayer(String playerName);

    Mono<Player> getPlayerById(Long playerId);

    Mono<Player> savePlayer(Player player);

    Mono<Player> createPlayer(String playerName);

    Mono<Player> addMoney(Long playerId, int money);

    Mono<Player> subtractMoney(Long playerId, int money);

    Mono<List<Player>> getRanking();

    Mono<Player> updatePlayerName(Long playerId, String playerName);
}
