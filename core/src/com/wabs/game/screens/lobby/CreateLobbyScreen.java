package com.wabs.game.screens.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.wabs.game.WaveAttack;
import com.wabs.global.lobbyEvents.CreateLobbyEvent;

public final class CreateLobbyScreen extends ScreenAdapter {

    private final Stage stage;
    private final Table table;
    private final Skin skin = WaveAttack.textureSkin;
    private final float elementWidth;
    private final float elementHeight;

    private TextField lobbyName;
    private TextButton createButton;
    private CheckBox checkBox;

    public CreateLobbyScreen() {
        this.stage = new Stage();
        this.table = new Table();

        elementWidth = Gdx.graphics.getWidth() / 3f;
        elementHeight = 40f;

        this.table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        setLobbyName();
        setCheckBox();
        setCreateButton();

        this.stage.addActor(this.table);
    }

    private void setLobbyName() {
        this.lobbyName = new TextField("Lobby name", skin);
        this.table.add(lobbyName).width(elementWidth).height(elementHeight).padBottom(20f).row();
    }

    public void setCreateButton() {
        this.createButton = new TextButton("Create", skin);
        this.createButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                CreateLobbyEvent createLobbyEvent = new CreateLobbyEvent();
                createLobbyEvent.admin = WaveAttack.getInstance().getClientUsername();
                createLobbyEvent.name = lobbyName.getText().trim();
                createLobbyEvent.privateLobby = checkBox.isChecked();

                WaveAttack.getInstance().getClient().sendTCP(createLobbyEvent);

                return super.touchDown(event, x, y, pointer, button);
            }
        });
        this.table.add(createButton).width(elementWidth).height(elementHeight).row();
    }

    private void setCheckBox() {
        checkBox = new CheckBox("Private", skin);
        this.table.add(checkBox).width(elementWidth).height(elementHeight).padBottom(20f).row();
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
