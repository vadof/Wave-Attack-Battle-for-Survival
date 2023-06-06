package com.wabs.game.weapons;

import com.badlogic.gdx.audio.Sound;
import com.wabs.game.settings.SoundSettings;
import com.wabs.global.weaponEvents.WeaponType;

public class Weapon {

    protected final WeaponType weaponType;
    protected final Integer clipCapacity;
    protected Integer bulletsInClip;
    protected Integer spareBullets;
    protected final Integer MAX_BULLETS;
    protected final int damage;
    protected final Integer bps;
    protected final Integer reloadDelay;
    protected final float recoil;
    protected final Integer price;
    protected final Integer slot;
    protected final Sound sound;

    /**
     * @param weaponType - weapon type
     * @param damage - weapon damage
     * @param clipCapacity - How many bullets can fit in a clip
     * @param maxBullets - How many bullets can be carried (including clip)
     * @param bps - bullets per seconds
     * @param reloadDelay - weapon reload time
     * @param recoil - weapon vertical recoil
     * @param slot - in which the weapon is located
     * @param sound - shoot sound
     */
    public Weapon(WeaponType weaponType, int damage, Integer clipCapacity, Integer maxBullets,
                  Integer bps, Integer reloadDelay, float recoil, Integer price, Integer slot, Sound sound) {
        this.weaponType = weaponType;
        this.damage = damage;
        this.clipCapacity = clipCapacity;
        this.MAX_BULLETS = maxBullets;
        this.bps = bps;
        this.reloadDelay = reloadDelay;
        this.recoil = recoil;
        this.price = price;
        this.slot = slot;
        this.sound = sound;

        this.spareBullets = MAX_BULLETS - clipCapacity;
        this.bulletsInClip = clipCapacity;
    }

    public void shoot() {
        if (canShoot()) {
            bulletsInClip--;
            makeSound();
        }
    }

    protected void makeSound() {
        long id = sound.play(SoundSettings.volume * 0.1f);
    }

    public boolean canShoot() {
        return bulletsInClip > 0;
    }

    public void reload() {
        if (spareBullets > 0 && bulletsInClip != clipCapacity) {
            int need = clipCapacity - bulletsInClip;
            if (spareBullets >= need) {
                bulletsInClip += need;
                spareBullets -= need;
            } else {
                bulletsInClip += spareBullets;
                spareBullets = 0;
            }
        }
    }

    public void buyBullets() {
        spareBullets += clipCapacity;
    }

    public Integer getBps() {
        return bps;
    }

    public Integer getReloadDelay() {
        return this.reloadDelay;
    }

    public Integer getDamage() {
        return damage;
    }

    public Integer getPrice() {
        return this.price;
    }

    public Integer getBulletsInClip() {
        return bulletsInClip;
    }

    public Integer getSpareBullets() {
        return spareBullets;
    }

    public Integer getSlot() {
        return this.slot;
    }

    public WeaponType getWeaponType() {
        return this.weaponType;
    }

    public float getRecoil() {
        return recoil;
    }
}
