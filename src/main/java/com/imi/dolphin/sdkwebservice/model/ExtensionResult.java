/**
 * Copyright (c) 2014 InMotion Innovation Technology. All Rights Reserved. <BR>
 * <BR>
 * This software contains confidential and proprietary information of InMotion
 * Innovation Technology. ("Confidential Information").<BR>
 * <BR>
 * Such Confidential Information shall not be disclosed and it shall only be
 * used in accordance with the terms of the license agreement entered into with
 * IMI; other than in accordance with the written permission of IMI. <BR>
 *
 *
 */
package com.imi.dolphin.sdkwebservice.model;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 *
 * @author reja
 *
 */
    @JsonInclude(Include.NON_NULL)
public class ExtensionResult implements Serializable {

    private static final long serialVersionUID = 1768303005374821099L;
    private Map<String, String> value;
    private boolean next;
    private boolean success;
    private boolean repeat;
    private boolean agent;
    private Map<String, String> entities;
    private Map<String, EasyMap> parameters;
   
    //-------------Create Appointment-----------------//
    private String booking_id;
    private String booking_type_id;
    private String booking_no;
    private String booking_date;
    private String booking_time;
    private String note;
    private String schedule_id;
    private String doctor_id;
    private String is_waiting_list;
    private String contact_id;
    private String address_line_1;
    private String address_line_2;
    private String email;
    private Map<String, String> sex;
    private Map<String, String> district;
    private Map<String, String> subdistrict;
    private Map<String, String> nationality;
    private Map<String, String> emergency_contact_detail;
    
   

    /**
     * @return Get the value
     */
    public Map<String, String> getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Map<String, String> value) {
        this.value = value;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return Get the repeat
     */
    public boolean isRepeat() {
        return repeat;
    }

    /**
     * @param repeat the repeat to set
     */
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    /**
     * @return Get the cont
     */
    public boolean isNext() {
        return next;
    }

    /**
     * 
     * @param next next to set
     */
    public void setNext(boolean next) {
        this.next = next;
    }

    /**
     * @return Get the agent
     */
    public boolean isAgent() {
        return agent;
    }

    /**
     * @param agent the agent to set
     */
    public void setAgent(boolean agent) {
        this.agent = agent;
    }

    /**
     * @return the entities
     */
    public Map<String, String> getEntities() {
        return entities;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(Map<String, String> entities) {
        this.entities = entities;
    }

    /**
     * @return Get the parameters
     */
    public Map<String, EasyMap> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(Map<String, EasyMap> parameters) {
        this.parameters = parameters;
    }
    
    //-----------------------------------------------------------//

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

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getIs_waiting_list() {
        return is_waiting_list;
    }

    public void setIs_waiting_list(String is_waiting_list) {
        this.is_waiting_list = is_waiting_list;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
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
 //-------------------------------------------------------------------//   
}
