module com.example.blockchain_desktop_manager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.blockchain_desktop_manager to javafx.fxml;
    exports com.example.blockchain_desktop_manager;
}