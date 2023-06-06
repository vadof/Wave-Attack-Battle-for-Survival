package com.wabs.game.weapons;

import com.wabs.game.WaveAttack;
import com.wabs.global.weaponEvents.WeaponType;

import java.util.HashMap;
import java.util.Map;

public abstract class Arsenal {

    public static Weapon getWeapon(WeaponType weaponType) {
        if (getWeaponInfo().containsKey(weaponType)) {
            int price = getWeaponInfo().get(weaponType)[0];
            int slot = getWeaponInfo().get(weaponType)[1];

            switch (weaponType) {
                case STANDARD_PISTOL:
                    return new Pistol(WeaponType.STANDARD_PISTOL, 15, 12, 48, 30, 80, 0.02f, price, slot, WaveAttack.pistolSound);
                case HALCON:
                    return new Pistol(WeaponType.HALCON, 20, 16, 64, 25, 60, 0.01f, price, slot, WaveAttack.pistolSound);
                case LUNARFLARE:
                    return new AutomaticRifle(WeaponType.LUNARFLARE, 25, 30, 180, 10, 120, 0.025f, price, slot, WaveAttack.blasterSound);
                case CRUSHER:
                    return new Shotgun(WeaponType.CRUSHER, 50, 16, 64, 60, 240, 0.15f, price, slot, WaveAttack.pistolSound);
                case COSMIC_SHREDDER:
                    return new AutomaticRifle(WeaponType.COSMIC_SHREDDER, 20, 100, 500, 5, 300, 0.03f, price, slot, WaveAttack.blasterSound);
            }
        }
        return null;
    }

    /**
     *
     * @return weapon name, price, slot
     */
    public static Map<WeaponType, Integer[]> getWeaponInfo() {
        Map<WeaponType, Integer[]> weaponPrices = new HashMap<>();
        weaponPrices.put(WeaponType.STANDARD_PISTOL, new Integer[]{50, 1});
        weaponPrices.put(WeaponType.HALCON, new Integer[]{100, 1});
        weaponPrices.put(WeaponType.LUNARFLARE, new Integer[]{200, 2});
        weaponPrices.put(WeaponType.CRUSHER, new Integer[]{400, 2});
        weaponPrices.put(WeaponType.COSMIC_SHREDDER, new Integer[]{1000, 2});
        return weaponPrices;
    }

}
