package com.cat.itacademy.s05.blackjack.dto.gamedto;

import com.cat.itacademy.s05.blackjack.model.Croupier;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import lombok.Getter;

import java.util.List;

@Getter
public class GameCompletedDTO implements GameDTO {

    private String id;

    private Croupier croupier;

    private List<PlayerInGame> players;

    public GameCompletedDTO(Game game) {
        this.id = game.getId();
        this.croupier = game.getCroupier();
        this.players = game.getPlayers();
    }
}
