package com.cat.itacademy.s05.blackjack.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class Deck {

    private List<Card> deck;

    {
        deck = new LinkedList<>();
    }

}
