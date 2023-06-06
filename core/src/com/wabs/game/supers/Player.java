package com.wabs.game.supers;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.wabs.game.WaveAttack;
import com.wabs.game.bullet.BulletPhysicsSystem;
import com.wabs.game.bullet.MotionState;
import com.wabs.game.screens.GameScreen;
import com.wabs.game.settings.SoundSettings;
import com.wabs.game.weapons.Arsenal;
import com.wabs.game.weapons.AutomaticRifle;
import com.wabs.game.weapons.Pistol;
import com.wabs.game.weapons.Weapon;
import com.wabs.global.weaponEvents.WeaponType;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class Player {

    private final String username;
    private final Integer id;
    private final Long lobbyId;

    private final Scene playerScene;

    private final SceneManager sceneManager;

    private Weapon firstWeapon;
    private Weapon secondWeapon;

//    private Scene firstWeaponScene;
//    private Scene secondWeaponScene;
    private Scene currentWeaponScene;

    private Integer money;
    private Weapon currentWeapon;
    private Integer hp;
    private boolean dead;

    // physics
    private btRigidBody playerBody;
    private Vector3 linearVelocity = new Vector3();
    private Vector3 angularVelocity = new Vector3();

    private int deactivationTimer = 0;

    public Player(String username, Integer id, Matrix4 playerTransform, Vector3 spawnPosition, Long lobbyId) {
        this.username = username;
        this.id = id;
        this.lobbyId = lobbyId;

        this.playerScene = WaveAttack.getInstance().playerScenes.get(id);
        this.sceneManager = WaveAttack.getInstance().sceneManager;

        playerTransform.translate(spawnPosition);
        playerScene.modelInstance.transform = playerTransform;

        this.playerBody = createPlayerBody();

        assignWeapons();

        this.money = 0;
        this.hp = 100;
        this.dead = false;
    }

    private btRigidBody createPlayerBody() {
        ModelInstance playerModelInstance = playerScene.modelInstance;

        BoundingBox boundingBox = new BoundingBox();
        playerModelInstance.calculateBoundingBox(boundingBox);

        btBoxShape boxShape = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(0.5f));

        MotionState motionState = new MotionState();
        motionState.transform = playerModelInstance.transform;

        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(1f, motionState, boxShape, new Vector3());
        btRigidBody body = new btRigidBody(info);

        // Prevent body from falling over and sleeping
        body.setAngularFactor(Vector3.Y);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);

        // Add damping so we dont slide forever
        body.setDamping(0.75f, 0.99f);

        BulletPhysicsSystem.INSTANCE.addBody(body);

        return body;
    }

    private void assignWeapons() {
//        firstWeaponScene = WaveAttack.getInstance().standardPistolScenes.get(id);
//        secondWeaponScene = WaveAttack.getInstance().automaticRiffleScenes.get(id);

        this.firstWeapon = Arsenal.getWeapon(WeaponType.STANDARD_PISTOL);
        currentWeapon = firstWeapon;
        currentWeaponScene = WaveAttack.getInstance().weaponTypeMap.get(currentWeapon.getWeaponType()).get(id);
    }

    public void updatePlayerScene(Matrix4 transform) {
        playerBody.setWorldTransform(transform);

        currentWeaponScene.modelInstance.transform.set(playerBody.getWorldTransform());
        currentWeaponScene.modelInstance.transform.translate(-1f, -2f, 0.5f);
    }

    public btRigidBody getPlayerBody() {
        return playerBody;
    }

    public Matrix4 getTransform() {
        return playerScene.modelInstance.transform;
    }

    public Scene getPlayerScene() {
        return this.playerScene;
    }

    public Integer getPlayerId() {
        return this.id;
    }

    public void removePlayerFromSceneManager(Player player) {
        this.sceneManager.getRenderableProviders().removeValue(player.getPlayerScene(), true);
        this.sceneManager.getRenderableProviders().removeValue(player.getCurrentWeaponScene(), true);
        BulletPhysicsSystem.INSTANCE.removeBody(player.getPlayerBody());
        player.playerBody.dispose();
    }

    public void addEnemyToGame(Enemy enemy) {
        this.sceneManager.addScene(enemy.getEnemyScene());
    }

    public void removeEnemyFromGame(Enemy enemy) {
        this.sceneManager.getRenderableProviders().removeValue(enemy.getEnemyScene(), true);
        BulletPhysicsSystem.INSTANCE.removeBody(enemy.getEnemyBody());
    }

    public void setLinearVelocity(Vector3 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public void setAngularVelocity(Vector3 angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public Weapon getFirstWeapon() {
        return firstWeapon;
    }

    public Weapon getSecondWeapon() {
        return secondWeapon;
    }

    public Scene getCurrentWeaponScene() {
        return this.currentWeaponScene;
    }

    public void buyWeapon(Weapon weapon) {
        if (this.money >= weapon.getPrice()) {
            switch (weapon.getSlot()) {
                case 1:
                    this.firstWeapon = weapon;
                    break;
                case 2:
                    this.secondWeapon = weapon;
                    break;
            }
            this.money -= weapon.getPrice();
        }
    }

    public void buyBullets(Integer slot, Integer price) {
        if (money >= price) {
            switch (slot) {
                case 1:
                    firstWeapon.buyBullets();
                    break;
                case 2:
                    secondWeapon.buyBullets();
                    break;
            }
            money -= price;
        }
    }

    public void switchWeapon(Integer slot) {
        switch (slot) {
            case 1:
                if (currentWeapon != firstWeapon) {
                    sceneManager.removeScene(currentWeaponScene);
                    this.currentWeapon = firstWeapon;
                    this.currentWeaponScene = WaveAttack.getInstance().weaponTypeMap.
                            get(currentWeapon.getWeaponType()).get(id);
                    sceneManager.addScene(currentWeaponScene);
                }
                break;
            case 2:
                if (this.secondWeapon != null && currentWeapon != secondWeapon) {
                    sceneManager.removeScene(currentWeaponScene);
                    this.currentWeapon = secondWeapon;
                    this.currentWeaponScene = WaveAttack.getInstance().weaponTypeMap.
                            get(currentWeapon.getWeaponType()).get(id);
                    sceneManager.addScene(currentWeaponScene);
                }
                break;
        }
    }

    public void shoot() {
        this.currentWeapon.shoot();
    }

    public void reload() {
        this.currentWeapon.reload();
    }

    public Weapon getCurrentWeapon() {
        return this.currentWeapon;
    }

    public void takeDamage(int damage) {
        if (this.hp - damage > 0) {
            this.hp -= damage;
            if (WaveAttack.getInstance().getClientUsername().equals(username)) {
                WaveAttack.playerGetDamageSound.play(SoundSettings.volume * 0.1f);
            }
        }
    }

    public void die() {
        hp = 0;
        dead = true;
        deactivateBody(true);
        WaveAttack.playerDeathSound.play(SoundSettings.volume * 0.15f);
    }

    public void setAlive() {
        hp = 100;
        dead = false;
        deactivateBody(false);
    }

    public void refreshHp() {
        hp = 100;
    }

    public void checkDeactivationTimer() {
        if (deactivationTimer > 0) {
            if (--deactivationTimer == 0) {
                playerBody.setMassProps(0, new Vector3());
                GameScreen.getInstance().removePlayerFromDeactivationList(this);
            }
        }
    }

    private void deactivateBody(boolean deactivate) {
        if (deactivate) {
            playerBody.setWorldTransform(playerBody.getWorldTransform().rotate(Vector3.X, 90));
            setDeactivationTimer();
            GameScreen.getInstance().addPlayerToDeactivate(this);
        } else {
            playerBody.setMassProps(1, new Vector3());
            playerBody.setWorldTransform(playerBody.getWorldTransform().rotate(Vector3.X, -90));
        }
    }

    public int getHp() {
        return this.hp;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public int getMoney() {
        return money;
    }

    public boolean isDead() {
        return dead;
    }

    public SceneManager getSceneManager() {
        return this.sceneManager;
    }

    private void setDeactivationTimer() {
        this.deactivationTimer = 90;
    }

    public String getUsername() {
        return username;
    }

    public Long getLobbyId() {
        return lobbyId;
    }
}
