package com.cat.itacademy.s05.blackjack.exceptions.custom;

public class GameNotFoundException extends RuntimeException{
    public GameNotFoundException(String message) {
        super(message);
    }
}
