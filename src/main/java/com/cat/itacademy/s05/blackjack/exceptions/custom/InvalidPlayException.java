package com.cat.itacademy.s05.blackjack.exceptions.custom;

public class InvalidPlayException extends RuntimeException{
    public InvalidPlayException(String message) {
        super(message);
    }
}
