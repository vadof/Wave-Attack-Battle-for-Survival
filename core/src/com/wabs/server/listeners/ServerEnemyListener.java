package com.wabs.server.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.global.enemyEvents.EnemyDeadEvent;
import com.wabs.global.enemyEvents.EnemyMoveEvent;
import com.wabs.global.enemyEvents.EnemyTakeDamageEvent;
import com.wabs.server.gameplay.GameLobby;
import com.wabs.server.handlers.GameLobbyHandler;
import com.wabs.server.supers.ServerEnemy;
import com.wabs.server.supers.ServerPlayer;

public class ServerEnemyListener extends Listener {

    private GameLobbyHandler gameLobbyHandler = GameLobbyHandler.getInstance();

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof EnemyTakeDamageEvent) {
            EnemyTakeDamageEvent enemyTakeDamageEvent = (EnemyTakeDamageEvent) object;

            GameLobby gameLobby = gameLobbyHandler.getGameLobbyById(enemyTakeDamageEvent.lobbyId);
            ServerEnemy enemy = gameLobby.getServerEnemy(enemyTakeDamageEvent.enemyId);

            if (enemy != null) {
                ServerPlayer player = gameLobby.getPlayerById(enemyTakeDamageEvent.playerId);

                enemy.takeDamage(enemyTakeDamageEvent.damage);
                player.addDamageDealt(enemyTakeDamageEvent.damage);

                if (enemy.isDead()) {
                    player.addKill();

                    EnemyDeadEvent enemyDeadEvent = new EnemyDeadEvent();
                    enemyDeadEvent.id = enemy.getEnemyId();
                    gameLobby.sendToAllTCP(enemyDeadEvent);

                    gameLobby.removeEnemy(enemy);
                }
            }
        } else if (object instanceof EnemyMoveEvent) {
            EnemyMoveEvent enemyMoveEvent = (EnemyMoveEvent) object;

            GameLobby gameLobby = gameLobbyHandler.getGameLobbyById(enemyMoveEvent.lobbyId);
            if (gameLobby != null) {
                ServerEnemy enemy = gameLobby.getServerEnemy(enemyMoveEvent.enemyId);
                if (enemy != null) {
                    enemyMoveEvent.admin = false;
                    gameLobby.sendUDPToOthers(enemyMoveEvent);

                    enemy.setEnemyTransform(enemyMoveEvent.transform);
                    enemy.setNeedStrafeLeft(enemyMoveEvent.needStrafeLeft);
                    enemy.setNeedStrafeCenter(enemyMoveEvent.needStrafeCenter);
                    enemy.setNeedStrafeRight(enemyMoveEvent.needStrafeRight);
                }
            }
        }

        super.received(connection, object);
    }

}
