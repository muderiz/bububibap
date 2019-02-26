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
 * @author reja
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

    @RequestMapping("/status/")
    @PostMapping
    public ExtensionResult doGetSrnResult(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.getSrnResult(extensionRequest);
    }

    @RequestMapping("/customers")
    @PostMapping
    public ExtensionResult doQueryCustomerInfo(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.getCustomerInfo(extensionRequest);
    }

    @RequestMapping("/modifycustomername")
    @PostMapping
    public ExtensionResult doClearCustomerName(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.modifyCustomerName(extensionRequest);
    }

    @RequestMapping("/productinfo")
    @PostMapping
    public ExtensionResult doQueryProductInfo(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.getProductInfo(extensionRequest);
    }

    @RequestMapping("/messages")
    @PostMapping
    public ExtensionResult doGetMessages(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.getMessageBody(extensionRequest);
    }

    @RequestMapping("/quickreplies")
    @PostMapping
    public ExtensionResult doBuildQuickReplies(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.getQuickReplies(extensionRequest);
    }

    @RequestMapping("/button")
    @PostMapping
    public ExtensionResult doBuildButton(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.getButtons(extensionRequest);
    }

    @RequestMapping("/carousel")
    @PostMapping
    public ExtensionResult doBuildCarousel(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.getCarousel(extensionRequest);
    }

    @RequestMapping("/transferAgent")
    @PostMapping
    public ExtensionResult doTransferToAgent(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doTransferToAgent(extensionRequest);
    }

    @RequestMapping("/menudoctorschedule")
    @PostMapping
    public ExtensionResult MenuDoctorSchedule(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.MenuDoctorSchedule(extensionRequest);
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
    // ----------------------- //

    // Get Doctor by Area //
    @RequestMapping("/getareas")
    @PostMapping
    public ExtensionResult doGetAreas(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetAreas(extensionRequest);
    }

    @RequestMapping("/gethospital")
    @PostMapping
    public ExtensionResult doGetHospitalByArea(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetHospitalByArea(extensionRequest);
    }

    @RequestMapping("/getspecialist/hospital")
    @PostMapping
    public ExtensionResult SiloamGetSpecialistByHospital(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.SiloamGetSpecialistByHospital(extensionRequest);
    }

    @RequestMapping("/setkonfirmasispecbyarea")
    @PostMapping
    public ExtensionResult SetKonfirmasiSpesialisbyHospital(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.SetKonfirmasiSpesialisbyHospital(extensionRequest);
    }

    @RequestMapping("/getdoctor/hospital/specialist")
    @PostMapping
    public ExtensionResult SiloamGetDoctorByHospitalAndSpecialist(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.SiloamGetDoctorByHospitalAndSpecialist(extensionRequest);
    }

    //----------------//
    //p//
    @RequestMapping("/getdoctorschedule")
    @PostMapping
    public ExtensionResult doGetDoctorSchedule(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetDoctorSchedule(extensionRequest);
    }

    // Get Doctor by Name //
    @RequestMapping("/getdoctor/name")
    @PostMapping
    public ExtensionResult doGetDoctorByName(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetDoctorByName(extensionRequest);
    }

    //---------------------//
    // Get Schdule by DoctorId //
    @RequestMapping("/getschedule/doctorid")
    @PostMapping
    public ExtensionResult doGetScheduleByDoctorId(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetScheduleByDoctorId(extensionRequest);
    }

    // ------------------- //
    // Get Doctor by Specialist //
    @RequestMapping("/getspecialist/name")
    @PostMapping
    public ExtensionResult SiloamGetSpecialistbyName(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.SiloamGetSpecialistbyName(extensionRequest);
    }

    @RequestMapping("/setcounterspecialist")
    @PostMapping
    public ExtensionResult SetCounterSpecialist(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.SetCounterSpecialist(extensionRequest);
    }

    @RequestMapping("/setkonfirmasi")
    @PostMapping
    public ExtensionResult SetKonfirmasiSpesialis(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.SetKonfirmasiSpesialis(extensionRequest);
    }

    @RequestMapping("/getdoctor/specialist")
    @PostMapping
    public ExtensionResult doGetDoctorBySpecialist(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetDoctorBySpecialist(extensionRequest);
    }
    // ---------------------- //

    @RequestMapping("/getspecialistpage1")
    @PostMapping
    public ExtensionResult doGetSpecialistPage1(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetSpecialistList1(extensionRequest);
    }

    // Booking Doctor //
    @RequestMapping("/kategorijam")
    @PostMapping
    public ExtensionResult KategoriJam(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.KategoriJam(extensionRequest);
    }

    @RequestMapping("/jampraktek")
    @PostMapping
    public ExtensionResult doGetJamPraktekDokter(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetJamPraktekDokter(extensionRequest);
    }

    @RequestMapping("/createpatient")
    @PostMapping
    public ExtensionResult doPostCreatePatient(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doPostCreatePatient(extensionRequest);
    }

    @RequestMapping("/createappointment")
    @PostMapping
    public ExtensionResult doPostCreateAppointment(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doPostCreateAppointment(extensionRequest);
    }
    //------------------------//
}
