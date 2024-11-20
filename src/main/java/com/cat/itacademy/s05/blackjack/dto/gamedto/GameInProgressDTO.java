package com.cat.itacademy.s05.blackjack.dto.gamedto;

import com.cat.itacademy.s05.blackjack.dto.CroupierDTO;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(name = "InProgressGame", description = "Data object representing a not concluded game")
public class GameInProgressDTO implements GameDTO {

    @Schema(description = "Id of the game", example = "673b4e77d181ca65a6f436b9")
    private String gameId;

    @Schema(description = "Details of the croupier associated with the game")
    private CroupierDTO croupier;

    @ArraySchema(schema = @Schema(description = "List of players in the game", implementation = PlayerInGame.class))
    private List<PlayerInGame> players;

    @Schema(description = "Index representing the index of the active player in the player's list")
    private int activePlayerIndex;

    public GameInProgressDTO(Game game) {
        this.gameId = game.getId();
        this.croupier = new CroupierDTO(game.getCroupier());
        this.players = game.getPlayers();
        this.activePlayerIndex = game.getActivePlayerIndex();
    }

}
