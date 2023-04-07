package com.example.onlineweddinghallbooking;

public class AllMessageModel {

    String name;
    String lastMessage;
    String date;

    public AllMessageModel() {
    }

    public AllMessageModel(String name, String lastMessage, String date) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
