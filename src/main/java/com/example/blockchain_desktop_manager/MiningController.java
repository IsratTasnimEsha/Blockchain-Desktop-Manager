package com.example.blockchain_desktop_manager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class MiningController implements Initializable {

    @FXML
    private TextField receiver;

    @FXML
    private TextField amount;

    @FXML
    private TextField fees;

    @FXML
    private Label name;

    @FXML
    private Button add;

    @FXML
    private Button confirm;

    @FXML
    private TableView<Transactions> t_table;

    @FXML
    private TableColumn<Transactions, Integer> idcol;

    @FXML
    private TableColumn<Transactions, String> sendercol;

    @FXML
    private TableColumn<Transactions, String> receivercol;
public String usrname =null;
    @FXML
    private TableColumn<Transactions, String> amountcol;

    @FXML
    private TableColumn<Transactions, String> feescol;

    @FXML
    private TableColumn<Transactions, String> verifycol;

    ObservableList<Transactions> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        LoadT_Table();

        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Parent root =null;



                try {
                    FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("block-view.fxml"));
                    root =loader.load();


                    blockController blkController = loader.getController();
                    blkController.setMinername(name.getText());
                } catch (IOException e){
                    e.printStackTrace();
                }


                Stage stage =(Stage) ((Node)event.getSource()).getScene().getWindow();

                stage.setScene(new Scene(root));

                stage.show();



            }
        });

        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (!receiver.getText().trim().isEmpty() && !amount.getText().trim().isEmpty()) {
                    DBUtils.AddTransaction(event, name.getText(), receiver.getText(), amount.getText(), fees.getText(), "Unrecognized");

                }
            }
        });
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> refreshTable()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refreshTable() {
        list.clear();

        try {
            String mysqlUrl = "jdbc:mysql://localhost:3306/blockchain";
            Connection con = DriverManager.getConnection(mysqlUrl, "root", "1234");

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String query = "select * from transaction where (temp_block='' or temp_block is null) and (block_no='' or block_no is NULL)";

            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                Transactions transaction = new Transactions(
                        resultSet.getInt("id"),
                        resultSet.getString("sender"),
                        resultSet.getString("receiver"),
                        resultSet.getString("amount"),
                        resultSet.getString("fees"),
                        resultSet.getString("verify")
                );

                list.add(transaction);
            }

            t_table.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setUserInformation(String username ){

        name.setText(username+"");

}
    public void LoadT_Table() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        sendercol.setCellValueFactory(new PropertyValueFactory<>("sender"));
        receivercol.setCellValueFactory(new PropertyValueFactory<>("receiver"));
        amountcol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        feescol.setCellValueFactory(new PropertyValueFactory<>("fees"));
        verifycol.setCellValueFactory(new PropertyValueFactory<>("verify"));

        TableColumn<Transactions, Void> addToBlockCol = new TableColumn<>("Add to Block");
        addToBlockCol.setSortable(false);
        addToBlockCol.setMinWidth(100);

        addToBlockCol.setCellFactory(param -> new TableCell<Transactions, Void>() {
            private final Button addToBlockButton = new Button();
            {
                addToBlockButton.setOnAction(event -> {
                    Transactions transaction = getTableView().getItems().get(getIndex());
                    String verifyStatus = transaction.getVerify();

                    if (Objects.equals(verifyStatus, "Unrecognized")) {
                        addToBlockButton.setText("Verify");
                        makeVerified(transaction); // Call makeVerified() when Verify is clicked
                    } else {
                        addToBlockButton.setText("Add To Block");
                        if (Objects.equals(verifyStatus, "Verified")) {
                            addToTempBlock(transaction);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Transactions transaction = getTableView().getItems().get(getIndex());
                    String verifyStatus = transaction.getVerify();

                    if (Objects.equals(verifyStatus, "Unrecognized")) {
                        addToBlockButton.setText("Verify");
                    } else {
                        addToBlockButton.setText("Add To Block");
                    }

                    setGraphic(addToBlockButton);
                }
            }
        });

        t_table.getColumns().addAll(addToBlockCol);

        ResultSet resultSet = null;

        try {
            String mysqlUrl = "jdbc:mysql://localhost:3306/blockchain";
            Connection con = DriverManager.getConnection(mysqlUrl, "root", "1234");

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String query = "select * from transaction where (temp_block='' or temp_block is null) and (block_no='' or block_no is NULL)";

            resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                Transactions transaction = new Transactions(
                        resultSet.getInt("id"),
                        resultSet.getString("sender"),
                        resultSet.getString("receiver"),
                        resultSet.getString("amount"),
                        resultSet.getString("fees"),
                        resultSet.getString("verify")
                );

                list.add(transaction);
            }

            t_table.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void makeVerified(Transactions transaction) {
        try {
            String mysqlUrl = "jdbc:mysql://localhost:3306/blockchain";
            Connection con = DriverManager.getConnection(mysqlUrl, "root", "1234");

            String updateQuery = "UPDATE transaction SET verify = 'Verified' WHERE id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(updateQuery);
            preparedStatement.setInt(1, transaction.getId());
            preparedStatement.executeUpdate();

            preparedStatement.close();
            con.close();

            transaction.setVerify("Verified");

            t_table.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToTempBlock(Transactions transaction) {
        try {
            String mysqlUrl = "jdbc:mysql://localhost:3306/blockchain";
            Connection con = DriverManager.getConnection(mysqlUrl, "root", "1234");

            String updateQuery = "UPDATE transaction SET temp_block = ? WHERE id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(updateQuery);
            preparedStatement.setString(1, name.getText());
            preparedStatement.setInt(2, transaction.getId());
            preparedStatement.executeUpdate();

            t_table.refresh();

            preparedStatement.close();
            con.close();

            list.remove(transaction);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}