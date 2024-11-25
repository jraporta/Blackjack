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
            description = "Creates a new Blackjack game with the player specified in the request body.\n" +
                    "If a player with that name exists, it is retrieved from the database; otherwise, a new player is created.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Game created", content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "example response",
                                    value = "Created game with id: 673b4e77d181ca65a6f436b9"
                            ))),
                    @ApiResponse(responseCode = "400", description = "No name provided", content = @Content())
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
            summary = "Add player to an existing game",
            description = "Given the id of an existing but not started Blackjack game, the player specified in the request body is added to the game.\n" +
                    "If there is a player with that name, it is retrieved from the database; otherwise, a new player is created.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Player added", content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "example response",
                                    value = "Player joined game with id: 673b4e77d181ca65a6f436b9"
                            ))),
                    @ApiResponse(responseCode = "400", description = "No name provided / The game is already in progress, no new players can join", content = @Content(
                            mediaType = "text/plain",
                            examples = {
                                    @ExampleObject(
                                            name = "Game in progress",
                                            value = "The game is in progress; no new players can join"
                                    ),
                                    @ExampleObject(
                                            name = "Bet limit for a player reached",
                                            value = "The player has reached the number of bets limit for a single game."
                                    ),
                                    @ExampleObject(
                                            name = "Player limit reached",
                                            value = "All the playing positions are occupied. No more players accepted."
                                    )
                            })),
                    @ApiResponse(responseCode = "404", description = "Game not found", content = @Content(
                            mediaType = "text/plain",
                            examples =
                                @ExampleObject(
                                        name = "Game not found",
                                        value = "No game with id: 673751d2af0fa27b22eb19a3"
                                )))
            }
    )
    @PostMapping("/game/{id}/join")
    public Mono<ResponseEntity<String>> joinGame(
            @Parameter(description = "Id of the game to search for", example = "673b4d48e52179685109a141")
            @PathVariable String id,
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
        return gameService.joinGame(id, playerName)
                .map(gameId -> ResponseEntity.ok("Player joined game with id: " + gameId));
    }

    @Operation(
            summary = "Get details of a game",
            description = "Get the details of a blackjack game.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Game found", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(oneOf = {GameCompletedDTO.class, GameInProgressDTO.class})
                    )),
                    @ApiResponse(responseCode = "404", description = "Game not found", content = @Content(
                            mediaType = "text/plain",
                            examples =
                            @ExampleObject(
                                    name = "Game not found",
                                    value = "No game with id: 673751d2af0fa27b22eb19a3"
                            )))
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
                    @ApiResponse(responseCode = "404", description = "Game not found", content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "Game not found",
                                    value = "No game with id: 673751d2af0fa27b22eb19a3"
                            )
                    )),
                    @ApiResponse(responseCode = "404", description = "Invalid play", content = @Content(
                            mediaType = "text/plain",
                            examples = {
                                    @ExampleObject(
                                            name = "Not active player",
                                            value = "It's the turn of the player with id: 673f22257b21b20c20d0d290"
                                    ),
                                    @ExampleObject(
                                            name = "Not bet",
                                            value = "Player has no bet. First play must be 'INITIAL_BET'."
                                    ),
                                    @ExampleObject(
                                            name = "Invalid play",
                                            value = "Split is only allowed if the two cards have the same rank."
                                    ),
                                    @ExampleObject(
                                            name = "Game is over",
                                            value = "Game is over, no more plays accepted."
                                    ),
                                    @ExampleObject(
                                            name = "Invalid bet",
                                            value = "Initial bet play must have a valid bet.",
                                            description = "Bet is 0 or less."
                                    )
                            }
                    ))
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
                    @ApiResponse(responseCode = "204", description = "Game deleted", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Game not found", content = @Content(
                            mediaType = "text/plain",
                            examples =
                            @ExampleObject(
                                    name = "Game not found",
                                    value = "No game with id: 673751d2af0fa27b22eb19a3"
                            )))
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
