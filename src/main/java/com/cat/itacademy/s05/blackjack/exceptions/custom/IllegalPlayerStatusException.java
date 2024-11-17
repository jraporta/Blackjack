package com.cat.itacademy.s05.blackjack.exceptions.custom;

public class IllegalPlayerStatusException extends RuntimeException{
    public IllegalPlayerStatusException(String message) {
        super(message);
    }
}
