package com.wabs.game.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.game.handlers.EnemyHandler;
import com.wabs.game.screens.GameScreen;
import com.wabs.game.supers.Enemy;
import com.wabs.global.enemyEvents.EnemyAddEvent;
import com.wabs.global.enemyEvents.EnemyDeadEvent;
import com.wabs.global.enemyEvents.EnemyMoveEvent;

public class EnemyListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof EnemyAddEvent) {
            EnemyAddEvent enemyAddEvent = (EnemyAddEvent) object;

            Enemy enemy = new Enemy(enemyAddEvent.enemyType, enemyAddEvent.enemyHp, enemyAddEvent.enemySpeed,
                    enemyAddEvent.enemyDamage, enemyAddEvent.enemyId, enemyAddEvent.spawnPos, enemyAddEvent.sensorScale);

            EnemyHandler.INSTANCE.addEnemy(enemy);
            GameScreen.getInstance().getPlayer().addEnemyToGame(enemy);

        } else if (object instanceof EnemyDeadEvent) {
            EnemyDeadEvent enemyDeadEvent = (EnemyDeadEvent) object;
            Enemy enemy = EnemyHandler.INSTANCE.getEnemyById(enemyDeadEvent.id);

            if (enemy != null) {
                EnemyHandler.INSTANCE.removeEnemy(enemy);
                GameScreen.getInstance().getPlayer().removeEnemyFromGame(enemy);

                String enemyRemainsText = GameScreen.getInstance().getEnemyRemainsLabelText();
                int remains = Integer.parseInt(enemyRemainsText.substring(0, 1));
                GameScreen.getInstance().setTextToEnemyRemainsLabel(--remains + enemyRemainsText.substring(1));
            }

        } else if (object instanceof EnemyMoveEvent) {
            EnemyMoveEvent enemyMoveEvent = (EnemyMoveEvent) object;
            Enemy enemy = EnemyHandler.INSTANCE.getEnemyById(enemyMoveEvent.enemyId);

            if (enemy != null) {
                if (enemyMoveEvent.admin) {
                    enemy.setLinearVelocity(enemyMoveEvent.linearVelocity);
                    enemy.setAngularVelocity(enemyMoveEvent.angularVelocity);
                    enemy.setStrafe(enemyMoveEvent.strafe);
                    enemy.update();
                } else {
                    enemy.updateEnemyTransform(enemyMoveEvent.transform);
                }
            }

        }

        super.received(connection, object);
    }
}
