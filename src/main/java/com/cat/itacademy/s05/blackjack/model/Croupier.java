package com.cat.itacademy.s05.blackjack.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Croupier {

    private List<Card> cards;

    public void addCard(Card card){
        this.cards.add(card);
    }

}
