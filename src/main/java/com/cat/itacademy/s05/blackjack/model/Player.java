package com.cat.itacademy.s05.blackjack.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Getter
@Setter
@Table("player")
public class Player {

    public Player(String name) {
        this.name = name;
        this.money = 100;
        this.gamesPlayed = 0;
    }

    public Player() {
    }

    @Id
    private Long id;

    private String name;

    private int money;

    private int gamesPlayed;

}
