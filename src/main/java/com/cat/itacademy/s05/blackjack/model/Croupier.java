package com.cat.itacademy.s05.blackjack.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Croupier {

    private List<Card> cards;

    {
        cards = new ArrayList<>();
    }

    public void getCard(Card card){
        this.cards.add(card);
    }

}
