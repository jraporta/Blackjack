package com.cat.itacademy.s05.blackjack.controllers;

import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayResponseDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayerDTO;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.Player;
import com.cat.itacademy.s05.blackjack.services.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {

    private final GameService gameService;

    public Controller(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/game/new")
    public ResponseEntity<String> createGame(@RequestBody String playerId){
        String gameId = gameService.createGame(playerId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created game with id: " + gameId);
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<Game> getGame(@PathVariable String gameId){
        return ResponseEntity.ok(gameService.getGame(gameId));
    }

    @PostMapping("/game/{id}/play")
    public ResponseEntity<PlayResponseDTO> executePlay(@PathVariable String gameId, @RequestBody PlayDTO play){
        return ResponseEntity.ok(gameService.executePlay(gameId, play));
    }

    @DeleteMapping("/game/{id}/delete")
    public ResponseEntity<String> deleteGame(@PathVariable String gameId){
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
