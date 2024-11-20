package com.cat.itacademy.s05.blackjack.dto;

import com.cat.itacademy.s05.blackjack.enums.Rank;
import com.cat.itacademy.s05.blackjack.enums.Suit;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Card", description = "Data object representing a card", hidden = true)
public record VisibleCardDTO (
        @Schema(description = "Suit of the card", examples = {"CLUBS", "DIAMONDS", "SPADES", "HEARTS"})
        Suit suit,

        @Schema(description = "Rank of the card", examples = {"TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT",
                "NINE", "TEN", "JACK", "QUEEN", "KING", "ACE"})
        Rank rank

) implements CardDTO{}
