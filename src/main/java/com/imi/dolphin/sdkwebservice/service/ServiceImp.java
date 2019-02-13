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
package com.imi.dolphin.sdkwebservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imi.dolphin.sdkwebservice.builder.ButtonBuilder;
import com.imi.dolphin.sdkwebservice.builder.CarouselBuilder;
import com.imi.dolphin.sdkwebservice.builder.QuickReplyBuilder;
import com.imi.dolphin.sdkwebservice.model.ButtonTemplate;
import com.imi.dolphin.sdkwebservice.model.EasyMap;
import com.imi.dolphin.sdkwebservice.model.ExtensionRequest;
import com.imi.dolphin.sdkwebservice.model.ExtensionResult;
import com.imi.dolphin.sdkwebservice.param.ParamSdk;
import com.imi.dolphin.sdkwebservice.property.AppProperties;
import com.imi.dolphin.sdkwebservice.util.OkHttpUtil;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.CaseNode;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author reja
 *
 */
@Service
public class ServiceImp implements IService {

    public static final String OUTPUT = "output";
    private static final String SAMPLE_IMAGE_PATH = "https://goo.gl/SHdL8D";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String CONSTANT_SPLIT_SYNTAX = "&split&";
    @Autowired
    AppProperties appProperties;

    @Autowired
    IMailService svcMailService;

    /**
     * Get parameter value from request body parameter
     *
     * @param extensionRequest
     * @param name
     * @return
     */
    private String getEasyMapValueByName(ExtensionRequest extensionRequest, String name) {
        EasyMap easyMap = extensionRequest.getParameters().stream().filter(x -> x.getName().equals(name)).findAny()
                .orElse(null);
        if (easyMap != null) {
            return easyMap.getValue();
        }
        return "";
    }

    /*
	 * Sample Srn status with static result
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getSrnResult(com.imi.dolphin.
	 * sdkwebservice.model.ExtensionRequest)
     */
    @Override
    public ExtensionResult getSrnResult(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        Map<String, String> output = new HashMap<>();
        StringBuilder respBuilder = new StringBuilder();
        respBuilder.append(
                "20-July-2018 16:10:32 Ahmad Mahatir Ridwan - PIC sudah onsite cek problem(printer nyala-mati)\n");
        respBuilder.append("PIC troubleshoot. restart printer(NOK), ganti kabel power(NOK)\n");
        respBuilder.append("PIC akan eskalasi ke vendor terkait.");
        output.put(OUTPUT, respBuilder.toString());
        extensionResult.setValue(output);
        return extensionResult;
    }

    /*
	 * Sample Customer Info with static result
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.imi.dolphin.sdkwebservice.service.IService#getCustomerInfo(com.imi.
	 * dolphin.sdkwebservice.model.ExtensionRequest)
     */
    @Override
    public ExtensionResult getCustomerInfo(ExtensionRequest extensionRequest) {
        String account = getEasyMapValueByName(extensionRequest, "account");
        String name = getEasyMapValueByName(extensionRequest, "name");
        Map<String, String> output = new HashMap<>();
        StringBuilder respBuilder = new StringBuilder();
        if (account.substring(0, 1).equals("1")) {
            respBuilder.append("Ticket Number : " + extensionRequest.getIntent().getTicket().getTicketNumber() + "\n");
            respBuilder.append(" Data Customer Account " + account + "\n");
            respBuilder.append("Nama: " + name + "\n");
            respBuilder.append("Setoran tiap bulan : Rp. 500,000\n");
            respBuilder.append("Jatuh tempo berikutnya : 15 Agustus 2018");
        } else {
            respBuilder.append("Ticket Number : " + extensionRequest.getIntent().getTicket().getTicketNumber() + "\n");
            respBuilder.append(appProperties.getFormId() + " Data Customer Account " + account + "\n");
            respBuilder.append("Nama: " + name + "\n");
            respBuilder.append("Setoran tiap bulan : Rp. 1,000,000\n");
            respBuilder.append("Jatuh tempo berikutnya : 27 Agustus 2018");
        }
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        output.put(OUTPUT, respBuilder.toString());
        extensionResult.setValue(output);
        return extensionResult;
    }

