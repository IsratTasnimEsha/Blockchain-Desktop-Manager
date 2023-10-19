module com.example.blockchain_desktop_manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.blockchain_desktop_manager to javafx.fxml;
    exports com.example.blockchain_desktop_manager;
}