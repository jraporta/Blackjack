package com.cat.itacademy.s05.blackjack.dto;

import com.cat.itacademy.s05.blackjack.model.Croupier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CroupierDTO {

    List<CardDTO> cards;

    public CroupierDTO(Croupier croupier) {
        this.cards = new ArrayList<>();
        if (croupier.getCards().size() == 2) {
            this.cards.add(new VisibleCardDTO(croupier.getCards().getFirst().suit(), croupier.getCards().getFirst().rank()));
            this.cards.add(new HiddenCardDTO());
        }
    }
}
