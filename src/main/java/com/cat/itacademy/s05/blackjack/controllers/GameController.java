package com.cat.itacademy.s05.blackjack.controllers;

import com.cat.itacademy.s05.blackjack.dto.GameDTO;
import com.cat.itacademy.s05.blackjack.dto.GameDTOFactory;
import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.services.GameService;
import com.cat.itacademy.s05.blackjack.services.PlayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class GameController {

    private final GameService gameService;
    private final PlayService playService;
    private final GameDTOFactory gameDTOFactory;

    public GameController(GameService gameService, PlayService playService, GameDTOFactory gameDTOFactory) {
        this.gameService = gameService;
        this.playService = playService;
        this.gameDTOFactory = gameDTOFactory;
    }

    @PostMapping("/game/new")
    public Mono<ResponseEntity<String>> createGame(@RequestBody String playerName){
        return gameService.createGame(playerName)
                .map(gameId -> ResponseEntity.status(HttpStatus.CREATED).body("Created game with id: " + gameId));
    }

    @GetMapping("/game/{id}")
    public Mono<ResponseEntity<GameDTO>> getGame(@PathVariable String id){
        return gameService.getGame(id)
                .flatMap(game -> Mono.just(ResponseEntity.ok(gameDTOFactory.getGameDTO(game))));
    }

    @PostMapping("/game/{id}/play")
    public Mono<ResponseEntity<GameDTO>> executePlay(@PathVariable String id, @RequestBody PlayDTO play){
        return playService.executePlay(id, play)
                .flatMap(game -> Mono.just(ResponseEntity.ok(gameDTOFactory.getGameDTO(game))));
    }

    @DeleteMapping("/game/{id}/delete")
    public Mono<ResponseEntity<String>> deleteGame(@PathVariable String id){
        return gameService.deleteGame(id)
                .map(gameId -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

}
