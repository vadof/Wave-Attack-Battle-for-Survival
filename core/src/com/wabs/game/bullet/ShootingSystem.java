package com.wabs.game.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.wabs.game.WaveAttack;
import com.wabs.game.supers.Player;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.scene.Scene;

public class ShootingSystem {

    private final Player player;
    private final PerspectiveCamera camera;
    private Texture bulletTexture = WaveAttack.bulletTexture;

    public ShootingSystem(Player player, PerspectiveCamera camera) {
        this.player = player;
        this.camera = camera;
    }

    public void shoot() {
        Ray ray = camera.getPickRay(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);

        BulletEntity bullet = createBullet(player.getCurrentWeaponScene().modelInstance.transform);
        BulletRemovalSystem.INSTANCE.addBullet(bullet);

        Vector3 dir = ray.direction.scl(10f);
        dir.y += 0.8f;

        bullet.getBody().applyCentralImpulse(dir);

        player.shoot();
    }

    private BulletEntity createBullet(Matrix4 bulletTransform) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        Material material = new Material();

        PBRTextureAttribute textureAttribute = new PBRTextureAttribute(PBRTextureAttribute.Emissive, bulletTexture);

        material.set(textureAttribute);
        MeshPartBuilder builder = modelBuilder.part("bullet", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, material);

        btCollisionShape shape;

        SphereShapeBuilder.build(builder, 0.2f, 0.2f, 0.2f, 8, 8);
        shape = new btSphereShape(0.1f);

        ModelInstance box = new ModelInstance(modelBuilder.end());

        Vector3 playerPosition = bulletTransform.getTranslation(new Vector3());
        playerPosition.y += 2;

        Vector3 forwardDirection = bulletTransform.getRotation(new Quaternion()).transform(new Vector3(0, 0, 1)).nor();
        float distance = 0.5f; // Set the distance in front of the character where you want to spawn the model
        Vector3 spawnPosition = new Vector3(forwardDirection).scl(distance).add(playerPosition);

        box.transform.setTranslation(spawnPosition);

        float mass = 0.1f;

        Vector3 localInertia = new Vector3();
        shape.calculateLocalInertia(mass, localInertia);

        MotionState motionState = new MotionState();
        motionState.transform = box.transform;

        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, localInertia);
        btRigidBody body = new btRigidBody(info);

        return new BulletEntity(body, new Scene(box));
    }

}
