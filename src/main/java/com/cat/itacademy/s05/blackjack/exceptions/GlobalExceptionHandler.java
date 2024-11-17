package com.cat.itacademy.s05.blackjack.exceptions;

import com.cat.itacademy.s05.blackjack.exceptions.custom.*;
import com.cat.itacademy.s05.blackjack.exceptions.custom.IllegalPlayerStatusException;
import com.cat.itacademy.s05.blackjack.exceptions.custom.IllegalGameStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public Mono<ResponseEntity<String>> handleGameNotFound(GameNotFoundException ex){
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()));
    }

    @ExceptionHandler(NotActivePlayerException.class)
    public Mono<ResponseEntity<String>> handleNotActivePlayer(NotActivePlayerException ex){
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()));
    }

    @ExceptionHandler(NoBetPlacedException.class)
    public Mono<ResponseEntity<String>> handleNoBetPlaced(NoBetPlacedException ex){
        return Mono.just(ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getMessage()));
    }

    @ExceptionHandler(InvalidPlayException.class)
    public Mono<ResponseEntity<String>> handleInvalidPlay(InvalidPlayException ex){
        return Mono.just(ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getMessage()));
    }

    @ExceptionHandler(GameIsOverException.class)
    public Mono<ResponseEntity<String>> handleGameIsOver(GameIsOverException ex){
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()));
    }

    @ExceptionHandler({IllegalPlayerStatusException.class, IllegalGameStateException.class, PlayerNotFoundException.class})
    public Mono<ResponseEntity<String>> handleUnexpectedExceptions(RuntimeException ex){
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
    }

}
