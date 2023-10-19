package com.example.blockchain_desktop_manager;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    @FXML
    private Button btn_login;

    @FXML
    private Button btn_signup;

    @FXML
    private TextField pass;

    @FXML
    private TextField confirm_pass;

    @FXML
    private TextField username;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btn_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event,"login-view.fxml");
            }
        });
        btn_signup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!username.getText().trim().isEmpty() && !pass.getText().trim().isEmpty() && !confirm_pass.getText().trim().isEmpty() && (pass.getText().trim().equals( confirm_pass.getText().trim()))){
                    DBUtils.signUpUser(event,username.getText(),pass.getText(),confirm_pass.getText());
                } else {
                    System.out.println("Please fill in all Information Appropriately");
                    Alert alert =new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Please Fill Up All Information Appropriately");
                    alert.show();
                }}});
    }
}