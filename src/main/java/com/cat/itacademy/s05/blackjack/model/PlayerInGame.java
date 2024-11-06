package com.cat.itacademy.s05.blackjack.model;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;


public class PlayerInGame {

    private Long id;

    private String name;

    private int money;

    private int gamesPlayed;

}
