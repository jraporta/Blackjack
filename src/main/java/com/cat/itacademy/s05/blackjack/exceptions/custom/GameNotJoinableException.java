package com.cat.itacademy.s05.blackjack.exceptions.custom;

public class GameNotJoinableException extends RuntimeException{
    public GameNotJoinableException(String message) {
        super(message);
    }
}
