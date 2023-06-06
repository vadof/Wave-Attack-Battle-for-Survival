package com.wabs.server.supers;

import com.badlogic.gdx.math.Matrix4;
import com.wabs.global.enemyEvents.EnemyType;

public class ServerEnemy {

    private final Integer enemyId;
    private final Long lobbyId;
    private final EnemyType enemyType;
    private Integer hp;
    private final float speed;
    private final Integer damage;
    private final Integer damageFrequency;
    private Integer damageTimer;
    private boolean dead;

    private boolean needStrafeLeft;
    private boolean needStrafeCenter;
    private boolean needStrafeRight;
    private float sensorsScale;
    private float attackRange;

    private Matrix4 enemyTransform = new Matrix4();

    public ServerEnemy(EnemyType enemyType, Integer hp, float speed, Integer damage,
                       Integer damageFrequency, Integer enemyId, Long lobbyId, float sensorsScale, float attackRange) {
        this.enemyType = enemyType;
        this.hp = hp;
        this.speed = speed;
        this.damage = damage;
        this.damageFrequency = damageFrequency;
        this.enemyId = enemyId;
        this.lobbyId = lobbyId;

        this.sensorsScale = sensorsScale;
        this.attackRange = attackRange;

        dead = false;
        damageTimer = 0;
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
        if (hp <= 0) dead = true;
    }

    public boolean canDealDamage() {
        return damageTimer == 0;
    }

    public void setDamageTimer() {
        damageTimer = damageFrequency;
    }

    public void refreshDamageTimer() {
        if (damageTimer > 0) damageTimer--;
    }

    public boolean isDead() {
        return dead;
    }

    public Matrix4 getEnemyTransform() {
        return enemyTransform;
    }

    public void setEnemyTransform(Matrix4 enemyTransform) {
        this.enemyTransform = enemyTransform;
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }

    public Integer getEnemyId() {
        return enemyId;
    }

    public Integer getHp() {
        return hp;
    }

    public float getSpeed() {
        return speed;
    }

    public Integer getDamage() {
        return damage;
    }

    public boolean isNeedStrafeLeft() {
        return needStrafeLeft;
    }

    public void setNeedStrafeLeft(boolean needStrafeLeft) {
        this.needStrafeLeft = needStrafeLeft;
    }

    public boolean isNeedStrafeCenter() {
        return needStrafeCenter;
    }

    public void setNeedStrafeCenter(boolean needStrafeCenter) {
        this.needStrafeCenter = needStrafeCenter;
    }

    public boolean isNeedStrafeRight() {
        return needStrafeRight;
    }

    public void setNeedStrafeRight(boolean needStrafeRight) {
        this.needStrafeRight = needStrafeRight;
    }

    public float getSensorsScale() {
        return sensorsScale;
    }

    public float getAttackRange() {
        return attackRange;
    }
}
