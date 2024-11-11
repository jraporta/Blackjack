package com.cat.itacademy.s05.blackjack.model;

import com.cat.itacademy.s05.blackjack.services.DeckService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "games")
public class Game {

    @Autowired
    DeckService deckService;

    private @Id String id;

    private Croupier croupier;

    private List<PlayerInGame> players;

    private int activePlayer = 0;

    private Deck deck;

    {
        Croupier croupier = new Croupier();
    }

    //TODO: Skip players that have passed
    public Game changeActivePlayer() {
        activePlayer++;
        if (activePlayer >= players.size()) activePlayer = 0;
        return this;
    }


}
