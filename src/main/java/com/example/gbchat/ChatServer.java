package com.example.gbchat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class ChatServer {
    private final String SERVERLOGFILENAME = "serverLog.txt";
    private final Map<String, ClientHandler> clients;

    private final AuthService authService;

    public ChatServer() {
        clients = new HashMap<>();
        authService = new InMemoryAuthService();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(55555)) {
            while (true) {
                System.out.println("Ждем подключения...");
                final Socket socket = serverSocket.accept();
                new ClientHandler(socket, this);
                System.out.println("Клиент подключился");
                createLogFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler client) {
        clients.put(client.getNick(), client);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client.getNick());
        broadcastClientList();
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
        addLogString(nick+": "+message);
    }

    public void systemMessage(String nick, String message) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(nick+Commands.TAB+ message);
        }
        addLogString(nick+Commands.TAB+message);
    }

    public void personalMessage(String destNick, String nick, String message) {
        for (ClientHandler client : clients.values()) {
            if (client.getNick().equals(nick)) {
                client.sendMessage("Личное сообщение от " + destNick + ": " + message.substring(Commands.PRIVATE_MESSAGE.length() + 2 + destNick.length()));
            }
        }
    }

    public void broadcastClientList() {
        final String message = clients.values().stream().map(ClientHandler::getNick).collect(Collectors.joining(Commands.TAB));
        systemMessage(Commands.CLIENTS, message);
    }

    public void createLogFile() {
        File file = new File(SERVERLOGFILENAME);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addLogString(String message){
        FileWriter writer = null;
        try {
            writer = new FileWriter(SERVERLOGFILENAME, true);
            writer.write("\n"+message);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
