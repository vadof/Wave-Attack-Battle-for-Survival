package com.wabs.server.gameplay;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.wabs.global.enemyEvents.EnemyMoveEvent;
import com.wabs.global.gameEvents.GameEndEvent;
import com.wabs.global.gameEvents.StartGameEvent;
import com.wabs.global.gameEvents.WaveClearedEvent;
import com.wabs.global.gameEvents.WaveInfoEvent;
import com.wabs.global.lobbyEvents.AdminChangeEvent;
import com.wabs.global.playerEvents.PlayerAddEvent;
import com.wabs.global.playerEvents.PlayerDeadEvent;
import com.wabs.global.playerEvents.PlayerRemoveEvent;
import com.wabs.global.playerEvents.PlayerTakeDamageEvent;
import com.wabs.server.database.HibernateUtil;
import com.wabs.server.gameplay.GameplayHandler;
import com.wabs.server.gameplay.Wave;
import com.wabs.server.handlers.GameLobbyHandler;
import com.wabs.server.handlers.UserHandler;
import com.wabs.server.supers.ServerEnemy;
import com.wabs.server.supers.ServerPlayer;

import java.util.*;

public class GameLobby {

    private Long lobbyId;
    private Map<Integer, ServerPlayer> players = new HashMap<>();
    private List<ServerPlayer> playerList = new ArrayList<>();

    private Vector3 zeroVector = new Vector3(Vector3.Zero);
    private EnemyMoveEvent enemyMoveEvent = new EnemyMoveEvent();
    private PlayerTakeDamageEvent playerTakeDamageEvent = new PlayerTakeDamageEvent();
    private PlayerDeadEvent playerDeadEvent = new PlayerDeadEvent();

    private String admin;
    private ServerPlayer adminPlayer;

    private Wave currentWave;
    private int currentWaveId = 0;
    private List<ServerEnemy> enemyList = new ArrayList<>();
    private boolean allEnemySpawned;

    public GameLobby(Long lobbyId, List<String> usernames, String admin) {
        this.lobbyId = lobbyId;
        this.admin = admin;
        System.out.println("new lobby id - " + lobbyId);

        setPlayers(usernames);
    }

    private void setPlayers(List<String> usernames) {
        for (int i = 0; i < usernames.size(); i++) {
            ServerPlayer serverPlayer = new ServerPlayer(usernames.get(i),
                    UserHandler.getInstance().getConnectionByUsername(usernames.get(i)),
                    i, lobbyId);

            if (usernames.get(i).equals(admin)) {
                adminPlayer = serverPlayer;
            }

            players.put(i, serverPlayer);
            playerList.add(serverPlayer);
        }
    }

    public ServerPlayer getPlayerById(Integer id) {
        return players.get(id);
    }

    public ServerPlayer getPlayerByUsername(String username) {
        for (ServerPlayer serverPlayer : players.values()) {
            if (serverPlayer.getUsername().equals(username)) {
                return serverPlayer;
            }
        }
        return null;
    }

