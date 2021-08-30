package com.codeoftheweb.salvo.DTO;

import com.codeoftheweb.salvo.model.Game;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class makeGameViewDTO {

    public static Map<String, Object> GameViewDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers", game.getGamePlayers().stream().map(makeGamePlayerDTO::GamePlayerDTO).collect(Collectors.toList()));

        return dto;
    }

}
