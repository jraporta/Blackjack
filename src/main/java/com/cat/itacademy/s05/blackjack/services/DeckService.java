package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.enums.Rank;
import com.cat.itacademy.s05.blackjack.enums.Suit;
import com.cat.itacademy.s05.blackjack.model.Card;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DeckService {

    @Value("${numberOfDecks}")
    private int numberOfDecks;

    private List<Card> deck = new ArrayList<>();

    public void generateDeck(){
        for (int i = 0; i < numberOfDecks; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    this.deck.add(new Card(suit, rank));
                }
            }
        }
    }

    public Card getCard(){
        return deck.remove(ThreadLocalRandom.current().nextInt(deck.size()));
    }

}