    public List<ServerPlayer> getPlayers() {
        return playerList;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void startGame() {
        PlayerAddEvent playerAddEvent = new PlayerAddEvent();
        playerAddEvent.admin = admin;
        playerAddEvent.lobbyId = lobbyId;

        for (ServerPlayer serverPlayer : getPlayers()) {
            UserHandler.getInstance().getUserByUsername(serverPlayer.getUsername()).setCurrentlyPlaying(true);

            playerAddEvent.username = serverPlayer.getUsername();
            playerAddEvent.playerId = serverPlayer.getPlayerId();
            playerAddEvent.playerTransform = serverPlayer.getPlayerTransform();
            playerAddEvent.spawnPosition = getPlayerSpawnPosition(serverPlayer.getPlayerId());

            for (ServerPlayer serverPlayer1 : getPlayers()) {
                serverPlayer1.getConnection().sendTCP(playerAddEvent);
            }
        }

        for (ServerPlayer serverPlayer : getPlayers()) {
            serverPlayer.getConnection().sendTCP(new StartGameEvent());
        }

        startNextWave();
    }

    private void startNextWave() {
        if (!allWavesCleared()) {
            currentWave = GameplayHandler.INSTANCE.getNewWave(++currentWaveId);
            currentWave.startWave(this);
            System.out.println("Wave started");

            WaveInfoEvent waveInfoEvent = new WaveInfoEvent();
            waveInfoEvent.wave = currentWave.getWave();
            waveInfoEvent.enemyAmount = currentWave.getEnemyAmount();
            sendToAllTCP(waveInfoEvent);
        }
    }

    public Vector3 getPlayerSpawnPosition(int playerId) {
        switch (playerId) {
            case 0:
                return new Vector3(-3.84f,5.5f,29.23f);
            case 1:
                return new Vector3(-5.1f,5.5f,19.75f);
            case 2:
                return new Vector3(-7.1f,5.5f,8.8f);
        }
        return new Vector3();
    }

    public void removePlayer(ServerPlayer player) {
        players.remove(player.getPlayerId());
        playerList.remove(player);

        PlayerRemoveEvent playerRemoveEvent = new PlayerRemoveEvent();
        playerRemoveEvent.id = player.getPlayerId();

        sendToAllTCP(playerRemoveEvent);

        if (player.getUsername().equals(admin) && players.size() > 0) {
            AdminChangeEvent adminChangeEvent = new AdminChangeEvent();
            adminChangeEvent.lobbyId = lobbyId;

            adminPlayer = playerList.get(0);
            adminChangeEvent.newAdmin = adminPlayer.getUsername();
            adminChangeEvent.inGame = true;

            sendToAllTCP(adminChangeEvent);
        }
    }

    public void sendToAllTCP(Object object) {
        for (ServerPlayer serverPlayer : getPlayers()) {
            serverPlayer.getConnection().sendTCP(object);
        }
    }

    public void sendToAllUDP(Object object) {
        for (ServerPlayer serverPlayer : getPlayers()) {
            serverPlayer.getConnection().sendUDP(object);
        }
    }

    public void addEnemy(ServerEnemy serverEnemy) {
        this.enemyList.add(serverEnemy);
    }

    public void removeEnemy(ServerEnemy serverEnemy) {
        this.enemyList.remove(serverEnemy);
        if (waveCleared()) {
            if (allWavesCleared()) {
                endGame(true);
            } else {
                sendWaveClearedEvent();
                startNextWave();
            }
        }
    }

    public ServerEnemy getServerEnemy(int id) {
        for (ServerEnemy serverEnemy : enemyList) {
            if (serverEnemy.getEnemyId() == id) {
                return serverEnemy;
            }
        }
        return null;
    }

    public boolean waveCleared() {
        return enemyList.size() == 0 && allEnemySpawned;
    }

    public boolean allPlayerAreDead() {
        for (ServerPlayer serverPlayer : getPlayers()) {
            if (!serverPlayer.isDead()) {
                return false;
            }
        }
        return true;
    }

    public void sendWaveClearedEvent() {
        int award = GameplayHandler.INSTANCE.getAwardForWave(currentWaveId);

        for (ServerPlayer serverPlayer : getPlayers()) {
            serverPlayer.addMoney(award);
            serverPlayer.refreshHp();
            serverPlayer.setAlive();
        }

        WaveClearedEvent waveClearedEvent = new WaveClearedEvent();
        waveClearedEvent.waveId = currentWaveId;
        waveClearedEvent.money = award;

        sendToAllTCP(waveClearedEvent);
    }

    public void endGame(Boolean win) {
        if (!win) enemyList.clear();

        GameEndEvent gameEndEvent = new GameEndEvent();

        List<String> playerUsernames = new ArrayList<>();
        List<Integer> playersDamage = new ArrayList<>();
        List<Integer> playersDamageReceived = new ArrayList<>();
        List<Integer> playersDeath = new ArrayList<>();
        List<Integer> playersKills = new ArrayList<>();

        for (ServerPlayer player : getPlayers()) {
            playerUsernames.add(player.getUsername());
            playersDamage.add(player.getDamageDealt());
            playersDamageReceived.add(player.getDamageReceived());
            playersDeath.add(player.getDeaths());
            playersKills.add(player.getKills());
        }

        gameEndEvent.win = win;
        gameEndEvent.playerUsernames = playerUsernames;
        gameEndEvent.playersDamage = playersDamage;
        gameEndEvent.playersDamageReceived = playersDamageReceived;
        gameEndEvent.playersDeath = playersDeath;
        gameEndEvent.playersKills = playersKills;

        sendToAllTCP(gameEndEvent);
        GameLobbyHandler.getInstance().removeGameLobby(this);

        HibernateUtil.saveStatistics(gameEndEvent);
    }

    public void setAllEnemySpawned(boolean spawned) {
        this.allEnemySpawned = spawned;
    }

    public List<ServerEnemy> getEnemyList() {
        return enemyList;
    }

    public boolean allWavesCleared() {
        return currentWaveId == 6;
    }

    private void sendUDPToAdmin(EnemyMoveEvent enemyMoveEvent) {
        enemyMoveEvent.admin = true;
        adminPlayer.getConnection().sendUDP(enemyMoveEvent);
    }

    public void sendUDPToOthers(EnemyMoveEvent enemyMoveEvent) {
        for (ServerPlayer serverPlayer : playerList) {
            if (serverPlayer != adminPlayer) {
                serverPlayer.getConnection().sendUDP(enemyMoveEvent);
            }
        }
    }

    public void updateEnemies() {
        for (ServerEnemy serverEnemy : getEnemyList()) {
            serverEnemy.refreshDamageTimer();

            Vector3 enemyPos = serverEnemy.getEnemyTransform().getTranslation(new Vector3());
            ServerPlayer followPlayer = null;
            float minDistance = Float.MAX_VALUE;

            for (ServerPlayer serverPlayer : getPlayers()) {
                if (serverPlayer.isDead()) continue;

                Vector3 playerPos = serverPlayer.getPlayerTransform().getTranslation(new Vector3());

                float distance = enemyPos.dst(playerPos);
                if (distance < minDistance) {
                    minDistance = distance;
                    followPlayer = serverPlayer;
                }
            }

            if (followPlayer != null) {
                // Deal damage
                if (minDistance < serverEnemy.getAttackRange() && serverEnemy.canDealDamage()) {
                    followPlayer.takeDamage(serverEnemy.getDamage());
                    serverEnemy.setDamageTimer();

                    playerTakeDamageEvent.playerId = followPlayer.getPlayerId();
                    playerTakeDamageEvent.damage = serverEnemy.getDamage();

                    if (followPlayer.isDead()) {
                        playerDeadEvent.id = followPlayer.getPlayerId();
                        sendToAllTCP(playerDeadEvent);

                        if (allPlayerAreDead()) {
                            endGame(false);
                        }
                    } else {
                        sendToAllTCP(playerTakeDamageEvent);
                    }
                }

                Vector3 direction = followPlayer.getPlayerTransform()
                        .getTranslation(new Vector3())
                        .sub(serverEnemy.getEnemyTransform().getTranslation(new Vector3()));
                direction.nor();

                // Enemy turn towards the player START
                Vector3 enemyFacingDir = serverEnemy.getEnemyTransform().getRotation(new Quaternion()).transform(new Vector3(0, 0, -1)).nor();
                float dotProduct = enemyFacingDir.dot(direction);
                float angle = MathUtils.radiansToDegrees * MathUtils.acos(dotProduct);

                if (180 - angle > 20) {
                    Vector3 crossProduct = new Vector3();
                    crossProduct.set(enemyFacingDir).crs(direction);

                    float turnDirection = -Math.signum(crossProduct.y);

                    Vector3 angularVelocity = new Vector3(0, turnDirection, 0).scl(serverEnemy.getSpeed() * 3.5f);

                    enemyMoveEvent.enemyId = serverEnemy.getEnemyId();
                    enemyMoveEvent.linearVelocity = zeroVector;
                    enemyMoveEvent.angularVelocity = angularVelocity;
                    // Enemy turn towards the player END
                } else {
                    Vector3 linearVelocity = direction.scl(serverEnemy.getSpeed());
                    linearVelocity.y = 0;

                    enemyMoveEvent.enemyId = serverEnemy.getEnemyId();
                    enemyMoveEvent.linearVelocity = linearVelocity;
                    enemyMoveEvent.angularVelocity = zeroVector;
                }

                // To avoid obstacles
                Vector3 strafe = new Vector3();
                if (serverEnemy.isNeedStrafeLeft()) {
                    strafe.set(direction.z, 0, -direction.x).scl(-1);
                } else if (serverEnemy.isNeedStrafeRight()) {
                    strafe.set(direction.z, 0, -direction.x);
                } else if (serverEnemy.isNeedStrafeCenter()) {
                    strafe.set(direction.z, 0, -direction.x);
                }
                enemyMoveEvent.strafe = strafe;

//                sendToAllUDP(enemyMoveEvent);
                sendUDPToAdmin(enemyMoveEvent);
            }
        }
    }
}
