package com.wabs.server;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.esotericsoftware.kryonet.Server;
import com.wabs.global.*;
import com.wabs.global.enemyEvents.*;
import com.wabs.global.gameEvents.GameEndEvent;
import com.wabs.global.gameEvents.StartGameEvent;
import com.wabs.global.gameEvents.WaveClearedEvent;
import com.wabs.global.gameEvents.WaveInfoEvent;
import com.wabs.global.lobbyEvents.*;
import com.wabs.global.playerEvents.*;
import com.wabs.global.weaponEvents.*;
import com.wabs.server.handlers.ServerEnemyUpdateHandler;
import com.wabs.server.listeners.*;

import java.io.IOException;
import java.util.ArrayList;

public class ServerFoundation {

    public static ServerFoundation instance;

    private Server server;

    public static void main(String[] args) {
        ServerFoundation.instance = new ServerFoundation();
    }

    public ServerFoundation() {
        this.server = new Server(8192, 8192);

        // Register only common classes
        this.server.getKryo().register(JoinRequestEvent.class);
        this.server.getKryo().register(JoinResponseEvent.class);
        this.server.getKryo().register(MoveUpdateEvent.class);
        this.server.getKryo().register(PlayerAddEvent.class);
        this.server.getKryo().register(PlayerRemoveEvent.class);
        this.server.getKryo().register(WeaponBuyEvent.class);
        this.server.getKryo().register(WeaponChangeEvent.class);
        this.server.getKryo().register(MoneyAdditionEvent.class);
        this.server.getKryo().register(BulletsBuyEvent.class);
        this.server.getKryo().register(EnemyAddEvent.class);
        this.server.getKryo().register(EnemyTakeDamageEvent.class);
        this.server.getKryo().register(EnemyDeadEvent.class);
        this.server.getKryo().register(WaveClearedEvent.class);
        this.server.getKryo().register(EnemyMoveEvent.class);
        this.server.getKryo().register(PlayerTakeDamageEvent.class);
        this.server.getKryo().register(StartGameEvent.class);
        this.server.getKryo().register(PlayerDeadEvent.class);
        this.server.getKryo().register(GameEndEvent.class);
        this.server.getKryo().register(WaveInfoEvent.class);

        this.server.getKryo().register(CreateLobbyEvent.class);
        this.server.getKryo().register(LobbyInfoEvent.class);
        this.server.getKryo().register(JoinTheLobbyEvent.class);
        this.server.getKryo().register(LobbyLeaveEvent.class);
        this.server.getKryo().register(LobbyRemoveEvent.class);
        this.server.getKryo().register(AdminChangeEvent.class);

        this.server.getKryo().register(String.class);
        this.server.getKryo().register(Integer.class);
        this.server.getKryo().register(Long.class);
        this.server.getKryo().register(float[].class);
        this.server.getKryo().register(EnemyType.class);
        this.server.getKryo().register(WeaponType.class);

        this.server.getKryo().register(List.class);
        this.server.getKryo().register(ArrayList.class);

        this.server.getKryo().register(Matrix4.class);
        this.server.getKryo().register(Vector3.class);
        this.server.getKryo().register(Lobby.class);

        this.server.addListener(new JoinListener());
        this.server.addListener(new MoveUpdateListener());
        this.server.addListener(new LeaveListener());
        this.server.addListener(new ShopListener());
        this.server.addListener(new WeaponListener());
        this.server.addListener(new ServerEnemyListener());
        this.server.addListener(new StartGameListener());
        this.server.addListener(new ServerLobbyListener());

        ServerEnemyUpdateHandler.getInstance().start();
        this.bindServer(8085, 8085);
    }

    private void bindServer(int tcpPort, int udpPort) {
        this.server.start();

        try {
            this.server.bind(tcpPort, udpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server getServer() {
        return server;
    }

}
