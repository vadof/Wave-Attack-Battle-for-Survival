package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.WaveAttack;
import com.wabs.game.handlers.PlayerHandler;
import com.wabs.game.screens.GameScreen;
import com.wabs.game.settings.SoundSettings;
import com.wabs.game.supers.Player;
import com.wabs.global.gameEvents.WaveClearedEvent;
import com.wabs.global.gameEvents.WaveInfoEvent;

public class WaveListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof WaveClearedEvent) {
            WaveClearedEvent waveClearedEvent = (WaveClearedEvent) object;
            WaveAttack.waveClearedSound.play(SoundSettings.volume * 0.1f);
            for (Player player : PlayerHandler.INSTANCE.getPlayers()) {
                player.addMoney(waveClearedEvent.money);
                GameScreen.getInstance().setWaveClearedLabel("Wave " + waveClearedEvent.waveId + " cleared!");
                if (player.isDead()) {
                    player.setAlive();
                } else {
                    player.refreshHp();
                }
            }
        } else if (object instanceof WaveInfoEvent) {
            WaveInfoEvent waveInfoEvent = (WaveInfoEvent) object;

            GameScreen.getInstance().setTextToCurrentWaveLabel("Wave - " + waveInfoEvent.wave + "/6   ");
            GameScreen.getInstance().setTextToEnemyRemainsLabel(waveInfoEvent.enemyAmount + " enemy remains");
        }

        super.received(connection, object);
    }

}
