package com.example.gbchat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class ChatServer {
    private final String SERVERLOGFILENAME = "serverLog.txt";
    private ArrayList<String> logList = new ArrayList<>();
    private final Map<String, ClientHandler> clients;
    private static int LOGSIZE = 10;

    private final AuthService authService;

    public ChatServer() {
        clients = new HashMap<>();
        authService = new InMemoryAuthService();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(55555)) {
            while (true) {
                createLogFile();
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
        addLogString(nick + ": " + message);
    }

    public void systemMessage(String nick, String message) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(nick + message);
        }
        addLogString(nick + Commands.TAB + message);
    }

    public void personalLogMessage(String nick) throws IOException {
        for (ClientHandler client : clients.values()) {
            if (client.getNick().equals(nick)) {
                BufferedReader reader = new BufferedReader(new FileReader(SERVERLOGFILENAME));
                String line;
                while ((line = reader.readLine()) != null) {
                    logList.add(line);
                }
                if (logList.size() < LOGSIZE) {
                    for (String s : logList)
                        client.sendMessage(s);
                } else {
                    for (int i = logList.size() - LOGSIZE; i < logList.size(); i++) {
                        client.sendMessage(logList.get(i));
                    }
                }
            }
        }
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

    public void addLogString(String message) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(SERVERLOGFILENAME, true);
            writer.write("\n" + message);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
