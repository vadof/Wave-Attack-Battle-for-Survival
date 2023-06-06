package com.wabs.server.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.global.playerEvents.MoveUpdateEvent;
import com.wabs.server.gameplay.GameLobby;
import com.wabs.server.handlers.GameLobbyHandler;
import com.wabs.server.supers.ServerPlayer;

public class MoveUpdateListener extends Listener {

    private GameLobby lobby;
    private ServerPlayer player;
    private GameLobbyHandler gameLobbyHandler = GameLobbyHandler.getInstance();

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof MoveUpdateEvent) {
            MoveUpdateEvent moveUpdateEvent = (MoveUpdateEvent) object;

            lobby = gameLobbyHandler.getGameLobbyById(moveUpdateEvent.lobbyId);

            if (lobby != null) {
                player = lobby.getPlayerById(moveUpdateEvent.playerId);
                if (player != null) {
                    player.setPlayerTransform(moveUpdateEvent.playerTransform);
                    for (ServerPlayer serverPlayer : lobby.getPlayers()) {
                        if (serverPlayer != player) {
                            serverPlayer.getConnection().sendUDP(moveUpdateEvent);
                        }
                    }
                }
            }
        }

        super.received(connection, object);
    }

}
