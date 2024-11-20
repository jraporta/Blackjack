package com.cat.itacademy.s05.blackjack.dto;

import com.cat.itacademy.s05.blackjack.enums.Play;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Play", description = "Data object representing a play")
public record PlayDTO(
        @Schema(description = "Id of the player doing the play", example = "1234")
        Long playerId,

        @Schema(description = "Type of play", examples = {"INITIAL_BET", "HIT", "STAND", "DOUBLE", "SPLIT", "SURRENDER"})
        Play play,

        @Schema(description = "Quantity to bet. Ignored in all plays but INITIAL_BET", example = "30")
        int bet
) {}
