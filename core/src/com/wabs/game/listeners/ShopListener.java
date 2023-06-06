package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.handlers.PlayerHandler;
import com.wabs.game.supers.Player;
import com.wabs.game.weapons.Arsenal;
import com.wabs.game.weapons.Weapon;
import com.wabs.global.weaponEvents.BulletsBuyEvent;
import com.wabs.global.weaponEvents.WeaponBuyEvent;

public class ShopListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof WeaponBuyEvent) {
            WeaponBuyEvent weaponBuyEvent = (WeaponBuyEvent) object;
            Player player = PlayerHandler.INSTANCE.getPlayerById(weaponBuyEvent.playerId);

            Weapon weapon = Arsenal.getWeapon(weaponBuyEvent.weapon);
            if (weapon != null) {
                player.buyWeapon(weapon);
            }
        } else if (object instanceof BulletsBuyEvent) {
            BulletsBuyEvent bulletsBuyEvent = (BulletsBuyEvent) object;
            Player player = PlayerHandler.INSTANCE.getPlayerById(bulletsBuyEvent.playerId);

            player.buyBullets(bulletsBuyEvent.slot, bulletsBuyEvent.price);
        }

        super.received(connection, object);
    }
}
