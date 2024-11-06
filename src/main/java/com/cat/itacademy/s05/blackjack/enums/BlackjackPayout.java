package com.cat.itacademy.s05.blackjack.enums;

import lombok.Getter;

@Getter
public enum BlackjackPayout {

    threeToTwo((double) 3 /2, "3:2"),
    sixToFive((double) 6 /5, "6:5"),
    sevenToFive((double) 7 /5, "7:5"),
    oneToOne(1, "1:1"),
    twoToOne(2, "2:1");

    private final double payout;
    private final String payScheme;

    BlackjackPayout(double payout, String payScheme) {
        this.payout = payout;
        this.payScheme = payScheme;
    }
}
