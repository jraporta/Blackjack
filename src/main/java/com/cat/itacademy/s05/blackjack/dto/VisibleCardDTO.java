package com.cat.itacademy.s05.blackjack.dto;

import com.cat.itacademy.s05.blackjack.enums.Rank;
import com.cat.itacademy.s05.blackjack.enums.Suit;

public record VisibleCardDTO (Suit suit, Rank rank) implements CardDTO{
}
