package com.wabs.game.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.wabs.game.WaveAttack;
import com.wabs.game.screens.GameScreen;
import com.wabs.game.supers.Player;
import com.wabs.game.weapons.Arsenal;
import com.wabs.global.weaponEvents.BulletsBuyEvent;
import com.wabs.global.weaponEvents.WeaponBuyEvent;
import com.wabs.global.weaponEvents.WeaponType;

public class ShopWindow extends Window {

    Player player;

    public ShopWindow(String title, Skin skin) {
        super(title, skin);
        player = GameScreen.getInstance().getPlayer();

        this.setMovable(false);

        this.getTitleLabel().setAlignment(Align.center);
        this.getTitleLabel().setHeight(40);

        addLabel("First Weapon");
        addLabel("Second Weapon");
        addLabel("Other");

        this.row();

        addWeapon(WeaponType.STANDARD_PISTOL);
        addWeapon(WeaponType.LUNARFLARE);
        addBullets("First weapon bullets", 10, 1);

        this.row();

        addWeapon(WeaponType.HALCON);
        addWeapon(WeaponType.CRUSHER);
//        this.add(new Label("", getSkin()));
//        this.add(new Label("", getSkin()));
        addBullets("Second weapon bullets", 30, 2);

        this.row();

        this.add(new Label("", getSkin()));
        addWeapon(WeaponType.COSMIC_SHREDDER);

        this.pack();
        this.setPosition(0, Gdx.graphics.getHeight() * 0.9f);
    }

    private TextButton createWeaponBuyButton(final WeaponType name) {
        TextButton button = new TextButton("BUY", getSkin());
        button.addListener(new InputListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                WeaponBuyEvent weaponBuyEvent = new WeaponBuyEvent();
                weaponBuyEvent.weapon = name;
                weaponBuyEvent.price = Arsenal.getWeaponInfo().get(name)[0];
                weaponBuyEvent.playerId = player.getPlayerId();
                weaponBuyEvent.lobbyId = player.getLobbyId();
                weaponBuyEvent.slot = Arsenal.getWeaponInfo().get(name)[1];

                WaveAttack.getInstance().getClient().sendTCP(weaponBuyEvent);
            }

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        return button;
    }

    private void addLabel(String name) {
        Table table = new Table();
        table.pad(20, 50, 20 ,50);
        Label label = new Label(name, getSkin());
        table.add(label);
        this.add(table);
    }

    private void addWeapon(WeaponType name) {
        Table table = new Table();
        table.pad(0, 50, 25, 50);

        String displayName = name.toString().replace("_", " ");
        displayName = displayName.charAt(0) + displayName.substring(1).toLowerCase();
        Label label = new Label(displayName, getSkin());
        table.add(label);
        table.row();

        label = new Label(Arsenal.getWeaponInfo().get(name)[0] + " coins", getSkin());
        label.setColor(Color.YELLOW);
        table.add(label);
        table.row();

        table.add(createWeaponBuyButton(name));
        this.add(table);
    }

    private void addBullets(String name, final int price, final int slot) {
        Table table = new Table();
        table.pad(0, 50, 25, 50);

        Label label = new Label(name, getSkin());
        table.add(label);
        table.row();

        label = new Label(price + " coins", getSkin());
        label.setColor(Color.YELLOW);
        table.add(label);
        table.row();

        TextButton button = new TextButton("BUY", getSkin());
        button.addListener(new InputListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                BulletsBuyEvent bulletsBuyEvent = new BulletsBuyEvent();
                bulletsBuyEvent.slot = slot;
                bulletsBuyEvent.price = price;
                bulletsBuyEvent.playerId = player.getPlayerId();
                bulletsBuyEvent.lobbyId = player.getLobbyId();

                WaveAttack.getInstance().getClient().sendTCP(bulletsBuyEvent);
            }

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        table.add(button);
        this.add(table);
    }
}
