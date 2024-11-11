package com.cat.itacademy.s05.blackjack.controllers;

import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayerDTO;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.Player;
import com.cat.itacademy.s05.blackjack.services.GameService;
import com.cat.itacademy.s05.blackjack.services.PlayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class Controller {

    private final GameService gameService;
    private final PlayService playService;

    public Controller(GameService gameService, PlayService playService) {
        this.gameService = gameService;
        this.playService = playService;
    }

    @PostMapping("/game/new")
    public Mono<ResponseEntity<String>> createGame(@RequestBody String playerName){
        return gameService.createGame(playerName)
                .map(gameId -> ResponseEntity.status(HttpStatus.CREATED).body("Created game with id: " + gameId));
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<Mono<Game>> getGame(@PathVariable String id){
        return ResponseEntity.ok(gameService.getGame(id));
    }

    @PostMapping("/game/{id}/play")
    public Mono<ResponseEntity<Game>> executePlay(@PathVariable String id, @RequestBody PlayDTO play){
        return playService.executePlay(id, play).flatMap(game -> Mono.just(ResponseEntity.ok(game)));
    }

    @DeleteMapping("/game/{id}/delete")
    public ResponseEntity<String> deleteGame(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<PlayerDTO>> getRanking(){
        return ResponseEntity.ok(gameService.getRanking());
    }

    @PutMapping("/player/{playerId}")
    public ResponseEntity<Player> updatePlayerName(@PathVariable String playerId, @RequestBody String playerName){
        return ResponseEntity.ok(gameService.updatePlayerName(playerId, playerName));
    }
}
