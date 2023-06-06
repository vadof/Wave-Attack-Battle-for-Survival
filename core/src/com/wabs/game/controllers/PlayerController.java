package com.wabs.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.wabs.game.bullet.BulletPhysicsSystem;
import com.wabs.game.settings.CameraSettings;
import com.wabs.game.WaveAttack;
import com.wabs.game.supers.Player;
import com.wabs.global.playerEvents.MoveUpdateEvent;

public class PlayerController {

    private final Player player;
    private final PerspectiveCamera camera;
    private final btRigidBody playerBody;


    // Player Movement
    private final float SPEED = 20f;
    private final float JUMP_FACTOR = 10f;

    private boolean CursorPitchBugFixed = false;
    private boolean CursorRotationBugFixed = false;

    private float camPitch = CameraSettings.CAMERA_START_PITCH;
    private float cameraAngleAroundPlayer = 270f;

    private final Vector3 currentDirection = new Vector3();
    private final Vector3 linearVelocity = new Vector3();
    private final Vector3 angularVelocity = new Vector3();
    private final Vector3 strafeDirection = new Vector3();
    private final Vector3 tmpVec = new Vector3();

    MoveUpdateEvent moveUpdateEvent = new MoveUpdateEvent();

    private final ClosestNotMeRayResultCallback callback;

    private final float cameraOffsetX = -1.5f;
    private final float cameraOffsetZ = -2f;
    private final float cameraOffsetY = 2.5f;

    public PlayerController(Player player, PerspectiveCamera camera) {
        this.player = player;
        this.playerBody = player.getPlayerBody();
        this.camera = camera;
        callback = new ClosestNotMeRayResultCallback(player.getPlayerBody());

        moveUpdateEvent.lobbyId = player.getLobbyId();
        moveUpdateEvent.playerId = player.getPlayerId();
    }

    public void render(float delta) {
        updateCamera();

        if (!player.isDead()) {
            movementInput(delta);
        }
    }

    private void movementInput(float delta) {
        tmpVec.set(Vector3.Z);
        currentDirection.set(tmpVec.rot(player.getTransform()).nor());

        angularVelocity.set(0,0,0);
        linearVelocity.set(0,0,0);
        strafeDirection.set(currentDirection.z, 0, -currentDirection.x);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            linearVelocity.set(currentDirection).scl(delta * SPEED);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            linearVelocity.set(currentDirection).scl(-delta * SPEED);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            linearVelocity.add(strafeDirection.scl(delta * SPEED));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            linearVelocity.add(strafeDirection.scl(-delta * SPEED));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && isOnGround()) {
            linearVelocity.y += JUMP_FACTOR;
        }

        float angleChange = getDeltaX();
        cameraAngleAroundPlayer += angleChange;
        angularVelocity.set(0, -angleChange, 0);
        stabilizeTheAngle();

        if (!linearVelocity.isZero()) {
            player.getPlayerBody().applyCentralImpulse(linearVelocity);
        }

        if (!angularVelocity.isZero()) {
            player.getPlayerBody().setAngularVelocity(angularVelocity);
        } else {
            player.getPlayerBody().setAngularVelocity(Vector3.Zero);
        }

        player.getCurrentWeaponScene().modelInstance.transform.set(playerBody.getWorldTransform());
        player.getCurrentWeaponScene().modelInstance.transform.translate(-1f, -2f, 0.5f);

        sendMoveUpdate();
    }

    public void sendMoveUpdate() {
        moveUpdateEvent.playerTransform = playerBody.getWorldTransform();

        WaveAttack.getInstance().getClient().sendTCP(moveUpdateEvent);
    }

    private boolean isOnGround() {
        callback.setClosestHitFraction(1.0f);
        callback.setCollisionObject(null);

        Vector3 position = new Vector3();
        player.getTransform().getTranslation(position);

        Vector3 tmpPosition = new Vector3().set(position).sub(0, 3f, 0);

        BulletPhysicsSystem.INSTANCE.raycast(position, tmpPosition, callback);

        return callback.hasHit();
    }

    private void updateCamera() {
        calculatePitch();
        camera.up.set(Vector3.Y);

        Vector3 cameraPos = player.getTransform().cpy().translate(cameraOffsetX, cameraOffsetY, cameraOffsetZ).getTranslation(new Vector3());
        camera.position.x = cameraPos.x;
        camera.position.y = cameraPos.y;
        camera.position.z = cameraPos.z;

        tmpVec.set(Vector3.Z);
        currentDirection.set(tmpVec.rot(player.getTransform()).nor());
        camera.lookAt(camera.position.x + currentDirection.x, cameraPos.y + camPitch, camera.position.z + currentDirection.z);

        camera.update();
    }

    private void calculatePitch() {
        camPitch -= getDeltaY() * 0.05f;
        if (camPitch < CameraSettings.CAMERA_MIN_PITCH) {
            camPitch = CameraSettings.CAMERA_MIN_PITCH;
        } else if (camPitch > CameraSettings.CAMERA_MAX_PITCH) {
            camPitch = CameraSettings.CAMERA_MAX_PITCH;
        }
    }

    private float getDeltaY() {
        int deltaY = Gdx.input.getDeltaY();
        if (!CursorPitchBugFixed) {
            if (deltaY > 50 || deltaY < -50) {
                deltaY = 0;
                CursorPitchBugFixed = true;
            }
        }
        return deltaY * CameraSettings.CAMERA_VERTICAL_SENSITIVITY;
    }

    private float getDeltaX() {
        int deltaX = Gdx.input.getDeltaX();
        if (!CursorRotationBugFixed) {
            if (deltaX < -50 || deltaX > 50) {
                deltaX = 0;
                CursorRotationBugFixed = true;
            }
        }
        return deltaX * CameraSettings.CAMERA_HORIZONTAL_SENSITIVITY;
    }

    private void stabilizeTheAngle() {
        while (cameraAngleAroundPlayer >= 360) {
            cameraAngleAroundPlayer -= 360;
        }

        while (cameraAngleAroundPlayer < 0.0) {
            cameraAngleAroundPlayer += 360;
        }
    }

    public void addCamPitch(float value) {
        camPitch += value;
    }
}
