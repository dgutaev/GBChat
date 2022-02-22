module com.example.gbchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.gbchat to javafx.fxml;
    exports com.example.gbchat;
}