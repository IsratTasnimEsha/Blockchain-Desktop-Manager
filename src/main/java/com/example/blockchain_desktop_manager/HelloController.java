package com.example.blockchain_desktop_manager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class HelloController {
    @FXML
    private TextField amount1, hash1, nonce1, block_no1, sender1, receiver1, prev_hash1;

    @FXML
    private Button mine1;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private HBox container;

    private String desiredPrefix = "00"; // Desired hash prefix

    public void initialize() {

        for (int i = 2; i <= 50; i++) {
            AnchorPane block = createBlock(i); // Create a new block
            block.setId("block" + i); // Set a unique fx:id for each block

            // Add the created block to the container
            container.getChildren().add(block);
            //block.setVisible(false); // Make the block invisible;
        }

        // Add listeners to the text properties of the relevant fields
        amount1.textProperty().addListener(new CalculateHashListener());
        block_no1.textProperty().addListener(new CalculateHashListener());
        nonce1.textProperty().addListener(new CalculateHashListener());
        sender1.textProperty().addListener(new CalculateHashListener());
        receiver1.textProperty().addListener(new CalculateHashListener());

        // Generate a random nonce and update the field
        generateRandomNoncesForAllBlocks();

        // Set up a handler for the mine button
        mine1.setOnAction(event -> {
            mineBlock();
        });
    }

    private AnchorPane createBlock(int blockNumber) {
        AnchorPane block = new AnchorPane();

        // Create a black border
        BorderStroke borderStroke = new BorderStroke(
                Color.BLACK, // Border color
                BorderStrokeStyle.SOLID, // Border style
                new CornerRadii(0), // Border radii (none in this case)
                new BorderWidths(0.4) // Border width
        );

        block.setBorder(new Border(borderStroke));

        block.setStyle("-fx-background-color: #f1f2f6;");

        // Create TextFields
        TextField amount = new TextField();
        amount.setLayoutX(66.0);
        amount.setLayoutY(248.0);
        amount.setPrefHeight(40.0);
        amount.setPrefWidth(564.0);
        amount.setId("amount" + blockNumber);

        TextField blockNo = new TextField();
        blockNo.setText("" + blockNumber);
        blockNo.setLayoutX(66.0);
        blockNo.setLayoutY(100.0);
        blockNo.setPrefHeight(40.0);
        blockNo.setPrefWidth(267.0);
        blockNo.setEditable(false);
        blockNo.setId("block_no" + blockNumber);

        TextField nonce = new TextField();
        nonce.setLayoutX(363.0);
        nonce.setLayoutY(100.0);
        nonce.setPrefHeight(40.0);
        nonce.setPrefWidth(267.0);
        nonce.setEditable(false); // Set as non-editable
        nonce.setId("nonce" + blockNumber);

        TextField receiver = new TextField();
        receiver.setLayoutX(363.0);
        receiver.setLayoutY(174.0);
        receiver.setPrefHeight(40.0);
        receiver.setPrefWidth(267.0);
        receiver.setId("receiver" + blockNumber);

        TextField sender = new TextField();
        sender.setLayoutX(66.0);
        sender.setLayoutY(174.0);
        sender.setPrefHeight(40.0);
        sender.setPrefWidth(267.0);
        sender.setId("sender" + blockNumber);

        TextField prevHash = new TextField();
        prevHash.setLayoutX(66.0);
        prevHash.setLayoutY(330.0);
        prevHash.setPrefHeight(40.0);
        prevHash.setPrefWidth(564.0);
        prevHash.setId("prev_hash" + blockNumber);

        TextField hash = new TextField();
        hash.setLayoutX(66.0);
        hash.setLayoutY(406.0);
        hash.setPrefHeight(40.0);
        hash.setPrefWidth(564.0);
        hash.setId("hash" + blockNumber);

        // Create Labels
        Label labelBlockNo = new Label("Block No.");
        labelBlockNo.setLayoutX(65.0);
        labelBlockNo.setLayoutY(70.0);

        Label labelNonce = new Label("Nonce");
        labelNonce.setLayoutX(358.0);
        labelNonce.setLayoutY(70.0);

        Label labelSender = new Label("Sender");
        labelSender.setLayoutX(70.0);
        labelSender.setLayoutY(146.0);

        Label labelReceiver = new Label("Receiver");
        labelReceiver.setLayoutX(363.0);
        labelReceiver.setLayoutY(146.0);

        Label labelAmount = new Label("Amount");
        labelAmount.setLayoutX(66.0);
        labelAmount.setLayoutY(221.0);

        Label labelPrevHash = new Label("Prev Hash");
        labelPrevHash.setLayoutX(67.0);
        labelPrevHash.setLayoutY(302.0);

        Label labelHash = new Label("Hash");
        labelHash.setLayoutX(66.0);
        labelHash.setLayoutY(377.0);

        // Create Button
        Button mineButton = new Button("MINE");
        mineButton.setLayoutX(287.0);
        mineButton.setLayoutY(470.0);
        mineButton.setMnemonicParsing(false);
        mineButton.setPrefHeight(54.0);
        mineButton.setPrefWidth(142.0);
        mineButton.setStyle("-fx-background-color: #3D4B66; -fx-cursor: hand;");
        mineButton.setTextFill(Color.WHITE);

        // Set font for the button
        mineButton.setFont(Font.font(19.0));

        // Add all elements to the block
        block.getChildren().addAll(
                amount, blockNo, nonce, receiver, sender, prevHash, hash,
                labelBlockNo, labelNonce, labelSender, labelReceiver, labelAmount, labelPrevHash, labelHash,
                mineButton
        );

        return block;
    }

    private class CalculateHashListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            // Calculate the SHA-256 hash based on block_no, nonce, sender, receiver, and amount
            String block_noText = block_no1.getText();
            String nonceText = nonce1.getText();
            String senderText = sender1.getText();
            String receiverText = receiver1.getText();
            String amountText = amount1.getText();

            String concatenatedText = block_noText + nonceText + senderText + receiverText + amountText;

            String hashedText = calculateSHA256(concatenatedText);

            // Set the hash value in the "hash" TextField
            hash1.setText(hashedText);

            // Check if the first three characters of the hash match the desired prefix
            if (hashedText.startsWith(desiredPrefix)) {
                disableMineButton(true); // Disable the "Mine" button
                createNewBlock(); // Create a new AnchorPane
            } else {
                disableMineButton(false); // Enable the "Mine" button
            }
        }
    }

    private void disableMineButton(boolean disable) {
        mine1.setDisable(disable);
    }

    private void generateRandomNoncesForAllBlocks() {
        Random random = new Random();

        for (int i = 1; i <= 50; i++) {
            String blockId = "block" + i;
            AnchorPane block = (AnchorPane) container.lookup("#" + blockId);

            if (block != null) {
                TextField nonceTextField = (TextField) block.lookup("#nonce" + i);
                if (nonceTextField != null) {
                    int randomNonce = random.nextInt(90000) + 10000; // Generate a random 5-digit number
                    nonceTextField.setText(String.valueOf(randomNonce));
                } else {
                    System.out.println("Nonce TextField not found in " + blockId);
                }
            } else {
                System.out.println("Block with fx:id '" + blockId + "' not found.");
            }
        }
    }

    private String calculateSHA256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(text.getBytes());

            // Convert the byte array to a hexadecimal string
            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = String.format("%02x", b);
                hexStringBuilder.append(hex);
            }

            return hexStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "Error calculating hash";
        }
    }

    private void mineBlock() {
        while (true) {
            // Update the nonce
            generateRandomNoncesForAllBlocks();

            // Display the updated nonce value in the "nonce" TextField
            String nonceText = nonce1.getText();

            // Calculate the hash
            String block_noText = block_no1.getText();
            String senderText = sender1.getText();
            String receiverText = receiver1.getText();
            String amountText = amount1.getText();

            String concatenatedText = block_noText + nonceText + senderText + receiverText + amountText;

            String hashedText = calculateSHA256(concatenatedText);

            // Set the hash value in the "hash" TextField
            hash1.setText(hashedText);

            // Check if the first three characters of the hash match the desired prefix
            if (hashedText.startsWith(desiredPrefix)) {
                disableMineButton(true); // Disable the "Mine" button
                break; // Nonce found, exit the loop
            }
        }
    }

    private void createNewBlock() {

    }
}