package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

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
        dto.put("gamePlayers", game.getGamePlayers().stream().map(g ->makeGamePlayerDTO(g)).collect(Collectors.toList()));
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

    @RequestMapping("/games")
    public Map<String, Object> getMapOfPlayers() {
        List<Game> list = gameRepository.findAll();
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("games", list.stream().map(this::makeGameDTO).collect(Collectors.toList()));
        return dto;
    }
/*    public List<Object> getGames() {
        List<Game> list = gameRepository.findAll();
        return list.stream().map(game -> makeGameDTO(game)).collect(Collectors.toList());
    }*/

    @RequestMapping("/game_view/{game_player_id}")
    public Map<String, Object> showGame(@PathVariable long game_player_id) {
        GamePlayer gamePlayer = gamePlayerRepository.getById(game_player_id);
        Map<String, Object> dto = makeGameDTO(gamePlayer.getGame());
        dto.put("ships", gamePlayer.getShips().stream().map(this::makeShipsDTO).collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(salvo -> salvo.getSalvoes().stream().map(this::makeSalvoesDTO)));
        return dto;
    }




    /*

    public double getPlayerScore(Player player) {
        List<Game> playerGameList = player.getGame();
        double totalScore = 0;
        for (Game g : playerGameList.getScore()) {
            totalScore = totalScore + g;
        }
        return totalScore;
    }

    public double getPlayerWinCount(Player player) {
        List<Game> playerGameList = player.getGame();
        double totalWinCount = 0;
        for (Game g : playerGameList.getScores()) {
            if(g == 1) {
                totalWinCount = totalWinCount + g;
            }
        }
        return totalWinCount;
    }

    public double getPlayerLossCount(Player player) {
        List<Game> playerGameList = player.getGame();
        double totalLossCount = 0;
        for (Game g : playerGameList.getScores()) {
            if(g == 0) {
                totalLossCount = totalLossCount + g;
            }
        }
        return totalLossCount;
    }

    public double getPlayerTieCount(Player player) {
        List<Game> playerGameList = player.getGame();
        double totalTieCount = 0;
        for (Game g : playerGameList.getScores()) {
            if(g == 0.5) {
                totalTieCount = totalTieCount + g;
            }
        }
        return totalTieCount;
    }
    */




    public GameRepository getGameRepository() {
        return gameRepository;
    }
}
