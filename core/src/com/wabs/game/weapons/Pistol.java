package com.wabs.game.weapons;

import com.badlogic.gdx.audio.Sound;
import com.wabs.global.weaponEvents.WeaponType;

public class Pistol extends Weapon {

    public Pistol(WeaponType weaponType, int damage, Integer clipCapacity, Integer maxBullets,
                  Integer bps, Integer reloadDelay, float recoil, Integer price, Integer slot, Sound sound) {
        super(weaponType, damage, clipCapacity, maxBullets, bps, reloadDelay, recoil, price, slot, sound);
    }

}
