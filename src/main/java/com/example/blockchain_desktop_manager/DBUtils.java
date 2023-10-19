package com.example.blockchain_desktop_manager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class DBUtils {


    public static void logInUser(ActionEvent event,String username,String password){

        Connection connection = null ;
        PreparedStatement preparedStatement = null ;

        ResultSet resultSet =null;
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/blockchain", "root", "1234");
            preparedStatement = connection.prepareStatement("SELECT pass FROM signup WHERE username = ?");
            preparedStatement.setString(1,username);
            resultSet =preparedStatement.executeQuery();

            if(!resultSet.isBeforeFirst()){
                Rectangle2D bounds = Screen.getPrimary().getBounds();


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Title");
                alert.setHeaderText("Wrong Information!");
                alert.setContentText("Username not found in database !");
                alert.getDialogPane().setPrefSize(300, 120);

                alert.setX(bounds.getMaxX()-820);
                alert.setY(bounds.getMaxY()-520);

                alert.initStyle(StageStyle.UNDECORATED);

                Thread thread = new Thread(() -> {
                    try {
                        // Wait for 5 secs
                        Thread.sleep(2000);
                        if (alert.isShowing()) {
                            Platform.runLater(() -> alert.close());
                        }
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                });
                thread.setDaemon(true);
                thread.start();
                Optional<ButtonType> result = alert.showAndWait();
            } else {
                while (resultSet.next()){
                    String retrievedPassword  =resultSet.getString("pass");

                    if(retrievedPassword.equals(password) ){
                        Parent root =null;



                        try {
                            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("mining-view.fxml"));
                            root =loader.load();


                            MiningController loggedinController = loader.getController();
                            loggedinController.setUserInformation(username);
                        } catch (IOException e){
                            e.printStackTrace();
                        }


                        Stage stage =(Stage) ((Node)event.getSource()).getScene().getWindow();

                        stage.setScene(new Scene(root));

                        stage.show();

                    } else {
                        Rectangle2D bounds = Screen.getPrimary().getBounds();


                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Title");
                        alert.setHeaderText("Wrong Information!");
                        alert.setContentText("Password  didn't match !");
                        alert.getDialogPane().setPrefSize(300, 120);

                        alert.setX(bounds.getMaxX()-820);
                        alert.setY(bounds.getMaxY()-520);
                        alert.initStyle(StageStyle.UNDECORATED);

                        Thread thread = new Thread(() -> {
                            try {
                                // Wait for 5 secs
                                Thread.sleep(2000);
                                if (alert.isShowing()) {
                                    Platform.runLater(() -> alert.close());
                                }
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }
                        });
                        thread.setDaemon(true);
                        thread.start();
                        Optional<ButtonType> result = alert.showAndWait();

                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
  }

}

public  static  void signUpUser(ActionEvent event,String username, String pass,String confirm_pass){

        Connection connection = null ;
        PreparedStatement psCheckUserExist = null ;
        PreparedStatement psInsert = null ;
        ResultSet resultSet =null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/blockchain", "root", "1234");
            psCheckUserExist = connection.prepareStatement("SELECT * FROM signup WHERE username = ?");
            psCheckUserExist.setString(1, username);
            resultSet = psCheckUserExist.executeQuery();

            if (resultSet.isBeforeFirst()) {

                Rectangle2D bounds = Screen.getPrimary().getBounds();


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Title");
                alert.setHeaderText("Wrong Information!");
                alert.setContentText("This Username Is Already Exist !");
                alert.getDialogPane().setPrefSize(300, 120);

                alert.setX(bounds.getMaxX()-820);
                alert.setY(bounds.getMaxY()-520);

                alert.initStyle(StageStyle.UNDECORATED);

                Thread thread = new Thread(() -> {
                    try {
                        // Wait for 5 secs
                        Thread.sleep(2000);
                        if (alert.isShowing()) {
                            Platform.runLater(() -> alert.close());
                        }
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                });
                thread.setDaemon(true);
                thread.start();
                Optional<ButtonType> result = alert.showAndWait();
            } else {
                psInsert = connection.prepareStatement("INSERT INTO signup (username, pass) VALUES (?, ?)");
                psInsert.setString(1, username);
                psInsert.setString(2, pass);

                psInsert.executeUpdate();

                changeScene(event, "login-view.fxml");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(psCheckUserExist != null){
                try {
                    psCheckUserExist.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(psInsert != null){
                try {
                    psInsert.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }


    public static void AddTransaction(ActionEvent event, String sender, String receiver, String amount, String fees, String verify){
        Connection connection = null ;
        PreparedStatement psCheckUserExist = null ;
        PreparedStatement psInsert = null ;
        ResultSet resultSet =null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/blockchain", "root", "1234");

            psInsert = connection.prepareStatement("INSERT INTO transaction (sender, receiver, amount, fees, verify) VALUES (?, ?, ?, ?, ?)");
            psInsert.setString(1, sender);
            psInsert.setString(2, receiver);
            psInsert.setString(3, amount);
            psInsert.setString(4, fees);
            psInsert.setString(5, verify);

            psInsert.executeUpdate();

            Parent root =null;

            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("mining-view.fxml"));
                root =loader.load();


                MiningController loggedinController = loader.getController();
                loggedinController.setUserInformation(sender);
            } catch (IOException e){
                e.printStackTrace();
            }


            Stage stage =(Stage) ((Node)event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));

            stage.show();

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(psCheckUserExist != null){
                try {
                    psCheckUserExist.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(psInsert != null){
                try {
                    psInsert.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }


    public static void changeScene(ActionEvent event, String fxmlFile){
        Parent root =null;

            try {
                root =FXMLLoader.load(DBUtils.class.getResource(fxmlFile));

            } catch (IOException e){
                e.printStackTrace();
            }

        Stage stage =(Stage) ((Node)event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }
}