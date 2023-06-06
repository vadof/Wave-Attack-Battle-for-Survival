package com.wabs.game.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.wabs.game.WaveAttack;
import com.wabs.game.bullet.BulletPhysicsSystem;
import com.wabs.game.bullet.BulletRemovalSystem;
import com.wabs.game.bullet.ShootingSystem;
import com.wabs.game.controllers.PlayerController;
import com.wabs.game.handlers.EnemyHandler;
import com.wabs.game.settings.SoundSettings;
import com.wabs.game.supers.Player;
import com.wabs.game.weapons.Shotgun;
import com.wabs.game.weapons.Weapon;
import com.wabs.game.windows.GameEndWindow;
import com.wabs.game.windows.SettingsWindow;
import com.wabs.game.windows.ShopWindow;
import com.wabs.global.weaponEvents.WeaponChangeEvent;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class GameScreen extends ScreenAdapter implements InputProcessor {

    private static GameScreen INSTANCE = null;

    public static GameScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GameScreen();
        }
        return INSTANCE;
    }

    private SceneManager sceneManager;
    private PlayerController playerController;

    private boolean admin;

    PerspectiveCamera camera;
    private Player player;
    private int fireDelay = 0;
    private int reloadDelay = 0;

    // Collision
    private BulletPhysicsSystem bulletPhysicsSystem;
    // To debug
    private boolean drawDebug = false;

    // To show images and text on screen
    private final Stage stage = WaveAttack.getInstance().stage;
    private final VisLabel healthLabel = WaveAttack.getInstance().healthLabel;
    private final VisLabel bulletsLabel = WaveAttack.getInstance().bulletsLabel;
    private final VisLabel moneyLabel = WaveAttack.getInstance().moneyLabel;
    private final VisLabel waveClearedLabel = WaveAttack.getInstance().waveClearedLabel;

    private final VisLabel enemyRemainsLabel = WaveAttack.getInstance().enemyRemainsLabel;
    private final VisLabel currentWaveLabel = WaveAttack.getInstance().currentWaveLabel;

    private boolean showWaveClearedLabel = false;
    private int waveClearedTimer;

    //Shop
    ShopWindow shopWindow;
    SettingsWindow settingsWindow;
    GameEndWindow gameEndWindow;
    private boolean windowOpen = false;

    // Shooting
    private ShootingSystem shootingSystem;
    private BulletRemovalSystem bulletRemovalSystem = BulletRemovalSystem.INSTANCE;

    private List<Player> playersToDeactivate = new ArrayList<>();

    public void setPlayer(Player player, boolean admin) {
        fireDelay = 60;
        this.player = player;
        this.sceneManager = player.getSceneManager();
        this.admin = admin;

        camera = createCamera();
        WaveAttack.gameStartSound.play(SoundSettings.volume * 0.1f);

        playerController = new PlayerController(this.player, this.camera);
        shootingSystem = new ShootingSystem(player, this.camera);

        bulletRemovalSystem.setPlayer(player);
        bulletRemovalSystem.start();
    }

    public PerspectiveCamera createCamera() {
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 1f;
        camera.far = 1000;
        sceneManager.setCamera(camera);
        camera.position.set(0f, 0, 4f);

        return camera;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void show() {
        bulletPhysicsSystem = BulletPhysicsSystem.INSTANCE;

        this.stage.addActor(healthLabel);
        this.stage.addActor(WaveAttack.getInstance().crosshair);

        this.stage.addActor(WaveAttack.getInstance().moneyImage);
        this.stage.addActor(moneyLabel);

        this.stage.addActor(WaveAttack.getInstance().bulletsImage);
        this.stage.addActor(bulletsLabel);

        this.stage.addActor(currentWaveLabel);
        this.stage.addActor(enemyRemainsLabel);

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        if (playersToDeactivate.size() > 0) {
            try {
                for (Player player1 : playersToDeactivate)
                    player1.checkDeactivationTimer();
            } catch (ConcurrentModificationException ignored) {}
        }

        if (admin) {
            EnemyHandler.INSTANCE.sendEnemyPositionEvent();
        }

        if (!windowOpen) {
            playerController.render(delta);
            Gdx.input.setCursorCatched(true);
            if (!player.isDead()) {
                weaponProcessor();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!windowOpen) {
                this.openSettingsWindow();
            } else if (settingsWindow != null){
                this.closeSettingsWindow();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            if (!windowOpen) {
                this.openShopWindow();
            } else if (shopWindow != null) {
                this.closeShopWindow();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            drawDebug = !drawDebug;
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        bulletPhysicsSystem.update(delta);

        sceneManager.update(delta);
        sceneManager.render();

        if (drawDebug) {
            bulletPhysicsSystem.render(camera);
        }

        if (showWaveClearedLabel) {
            if (waveClearedTimer-- == 0) {
                showWaveClearedLabel = false;
                waveClearedLabel.remove();
            }
        }

        this.stage.act();
        this.stage.draw();

        Weapon currentWeapon = player.getCurrentWeapon();
        healthLabel.setText(player.getHp() + " HP");
        bulletsLabel.setText(currentWeapon.getBulletsInClip() + " / " + currentWeapon.getSpareBullets());

        moneyLabel.setText(player.getMoney());

        super.render(delta);
    }

    private void weaponProcessor() {
        if (fireDelay > 0) {
            fireDelay--;
        }

        if (reloadDelay > 0) {
            reloadDelay--;
            if (reloadDelay == 0) {
                player.reload();
            }
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (fireDelay == 0 && reloadDelay == 0) {
                if (player.getCurrentWeapon().canShoot()) {

                    if (player.getCurrentWeapon() instanceof Shotgun) {
                        for (int i = 0; i < 3; i++) {
                            shootingSystem.shoot();
                        }
                    }
                    shootingSystem.shoot();
                    playerController.addCamPitch(player.getCurrentWeapon().getRecoil());
                }
                fireDelay = player.getCurrentWeapon().getBps();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (reloadDelay == 0) {
                reloadDelay = player.getCurrentWeapon().getReloadDelay();
                WaveAttack.reloadSound.play(SoundSettings.volume * 0.3f);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            switchWeapon(1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            switchWeapon(2);
        }
    }

    private void switchWeapon(int slot) {
        WeaponChangeEvent weaponChangeEvent = new WeaponChangeEvent();
        weaponChangeEvent.slot = slot;
        weaponChangeEvent.playerId = player.getPlayerId();
        weaponChangeEvent.lobbyId = player.getLobbyId();

        WaveAttack.getInstance().getClient().sendTCP(weaponChangeEvent);
    }

    private void openShopWindow() {
        shopWindow = new ShopWindow("Shop", WaveAttack.textureSkin);
        this.stage.addActor(shopWindow);

        changeWindow();
    }

    private void closeShopWindow() {
        this.shopWindow.remove();
        this.shopWindow = null;

        changeWindow();
    }

    private void openSettingsWindow() {
        settingsWindow = new SettingsWindow("Settings", WaveAttack.textureSkin);
        this.stage.addActor(settingsWindow);

        changeWindow();
    }

    private void closeSettingsWindow() {
        this.settingsWindow.remove();
        this.settingsWindow = null;

        changeWindow();
    }

    private void changeWindow() {
        if (!windowOpen) {
            Gdx.input.setInputProcessor(this.stage);
            Gdx.input.setCursorCatched(false);
        } else {
            Gdx.input.setInputProcessor(this);
            Gdx.input.setCursorCatched(true);
        }
        windowOpen = !windowOpen;
    }

    public void setWaveClearedLabel(String text) {
        waveClearedLabel.setText(text);
        this.stage.addActor(waveClearedLabel);
        showWaveClearedLabel = true;
        waveClearedTimer = 300;
    }

    public void setGameEndWindow(GameEndWindow gameEndWindow) {
        if (windowOpen) {
            if (settingsWindow != null) {
                closeSettingsWindow();
            } else if (shopWindow != null) {
                closeShopWindow();
            }
        }

        this.gameEndWindow = gameEndWindow;
        this.stage.addActor(gameEndWindow);

        changeWindow();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void addPlayerToDeactivate(Player player) {
        playersToDeactivate.add(player);
    }

    public void removePlayerFromDeactivationList(Player player) {
        playersToDeactivate.remove(player);
    }

    public void setAdmin() {
        admin = true;
    }

    public void setTextToEnemyRemainsLabel(String text) {
        enemyRemainsLabel.setText(text);
    }

    public void setTextToCurrentWaveLabel(String text) {
        currentWaveLabel.setText(text);
    }

    public String getEnemyRemainsLabelText() {
        return enemyRemainsLabel.getText() + "";
    }
}
