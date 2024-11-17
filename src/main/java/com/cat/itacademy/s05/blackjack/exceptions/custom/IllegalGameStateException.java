package com.cat.itacademy.s05.blackjack.exceptions.custom;

public class IllegalGameStateException extends RuntimeException{
    public IllegalGameStateException(String message) {
        super(message);
    }
}
