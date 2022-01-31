package com.example.gbchat;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthService implements AuthService {

    private final List<UserData> users;

    public InMemoryAuthService() {
        users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(new UserData("login" + i, "pass" + i, "nick" + i));
        }
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        for (UserData user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) return user.getNick();
        }
        return null;
    }

    private static class UserData {
        private final String login;

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getNick() {
            return nick;
        }

        private final String password;
        private final String nick;

        private UserData(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }
}
