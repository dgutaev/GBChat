package com.example.gbchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {

    private final Map<String, ClientHandler> clients;

    private final AuthService authService;

    public ChatServer() {
        clients = new HashMap<>();
        authService = new InMemoryAuthService();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                System.out.println("Ждем подключения...");
                final Socket socket = serverSocket.accept();
                new ClientHandler(socket, this);
                System.out.println("Клиент подключился");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler client) {
        clients.put(client.getNick(), client);
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client.getNick());
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isNickBusy(String nick) {
        return clients.containsKey(nick);
    }

    public void broadcast(String nick, String message) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(nick + ": " + message);
        }
    }

    public void personalMessage(String destNick, String nick, String message) {
        for (ClientHandler client : clients.values()) {
            if (client.getNick().equals(nick)) {
                client.sendMessage("Личное сообщение от " + destNick + ": " + message);
            }
        }
    }
}
