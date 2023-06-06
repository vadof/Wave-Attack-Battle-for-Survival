package com.wabs.game.bullet;

import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import net.mgsx.gltf.scene3d.scene.Scene;

public class BulletEntity {

    private final btRigidBody body;
    private final Scene scene;
    private int lifeSpan = 120;

    public BulletEntity(btRigidBody body, Scene scene) {
        this.body = body;
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    public btRigidBody getBody() {
        return body;
    }

    public boolean needToRemove() {
        return --lifeSpan <= 0;
    }

}
