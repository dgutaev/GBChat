package com.example.gbchat;

import java.sql.SQLException;

public interface AuthService {
    String getNickByLoginAndPassword(String login, String password) throws SQLException;
    boolean login(String login, String password, String nick );
}
