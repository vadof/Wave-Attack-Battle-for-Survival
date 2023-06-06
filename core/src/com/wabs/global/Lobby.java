package com.wabs.global;

import com.wabs.global.lobbyEvents.LobbyRemoveEvent;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    private static Long lobbyId = 0L;

    private final List<String> playerUsernames = new ArrayList<>();
    private Long id;
    private String lobbyName;
    private String admin;
    private boolean privateLobby;

    public Lobby() {

    }

    public Lobby(String lobbyName, String admin, boolean privateLobby) {
        this.id = lobbyId++;
        this.lobbyName = lobbyName;
        this.admin = admin;
        this.privateLobby = privateLobby;
        playerUsernames.add(admin);
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void addPlayer(String username) {
        playerUsernames.add(username);
    }

    public void removePlayer(String username) {
        playerUsernames.remove(username);
    }

    public Long getLobbyId() {
        return id;
    }

    public int getLobbySize() {
        return playerUsernames.size();
    }

    public List<String> getPlayerUsernames() {
        return playerUsernames;
    }

    public boolean isPrivate() {
        return privateLobby;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String username) {
        this.admin = username;
    }
}