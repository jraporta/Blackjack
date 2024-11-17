package com.cat.itacademy.s05.blackjack.utils;

import com.cat.itacademy.s05.blackjack.enums.BlackjackPayout;
import com.cat.itacademy.s05.blackjack.enums.Rank;
import com.cat.itacademy.s05.blackjack.model.Card;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BlackjackHelper {

    @Value("${blackjackPayout:THREE_TO_TWO}")
    private String blackjackPayout;

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
        return cards.stream().mapToInt(card -> card.rank().getValue()).sum() == 17 &&
                cards.stream().anyMatch(card -> card.rank() == Rank.ACE);
    }

    public int getSurrenderPayout(int bet) {
        return bet / 2;
    }

    public int getBlackjackPayout(int bet) {
        return (int) (bet * (1 + BlackjackPayout.valueOf(blackjackPayout).getPayout()));
    }

    public int getTiePayout(int bet) {
        return bet;
    }

    public int getWinPayout(int bet) {
        return bet * 2;
    }
}
