package com.wabs.game.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.wabs.game.WaveAttack;
import com.wabs.game.settings.CameraSettings;

public class SettingsWindow extends Window {

    private VisSelectBox<String> resolutionSelect;

    public SettingsWindow(String title, Skin skin) {
        super(title, skin);

        this.setMovable(false);
        this.getTitleLabel().setAlignment(Align.center);
        this.getTitleLabel().setHeight(40);

        createSensitivitySetting();
        createResolutionSetting();
        this.row();
        createExitButton();

        this.pack();

        this.setSize(WaveAttack.getInstance().stage.getViewport().getScreenWidth() * 0.3f, WaveAttack.getInstance().stage.getViewport().getScreenHeight() * 0.7f);
        this.setPosition(Gdx.graphics.getWidth() * 0.35f, Gdx.graphics.getHeight() * 0.15f);
    }

    private void createSensitivitySetting() {
        VisLabel horSensLabel = new VisLabel("Horizontal Sensitivity");
        VisLabel horSensValue = new VisLabel(CameraSettings.CAMERA_HORIZONTAL_SENSITIVITY + "");
        Slider horizontalSensitivity = new Slider(0.1F, 1.0F, 0.05F, false, getSkin());
        horizontalSensitivity.setValue(CameraSettings.CAMERA_HORIZONTAL_SENSITIVITY);
        horizontalSensitivity.addListener((new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Slider slider = (Slider)actor;
                CameraSettings.CAMERA_HORIZONTAL_SENSITIVITY = slider.getValue();

                String value = String.valueOf(slider.getValue());
                if (value.length() > 4) {
                    horSensValue.setText(value.substring(0, 4));
                } else {
                    horSensValue.setText(value);
                }
            }
        }));

        this.add(horSensLabel).padBottom(5f).row();
        this.add(horSensValue).padBottom(5f).row();
        this.add(horizontalSensitivity).padBottom(20f).row();


        VisLabel verticalSensLabel = new VisLabel("Vertical Sensitivity");
        VisLabel verticalSensValue = new VisLabel(CameraSettings.CAMERA_VERTICAL_SENSITIVITY + "");
        Slider verticalSensitivity = new Slider(0.05F, 1.0F, 0.05F, false, getSkin());
        verticalSensitivity.setValue(CameraSettings.CAMERA_VERTICAL_SENSITIVITY);
        verticalSensitivity.addListener((new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Slider slider = (Slider)actor;
                CameraSettings.CAMERA_VERTICAL_SENSITIVITY = slider.getValue();

                String value = String.valueOf(slider.getValue());
                if (value.length() > 4) {
                    verticalSensValue.setText(value.substring(0, 4));
                } else {
                    verticalSensValue.setText(value);
                }
            }
        }));

        this.add(verticalSensLabel).padBottom(5f).row();
        this.add(verticalSensValue).padBottom(5f).row();
        this.add(verticalSensitivity).padBottom(20f).row();
    }

    private void createResolutionSetting() {
        Table table = new Table();
        table.pad(20);

        resolutionSelect = new VisSelectBox<>();

        resolutionSelect.setItems("1280 x 720", "1920 x 1080", "2560 x 1440");

        String currentResolution = WaveAttack.getInstance().stage.getViewport().getScreenWidth() + " x " + WaveAttack.getInstance().stage.getViewport().getScreenHeight();
        resolutionSelect.setSelected(currentResolution);

        resolutionSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Graphics.DisplayMode mode;
                switch (resolutionSelect.getSelected()) {
                    case "1280 x 720":
                        mode = getDisplayMode(1280, 720);
                        if (mode != null) {
                            Gdx.graphics.setFullscreenMode(mode);
                            WaveAttack.getInstance().stage.getViewport().update(1280, 720);
                        }
                        break;
                    case "1920 x 1080":
                        mode = getDisplayMode(1920, 1080);
                        if (mode != null) {
                            Gdx.graphics.setFullscreenMode(mode);
                            WaveAttack.getInstance().stage.getViewport().update(1920, 1080);
                        }
                        break;
                    case "2560 x 1440":
                        mode = getDisplayMode(2560, 1440);
                        if (mode != null) {
                            Gdx.graphics.setFullscreenMode(mode);
                            WaveAttack.getInstance().stage.getViewport().update(2560, 1440);
                        }
                        break;
                }
            }
        });

        table.add(new VisLabel("Resolution")).padBottom(20f);
        table.row();
        table.add(resolutionSelect);
        this.add(table);
    }

    private Graphics.DisplayMode getDisplayMode(int height, int width) {
        Graphics.DisplayMode matchMode = null;
        for (Graphics.DisplayMode mode : Gdx.graphics.getDisplayModes()) {
            if (mode.width == height && mode.height == width) {
                matchMode = mode;
                break;
            }
        }

        return matchMode;
    }

    private void createExitButton() {
        Table table = new Table();
        table.pad(20);

        VisTextButton button = new VisTextButton("EXIT");
        button.addListener(new InputListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        table.add(button);
        this.add(table);
    }
}
