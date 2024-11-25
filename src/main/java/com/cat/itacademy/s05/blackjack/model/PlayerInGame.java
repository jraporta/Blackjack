package com.cat.itacademy.s05.blackjack.model;

import com.cat.itacademy.s05.blackjack.enums.PlayerStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Schema(name = "PlayerInGame", description = "Data object representing a player in a game")
public class PlayerInGame {

    @Schema(description = "Identifier of the player", example = "673f22257b21b20c20d0d290")
    private String id;

    @Schema(description = "Name of the player", example = "John Doe")
    private String name;

    @Schema(description = "Amount bet by the player", example = "20")
    private int bet;

    @ArraySchema(schema = @Schema(description = "List of cards in the player's hand", implementation = Card.class))
    private List<Card> cards;

    @Schema(description = "Status of the player", examples = {"PENDING_BET", "WAITING_FOR_DEAL", "PLAYING", "STAND",
            "SURRENDER", "BLACKJACK", "BUST", "TIE", "WIN", "LOOSE"})
    private PlayerStatus status;

    public PlayerInGame(String id, String name) {
        this.id = id;
        this.name = name;
        this.bet = 0;
        this.cards = new ArrayList<>();
        status = PlayerStatus.PENDING_BET;
    }

    public void getCard(Card card){
        this.cards.add(card);
    }

    @Override
    public boolean equals(Object o){
        return (o instanceof PlayerInGame &&
                this.getId().equals(((PlayerInGame) o).getId()) &&
                this.getName().equals(((PlayerInGame) o).getName()));
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.id, this.name);
    }

}
