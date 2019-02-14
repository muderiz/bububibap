/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imi.dolphin.sdkwebservice.model;

/**
 *
 * @author Deka
 */
public class CreatePatient {

    private String name;
    private String date_of_birth;
    private String phone_number;
    private String hospital_id;
    private String user_id;

    private static final String PEMBUKA = "{";
    private static final String PENUTUP = "}";

    public String build() {
        String result = PEMBUKA
                + "\"name\":" + "\"" + name + "\","
                + "\"date_of_birth\":" + "\"" + date_of_birth + "\","
                + "\"phone_number\":" + "\"" + phone_number + "\","
                + "\"hospital_id\":" + "\"" + hospital_id + "\","
                + "\"user_id\":" + "\"" + user_id + "\"" + PENUTUP;
        
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(String hospital_id) {
        this.hospital_id = hospital_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

}
