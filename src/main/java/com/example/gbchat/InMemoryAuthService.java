package com.example.gbchat;

import java.sql.*;

public class InMemoryAuthService implements AuthService {

    private final Connection connect;

    public InMemoryAuthService() {
        connect = connect();
    }

    private Connection connect() {
        try {
            try {
                return DriverManager.getConnection("jdbc:sqlite:clientsDB.db");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) throws SQLException {
        try (final PreparedStatement ps = connect.prepareStatement("select nick from clients where login = ? and password = ?")) {
            ps.setString(1, login);
            ps.setString(2, password);
            final ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        }
    }

    @Override
    public boolean login(String login, String password, String nick) {
        try (final PreparedStatement ps = connect.prepareStatement("insert into client (login, password, nick) values ?, ?, ?")) {
            ps.setString(1, login);
            ps.setString(2, password);
            ps.setString(3, nick);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
