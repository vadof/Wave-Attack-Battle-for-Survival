package com.wabs.game.handlers;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.wabs.game.WaveAttack;
import com.wabs.game.bullet.BulletPhysicsSystem;
import com.wabs.game.screens.GameScreen;
import com.wabs.game.supers.Enemy;
import com.wabs.global.enemyEvents.EnemyMoveEvent;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class EnemyHandler {

    private final ClosestRayResultCallback callback = new ClosestRayResultCallback(new Vector3(), new Vector3());
    public static EnemyHandler INSTANCE = new EnemyHandler();
    private final List<Enemy> enemyList = new ArrayList<>();
    private final EnemyMoveEvent enemyMoveEvent = new EnemyMoveEvent();

    public void addEnemy(Enemy enemy) {
        this.enemyList.add(enemy);
    }

    public void removeEnemy(Enemy enemy) {
        this.enemyList.remove(enemy);
    }

    public Enemy getEnemyByBody(btRigidBody body) {
        for (Enemy enemy : enemyList) {
           if (enemy.getEnemyBody().equals(body)) {
               return enemy;
           }
        }
        return null;
    }

    public Enemy getEnemyById(int id) {
        for (Enemy enemy : enemyList) {
            if (enemy.getId() == id) {
                return enemy;
            }
        }
        return null;
    }

    public List<Enemy> getEnemyList() {
        return enemyList;
    }

    public void clearEnemyList() {
        enemyList.clear();
    }

    public void sendEnemyPositionEvent() {
        try {
            enemyMoveEvent.lobbyId = GameScreen.getInstance().getPlayer().getLobbyId();
            for (Enemy enemy : enemyList) {
                float sensorScale = enemy.getSensorsScale();
                enemyMoveEvent.enemyId = enemy.getId();
                enemyMoveEvent.transform = enemy.getEnemyBody().getWorldTransform();

                enemyMoveEvent.needStrafeLeft = needStrafe(enemy, "L", sensorScale);
                enemyMoveEvent.needStrafeCenter = needStrafe(enemy, "C", sensorScale);
                enemyMoveEvent.needStrafeRight = needStrafe(enemy, "R", sensorScale);

                WaveAttack.getInstance().getClient().sendUDP(enemyMoveEvent);
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("Error in class " + this.getClass());
        }
    }

    private boolean needStrafe(Enemy enemy, String direction, float sensorScale) {
        callback.setClosestHitFraction(1.0f);
        callback.setCollisionObject(null);

        Vector3 position = enemy.getEnemyBody().getWorldTransform().getTranslation(new Vector3());

        Vector3 strafeDirection;
        if (direction.equals("L")) {
            strafeDirection = new Vector3(1, sensorScale, 1);
        } else if (direction.equals("R")) {
            strafeDirection = new Vector3(-1, sensorScale, 1);
        } else {
            strafeDirection = new Vector3(0, sensorScale, 1);
        }

        Quaternion rotation = enemy.getEnemyBody().getWorldTransform().getRotation(new Quaternion());
        rotation.transform(strafeDirection);

        Vector3 endPosition = new Vector3().set(position).add(strafeDirection.scl(2f));

        return BulletPhysicsSystem.INSTANCE.enemyRaycast(position, endPosition, callback);
    }



}
