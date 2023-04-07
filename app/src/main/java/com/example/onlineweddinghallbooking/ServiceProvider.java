package com.example.onlineweddinghallbooking;

public class ServiceProvider extends User{

    private String cnic;
    private String address;
    private String verified;

    public ServiceProvider(){


    }

    public ServiceProvider(String cnic, String address, String verified) {
        this.cnic = cnic;
        this.address = address;
        this.verified = verified;
    }

    public ServiceProvider(String name, String username, String gender, String phoneNo, String email, String password, String dateOfBirth, String role, String cnic, String address, String verified) {
        super(name, username, gender, phoneNo, email, password, dateOfBirth, role);
        this.cnic = cnic;
        this.address = address;
        this.verified = verified;
    }
    public ServiceProvider(String name, String username, String gender, String phoneNo, String email, String password, String dateOfBirth, String role) {
        super(name, username, gender, phoneNo, email, password, dateOfBirth, role);
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
}
