package com.wabs.server.handlers;

import com.esotericsoftware.kryonet.Connection;
import com.wabs.server.User;

import java.util.ArrayList;
import java.util.List;

public class UserHandler {

    private static UserHandler INSTANCE = null;

    List<User> userList = new ArrayList<>();

    private UserHandler() {

    }

    public static UserHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserHandler();
        }
        return INSTANCE;
    }

    public Connection getConnectionByUsername(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return user.getConnection();
            }
        }
        return null;
    }

    public void addUser(User user) {
        userList.add(user);
    }

    public void removeUser(User user) {
        userList.remove(user);
    }

    public void removeUserByUsername(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                userList.remove(user);
                break;
            }
        }
    }

    public User getUserByUsername(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public User getUserByConnection(Connection connection) {
        for (User user : userList) {
            if (user.getConnection().equals(connection)) {
                return user;
            }
        }
        return null;
    }
}
