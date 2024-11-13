package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.model.Game;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CroupierService {

    public Mono<Game> resolveCroupierHand(Game game) {
        return Mono.just(game);
    }

}
