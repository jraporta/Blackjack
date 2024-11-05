package com.cat.itacademy.s05.blackjack.model;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Game {

    @Id
    private String id;

    private List<Player> players;

}
