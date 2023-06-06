package com.wabs.global.playerEvents;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class PlayerAddEvent {

    public String username;
    public Integer playerId;
    public Matrix4 playerTransform;
    public Vector3 spawnPosition;
    public String admin;
    public Long lobbyId;

    public PlayerAddEvent() {

    }

}
