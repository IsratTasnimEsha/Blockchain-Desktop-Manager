package com.example.blockchain_desktop_manager;

import javafx.scene.control.Button;

public class Transactions {
    int id;
    String sender, receiver, amount, fees, verify, temp_block;
    int block_no;
    Button verifyButton, addToBlockButton;

    public Transactions(int id, String sender, String receiver, String amount, String fees, String verify) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.fees = fees;
        this.verify = verify;
        this.temp_block = temp_block;
        this.block_no = block_no;
        this.verifyButton = verifyButton;
        this.addToBlockButton = addToBlockButton;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public String getTemp_block() {
        return temp_block;
    }

    public void setTemp_block(String temp_block) {
        this.temp_block = temp_block;
    }

    public int getBlock_no() {
        return block_no;
    }

    public void setBlock_no(int block_no) {
        this.block_no = block_no;
    }

    public Button getVerifyButton() {
        return verifyButton;
    }

    public void setVerifyButton(Button verifyButton) {
        this.verifyButton = verifyButton;
    }

    public Button getAddToBlockButton() {
        return addToBlockButton;
    }

    public void setAddToBlockButton(Button addToBlockButton) {
        this.addToBlockButton = addToBlockButton;
    }
}