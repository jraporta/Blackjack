package com.cat.itacademy.s05.blackjack.model;

import org.springframework.data.annotation.Id;

import java.util.List;

public record Game(@Id String id, List<PlayerInGame> players) {

}
