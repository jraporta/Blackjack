package com.cat.itacademy.s05.blackjack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "HiddenCard", description = "Data object representing a card", hidden = true)
public class HiddenCardDTO implements CardDTO{

    @Schema(description = "String showing the message \"Hidden card\"", example = "Hidden card")
    String msg = "Hidden card";

}
