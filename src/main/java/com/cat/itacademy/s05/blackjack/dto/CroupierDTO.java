package com.cat.itacademy.s05.blackjack.dto;

import com.cat.itacademy.s05.blackjack.dto.gamedto.GameCompletedDTO;
import com.cat.itacademy.s05.blackjack.dto.gamedto.GameInProgressDTO;
import com.cat.itacademy.s05.blackjack.model.Card;
import com.cat.itacademy.s05.blackjack.model.Croupier;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Schema(name = "Croupier", description = "Data object representing the croupier of the game")
public class CroupierDTO {


    @ArraySchema(schema = @Schema(oneOf = {VisibleCardDTO.class, HiddenCardDTO.class}))
    List<CardDTO> cards;

    public CroupierDTO(Croupier croupier) {
        this.cards = new ArrayList<>();
        if (croupier.getCards().size() == 2) {
            this.cards.add(new VisibleCardDTO(croupier.getCards().getFirst().suit(), croupier.getCards().getFirst().rank()));
            this.cards.add(new HiddenCardDTO());
        }
    }
}
