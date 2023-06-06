package com.wabs.server.handlers;

import com.wabs.server.gameplay.GameLobby;
import com.wabs.server.User;
import com.wabs.server.supers.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLobbyHandler {

    private static GameLobbyHandler INSTANCE = null;

    private final Map<Long, GameLobby> gameLobbyMap = new HashMap<>();
    private final List<GameLobby> gameLobbyList = new ArrayList<>();

    private GameLobbyHandler() {

    }

    public static GameLobbyHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GameLobbyHandler();
        }
        return INSTANCE;
    }

    public void addGameLobby(GameLobby gameLobby) {
        gameLobbyMap.put(gameLobby.getLobbyId(), gameLobby);
        gameLobbyList.add(gameLobby);
    }

    public void removeGameLobby(GameLobby gameLobby) {
        gameLobbyMap.remove(gameLobby.getLobbyId());
        gameLobbyList.remove(gameLobby);

        for (ServerPlayer serverPlayer : gameLobby.getPlayers()) {
            User user = UserHandler.getInstance().getUserByUsername(serverPlayer.getUsername());
            user.setCurrentLobbyId(null);
            user.setCurrentlyPlaying(false);
        }
    }

    public GameLobby getGameLobbyById(Long id) {
        return gameLobbyMap.get(id);
    }

    public List<GameLobby> getGameLobbyList() {
        return gameLobbyList;
    }
}
