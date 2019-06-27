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
package com.imi.dolphin.sdkwebservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imi.dolphin.sdkwebservice.model.ExtensionRequest;
import com.imi.dolphin.sdkwebservice.model.ExtensionResult;
import com.imi.dolphin.sdkwebservice.property.AppProperties;
import com.imi.dolphin.sdkwebservice.service.IMailService;
import com.imi.dolphin.sdkwebservice.service.IService;

/**
 *
 * @author Deka
 *
 */
@RestController
public class Controller {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    IService svcService;

    @Autowired
    IMailService svcMailService;

    @RequestMapping("/forms")
    public String getStarted() {
        return "Hello Form, service port: " + appProperties.getServicePort() + ", " + appProperties.getFormId();
    }

    @RequestMapping("/transferAgent")
    @PostMapping
    public ExtensionResult doTransferToAgent(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doTransferToAgent(extensionRequest);
    }

    @RequestMapping("/tipepencarian")
    @PostMapping
    public ExtensionResult TipePencarian(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.TipePencarian(extensionRequest);
    }

    // Get Nearest Hospital //
    @RequestMapping("/sendlocation")
    @PostMapping
    public ExtensionResult doSendLocation(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doSendLocation(extensionRequest);
    }

    @RequestMapping("/nearesthospital")
    @PostMapping
    public ExtensionResult doGetHospitalTerdekat(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetHospitalTerdekat(extensionRequest);
    }

    @RequestMapping("/callhospital")
    @PostMapping
    public ExtensionResult doCallHospital(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doCallHospital(extensionRequest);
    }

    @RequestMapping("/createappointment")
    @PostMapping
    public ExtensionResult doPostCreateAppointment(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doPostCreateAppointment(extensionRequest);
    }

    //------------------------//
    @RequestMapping("/validatephone")
    @PostMapping
    public ExtensionResult doValidatePhone(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doValidatePhone(extensionRequest);
    }

    @RequestMapping("/valdate")
    @PostMapping
    public ExtensionResult doValidateDate(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doValidateDate(extensionRequest);
    }

    @RequestMapping("/SiloammenggunakanBPJS")
    @PostMapping
    public ExtensionResult SiloamMenggunakanBPJS(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.siloamMenggunakanBPJS(extensionRequest);
    }

    // New Booking Flow
    @RequestMapping("/setkonfirmasitipe")
    @PostMapping
    public ExtensionResult SetKonfirmasiTipe(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.SetKonfirmasiTipe(extensionRequest);
    }

    @RequestMapping("/setstepdua")
    @PostMapping
    public ExtensionResult setStepDua(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.setStepDua(extensionRequest);
    }

    @RequestMapping("/setsteptiga")
    @PostMapping
    public ExtensionResult setStepTiga(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.setStepTiga(extensionRequest);
    }

    @RequestMapping("/newgetdoctor")
    @PostMapping
    public ExtensionResult newGetDoctor(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.newGetDoctor(extensionRequest);
    }

    @RequestMapping("/newgetscheduledoctorid")
    @PostMapping
    public ExtensionResult newGetScheduleDoctorId(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.newGetScheduleDoctorId(extensionRequest);
    }

    @RequestMapping("/newgetjampraktek")
    @PostMapping
    public ExtensionResult newGetJamPraktek(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.newGetJamPraktek(extensionRequest);
    }

    @RequestMapping("/tanyanamapasien")
    @PostMapping
    public ExtensionResult tanyaNamaPasien(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.tanyaNamaPasien(extensionRequest);
    }

    @RequestMapping("/validasinamapasien")
    @PostMapping
    public ExtensionResult validasiNamaPasien(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.validasiNamaPasien(extensionRequest);
    }

}
