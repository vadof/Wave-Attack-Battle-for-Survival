package com.wabs.server.supers;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Connection;
import com.wabs.global.weaponEvents.WeaponType;

public class ServerPlayer {

    private final String username;
    private final Integer playerId;
    private final Connection connection;
    private final Long lobbyId;

    private Integer money = 0;
    private WeaponType firstWeapon;
    private WeaponType secondWeapon;
    private WeaponType currentWeapon;

    private Vector3 linearVelocity = new Vector3();
    private Vector3 angularVelocity = new Vector3();
    private Matrix4 playerTransform = new Matrix4();

    private Integer hp;
    private boolean dead;

    // Statistics
    private int damageDealt = 0;
    private int deaths = 0;
    private int kills = 0;
    private int damageReceived = 0;

    public ServerPlayer(String username, Connection connection, Integer playerId, Long lobbyId) {
        this.username = username;
        this.connection = connection;
        this.playerId = playerId;
        this.lobbyId = lobbyId;
        this.hp = 100;
        dead = false;

        this.firstWeapon = WeaponType.STANDARD_PISTOL;
        this.currentWeapon = firstWeapon;
    }

    public Integer getMoney() {
        return money;
    }

    public boolean buyWeapon(WeaponType type, Integer slot, Integer price) {
        if (money >= price) {
            switch (slot) {
                case 1:
                    firstWeapon = type;
                    break;
                case 2:
                    secondWeapon = type;
                    break;
            }
            removeMoney(price);
            return true;
        }
        return false;
    }

    public void setCurrentWeapon(Integer slot) {
        switch (slot) {
            case 1:
                currentWeapon = firstWeapon;
                break;
            case 2:
                if (secondWeapon != null){
                    currentWeapon = secondWeapon;
                }
                break;
        }
    }

    public void addMoney(Integer amount) {
        this.money += amount;
    }

    public void removeMoney(Integer amount) {
        money -= amount;
    }

    public String getUsername() {
        return username;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public Connection getConnection() {
        return connection;
    }

    public Matrix4 getPlayerTransform() {
        return playerTransform;
    }

    public void setPlayerTransform(Matrix4 playerTransform) {
        this.playerTransform = playerTransform;
    }

    public void setAngularVelocity(Vector3 angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public void setLinearVelocity(Vector3 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public Vector3 getLinearVelocity() {
        return linearVelocity;
    }

    public Vector3 getAngularVelocity() {
        return angularVelocity;
    }

    public Integer getHp() {
        return hp;
    }

    public void refreshHp() {
        this.hp = 100;
    }

    public void setAlive() {
        dead = false;
    }

    public void takeDamage(Integer amount) {
        if (hp - amount <= 0) {
            damageReceived += hp;
            hp = 0;
            dead = true;
            deaths++;
        } else {
            hp -= amount;
            damageReceived += amount;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public void addDamageDealt(Integer damage) {
        this.damageDealt += damage;
    }

    public void addKill() {
        this.kills++;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getKills() {
        return kills;
    }

    public int getDamageReceived() {
        return damageReceived;
    }
}
