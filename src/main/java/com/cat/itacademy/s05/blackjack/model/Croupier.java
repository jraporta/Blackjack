package com.cat.itacademy.s05.blackjack.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(name = "Croupier", description = "Data object representing the croupier of the game")
public class Croupier {

    @ArraySchema(schema = @Schema(description = "List of cards in the Croupier's hand", implementation = Card.class))
    private List<Card> cards;

    {
        cards = new ArrayList<>();
    }

    public void getCard(Card card){
        this.cards.add(card);
    }

}
