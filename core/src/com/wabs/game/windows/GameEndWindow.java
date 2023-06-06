package com.wabs.game.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.List;

public class GameEndWindow extends Window {

    private Boolean win;
    private List<String> playerUsernames;
    private List<Integer> playersDamage;
    private List<Integer> playersDamageReceived;
    private List<Integer> playersDeath;
    private List<Integer> playersKills;

    private final float windowWidth;
    private final float windowHeight;

    private final float cellWidth;
    private final float cellHeight;

    private final int totalCellsInRow = 5;
    private final int totalCellsInColumn = 4;

    public GameEndWindow(String title, Skin skin,
                         List<String> playerUsernames, List<Integer> playersDamage,
                         List<Integer> playersDamageReceived, List<Integer> playersDeath,
                         List<Integer> playersKills, Boolean win) {
        super(title, skin);

        this.win = win;
        this.playerUsernames = playerUsernames;
        this.playersDamage = playersDamage;
        this.playersDamageReceived = playersDamageReceived;
        this.playersDeath = playersDeath;
        this.playersKills = playersKills;

        this.windowWidth = Gdx.graphics.getWidth() * 0.8f;
        this.windowHeight = Gdx.graphics.getHeight() * 0.6f;

        this.cellWidth = windowWidth / totalCellsInRow / 4;
        this.cellHeight = windowHeight / totalCellsInColumn / 4;

        setTitle();

        setUsernames();
        setKills();
        setDamageDealt();
        setDeaths();
        setDamageReceived();

        this.row();
        this.add(setExitButton());

        this.setMovable(false);

        this.setHeight(windowHeight);
        this.setWidth(windowWidth);

        this.pack();
        this.setPosition(windowWidth * 0.1f, windowHeight * 0.8f);
    }

    private void setTitle() {
        this.getTitleLabel().setAlignment(Align.center);
        this.getTitleLabel().setFontScale(2);

        if (win) {
            this.getTitleLabel().setColor(Color.GREEN);
        } else {
            this.getTitleLabel().setColor(Color.RED);
        }
    }

    private void setUsernames() {
        Table table = new Table();
        table.pad(cellHeight, cellWidth, cellHeight, 0);
        table.add(createNewLabel("Username"));
        for (String username: playerUsernames) {
            table.row();
            table.add(createNewLabel(username));
        }

        this.add(table);
    }

    private void setKills() {
        Table table = new Table();
        table.pad(cellHeight, cellWidth, cellHeight, 0);
        table.add(createNewLabel("Kills"));
        for (Integer kills: playersKills) {
            table.row();
            table.add(createNewLabel(kills.toString()));
        }

        this.add(table);
    }

    private void setDamageDealt() {
        Table table = new Table();
        table.pad(cellHeight, cellWidth, cellHeight, 0);
        table.add(createNewLabel("Damage dealt"));
        for (Integer damage : playersDamage) {
            table.row();
            table.add(createNewLabel(damage.toString()));
        }

        this.add(table);
    }

    private Button setExitButton() {
        //Button button = new TextButton("EXIT", getSkin());
        VisTextButton button = new VisTextButton("EXIT");
        button.addListener(new InputListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        this.add(button);
        this.add(button);
        return button;
    }

    private void setDeaths() {
        Table table = new Table();
        table.pad(cellHeight, cellWidth, cellHeight, 0);
        table.add(createNewLabel("Deaths"));
        for (Integer deaths : playersDeath) {
            table.row();
            table.add(createNewLabel(deaths.toString()));
        }

        this.add(table);
    }

    private void setDamageReceived() {
        Table table = new Table();
        table.pad(cellHeight, cellWidth, cellHeight, 0);
        table.add(createNewLabel("Damage received"));
        for (Integer damage : playersDamageReceived) {
            table.row();
            table.add(createNewLabel(damage.toString()));
        }

        this.add(table);
    }

    private Label createNewLabel(String name) {
        Label label = new Label(name, getSkin());
        label.setFontScale(2);
        label.setWidth(cellWidth);
        label.setHeight(cellHeight);
        label.setAlignment(Align.center);
        return label;
    }

}
