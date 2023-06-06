package com.wabs.game.handlers;

import com.wabs.game.WaveAttack;
import com.wabs.game.supers.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerHandler {

    public static PlayerHandler INSTANCE = new PlayerHandler();

    private final List<Player> players = new ArrayList<>();

    public void addPlayer(Player player) {
        this.players.add(player);

        WaveAttack.getInstance().sceneManager.addScene(player.getPlayerScene());
        WaveAttack.getInstance().sceneManager.addScene(player.getCurrentWeaponScene());
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public Player getPlayerById(Integer id) {
        for (Player player : players) {
            if (player.getPlayerId() == id) {
                return player;
            }
        }
        return null;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

}
