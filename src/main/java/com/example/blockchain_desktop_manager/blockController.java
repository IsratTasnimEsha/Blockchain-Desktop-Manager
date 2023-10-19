package com.example.blockchain_desktop_manager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class blockController implements Initializable {

    @FXML
    private TableView<blockinfo> t_table;
    @FXML
    private Label minername;

    @FXML
    private TableColumn<blockinfo, Integer> idcol;

    @FXML
    private TableColumn<blockinfo, String> sendercol;

    @FXML
    private TableColumn<blockinfo, String> receivercol;

    @FXML
    private TableColumn<blockinfo, String> amountcol;

    @FXML
    private TableColumn<blockinfo, String> feescol;

    @FXML
    private Button mine;

    @FXML
    private Button back;

    @FXML
    private Button cancel;
    ObservableList<blockinfo> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        LoadT_Table();

        mine.setOnAction(event -> mineDataAndHash(event));
        cancel.setOnAction(event -> cancelMining(event));
        back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event,"mining-view.fxml");
            }
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> refreshTable()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    public void setMinername(String username){
        minername.setText(username+"");
    }
    private void refreshTable() {
        list.clear();

        try {
            String mysqlUrl = "jdbc:mysql://localhost:3306/blockchain";
            Connection con = DriverManager.getConnection(mysqlUrl, "root", "1234");

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String query = "SELECT * FROM transaction where temp_block = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, minername.getText());

            ResultSet resultSet = preparedStatement.executeQuery(); // Use the prepared statement here

            while (resultSet.next()) {
                blockinfo transaction = new blockinfo(
                        resultSet.getInt("id"),
                        resultSet.getString("sender"),
                        resultSet.getString("receiver"),
                        resultSet.getString("amount"),
                        resultSet.getString("fees")
                );

                list.add(transaction);
            }

            t_table.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cancelMining(ActionEvent event) {
        try {
            String mysqlUrl = "jdbc:mysql://localhost:3306/blockchain";
            Connection con = DriverManager.getConnection(mysqlUrl, "root", "1234");

            String updateQuery = "UPDATE transaction SET temp_block = '', verify = 'Unrecognized' WHERE temp_block = ?";
            PreparedStatement preparedStatement = con.prepareStatement(updateQuery);
            preparedStatement.setString(1, minername.getText());
            preparedStatement.executeUpdate();

            preparedStatement.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DBUtils.changeScene(event,"mining-view.fxml");
    }

    public void LoadT_Table() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        sendercol.setCellValueFactory(new PropertyValueFactory<>("sender"));
        receivercol.setCellValueFactory(new PropertyValueFactory<>("receiver"));
        amountcol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        feescol.setCellValueFactory(new PropertyValueFactory<>("fees"));

        TableColumn<blockinfo, Void> removeFromBlockCol = new TableColumn<>("Remove From Block");
        removeFromBlockCol.setSortable(false);
        removeFromBlockCol.setMinWidth(100);

        removeFromBlockCol.setCellFactory(param -> new TableCell<blockinfo, Void>() {
            private final Button removeFromBlockButton = new Button("Remove From Block");
            {
                removeFromBlockButton.setOnAction(event -> {
                    blockinfo transaction = getTableView().getItems().get(getIndex());
                    removeFromTempBlock(transaction);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    blockinfo transaction = getTableView().getItems().get(getIndex());
                    setGraphic(removeFromBlockButton);
                }
            }
        });

        t_table.getColumns().addAll(removeFromBlockCol);

        ResultSet resultSet = null;

        try {
            String mysqlUrl = "jdbc:mysql://localhost:3306/blockchain";
            Connection con = DriverManager.getConnection(mysqlUrl, "root", "1234");

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String query = "SELECT * FROM transaction where temp_block = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, minername.getText());
            resultSet = preparedStatement.executeQuery(); // Use the prepared statement here

            while (resultSet.next()) {
                blockinfo transaction = new blockinfo(
                        resultSet.getInt("id"),
                        resultSet.getString("sender"),
                        resultSet.getString("receiver"),
                        resultSet.getString("amount"),
                        resultSet.getString("fees")
                );

                list.add(transaction);
            }

            t_table.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeFromTempBlock(blockinfo transaction) {
        try {
            String mysqlUrl = "jdbc:mysql://localhost:3306/blockchain";
            Connection con = DriverManager.getConnection(mysqlUrl, "root", "1234");

            String updateQuery = "UPDATE transaction SET temp_block = ?, verify = 'Unrecognized' WHERE id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(updateQuery);
            preparedStatement.setString(1, "");
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

    private String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addToBlock(String concatenatedData, String nonce, String hashedData, ActionEvent event) {
        try {
            String mysqlUrl = "jdbc:mysql://localhost:3306/blockchain";
            Connection con = DriverManager.getConnection(mysqlUrl, "root", "1234");

            String getLastBlockNoQuery = "SELECT MAX(block_no) FROM block";
            PreparedStatement getLastBlockNoStatement = con.prepareStatement(getLastBlockNoQuery);
            ResultSet resultSet = getLastBlockNoStatement.executeQuery();

            int lastBlockNo = 0;

            if (resultSet.next()) {
                lastBlockNo = resultSet.getInt(1);
            }

            String getPrevHashQuery = "SELECT hash FROM block WHERE block_no = ?";
            PreparedStatement getPrevHashStatement = con.prepareStatement(getPrevHashQuery);
            getPrevHashStatement.setInt(1, lastBlockNo);

            ResultSet prevHashResultSet = getPrevHashStatement.executeQuery();

            String prevHashedData = "";

            if (prevHashResultSet.next()) {
                prevHashedData = prevHashResultSet.getString("hash");
            }

            long minedTime = System.currentTimeMillis();

            int numberOfTransactions = list.size();

            int blockSize = concatenatedData.length();

            BigDecimal totalSent = BigDecimal.ZERO;
            for (blockinfo transaction : list) {
                BigDecimal amount = new BigDecimal(transaction.getAmount());
                totalSent = totalSent.add(amount);
            }

            BigDecimal fees = BigDecimal.ZERO;
            for (blockinfo transaction : list) {
                BigDecimal fee = new BigDecimal(transaction.getFees());
                fees = fees.add(fee);
            }

            String insertQuery = "INSERT INTO block (nonce, prev_hash, hash, miner, mined, no_of_transactions, size, total_sent, fees) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(insertQuery);
            preparedStatement.setString(1, nonce);
            preparedStatement.setString(2, prevHashedData);
            preparedStatement.setString(3, hashedData);
            preparedStatement.setString(4, minername.getText());
            preparedStatement.setLong(5, minedTime);
            preparedStatement.setInt(6, numberOfTransactions);
            preparedStatement.setInt(7, blockSize);
            preparedStatement.setBigDecimal(8, totalSent);
            preparedStatement.setBigDecimal(9, fees);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            getPrevHashStatement.close();
            getLastBlockNoStatement.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DBUtils.changeScene(event, "mining-view.fxml");
    }

    private void mineDataAndHash(ActionEvent event) {
        SecureRandom random = new SecureRandom();
        String desiredPrefix = "000";
        String nonce;
        String concatenatedData = "";
        String hashedData;

        do {
            nonce = String.format("%05d", random.nextInt(100000));

            StringBuilder concatenatedDataBuilder = new StringBuilder();
            for (blockinfo transaction : list) {
                String t_id = Integer.toString(transaction.getId());
                concatenatedDataBuilder.append(t_id);
                concatenatedDataBuilder.append(transaction.getSender());
                concatenatedDataBuilder.append(transaction.getReceiver());
                concatenatedDataBuilder.append(transaction.getAmount());
                concatenatedDataBuilder.append(transaction.getFees());
            }
            concatenatedData = concatenatedDataBuilder.toString();

            hashedData = hashSHA256(nonce + concatenatedData);
        } while (!hashedData.startsWith(desiredPrefix));

        updateTempBlockStatus();

        addToBlock(concatenatedData, nonce, hashedData, event);
    }

    private void updateTempBlockStatus() {
        try {
            String mysqlUrl = "jdbc:mysql://localhost:3306/blockchain";
            Connection con = DriverManager.getConnection(mysqlUrl, "root", "1234");

            String getLastBlockNoQuery = "SELECT MAX(block_no) FROM block";
            PreparedStatement getLastBlockNoStatement = con.prepareStatement(getLastBlockNoQuery);
            ResultSet resultSet = getLastBlockNoStatement.executeQuery();

            int lastBlockNo = 0;

            if (resultSet.next()) {
                lastBlockNo = resultSet.getInt(1);
            }

            String updateTempBlockQuery = "UPDATE transaction SET temp_block = 'Blocked', block_no = ? WHERE temp_block = ?";
            PreparedStatement updateTempBlockStatement = con.prepareStatement(updateTempBlockQuery);
            updateTempBlockStatement.setInt(1, lastBlockNo + 1);
            updateTempBlockStatement.setString(2, minername.getText());
            updateTempBlockStatement.executeUpdate();

            updateTempBlockStatement.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}