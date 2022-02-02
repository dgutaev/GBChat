package com.example.gbchat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler {
    private final Socket socket;
    private final ChatServer chatServer;
    private final DataInputStream in;
    private final DataOutputStream out;
    public final static String TAB = " ";
    private String nick;

    public ClientHandler(Socket socket, ChatServer chatServer) {
        try {
            this.nick = "";
            this.socket = socket;
            this.chatServer = chatServer;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    authenticate();
                    readMessage();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                chatServer.unsubscribe(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMessage() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        try {
            while (true) {
                final String message = in.readUTF();
                if ("/end".equals(message)) {
                    break;
                }
                if (message.startsWith("/w")) {
                    final String[] split = message.split(TAB);
                    final String destNick = split[1];
                    final String personalMessage = split[2];
                    chatServer.personalMessage(nick, destNick, "(" + dateFormat.format(date) + ") " + personalMessage);
                } else {
                    chatServer.broadcast(nick, "(" + dateFormat.format(date) + ") " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authenticate() {
        while (true) {
            try {
                final String message = in.readUTF();
                if (message.startsWith("/auth")) {
                    final String[] split = message.split(TAB);
                    final String login = split[1];
                    final String password = split[2];
                    final String nick = chatServer.getAuthService().getNickByLoginAndPassword(login, password);
                    if (nick != null) {
                        if (chatServer.isNickBusy(nick)) {
                            sendMessage("Пользователь уже авторизован");
                            continue;
                        }
                        sendMessage("/authok" + TAB + nick);
                        this.nick = nick;
                        chatServer.broadcast(nick, "зашел в чат");
                        chatServer.subscribe(this);
                        break;
                    } else {
                        sendMessage("Неверные логин и пароль");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            if (message.startsWith("/authok")) {
                out.writeUTF(message);
            } else {
                System.out.println(nick);
                out.writeUTF(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }

}
