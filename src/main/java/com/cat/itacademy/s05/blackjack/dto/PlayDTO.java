package com.cat.itacademy.s05.blackjack.dto;

import com.cat.itacademy.s05.blackjack.enums.Play;

public record PlayDTO(Long playerId, Play play, int bet) {
}
