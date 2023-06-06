package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.handlers.PlayerHandler;
import com.wabs.game.supers.Player;
import com.wabs.global.playerEvents.MoveUpdateEvent;

public class MoveUpdateListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof MoveUpdateEvent) {
            PlayerHandler playerHandler = PlayerHandler.INSTANCE;
            MoveUpdateEvent moveUpdateEvent = (MoveUpdateEvent) object;

            Player player = playerHandler.getPlayerById(moveUpdateEvent.playerId);
            player.updatePlayerScene(moveUpdateEvent.playerTransform);

        }

        super.received(connection, object);
    }

}
