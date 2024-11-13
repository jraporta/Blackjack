package com.cat.itacademy.s05.blackjack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "games")
public class Game {

    private @Id String id;

    private boolean concluded;

    private Croupier croupier;

    private List<PlayerInGame> players;

    private int activePlayerId = 0;

    @JsonIgnore
    private Deck deck;

    {
        concluded = false;
        croupier = new Croupier();
        players = new ArrayList<>();

    }

    public PlayerInGame getActivePlayer() {
        return this.getPlayers().get(this.getActivePlayerId());
    }
}
