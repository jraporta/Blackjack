package com.cat.itacademy.s05.blackjack.model;

import com.cat.itacademy.s05.blackjack.enums.PlayerStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerInGame {

    private Long id;

    private String name;

    private int bet;

    private List<Card> cards;

    private PlayerStatus status;

    //TODO delete passed, use status instead
    boolean passed;

    public PlayerInGame(Long id, String name) {
        this.id = id;
        this.name = name;
        this.bet = 0;
        this.cards = new ArrayList<>();
        status = PlayerStatus.PENDING_BET;
        passed = false;
    }

    public void addCard(Card card){
        this.cards.add(card);
    }

}
