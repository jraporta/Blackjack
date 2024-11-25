package com.cat.itacademy.s05.blackjack.controllers;

import com.cat.itacademy.s05.blackjack.model.Player;
import com.cat.itacademy.s05.blackjack.services.GameService;
import com.cat.itacademy.s05.blackjack.services.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Get ranking",
            description = "Get a list of all the players ordered by their performance in the blackjack games.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Get list of players", content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Player.class))
                    ))
            }
    )
    @GetMapping("/ranking")
    public Mono<ResponseEntity<List<Player>>> getRanking(){
        return playerService.getRanking().
                map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Change a player's name",
            description = "Change a player's name in the players database and throughout all the existing games.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Name is updated", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Player.class)
                    )),
                    @ApiResponse(responseCode = "404", description = "Player not found", content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "Player not found",
                                    value = "No player found with id: 674487950b80db2bc72ea574"
                            )))
            }
    )
    @PutMapping("/player/{playerId}")
    public Mono<ResponseEntity<Player>> updatePlayerName(
            @Parameter(description = "Id of the player to update", example = "673f22257b21b20c20d0d290")
            @PathVariable String playerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New name of the player",
                    required = true,
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "New player name",
                                    value = "John Doe"
                            )))
            @RequestBody String playerName){
        return playerService.updatePlayerName(playerId, playerName)
                .flatMap(gameService::updatePlayerNameInGames)
                .map(ResponseEntity::ok);
    }

}
