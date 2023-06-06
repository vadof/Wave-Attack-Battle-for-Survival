package com.wabs.game.bullet;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;

public class BulletPhysicsSystem implements Disposable {

    public static final Vector3 DEFAULT_GRAVITY = new Vector3(0, -9.81f, 0f);
    public static BulletPhysicsSystem INSTANCE = new BulletPhysicsSystem();

    private final btDynamicsWorld dynamicsWorld;
    private final btCollisionConfiguration collisionConfig;
    private final btDispatcher dispatcher;
    private final btBroadphaseInterface broadphase;
    private final btConstraintSolver constraintSolver;

    private final DebugDrawer debugDrawer;

    private final float fixedTimeStep = 1/60f;

    // Debug drawing ray casts
    private final Vector3 lastRayFrom = new Vector3();
    private final Vector3 lastRayTo = new Vector3();
    private final Vector3 rayColor = new Vector3(1, 0, 1);

    private final Vector3 enemyLastRayFrom = new Vector3();
    private final Vector3 enemyLastRayTo = new Vector3();


    public BulletPhysicsSystem() {
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);

        // General purpose, well optimized broadphase, adapts dynamically to the dimensions of the world.
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);

        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe);

        dynamicsWorld.setDebugDrawer(debugDrawer);
    }

    /**
     * Update physics world, should be called every frame
     * @param delta deltaTime since last frame
     */
    public void update(float delta) {
        // performs collision detection and physics simulation
        dynamicsWorld.stepSimulation(delta, 5, fixedTimeStep);
    }

    /**
     * Debug draw the physics world
     * @param camera camera to render to
     */
    public void render(Camera camera) {
        debugDrawer.begin(camera);
        debugDrawer.drawLine(lastRayFrom, lastRayTo, rayColor);
        dynamicsWorld.debugDrawWorld();
        debugDrawer.end();
    }

    public void addBody(btRigidBody body) {
        dynamicsWorld.addRigidBody(body);
    }

    public void removeBody(btRigidBody body) {
        dynamicsWorld.removeRigidBody(body);
        body.dispose();
    }

    /**
     * Perform a raycast in the physics world.
     * @param from the starting position (origin) of the ray
     * @param to the end position of the ray
     * @param callback the callback object to use
     */
    public void raycast(Vector3 from, Vector3 to, RayResultCallback callback) {
        lastRayFrom.set(from).sub(0, 5f, 0f);

        dynamicsWorld.rayTest(from, to, callback);

        if (callback.hasHit() && callback instanceof ClosestRayResultCallback) {
            // Use interpolation to determine the hitpoint where the ray hit the object
            // This is what bullet does behind the scenes as well
            lastRayTo.set(from);
            lastRayTo.lerp(to, callback.getClosestHitFraction());
        } else {
            lastRayTo.set(to);
        }
    }

    public boolean enemyRaycast(Vector3 from, Vector3 to, RayResultCallback callback) {
        enemyLastRayFrom.set(from).sub(0, 5f, 0f);

        dynamicsWorld.rayTest(from, to, callback);

        if (callback.hasHit() && callback instanceof ClosestRayResultCallback) {
            // Use interpolation to determine the hitpoint where the ray hit the object
            // This is what bullet does behind the scenes as well
            enemyLastRayFrom.set(from);
            enemyLastRayFrom.lerp(to, callback.getClosestHitFraction());
        } else {
            enemyLastRayFrom.set(to);
        }

        return callback.hasHit();
    }

    @Override
    public void dispose() {
        collisionConfig.dispose();
        dispatcher.dispose();
        broadphase.dispose();
        constraintSolver.dispose();
        dynamicsWorld.dispose();
    }
}
