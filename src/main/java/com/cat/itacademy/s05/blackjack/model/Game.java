package com.cat.itacademy.s05.blackjack.model;

import com.cat.itacademy.s05.blackjack.services.DeckService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Game {

    @Autowired
    DeckService deckService;

    private @Id String id;

    private Croupier croupier;

    private List<PlayerInGame> players;

    private int activePlayer = 0;

    private List<Card> deck;

    {
        Croupier croupier = new Croupier();
        deck = new ArrayList<>();
        deckService.generateDeck();
    }

    //TODO: Skip players that have passed
    public Game changeActivePlayer() {
        activePlayer++;
        if (activePlayer >= players.size()) activePlayer = 0;
        return this;
    }


}
