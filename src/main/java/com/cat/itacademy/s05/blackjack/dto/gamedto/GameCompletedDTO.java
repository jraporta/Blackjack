package com.cat.itacademy.s05.blackjack.dto.gamedto;

import com.cat.itacademy.s05.blackjack.model.Card;
import com.cat.itacademy.s05.blackjack.model.Croupier;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(name = "CompletedGame", description = "Data object representing a finished game")
public class GameCompletedDTO implements GameDTO {

    @Schema(description = "Id of the game", example = "673b4e77d181ca65a6f436b9")
    private String id;

    @Schema(description = "Details of the croupier associated with the game", implementation = Croupier.class)
    private Croupier croupier;

    @ArraySchema(schema = @Schema(description = "List of players in the game", implementation = PlayerInGame.class))
    private List<PlayerInGame> players;

    public GameCompletedDTO(Game game) {
        this.id = game.getId();
        this.croupier = game.getCroupier();
        this.players = game.getPlayers();
    }
}
