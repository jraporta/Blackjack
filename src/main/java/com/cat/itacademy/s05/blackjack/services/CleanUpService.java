package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.enums.PlayerStatus;
import com.cat.itacademy.s05.blackjack.exceptions.custom.IllegalPlayerStatusException;
import com.cat.itacademy.s05.blackjack.exceptions.custom.IllegalGameStateException;
import com.cat.itacademy.s05.blackjack.model.Player;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import com.cat.itacademy.s05.blackjack.utils.BlackjackHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CleanUpService {

    private final BlackjackHelper helper;
    private final PlayerService playerService;

    public CleanUpService(BlackjackHelper helper, PlayerService playerService) {
        this.helper = helper;
        this.playerService = playerService;
    }

    public Mono<Player> resolveBet(PlayerInGame playerInGame) {
        return getWinnings(playerInGame)
                .flatMap(winnings -> updatePlayer(playerInGame.getId(), winnings));
    }

    public Mono<PlayerInGame> determinePlayerFinalStatus(PlayerInGame playerInGame, boolean croupierHasBlackjack,
                                                         int croupierScore) {
        try {
            playerInGame.setStatus(determinePlayerStatus(playerInGame, croupierHasBlackjack, croupierScore));
            return Mono.just(playerInGame);
        }catch (IllegalGameStateException e) {
            return Mono.error(e);
        }

    }

    private PlayerStatus determinePlayerStatus(PlayerInGame player, boolean croupierHasBlackjack, int croupierScore) {
        if (player.getStatus() == PlayerStatus.SURRENDER) {
            return PlayerStatus.SURRENDER;
        } else if (helper.isBlackjack(player.getCards()) && !croupierHasBlackjack) {
            return PlayerStatus.BLACKJACK;
        } else if (player.getStatus() == PlayerStatus.BUST || helper.isBust(player.getCards())) {
            return PlayerStatus.LOOSE;
        } else if (croupierScore > 21 || helper.getHandValue(player.getCards()) > croupierScore) {
            return PlayerStatus.WIN;
        } else if ((croupierHasBlackjack && !helper.isBlackjack(player.getCards())) ||
                croupierScore > helper.getHandValue(player.getCards())) {
            return PlayerStatus.LOOSE;
        } else if (helper.getHandValue(player.getCards()) == croupierScore) {
            return PlayerStatus.TIE;
        }
        throw new IllegalGameStateException("Unexpected game state for player: " + player.getId());
    }

    private Mono<Integer> getWinnings(PlayerInGame player) {
        try {
            int winnings = switch (player.getStatus()) {
                case SURRENDER -> helper.getSurrenderPayout(player.getBet());
                case BLACKJACK -> helper.getBlackjackPayout(player.getBet());
                case TIE -> helper.getTiePayout(player.getBet());
                case PlayerStatus.WIN -> helper.getWinPayout(player.getBet());
                case PlayerStatus.LOOSE -> 0;
                default -> throw new IllegalPlayerStatusException("Invalid player status for player: " + player.getId());
            };
            return Mono.just(winnings);
        }catch (IllegalPlayerStatusException e) {
            return Mono.error(e);
        }


    }

    private Mono<Player> updatePlayer(String playerId, int winnings) {
        return playerService.getPlayerById(playerId)
                .flatMap(player -> {
                    player.setGamesPlayed(player.getGamesPlayed() + 1);
                    player.setMoney(player.getMoney() + winnings);
                    return playerService.savePlayer(player);
                });
    }

}
