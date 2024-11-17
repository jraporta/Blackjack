package com.cat.itacademy.s05.blackjack.services;


import com.cat.itacademy.s05.blackjack.dto.PlayDTO;
import com.cat.itacademy.s05.blackjack.dto.gamedto.GameDTO;
import com.cat.itacademy.s05.blackjack.dto.gamedto.GameDTOFactory;
import com.cat.itacademy.s05.blackjack.exceptions.custom.GameNotFoundException;
import com.cat.itacademy.s05.blackjack.model.*;
import com.cat.itacademy.s05.blackjack.repositories.GameRepository;
import com.cat.itacademy.s05.blackjack.utils.BlackjackHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final PlayerServiceImpl playerService;
    private final PlayService playService;
    private final DeckService deckService;
    private final GameDTOFactory gameDTOFactory;
    private final CleanUpService cleanUpService;
    private final CroupierService croupierService;
    private final BlackjackHelper blackjackHelper;

    public GameServiceImpl(GameRepository gameRepository, PlayerServiceImpl playerService, PlayService playService,
                           DeckService deckService, GameDTOFactory gameDTOFactory, CleanUpService cleanUpService,
                           CroupierService croupierService, BlackjackHelper blackjackHelper) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
        this.playService = playService;
        this.deckService = deckService;
        this.gameDTOFactory = gameDTOFactory;
        this.cleanUpService = cleanUpService;
        this.croupierService = croupierService;
        this.blackjackHelper = blackjackHelper;
    }

    @Override
    public Mono<String> createGame(String playerName) {
        return initializeGame()
                .flatMap(game -> addPlayer(game, playerName))
                .flatMap(gameRepository::save)
                .map(Game::getId);
    }

    private Mono<Game> initializeGame() {
        return Mono.fromCallable(() -> {
            Game game = new Game();
            game.setDeck(deckService.generateDeck(new Deck()));
            return game;
        });
    }

    private Mono<Game> addPlayer(Game game, String playerName) {
        return playerService.getPlayer(playerName)
                .switchIfEmpty(playerService.createPlayer(playerName))
                .flatMap(player -> {
                    PlayerInGame playerInGame = new PlayerInGame(player.getId(),player.getName());
                    game.getPlayers().add(playerInGame);
                    return Mono.just(game);
                });
    }

    @Override
    public Mono<Game> getGame(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(() -> new GameNotFoundException("No game with id: " + gameId)));
    }

    @Override
    public Mono<GameDTO> getGameDTO(String gameId) {
        return getGame(gameId)
                .map(gameDTOFactory::getGameDTO);
    }

    private Flux<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public Mono<Game> saveGame(Game game) {
        return gameRepository.save(game);
    }

    @Override
    public Mono<Void> deleteGame(String id) {
        return getGame(id).
                flatMap(gameRepository::delete);
    }

    @Override
    public Mono<Player> updatePlayerNameInGames(Player player) {
        return getAllGames()
                .flatMap(game -> updatePlayerNameInGame(game, player))
                .then(Mono.just(player));
    }

    @Override
    public Mono<Void> executePlay(String gameId, PlayDTO play) {
        return getGame(gameId)
                .flatMap(game -> playService.executePlay(game, play))
                .flatMap(game -> {
                    if (game.isConcluded()) {
                        return croupierService.resolveCroupierHand(game)
                                .flatMap(this::executeCleanUp);
                    }
                    return Mono.just(game);
                })
                .flatMap(gameRepository::save)
                .flatMap(game -> Mono.empty());
    }

    private Mono<Game> executeCleanUp(Game game) {
        boolean croupierHasBlackjack = blackjackHelper.isBlackjack(game.getCroupier().getCards());
        int croupierScore = blackjackHelper.getHandValue(game.getCroupier().getCards());
        return Flux.fromIterable(game.getPlayers())
                .flatMap(playerInGame -> cleanUpService.determinePlayerFinalStatus(playerInGame, croupierHasBlackjack,
                        croupierScore))
                .flatMap(cleanUpService::resolveBet)
                .then(Mono.defer(() -> Mono.just(game)));
    }

    private Mono<Game> updatePlayerNameInGame(Game game, Player player) {
        boolean isChanged = game.getPlayers().stream()
                .filter(playerInGame -> playerInGame.getId().equals(player.getId()))
                .peek(playerInGame -> playerInGame.setName(player.getName()))
                .findFirst().isPresent();
        return isChanged ? gameRepository.save(game) : Mono.empty();
    }
}
