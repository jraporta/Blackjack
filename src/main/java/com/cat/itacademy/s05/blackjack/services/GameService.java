package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayResponseDTO;
import com.cat.itacademy.s05.blackjack.dto.PlayerDTO;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    //TODO
    public String createGame(String player) {
    return "{gameId}";
    }

    //TODO
    public Game getGame(String gameId) {
        return new Game();
    }

    //TODO
    public PlayResponseDTO executePlay(String gameId, PlayDTO play) {
        return new PlayResponseDTO();
    }

    //TODO
    public List<PlayerDTO> getRanking() {
        return new ArrayList<PlayerDTO>();
    }

    //TODO
    public Player updatePlayerName(String playerId, String playerName) {
        return new Player();
    }
}
