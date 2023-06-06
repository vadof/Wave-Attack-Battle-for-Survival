package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.WaveAttack;
import com.wabs.game.screens.lobby.AvailableLobbiesScreen;
import com.wabs.global.playerEvents.JoinResponseEvent;

public class PlayerJoinListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof JoinResponseEvent) {
            if (((JoinResponseEvent) object).access) {
                WaveAttack.getInstance().setScreen(AvailableLobbiesScreen.getInstance());
            }
        }

        super.received(connection, object);
    }

}