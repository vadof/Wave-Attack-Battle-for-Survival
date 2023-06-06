package com.wabs.server.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.global.Lobby;
import com.wabs.global.lobbyEvents.*;
import com.wabs.server.ServerFoundation;
import com.wabs.server.handlers.UserHandler;
import com.wabs.server.handlers.ServerLobbyHandler;

public class ServerLobbyListener extends Listener {

    CreateLobbyEvent createLobbyEvent = new CreateLobbyEvent();

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof CreateLobbyEvent) {
            CreateLobbyEvent createLobbyEvent = (CreateLobbyEvent) object;
            Lobby lobby = new Lobby(createLobbyEvent.name, createLobbyEvent.admin, createLobbyEvent.privateLobby);

            ServerLobbyHandler.getInstance().addLobby(lobby);
            UserHandler.getInstance().getUserByUsername(createLobbyEvent.admin).setCurrentLobbyId(lobby.getLobbyId());

            this.createLobbyEvent.lobby = lobby;

            ServerFoundation.instance.getServer().sendToAllTCP(this.createLobbyEvent);
        }

        if (object instanceof JoinTheLobbyEvent) {
            JoinTheLobbyEvent joinTheLobbyEvent = (JoinTheLobbyEvent) object;
            ServerLobbyHandler.getInstance().addUserToLobby(joinTheLobbyEvent.username, joinTheLobbyEvent.lobbyId);

            Lobby lobby = ServerLobbyHandler.getInstance().getLobbyById(joinTheLobbyEvent.lobbyId);

            if (lobby != null) {
                UserHandler.getInstance().getUserByUsername(joinTheLobbyEvent.username).setCurrentLobbyId(lobby.getLobbyId());
                ServerFoundation.instance.getServer().sendToAllTCP(joinTheLobbyEvent);
            }
        }

        if (object instanceof LobbyLeaveEvent) {
            LobbyLeaveEvent lobbyLeaveEvent = (LobbyLeaveEvent) object;

            ServerLobbyHandler.getInstance().removeUserFromLobby(lobbyLeaveEvent.username, lobbyLeaveEvent.lobbyId);
        }

        super.received(connection, object);
    }
}
