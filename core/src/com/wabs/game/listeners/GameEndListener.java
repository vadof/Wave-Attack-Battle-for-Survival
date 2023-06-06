package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.WaveAttack;
import com.wabs.game.bullet.BulletRemovalSystem;
import com.wabs.game.handlers.EnemyHandler;
import com.wabs.game.screens.GameScreen;
import com.wabs.game.settings.SoundSettings;
import com.wabs.game.windows.GameEndWindow;
import com.wabs.global.gameEvents.GameEndEvent;

public class GameEndListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof GameEndEvent) {
            EnemyHandler.INSTANCE.clearEnemyList();
            BulletRemovalSystem.INSTANCE.run = false;

            GameEndEvent gameEndEvent = (GameEndEvent) object;

            if (gameEndEvent.win) {
                WaveAttack.victorySound.play(SoundSettings.volume);
            } else {
                WaveAttack.defeatSound.play(SoundSettings.volume);
            }

            String title = gameEndEvent.win ? "Victory" : "Defeat";
            GameEndWindow gameEndWindow = new GameEndWindow(title, WaveAttack.textureSkin,
                    gameEndEvent.playerUsernames, gameEndEvent.playersDamage, gameEndEvent.playersDamageReceived,
                    gameEndEvent.playersDeath, gameEndEvent.playersKills, gameEndEvent.win);

            GameScreen.getInstance().setGameEndWindow(gameEndWindow);
        }

        super.received(connection, object);
    }

}
