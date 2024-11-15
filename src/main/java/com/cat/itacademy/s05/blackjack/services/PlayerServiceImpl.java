package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.exceptions.custom.PlayerNotFoundException;
import com.cat.itacademy.s05.blackjack.model.Player;
import com.cat.itacademy.s05.blackjack.repositories.PlayerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Mono<Player> getPlayer(String playerName) {
        return playerRepository.findByName(playerName);
    }

    @Override
    public Mono<Player> getPlayerById(Long playerId) {
        return playerRepository.findById(playerId);
    }

    @Override
    public Mono<Player> savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public Mono<Player> createPlayer(String playerName) {
        return playerRepository.save(new Player(playerName));
    }

    @Override
    public Mono<Player> addMoney(Long playerId, int money) {
        return playerRepository.findById(playerId)
                .flatMap(player -> {
                    player.setMoney(player.getMoney() + money);
                    return playerRepository.save(player);
                })
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player with id " + playerId + " not found.")));
    }

    @Override
    public Mono<Player> subtractMoney(Long playerId, int money) {
        return addMoney(playerId, -money);
    }

    @Override
    public Mono<List<Player>> getRanking() {
        return playerRepository.findAllOrderByMoneyDesc().collectList();
    }

    @Override
    public Mono<Player> updatePlayerName(Long playerId, String playerName) {
        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("No player found with id: " + playerId)))
                .flatMap(player -> {
                    player.setName(playerName);
                    return savePlayer(player);
                });
    }

}
