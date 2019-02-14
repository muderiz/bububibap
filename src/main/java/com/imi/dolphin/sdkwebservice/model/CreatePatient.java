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
    private static final String BATAS = "\"";
    private static final String KOMA = "\",";

    public String build() {
        String result = PEMBUKA
                + "\"name\":" + BATAS + name + KOMA
                + "\"date_of_birth\":" + BATAS + date_of_birth + KOMA
                + "\"phone_number\":" + BATAS + phone_number + KOMA
                + "\"hospital_id\":" + BATAS + hospital_id + KOMA
                + "\"user_id\":" + BATAS + user_id + KOMA + PENUTUP;

        return result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setHospital_id(String hospital_id) {
        this.hospital_id = hospital_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}
