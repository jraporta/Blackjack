package com.cat.itacademy.s05.blackjack.controllers;

import com.cat.itacademy.s05.blackjack.dto.GameDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.services.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/game/new")
    public Mono<ResponseEntity<String>> createGame(@RequestBody String playerName){
        return gameService.createGame(playerName)
                .map(gameId -> ResponseEntity.status(HttpStatus.CREATED).body("Created game with id: " + gameId));
    }

    @GetMapping("/game/{id}")
    public Mono<ResponseEntity<GameDTO>> getGame(@PathVariable String id){
        return gameService.getGameDTO(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/game/{id}/play")
    public Mono<ResponseEntity<GameDTO>> executePlay(@PathVariable String id, @RequestBody PlayDTO play){
        return gameService.executePlay(id, play)
                .then(gameService.getGameDTO(id))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/game/{id}/delete")
    public Mono<ResponseEntity<Void>> deleteGame(@PathVariable String id){
        return gameService.deleteGame(id)
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

}
