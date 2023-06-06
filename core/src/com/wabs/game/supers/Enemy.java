package com.wabs.game.supers;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.wabs.game.WaveAttack;
import com.wabs.game.bullet.BulletPhysicsSystem;
import com.wabs.game.bullet.MotionState;
import com.wabs.global.enemyEvents.EnemyType;
import net.mgsx.gltf.scene3d.scene.Scene;

public class Enemy {

    protected final EnemyType enemyType;
    protected Integer hp;
    protected float speed;
    protected Integer damage;
    protected boolean dead;

    protected btRigidBody enemyBody;
    protected Scene enemyScene;
    protected Integer id;
    protected Vector3 spawnPos;

    protected Vector3 linearVelocity = new Vector3();
    protected Vector3 angularVelocity = new Vector3();
    protected Vector3 strafe = new Vector3();

    protected ClosestNotMeRayResultCallback callback;
    private float sensorsScale;

    public Enemy(EnemyType enemyType, Integer hp, float speed, Integer damage, Integer id, Vector3 spawnPos, float sensorsScale) {
        this.enemyType = enemyType;
        this.hp = hp;
        this.speed = speed;
        this.damage = damage;
        this.id = id;
        this.spawnPos = spawnPos;

        this.sensorsScale = sensorsScale;

        this.enemyScene = WaveAttack.getInstance().enemyTypeMap.get(enemyType).get(id);
        enemyBody = createEnemyBody();
        this.dead = false;

        callback = new ClosestNotMeRayResultCallback(enemyBody);
    }

    private btRigidBody createEnemyBody() {
        ModelInstance enemyModelInstance = enemyScene.modelInstance;

        spawnPos.y += 3f;
        enemyModelInstance.transform.setToTranslation(spawnPos);


        BoundingBox boundingBox = new BoundingBox();
        enemyModelInstance.calculateBoundingBox(boundingBox);

        btBoxShape boxShape = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(0.5f));

        MotionState motionState = new MotionState();
        motionState.transform = enemyModelInstance.transform;

        Vector3 inertia = new Vector3();

        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(1f, motionState, boxShape, inertia);
        btRigidBody body = new btRigidBody(info);

        body.setAngularFactor(Vector3.Y);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);

        body.setDamping(0.75f, 0.99f);

        BulletPhysicsSystem.INSTANCE.addBody(body);

        return body;
    }

    public void update() {
        if (!angularVelocity.isZero()) {
            enemyBody.setAngularVelocity(angularVelocity);
        }

        if (!strafe.isZero()) {
            enemyBody.applyCentralImpulse(strafe);
        }

        if (!linearVelocity.isZero()) {
            enemyBody.applyCentralImpulse(linearVelocity);
        }
    }

    public boolean isOnGround() {
        callback.setClosestHitFraction(1.0f);
        callback.setCollisionObject(null);

        Vector3 position = enemyBody.getWorldTransform().getTranslation(new Vector3());

        Vector3 tmpPosition = new Vector3().set(position).sub(0, 3f, 0);

        BulletPhysicsSystem.INSTANCE.raycast(position, tmpPosition, callback);

        return callback.hasHit();
    }

    public void setLinearVelocity(Vector3 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public void setAngularVelocity(Vector3 angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public void setStrafe(Vector3 strafe) {
        this.strafe = strafe;
    }

    public EnemyType getEnemyType() {
        return enemyType;
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

    public btRigidBody getEnemyBody() {
        return enemyBody;
    }

    public Scene getEnemyScene() {
        return enemyScene;
    }

    public Integer getId() {
        return id;
    }

    public float getSensorsScale() {
        return sensorsScale;
    }

    public void updateEnemyTransform(Matrix4 transform) {
        enemyBody.setWorldTransform(transform);
    }
}
