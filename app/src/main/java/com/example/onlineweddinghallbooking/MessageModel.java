package com.example.onlineweddinghallbooking;

public class MessageModel {

    String senderUid;
    String receiverUid;
    String msg;
    String dateAndTime;

    public MessageModel() {
    }

    public MessageModel(String senderUid, String receiverUid, String msg, String dateAndTime) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.msg = msg;
        this.dateAndTime = dateAndTime;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}
