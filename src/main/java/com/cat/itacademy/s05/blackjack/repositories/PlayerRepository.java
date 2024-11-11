package com.cat.itacademy.s05.blackjack.repositories;

import com.cat.itacademy.s05.blackjack.model.Player;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface PlayerRepository extends R2dbcRepository<Player, Long> {

    Mono<Player> findByName(String playerName);

}
