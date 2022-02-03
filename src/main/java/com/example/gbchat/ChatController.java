package com.example.gbchat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.Arrays;
import java.util.List;

public class ChatController {
    public TextArea chatDisplay;
    public TextField inputArea;
    final ChatClient client;
    @FXML
    private HBox loginBox;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private HBox messageBox;
    @FXML
    private ListView<String> clientList;

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

    public void bthAuthClick(ActionEvent actionEvent) {
        client.sendMessage("/auth" + Commands.TAB + loginField.getText() + Commands.TAB + passwordField.getText());
    }

    public void setAuth(boolean isAuthSuccess) {
        loginBox.setVisible(!isAuthSuccess);
        messageBox.setVisible(isAuthSuccess);
    }

    public void selectClient(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount()==2) {
            final String message = inputArea.getText();
            clientList.getSelectionModel().getSelectedItems();
            inputArea.setText("/w"+client+Commands.TAB+message);
            inputArea.requestFocus();
            inputArea.selectEnd();
        }
    }

    public void updateClientsList(String[] clients) {
        clientList.getItems().clear();
        clientList.getItems().addAll(clients);
    }
}