package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.handlers.PlayerHandler;
import com.wabs.game.supers.Player;
import com.wabs.global.weaponEvents.WeaponChangeEvent;

public class WeaponListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof WeaponChangeEvent) {
            WeaponChangeEvent weaponChangeEvent = (WeaponChangeEvent) object;
            Player player = PlayerHandler.INSTANCE.getPlayerById(weaponChangeEvent.playerId);

            player.switchWeapon(weaponChangeEvent.slot);
        }


        super.received(connection, object);
    }

}
