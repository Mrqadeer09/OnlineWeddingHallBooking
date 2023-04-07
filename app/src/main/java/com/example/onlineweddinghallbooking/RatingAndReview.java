package com.example.onlineweddinghallbooking;

public class RatingAndReview {

    String uid;
    String customerName;
    String customerUid;
    int rating;
    String review;
    String dateAndTime;

    public RatingAndReview() {
    }

    public RatingAndReview(String uid, String customerName, String customerUid, int rating, String review, String dateAndTime) {
        this.uid = uid;
        this.customerName = customerName;
        this.customerUid = customerUid;
        this.rating = rating;
        this.review = review;
        this.dateAndTime = dateAndTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerUid() {
        return customerUid;
    }

    public void setCustomerUid(String customerUid) {
        this.customerUid = customerUid;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}
