package com.cat.itacademy.s05.blackjack.utils;


import com.cat.itacademy.s05.blackjack.enums.Rank;
import com.cat.itacademy.s05.blackjack.enums.Suit;
import com.cat.itacademy.s05.blackjack.model.Card;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@SpringBootTest
@TestPropertySource(properties = "blackjackPayout=THREE_TO_TWO")
public class BlackjackHelperTest {

    @Autowired
    BlackjackHelper blackjackHelper;

    List<Card> blackjackHand, withoutAcesHand, withAceUnder21Hand, withAceOver21Hand, nonBlackjackValue21Hand,
            soft17Hand, hard17WithAceHand, hard17Hand;

    public BlackjackHelperTest() {
        this.blackjackHand = List.of(new Card(Suit.CLUBS, Rank.ACE), new Card(Suit.CLUBS, Rank.KING));
        this.withoutAcesHand = List.of(new Card(Suit.HEARTS, Rank.TWO), new Card(Suit.CLUBS, Rank.TEN)
                , new Card(Suit.DIAMONDS, Rank.EIGHT));
        this.withAceUnder21Hand = List.of(new Card(Suit.HEARTS, Rank.FOUR), new Card(Suit.CLUBS, Rank.FIVE)
                , new Card(Suit.DIAMONDS, Rank.ACE));
        this.withAceOver21Hand = List.of(new Card(Suit.HEARTS, Rank.NINE), new Card(Suit.CLUBS, Rank.EIGHT)
                , new Card(Suit.DIAMONDS, Rank.ACE), new Card(Suit.CLUBS, Rank.KING));
        this.nonBlackjackValue21Hand = List.of(new Card(Suit.HEARTS, Rank.NINE), new Card(Suit.CLUBS, Rank.EIGHT)
                , new Card(Suit.DIAMONDS, Rank.FOUR));
        this.soft17Hand = List.of(new Card(Suit.HEARTS, Rank.ACE), new Card(Suit.HEARTS, Rank.THREE)
                , new Card(Suit.CLUBS, Rank.THREE));
        this.hard17WithAceHand = List.of(new Card(Suit.HEARTS, Rank.ACE), new Card(Suit.HEARTS, Rank.TEN)
                , new Card(Suit.CLUBS, Rank.SIX));
        this.hard17Hand = List.of(new Card(Suit.HEARTS, Rank.TEN), new Card(Suit.HEARTS, Rank.SEVEN));
    }

    @Test
    void getHandValue_Blackjack_Is21(){
        assertEquals(21, blackjackHelper.getHandValue(blackjackHand));
    }

    @Test
    void getHandValue_WithoutAces_IsCorrect(){
        assertEquals(21, blackjackHelper.getHandValue(nonBlackjackValue21Hand));
    }

    @Test
    void getHandValue_AceUnder22_Counts11(){
        assertEquals(20, blackjackHelper.getHandValue(withAceUnder21Hand));
    }

    @Test
    void getHandValue_AceAbove21_Counts1(){
        assertEquals(28, blackjackHelper.getHandValue(withAceOver21Hand));
    }

    @Test
    void isBlackjack_Blackjack_ReturnsTrue(){
        assertTrue(blackjackHelper.isBlackjack(blackjackHand));
    }

    @Test
    void isBlackjack_NotBlackjack_ReturnsFalse(){
        assertFalse(blackjackHelper.isBlackjack(withAceUnder21Hand));
    }

    @Test
    void isBlackjack_NotBlackjackWithValue21_ReturnsFalse(){
        assertFalse(blackjackHelper.isBlackjack(nonBlackjackValue21Hand));
    }

    @Test
    void isBust_isBust_ReturnsTrue(){
        assertTrue(blackjackHelper.isBust(withAceOver21Hand));
    }

    @Test
    void isBust_isNotBust_ReturnsFalse(){
        assertFalse(blackjackHelper.isBust(withAceUnder21Hand));
    }

    @Test
    void isBust_isBlackjack_ReturnsFalse(){
        assertFalse(blackjackHelper.isBust(blackjackHand));
    }

    @Test
    void isSoft17_17WithAcesValued11_ReturnsTrue(){
        assertTrue(blackjackHelper.isSoft17(soft17Hand));
    }

    @Test
    void isSoft17_17WithAcesValued1_ReturnsFalse(){
        assertFalse(blackjackHelper.isSoft17(hard17WithAceHand));
    }

    @Test
    void isSoft17_17WithoutAces_ReturnsFalse(){
        assertFalse(blackjackHelper.isSoft17(hard17Hand));
    }

    @Test
    void getSoftSurrenderPayout_ValidBet_ReturnsHalfBet(){
        assertEquals(10, blackjackHelper.getSurrenderPayout(20));
    }

    @Test
    void getBlackjackPayout_bet_ReturnsBlackjackPayout(){
        assertEquals(25, blackjackHelper.getBlackjackPayout(10));
    }

    @Test
    void getTiePayout_bet_ReturnsBetValue(){
        assertEquals(20, blackjackHelper.getTiePayout(20));
    }

    @Test
    void getWinPayout_bet_ReturnsBetTimes2(){
        assertEquals(40, blackjackHelper.getWinPayout(20));
    }
}
