package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.model.Card;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.utils.BlackjackHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CroupierService {

    @Value("${soft17ForcedDraw}")
    private boolean soft17ForcedDraw;

    private final DeckService deckService;
    private final BlackjackHelper helper;

    public CroupierService(DeckService deckService, BlackjackHelper helper) {
        this.deckService = deckService;
        this.helper = helper;
    }

    public Mono<Game> resolveCroupierHand(Game game) {
        List<Card> croupiersHand = game.getCroupier().getCards();
        while (helper.getHandValue(croupiersHand) < 17 || (soft17ForcedDraw && helper.isSoft17(croupiersHand))) {
            deckService.dealCard(game.getDeck(), croupiersHand);
        }
        return Mono.just(game);
    }

}
