package com.example.onlineweddinghallbooking;

public class Nadra {

    String name;
    String cnic;

    public Nadra(){

    }

    public Nadra(String name, String cnic) {
        this.name = name;
        this.cnic = cnic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }
}
