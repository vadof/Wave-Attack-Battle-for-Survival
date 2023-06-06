package com.wabs.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Client;
import com.wabs.game.WaveAttack;
import com.wabs.game.handlers.LabelHandler;
import com.wabs.game.listeners.*;
import com.wabs.global.*;
import com.wabs.global.enemyEvents.*;
import com.wabs.global.gameEvents.GameEndEvent;
import com.wabs.global.gameEvents.StartGameEvent;
import com.wabs.global.gameEvents.WaveClearedEvent;
import com.wabs.global.gameEvents.WaveInfoEvent;
import com.wabs.global.lobbyEvents.*;
import com.wabs.global.playerEvents.*;
import com.wabs.global.weaponEvents.*;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class ConnectScreen extends ScreenAdapter {

    public static final ConnectScreen INSTANCE = new ConnectScreen();

    public static Stage stage;
    private final Table root;

    private final TextField usernameLabel;
    private final TextField passwordLabel;
    private final TextButton connectButton;
    private final TextButton signUpButton;

    private final Skin skin = WaveAttack.textureSkin;

    private final Label errorLabel;
//    private final String ip = "193.40.156.34";
    private final String ip = "localhost";
    private final String websiteIp = "http://193.40.156.34:8083";

    public ConnectScreen() {
        stage = new Stage();

        this.root = new Table();
        this.root.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//        this.usernameLabel = new TextField("Username", skin);
//        this.passwordLabel = new TextField("Password", skin);
        this.usernameLabel = new TextField("test", skin);
        this.passwordLabel = new TextField("test", skin);

        this.connectButton = new TextButton("Connect", skin);
        this.connectButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Connect player to a server
                final Client client = new Client(8192, 8192);

                client.getKryo().register(JoinRequestEvent.class);
                client.getKryo().register(JoinResponseEvent.class);
                client.getKryo().register(MoveUpdateEvent.class);
                client.getKryo().register(PlayerAddEvent.class);
                client.getKryo().register(PlayerRemoveEvent.class);
                client.getKryo().register(WeaponBuyEvent.class);
                client.getKryo().register(WeaponChangeEvent.class);
                client.getKryo().register(MoneyAdditionEvent.class);
                client.getKryo().register(BulletsBuyEvent.class);
                client.getKryo().register(EnemyAddEvent.class);
                client.getKryo().register(EnemyTakeDamageEvent.class);
                client.getKryo().register(EnemyDeadEvent.class);
                client.getKryo().register(WaveClearedEvent.class);
                client.getKryo().register(EnemyMoveEvent.class);
                client.getKryo().register(PlayerTakeDamageEvent.class);
                client.getKryo().register(StartGameEvent.class);
                client.getKryo().register(PlayerDeadEvent.class);
                client.getKryo().register(GameEndEvent.class);
                client.getKryo().register(WaveInfoEvent.class);

                client.getKryo().register(CreateLobbyEvent.class);
                client.getKryo().register(LobbyInfoEvent.class);
                client.getKryo().register(JoinTheLobbyEvent.class);
                client.getKryo().register(LobbyLeaveEvent.class);
                client.getKryo().register(LobbyRemoveEvent.class);
                client.getKryo().register(AdminChangeEvent.class);

                client.getKryo().register(String.class);
                client.getKryo().register(Integer.class);
                client.getKryo().register(Long.class);
                client.getKryo().register(float[].class);
                client.getKryo().register(EnemyType.class);
                client.getKryo().register(WeaponType.class);

                client.getKryo().register(List.class);
                client.getKryo().register(ArrayList.class);

                client.getKryo().register(Matrix4.class);
                client.getKryo().register(Vector3.class);
                client.getKryo().register(Lobby.class);

                client.addListener(new PlayerJoinListener());
                client.addListener(new MoveUpdateListener());
                client.addListener(new PlayerAddListener());
                client.addListener(new PlayerRemoveListener());
                client.addListener(new ShopListener());
                client.addListener(new WeaponListener());
                client.addListener(new MoneyListener());
                client.addListener(new EnemyListener());
                client.addListener(new WaveListener());
                client.addListener(new PlayerTakeDamageListener());
                client.addListener(new GameEndListener());
                client.addListener(new LobbyListener());

                try {
                    client.start();
                    client.connect(5000, ip, 8085, 8085);
                } catch (Exception e) {
                    errorLabel.setText(e.getMessage());
                    return super.touchDown(event, x, y, pointer, button);
                }

                WaveAttack.getInstance().setClient(client);
                WaveAttack.getInstance().setClientUsername(usernameLabel.getText().trim());

                JoinRequestEvent joinRequestEvent = new JoinRequestEvent();
                joinRequestEvent.username = usernameLabel.getText().trim();
                joinRequestEvent.password = passwordLabel.getText().trim();

                client.sendTCP(joinRequestEvent);

                return super.touchDown(event, x, y, pointer, button);
            }
        });

        this.errorLabel = LabelHandler.INSTANCE.createLabel(null, 16, Color.RED);

        this.signUpButton = new TextButton("Sign Up", skin);
        this.signUpButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    Desktop.getDesktop().browse(new URL(websiteIp).toURI());
                } catch (Exception e) {
                    errorLabel.setText(e.getMessage());
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });


        this.stage.addActor(this.root);
        this.setToDefault();
    }

    public void setToDefault() {
        this.root.clear();


        this.signUpButton.setColor(Color.YELLOW);
        this.root.add(signUpButton).width(100).height(40).row();
        this.root.add(this.usernameLabel).width(250).padTop(25).row();
        this.root.add(this.passwordLabel).width(250).padTop(25).row();
        this.root.add(this.connectButton).size(250, 50).padTop(100).row();
        this.root.add(this.errorLabel).padTop(50);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void render(float delta) {
        this.stage.draw();
        this.stage.act(delta);
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Label getErrorLabel() {
        return errorLabel;
    }

}
