package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.DTO.makeGameViewDTO;
import com.codeoftheweb.salvo.DTO.makeSalvoDTO;
import com.codeoftheweb.salvo.DTO.makeShipDTO;
import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeoftheweb.salvo.Utils.Utils.makeMap;


@RestController
@RequestMapping("/api")
public class GameViewController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    private Map<String, Object> HitDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("self", new ArrayList<>());
        dto.put("opponent", new ArrayList<>());
        return dto;
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> checkUser(@PathVariable long gamePlayerId, Authentication auth) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();
        Game game = gamePlayer.getGame();
        Player playerAuth = playerRepository.findByUserName(auth.getName());
        if (gamePlayer.getPlayer().getId() == playerAuth.getId()) {
            Map<String, Object> dto = makeGameViewDTO.GameViewDTO(game);
            dto.put("ships", gamePlayer.getShips().stream().map(makeShipDTO::ShipDTO).collect(Collectors.toList()));
            dto.put("salvoes", game.getGamePlayers().stream().flatMap(salvo -> salvo.getSalvoes().stream().map(makeSalvoDTO::SalvoDTO)).collect(Collectors.toList()));
            dto.put("hits", HitDTO());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }

        return new ResponseEntity<>(makeMap("error", "you're not allowed to see this information"), HttpStatus.UNAUTHORIZED);
    }

}
