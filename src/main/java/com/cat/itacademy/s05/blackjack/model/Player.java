package com.cat.itacademy.s05.blackjack.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Getter
@Setter
@Schema(name = "Player", description = "Data object representing a player")
@Table("player")
public class Player {

    @Schema(description = "Identifier of the player", example = "1234")
    @Id
    private Long id;

    @Schema(description = "Name of the player", example = "John Doe")
    private String name;

    @Schema(description = "Money of the player", example = "100")
    private int money;

    @Schema(description = "Games player by the player", example = "6")
    private int gamesPlayed;

    public Player(String name) {
        this.name = name;
        this.money = 100;
        this.gamesPlayed = 0;
    }

    public Player() {
    }

}
