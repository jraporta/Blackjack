package com.cat.itacademy.s05.blackjack.controllers;

import com.cat.itacademy.s05.blackjack.model.Player;
import com.cat.itacademy.s05.blackjack.services.GameService;
import com.cat.itacademy.s05.blackjack.services.PlayerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "Player Management", description = "Endpoints for managing players")
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
