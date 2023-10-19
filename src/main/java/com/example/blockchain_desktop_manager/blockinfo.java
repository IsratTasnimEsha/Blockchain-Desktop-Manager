package com.example.blockchain_desktop_manager;

import javafx.scene.control.Button;

public class blockinfo {
    int id;
    String sender, receiver, amount, fees;
    Button removeFromBlockButton;

    public blockinfo(int id, String sender, String receiver, String amount, String fees) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.fees = fees;
        this.removeFromBlockButton = removeFromBlockButton;
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

    public Button getRemoveFromBlockButton() {
        return removeFromBlockButton;
    }

    public void setRemoveFromBlockButton(Button removeFromBlockButton) {
        this.removeFromBlockButton = removeFromBlockButton;
    }
}