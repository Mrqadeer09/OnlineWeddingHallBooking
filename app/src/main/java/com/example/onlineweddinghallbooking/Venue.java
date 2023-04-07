package com.example.onlineweddinghallbooking;

public class Venue {

    String uid;
    String name;
    String address;
    String basePrice;
    String description;
    String minimumSeatingCapacity;
    String maximumSeatingCapacity;
    String minimumParkingCapacity;
    String maximumParkingCapacity;
    String partitionAvailable;
    String thumbnailImage;
    String latitude;
    String longitude;
    String ownerUid;
    String contact;
    String avgRating;
    String cateringAvailable;
    String maximumVIPTables;
    String pricePerVipTable;
    String pricePerChair;
    String soundSystemPrice;
    int noOfBookings;

    public Venue()
    {

    }

    public Venue(String uid, String name, String address, String basePrice, String description, String minimumSeatingCapacity, String maximumSeatingCapacity, String minimumParkingCapacity, String maximumParkingCapacity, String partitionAvailable, String thumbnailImage, String latitude, String longitude, String ownerUid, String contact, String avgRating, String cateringAvailable, String maximumVIPTables, String pricePerVipTable, String pricePerChair, String soundSystemPrice, int noOfBookings) {
        this.uid = uid;
        this.name = name;
        this.address = address;
        this.basePrice = basePrice;
        this.description = description;
        this.minimumSeatingCapacity = minimumSeatingCapacity;
        this.maximumSeatingCapacity = maximumSeatingCapacity;
        this.minimumParkingCapacity = minimumParkingCapacity;
        this.maximumParkingCapacity = maximumParkingCapacity;
        this.partitionAvailable = partitionAvailable;
        this.thumbnailImage = thumbnailImage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ownerUid = ownerUid;
        this.contact = contact;
        this.avgRating = avgRating;
        this.cateringAvailable = cateringAvailable;
        this.maximumVIPTables = maximumVIPTables;
        this.pricePerVipTable = pricePerVipTable;
        this.pricePerChair = pricePerChair;
        this.soundSystemPrice = soundSystemPrice;
        this.noOfBookings = noOfBookings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(String basePrice) {
        this.basePrice = basePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMinimumSeatingCapacity() {
        return minimumSeatingCapacity;
    }

    public void setMinimumSeatingCapacity(String minimumSeatingCapacity) {
        this.minimumSeatingCapacity = minimumSeatingCapacity;
    }

    public String getMaximumSeatingCapacity() {
        return maximumSeatingCapacity;
    }

    public void setMaximumSeatingCapacity(String maximumSeatingCapacity) {
        this.maximumSeatingCapacity = maximumSeatingCapacity;
    }

    public String getMinimumParkingCapacity() {
        return minimumParkingCapacity;
    }

    public void setMinimumParkingCapacity(String minimumParkingCapacity) {
        this.minimumParkingCapacity = minimumParkingCapacity;
    }

    public String getMaximumParkingCapacity() {
        return maximumParkingCapacity;
    }

    public void setMaximumParkingCapacity(String maximumParkingCapacity) {
        this.maximumParkingCapacity = maximumParkingCapacity;
    }

    public String getPartitionAvailable() {
        return partitionAvailable;
    }

    public void setPartitionAvailable(String partitionAvailable) {
        this.partitionAvailable = partitionAvailable;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public String getCateringAvailable() {
        return cateringAvailable;
    }

    public void setCateringAvailable(String cateringAvailable) {
        this.cateringAvailable = cateringAvailable;
    }

    public String getMaximumVIPTables() {
        return maximumVIPTables;
    }

    public void setMaximumVIPTables(String maximumVIPTables) {
        this.maximumVIPTables = maximumVIPTables;
    }

    public String getPricePerVipTable() {
        return pricePerVipTable;
    }

    public void setPricePerVipTable(String pricePerVipTable) {
        this.pricePerVipTable = pricePerVipTable;
    }

    public String getPricePerChair() {
        return pricePerChair;
    }

    public void setPricePerChair(String pricePerChair) {
        this.pricePerChair = pricePerChair;
    }

    public String getSoundSystemPrice() {
        return soundSystemPrice;
    }

    public void setSoundSystemPrice(String soundSystemPrice) {
        this.soundSystemPrice = soundSystemPrice;
    }

    public int getNoOfBookings() {
        return noOfBookings;
    }

    public void setNoOfBookings(int noOfBookings) {
        this.noOfBookings = noOfBookings;
    }
}
