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

    private Croupier croupier;

    private List<PlayerInGame> players;

    private int activePlayer = 0;

    private Deck deck;

    {
        croupier = new Croupier();
    }

    //TODO: Skip players that have passed
    public Game changeActivePlayer() {
        activePlayer++;
        if (activePlayer >= players.size()) activePlayer = 0;
        return this;
    }


}
