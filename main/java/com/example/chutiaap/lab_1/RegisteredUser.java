package com.example.chutiaap.lab_1;

/**
 * Created by Rehan Rajput on 15/04/2018.
 */

public class RegisteredUser {
    private String name;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    private String surname;
    private String email;
    private String phone;
    private String description;
    private String dob;

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }

    private String userid;

    public RegisteredUser(){
        userid="";
        name = "";
        surname = "";
        email ="";
        phone ="";
        description="";
        dob="";
        address="";
    }

    public RegisteredUser(String userid){
        this.userid = userid;
        name = "";
        surname = "";
        email ="";
        phone ="";
        description="";
        dob="";
        address="";
    }
}
