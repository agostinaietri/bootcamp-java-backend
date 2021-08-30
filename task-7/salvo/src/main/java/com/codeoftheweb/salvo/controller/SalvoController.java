package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import jdk.javadoc.doclet.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::makeGamePlayerDTO).collect(Collectors.toList()));
        dto.put("scores", game.getGamePlayers().stream().map(gp -> {
            if(gp.getScore(game).isPresent()) {
                return makeScoreDTO(gp.getScore(game).get());
            }
            else {
                return "match in progress";
            }
        }).collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }

    private Map<String, Object> makeSalvoesDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", salvo.getTurn());
        dto.put("player", salvo.getGamePlayer().getPlayer().getId());
        dto.put("locations", salvo.getLocations());
        return dto;
    }


    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        return dto;
    }

    private Map<String, Object> makeShipsDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getLocations());
        return dto;
    }

    private Map<String, Object> makeScoreDTO(Score score) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("player", score.getPlayer().getId());
        dto.put("score", score.getScore());
        dto.put("finishDate", score.getFinishDate());
        return dto;
    }

    private Map<String, Object> makeHitsDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("self", new ArrayList<>());
        dto.put("opponent", new ArrayList<>());
        return dto;
    }

    private Map<String, Object> makeGameViewDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::makeGamePlayerDTO).collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping("/games")
    public Map<String, Object> getCurrentGameInfo(Authentication authentication) {
        List<Game> gameList = gameRepository.findAll();
        Map<String, Object> gameInfoDTO = new LinkedHashMap<String, Object>();

        if(isGuest(authentication)) {
            gameInfoDTO.put("player", "Guest");
        } else {
            gameInfoDTO.put("player", makePlayerDTO(getPlayers(authentication)));
        }

        gameInfoDTO.put("games", gameList.stream().map(this::makeGameDTO).collect(Collectors.toList()));

        return gameInfoDTO;
    }

    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication auth) {
        Player player = playerRepository.findByUserName(auth.getName());
        if(isGuest(auth)) {
            return new ResponseEntity<>(makeMap("error", "user not logged in"), HttpStatus.UNAUTHORIZED);
        }
        Date joinDate = new Date();
        Game game = gameRepository.save(new Game(joinDate));
        GamePlayer gp = gamePlayerRepository.save(new GamePlayer(game, player, Date.from(joinDate.toInstant())));
        return new ResponseEntity<>(makeMap("gpid", gp.getId()), HttpStatus.CREATED);
    }


    public Player getPlayers(Authentication auth) {
            return playerRepository.findByUserName(auth.getName());
    }


    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> checkUser(@PathVariable long gamePlayerId, Authentication auth) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();
        Game game = gamePlayer.getGame();
        if(gamePlayer.getPlayer().getId() == getPlayers(auth).getId()) {
            Map<String, Object> dto = makeGameViewDTO(game);
            dto.put("ships", gamePlayer.getShips().stream().map(this::makeShipsDTO).collect(Collectors.toList()));
            dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(salvo -> salvo.getSalvoes().stream().map(this::makeSalvoesDTO)));
            dto.put("hits", makeHitsDTO());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(makeMap("error", "something's happened"), HttpStatus.UNAUTHORIZED);
    }


    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByUserName(email) !=  null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        } else {
            playerRepository.save(new Player(email, passwordEncoder.encode(password)));
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @PostMapping(path = "/game/{gameId}/players")
    public ResponseEntity<Object> createGame(@PathVariable long gameId, Authentication auth) {
        Optional<Game> game = gameRepository.findById(gameId);
        if(!isGuest(auth)) {
            if (!game.isEmpty()) {
                if (game.get().getGamePlayers().size() == 1) {
                    GamePlayer gamePlayer = game.get().getGamePlayers().stream().findFirst().get();
                    Player player = playerRepository.findByUserName(auth.getName());
                    if (gamePlayer.getPlayer() != player) {
                        Date creationDate = new Date();
                        GamePlayer gp = new GamePlayer(game.get(), player, creationDate);
                        gamePlayerRepository.save(gp);
                        return new ResponseEntity<>((makeMap("gpid", gp.getId())), HttpStatus.CREATED);
                    }
                    return new ResponseEntity<>(makeMap("error", "cannot join the same game twice"), HttpStatus.FORBIDDEN);
                }
                return new ResponseEntity<>(makeMap("error", "game already full"), HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(makeMap("error", "game doesn't exist"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(makeMap("error", "user isn't logged in"), HttpStatus.UNAUTHORIZED);
    }

/*
   @PostMapping(path = "/game/{gameId}/players")
    public ResponseEntity<Object> joinGame(@PathVariable long gameId, Authentication auth) {
        if(isGuest(auth)) {
            return new ResponseEntity<>(makeMap("error", "-"), HttpStatus.UNAUTHORIZED);
        } else if(gameRepository.findById(gameId) == null) {
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        } else if(gameRepository.findById(gameId).get().getGamePlayers().size() > 1) {
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        } else {
            Player player = playerRepository.findByUserName(auth.getName());
            Game game = gameRepository.findById(gameId).get();
            GamePlayer gp = new GamePlayer(player, game);
            gamePlayerRepository.save(gp);
            return new ResponseEntity<>((makeMap("gpid", gp.getId())), HttpStatus.CREATED);
        }
    }*/




    private boolean isGuest(Authentication auth) {
        return auth == null || auth instanceof AnonymousAuthenticationToken;
    }

    public GameRepository getGameRepository() {
        return gameRepository;
    }
}
