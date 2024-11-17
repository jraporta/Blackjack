package com.cat.itacademy.s05.blackjack.enums;

import lombok.Getter;

@Getter
public enum BlackjackPayout {

    THREE_TO_TWO((double) 3 /2, "3:2"),
    SIX_TO_FIVE((double) 6 /5, "6:5"),
    SEVEN_TO_FIVE((double) 7 /5, "7:5"),
    ONE_TO_ONE(1, "1:1"),
    TWO_TO_ONE(2, "2:1");

    private final double payout;
    private final String payScheme;

    BlackjackPayout(double payout, String payScheme) {
        this.payout = payout;
        this.payScheme = payScheme;
    }
}
