package com.wabs.server.handlers;

import com.wabs.global.Lobby;
import com.wabs.global.lobbyEvents.AdminChangeEvent;
import com.wabs.global.lobbyEvents.LobbyLeaveEvent;
import com.wabs.global.lobbyEvents.LobbyRemoveEvent;
import com.wabs.server.ServerFoundation;

import java.util.ArrayList;
import java.util.List;

public class ServerLobbyHandler {

    private static ServerLobbyHandler INSTANCE = null;

    private ServerLobbyHandler() {

    }

    public static ServerLobbyHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerLobbyHandler();
        }
        return INSTANCE;
    }

    public List<Lobby> lobbyList = new ArrayList<>();

    public void startGameForLobby(Lobby lobby) {

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

    public boolean userInLobby(String username) {
        for (Lobby lobby : lobbyList) {
            for (String username1 : lobby.getPlayerUsernames()) {
                if (username1.equals(username)) {
                    return true;
                }
            }
        }
        return false;
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
        Lobby lobby = getLobbyById(lobbyId);
        lobby.removePlayer(username);

        LobbyLeaveEvent lobbyLeaveEvent = new LobbyLeaveEvent();
        lobbyLeaveEvent.username = username;
        lobbyLeaveEvent.lobbyId = lobbyId;

        ServerFoundation.instance.getServer().sendToAllTCP(lobbyLeaveEvent);

        UserHandler.getInstance().getUserByUsername(lobbyLeaveEvent.username).setCurrentLobbyId(null);
        if (lobby.getLobbySize() == 0) {
            removeLobby(lobby);
            LobbyRemoveEvent lobbyRemoveEvent = new LobbyRemoveEvent();
            lobbyRemoveEvent.lobbyId = lobby.getLobbyId();

            ServerFoundation.instance.getServer().sendToAllTCP(lobbyRemoveEvent);
        } else if (lobby.getAdmin().equals(lobbyLeaveEvent.username)) {
            AdminChangeEvent adminChangeEvent = new AdminChangeEvent();
            adminChangeEvent.newAdmin = setNewLobbyAdmin(lobby.getLobbyId());
            adminChangeEvent.lobbyId = lobbyId;
            adminChangeEvent.inGame = false;

            ServerFoundation.instance.getServer().sendToAllTCP(adminChangeEvent);
        }
    }

    public String setNewLobbyAdmin(Long lobbyId) {
        Lobby lobby = getLobbyById(lobbyId);
        String newAdmin = null;
        if (lobby != null && lobby.getPlayerUsernames().size() > 0) {
            newAdmin = lobby.getPlayerUsernames().get(0);
            lobby.setAdmin(newAdmin);
        }
        return newAdmin;
    }
}
