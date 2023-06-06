package com.wabs.global.playerEvents;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class MoveUpdateEvent {

    public Matrix4 playerTransform = new Matrix4();
    public Integer playerId;
    public Long lobbyId;

    public MoveUpdateEvent() {

    }

}
