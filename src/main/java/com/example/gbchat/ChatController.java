package com.example.gbchat;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
    public TextArea chatDisplay;
    public TextField inputArea;

    @FXML
    protected void onSendButtonClick() {
        String stringMessage = inputArea.getText();
        if (stringMessage != null && !stringMessage.isEmpty()) {
            if (chatDisplay.getText().isEmpty()) {
                chatDisplay.setText(stringMessage);
            } else {
                chatDisplay.setText(chatDisplay.getText() + "\n" + stringMessage);
            }
            inputArea.setText("");
        }
    }
}