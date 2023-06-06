package com.wabs.game.bullet;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;
import com.wabs.game.WaveAttack;
import com.wabs.game.handlers.EnemyHandler;
import com.wabs.game.screens.GameScreen;
import com.wabs.game.settings.SoundSettings;
import com.wabs.game.supers.Enemy;
import com.wabs.game.supers.Player;
import com.wabs.global.enemyEvents.EnemyTakeDamageEvent;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BulletRemovalSystem implements Runnable {

    public boolean run = false;
    private Player player;

    public static BulletRemovalSystem INSTANCE = new BulletRemovalSystem();

    private final List<BulletEntity> bulletEntityList = new LinkedList<>();
    private final EnemyTakeDamageEvent enemyTakeDamageEvent = new EnemyTakeDamageEvent();
    private final Sound enemyHitSound = WaveAttack.enemyHitSound;

    public void start() {
        run = true;

        enemyTakeDamageEvent.playerId = player.getPlayerId();
        enemyTakeDamageEvent.lobbyId = player.getLobbyId();

        final Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (run) {

            try {
                Thread.sleep((1000 / 60));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Iterator<BulletEntity> iterator = bulletEntityList.iterator();

                while (iterator.hasNext()) {
                    boolean remove = false;
                    BulletEntity bullet = iterator.next();
                    for (Enemy enemy : EnemyHandler.INSTANCE.getEnemyList()) {

                        Vector3 bulletPos = bullet.getBody().getWorldTransform().getTranslation(new Vector3());
                        Vector3 enemyPos = enemy.getEnemyBody().getCenterOfMassPosition();

                        float distance = bulletPos.dst(enemyPos);

                        if (distance < 3) {
                            try {
                                iterator.remove();
                                removeBullet(bullet);

                                enemyTakeDamageEvent.damage = GameScreen.getInstance().getPlayer().getCurrentWeapon().getDamage();
                                enemyTakeDamageEvent.enemyId = enemy.getId();
                                enemyHitSound.play(SoundSettings.volume * 0.6f);

                                remove = true;

                                WaveAttack.getInstance().getClient().sendUDP(enemyTakeDamageEvent);
                                break;
                            } catch (ConcurrentModificationException e) {
                                System.out.println("Error in class " + this.getClass());
                            }

                        }
                    }
                    if (!remove && bullet.needToRemove()) {
                        iterator.remove();
                        removeBullet(bullet);
                    }
                }
            } catch (Exception e) {
                System.out.println("Caught exception in " + this.getClass());
            }

        }
    }

    public void addBullet(BulletEntity bullet) {
        WaveAttack.getInstance().sceneManager.addScene(bullet.getScene());
        BulletPhysicsSystem.INSTANCE.addBody(bullet.getBody());
        bulletEntityList.add(bullet);
    }

    public void removeBullet(BulletEntity bullet) {
        WaveAttack.getInstance().sceneManager.removeScene(bullet.getScene());
        BulletPhysicsSystem.INSTANCE.removeBody(bullet.getBody());
        bulletEntityList.remove(bullet);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
