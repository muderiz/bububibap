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

import com.imi.dolphin.sdkwebservice.model.BMWModel;
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

    @RequestMapping("/form")
    @PostMapping
    public ExtensionResult doBuildForm(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.getForms(extensionRequest);
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

    @RequestMapping("/image")
    @PostMapping
    public ExtensionResult doBuildImage(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.getImage(extensionRequest);
    }

    @RequestMapping("/sendMail")
    @PostMapping
    public ExtensionResult doSendMail(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.doSendMail(extensionRequest);
    }

    @RequestMapping("/formcuti")
    @PostMapping
    public ExtensionResult dogetFormcuti(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.dogetFormcuti(extensionRequest);
    }

    @RequestMapping("/ajuincuti")
    @PostMapping
    public ExtensionResult dogetajuincuti(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.dogetajuincuti(extensionRequest);
    }

    @RequestMapping("/harga")
    @PostMapping
    public ExtensionResult dogetHargaMobil(@RequestBody ExtensionRequest extensionRequest) {
        return svcService.dogetHargaMobil(extensionRequest);
    }

    @RequestMapping(path = "/getMerkMobil", method = RequestMethod.GET)
    public ResponseEntity<List<MerkMobil>> listMerks() {
        return new ResponseEntity<>(getMerks(), HttpStatus.OK);
    }
    
    @RequestMapping(path = "/getBMW", method = RequestMethod.GET)
    public ResponseEntity<List<BMWModel>> listBMW() {
        return new ResponseEntity<>(getBMW(), HttpStatus.OK);
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

    private List<BMWModel> getBMW() {

        BMWModel bmw = new BMWModel();
        bmw.setId("1");
        bmw.setType("sedan");
        bmw.setModel("BMW 3 Series");

        BMWModel bmw1 = new BMWModel();
        bmw1.setId("2");
        bmw1.setType("sedan");
        bmw1.setModel("BMW M3");

        BMWModel bmw2 = new BMWModel();
        bmw2.setId("3");
        bmw2.setType("sedan");
        bmw2.setModel("BMW 5 Series");

        BMWModel bmw3 = new BMWModel();
        bmw3.setId("4");
        bmw3.setType("sedan");
        bmw3.setModel("All New BMW 7 Series");

        BMWModel bmw4 = new BMWModel();
        bmw4.setId("5");
        bmw4.setType("sedan");
        bmw4.setModel("BMW M5 with M xDrive");

        BMWModel bmw5 = new BMWModel();
        bmw5.setId("6");
        bmw5.setType("hatchback");
        bmw5.setModel("BMW 1 Series 5-door");

        BMWModel bmw6 = new BMWModel();
        bmw6.setId("7");
        bmw6.setType("suv");
        bmw6.setModel("BMW X1");

        BMWModel bmw7 = new BMWModel();
        bmw7.setId("8");
        bmw7.setType("suv");
        bmw7.setModel("BMW X3");

        BMWModel bmw8 = new BMWModel();
        bmw8.setId("9");
        bmw8.setType("suv");
        bmw8.setModel("BMW X5");

        BMWModel bmw9 = new BMWModel();
        bmw9.setId("10");
        bmw9.setType("suv");
        bmw9.setModel("BMW X5 M");
        
        
        return Arrays.asList(bmw, bmw1, bmw2, bmw3, bmw4, bmw5, bmw6, bmw7, bmw8, bmw9);
    }

}
