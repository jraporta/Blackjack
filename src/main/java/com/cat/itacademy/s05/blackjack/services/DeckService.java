package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.enums.Rank;
import com.cat.itacademy.s05.blackjack.enums.Suit;
import com.cat.itacademy.s05.blackjack.model.Card;
import com.cat.itacademy.s05.blackjack.model.Deck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class DeckService {

    @Value("${numberOfDecks}")
    private int numberOfDecks;

    private Deck deck;

    public void generateDeck(){
        for (int i = 0; i < numberOfDecks; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    this.deck.getDeck().add(new Card(suit, rank));
                }
            }
        }
    }

    public Card getCard(){
        return deck.getDeck().remove(ThreadLocalRandom.current().nextInt(deck.getDeck().size()));
    }

}
