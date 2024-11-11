package com.cat.itacademy.s05.blackjack.exceptions;

public class NotActivePlayerException extends RuntimeException{
    public NotActivePlayerException(String message) {
        super(message);
    }
}
