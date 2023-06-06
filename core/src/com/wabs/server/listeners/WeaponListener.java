package com.wabs.server.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.global.weaponEvents.WeaponChangeEvent;
import com.wabs.server.gameplay.GameLobby;
import com.wabs.server.handlers.GameLobbyHandler;
import com.wabs.server.supers.ServerPlayer;

public class WeaponListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {

         if (object instanceof WeaponChangeEvent) {
            WeaponChangeEvent weaponChangeEvent = (WeaponChangeEvent) object;

            GameLobby gameLobby = GameLobbyHandler.getInstance().getGameLobbyById(weaponChangeEvent.lobbyId);
            ServerPlayer player = gameLobby.getPlayerById(weaponChangeEvent.playerId);

            player.setCurrentWeapon(weaponChangeEvent.slot);

            gameLobby.sendToAllTCP(weaponChangeEvent);
        }

        super.received(connection, object);
    }

}