    /*
	 * Modify Customer Name Entity
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#clearCustomerName(com.imi.
	 * dolphin.sdkwebservice.model.ExtensionRequest)
     */
    @Override
    public ExtensionResult modifyCustomerName(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> clearEntities = new HashMap<>();
        String name = getEasyMapValueByName(extensionRequest, "name");
        if (name.equalsIgnoreCase("reja")) {
            clearEntities.put("name", "budi");
            extensionResult.setEntities(clearEntities);
        }
        return extensionResult;
    }

    /*
	 * Sample Product info with static value
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getProductInfo(com.imi.dolphin
	 * .sdkwebservice.model.ExtensionRequest)
     */
    @Override
    public ExtensionResult getProductInfo(ExtensionRequest extensionRequest) {
        String model = getEasyMapValueByName(extensionRequest, "model");
        String type = getEasyMapValueByName(extensionRequest, "type");

        Map<String, String> output = new HashMap<>();
        StringBuilder respBuilder = new StringBuilder();

        respBuilder.append("Untuk harga mobil " + model + " tipe " + type + " adalah 800,000,000\n");
        respBuilder.append("Jika kak {customer_name} tertarik, bisa klik tombol dibawah ini. \n");
        respBuilder.append("Maka nanti live agent kami akan menghubungi kakak ;)");

        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        output.put(OUTPUT, respBuilder.toString());
        extensionResult.setValue(output);
        return extensionResult;
    }

