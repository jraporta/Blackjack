package com.cat.itacademy.s05.blackjack.controllers;

import com.cat.itacademy.s05.blackjack.model.Player;
import com.cat.itacademy.s05.blackjack.services.GameService;
import com.cat.itacademy.s05.blackjack.services.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class PlayerController {

    private final PlayerService playerService;
    private final GameService gameService;

    public PlayerController(PlayerService playerService, GameService gameService) {
        this.playerService = playerService;
        this.gameService = gameService;
    }

    @GetMapping("/ranking")
    public Mono<ResponseEntity<List<Player>>> getRanking(){
        return playerService.getRanking().
                map(ResponseEntity::ok);
    }

    @PutMapping("/player/{playerId}")
    public Mono<ResponseEntity<Player>> updatePlayerName(@PathVariable Long playerId, @RequestBody String playerName){
        return playerService.updatePlayerName(playerId, playerName)
                .flatMap(gameService::updatePlayerNameInGames)
                .map(ResponseEntity::ok);
    }

}