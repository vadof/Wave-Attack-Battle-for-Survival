package com.wabs.server.gameplay;

import com.badlogic.gdx.math.Vector3;
import com.wabs.global.enemyEvents.EnemyAddEvent;
import com.wabs.global.enemyEvents.EnemyType;
import com.wabs.server.supers.ServerEnemy;

import java.util.List;

public class Wave implements Runnable {

    private final Integer wave;
    private final EnemyType enemyTypes;
    private final Integer enemyHp;
    private final float enemySpeed;
    private final Integer enemyDamage;
    private final Integer damageFrequency;
    private final Integer enemyAmount;
    private final List<Vector3> spawnPositions;
    private final int frequencyOfOccurrence;
    private GameLobby gameLobby;

    private float attackRange;

    private int spawnPosId;

    public Wave(Integer wave, EnemyType enemyTypes, Integer enemyHp, float enemySpeed, Integer enemyDamage,
                Integer damageFrequency, Integer enemyAmount, List<Vector3> spawnPositions, int frequencyOfOccurrence, float attackRange) {
        this.wave = wave;
        this.enemyTypes = enemyTypes;
        this.enemyHp = enemyHp;
        this.enemySpeed = enemySpeed;
        this.enemyDamage = enemyDamage;
        this.damageFrequency = damageFrequency;
        this.enemyAmount = enemyAmount;
        this.frequencyOfOccurrence = frequencyOfOccurrence;
        this.attackRange = attackRange;


        this.spawnPositions = spawnPositions;
        spawnPosId = 0;
    }

    public void startWave(GameLobby gameLobby) {
        Thread thread = new Thread(this);
        thread.start();
        this.gameLobby = gameLobby;
    }

    @Override
    public void run() {
        gameLobby.setAllEnemySpawned(false);
        float sensorScale = GameplayHandler.INSTANCE.getEnemySensorScale(enemyTypes);

        for (int i = 0; i < enemyAmount; i++) {
            try {
                if (i == 0)
                    Thread.sleep(7000);
                else
                    Thread.sleep(frequencyOfOccurrence);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            gameLobby.addEnemy(new ServerEnemy(enemyTypes, enemyHp, enemySpeed, enemyDamage, damageFrequency, i, gameLobby.getLobbyId(), sensorScale, attackRange));

            EnemyAddEvent enemyAddEvent = new EnemyAddEvent();
            enemyAddEvent.enemyType = enemyTypes;
            enemyAddEvent.enemyHp = enemyHp;
            enemyAddEvent.enemySpeed = enemySpeed;
            enemyAddEvent.enemyDamage = enemyDamage;
            enemyAddEvent.enemyId = i;
            enemyAddEvent.spawnPos = getSpawnPos();
            enemyAddEvent.sensorScale = sensorScale;

            gameLobby.sendToAllTCP(enemyAddEvent);
        }
        gameLobby.setAllEnemySpawned(true);
    }

    private Vector3 getSpawnPos() {
        if (spawnPosId < spawnPositions.size()) {
            return spawnPositions.get(spawnPosId++);
        } else {
            spawnPosId = 0;
            return getSpawnPos();
        }
    }

    public Integer getWave() {
        return wave;
    }

    public EnemyType getEnemyTypes() {
        return enemyTypes;
    }

    public Integer getEnemyHp() {
        return enemyHp;
    }

    public float getEnemySpeed() {
        return enemySpeed;
    }

    public Integer getEnemyDamage() {
        return enemyDamage;
    }

    public Integer getDamageFrequency() {
        return damageFrequency;
    }

    public Integer getEnemyAmount() {
        return enemyAmount;
    }

    public List<Vector3> getSpawnPositions() {
        return spawnPositions;
    }

    public int getFrequencyOfOccurrence() {
        return frequencyOfOccurrence;
    }

    public float getAttackRange() {
        return attackRange;
    }
}
