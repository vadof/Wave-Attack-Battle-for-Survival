package com.wabs.server.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wabs.server.gameplay.GameLobby;
import com.wabs.server.gameplay.GameplayHandler;
import com.wabs.server.handlers.GameLobbyHandler;
import com.wabs.server.User;
import com.wabs.server.handlers.UserHandler;
import com.wabs.server.handlers.ServerLobbyHandler;

public class LeaveListener extends Listener {
    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);

        User leaveUser = UserHandler.getInstance().getUserByConnection(connection);

        if (leaveUser != null) {
            if (leaveUser.isCurrentlyPlaying()) {
                GameLobby gameLobby = GameLobbyHandler.getInstance().getGameLobbyById(leaveUser.getCurrentLobbyId());
                gameLobby.removePlayer(gameLobby.getPlayerByUsername(leaveUser.getUsername()));

                if (gameLobby.getPlayers().size() == 0) {
                    GameLobbyHandler.getInstance().removeGameLobby(gameLobby);
                }
            } else if (leaveUser.getCurrentLobbyId() != null) {
                ServerLobbyHandler.getInstance().removeUserFromLobby(leaveUser.getUsername(), leaveUser.getCurrentLobbyId());
            }
            UserHandler.getInstance().removeUser(leaveUser);
        }
    }

}
