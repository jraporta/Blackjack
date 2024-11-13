package com.cat.itacademy.s05.blackjack.utils;

import com.cat.itacademy.s05.blackjack.enums.Rank;
import com.cat.itacademy.s05.blackjack.model.Card;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BlackjackHelper {

    public int getHandValue(List<Card> cards) {
        AtomicInteger handValue = new AtomicInteger();
        AtomicInteger numberOfAces = new AtomicInteger();
        cards.forEach(card -> {
            handValue.addAndGet(card.rank().getValue());
            if (card.rank() == Rank.ACE) numberOfAces.getAndIncrement();
        });
        while (handValue.get() > 21 && numberOfAces.get() >0) {
            handValue.getAndAdd(-10);
            numberOfAces.decrementAndGet();
        }
        return handValue.get();
    }

    public boolean isBlackjack(List<Card> cards) {
        return cards.size() == 2 && getHandValue(cards) == 21;
    }

    public boolean isBust(List<Card> cards) {
        return getHandValue(cards) > 21;
    }

    public boolean isSoft17(List<Card> cards) {
        return getHandValue(cards) == 17 && cards.stream().anyMatch(card -> card.rank() == Rank.ACE);
    }
}
