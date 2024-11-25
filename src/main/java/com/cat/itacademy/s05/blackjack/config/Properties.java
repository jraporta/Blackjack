package com.cat.itacademy.s05.blackjack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties {

    @Value("${playingPositions:1}")
    private int playingPositions;

    @Value("${simultaneousBetsAllowed:3}")
    private int simultaneousBetsAllowed;

    public int getPlayingPositions() {
        if (playingPositions >= 1 && playingPositions <= 9) return playingPositions;
        return 1;
    }

    public int getSimultaneousBetsAllowed() {
        if (simultaneousBetsAllowed >= 1 && simultaneousBetsAllowed <= 3) return simultaneousBetsAllowed;
        return 3;
    }

}
