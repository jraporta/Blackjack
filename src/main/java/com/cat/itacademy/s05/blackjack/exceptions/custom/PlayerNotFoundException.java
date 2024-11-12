package com.cat.itacademy.s05.blackjack.exceptions.custom;

public class PlayerNotFoundException extends RuntimeException{
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
