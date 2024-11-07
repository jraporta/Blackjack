package com.cat.itacademy.s05.blackjack.repositories;

import com.cat.itacademy.s05.blackjack.model.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface GameRepository extends ReactiveMongoRepository<Game, String> {
}
