package com.wabs.server.gameplay;

import com.badlogic.gdx.math.Vector3;
import com.wabs.global.enemyEvents.EnemyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameplayHandler {

    public static GameplayHandler INSTANCE = new GameplayHandler();
    List<Vector3> spawnPositions = new ArrayList<>();

    public static boolean game = false;

    private final List<Wave> wavesInfo = new ArrayList<>();
    HashMap<EnemyType, Float> sensorsScale = new HashMap<>();

    public GameplayHandler() {
        sensorsScale.put(EnemyType.TENTACLE, -0.5f);
        sensorsScale.put(EnemyType.KLERK, -0.4f);
        sensorsScale.put(EnemyType.NYX, 0.2f);
        sensorsScale.put(EnemyType.KRONG, -0.7f);
        sensorsScale.put(EnemyType.RACHNA, -0.9f);
        sensorsScale.put(EnemyType.SQUIRTEL, -0.3f);

        spawnPositions.add(new Vector3(-84.2f, 3.5f, 39.16f));
        spawnPositions.add(new Vector3(-86.3f, 3.5f,18.1f));
        spawnPositions.add(new Vector3(-88.3f, 3.5f, 2.1f));

        spawnPositions.add(new Vector3(-79.68f, 5f, -44.3f));
        spawnPositions.add(new Vector3(-85.17f, 7f, -17.3f));
        spawnPositions.add(new Vector3(29.2f, 0f, -45f));

        spawnPositions.add(new Vector3(-68.45f, 1f, -27f));
        spawnPositions.add(new Vector3(-5f, 5f, -45f));


        addNewWave(new Wave(1, EnemyType.KLERK, 100, 0.5f, 15, 100, 5, spawnPositions, 1000, 3f));
        addNewWave(new Wave(2, EnemyType.TENTACLE, 200, 0.6f, 25, 50, 4, spawnPositions, 1500, 4f));
        addNewWave(new Wave(3, EnemyType.KRONG, 400, 0.5f, 30, 150, 5, spawnPositions, 1500, 6f));
        addNewWave(new Wave(4, EnemyType.SQUIRTEL, 400, 0.4f, 30, 100, 6, spawnPositions, 1000, 5f));
        addNewWave(new Wave(5, EnemyType.NYX, 200, 0.8f, 20, 35, 8, spawnPositions, 500, 4f));
        addNewWave(new Wave(6, EnemyType.RACHNA, 1000, 0.4f, 50, 150, 3, spawnPositions, 5000, 9f));

//        addNewWave(new Wave(1, EnemyType.KLERK, 100, 0.3f, 15, 100, 1, spawnPositions, 1000, 3f));
//        addNewWave(new Wave(2, EnemyType.TENTACLE, 200, 0.3f, 25, 50, 1, spawnPositions, 1500, 4f));
//        addNewWave(new Wave(3, EnemyType.KRONG, 400, 0.2f, 30, 150, 1, spawnPositions, 1500, 6f));
//        addNewWave(new Wave(4, EnemyType.SQUIRTEL, 400, 0.25f, 30, 100, 1, spawnPositions, 1000, 5f));
//        addNewWave(new Wave(5, EnemyType.NYX, 200, 0.5f, 20, 30, 1, spawnPositions, 500, 4f));
//        addNewWave(new Wave(6, EnemyType.RACHNA, 1000, 0.2f, 50, 150, 1, spawnPositions, 5000, 9f));
    }

    private void addNewWave(Wave wave) {
        wavesInfo.add(wave);
    }

    public Wave getNewWave(int wave) {
        if (wave < 7) {
            Wave waveInfo = wavesInfo.get(wave - 1);
            return new Wave(waveInfo.getWave(), waveInfo.getEnemyTypes(),
                    waveInfo.getEnemyHp(), waveInfo.getEnemySpeed(),
                    waveInfo.getEnemyDamage(), waveInfo.getDamageFrequency(),
                    waveInfo.getEnemyAmount(), waveInfo.getSpawnPositions(),
                    waveInfo.getFrequencyOfOccurrence(), waveInfo.getAttackRange());
        }
        return null;
    }

    public int getAwardForWave(int wave) {
        return 100 * (wave);
    }

    public float getEnemySensorScale(EnemyType enemyType) {
        return sensorsScale.get(enemyType);
    }
}
