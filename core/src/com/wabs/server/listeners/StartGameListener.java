package com.wabs.server.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.global.Lobby;
import com.wabs.global.gameEvents.StartGameEvent;
import com.wabs.global.lobbyEvents.LobbyRemoveEvent;
import com.wabs.server.gameplay.GameLobby;
import com.wabs.server.handlers.GameLobbyHandler;
import com.wabs.server.ServerFoundation;
import com.wabs.server.handlers.ServerLobbyHandler;

public class StartGameListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof StartGameEvent) {
            StartGameEvent startGameEvent = (StartGameEvent) object;
            Lobby lobby = ServerLobbyHandler.getInstance().getLobbyById(startGameEvent.lobbyId);

            GameLobby gameLobby = new GameLobby(lobby.getLobbyId(), lobby.getPlayerUsernames(), lobby.getAdmin());
            GameLobbyHandler.getInstance().addGameLobby(gameLobby);

            destroyLobby(lobby);

            gameLobby.startGame();
        }

        super.received(connection, object);
    }

    private void destroyLobby(Lobby lobby) {
        ServerLobbyHandler.getInstance().removeLobby(lobby);
        LobbyRemoveEvent lobbyRemoveEvent = new LobbyRemoveEvent();
        lobbyRemoveEvent.lobbyId = lobby.getLobbyId();

        ServerFoundation.instance.getServer().sendToAllTCP(lobbyRemoveEvent);
    }

}
