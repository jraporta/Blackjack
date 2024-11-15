package com.cat.itacademy.s05.blackjack.dto.gamedto;

import com.cat.itacademy.s05.blackjack.dto.CroupierDTO;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import lombok.Getter;

import java.util.List;

@Getter
public class GameInProgressMultiplayerDTO implements GameDTO {

    private String gameId;

    private CroupierDTO croupier;

    private List<PlayerInGame> players;

    private int activePlayerIndex;

    public GameInProgressMultiplayerDTO(Game game) {
        this.gameId = game.getId();
        this.croupier = new CroupierDTO(game.getCroupier());
        this.players = game.getPlayers();
        this.activePlayerIndex = game.getActivePlayerIndex();
    }

}
