package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.handlers.PlayerHandler;
import com.wabs.game.supers.Player;
import com.wabs.global.playerEvents.PlayerRemoveEvent;

public class PlayerRemoveListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof PlayerRemoveEvent) {
            PlayerRemoveEvent playerRemoveEvent = (PlayerRemoveEvent) object;
            PlayerHandler playerHandler = PlayerHandler.INSTANCE;

            Player disconnectedPlayer = playerHandler.getPlayerById(playerRemoveEvent.id);

            playerHandler.removePlayer(disconnectedPlayer);

            for (Player player : playerHandler.getPlayers()) {
                player.removePlayerFromSceneManager(disconnectedPlayer);
            }
        }

        super.received(connection, object);
    }

}
