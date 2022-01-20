package com.example.gbchat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Objects;

public class HelloController {
    String displayString;
    public TextArea chatDisplay;
    public TextField inputArea;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onSendButtonClick() {
        if (!Objects.equals(inputArea.getText(), "")) {
            displayString = displayString + "\n" + inputArea.getText();
            chatDisplay.setText(displayString);
            inputArea.setText("");
        }
    }
}