/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imi.dolphin.sdkwebservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

/**
 *
 * @author Deka
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CreateAppointment {

    private String booking_id;
    private String booking_type_id;
    private String booking_no;
    private String booking_date;
    private String booking_time;
    private String note;
    private String schedule_id;
    private String hospital_id;
    private String doctor_id;
    private String user_id;
    private boolean is_waiting_list;
    private String contact_id;
    private String name;
    private String date_of_birth;
    private String phone_number;
    private String address_line_1;
    private String address_line_2;
    private String email;

    private Map<String, String> sex;
    private Map<String, String> city;
    private Map<String, String> district;
    private Map<String, String> subdistrict;
    private Map<String, String> nationality;
    private Map<String, String> emergency_contact_detail;
    
    
    
    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getBooking_type_id() {
        return booking_type_id;
    }

    public void setBooking_type_id(String booking_type_id) {
        this.booking_type_id = booking_type_id;
    }

    public String getBooking_no() {
        return booking_no;
    }

    public void setBooking_no(String booking_no) {
        this.booking_no = booking_no;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(String schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(String hospital_id) {
        this.hospital_id = hospital_id;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isIs_waiting_list() {
        return is_waiting_list;
    }

    public void setIs_waiting_list(boolean is_waiting_list) {
        this.is_waiting_list = is_waiting_list;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
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

    public String getAddress_line_1() {
        return address_line_1;
    }

    public void setAddress_line_1(String address_line_1) {
        this.address_line_1 = address_line_1;
    }

    public String getAddress_line_2() {
        return address_line_2;
    }

    public void setAddress_line_2(String address_line_2) {
        this.address_line_2 = address_line_2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getSex() {
        return sex;
    }

    public void setSex(Map<String, String> sex) {
        this.sex = sex;
    }

    public Map<String, String> getCity() {
        return city;
    }

    public void setCity(Map<String, String> city) {
        this.city = city;
    }

    public Map<String, String> getDistrict() {
        return district;
    }

    public void setDistrict(Map<String, String> district) {
        this.district = district;
    }

    public Map<String, String> getSubdistrict() {
        return subdistrict;
    }

    public void setSubdistrict(Map<String, String> subdistrict) {
        this.subdistrict = subdistrict;
    }

    public Map<String, String> getNationality() {
        return nationality;
    }

    public void setNationality(Map<String, String> nationality) {
        this.nationality = nationality;
    }

    public Map<String, String> getEmergency_contact_detail() {
        return emergency_contact_detail;
    }

    public void setEmergency_contact_detail(Map<String, String> emergency_contact_detail) {
        this.emergency_contact_detail = emergency_contact_detail;
    }

}
