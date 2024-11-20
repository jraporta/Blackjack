package com.cat.itacademy.s05.blackjack.controllers;

import com.cat.itacademy.s05.blackjack.dto.gamedto.GameCompletedDTO;
import com.cat.itacademy.s05.blackjack.dto.gamedto.GameDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.dto.gamedto.GameInProgressDTO;
import com.cat.itacademy.s05.blackjack.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Game Management", description = "Endpoints for managing games")
@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Operation(
            summary = "Create a game",
            description = "Creates a new Blackjack game for the user specified in the request body. If a user with " +
                    "the given name exists, it is retrieved from the database; otherwise, a new user is created.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Game created", content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "example response",
                                    value = "Created game with id: 673b4e77d181ca65a6f436b9"
                            ))),
                    @ApiResponse(responseCode = "400", description = "No name provided")
            }
    )
    @PostMapping("/game/new")
    public Mono<ResponseEntity<String>> createGame(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Name of the player to include in the game",
                    required = true,
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "Player name",
                                    value = "John Doe"
                            )))
            @RequestBody String playerName){
        return gameService.createGame(playerName)
                .map(gameId -> ResponseEntity.status(HttpStatus.CREATED).body("Created game with id: " + gameId));
    }

    @Operation(
            summary = "Get details of a game",
            description = "Get the details of a blackjack game.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Game found", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(oneOf = {GameCompletedDTO.class, GameInProgressDTO.class})
                    )),
                    @ApiResponse(responseCode = "404", description = "Game not found")
            }
    )
    @GetMapping("/game/{id}")
    public Mono<ResponseEntity<GameDTO>> getGame(
            @Parameter(description = "Id of the game to search for", example = "673b4d48e52179685109a141")
            @PathVariable String id){
        return gameService.getGameDTO(id)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Make a play",
            description = "Make a play in an existing blackjack game.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(oneOf = {GameCompletedDTO.class, GameInProgressDTO.class})
                    )),
                    @ApiResponse(responseCode = "404", description = "Invalid play")
            }
    )
    @PostMapping("/game/{id}/play")
    public Mono<ResponseEntity<GameDTO>> executePlay(
            @Parameter(description = "Id of the game", example = "673b4d48e52179685109a141")
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the play",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PlayDTO.class)
                    ))
            @RequestBody PlayDTO play){
        return gameService.executePlay(id, play)
                .then(gameService.getGameDTO(id))
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Delete a game",
            description = "Delete a blackjack game.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Game deleted"),
                    @ApiResponse(responseCode = "404", description = "Game not found")
            }
    )
    @DeleteMapping("/game/{id}/delete")
    public Mono<ResponseEntity<Void>> deleteGame(
            @Parameter(description = "Id of the game to delete", example = "673b4d48e52179685109a141")
            @PathVariable String id){
        return gameService.deleteGame(id)
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

}
