package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.WaveAttack;
import com.wabs.game.handlers.LobbyHandler;
import com.wabs.game.screens.GameScreen;
import com.wabs.game.screens.lobby.AvailableLobbiesScreen;
import com.wabs.global.Lobby;
import com.wabs.game.screens.lobby.LobbyInfoScreen;
import com.wabs.global.lobbyEvents.*;

public class LobbyListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof LobbyInfoEvent) {
            LobbyInfoEvent lobbyInfoEvent = (LobbyInfoEvent) object;
            for (Lobby lobby : lobbyInfoEvent.lobbyList) {
                LobbyHandler.getInstance().addLobby(lobby);
            }
        }

        if (object instanceof CreateLobbyEvent) {
            CreateLobbyEvent createLobbyEvent = (CreateLobbyEvent) object;
            Lobby lobby = createLobbyEvent.lobby;

            LobbyHandler.getInstance().addLobby(lobby);
            if (lobby.getAdmin().equals(WaveAttack.getInstance().getClientUsername())) {
                LobbyInfoScreen.getInstance().refresh(lobby);
                WaveAttack.getInstance().setScreen(LobbyInfoScreen.getInstance());
                WaveAttack.getInstance().setCurrentLobbyId(lobby.getLobbyId());
            }
        }

        if (object instanceof JoinTheLobbyEvent) {
            JoinTheLobbyEvent joinTheLobbyEvent = (JoinTheLobbyEvent) object;
            LobbyHandler.getInstance().addUserToLobby(joinTheLobbyEvent.username, joinTheLobbyEvent.lobbyId);

            if (joinTheLobbyEvent.username.equals(WaveAttack.getInstance().getClientUsername())) {
                WaveAttack.getInstance().setCurrentLobbyId(joinTheLobbyEvent.lobbyId);
                LobbyInfoScreen.getInstance().refresh(LobbyHandler.getInstance().getLobbyById(joinTheLobbyEvent.lobbyId));
                WaveAttack.getInstance().setScreen(LobbyInfoScreen.getInstance());
            }

            Long current = WaveAttack.getInstance().getCurrentLobbyId();
            if (current != null && current.equals(joinTheLobbyEvent.lobbyId)) {
                LobbyInfoScreen.getInstance().refresh(LobbyHandler.getInstance().getLobbyById(current));
            }
        }

        if (object instanceof LobbyLeaveEvent) {
            LobbyLeaveEvent lobbyLeaveEvent = (LobbyLeaveEvent) object;
            LobbyHandler.getInstance().removeUserFromLobby(lobbyLeaveEvent.username, lobbyLeaveEvent.lobbyId);

            if (lobbyLeaveEvent.lobbyId.equals(WaveAttack.getInstance().getCurrentLobbyId())) {

                if (lobbyLeaveEvent.username.equals(WaveAttack.getInstance().getClientUsername())) {
                    AvailableLobbiesScreen.getInstance().refresh();
                    WaveAttack.getInstance().setScreen(AvailableLobbiesScreen.getInstance());

                    WaveAttack.getInstance().setCurrentLobbyId(null);
                } else {
                    LobbyInfoScreen.getInstance().refresh(LobbyHandler.getInstance().getLobbyById(lobbyLeaveEvent.lobbyId));
                }

            }
        }

        if (object instanceof LobbyRemoveEvent) {
            LobbyHandler.getInstance().removeLobbyById(((LobbyRemoveEvent) object).lobbyId);
        }

        if (object instanceof AdminChangeEvent) {
            AdminChangeEvent adminChangeEvent = (AdminChangeEvent) object;

            if (adminChangeEvent.inGame) {
                if (adminChangeEvent.newAdmin.equals(WaveAttack.getInstance().getClientUsername())) {
                    GameScreen.getInstance().setAdmin();
                }
            } else {
                LobbyHandler.getInstance().getLobbyById(adminChangeEvent.lobbyId).setAdmin(adminChangeEvent.newAdmin);
            }
        }

        super.received(connection, object);
    }

}
