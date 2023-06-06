package com.wabs.server;

import com.esotericsoftware.kryonet.Connection;

public class User {

    private Connection connection;
    private String username;
    private Long currentLobbyId;
    private boolean currentlyPlaying;

    public User(String username, Connection connection) {
        this.username = username;
        this.connection = connection;
        this.currentlyPlaying = false;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getUsername() {
        return username;
    }

    public Long getCurrentLobbyId() {
        return currentLobbyId;
    }

    public void setCurrentLobbyId(Long currentLobbyId) {
        this.currentLobbyId = currentLobbyId;
    }

    public boolean isCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public void setCurrentlyPlaying(boolean currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;
    }
}
