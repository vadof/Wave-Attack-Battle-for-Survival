package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.handlers.PlayerHandler;
import com.wabs.game.supers.Player;
import com.wabs.global.weaponEvents.MoneyAdditionEvent;

public class MoneyListener extends Listener {

    public void received(Connection connection, Object object) {
        if (object instanceof MoneyAdditionEvent) {
            MoneyAdditionEvent moneyAdditionEvent = (MoneyAdditionEvent) object;

            Player player = PlayerHandler.INSTANCE.getPlayerById(moneyAdditionEvent.playerId);

            player.addMoney(moneyAdditionEvent.amount);
        }
    }

}
