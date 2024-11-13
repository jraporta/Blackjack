package com.cat.itacademy.s05.blackjack.services;

import com.cat.itacademy.s05.blackjack.enums.PlayerStatus;
import com.cat.itacademy.s05.blackjack.model.Game;
import com.cat.itacademy.s05.blackjack.model.PlayerInGame;
import com.cat.itacademy.s05.blackjack.utils.BlackjackHelper;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PrizesService {

    BlackjackHelper helper;
    PlayerService playerService;

    public PrizesService(BlackjackHelper helper, PlayerService playerService) {
        this.helper = helper;
        this.playerService = playerService;
    }

    public Mono<Game> resolveBets(Game game) {
        boolean croupierHasBlackjack = helper.isBlackjack(game.getCroupier().getCards());
        int croupierScore = helper.getHandValue(game.getCroupier().getCards());
        return Flux.fromIterable(game.getPlayers())
                .flatMap(playerInGame -> processWinnings(playerInGame, croupierHasBlackjack, croupierScore))
                .then(Mono.defer(() -> Mono.just(game)));
    }

    //TODO make winnings dependant on the properties file
    private Publisher<?> processWinnings(PlayerInGame player, boolean croupierHasBlackjack, int croupierScore) {
        int winnings = 0;
        if (player.getStatus() == PlayerStatus.SURRENDER) {
            winnings = player.getBet() / 2;
        }else if (helper.isBust(player.getCards())) {
            player.setStatus(PlayerStatus.BUST);
        }else if (helper.isBlackjack(player.getCards())) {
            if (croupierHasBlackjack) {
                player.setStatus(PlayerStatus.BLACKJACK_TIE);
                winnings = player.getBet();
            }else {
                player.setStatus(PlayerStatus.BLACKJACK);
                winnings = (int) (player.getBet() * (1 + 1.5));
            }
        }else if (croupierHasBlackjack) {
            player.setStatus(PlayerStatus.LOOSE);
        }else if (croupierScore > 21 || helper.getHandValue(player.getCards()) > croupierScore) {
            player.setStatus(PlayerStatus.WIN);
            winnings = player.getBet() * 2;
        }else if (helper.getHandValue(player.getCards()) == croupierScore){
            player.setStatus(PlayerStatus.TIE);
            winnings = player.getBet();
        }else if (croupierScore > helper.getHandValue(player.getCards())) {
            player.setStatus(PlayerStatus.LOOSE);
        }
        return playerService.addMoney(player.getId(), winnings);
    }

}
