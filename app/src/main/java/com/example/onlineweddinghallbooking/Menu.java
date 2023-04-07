package com.example.onlineweddinghallbooking;

import java.io.Serializable;
import java.util.Comparator;

public class Menu implements Serializable {

    private String dishUid;
    private String dishImgUrl;
    private String dishName;
    private String dishPrice;
    private String dishType;

    public Menu() {
    }

    public Menu(String dishUid, String dishImgUrl, String dishName, String dishPrice, String dishType) {
        this.dishUid = dishUid;
        this.dishImgUrl = dishImgUrl;
        this.dishName = dishName;
        this.dishPrice = dishPrice;
        this.dishType = dishType;
    }

    public String getDishUid() {
        return dishUid;
    }

    public void setDishUid(String dishUid) {
        this.dishUid = dishUid;
    }

    public String getDishImgUrl() {
        return dishImgUrl;
    }

    public void setDishImgUrl(String dishImgUrl) {
        this.dishImgUrl = dishImgUrl;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public String getDishType() {
        return dishType;
    }

    public void setDishType(String dishType) {
        this.dishType = dishType;
    }

    public static Comparator<Menu> menuDishPrice = new Comparator<Menu>() {
        @Override
        public int compare(Menu o1, Menu o2) {

            int dishPrice1 = Integer.parseInt(o1.getDishPrice());
            int dishPrice2 = Integer.parseInt(o2.getDishPrice());

            return dishPrice1 - dishPrice2;
        }
    };

}
