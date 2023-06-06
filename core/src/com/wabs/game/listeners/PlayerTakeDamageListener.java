package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.handlers.PlayerHandler;
import com.wabs.game.supers.Player;
import com.wabs.global.playerEvents.PlayerDeadEvent;
import com.wabs.global.playerEvents.PlayerTakeDamageEvent;

public class PlayerTakeDamageListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof PlayerTakeDamageEvent) {
            PlayerTakeDamageEvent playerTakeDamageEvent = (PlayerTakeDamageEvent) object;
            Player player = PlayerHandler.INSTANCE.getPlayerById(playerTakeDamageEvent.playerId);
            player.takeDamage(playerTakeDamageEvent.damage);
        }

        if (object instanceof PlayerDeadEvent) {
            Player player = PlayerHandler.INSTANCE.getPlayerById(((PlayerDeadEvent) object).id);
            player.die();
        }


        super.received(connection, object);
    }

}
