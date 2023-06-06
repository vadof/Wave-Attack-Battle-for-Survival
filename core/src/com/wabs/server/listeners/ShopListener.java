package com.wabs.server.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.global.weaponEvents.BulletsBuyEvent;
import com.wabs.global.weaponEvents.WeaponBuyEvent;
import com.wabs.server.gameplay.GameLobby;
import com.wabs.server.handlers.GameLobbyHandler;
import com.wabs.server.supers.ServerPlayer;

public class ShopListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof WeaponBuyEvent) {
            WeaponBuyEvent weaponBuyEvent = (WeaponBuyEvent) object;

            GameLobby gameLobby = GameLobbyHandler.getInstance().getGameLobbyById(weaponBuyEvent.lobbyId);
            ServerPlayer player = gameLobby.getPlayerById(weaponBuyEvent.playerId);

            if (player.getMoney() >= weaponBuyEvent.price) {
                if (player.buyWeapon(weaponBuyEvent.weapon, weaponBuyEvent.slot, weaponBuyEvent.price)) {
                    gameLobby.sendToAllTCP(weaponBuyEvent);
                }
            }
        } else if (object instanceof BulletsBuyEvent) {
            BulletsBuyEvent bulletsBuyEvent = (BulletsBuyEvent) object;

            GameLobby gameLobby = GameLobbyHandler.getInstance().getGameLobbyById(bulletsBuyEvent.lobbyId);
            ServerPlayer player = gameLobby.getPlayerById(bulletsBuyEvent.playerId);

            if (player.getMoney() >= bulletsBuyEvent.price) {
                player.removeMoney(bulletsBuyEvent.price);
                gameLobby.sendToAllTCP(bulletsBuyEvent);
            }
        }
        super.received(connection, object);
    }

}
