package com.example.onlineweddinghallbooking;

public class Stage {

    private String uid;
    private String stageImgUrl;
    private String stageName;
    private String stagePrice;

    public Stage() {
    }

    public Stage(String uid, String stageImgUrl, String stageName, String stagePrice) {
        this.uid = uid;
        this.stageImgUrl = stageImgUrl;
        this.stageName = stageName;
        this.stagePrice = stagePrice;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStageImgUrl() {
        return stageImgUrl;
    }

    public void setStageImgUrl(String stageImgUrl) {
        this.stageImgUrl = stageImgUrl;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStagePrice() {
        return stagePrice;
    }

    public void setStagePrice(String stagePrice) {
        this.stagePrice = stagePrice;
    }
}
