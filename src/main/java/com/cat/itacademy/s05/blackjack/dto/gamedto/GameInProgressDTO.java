package com.cat.itacademy.s05.blackjack.dto.gamedto;

import com.cat.itacademy.s05.blackjack.dto.CroupierDTO;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import lombok.Getter;

@Getter
public class GameInProgressDTO implements GameDTO {

    private String gameId;

    private CroupierDTO croupier;

    private PlayerInGame player;

    public GameInProgressDTO(Game game) {
        this.gameId = game.getId();
        this.croupier = new CroupierDTO(game.getCroupier());
        this.player = game.getPlayers().getFirst();
    }
}
