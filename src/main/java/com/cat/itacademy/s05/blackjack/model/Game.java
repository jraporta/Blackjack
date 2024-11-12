package com.cat.itacademy.s05.blackjack.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "games")
public class Game {

    private @Id String id;

    private boolean concluded;
    //TODO delete?
    private String description;

    private Croupier croupier;

    private List<PlayerInGame> players;

    private int activePlayer = 0;

    //TODO jackson ignore on delivery
    private Deck deck;

    {
        concluded = false;
        croupier = new Croupier();
    }

    //TODO move to player service
    //TODO: Skip players that have passed
    public Game changeActivePlayer() {
        activePlayer++;
        if (activePlayer >= players.size()) activePlayer = 0;
        return this;
    }


}
