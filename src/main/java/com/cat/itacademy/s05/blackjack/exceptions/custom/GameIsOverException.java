package com.cat.itacademy.s05.blackjack.exceptions.custom;

public class GameIsOverException extends RuntimeException{
    public GameIsOverException(String message) {
        super(message);
    }
}
