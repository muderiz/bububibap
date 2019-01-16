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

import com.imi.dolphin.sdkwebservice.model.ToyotaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imi.dolphin.sdkwebservice.model.ExtensionRequest;
import com.imi.dolphin.sdkwebservice.model.ExtensionResult;
import com.imi.dolphin.sdkwebservice.model.MerkMobil;
import com.imi.dolphin.sdkwebservice.property.AppProperties;
import com.imi.dolphin.sdkwebservice.service.IMailService;
import com.imi.dolphin.sdkwebservice.service.IService;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @RequestMapping("/sendLocation")
    @PostMapping
    public ExtensionResult doBuildSendLocation(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doSendLocation(extensionRequest);
    }

    @RequestMapping(path = "/getMerkMobil", method = RequestMethod.GET)
    public ResponseEntity<List<MerkMobil>> listMerks() {
        return new ResponseEntity<>(getMerks(), HttpStatus.OK);
    }

    @RequestMapping(path = "/getToyota", method = RequestMethod.GET)
    public ResponseEntity<List<ToyotaModel>> listBMW() {
        return new ResponseEntity<>(getToyota(), HttpStatus.OK);
    }

    private List<MerkMobil> getMerks() {
        MerkMobil merk = new MerkMobil();
        merk.setId("1");
        merk.setMerk("BMW");

        MerkMobil merk1 = new MerkMobil();
        merk1.setId("2");
        merk1.setMerk("Toyota");

        MerkMobil merk2 = new MerkMobil();
        merk2.setId("3");
        merk2.setMerk("Daihatsu");

        MerkMobil merk3 = new MerkMobil();
        merk3.setId("4");
        merk3.setMerk("Isuzu");

        MerkMobil merk4 = new MerkMobil();
        merk4.setId("5");
        merk4.setMerk("Peugeot");

        return Arrays.asList(merk, merk1, merk2, merk3, merk4);
    }

    private List<ToyotaModel> getToyota() {

        ToyotaModel tm = new ToyotaModel();
        tm.setId("1");
        tm.setType("hatchback");
        tm.setModel("New Yaris");

        ToyotaModel tm1 = new ToyotaModel();
        tm1.setId("2");
        tm1.setType("hatchback");
        tm1.setModel("New Agya");

        ToyotaModel tm2 = new ToyotaModel();
        tm2.setId("3");
        tm2.setType("suv");
        tm2.setModel("All New C-HR");

        ToyotaModel tm3 = new ToyotaModel();
        tm3.setId("4");
        tm3.setType("suv");
        tm3.setModel("Land Cruiser");

        ToyotaModel tm4 = new ToyotaModel();
        tm4.setId("5");
        tm4.setType("suv");
        tm4.setModel("Fortuner");

        ToyotaModel tm5 = new ToyotaModel();
        tm5.setId("6");
        tm5.setType("suv");
        tm5.setModel("All New Rush");

        ToyotaModel tm6 = new ToyotaModel();
        tm6.setId("7");
        tm6.setType("sedan");
        tm6.setModel("New Vios");

        ToyotaModel tm7 = new ToyotaModel();
        tm7.setId("8");
        tm7.setType("sedan");
        tm7.setModel("New Camry");

        ToyotaModel tm8 = new ToyotaModel();
        tm8.setId("9");
        tm8.setType("sedan");
        tm8.setModel("New Corolla Altis");

        ToyotaModel tm9 = new ToyotaModel();
        tm9.setId("10");
        tm9.setType("mpv");
        tm9.setModel("Avanza");

        ToyotaModel tm10 = new ToyotaModel();
        tm10.setId("11");
        tm10.setType("mpv");
        tm10.setModel("Veloz");

        ToyotaModel tm11 = new ToyotaModel();
        tm11.setId("12");
        tm11.setType("mpv");
        tm11.setModel("Calya");

        ToyotaModel tm12 = new ToyotaModel();
        tm12.setId("13");
        tm12.setType("mpv");
        tm12.setModel("All New Voxy");

        ToyotaModel tm13 = new ToyotaModel();
        tm13.setId("14");
        tm13.setType("mpv");
        tm13.setModel("Kijang Inova");

        ToyotaModel tm14 = new ToyotaModel();
        tm14.setId("15");
        tm14.setType("mpv");
        tm14.setModel("New Sienta");

        ToyotaModel tm15 = new ToyotaModel();
        tm15.setId("16");
        tm15.setType("mpv");
        tm15.setModel("New Alphard");

        ToyotaModel tm16 = new ToyotaModel();
        tm16.setId("17");
        tm16.setType("mpv");
        tm16.setModel("New Vellfire");

        return Arrays.asList(tm, tm1, tm2, tm3, tm4, tm5, tm6, tm7, tm8,
                tm9, tm10, tm11, tm12, tm13, tm14, tm15, tm16);
    }

    @RequestMapping("/getstarted")
    @PostMapping
    public ExtensionResult doGetStarted(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetStarted(extensionRequest);
    }

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
    
    @RequestMapping("/getspecialist")
    @PostMapping
    public ExtensionResult doGetSpecialist(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetSpecialist(extensionRequest);
    }
    
    @RequestMapping("/getdoctorbyhosspec")
    @PostMapping
    public ExtensionResult doGetDokterByHospitalAndSpecialist(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetDokterByHospitalAndSpecialist(extensionRequest);
    }

    @RequestMapping("/getdoctorschedule")
    @PostMapping
    public ExtensionResult doGetDoctorSchedule(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetDoctorSchedule(extensionRequest);
    }

    @RequestMapping("/getdoctorbyname")
    @PostMapping
    public ExtensionResult doGetDoctorByName(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetDoctorByName(extensionRequest);
    }
    
    @RequestMapping("/getschedulebydoctorid")
    @PostMapping
    public ExtensionResult doGetScheduleByDoctorId(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetScheduleByDoctorId(extensionRequest);
    }

    @RequestMapping("/menudoctorschedule")
    @PostMapping
    public ExtensionResult MenuDoctorSchedule(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.MenuDoctorSchedule(extensionRequest);
    }
    
    @RequestMapping("/hospitalterdekat")
    @PostMapping
    public ExtensionResult doGetHospitalTerdekat(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetHospitalTerdekat(extensionRequest);
    }
    
    @RequestMapping("/getspecialistbyname")
    @PostMapping
    public ExtensionResult doGetSpecialistbyName(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetSpecialistbyName(extensionRequest);
    }
    
    @RequestMapping("/getspecialist1")
    @PostMapping
    public ExtensionResult doGetSpecialist1(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doGetSpecialist1(extensionRequest);
    }
    
    
  
    
    
    
    
}
