package com.example.onlineweddinghallbooking;

public class Booking {

    private String venueUid;
    private String venueName;
    private String venuePrice;
    private String venueImgURL;
    private String bookingUid;
    private String timeSlot;
    private String dateTime;
    private String partitionRequired;
    private String soundSystemRequired;
    private String soundSystemPrice;
    private String internalCateringRequired;
    private String cateringPrice;
    private String noOfGuests;
    private String noOfVipTablesRequired;
    private String vipTablesPrice;
    private String totalBill;
    private String stageUid;
    private String stageName;
    private String stagePrice;
    private String totalBookingCost;
    private String tax;
    private String seatingPrice;
    private String status;

    public Booking() {
    }

    public Booking(String venueUid, String venueName, String venuePrice, String venueImgURL, String bookingUid, String timeSlot, String dateTime, String partitionRequired, String soundSystemRequired, String soundSystemPrice, String internalCateringRequired, String cateringPrice, String noOfGuests, String noOfVipTablesRequired, String vipTablesPrice, String totalBill, String stageUid, String stageName, String stagePrice, String totalBookingCost, String tax, String seatingPrice, String status) {
        this.venueUid = venueUid;
        this.venueName = venueName;
        this.venuePrice = venuePrice;
        this.venueImgURL = venueImgURL;
        this.bookingUid = bookingUid;
        this.timeSlot = timeSlot;
        this.dateTime = dateTime;
        this.partitionRequired = partitionRequired;
        this.soundSystemRequired = soundSystemRequired;
        this.soundSystemPrice = soundSystemPrice;
        this.internalCateringRequired = internalCateringRequired;
        this.cateringPrice = cateringPrice;
        this.noOfGuests = noOfGuests;
        this.noOfVipTablesRequired = noOfVipTablesRequired;
        this.vipTablesPrice = vipTablesPrice;
        this.totalBill = totalBill;
        this.stageUid = stageUid;
        this.stageName = stageName;
        this.stagePrice = stagePrice;
        this.totalBookingCost = totalBookingCost;
        this.tax = tax;
        this.seatingPrice = seatingPrice;
        this.status = status;
    }

    public String getBookingUid() {
        return bookingUid;
    }

    public void setBookingUid(String bookingUid) {
        this.bookingUid = bookingUid;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPartitionRequired() {
        return partitionRequired;
    }

    public void setPartitionRequired(String partitionRequired) {
        this.partitionRequired = partitionRequired;
    }

    public String getSoundSystemRequired() {
        return soundSystemRequired;
    }

    public void setSoundSystemRequired(String soundSystemRequired) {
        this.soundSystemRequired = soundSystemRequired;
    }

    public String getInternalCateringRequired() {
        return internalCateringRequired;
    }

    public void setInternalCateringRequired(String internalCateringRequired) {
        this.internalCateringRequired = internalCateringRequired;
    }

    public String getNoOfGuests() {
        return noOfGuests;
    }

    public void setNoOfGuests(String noOfGuests) {
        this.noOfGuests = noOfGuests;
    }

    public String getNoOfVipTablesRequired() {
        return noOfVipTablesRequired;
    }

    public void setNoOfVipTablesRequired(String noOfVipTablesRequired) {
        this.noOfVipTablesRequired = noOfVipTablesRequired;
    }

    public String getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(String totalBill) {
        this.totalBill = totalBill;
    }

    public String getStageUid() {
        return stageUid;
    }

    public void setStageUid(String stageUid) {
        this.stageUid = stageUid;
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

    public String getVenueUid() {
        return venueUid;
    }

    public void setVenueUid(String venueUid) {
        this.venueUid = venueUid;
    }

    public String getVenuePrice() {
        return venuePrice;
    }

    public void setVenuePrice(String venuePrice) {
        this.venuePrice = venuePrice;
    }

    public String getCateringPrice() {
        return cateringPrice;
    }

    public void setCateringPrice(String cateringPrice) {
        this.cateringPrice = cateringPrice;
    }

    public String getTotalBookingCost() {
        return totalBookingCost;
    }

    public void setTotalBookingCost(String totalBookingCost) {
        this.totalBookingCost = totalBookingCost;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getSoundSystemPrice() {
        return soundSystemPrice;
    }

    public void setSoundSystemPrice(String soundSystemPrice) {
        this.soundSystemPrice = soundSystemPrice;
    }

    public String getVipTablesPrice() {
        return vipTablesPrice;
    }

    public void setVipTablesPrice(String vipTablesPrice) {
        this.vipTablesPrice = vipTablesPrice;
    }

    public String getSeatingPrice() {
        return seatingPrice;
    }

    public void setSeatingPrice(String seatingPrice) {
        this.seatingPrice = seatingPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueImgURL() {
        return venueImgURL;
    }

    public void setVenueImgURL(String venueImgURL) {
        this.venueImgURL = venueImgURL;
    }
}
