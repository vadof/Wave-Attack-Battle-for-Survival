package com.wabs.global.gameEvents;

import java.util.List;

public class GameEndEvent {

    public Boolean win;
    public List<String> playerUsernames;
    public List<Integer> playersDamage;
    public List<Integer> playersDamageReceived;
    public List<Integer> playersDeath;
    public List<Integer> playersKills;
    public Long lobbyId;

    public GameEndEvent() {

    }

    public GameEndEvent(Boolean win, List<String> playerUsernames, List<Integer> playersDamage, List<Integer> playersDamageReceived, List<Integer> playersDeath, List<Integer> playersKills) {
        this.win = win;
        this.playerUsernames = playerUsernames;
        this.playersDamage = playersDamage;
        this.playersDamageReceived = playersDamageReceived;
        this.playersDeath = playersDeath;
        this.playersKills = playersKills;
    }
}
