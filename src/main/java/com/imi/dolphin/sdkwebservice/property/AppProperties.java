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
package com.imi.dolphin.sdkwebservice.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author reja
 *
 */
@Component
public class AppProperties {

    @Value("${server.port}")
    String servicePort;

    @Value("${app.form.id}")
    String formId;

    @Value("${mail.username}")
    String mailUsername;

    @Value("${mail.password}")
    String mailPassword;

    @Value("${mail.smtp.auth}")
    String mailStmpAuth;

    @Value("${mail.smtp.starttls.enable}")
    String mailSmtpTls;

    @Value("${mail.smtp.host}")
    String mailSmtpHost;

    @Value("${mail.smtp.port}")
    String mailSmtpPort;

    @Value("${email.recipient1}")
    String emailrecipient1;

    @Value("${email.recipient2}")
    String emailrecipient2;

    @Value("${email.recipient1.name}")
    String namerecipient1;

    @Value("${email.recipient2.name}")
    String namerecipient2;

    @Value("${dolphin.url.base}")
    String baseUrl;

    @Value("${dolphin.api.token}")
    String apiToken;

    @Value("${dolphin.api.form}")
    String apiForm;

    @Value("${fieldName.ticketNumber}")
    String ticketNumber;

    @Value("${siloam.api.area}")
    String apiArea;

    @Value("${siloam.api.hospital}")
    String apiHospital;

    @Value("${siloam.api.specialist}")
    String apiSpecialist;

    @Value("${siloam.api.specialistbyname}")
    String apiSpecialistbyname;

    @Value("${siloam.api.doctorbyname}")
    String apiDoctorbyname;

    @Value("${siloam.api.doctorbydoctorid}")
    String apiDoctorbydoctorid;

    @Value("${siloam.api.doctorbydoctorid&spesialis}")
    String apiDoctorbydoctorIdSpecialist;

    @Value("${siloam.api.doctorschedule}")
    String apiDoctorschedule;

    @Value("${siloam.dummy.hospital}")
    String dummyHospital;

    @Value("${google.map.query}")
    String googleMapQuery;

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getApiForm() {
        return apiForm;
    }

    public void setApiForm(String apiForm) {
        this.apiForm = apiForm;
    }

    public String getNamerecipient1() {
        return namerecipient1;
    }

    public void setNamerecipient1(String namerecipient1) {
        this.namerecipient1 = namerecipient1;
    }

    public String getEmailrecipient1() {
        return emailrecipient1;
    }

    public void setEmailrecipient1(String emailrecipient1) {
        this.emailrecipient1 = emailrecipient1;
    }

    public String getEmailrecipient2() {
        return emailrecipient2;
    }

    public void setEmailrecipient2(String emailrecipient2) {
        this.emailrecipient2 = emailrecipient2;
    }

    public String getNamerecipient2() {
        return namerecipient2;
    }

    public void setNamerecipient2(String namerecipient2) {
        this.namerecipient2 = namerecipient2;
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getMailUsername() {
        return mailUsername;
    }

    public void setMailUsername(String mailUsername) {
        this.mailUsername = mailUsername;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public String getMailStmpAuth() {
        return mailStmpAuth;
    }

    public void setMailStmpAuth(String mailStmpAuth) {
        this.mailStmpAuth = mailStmpAuth;
    }

    public String getMailSmtpTls() {
        return mailSmtpTls;
    }

    public void setMailSmtpTls(String mailSmtpTls) {
        this.mailSmtpTls = mailSmtpTls;
    }

    public String getMailSmtpHost() {
        return mailSmtpHost;
    }

    public void setMailSmtpHost(String mailSmtpHost) {
        this.mailSmtpHost = mailSmtpHost;
    }

    public String getMailSmtpPort() {
        return mailSmtpPort;
    }

    public void setMailSmtpPort(String mailSmtpPort) {
        this.mailSmtpPort = mailSmtpPort;
    }

    public String getApiArea() {
        return apiArea;
    }

    public void setApiArea(String apiArea) {
        this.apiArea = apiArea;
    }

    public String getApiHospital() {
        return apiHospital;
    }

    public void setApiHospital(String apiHospital) {
        this.apiHospital = apiHospital;
    }

    public String getApiSpecialist() {
        return apiSpecialist;
    }

    public void setApiSpecialist(String apiSpecialist) {
        this.apiSpecialist = apiSpecialist;
    }

    public String getApiSpecialistbyname() {
        return apiSpecialistbyname;
    }

    public void setApiSpecialistbyname(String apiSpecialistbyname) {
        this.apiSpecialistbyname = apiSpecialistbyname;
    }

    public String getApiDoctorbyname() {
        return apiDoctorbyname;
    }

    public void setApiDoctorbyname(String apiDoctorbyname) {
        this.apiDoctorbyname = apiDoctorbyname;
    }

    public String getApiDoctorbydoctorid() {
        return apiDoctorbydoctorid;
    }

    public void setApiDoctorbydoctorid(String apiDoctorbydoctorid) {
        this.apiDoctorbydoctorid = apiDoctorbydoctorid;
    }

    public String getApiDoctorschedule() {
        return apiDoctorschedule;
    }

    public void setApiDoctorschedule(String apiDoctorschedule) {
        this.apiDoctorschedule = apiDoctorschedule;
    }

    public String getDummyHospital() {
        return dummyHospital;
    }

    public void setDummyHospital(String dummyHospital) {
        this.dummyHospital = dummyHospital;
    }

    public String getGoogleMapQuery() {
        return googleMapQuery;
    }

    public void setGoogleMapQuery(String googleMapQuery) {
        this.googleMapQuery = googleMapQuery;
    }

    public String getApiDoctorbydoctorIdSpecialist() {
        return apiDoctorbydoctorIdSpecialist;
    }

    public void setApiDoctorbydoctorIdSpecialist(String apiDoctorbydoctorIdSpecialist) {
        this.apiDoctorbydoctorIdSpecialist = apiDoctorbydoctorIdSpecialist;
    }

}
