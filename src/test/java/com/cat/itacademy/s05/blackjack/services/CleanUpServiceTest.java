package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.enums.PlayerStatus;
import com.cat.itacademy.s05.blackjack.enums.Rank;
import com.cat.itacademy.s05.blackjack.enums.Suit;
import com.cat.itacademy.s05.blackjack.exceptions.custom.IllegalPlayerStatusException;
import com.cat.itacademy.s05.blackjack.model.*;
import com.cat.itacademy.s05.blackjack.utils.BlackjackHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class CleanUpServiceTest {

    @InjectMocks
    CleanUpService cleanUpService;

    @Mock private BlackjackHelper mockBlackjackHelper;
    @Mock private PlayerService mockPlayerService;

    private PlayerInGame playerInGame;

    private Player player;
    private final int INITIAL_GAMES_PLAYED = 3;
    private final int INITIAL_MONEY = 100;

    private final List<Card> blackjackHand = List.of(new Card(Suit.CLUBS, Rank.ACE), new Card(Suit.CLUBS, Rank.KING));
    private final List<Card> nonBlackjackValue21Hand = List.of(new Card(Suit.HEARTS, Rank.NINE), new Card(Suit.CLUBS, Rank.EIGHT)
            , new Card(Suit.DIAMONDS, Rank.FOUR));
    private final List<Card> bustHand = List.of(new Card(Suit.HEARTS, Rank.NINE), new Card(Suit.CLUBS, Rank.EIGHT)
                , new Card(Suit.DIAMONDS, Rank.ACE), new Card(Suit.CLUBS, Rank.KING));
    private final List<Card> value19Hand = List.of(new Card(Suit.HEARTS, Rank.FOUR), new Card(Suit.CLUBS, Rank.FIVE)
            , new Card(Suit.DIAMONDS, Rank.TEN));
    private final List<Card> value17Hand = List.of(new Card(Suit.HEARTS, Rank.TEN), new Card(Suit.HEARTS, Rank.SEVEN));




    @BeforeEach
    void setUp() {
        playerInGame = new PlayerInGame(123L, "example player");

        player = new Player("example player");
        player.setGamesPlayed(INITIAL_GAMES_PLAYED);
        player.setMoney(INITIAL_MONEY);

        lenient().when(mockBlackjackHelper.getSurrenderPayout(anyInt()))
                .thenAnswer(invocation -> (int) invocation.getArguments()[0] / 2);
        lenient().when(mockBlackjackHelper.getBlackjackPayout(anyInt()))
                .thenAnswer(invocation -> (int) ((int) invocation.getArguments()[0] * 2.5));
        lenient().when(mockBlackjackHelper.getTiePayout(anyInt()))
                .thenAnswer(invocation -> invocation.getArguments()[0]);
        lenient().when(mockBlackjackHelper.getWinPayout(anyInt()))
                .thenAnswer(invocation -> (int) invocation.getArguments()[0] * 2);

        lenient().when(mockBlackjackHelper.isBlackjack(anyList()))
                .thenAnswer(invocation -> invocation.getArguments()[0].equals(blackjackHand));
        lenient().when(mockBlackjackHelper.isBust(anyList()))
                .thenAnswer(invocation -> invocation.getArguments()[0].equals(bustHand));

        lenient().when(mockBlackjackHelper.getHandValue(blackjackHand)).thenReturn(21);
        lenient().when(mockBlackjackHelper.getHandValue(nonBlackjackValue21Hand)).thenReturn(21);
        lenient().when(mockBlackjackHelper.getHandValue(bustHand)).thenReturn(28);
        lenient().when(mockBlackjackHelper.getHandValue(value19Hand)).thenReturn(19);
        lenient().when(mockBlackjackHelper.getHandValue(value17Hand)).thenReturn(17);
    }

    @Test
    void determinePlayerFinalStatus_WhenWin_AssertPlayerStatusIsWin() {
        boolean croupierHasBj = false;
        int croupierScore = 17;
        playerInGame.setCards(value19Hand);

        StepVerifier.create(cleanUpService.determinePlayerFinalStatus(playerInGame, croupierHasBj, croupierScore))
                .consumeNextWith(unused -> {
                    assertEquals(PlayerStatus.WIN, playerInGame.getStatus());
                }).verifyComplete();
    }

    @Test
    void determinePlayerFinalStatus_WhenLoose_AssertPlayerStatusIsLoose() {
        boolean croupierHasBj = false;
        int croupierScore = 20;
        playerInGame.setCards(value19Hand);

        StepVerifier.create(cleanUpService.determinePlayerFinalStatus(playerInGame, croupierHasBj, croupierScore))
                .consumeNextWith(unused -> {
                    assertEquals(PlayerStatus.LOOSE, playerInGame.getStatus());
                }).verifyComplete();
    }

    @Test
    void determinePlayerFinalStatus_WhenTie_AssertPlayerStatusIsTie() {
        boolean croupierHasBj = false;
        int croupierScore = 19;
        playerInGame.setCards(value19Hand);

        StepVerifier.create(cleanUpService.determinePlayerFinalStatus(playerInGame, croupierHasBj, croupierScore))
                .consumeNextWith(unused -> {
                    assertEquals(PlayerStatus.TIE, playerInGame.getStatus());
                }).verifyComplete();
    }

    @Test
    void determinePlayerFinalStatus_WhenBlackjack_AssertPlayerStatusIsBlackjack() {
        boolean croupierHasBj = false;
        int croupierScore = 20;
        playerInGame.setCards(blackjackHand);

        StepVerifier.create(cleanUpService.determinePlayerFinalStatus(playerInGame, croupierHasBj, croupierScore))
                .consumeNextWith(unused -> {
                    assertEquals(PlayerStatus.BLACKJACK, playerInGame.getStatus());
                }).verifyComplete();
    }

    @Test
    void determinePlayerFinalStatus_WhenLooseAgainstBlackjack_AssertPlayerStatusIsLoose() {
        boolean croupierHasBj = true;
        int croupierScore = 21;
        playerInGame.setCards(value19Hand);

        StepVerifier.create(cleanUpService.determinePlayerFinalStatus(playerInGame, croupierHasBj, croupierScore))
                .consumeNextWith(unused -> {
                    assertEquals(PlayerStatus.LOOSE, playerInGame.getStatus());
                }).verifyComplete();
    }

    @Test
    void determinePlayerFinalStatus_WhenBothBlackjack_AssertPlayerStatusIsTie() {
        boolean croupierHasBj = true;
        int croupierScore = 21;
        playerInGame.setCards(blackjackHand);

        StepVerifier.create(cleanUpService.determinePlayerFinalStatus(playerInGame, croupierHasBj, croupierScore))
                .consumeNextWith(unused -> {
                    assertEquals(PlayerStatus.TIE, playerInGame.getStatus());
                }).verifyComplete();
    }

    @Test
    void determinePlayerFinalStatus_WhenSurrender_AssertPlayerStatusIsSurrender() {
        boolean croupierHasBj = true;
        int croupierScore = 21;
        playerInGame.setCards(value19Hand);
        playerInGame.setStatus(PlayerStatus.SURRENDER);

        StepVerifier.create(cleanUpService.determinePlayerFinalStatus(playerInGame, croupierHasBj, croupierScore))
                .consumeNextWith(unused -> {
                    assertEquals(PlayerStatus.SURRENDER, playerInGame.getStatus());
                }).verifyComplete();
    }

    @Test
    void determinePlayerFinalStatus_WhenBustHand_AssertPlayerStatusIsLoose() {
        boolean croupierHasBj = false;
        int croupierScore = 18;
        playerInGame.setCards(bustHand);

        StepVerifier.create(cleanUpService.determinePlayerFinalStatus(playerInGame, croupierHasBj, croupierScore))
                .consumeNextWith(unused -> {
                    assertEquals(PlayerStatus.LOOSE, playerInGame.getStatus());
                }).verifyComplete();
    }

    @Test
    void determinePlayerFinalStatus_WhenBustStatus_AssertPlayerStatusIsLoose() {
        boolean croupierHasBj = false;
        int croupierScore = 18;
        playerInGame.setCards(bustHand);
        playerInGame.setStatus(PlayerStatus.BUST);

        StepVerifier.create(cleanUpService.determinePlayerFinalStatus(playerInGame, croupierHasBj, croupierScore))
                .consumeNextWith(unused -> {
                    assertEquals(PlayerStatus.LOOSE, playerInGame.getStatus());
                }).verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({
            "LOOSE, 10, 0",
            "SURRENDER, 10, 5",
            "TIE, 10, 10",
            "WIN, 10, 20",
            "BLACKJACK, 10, 25"
    })
    void resolveBet_GivenPlayerStatus_WinningsAreCorrect(PlayerStatus status, int bet, int expectedWinnings){
        when(mockPlayerService.getPlayerById(anyLong())).thenReturn(Mono.just(player));
        when(mockPlayerService.savePlayer(player)).thenReturn(Mono.just(player));

        playerInGame.setStatus(status);
        playerInGame.setBet(bet);

        StepVerifier.create(cleanUpService.resolveBet(playerInGame))
                .consumeNextWith(player -> {
                    assertEquals(INITIAL_MONEY + expectedWinnings, player.getMoney());
                }).verifyComplete();
    }

    @ParameterizedTest
    @MethodSource(value = "playerInvalidStatus")
    void resolveBet_WhenInvalidStatus_ThrowsIllegalArgumentException(PlayerStatus status){
        playerInGame.setStatus(status);
        playerInGame.setBet(10);

        StepVerifier.create(cleanUpService.resolveBet(playerInGame))
                .expectError(IllegalPlayerStatusException.class)
                .verify();
    }

    private static Stream<PlayerStatus> playerInvalidStatus(){
        return Stream.of(
                PlayerStatus.PENDING_BET,
                PlayerStatus.PLAYING,
                PlayerStatus.STAND,
                PlayerStatus.BUST);
    }

    @Test
    void resolveBet_WhenProcessed_IncreaseByOneGamesPlayed(){
        when(mockPlayerService.getPlayerById(anyLong())).thenReturn(Mono.just(player));
        when(mockPlayerService.savePlayer(player)).thenReturn(Mono.just(player));

        playerInGame.setStatus(PlayerStatus.LOOSE);
        playerInGame.setBet(10);

        StepVerifier.create(cleanUpService.resolveBet(playerInGame))
                .consumeNextWith(player -> {
                    assertEquals(INITIAL_GAMES_PLAYED + 1, player.getGamesPlayed());
                }).verifyComplete();
    }

}
