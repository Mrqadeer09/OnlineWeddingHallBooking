package com.example.onlineweddinghallbooking;

public class Customer extends User{


    public Customer() {

    }

    public Customer(String name, String username, String gender, String phoneNo, String email, String password, String dateOfBirth, String role) {
        super(name, username, gender, phoneNo, email, password, dateOfBirth, role);
    }
}
