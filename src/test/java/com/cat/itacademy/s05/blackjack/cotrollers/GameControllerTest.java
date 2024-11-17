package com.cat.itacademy.s05.blackjack.cotrollers;

import com.cat.itacademy.s05.blackjack.controllers.GameController;
import com.cat.itacademy.s05.blackjack.dto.gamedto.GameDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.services.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameControllerTest {

    @InjectMocks
    GameController gameController;

    @Mock private GameService mockGameService;
    @Mock private GameDTO mockGameDTO;
    @Mock private PlayDTO mockPlayDTO;

    @Test
    void createGame_CreatesNewGame(){
        when(mockGameService.createGame("Goku")).thenReturn(Mono.just("abc123456"));

        ResponseEntity<String> expectedResponse = ResponseEntity.status(HttpStatus.CREATED)
                .body("Created game with id: abc123456");

        StepVerifier.create(gameController.createGame("Goku"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getGame_ReturnsGameDTO(){
        when(mockGameService.getGameDTO("abc123456")).thenReturn(Mono.just(mockGameDTO));

        ResponseEntity<GameDTO> expectedResponse = ResponseEntity.ok(mockGameDTO);

        StepVerifier.create(gameController.getGame("abc123456"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void executePlay_ReturnsGameDTO(){
        when(mockGameService.executePlay("abc123456", mockPlayDTO)).thenReturn((Mono.empty()));
        when(mockGameService.getGameDTO("abc123456")).thenReturn(Mono.just(mockGameDTO));

        ResponseEntity<GameDTO> expectedResponse = ResponseEntity.ok(mockGameDTO);

        StepVerifier.create(gameController.executePlay("abc123456", mockPlayDTO))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void deleteGame_Returns204Response(){
        when(mockGameService.deleteGame("abc123456")).thenReturn((Mono.empty()));

        ResponseEntity<Void> expectedResponse = ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        StepVerifier.create(gameController.deleteGame("abc123456"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

}