    /*
	 * Get messages from third party service
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getMessageBody(com.imi.dolphin
	 * .sdkwebservice.model.ExtensionRequest)
     */
    @Override
    public ExtensionResult getMessageBody(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        StringBuilder respBuilder = new StringBuilder();

        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url("https://jsonplaceholder.typicode.com/comments").get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONArray jsonArray = new JSONArray(response.body().string());

            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String message = jsonObject.getString("body");
            respBuilder.append(message);
        } catch (Exception e) {

        }

        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        output.put(OUTPUT, respBuilder.toString());
        extensionResult.setValue(output);
        return extensionResult;
    }

    /*
	 * Generate quick replies output
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IFormService#getQuickReplies(com.imi.
	 * dolphin.sdkwebservice.model.ExtensionRequest)
     */
    @Override
    public ExtensionResult getQuickReplies(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Hello").add("Hello World", "hello world")
                .add("Hello Java", "B0F63CE1-F16F-4761-8881-F44C95D2792F").build();
        output.put(OUTPUT, quickReplyBuilder.string());
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }


    /*
	 * Generate buttons output
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getButtons(com.imi.dolphin.
	 * sdkwebservice.model.ExtensionRequest)
     */
    @Override
    public ExtensionResult getButtons(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();

        ButtonTemplate button = new ButtonTemplate();
        button.setTitle("This is title");
        button.setSubTitle("This is subtitle");
        button.setPictureLink(SAMPLE_IMAGE_PATH);
        button.setPicturePath(SAMPLE_IMAGE_PATH);
        List<EasyMap> actions = new ArrayList<>();
        EasyMap bookAction = new EasyMap();
        bookAction.setName("Label");
        bookAction.setValue("Payload");
        actions.add(bookAction);
        button.setButtonValues(actions);

        ButtonBuilder buttonBuilder = new ButtonBuilder(button);
        output.put(OUTPUT, buttonBuilder.build());

        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    /*
	 * Generate Carousel
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getCarousel(com.imi.dolphin.
	 * sdkwebservice.model.ExtensionRequest)
     */
    @Override
    public ExtensionResult getCarousel(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();

        ButtonTemplate button = new ButtonTemplate();
        button.setPictureLink(SAMPLE_IMAGE_PATH);
        button.setPicturePath(SAMPLE_IMAGE_PATH);
        button.setTitle("This is title");
        button.setSubTitle("This is subtitle");
        List<EasyMap> actions = new ArrayList<>();
        EasyMap bookAction = new EasyMap();
        bookAction.setName("Label");
        bookAction.setValue("Payload");
        actions.add(bookAction);
        button.setButtonValues(actions);
        ButtonBuilder buttonBuilder = new ButtonBuilder(button);

        ButtonTemplate button2 = new ButtonTemplate();
        button2.setPictureLink(SAMPLE_IMAGE_PATH);
        button2.setPicturePath(SAMPLE_IMAGE_PATH);
        button2.setTitle("This is title 2");
        button2.setSubTitle("This is subtitle 2");
        List<EasyMap> actions2 = new ArrayList<>();
        EasyMap bookAction2 = new EasyMap();
        bookAction2.setName("Label 2");
        bookAction2.setValue("Payload 2");
        actions2.add(bookAction2);
        button2.setButtonValues(actions2);
        ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);

        ButtonTemplate button3 = new ButtonTemplate();
        button3.setPictureLink(SAMPLE_IMAGE_PATH);
        button3.setPicturePath(SAMPLE_IMAGE_PATH);
        button3.setTitle("This is title 3");
        button3.setSubTitle("This is subtitle 3");
        button3.setButtonValues(actions2);
        ButtonBuilder buttonBuilder3 = new ButtonBuilder(button3);

        ButtonTemplate button4 = new ButtonTemplate();
        button4.setPictureLink(SAMPLE_IMAGE_PATH);
        button4.setPicturePath(SAMPLE_IMAGE_PATH);
        button4.setTitle("This is title 4");
        button4.setSubTitle("This is subtitle 4");
        button4.setButtonValues(actions2);
        ButtonBuilder buttonBuilder4 = new ButtonBuilder(button4);

        ButtonTemplate button5 = new ButtonTemplate();
        button5.setPictureLink(SAMPLE_IMAGE_PATH);
        button5.setPicturePath(SAMPLE_IMAGE_PATH);
        button5.setTitle("This is title 5");
        button5.setSubTitle("This is subtitle 5");
        button5.setButtonValues(actions2);
        ButtonBuilder buttonBuilder5 = new ButtonBuilder(button5);

        CarouselBuilder carouselBuilder = new CarouselBuilder(buttonBuilder.build(), buttonBuilder2.build(),
                buttonBuilder3.build(), buttonBuilder4.build(), buttonBuilder5.build());

        output.put(OUTPUT, carouselBuilder.build());

        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    /*
	 * Transfer ticket to agent
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#doTransferToAgent(com.imi.
	 * dolphin.sdkwebservice.model.ExtensionRequest)
     */
    @Override
    public ExtensionResult doTransferToAgent(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(true);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(false);
        return extensionResult;
    }

    /*
	 * Split bubble chat conversation
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#getSplitConversation(com.imi.
	 * dolphin.sdkwebservice.model.ExtensionRequest)
     */
    @Override
    public ExtensionResult getSplitConversation(ExtensionRequest extensionRequest) {
        String firstLine = "Terima kasih {customer_name}";
        String secondLine = "Data telah kami terima dan agent kami akan proses terlebih dahulu ya kak";
        Map<String, String> output = new HashMap<>();
        output.put(OUTPUT, firstLine + ParamSdk.SPLIT_CHAT + secondLine);

        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    @Override
    public ExtensionResult MenuDoctorSchedule(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();

        ButtonTemplate button1 = new ButtonTemplate();
//        button1.setPictureLink(SAMPLE_IMAGE_PATH);
//        button1.setPicturePath(SAMPLE_IMAGE_PATH);
        button1.setTitle("Silahkan Pilih Dokter");
        button1.setSubTitle("Berdasarkan AREA");
        List<EasyMap> actions1 = new ArrayList<>();

        EasyMap bookActionArea = new EasyMap();
        bookActionArea.setName("PILIH");
        bookActionArea.setValue("booking");
        actions1.add(bookActionArea);
        button1.setButtonValues(actions1);
        ButtonBuilder buttonBuilder1 = new ButtonBuilder(button1);

        ButtonTemplate button2 = new ButtonTemplate();
//        button2.setPictureLink(SAMPLE_IMAGE_PATH);
//        button2.setPicturePath(SAMPLE_IMAGE_PATH);
        button2.setTitle("Silahkan Pilih Dokter");
        button2.setTitle("Berdasarkan Nama");
        List<EasyMap> actions2 = new ArrayList<>();
        EasyMap bookActionName = new EasyMap();
        bookActionName.setName("PILIH");
        bookActionName.setValue("info dokter");
        actions2.add(bookActionName);
        button2.setButtonValues(actions2);
        ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);

        ButtonTemplate button3 = new ButtonTemplate();
//        button2.setPictureLink(SAMPLE_IMAGE_PATH);
//        button2.setPicturePath(SAMPLE_IMAGE_PATH);
        button3.setTitle("Silahkan Pilih Dokter");
        button3.setTitle("Berdasarkan Nama");
        List<EasyMap> actions3 = new ArrayList<>();
        EasyMap bookActionSpec = new EasyMap();
        bookActionSpec.setName("PILIH");
        bookActionSpec.setValue("info dokter");
        actions3.add(bookActionSpec);
        button3.setButtonValues(actions3);
        ButtonBuilder buttonBuilder3 = new ButtonBuilder(button3);

        CarouselBuilder carouselBuilder = new CarouselBuilder(buttonBuilder1.build(), buttonBuilder2.build(), buttonBuilder3.build());

        output.put(OUTPUT, carouselBuilder.build());
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    /*
	 * Send Location
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imi.dolphin.sdkwebservice.service.IService#doSendLocation(com.imi.dolphin
	 * .sdkwebservice.model.ExtensionRequest)
     */
    @Override
    public ExtensionResult doSendLocation(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Silahkan kirim Lokasi kamu sekarang ya.")
                .add("Location", "location").build();
        output.put(OUTPUT, quickReplyBuilder.string());
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    /**
     * Method ExecuteAPI General
     *
     * @param link
     * @return
     */
    private JSONObject GeneralExecuteAPI(String link) {
        OkHttpUtil okHttpUtil = new OkHttpUtil();
        okHttpUtil.init(true);
        JSONObject jsonobj = null;
        try {
            Request request = new Request.Builder().url(link).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            jsonobj = new JSONObject(response.body().string());
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return jsonobj;
    }

    // Flow Rumah Sakit Terdekat //
    @Override
    public ExtensionResult doGetHospitalTerdekat(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String lokasiUser = getEasyMapValueByName(extensionRequest, "lokasi");
        String[] alonglat = lokasiUser.split(";");
        String lat = alonglat[0];
        String longi = alonglat[1];

        String apiHospital = appProperties.getDummyHospital();
        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
        int leng = results.length();
        BigDecimal longitud;
        BigDecimal latitud;
        BigDecimal[][] point = new BigDecimal[leng][leng];
        int x = 0;
        double hasil;
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String hospitalName = jObj.getString("hospital_name");
            longitud = jObj.getBigDecimal("longitude");
            latitud = jObj.getBigDecimal("latitude");
            hasil = distanceInKilometers((Double.valueOf(lat)), (Double.valueOf(longi)), latitud.doubleValue(), longitud.doubleValue());
            if (hasil < 20) {
                point[x][0] = latitud;
                point[x][1] = longitud;

                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(hospitalName);
                button.setSubTitle(hospitalName);
                List<EasyMap> actions = new ArrayList<>();

                EasyMap bookAction = new EasyMap();
                EasyMap callAction = new EasyMap();

                bookAction.setName("Direction");
                bookAction.setValue(appProperties.getGoogleMapQuery() + "" + point[x][0] + "," + point[x][1]);
                actions.add(bookAction);
                button.setButtonValues(actions);

                callAction.setName("Call Center");
                callAction.setValue("tel:");
                actions.add(callAction);
                button.setButtonValues(actions);

                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);

                x++;
            }

        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    public double distanceInKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;
        return 6371.01 * Math.acos(Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1));
    }
    //---------------------------------//

    // Doctor Schedule Flow Search By Area,Hospital,Specialist //
    @Override
    public ExtensionResult doGetAreas(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        String apiArea = appProperties.getApiArea();
        JSONArray results = GeneralExecuteAPI(apiArea).getJSONArray("data");
        int leng = results.length();
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);

            String areaId = jObj.getString("area_id");
            String areaName = jObj.getString("area_name");

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(areaName);
            List<EasyMap> actions = new ArrayList<>();
            EasyMap bookAction = new EasyMap();
            bookAction.setName(areaName);
            bookAction.setValue(areaId);
            actions.add(bookAction);
            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

            String btnBuilder = buttonBuilder.build().toString();
            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    @Override
    public ExtensionResult doGetHospitalByArea(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String areaId = getEasyMapValueByName(extensionRequest, "area");

        String apiHospital = appProperties.getApiHospitalByArea() + areaId;
        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
        int leng = results.length();
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String hospitalId = jObj.getString("hospital_id");
            String hospitalName = jObj.getString("hospital_name");

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(hospitalName);
            List<EasyMap> actions = new ArrayList<>();
            EasyMap bookAction = new EasyMap();
            bookAction.setName(hospitalName);
            bookAction.setValue(hospitalId);
            actions.add(bookAction);
            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

            String btnBuilder = buttonBuilder.build().toString();
            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    @Override
    public ExtensionResult doGetSpecialistbyHospital(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String hospitalId = getEasyMapValueByName(extensionRequest, "hospital");

        String apiSpecialist = appProperties.getApiSpecialistbyHospital() + hospitalId;
        JSONArray results = GeneralExecuteAPI(apiSpecialist).getJSONArray("data");
        int leng = results.length();
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String specialistId = jObj.getString("specialization_id");
            String specialistName = jObj.getString("name_id");

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(specialistName);
            List<EasyMap> actions = new ArrayList<>();
            EasyMap bookAction = new EasyMap();
            bookAction.setName("Pilih");
            bookAction.setValue(specialistId);
            actions.add(bookAction);
            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

            String btnBuilder = buttonBuilder.build().toString();
            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    @Override
    public ExtensionResult doGetDoctorByHospitalAndSpecialist(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String hospitalId = getEasyMapValueByName(extensionRequest, "hospital");
        String specialistId = getEasyMapValueByName(extensionRequest, "specialist");

        String apiGetDokter = appProperties.getApiDoctorbyhospitalIdSpecialist() + hospitalId + "&specialistId=" + specialistId;
        JSONArray results = GeneralExecuteAPI(apiGetDokter).getJSONArray("data");
        int leng = results.length();
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String doctorId = jObj.getString("doctor_id");
            String doctorName = jObj.getString("doctor_name");
            String doctorSpecialist = jObj.getString("doctor_specialist");
            String doctorHospitals = jObj.getString("doctor_hospitals_unit");

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(doctorName);
            button.setSubTitle(doctorSpecialist + "\n" + doctorHospitals);
            List<EasyMap> actions = new ArrayList<>();

            EasyMap LihatJadwal = new EasyMap();

            LihatJadwal.setName("Lihat Jadwal");
            LihatJadwal.setValue(doctorId);
            actions.add(LihatJadwal);

            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

            String btnBuilder = buttonBuilder.build().toString();
            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    @Override
    public ExtensionResult doGetDoctorSchedule(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String doctorId = getEasyMapValueByName(extensionRequest, "doctor");
        String hospitalId = getEasyMapValueByName(extensionRequest, "hospital");
        Calendar calendar = Calendar.getInstance();

        String schedule = appProperties.getApiDoctorschedule() + doctorId + "/" + hospitalId;
        JSONArray results = GeneralExecuteAPI(schedule).getJSONArray("data");
        int leng = results.length();

        List<Integer> dayslist = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            String datenow = calendar.getTime().toString();
            String hari = datenow.substring(0, 3);
            String tanggal = datenow.substring(8, 10);
            String bulan = datenow.substring(4, 7);
            String tahun = datenow.substring(24, 28);
            int x = 0;
            int kodeHari = 0;
            String kodeBulan = Bulan(bulan);
            String date = tahun + "-" + kodeBulan + "-" + tanggal;
            String available = "";

            String[] daypoint = new String[leng];
            String scheduleTime = appProperties.getApiDoctorappointment() + hospitalId + "/doctorId/" + doctorId + "/date/" + date;
            if (GeneralExecuteAPI(scheduleTime).getInt("code") == 200) {
                JSONArray results2 = GeneralExecuteAPI(scheduleTime).getJSONArray("data");
                int leng2 = results2.length();
                for (int j = 0; j < leng2; j++) {
                    JSONObject jObj2 = results2.getJSONObject(j);
                    Boolean isFull = jObj2.getBoolean("is_full");

                    if (isFull.equals(false)) {
                        available = "Available";
                        break;
                    } else {
                        available = "Not Available";
                    }
                }
                for (int k = 0; k < leng; k++) {
                    JSONObject jObj = results.getJSONObject(k);
                    kodeHari = Hari(hari);
                    int daysnumber = jObj.getInt("doctor_schedule_day");
                    dayslist.add(daysnumber);
                    String fromtime = jObj.getString("doctor_schedule_from_time");
                    String totime = jObj.getString("doctor_schedule_to_time");
                    String dateF = fromtime.substring(11, 16);
                    String dateT = totime.substring(11, 16);
                    String jadwal = dateF + "-" + dateT;

                    if (daysnumber == kodeHari) {
                        if (daypoint[x] == null) {
                            daypoint[x] = jadwal;
                        } else {
                            daypoint[x] = daypoint[x] + "/" + jadwal;
                        }
                    }
//                    Date dnow = new Date();
//                    SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss SSS");
//                    sb.append(ft.format(dnow) + " # ");
                }
            }
            if (dayslist.contains(kodeHari)) {
                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(hari + ", " + tanggal + " " + bulan + " " + tahun);
                button.setSubTitle(daypoint[x] + "/n" + available);
                List<EasyMap> actions = new ArrayList<>();

                EasyMap bookAction = new EasyMap();

                bookAction.setName("Pilih");
                bookAction.setValue(date);
                actions.add(bookAction);

                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }
            x++;
            calendar.add(Calendar.DATE, +1);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    private int Hari(String day) {

        int kodeHari = 0;
        try {
            Date date = new SimpleDateFormat("EEE").parse(day);//put your day name here
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.DAY_OF_WEEK);
            month--;
            kodeHari = month;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kodeHari;
    }

    private String Bulan(String monthName) {
        String kodeBulan = "";
        try {
            Date date = new SimpleDateFormat("MMM").parse(monthName);//put your month name here
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int monthNum = cal.get(Calendar.MONTH);
            monthNum++;
            if (monthNum < 10) {
                kodeBulan = "0" + monthNum;
            } else {
                kodeBulan = monthNum + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kodeBulan;
    }

    public ExtensionResult doGetJamPraktekDokter(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String hospital = getEasyMapValueByName(extensionRequest, "hospital");
        String doctor = getEasyMapValueByName(extensionRequest, "doctor");
        String date = getEasyMapValueByName(extensionRequest, "hari");

        String getDoctorByName = appProperties.getApiDoctorappointment() + hospital + "/doctor/" + doctor + "/date/" + date;
        JSONArray results = GeneralExecuteAPI(getDoctorByName).getJSONArray("data");
        int leng = results.length();
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String schedulId = jObj.getString("schedule_id");
            String fromtime = jObj.getString("from_time");
            String totime = jObj.getString("to_time");

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(fromtime);
            List<EasyMap> actions = new ArrayList<>();
            EasyMap bookAction = new EasyMap();
            bookAction.setName("Pilih");
            bookAction.setValue(fromtime);
            actions.add(bookAction);
            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

            String btnBuilder = buttonBuilder.build().toString();
            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    //----------------------------//
    // Doctor Schedule Flow Search By Name //
    @Override
    public ExtensionResult doGetDoctorByName(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String nama = getEasyMapValueByName(extensionRequest, "name");

        String getDoctorByName = appProperties.getApiDoctorbyname() + nama;
        JSONArray results = GeneralExecuteAPI(getDoctorByName).getJSONArray("data");
        int leng = results.length();
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String doctorId = jObj.getString("doctor_id");
            String doctorName = jObj.getString("doctor_name");

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(doctorName);
            List<EasyMap> actions = new ArrayList<>();
            EasyMap bookAction = new EasyMap();
            bookAction.setName("Lihat Jadwal");
            bookAction.setValue(doctorId);
            actions.add(bookAction);
            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

            String btnBuilder = buttonBuilder.build().toString();
            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    @Override
    public ExtensionResult doGetScheduleByDoctorId(ExtensionRequest extensionRequest) {
        StringBuilder proctime = new StringBuilder();
        Date dnow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss SSS");
        proctime.append(ft.format(dnow) + " # ");

        Map<String, String> output = new HashMap<>();
        Map<String, String> extra = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        String doctorId = getEasyMapValueByName(extensionRequest, "doctor");
        Calendar calendar = Calendar.getInstance();

        String getDoctorByDoctorId = appProperties.getApiDoctorbydoctorid() + doctorId;
        JSONArray results3 = GeneralExecuteAPI(getDoctorByDoctorId).getJSONArray("data");
        int leng3 = results3.length();
        for (int l = 0; l < leng3; l++) {
            JSONObject jObj3 = results3.getJSONObject(l);
            String hospitalId = jObj3.getString("hospital_id");

            String schedule = appProperties.getApiDoctorschedule() + doctorId + "/" + hospitalId;
            JSONArray results = GeneralExecuteAPI(schedule).getJSONArray("data");
            int leng = results.length();
            List<Integer> dayslist = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                String datenow = calendar.getTime().toString();
                String hari = datenow.substring(0, 3);
                String tanggal = datenow.substring(8, 10);
                String bulan = datenow.substring(4, 7);
                String tahun = datenow.substring(24, 28);
                int x = 0;
                int kodeHari = 0;
                String kodeBulan = Bulan(bulan);
                String date = tahun + "-" + kodeBulan + "-" + tanggal;
                String available = "";

                String[] daypoint = new String[leng];
                String scheduleTime = appProperties.getApiDoctorappointment() + hospitalId + "/doctorId/" + doctorId + "/date/" + date;
                if (GeneralExecuteAPI(scheduleTime).getInt("code") == 200) {
                    JSONArray results2 = GeneralExecuteAPI(scheduleTime).getJSONArray("data");
                    int leng2 = results2.length();
                    for (int j = 0; j < leng2; j++) {
                        JSONObject jObj2 = results2.getJSONObject(j);
                        Boolean isFull = jObj2.getBoolean("is_full");

                        if (isFull.equals(false)) {
                            available = "Available";
                            break;
                        } else {
                            available = "Not Available";
                        }
                    }
                    for (int k = 0; k < leng; k++) {
                        JSONObject jObj = results.getJSONObject(k);
                        kodeHari = Hari(hari);
                        int daysnumber = jObj.getInt("doctor_schedule_day");
                        dayslist.add(daysnumber);
                        String fromtime = jObj.getString("doctor_schedule_from_time");
                        String totime = jObj.getString("doctor_schedule_to_time");
                        String dateF = fromtime.substring(11, 16);
                        String dateT = totime.substring(11, 16);
                        String jadwal = dateF + "-" + dateT;

                        if (daysnumber == kodeHari) {
                            if (daypoint[x] == null) {
                                daypoint[x] = jadwal;
                            } else {
                                daypoint[x] = daypoint[x] + "/" + jadwal;
                            }
                        }
                    }
                }
                if (dayslist.contains(kodeHari)) {
                    //Buat Button
                    ButtonTemplate button = new ButtonTemplate();
                    button.setTitle(hari + ", " + tanggal + " " + bulan + " " + tahun);
                    button.setSubTitle(daypoint[x] + "/n" + available);
                    List<EasyMap> actions = new ArrayList<>();

                    EasyMap bookAction = new EasyMap();
                    EasyMap callAction = new EasyMap();

                    bookAction.setName("Reservasi Online");
                    bookAction.setValue("https://www.siloamhospitals.com");
                    actions.add(bookAction);

                    callAction.setName("Call Center");
                    callAction.setValue("tel:1500181");
                    actions.add(callAction);

                    button.setButtonValues(actions);
                    ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                    String btnBuilder = buttonBuilder.build().toString();
                    sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                }
                x++;
                calendar.add(Calendar.DATE, +1);
            }
        }
        dnow = new Date();
        proctime.append(ft.format(dnow));

        output.put(OUTPUT, sb.toString());
        output.put("extra", proctime.toString());
        extensionResult.setValue(output);

        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        return extensionResult;
    }

    //-------------------------------------//
    // Get Docter by Specialist Name //
    @Override
    public ExtensionResult doGetSpecialistbyName(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
//        String nama = getEasyMapValueByName(extensionRequest, "name");
        String counter = getEasyMapValueByName(extensionRequest, "counter");

//        String getSpecialistByName = appProperties.getApiSpecialistbyname() + nama;
        String getSpecialistByName = appProperties.getApiSpecialistbyname();
        JSONArray results = GeneralExecuteAPI(getSpecialistByName).getJSONArray("data");
        int leng = results.length();
        if (leng > 9) {
            int count = Integer.parseInt(counter);
            switch (count) {
                case 1:
                    int leng1 = 9;
                    sb = carospec(sb, leng1, results);
                    break;
                case 2:
                    int leng2 = 18;
                    sb = carospec(sb, leng2, results);
                    break;
                case 3:
                    int leng3 = 27;
                    sb = carospec(sb, leng3, results);
                    break;
                case 4:
                    int leng4 = results.length();
                    sb = carospec(sb, leng4, results);
                    break;
            }
        } else {
            for (int i = 0; i < leng; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String specialistId = jObj.getString("specialization_id");
                String specialistName = jObj.getString("name_id");

                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(specialistName);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName("Pilih");
                bookAction.setValue(specialistId);
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }
        }

        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    private StringBuilder carospec(StringBuilder sb, int leng, JSONArray results) {

        for (int i = leng - 9; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String specialistId = jObj.getString("specialization_id");
            String specialistName = jObj.getString("name_id");

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(specialistName);
            List<EasyMap> actions = new ArrayList<>();

            EasyMap bookAction = new EasyMap();
            bookAction.setName(specialistName);
            bookAction.setValue(specialistId);
            actions.add(bookAction);

            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

            String btnBuilder = buttonBuilder.build().toString();
            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
        }
        if (leng < 27) {
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle("Lainnya");
            List<EasyMap> actions = new ArrayList<>();

            EasyMap bookAction = new EasyMap();
            bookAction.setName("Lainnya");
            bookAction.setValue("lainnya");
            actions.add(bookAction);

            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

            String btnBuilder = buttonBuilder.build();
            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
        }
        return sb;
    }

    @Override
    public ExtensionResult SetCounterSpecialist(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> clearEntities = new HashMap<>();

        clearEntities.put("counter", "1");
        extensionResult.setEntities(clearEntities);
        return extensionResult;
    }

    @Override
    public ExtensionResult SetKonfirmasiLainnya(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> clearEntities = new HashMap<>();
        String counter = getEasyMapValueByName(extensionRequest, "counter");
        String specialist = getEasyMapValueByName(extensionRequest, "specialist");
        if (specialist.equalsIgnoreCase("lainnya")) {
            int code = Integer.parseInt(counter) + 1;
            clearEntities.remove("specialist");
            clearEntities.put("counter", "" + code);
            clearEntities.put("konfirmasi", "");
            extensionResult.setEntities(clearEntities);
        } else {
            clearEntities.put("konfirmasi", "yes");
            extensionResult.setEntities(clearEntities);
        }
        return extensionResult;
    }

    @Override
    public ExtensionResult doGetDoctorBySpecialist(ExtensionRequest extensionRequest) {

        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        Map<String, String> output = new HashMap<>();
        String specialistId = getEasyMapValueByName(extensionRequest, "specialist");
        String apiGetDokter = appProperties.getApiDoctorbySpecialist() + specialistId;
        JSONArray results = GeneralExecuteAPI(apiGetDokter).getJSONArray("data");
        int leng = results.length();
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String doctorId = jObj.getString("doctor_id");
            String doctorName = jObj.getString("doctor_name");
            String doctorSpecialist = jObj.getString("doctor_specialist");
            String doctorHospitals = jObj.getString("doctor_hospitals_unit");

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(doctorName);
            button.setSubTitle(doctorSpecialist + "\n" + doctorHospitals);
            List<EasyMap> actions = new ArrayList<>();

            EasyMap LihatJadwal = new EasyMap();

            LihatJadwal.setName("Lihat Jadwal");
            LihatJadwal.setValue(doctorId);
            actions.add(LihatJadwal);

            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

            String btnBuilder = buttonBuilder.build().toString();
            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setValue(output);

        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        return extensionResult;
    }
    //-------------------------------------------------------------------//

    // Method Get List Specialist //
    @Override
    public ExtensionResult doGetSpecialistList1(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        String specialist = getEasyMapValueByName(extensionRequest, "specialist");

        if (specialist.contains("Lainnya")) {
            String getSpecialistPage1 = appProperties.getApiSpecialistbyname();
            JSONArray results = GeneralExecuteAPI(getSpecialistPage1).getJSONArray("data");
            int leng = 18;
            for (int i = 9; i < leng; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String specialistId = jObj.getString("specialization_id");
                String specialistName = jObj.getString("name_id");

                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(specialistName);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName(specialistName);
                bookAction.setValue(specialistId);
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle("Lainnya");
            List<EasyMap> actions = new ArrayList<>();

            EasyMap bookAction = new EasyMap();
            bookAction.setName("Lainnya");
            bookAction.setValue("Lainnya");
            actions.add(bookAction);

            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

            String btnBuilder = buttonBuilder.build().toString();
            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);

            output.put(OUTPUT, sb.toString());
            extensionResult.setAgent(false);
            extensionResult.setRepeat(false);
            extensionResult.setSuccess(true);
            extensionResult.setNext(true);
            extensionResult.setValue(output);
        } else {
            extensionResult.setAgent(false);
            extensionResult.setRepeat(false);
            extensionResult.setSuccess(true);
            extensionResult.setNext(true);

            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("specialist2", "Lainnya");
            clearEntities.put("specialist3", "Lainnya");
            clearEntities.put("specialist4", "Lainnya");

            extensionResult.setEntities(clearEntities);
        }

        return extensionResult;
    }

    //-------------------------------------------------------------------//
}
