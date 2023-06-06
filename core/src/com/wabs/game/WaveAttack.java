package com.wabs.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.kryonet.Client;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.wabs.game.bullet.BulletPhysicsSystem;
import com.wabs.game.screens.ConnectScreen;
import com.wabs.game.weapons.Weapon;
import com.wabs.global.enemyEvents.EnemyType;
import com.wabs.global.weaponEvents.WeaponType;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WaveAttack extends Game {

	private Client client;
	private String clientUsername;
	private Long currentLobbyId;

	public List<Scene> playerScenes = new ArrayList<>();
	public SceneManager sceneManager;

	public List<Scene> standardPistolScenes = new ArrayList<>();
	public List<Scene> halconScenes = new ArrayList<>();

	public List<Scene> lunarflareScenes = new ArrayList<>();
	public List<Scene> crusherScenes = new ArrayList<>();

	public List<Scene> cosmicShredderScenes = new ArrayList<>();

	public List<Scene> klerkScenes = new ArrayList<>();
	public List<Scene> krongScenes = new ArrayList<>();
	public List<Scene> nyxScenes = new ArrayList<>();
	public List<Scene> rachnaScenes = new ArrayList<>();
	public List<Scene> squirtelScenes = new ArrayList<>();
	public List<Scene> tentacleScenes = new ArrayList<>();

	private SceneAsset sceneAsset;
	private Cubemap diffuseCubemap;
	private Cubemap environmentCubemap;
	private Cubemap specularCubemap;
	private Texture brdfLUT;
	private SceneSkybox skybox;
	private DirectionalLightEx light;

	private final int NUMBER_OF_PLAYERS = 3;

	public Stage stage;
	public VisLabel healthLabel;
	public Image crosshair;
	public Image moneyImage;
	public VisLabel moneyLabel;
	public Image bulletsImage;
	public VisLabel bulletsLabel;
	public VisLabel waveClearedLabel;
	public VisLabel enemyRemainsLabel;
	public VisLabel currentWaveLabel;

	public static TextureAtlas atlas;
	public static Skin textureSkin;

	public static Sound blasterSound;
	public static Sound pistolSound;
	public static Sound enemyHitSound;
	public static Sound playerDeathSound;
	public static Sound playerRespawnSound;
	public static Sound playerGetDamageSound;
	public static Sound victorySound;
	public static Sound defeatSound;
	public static Sound waveClearedSound;
	public static Sound gameStartSound;
	public static Sound reloadSound;

	public HashMap<EnemyType, List<Scene>> enemyTypeMap = new HashMap<>();
	public HashMap<WeaponType, List<Scene>> weaponTypeMap = new HashMap<>();

	public static Texture bulletTexture;

	@Override
	public void create() {
		Bullet.init();

		sceneManager = new SceneManager();

		atlas = new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
		textureSkin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);
		bulletTexture = new Texture(Gdx.files.internal("ui/bullet_texture.jpg"));

		createAllScenes();
		createEnemyScenes();
		createWeaponScenes();

		putValuesToEnemyTypeMap();
		putValuesToWeaponTypeMap();

		loadSounds();

		setUpTextureDisplay();
		createStage();

		setScreen(new ConnectScreen());
	}

	public void createAllScenes() {
		// Create Map
		sceneAsset = new GLTFLoader().load(Gdx.files.internal("map/map.gltf"));
		Scene terrainScene = new Scene(sceneAsset.scene);
		sceneManager.addScene(terrainScene);

		btCollisionShape shape = Bullet.obtainStaticNodeShape(terrainScene.modelInstance.nodes);
		btRigidBody.btRigidBodyConstructionInfo sceneInfo = new btRigidBody.btRigidBodyConstructionInfo(0f, null, shape, Vector3.Zero);
		btRigidBody body = new btRigidBody(sceneInfo);
		BulletPhysicsSystem.INSTANCE.addBody(body);

		// create PLayer and weapon scenes.
		for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
			sceneAsset = new GLTFLoader().load(Gdx.files.internal("character/character.gltf"));
			Scene playerScene = new Scene(sceneAsset.scene);
			playerScenes.add(playerScene);
		}
	}

	public void loadSounds() {
		blasterSound = Gdx.audio.newSound(Gdx.files.internal("sounds/blaster_shoot.mp3"));
		pistolSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pistol_shoot.mp3"));
		enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit_enemy.mp3"));
		playerRespawnSound= Gdx.audio.newSound(Gdx.files.internal("sounds/player_respawn.wav"));
		playerGetDamageSound = Gdx.audio.newSound(Gdx.files.internal("sounds/player_get_damage.wav"));
		playerDeathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/player_death.mp3"));
		victorySound = Gdx.audio.newSound(Gdx.files.internal("sounds/victory.mp3"));
		defeatSound = Gdx.audio.newSound(Gdx.files.internal("sounds/defeat.mp3"));
		waveClearedSound = Gdx.audio.newSound(Gdx.files.internal("sounds/wave_cleared.mp3"));
		gameStartSound = Gdx.audio.newSound(Gdx.files.internal("sounds/game_start.wav"));
		reloadSound = Gdx.audio.newSound(Gdx.files.internal("sounds/reload.mp3"));
	}

	private void createEnemyScenes() {
		Scene scene;
		for (int i = 0; i < 5; i++) {
			sceneAsset = new GLTFLoader().load(Gdx.files.internal("enemies/KLERK/scene.gltf"));
			scene = new Scene(sceneAsset.scene);
			klerkScenes.add(scene);
		}

		for (int i = 0; i < 4; i++) {
			sceneAsset = new GLTFLoader().load(Gdx.files.internal("enemies/TENTACLE/scene.gltf"));
			scene = new Scene(sceneAsset.scene);
			tentacleScenes.add(scene);
		}

		for (int i = 0; i < 8; i++) {
			sceneAsset = new GLTFLoader().load(Gdx.files.internal("enemies/NYX/scene.gltf"));
			scene = new Scene(sceneAsset.scene);
			nyxScenes.add(scene);
		}

		for (int i = 0; i < 5; i++) {
			sceneAsset = new GLTFLoader().load(Gdx.files.internal("enemies/KRONG/scene.gltf"));
			scene = new Scene(sceneAsset.scene);
			krongScenes.add(scene);
		}

		for (int i = 0; i < 6; i++) {
			sceneAsset = new GLTFLoader().load(Gdx.files.internal("enemies/SQUIRTEL/scene.gltf"));
			scene = new Scene(sceneAsset.scene);
			squirtelScenes.add(scene);
		}

		for (int i = 0; i < 3; i++) {
			sceneAsset = new GLTFLoader().load(Gdx.files.internal("enemies/RACHNA/scene.gltf"));
			scene = new Scene(sceneAsset.scene);
			rachnaScenes.add(scene);
		}
	}

	private void createWeaponScenes() {
		Scene scene;
		for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
			sceneAsset = new GLTFLoader().load(Gdx.files.internal("weapon/standard_pistol/pistol.gltf"));
			scene = new Scene(sceneAsset.scene);
			scene.modelInstance.nodes.get(0).scale.set(0.5f, 0.5f, 0.5f);
			scene.modelInstance.calculateTransforms();
			standardPistolScenes.add(scene);

			sceneAsset = new GLTFLoader().load(Gdx.files.internal("weapon/halcon/scene.gltf"));
			scene = new Scene(sceneAsset.scene);
			halconScenes.add(scene);

			sceneAsset = new GLTFLoader().load(Gdx.files.internal("weapon/lunarflare/scene.gltf"));
			scene = new Scene(sceneAsset.scene);
			lunarflareScenes.add(scene);

			sceneAsset = new GLTFLoader().load(Gdx.files.internal("weapon/crusher/scene.gltf"));
			scene = new Scene(sceneAsset.scene);
			crusherScenes.add(scene);

			sceneAsset = new GLTFLoader().load(Gdx.files.internal("weapon/cosmic_shredder/scene.gltf"));
			scene = new Scene(sceneAsset.scene);
			cosmicShredderScenes.add(scene);
		}
	}

	private void putValuesToEnemyTypeMap() {
		enemyTypeMap.put(EnemyType.KLERK, klerkScenes);
		enemyTypeMap.put(EnemyType.TENTACLE, tentacleScenes);
		enemyTypeMap.put(EnemyType.KRONG, krongScenes);
		enemyTypeMap.put(EnemyType.NYX, nyxScenes);
		enemyTypeMap.put(EnemyType.SQUIRTEL, squirtelScenes);
		enemyTypeMap.put(EnemyType.RACHNA, rachnaScenes);
	}

	private void putValuesToWeaponTypeMap() {
		weaponTypeMap.put(WeaponType.STANDARD_PISTOL, standardPistolScenes);
		weaponTypeMap.put(WeaponType.HALCON, halconScenes);
		weaponTypeMap.put(WeaponType.LUNARFLARE, lunarflareScenes);
		weaponTypeMap.put(WeaponType.CRUSHER, crusherScenes);
		weaponTypeMap.put(WeaponType.COSMIC_SHREDDER, cosmicShredderScenes);
	}

	public void createStage() {
		if (!VisUI.isLoaded())
			VisUI.load();

		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

		Texture texture = new Texture(Gdx.files.internal("GameScreenStage/crosshair.png"));
		this.crosshair = new Image(texture);
		this.crosshair.setSize(this.crosshair.getWidth() * 0.65f, this.crosshair.getHeight() * 0.65f);
		this.crosshair.setPosition(
				((float) Gdx.graphics.getWidth() - this.crosshair.getWidth()) / 2f * 0.992f,
				((float) Gdx.graphics.getHeight() - this.crosshair.getHeight()) / 2f);

		this.healthLabel = new VisLabel();
		this.healthLabel.setColor(Color.GREEN);
		this.healthLabel.setFontScale(2);
		this.healthLabel.setPosition(10, 20);

		texture = new Texture(Gdx.files.internal("GameScreenStage/coin.png"));
		this.moneyImage = new Image(texture);
		this.moneyImage.setSize(this.moneyImage.getWidth() * 0.07f, this.moneyImage.getHeight() * 0.07f);
		this.moneyImage.setPosition(Gdx.graphics.getWidth() - 110, Gdx.graphics.getHeight() - 40);

		this.moneyLabel = new VisLabel();
		this.moneyLabel.setColor(Color.GOLD);
		this.moneyLabel.setFontScale(2);
		this.moneyLabel.setPosition(Gdx.graphics.getWidth() - 70, Gdx.graphics.getHeight() - 20);

		this.bulletsLabel = new VisLabel();
		this.bulletsLabel.setColor(Color.BLACK);
		this.bulletsLabel.setFontScale(2);
		this.bulletsLabel.setPosition(Gdx.graphics.getWidth() - 120, 20);

		texture = new Texture(Gdx.files.internal("GameScreenStage/bullet.png"));
		this.bulletsImage = new Image(texture);
		this.bulletsImage.setSize(this.bulletsImage.getWidth() * 1.8f, this.bulletsImage.getHeight() * 1.8f);
		this.bulletsImage.setPosition(Gdx.graphics.getWidth() - 150, 5);

		this.waveClearedLabel = new VisLabel();
		this.waveClearedLabel.setColor(Color.BLACK);
		this.waveClearedLabel.setFontScale(4);
		this.waveClearedLabel.setPosition(Gdx.graphics.getWidth() * 0.3f, Gdx.graphics.getHeight() / 1.8f);

		this.currentWaveLabel = new VisLabel("Wave - 1" + "/6");
		this.currentWaveLabel.setColor(Color.WHITE);
		this.currentWaveLabel.setFontScale(2);
		this.currentWaveLabel.setPosition(Gdx.graphics.getWidth() * 0.02f, Gdx.graphics.getHeight() * 0.95f);

		this.enemyRemainsLabel = new VisLabel("5 enemy remains");
		this.enemyRemainsLabel.setColor(Color.WHITE);
		this.enemyRemainsLabel.setFontScale(2);
		this.enemyRemainsLabel.setPosition(Gdx.graphics.getWidth() * 0.19f, Gdx.graphics.getHeight() * 0.95f);
	}

	public void setUpTextureDisplay() {
		// Setup texture display
		light = new DirectionalLightEx();
		light.direction.set(1, -3, 1).nor();
		light.color.set(Color.WHITE);
		sceneManager.environment.add(light);

		IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);


		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
		pixmap.setColor(41 / 255f, 30 / 255f, 44 / 255f, 1f); // Set the RGB and alpha values
		pixmap.fill();
		environmentCubemap = new Cubemap(pixmap, pixmap, pixmap, pixmap, pixmap, pixmap);
		pixmap.dispose();


		diffuseCubemap = iblBuilder.buildIrradianceMap(2048);
		specularCubemap = iblBuilder.buildRadianceMap(10);
		iblBuilder.dispose();

		brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
		sceneManager.setAmbientLight(1f);
		sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
		sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
		sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

		skybox = new SceneSkybox(environmentCubemap);

		sceneManager.setSkyBox(skybox);
	}

	@Override
	public void resize(int width, int height) {
		sceneManager.updateViewport(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}

	@Override
	public void dispose () {
		sceneManager.dispose();
		sceneAsset.dispose();
		environmentCubemap.dispose();
		diffuseCubemap.dispose();
		specularCubemap.dispose();
		brdfLUT.dispose();
		skybox.dispose();
		pistolSound.dispose();
		blasterSound.dispose();
		blasterSound.dispose();
		pistolSound.dispose();
		enemyHitSound.dispose();
		playerDeathSound.dispose();
		playerRespawnSound.dispose();
		playerGetDamageSound.dispose();
		victorySound.dispose();
		defeatSound.dispose();
		waveClearedSound.dispose();
		gameStartSound.dispose();
		reloadSound.dispose();
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Client getClient() {
		return client;
	}

	public static WaveAttack getInstance() {
		return (WaveAttack) Gdx.app.getApplicationListener();
	}

	public String getClientUsername() {
		return clientUsername;
	}

	public void setClientUsername(String username) {
		this.clientUsername = username;
	}

	public Long getCurrentLobbyId() {
		return currentLobbyId;
	}

	public void setCurrentLobbyId(Long id) {
		currentLobbyId = id;
	}

}
