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
import com.wabs.game.screens.ConnectScreen;
import com.wabs.global.Lobby;
import com.wabs.global.gameEvents.StartGameEvent;
import com.wabs.global.lobbyEvents.LobbyLeaveEvent;

public final class LobbyInfoScreen extends ScreenAdapter {

    private static LobbyInfoScreen instance = null;

    private final Stage stage;
    private final Table tableLeave;
    private final Table tableTitle;
    private final Table tableStart;
    private final Skin skin = WaveAttack.textureSkin;
    private final VisLabel labelPlayers = new VisLabel("Players:");
    private final float elementWidth = Gdx.graphics.getWidth() / 3f;
    private final float elementHeight = 40f;

    private Lobby lobby;
    private TextButton startButton;

    private LobbyInfoScreen() {
        this.stage = ConnectScreen.stage;
        this.tableLeave = new Table();
        this.tableTitle = new Table();
        this.tableStart = new Table();

        setToDefault();

        this.tableLeave.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.tableTitle.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.tableTitle.center().top();
        this.tableStart.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.tableStart.right().bottom();

        labelPlayers.setAlignment(Align.center);
        labelPlayers.setFontScale(2f);
    }

    public static LobbyInfoScreen getInstance() {
        if (instance == null) {
            instance = new LobbyInfoScreen();
        }
        return instance;
    }

    public void refresh(Lobby lobby) {
        this.tableLeave.clear();
        this.tableTitle.clear();
        this.tableStart.clear();
        this.stage.clear();

        this.lobby = lobby;

        setToDefault();

        setRootTitle();
        showUsernames();
        setStartGameButton();
        setLeaveButton();

        this.stage.addActor(this.tableLeave);
        this.stage.addActor(this.tableTitle);
        this.stage.addActor(this.tableStart);
    }

    public void setToDefault() {
        this.tableLeave.clear();
        this.tableTitle.clear();
        this.tableStart.clear();
        this.stage.clear();
    }

    private void setLeaveButton() {
        TextButton leaveButton = new TextButton("LEAVE", skin);

        leaveButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                LobbyLeaveEvent lobbyLeaveEvent = new LobbyLeaveEvent();
                lobbyLeaveEvent.lobbyId = lobby.getLobbyId();
                lobbyLeaveEvent.username = WaveAttack.getInstance().getClientUsername();

                WaveAttack.getInstance().getClient().sendTCP(lobbyLeaveEvent);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        this.tableLeave.add(leaveButton).padTop(60f).height(elementHeight).width(100f).row();
    }

    public void setRootTitle() {
        VisLabel title = new VisLabel(lobby.getLobbyName());
        title.setAlignment(Align.center);
        title.setColor(Color.WHITE);
        title.setFontScale(2);

        this.tableTitle.add(title).padTop(10f).row();
    }

    public void showUsernames() {
        this.tableLeave.add(labelPlayers).height(elementHeight).width(elementWidth).padBottom(40f).row();
        for (String username : lobby.getPlayerUsernames()) {
            addNewUsername(username);
        }
    }

    private void setStartGameButton() {
        this.startButton = new TextButton("Start Game", skin);

        this.startButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (lobby.getAdmin().equals(WaveAttack.getInstance().getClientUsername())) {
                    StartGameEvent startGameEvent = new StartGameEvent();
                    startGameEvent.lobbyId = lobby.getLobbyId();

                    WaveAttack.getInstance().getClient().sendTCP(startGameEvent);
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        this.tableStart.add(startButton).width(elementWidth).height(elementHeight).padRight(10f).padBottom(10f).row();
    }

    public void addNewUsername(String username) {
        VisLabel user = new VisLabel();
        user.setText(username);
        user.setAlignment(Align.center);
        user.setFontScale(2f);
        this.tableLeave.add(user).height(elementHeight).width(elementWidth).padBottom(20f).row();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void render(final float delta) {
        this.stage.draw();
        this.stage.act(delta);
//        this.stage.setDebugAll(true);
    }

}
