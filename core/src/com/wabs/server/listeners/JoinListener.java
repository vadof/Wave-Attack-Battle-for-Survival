package com.wabs.server.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.global.lobbyEvents.LobbyInfoEvent;
import com.wabs.global.playerEvents.JoinRequestEvent;
import com.wabs.global.playerEvents.JoinResponseEvent;
import com.wabs.server.User;
import com.wabs.server.handlers.UserHandler;
import com.wabs.server.database.HibernateUtil;
import com.wabs.server.handlers.ServerLobbyHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JoinListener extends Listener {

    public static final List<Integer> id = new ArrayList<>(Arrays.asList(0, 1, 2));
    private final JoinResponseEvent joinResponseEvent = new JoinResponseEvent();

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof JoinRequestEvent) {
            JoinRequestEvent joinRequestEvent = (JoinRequestEvent) object;

//            if (!HibernateUtil.usernameAndPasswordIsValid(joinRequestEvent.username, joinRequestEvent.password)
//                    || UserHandler.getInstance().getConnectionByUsername(joinRequestEvent.username) != null) {
//                joinResponseEvent.access = false;
//            } else {
                joinResponseEvent.access = true;
                LobbyInfoEvent lobbyInfoEvent = new LobbyInfoEvent();
                lobbyInfoEvent.lobbyList = ServerLobbyHandler.getInstance().getLobbyList();

                UserHandler.getInstance().addUser(new User(joinRequestEvent.username, connection));
                connection.sendTCP(lobbyInfoEvent);
//            }

            connection.sendTCP(joinResponseEvent);
        }

        super.received(connection, object);

    }

    public static Integer getId() {
        if (id.size() > 0) {
            return id.remove(0);
        }
        return null;
    }
}
