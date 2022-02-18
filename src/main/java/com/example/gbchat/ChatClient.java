package com.example.gbchat;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final ChatController controller;
    private String logFileName = "";

    public ChatClient(ChatController controller) {
        this.controller = controller;
        openConnection();
    }

    private void openConnection() {
        try {
            socket = new Socket("localhost", 55555);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                     while (true) {
                        final String authMsg = in.readUTF();
                        if (authMsg.startsWith(Commands.AUTOK)) {
                            final String nick = authMsg.split(Commands.TAB)[1];
                            controller.addMessage("Успешная авторизация под ником " + nick);
                            createLogFile(nick); //создание лога
                            controller.setAuth(true);
                            break;
                        }
                    }
                    while (true) {
                        final String message = in.readUTF();
                        if (Commands.END.equals(message)) {
                            controller.setAuth(false);
                            break;
                        }
                        if (message.startsWith(Commands.CLIENTS)) {
                            final String[] clients = message.replace(Commands.CLIENTS, "").split(Commands.TAB);
                            controller.updateClientsList(clients);
                        }
                        controller.addMessage(message);
                        FileWriter writer = new FileWriter(logFileName, true);
                        writer.write("\n"+message);
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }

            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createLogFile(String nick) {
        logFileName = "history_" + nick + ".txt";
        File file = new File(logFileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
