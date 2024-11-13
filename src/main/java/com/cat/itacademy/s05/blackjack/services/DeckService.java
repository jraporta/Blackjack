package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.enums.Rank;
import com.cat.itacademy.s05.blackjack.enums.Suit;
import com.cat.itacademy.s05.blackjack.model.Card;
import com.cat.itacademy.s05.blackjack.model.Deck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DeckService {

    @Value("${numberOfDecks}")
    private int numberOfDecks;

    public Deck generateDeck(Deck deck){
        for (int i = 0; i < numberOfDecks; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    deck.getDeck().add(new Card(suit, rank));
                }
            }
        }
        return deck;
    }

    private Card getCard(Deck deck){
        return deck.getDeck().remove(ThreadLocalRandom.current().nextInt(deck.getDeck().size()));
    }

    public void dealCard(Deck deck, List<Card> cards) {
        cards.add(getCard(deck));
    }

}
