/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imi.dolphin.sdkwebservice.model;

import java.util.Map;

/**
 *
 * @author Deka
 */
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
    private String sexid;
    private String sexname;
    private String cityid;
    private String cityname;
    private String districtid;
    private String districtname;
    private String subdistrictid;
    private String subdistrictname;
    private String nationalityid;
    private String nationalityname;

    private static final String PEMBUKA = "{";
    private static final String PENUTUP = "}";
    private static final String BATAS = "\"";
    private static final String KOMA = "\",";

    public String build() {
        String result = PEMBUKA
                + "\"booking_id\":" + BATAS + null + KOMA
                + "\"booking_type_id\":" + BATAS + booking_type_id + KOMA
                + "\"booking_no\":" + BATAS + null + KOMA
                + "\"booking_date\":" + BATAS + booking_date + KOMA
                + "\"booking_time\":" + BATAS + booking_time + KOMA
                + "\"note\":" + BATAS + note + KOMA
                + "\"schedule_id\":" + BATAS + schedule_id + KOMA
                + "\"hospital_id\":" + BATAS + hospital_id + KOMA
                + "\"doctor_id\":" + BATAS + doctor_id + KOMA
                + "\"user_id\":" + BATAS + user_id + KOMA
                + "\"is_waiting_list\":" + BATAS + is_waiting_list + KOMA
                + "\"contact_id\":" + BATAS + contact_id + KOMA
                + "\"name\":" + BATAS + name + KOMA
                + "\"date_of_birth\":" + BATAS + date_of_birth + KOMA
                + "\"phone_number\":" + BATAS + phone_number + KOMA
                + "\"address_line_1\":" + BATAS + address_line_1 + KOMA
                + "\"address_line_2\":" + BATAS + address_line_2 + KOMA
                + "\"email\":" + BATAS + email + KOMA
                + "\"sex\":" + PEMBUKA + "\"id\":" + "1" + "," + "\"name\":" + "" + PENUTUP + ","
                + "\"city\":" + PEMBUKA + "\"id\":" + "1" + "," + "\"name\":" + "" + PENUTUP + ","
                + "\"district\":" + PEMBUKA + "\"id\":" + "1" + "," + "\"name\":" + "" + PENUTUP + ","
                + "\"subdistrict\":" + PEMBUKA + "\"id\":" + "1" + "," + "\"name\":" + "" + PENUTUP + ","
                + "\"nationality\":" + PEMBUKA + "\"id\":" + "1" + "," + "\"name\":" + "" + PENUTUP + ","
                + "\"emergency_contact_detail\":" + PEMBUKA + "\"contact_name\":" + "1" + "," + "\"contact_phone_number\":" + "" + PENUTUP
                + PENUTUP;
        return result;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public void setBooking_type_id(String booking_type_id) {
        this.booking_type_id = booking_type_id;
    }

    public void setBooking_no(String booking_no) {
        this.booking_no = booking_no;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setSchedule_id(String schedule_id) {
        this.schedule_id = schedule_id;
    }

    public void setHospital_id(String hospital_id) {
        this.hospital_id = hospital_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setIs_waiting_list(boolean is_waiting_list) {
        this.is_waiting_list = is_waiting_list;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
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

    public void setAddress_line_1(String address_line_1) {
        this.address_line_1 = address_line_1;
    }

    public void setAddress_line_2(String address_line_2) {
        this.address_line_2 = address_line_2;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSexid(String sexid) {
        this.sexid = sexid;
    }

    public void setSexname(String sexname) {
        this.sexname = sexname;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public void setDistrictid(String districtid) {
        this.districtid = districtid;
    }

    public void setDistrictname(String districtname) {
        this.districtname = districtname;
    }

    public void setSubdistrictid(String subdistrictid) {
        this.subdistrictid = subdistrictid;
    }

    public void setSubdistrictname(String subdistrictname) {
        this.subdistrictname = subdistrictname;
    }

    public void setNationalityid(String nationalityid) {
        this.nationalityid = nationalityid;
    }

    public void setNationalityname(String nationalityname) {
        this.nationalityname = nationalityname;
    }

}
