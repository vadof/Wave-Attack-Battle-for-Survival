package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.WaveAttack;
import com.wabs.game.handlers.PlayerHandler;
import com.wabs.game.screens.GameScreen;
import com.wabs.game.supers.Player;
import com.wabs.global.gameEvents.StartGameEvent;
import com.wabs.global.playerEvents.PlayerAddEvent;

public class PlayerAddListener extends Listener {

    @Override
    public void received(Connection connection, final Object object) {
        if (object instanceof PlayerAddEvent) {
            PlayerAddEvent playerAddEvent = (PlayerAddEvent) object;

            Player newPlayer = new Player(playerAddEvent.username, playerAddEvent.playerId,
                    playerAddEvent.playerTransform, playerAddEvent.spawnPosition, playerAddEvent.lobbyId);

            if (WaveAttack.getInstance().getClientUsername().equals(newPlayer.getUsername())) {
                boolean admin = WaveAttack.getInstance().getClientUsername().equals(playerAddEvent.admin);

                GameScreen.getInstance().setPlayer(newPlayer, admin);
                PlayerHandler.INSTANCE.addPlayer(newPlayer);
            } else {
                PlayerHandler.INSTANCE.addPlayer(newPlayer);
            }
        }

        if (object instanceof StartGameEvent) {
            WaveAttack.getInstance().setScreen(GameScreen.getInstance());
        }

        super.received(connection, object);
    }

}
