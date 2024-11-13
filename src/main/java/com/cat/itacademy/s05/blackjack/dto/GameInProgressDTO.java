package com.cat.itacademy.s05.blackjack.dto;

import com.cat.itacademy.s05.blackjack.model.Croupier;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import lombok.Getter;

@Getter
public class GameInProgressDTO implements GameDTO{

    private String gameId;

    private Croupier croupier;

    private PlayerInGame player;

    public GameInProgressDTO(Game game) {
        this.gameId = game.getId();
        this.croupier = game.getCroupier();
        if (this.croupier.getCards().size() == 2) {
            this.croupier.getCards().removeLast();
        }
        this.player = game.getPlayers().getFirst();
    }
}
