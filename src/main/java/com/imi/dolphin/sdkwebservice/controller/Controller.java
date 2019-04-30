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

    @RequestMapping("/transferAgent")
    @PostMapping
    public ExtensionResult doTransferToAgent(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doTransferToAgent(extensionRequest);
    }

    @RequestMapping("/menuutama")
    @PostMapping
    public ExtensionResult MenuUtama(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.MenuUtama(extensionRequest);
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

    @RequestMapping("/sendlocationspecialist")
    @PostMapping
    public ExtensionResult doSendLocationSpecialist(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doSendLocationSpecialist(extensionRequest);
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

    @RequestMapping("/setkonfirmasispecbyhospital")
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
    @RequestMapping("/tanyanama")
    @PostMapping
    public ExtensionResult tanyaNama(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.tanyaNama(extensionRequest);
    }

    @RequestMapping("/validasinama")
    @PostMapping
    public ExtensionResult validasiNama(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.validasiNama(extensionRequest);
    }

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

    @RequestMapping("/specialisthospitalterdekat")
    @PostMapping
    public ExtensionResult SpecialistHospitalTerdekat(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.SpecialistHospitalTerdekat(extensionRequest);
    }

    @RequestMapping("/getdoctor/specialist")
    @PostMapping
    public ExtensionResult doGetDoctorBySpecialist(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetDoctorBySpecialist(extensionRequest);
    }
    // ---------------------- //

    // Booking Doctor //
    @RequestMapping("/jampraktek")
    @PostMapping
    public ExtensionResult JamPraktek(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.JamPraktek(extensionRequest);
    }

    @RequestMapping("/jampraktekdokter")
    @PostMapping
    public ExtensionResult doGetJamPraktekDokter(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetJamPraktekDokter(extensionRequest);
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

    @RequestMapping("/cleardate")
    @PostMapping
    public ExtensionResult doClearDate(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doClearDate(extensionRequest);
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
    
//    @RequestMapping("/setnewkonfirmasispesialis")
//    @PostMapping
//    public ExtensionResult setNewKonfirmasiSpesialis(@RequestBody ExtensionRequest extensionRequest) {
//        return svcService.setNewKonfirmasiSpesialis(extensionRequest);
//    }

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
