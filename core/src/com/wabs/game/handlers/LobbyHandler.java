package com.wabs.game.handlers;

import com.wabs.global.Lobby;

import java.util.ArrayList;
import java.util.List;

public class LobbyHandler {

    private static LobbyHandler INSTANCE = null;

    List<Lobby> lobbyList = new ArrayList<>();

    private LobbyHandler() {

    }

    public static LobbyHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LobbyHandler();
        }
        return INSTANCE;
    }

    public void addLobby(Lobby lobby) {
        lobbyList.add(lobby);
    }

    public void removeLobby(Lobby lobby) {
        lobbyList.remove(lobby);
    }

    public void removeLobbyById(Long lobbyId) {
        for (Lobby lobby : lobbyList) {
            if (lobby.getLobbyId().equals(lobbyId)) {
                lobbyList.remove(lobby);
                break;
            }
        }
    }

    public List<Lobby> getLobbyList() {
        return lobbyList;
    }

    public Lobby getLobbyById(Long id) {
        for (Lobby lobby : lobbyList) {
            if (lobby.getLobbyId() == id) {
                return lobby;
            }
        }
        return null;
    }

    public void addUserToLobby(String username, Long lobbyId) {
        Lobby lobby = getLobbyById(lobbyId);
        if (lobby != null) {
            lobby.addPlayer(username);
        }
    }

    public void removeUserFromLobby(String username, Long lobbyId) {
        getLobbyById(lobbyId).removePlayer(username);
    }
}
