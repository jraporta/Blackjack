package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.exceptions.custom.PlayerNotFoundException;
import com.cat.itacademy.s05.blackjack.model.Player;
import com.cat.itacademy.s05.blackjack.repositories.PlayerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Mono<Player> getPlayer(String playerName) {
        return playerRepository.findByName(playerName);
    }

    public Mono<Player> getPlayerById(Long playerId) {
        return playerRepository.findById(playerId);
    }

    public Mono<Player> savePlayer(Player player) {
        return playerRepository.save(player);
    }

    public Mono<Player> createPlayer(String playerName) {
        return playerRepository.save(new Player(playerName));
    }

    public Mono<Player> addMoney(Long playerId, int money) {
        return playerRepository.findById(playerId)
                .flatMap(player -> {
                    player.setMoney(player.getMoney() + money);
                    return playerRepository.save(player);
                })
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player with id " + playerId + " not found.")));
    }

    public Mono<Player> subtractMoney(Long playerId, int money) {
        return addMoney(playerId, -money);
    }

    //TODO
    public Flux<Player> getRanking() {
        return playerRepository.findAllOrderByMoneyDesc();
    }

    public Mono<Player> updatePlayerName(Long playerId, String playerName) {
        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("No player found with id: " + playerId)))
                .flatMap(player -> {
                    player.setName(playerName);
                    return savePlayer(player);
                });
    }

}
