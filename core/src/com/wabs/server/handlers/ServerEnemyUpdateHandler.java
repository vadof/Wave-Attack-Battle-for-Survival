package com.wabs.server.handlers;

import com.wabs.server.gameplay.GameLobby;

import java.util.ConcurrentModificationException;

public class ServerEnemyUpdateHandler implements Runnable {

    private static ServerEnemyUpdateHandler INSTANCE = null;
    public static boolean running = false;
    private GameLobbyHandler gameLobbyHandler = GameLobbyHandler.getInstance();

    private ServerEnemyUpdateHandler() {

    }

    public static ServerEnemyUpdateHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerEnemyUpdateHandler();
        }
        return INSTANCE;
    }

    public synchronized void start() {
        Thread thread = new Thread(this);
        running = true;
        thread.start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                for (GameLobby gameLobby : gameLobbyHandler.getGameLobbyList()) {
                    gameLobby.updateEnemies();
                }
            } catch (ConcurrentModificationException e) {
                System.out.println("Caught error in " + this.getClass());
            }
        }
    }
}
