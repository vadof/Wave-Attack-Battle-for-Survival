package com.wabs.game.screens.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.wabs.game.WaveAttack;
import com.wabs.game.handlers.LobbyHandler;
import com.wabs.game.screens.ConnectScreen;
import com.wabs.global.Lobby;
import com.wabs.global.lobbyEvents.JoinTheLobbyEvent;

public final class AvailableLobbiesScreen extends ScreenAdapter {

    private static AvailableLobbiesScreen instance = null;

    private final Stage stage;
    private final Table tableLobbies;
    private final Table tableRefresh;
    private final Table tableLabel;
    private final Table tableCreate;
    private final Skin skin = WaveAttack.textureSkin;
    private final float elementWidth;
    private final float elementHeight;

    private TextButton createLobbyButton;

    private AvailableLobbiesScreen() {
        this.tableLobbies = new Table();
        this.tableRefresh = new Table();
        this.tableLabel = new Table();
        this.tableCreate = new Table();
        this.stage = ConnectScreen.stage;

        elementWidth = Gdx.graphics.getWidth() / 3f;
        elementHeight = 40f;

        this.tableLobbies.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.tableRefresh.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.tableRefresh.left().top();
        this.tableLabel.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.tableLabel.center().top();
        this.tableCreate.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.tableCreate.right().bottom();

        this.refresh();
    }

    public static AvailableLobbiesScreen getInstance() {
        if (instance == null) {
            instance = new AvailableLobbiesScreen();
        }
        return instance;
    }

    public void refresh() {
        this.tableLobbies.clear();
        this.tableRefresh.clear();
        this.tableLabel.clear();
        this.tableCreate.clear();
        this.stage.clear();

        setRefreshButton();
        setRootTitle();
        showLobbies();
        setCreateLobbyButton();

        this.stage.addActor(this.tableLobbies);
        this.stage.addActor(this.tableRefresh);
        this.stage.addActor(this.tableLabel);
        this.stage.addActor(this.tableCreate);
    }

    private void setRootTitle() {
        VisLabel title = new VisLabel("Lobbies");
        title.setAlignment(Align.center);
        title.setColor(Color.WHITE);
        title.setFontScale(2);

        this.tableLabel.add(title).padTop(10f).row();
    }

    private void showLobbies() {
        for (Lobby lobby : LobbyHandler.getInstance().getLobbyList()) {
            addNewLobby(lobby);
        }
    }

    private void setRefreshButton() {
        TextButton refreshButton = new TextButton("REFRESH", skin);

        refreshButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                refresh();
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        this.tableRefresh.add(refreshButton).height(elementHeight).width(100f).padLeft(10f).padTop(10f).row();
    }

    private void setCreateLobbyButton() {
        this.createLobbyButton = new TextButton("Create Lobby", skin);

        this.createLobbyButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                WaveAttack.getInstance().setScreen(new CreateLobbyScreen());
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        this.tableCreate.add(createLobbyButton).width(elementWidth).height(elementHeight)
                .padRight(10f).padBottom(10f).row();
    }

    private void addNewLobby(final Lobby lobby) {
        Table table = new Table();

        TextButton lobbyName = new TextButton(lobby.getLobbyName(), skin);
        lobbyName.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                JoinTheLobbyEvent joinTheLobbyEvent = new JoinTheLobbyEvent();
                joinTheLobbyEvent.username = WaveAttack.getInstance().getClientUsername();
                joinTheLobbyEvent.lobbyId = lobby.getLobbyId();

                WaveAttack.getInstance().getClient().sendTCP(joinTheLobbyEvent);

                return super.touchDown(event, x, y, pointer, button);
            }
        });

        lobbyName.setText(lobby.getLobbyName() + " | " + lobby.getLobbySize() + "/3");

        table.add(lobbyName).width(elementWidth).height(elementHeight);
        this.tableLobbies.add(table).padBottom(20).row();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void render(float delta) {
        this.stage.draw();
        this.stage.act(delta);
//        this.stage.setDebugAll(true);
    }

}
