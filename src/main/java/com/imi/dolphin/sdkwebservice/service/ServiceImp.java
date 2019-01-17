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
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder(null)
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
    
    // Flow Rumah Sakit Terdekat //
    @Override
    public ExtensionResult doGetHospitalTerdekat(ExtensionRequest extensionRequest
    ) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String lokasiUser = getEasyMapValueByName(extensionRequest, "lokasi");
        String[] alonglat = lokasiUser.split(";");
        String lat = alonglat[0];
        String longi = alonglat[1];
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getDummyHospital()).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
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
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiArea()).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
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

        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiHospital() + areaId).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
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

        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
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
    public ExtensionResult doGetSpecialist(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiSpecialist()).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
            int leng = results.length();

            for (int i = 0; i < leng; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String specialistId = jObj.getString("specialist_id");
                String specialistName = jObj.getString("specialist_name");

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

        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
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
    public ExtensionResult doGetDokterByHospitalAndSpecialist(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String hospitalId = getEasyMapValueByName(extensionRequest, "hospital");
        String specialistId = getEasyMapValueByName(extensionRequest, "specialist");
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiDoctorbydoctorIdSpecialist() + hospitalId + "&specialistId=" + specialistId).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
            int leng = results.length();

            for (int i = 0; i < leng; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String doctorId = jObj.getString("doctor_id");
                String doctorName = jObj.getString("doctor_name");
                String doctorSpecialist = jObj.getString("doctor_specialist");
                String doctorHospitals = jObj.getString("doctor_hospitals_unit");
                Boolean doctorAvailable = jObj.getBoolean("is_doctor_appointment");
                String available = "";
                String valueavailab = "";

                if (doctorAvailable == false) {
                    available = "Not Available Today";
                } else {
                    available = "Available Today";
                }

                if (available.equalsIgnoreCase("Not Available Today")) {
                    valueavailab = "Maaf Dokter Tidak Tersedia";
                } else {
                    valueavailab = "";
                }

                //Buat Button 
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(doctorName);
                button.setSubTitle(doctorSpecialist + "\n" + doctorHospitals);
                List<EasyMap> actions = new ArrayList<>();

                EasyMap AvailableToday = new EasyMap();
                EasyMap AvailableTomorrow = new EasyMap();
                EasyMap SelectOtherDate = new EasyMap();

                AvailableToday.setName(available);
                AvailableToday.setValue(valueavailab);
                actions.add(AvailableToday);

                AvailableTomorrow.setName("Tomorrow");
                AvailableTomorrow.setValue(doctorId);
                actions.add(AvailableTomorrow);

                SelectOtherDate.setName("Select Other Date");
                SelectOtherDate.setValue("");
                actions.add(SelectOtherDate);

                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
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

        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiDoctorschedule() + doctorId + "/" + hospitalId).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
            int leng = results.length();
            List<Integer> dayslist = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                String datenow = calendar.getTime().toString();
                String hari = datenow.substring(0, 3);
                String tanggal = datenow.substring(8, 10);
                String bulan = datenow.substring(4, 7);
                String tahun = datenow.substring(23, 28);
                int x = 0;
                int kodeHari = 0;
                String[] daypoint = new String[leng];
                for (int j = 0; j < leng; j++) {
                    JSONObject jObj = results.getJSONObject(j);

                    if (hari.equalsIgnoreCase("Mon")) {
                        kodeHari = 1;
                    } else if (hari.equalsIgnoreCase("Tue")) {
                        kodeHari = 2;
                    } else if (hari.equalsIgnoreCase("Wed")) {
                        kodeHari = 3;
                    } else if (hari.equalsIgnoreCase("Thu")) {
                        kodeHari = 4;
                    } else if (hari.equalsIgnoreCase("Fri")) {
                        kodeHari = 5;
                    } else if (hari.equalsIgnoreCase("Sat")) {
                        kodeHari = 6;
                    } else if (hari.equalsIgnoreCase("Sun")) {
                        kodeHari = 7;
                    }

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
                if (dayslist.contains(kodeHari)) {
                    //Buat Button 
                    ButtonTemplate button = new ButtonTemplate();
                    button.setTitle(hari + ", " + tanggal + " " + bulan + "" + tahun);
                    button.setSubTitle(daypoint[x]);
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

        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiDoctorbyname() + nama).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
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
                bookAction.setName(doctorName);
                bookAction.setValue(doctorId);
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
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
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String doctorId = getEasyMapValueByName(extensionRequest, "doctor");
        Calendar calendar = Calendar.getInstance();
        if (doctorId.contains("-")) {
            try {
                OkHttpUtil okHttpUtil = new OkHttpUtil();
                okHttpUtil.init(true);
                Request request = new Request.Builder().url(appProperties.getApiDoctorbydoctorid() + doctorId).get().build();
                Response response = okHttpUtil.getClient().newCall(request).execute();
                JSONObject jsonobj = new JSONObject(response.body().string());
                JSONArray results = jsonobj.getJSONArray("data");
                int leng = results.length();

                for (int i = 0; i < leng; i++) {
                    JSONObject jObj = results.getJSONObject(i);
                    String hospitalId = jObj.getString("hospital_id");
                    Request request2 = new Request.Builder().url(appProperties.getApiDoctorschedule() + doctorId + "/" + hospitalId).get().build();
                    Response response2 = okHttpUtil.getClient().newCall(request2).execute();
                    JSONObject jsonobj2 = new JSONObject(response2.body().string());
                    JSONArray results2 = jsonobj2.getJSONArray("data");
                    int leng2 = results2.length();
                    List<Integer> dayslist = new ArrayList<>();

                    for (int j = 0; j < 7; j++) {
                        String datenow = calendar.getTime().toString();
                        String hari = datenow.substring(0, 3);
                        String tanggal = datenow.substring(8, 10);
                        String bulan = datenow.substring(4, 7);
                        String tahun = datenow.substring(23, 28);
                        int x = 0;
                        int kodeHari = 0;
                        String[] daypoint = new String[leng2];
                        for (int k = 0; k < leng2; k++) {
                            JSONObject jObj2 = results2.getJSONObject(k);

                            if (hari.equalsIgnoreCase("Mon")) {
                                kodeHari = 1;
                            } else if (hari.equalsIgnoreCase("Tue")) {
                                kodeHari = 2;
                            } else if (hari.equalsIgnoreCase("Wed")) {
                                kodeHari = 3;
                            } else if (hari.equalsIgnoreCase("Thu")) {
                                kodeHari = 4;
                            } else if (hari.equalsIgnoreCase("Fri")) {
                                kodeHari = 5;
                            } else if (hari.equalsIgnoreCase("Sat")) {
                                kodeHari = 6;
                            } else if (hari.equalsIgnoreCase("Sun")) {
                                kodeHari = 7;
                            }

                            int daysnumber = jObj2.getInt("doctor_schedule_day");
                            dayslist.add(daysnumber);

                            String fromtime = jObj2.getString("doctor_schedule_from_time");
                            String totime = jObj2.getString("doctor_schedule_to_time");
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
                        if (dayslist.contains(kodeHari)) {
                            //Buat Button 
                            ButtonTemplate button = new ButtonTemplate();
                            button.setTitle(hari + ", " + tanggal + " " + bulan + "" + tahun);
                            button.setSubTitle(daypoint[x]);
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

            } catch (MalformedURLException ex) {
                Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            }
            output.put(OUTPUT, sb.toString());
            extensionResult.setValue(output);

        } else {
            output.put("name", doctorId);
            output.put("doctor", null);

            extensionResult.setEntities(output);
        }
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        return extensionResult;
    }

    //-------------------------------------//
    

    // Get Specialist By Name //
    @Override
    public ExtensionResult doGetSpecialistbyName(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String nama = getEasyMapValueByName(extensionRequest, "nama");
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiSpecialistbyname() + nama).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
            int leng = results.length();

            for (int i = 0; i < leng; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String specialistId = jObj.getString("specialist_id");
                String specialistName = jObj.getString("specialist_name");

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

        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }
    //-----------------------------------//

    // Method Get List Specialist //
    @Override
    public ExtensionResult doGetSpecialistPage1(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiSpecialist()).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
            int leng = 9;

            for (int i = 0; i < leng; i++) {
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

        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
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
    public ExtensionResult doGetSpecialistPage2(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiSpecialist()).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
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

        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }
    
    public ExtensionResult doGetSpecialistPage3(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiSpecialist()).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
            int leng = 27;

            for (int i = 18; i < leng; i++) {
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

        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }
    
    public ExtensionResult doGetSpecialistPage4(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiSpecialist()).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
            int leng = 37;

            for (int i = 27; i < leng; i++) {
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

        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        output.put(OUTPUT, sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }
    //-------------------------------------------------------------------//
}
