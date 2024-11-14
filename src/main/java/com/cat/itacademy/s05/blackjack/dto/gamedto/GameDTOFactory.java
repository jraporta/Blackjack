package com.cat.itacademy.s05.blackjack.dto.gamedto;

import com.cat.itacademy.s05.blackjack.model.Game;

public class GameDTOFactory {

    public GameDTO getGameDTO(Game game) {
        if (game.isConcluded()) return new GameCompletedDTO(game);
        if (game.getPlayers().size() == 1) return new GameInProgressDTO(game);
        return new GameInProgressMultiplayerDTO(game);
    }

}
