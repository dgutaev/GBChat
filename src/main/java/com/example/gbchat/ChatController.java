package com.example.gbchat;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
    public TextArea chatDisplay;
    public TextField inputArea;
    final ChatClient client;

    public ChatController() {
        client = new ChatClient(this);
    }

    @FXML
    protected void onSendButtonClick() {
        final String message = inputArea.getText();
        if (message != null && !message.isEmpty()) {
            client.sendMessage(message);
            inputArea.clear();
            inputArea.requestFocus();
        }
    }

    public void addMessage(String message) {
        chatDisplay.appendText(message + "\n");
    }
}