package com.cat.itacademy.s05.blackjack.repositories;

import com.cat.itacademy.s05.blackjack.model.Player;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerRepository extends ReactiveMongoRepository<Player, String> {

    Mono<Player> findByName(String playerName);

    Flux<Player> findAllByOrderByMoneyDesc();

}
