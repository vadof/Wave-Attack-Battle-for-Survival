package com.wabs.global.enemyEvents;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class EnemyMoveEvent {

    public Integer enemyId;
    public Long lobbyId;
    public Vector3 linearVelocity;
    public Vector3 angularVelocity;
    public Matrix4 transform;
    public Vector3 strafe;
    public boolean needStrafeLeft;
    public boolean needStrafeCenter;
    public boolean needStrafeRight;

    public boolean admin;

    public EnemyMoveEvent() {

    }
}
