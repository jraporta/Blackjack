package com.cat.itacademy.s05.blackjack.dto;

import com.cat.itacademy.s05.blackjack.model.Croupier;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import lombok.Getter;

import java.util.List;

@Getter
public class GameInProgressMultiplayerDTO implements GameDTO {

    private String gameId;

    private Croupier croupier;

    private List<PlayerInGame> players;

    private int activePlayerIndex;

    public GameInProgressMultiplayerDTO(Game game) {
        this.gameId = game.getId();
        this.croupier = game.getCroupier();
        if (this.croupier.getCards().size() == 2) {
            this.croupier.getCards().removeLast();
        }
        this.players = game.getPlayers();
        this.activePlayerIndex = game.getActivePlayerIndex();
    }

}
