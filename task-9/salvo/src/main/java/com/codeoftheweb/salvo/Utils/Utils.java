package com.codeoftheweb.salvo.Utils;

import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    @Autowired
    private PlayerRepository playerRepository;

    public static Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static boolean isGuest(Authentication auth) {
        return auth == null || auth instanceof AnonymousAuthenticationToken;
    }

    public Player getPlayers(Authentication auth) {
        return playerRepository.findByUserName(auth.getName());
    }

}
