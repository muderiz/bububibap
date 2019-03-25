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
import com.imi.dolphin.sdkwebservice.builder.FormBuilder;
import com.imi.dolphin.sdkwebservice.builder.QuickReplyBuilder;
import com.imi.dolphin.sdkwebservice.model.ButtonTemplate;
import com.imi.dolphin.sdkwebservice.model.CreateAppointment;
import com.imi.dolphin.sdkwebservice.model.CreatePatient;
import com.imi.dolphin.sdkwebservice.model.EasyMap;
import com.imi.dolphin.sdkwebservice.model.ExtensionRequest;
import com.imi.dolphin.sdkwebservice.model.ExtensionResult;
import com.imi.dolphin.sdkwebservice.model.MonthBuilder;
import com.imi.dolphin.sdkwebservice.param.ParamSdk;
import com.imi.dolphin.sdkwebservice.property.AppProperties;
import com.imi.dolphin.sdkwebservice.util.OkHttpUtil;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author reja
 *
 */
@Service
public class ServiceImp implements IService {

    public static final String OUTPUT = "output";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CONTACT_NAME = "contact_name";
    public static final String CONTACT_PHONE_NAME = "contact_phone_number";

    private static final String SAMPLE_IMAGE_PATH = "https://goo.gl/SHdL8D";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String CONSTANT_SPLIT_SYNTAX = "&split&";

    private static final String QUICK_REPLY_SYNTAX = "{replies:title=";
    private static final String COMMA = ",";
    private static final String QUICK_REPLY_SYNTAX_SUFFIX = "}";
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
        EasyMap bookAction2 = new EasyMap();
        bookAction.setName("Label");
        bookAction.setValue("Payload");
        actions.add(bookAction);

        bookAction2.setName("Label");
        bookAction2.setValue("Payload");
        actions.add(bookAction2);
        button.setButtonValues(actions);
        ButtonBuilder buttonBuilder = new ButtonBuilder(button);

//        ButtonTemplate button2 = new ButtonTemplate();
//        button2.setPictureLink(SAMPLE_IMAGE_PATH);
//        button2.setPicturePath(SAMPLE_IMAGE_PATH);
//        button2.setTitle("This is title 2");
//        button2.setSubTitle("This is subtitle 2");
//        List<EasyMap> actions2 = new ArrayList<>();
//        EasyMap bookAction2 = new EasyMap();
//        bookAction2.setName("Label 2");
//        bookAction2.setValue("Payload 2");
//        actions2.add(bookAction2);
//        button2.setButtonValues(actions2);
//        ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
//
//        ButtonTemplate button3 = new ButtonTemplate();
//        button3.setPictureLink(SAMPLE_IMAGE_PATH);
//        button3.setPicturePath(SAMPLE_IMAGE_PATH);
//        button3.setTitle("This is title 3");
//        button3.setSubTitle("This is subtitle 3");
//        button3.setButtonValues(actions2);
//        ButtonBuilder buttonBuilder3 = new ButtonBuilder(button3);
//
//        ButtonTemplate button4 = new ButtonTemplate();
//        button4.setPictureLink(SAMPLE_IMAGE_PATH);
//        button4.setPicturePath(SAMPLE_IMAGE_PATH);
//        button4.setTitle("This is title 4");
//        button4.setSubTitle("This is subtitle 4");
//        button4.setButtonValues(actions2);
//        ButtonBuilder buttonBuilder4 = new ButtonBuilder(button4);
//
//        ButtonTemplate button5 = new ButtonTemplate();
//        button5.setPictureLink(SAMPLE_IMAGE_PATH);
//        button5.setPicturePath(SAMPLE_IMAGE_PATH);
//        button5.setTitle("This is title 5");
//        button5.setSubTitle("This is subtitle 5");
//        button5.setButtonValues(actions2);
//        ButtonBuilder buttonBuilder5 = new ButtonBuilder(button5);
        CarouselBuilder carouselBuilder = new CarouselBuilder(buttonBuilder.build());

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

    public ExtensionResult getForms(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        FormBuilder formBuilder = new FormBuilder("");
        ButtonTemplate button = new ButtonTemplate();
        button.setTitle("Title is here");
        button.setSubTitle("Subtitle is here");
        button.setPictureLink("Picture link");
        button.setPicturePath("Picture path");
        List<EasyMap> actions = new ArrayList<>();
        EasyMap bookAction = new EasyMap();
        bookAction.setName("Label here");
        bookAction.setValue(formBuilder.build());
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

    @Override
    public ExtensionResult MenuUtama(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();

        //Button 1
        ButtonTemplate button1 = new ButtonTemplate();
        button1.setPictureLink(SAMPLE_IMAGE_PATH);
        button1.setPicturePath(SAMPLE_IMAGE_PATH);
        button1.setTitle("Appointment");
        button1.setSubTitle("");
        List<EasyMap> actions1 = new ArrayList<>();
        EasyMap butAction11 = new EasyMap();
        EasyMap butAction12 = new EasyMap();

        butAction11.setName("Jadwal Dokter");
        butAction11.setValue("cari dokter");
        actions1.add(butAction11);

        butAction12.setName("Siloam Terdekat");
        butAction12.setValue("siloam terdekat");
        actions1.add(butAction12);

        button1.setButtonValues(actions1);
        ButtonBuilder buttonBuilder1 = new ButtonBuilder(button1);

        //Button 2
        ButtonTemplate button2 = new ButtonTemplate();
        button2.setPictureLink(SAMPLE_IMAGE_PATH);
        button2.setPicturePath(SAMPLE_IMAGE_PATH);
        button2.setTitle("General Enquires");
        button2.setSubTitle("");
        List<EasyMap> actions2 = new ArrayList<>();
        EasyMap butAction21 = new EasyMap();
        EasyMap butAction22 = new EasyMap();

        butAction21.setName("MCU");
        butAction21.setValue("daftar mcu");
        actions2.add(butAction21);

        butAction22.setName("Informasi");
        butAction22.setValue("faq");
        actions2.add(butAction22);

        button2.setButtonValues(actions2);
        ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
//
//        //Button 3
//        ButtonTemplate button3 = new ButtonTemplate();
//        button3.setPictureLink(SAMPLE_IMAGE_PATH);
//        button3.setPicturePath(SAMPLE_IMAGE_PATH);
//        button3.setTitle("Inquiry");
//        button3.setSubTitle("");
//        List<EasyMap> actions3 = new ArrayList<>();
//        EasyMap butAction31 = new EasyMap();
//        EasyMap butAction32 = new EasyMap();
//        EasyMap butAction33 = new EasyMap();
//
//        butAction31.setName("F.A.Q");
//        butAction31.setValue("faq");
//        actions3.add(butAction31);
//
//        butAction32.setName("BPJS");
//        butAction32.setValue("bpjs kesehatan");
//        actions3.add(butAction32);
//
//        butAction33.setName("Karir");
//        butAction33.setValue("lowongan kerja");
//        actions3.add(butAction33);
//        button3.setButtonValues(actions3);
//        ButtonBuilder buttonBuilder3 = new ButtonBuilder(button3);
//
//        //Button 4
//        ButtonTemplate button4 = new ButtonTemplate();
//        button4.setPictureLink(SAMPLE_IMAGE_PATH);
//        button4.setPicturePath(SAMPLE_IMAGE_PATH);
//        button4.setTitle("Medical Check Up");
//        button4.setSubTitle("");
//        List<EasyMap> actions4 = new ArrayList<>();
//        EasyMap butAction41 = new EasyMap();
//        EasyMap butAction42 = new EasyMap();
//        EasyMap butAction43 = new EasyMap();
//
//        butAction41.setName("Pendaftaran MCU");
//        butAction41.setValue("daftar mcu");
//        actions4.add(butAction41);
//
//        butAction42.setName("Paket MCU");
//        butAction42.setValue("paket mcu");
//        actions4.add(butAction42);
//
//        butAction43.setName("Persiapan MCU");
//        butAction43.setValue("persiapan mcu");
//        actions4.add(butAction43);
//        button4.setButtonValues(actions4);
//        ButtonBuilder buttonBuilder4 = new ButtonBuilder(button4);
//
//        //Button 5
//        ButtonTemplate button5 = new ButtonTemplate();
//        button5.setPictureLink(SAMPLE_IMAGE_PATH);
//        button5.setPicturePath(SAMPLE_IMAGE_PATH);
//        button5.setTitle("Feedback");
//        button5.setSubTitle("");
//        List<EasyMap> actions5 = new ArrayList<>();
//        EasyMap butAction51 = new EasyMap();
//        EasyMap butAction52 = new EasyMap();
//        EasyMap butAction53 = new EasyMap();
//
//        butAction51.setName("Komplain");
//        butAction51.setValue("komplain");
//        actions5.add(butAction51);
//
//        butAction52.setName("Komplimen");
//        butAction52.setValue("komplimen");
//        actions5.add(butAction52);
//
//        butAction53.setName("Kotak Saran");
//        butAction53.setValue("kotak saran");
//        actions5.add(butAction53);
//        button5.setButtonValues(actions5);
//        ButtonBuilder buttonBuilder5 = new ButtonBuilder(button5);

        CarouselBuilder carouselBuilder = new CarouselBuilder(buttonBuilder1.build(), buttonBuilder2.build());

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
        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Silakan kirim lokasi Anda sekarang.")
                .add("Kirim Lokasi", "location").build();
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
    public ExtensionResult doSendLocationSpecialist(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        String konfirmasi = getEasyMapValueByName(extensionRequest, "konfirmasi");
        if (konfirmasi.equalsIgnoreCase("yes")) {
            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Silahkan kirim Lokasi kamu sekarang ya.")
                    .add("Kirim Lokasi", "location").build();
            output.put(OUTPUT, quickReplyBuilder.string());
        } else {
            String dialogfail = "Maaf gagal mengambil lokasi.";
            output.put(OUTPUT, dialogfail);
        }

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

        double latitude = extensionRequest.getIntent().getTicket().getLatitude();
        double longitude = extensionRequest.getIntent().getTicket().getLongitude();
//        String longitude = getEasyMapValueByName(extensionRequest, "longitude");
//        String latitude = getEasyMapValueByName(extensionRequest, "latitude");

        String apiHospitalDummy = appProperties.getDummyHospital();
//        String apiHospital = appProperties.getApiHospital();
        JSONArray results = GeneralExecuteAPI(apiHospitalDummy).getJSONArray("data");
        int leng = results.length();
        BigDecimal longitud;
        BigDecimal latitud;
        List<List<String>> data = new ArrayList<>();
        double hasil;
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String hospitalid = jObj.getString("hospital_id");
            String hospitalname = jObj.getString("hospital_name");
            longitud = jObj.getBigDecimal("longitude");
            latitud = jObj.getBigDecimal("latitude");
            String phonenumber = jObj.getString("phoneNumber");

            hasil = distanceInKilometers((Double.valueOf(latitude)), (Double.valueOf(longitude)), latitud.doubleValue(), longitud.doubleValue());
//            hasil = distanceInKilometers((Double.valueOf(latit)), (Double.valueOf(longi)), latitud.doubleValue(), longitud.doubleValue());
            List<String> jarak = new ArrayList<>();
            if (hasil < 30) {
                jarak.add(hasil + "");
                jarak.add(hospitalid);
                jarak.add(hospitalname);
                jarak.add(phonenumber);
                data.add(jarak);
            }
        }
        Collections.sort(data, new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                return Double.valueOf(o1.get(0)).compareTo(Double.valueOf(o2.get(0)));
            }
        });
        for (int j = 0; j < data.size(); j++) {
            String idhospital = data.get(j).get(1);
            String namehospital = data.get(j).get(2);
            String phonenum = data.get(j).get(3);
            if (phonenum.equalsIgnoreCase("")) {
                phonenum = "000";
            }
            ButtonTemplate button = new ButtonTemplate();
            button.setPictureLink(appProperties.getSiloamLogo());
//            button.setPicturePath("");
            button.setTitle(namehospital);
            button.setSubTitle(namehospital);
            List<EasyMap> actions = new ArrayList<>();

            EasyMap direction = new EasyMap();
            EasyMap callAction = new EasyMap();
            EasyMap booknow = new EasyMap();

            booknow.setName("Book Now");
            booknow.setValue("dokter by area di dummy di " + idhospital + " konter 0");
            actions.add(booknow);
            button.setButtonValues(actions);

            callAction.setName("Call Hospital");
            callAction.setValue("mau call hospital nomor " + phonenum + " ya");
            actions.add(callAction);
            button.setButtonValues(actions);

            direction.setName("Direction");
            direction.setValue(appProperties.getGoogleMapQuery() + namehospital);
            actions.add(direction);
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

    public double distanceInKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;
        return 6371.01 * Math.acos(Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1));
    }

    @Override
    public ExtensionResult doCallHospital(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        String callhospital = getEasyMapValueByName(extensionRequest, "callhospital");
        String dialog = "";
        if (callhospital.equalsIgnoreCase("000")) {
            dialog = "Maaf. nomor telepon Hospital tersebut belum tersedia.";
        } else {
            dialog = "Berikut nomor telepon Hospital tersebut yang dapat kamu hubungi " + callhospital;
        }
        output.put(OUTPUT, dialog);

        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        extensionResult.setValue(output);
        return extensionResult;
    }

    //---------------------------------//
    // Doctor Schedule Flow Search By Area, Hospital, Specialist //
    @Override
    public ExtensionResult doGetAreas(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        StringBuilder quickBuilder = new StringBuilder();

        String apiArea = appProperties.getApiArea();
        JSONArray results = GeneralExecuteAPI(apiArea).getJSONArray("data");
        int leng = results.length();
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String areaId = jObj.getString("area_id");
            String areaName = jObj.getString("area_name");
            //Buat QuickReplay
//            quickBuilder.append(areaName).append("@===@").append(areaId).append(COMMA);

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(areaName);
            button.setSubTitle(areaName);
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
//        sb.append(QUICK_REPLY_SYNTAX);
//        sb.append("Silahkan pilih area yang ingin kamu tuju.").append(COMMA);
//        sb.append(quickBuilder);
//        sb.replace(sb.toString().length() - 1, sb.toString().length(), "");
//        sb.append(QUICK_REPLY_SYNTAX_SUFFIX);

        String dialog = "Silahkan pilih area yang ingin kamu tuju.";
        String area = sb.toString();
        output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + area);
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
        String areaId = getEasyMapValueByName(extensionRequest, "areaid");

        String apiHospital = appProperties.getApiHospitalByArea() + areaId;
        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
        int leng = results.length();
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String hospitalId = jObj.getString("hospital_id");
            String hospitalName = jObj.getString("hospital_name");

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setPictureLink(appProperties.getSiloamLogo());
            button.setTitle(hospitalName);
            button.setSubTitle(hospitalName);
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
        Map<String, String> clearEntities = new HashMap<>();

        clearEntities.put("counter", "0");
        extensionResult.setEntities(clearEntities);
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    @Override
    public ExtensionResult SiloamGetSpecialistByHospital(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        StringBuilder btnbuild = new StringBuilder();
        ExtensionResult extensionResult = new ExtensionResult();
        String area = getEasyMapValueByName(extensionRequest, "areaid");
        String hospital = getEasyMapValueByName(extensionRequest, "hospitalid");
        String counter = getEasyMapValueByName(extensionRequest, "counter");
        String stat = "";

        if (area.equalsIgnoreCase("dummy")) {
            String apiHospital = appProperties.getApiHospitalbyId() + hospital;
            JSONObject jobj = GeneralExecuteAPI(apiHospital);
            if (jobj.getInt("code") == 200) {
                stat = "hospital";
            }
        } else {
            String apiHospital = appProperties.getApiHospitalByArea() + area;
            JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
            int lenghospital = results.length();
            for (int i = 0; i < lenghospital; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String hospitalId = jObj.getString("hospital_id");
                String hospitalName = jObj.getString("hospital_name");
                hospitalName = hospitalName.toLowerCase();
                if (hospital.equalsIgnoreCase(hospitalId)) {
                    stat = "hospital";
                    break;
                }
                if (hospital.equalsIgnoreCase(hospitalName)) {
                    stat = "hospital";
                    hospital = hospitalId;
                    break;
                }
            }
        }

        if (stat.equalsIgnoreCase("hospital")) {
            int code = Integer.parseInt(counter);
            try {
                OkHttpUtil okHttpUtil = new OkHttpUtil();
                okHttpUtil.init(true);
//                Request request = new Request.Builder().url(appProperties.getApiSpecialistbyHospital() + hospital).get().build();
                Request request = new Request.Builder().url(appProperties.getApiSpecialistbyHospital() + hospital + "&top10=true").get().build();
                Response response = okHttpUtil.getClient().newCall(request).execute();
                JSONObject jsonobj = new JSONObject(response.body().string());
                JSONArray data = jsonobj.getJSONArray("data");
                //code = code - 1;
                int leng;
                leng = leng(code, data);
                sb = carospec(sb, leng, data);

            } catch (Exception e) {
            }

            String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin kamu tuju.";
            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());

        } else {
            Map<String, String> clearEntities = new HashMap<>();
//            clearEntities.put("areaid", hospital + "");
            clearEntities.put("hospitalid", "");
            extensionResult.setEntities(clearEntities);

            String apiHosArea = appProperties.getApiHospitalByArea() + hospital;
            JSONArray results2 = GeneralExecuteAPI(apiHosArea).getJSONArray("data");
            int leng = results2.length();
            for (int i = 0; i < leng; i++) {
                JSONObject jObj = results2.getJSONObject(i);
                String hospitalId = jObj.getString("hospital_id");
                String hospitalName = jObj.getString("hospital_name");

                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(hospitalName);
                button.setSubTitle(hospitalName);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName(hospitalName);
                bookAction.setValue(hospitalId);
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);

                output.put(OUTPUT, sb.toString());
            }
        }

        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

//    private String CekEntitas(String areaid, String hospitalid, String spesialisid) {
//        String area = areaid;
//
//        String hospital = hospitalid;
//
//        String spesialis = spesialisid;
//
//    }
    @Override
    public ExtensionResult SetKonfirmasiSpesialisbyHospital(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> clearEntities = new HashMap<>();
        String spesialisid = getEasyMapValueByName(extensionRequest, "spesialisid");
        String counter = getEasyMapValueByName(extensionRequest, "counter");
        String hospitalid = getEasyMapValueByName(extensionRequest, "hospitalid");

        String[] splitspesialis = spesialisid.split(" ");
        String spesial1 = splitspesialis[0];

        int code = Integer.parseInt(counter);
        if (spesialisid.equalsIgnoreCase("lainnya")) {
            code++;
            clearEntities.put("counter", "" + code);
            clearEntities.put("spesialisid", "");
            extensionResult.setEntities(clearEntities);
            //------------------------------------------------------------------------//
            Map<String, String> output = new HashMap<>();
            StringBuilder sb = new StringBuilder();

            try {
                OkHttpUtil okHttpUtil = new OkHttpUtil();
                okHttpUtil.init(true);
                Request request = new Request.Builder().url(appProperties.getApiSpecialistbyHospital() + hospitalid + "&high10=true").get().build();
                Response response = okHttpUtil.getClient().newCall(request).execute();
                JSONObject jsonobj = new JSONObject(response.body().string());
                JSONArray data = jsonobj.getJSONArray("data");
                int leng;
                leng = leng(code, data);
                sb = carospec(sb, leng, data);
            } catch (Exception e) {
            }
            String dialog = "Silakan pilih spesialisasi yang ingin dituju.";
            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());
            //-----------------------------------------------------------------------------
            extensionResult.setValue(output);
        } else if (spesial1.equalsIgnoreCase("spesialis")) {
            clearEntities.put("konfirmasi", "yes");
            extensionResult.setEntities(clearEntities);
        } else {
            clearEntities.put("spesialisid", "");
            extensionResult.setEntities(clearEntities);
            Map<String, String> output = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            try {
                OkHttpUtil okHttpUtil = new OkHttpUtil();
                okHttpUtil.init(true);
                Request request = new Request.Builder().url(appProperties.getApiSpecialistbyname() + spesialisid).get().build();
                Response response = okHttpUtil.getClient().newCall(request).execute();
                JSONObject jsonobj = new JSONObject(response.body().string());
                if (jsonobj.getInt("code") == 200) {
                    JSONArray data = jsonobj.getJSONArray("data");
                    int leng;
                    leng = data.length();
                    sb = carospec(sb, leng, data);
                    String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silakan pilih spesialisasi yang ingin dituju.";
                    output.put(OUTPUT, truecase + ParamSdk.SPLIT_CHAT + sb.toString());
                } else {
                    request = new Request.Builder().url(appProperties.getApiSpecialistbyHospital() + hospitalid).get().build();
                    // request = new Request.Builder().url(appProperties.getApiSpecialist()).get().build();
                    response = okHttpUtil.getClient().newCall(request).execute();
                    jsonobj = new JSONObject(response.body().string());
                    JSONArray data = jsonobj.getJSONArray("data");
                    int leng;
                    leng = leng(code, data);
                    sb = carospec(sb, leng, data);
                    String falsecase1 = "Maaf {bot_name} tidak dapat menemukan spesialis yang kamu cari.";
                    String falsecase2 = "Silahkan pilih Spesialis dibawah ini. Atau ketik kembali nama spesialis yang kamu inginkan dengan benar.";
                    output.put(OUTPUT, falsecase1 + ParamSdk.SPLIT_CHAT + falsecase2 + ParamSdk.SPLIT_CHAT + sb.toString());
                }
            } catch (Exception e) {
            }
            //-----------------------------------------------------------------------------

            extensionResult.setValue(output);
        }
        return extensionResult;
    }

    @Override
    public ExtensionResult SiloamGetDoctorByHospitalAndSpecialist(ExtensionRequest extensionRequest
    ) {
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        Map<String, String> output = new HashMap<>();
        String spesialisid = getEasyMapValueByName(extensionRequest, "spesialisid");
        String hospitalid = getEasyMapValueByName(extensionRequest, "hospitalid");
        String[] splitspesialis = spesialisid.split(" ");
        String spesial1 = splitspesialis[2];

        String apiGetDokter = appProperties.getApiDoctorbyhospitalIdSpecialist() + hospitalid + "&specialistId=" + spesial1;
//        String apiGetDokter = appProperties.getDummyDoctor();

        JSONArray results = GeneralExecuteAPI(apiGetDokter).getJSONArray("data");
        int leng = results.length();
        if (leng > 10) {
            leng = 10;
        }
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String doctorId = jObj.getString("doctor_id");
            String doctorName = jObj.getString("doctor_name");
            String doctorSpecialist = jObj.getString("doctor_specialist");
            String doctorHospitals = jObj.getString("doctor_hospitals_unit");

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(doctorName);
            button.setSubTitle(doctorSpecialist + "<br/>" + doctorHospitals);
            List<EasyMap> actions = new ArrayList<>();

            EasyMap LihatJadwal = new EasyMap();
            LihatJadwal.setName(doctorName);
            LihatJadwal.setValue("booking dokter id " + doctorId);
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
    //--------------------------------------------------//

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
            button.setSubTitle(doctorSpecialist + "<br/>" + doctorHospitals);
            List<EasyMap> actions = new ArrayList<>();

            EasyMap LihatJadwal = new EasyMap();

            LihatJadwal.setName(doctorName);
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

    @Override
    public ExtensionResult doGetDoctorSchedule(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String doctorId = getEasyMapValueByName(extensionRequest, "doctor");
        String hospitalId = getEasyMapValueByName(extensionRequest, "hospital");
        Calendar calendar = Calendar.getInstance();

        String[] iddokter = doctorId.split(" ");
        String dokid = iddokter[1];

        String schedule = appProperties.getApiDoctorschedule() + dokid + "/" + hospitalId;
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
            String scheduleTime = appProperties.getApiDoctorappointment() + hospitalId + "/doctorId/" + dokid + "/date/" + date;
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
                button.setSubTitle(daypoint[x] + "\n" + available);
                List<EasyMap> actions = new ArrayList<>();

                EasyMap bookAction = new EasyMap();

                bookAction.setName("Pilih " + date);
                bookAction.setValue(date + " / " + daypoint[x]);
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

    /**
     * Get Kategori Jam Praktek, Post to API Booking Siloam
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult KategoriJam(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> output = new HashMap<>();

        String doctorId = getEasyMapValueByName(extensionRequest, "dokterid");
        String[] iddokter = doctorId.split(" ");
        String dokid = iddokter[1];
        String tanggalpesan = getEasyMapValueByName(extensionRequest, "tanggalpesan");
        String[] jampilihan = tanggalpesan.split("=");
        String jam = jampilihan[0];
        String tanggal = jampilihan[1];

        String newsplit = "";
        String newjam = jam.replace(" ", ":");
        if (newjam.contains("t")) {
            String[] jamsplit = newjam.split("t");
            String split1 = jamsplit[0];
            String split2 = jamsplit[1];
            newsplit = split2 + " & " + split1;
        } else {
            newsplit = newjam;
        }

        String getLiveHospital = appProperties.getApiHospitalLive();
        JSONArray araylivehos = GeneralExecuteAPI(getLiveHospital).getJSONArray("data");

        String getDoctorByDoctorId = appProperties.getApiDoctorbydoctorid() + dokid;
        JSONArray results = GeneralExecuteAPI(getDoctorByDoctorId).getJSONArray("data");
        JSONObject jObj = results.getJSONObject(0);
        String hospitalId = jObj.getString("hospital_id");
        String doctorname = jObj.getString("doctor_name");

        String getNameHospital = appProperties.getApiHospitalbyId() + hospitalId;
        JSONArray results2 = GeneralExecuteAPI(getNameHospital).getJSONArray("data");
        JSONObject jObj2 = results2.getJSONObject(0);
        String hospitalName = jObj2.getString("hospital_name");

        String getSchedule = appProperties.getApiDoctorappointment() + hospitalId + "/doctorId/" + dokid + "/date/" + tanggal;
        JSONArray arraySchedule = GeneralExecuteAPI(getSchedule).getJSONArray("data");

        //Cek Live Hospital
        String hasilcekhospital = CekHospital(hospitalId, araylivehos);
        if (hasilcekhospital.equalsIgnoreCase("ada")) {
            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("jampraktek", "1");
            extensionResult.setEntities(clearEntities);
//            String cb = CarouselJamDinamic(newjam);
            String carojam = CarouselJamPraktek(arraySchedule);
            String dialog = "Silahkan pilih waktu kunjungan antara " + newsplit + " yang Anda kehendaki.";
            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + carojam);
        } else {
            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("kategorijam", "1");
            clearEntities.put("jampraktek", "2");
            clearEntities.put("namapasien", "3");
            clearEntities.put("tanggallahir", "4");
            clearEntities.put("notelp", "5");
            clearEntities.put("confirm", "6");
            extensionResult.setEntities(clearEntities);
            String dialog1 = "Untuk melakukan reservasi Dokter " + doctorname + " di " + hospitalName + ".";
            String dialog2 = "Silahkan hubungi Call Center berikut: +1500181";

            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + dialog2);
        }

        extensionResult.setValue(output);
        return extensionResult;
    }

    private String CarouselJamDinamic(String jam) {
        // TODO Auto-generated method stub
        String[] jamArray = jam.split("-");
        int jamStart = Integer.parseInt(jamArray[0].split(":")[0]);
        int jamFinish = Integer.parseInt(jamArray[1].split(":")[0]);
        int selisihJam = jamFinish - jamStart + 1;
        int jumlahButton = (selisihJam / 3) + (selisihJam % 3);
        int jamFlag = jamStart;
//        int loopBtnAction = 0;

        StringBuilder sb = new StringBuilder();

        for (int x = 0; x < jumlahButton; x++) {
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(String.format("%02d:00", jamFlag) + " - "
                    + (jamFlag + 2 <= jamFinish ? String.format("%02d:00", jamFlag + 2) : String.format("%02d:00", jamFinish)));
            button.setSubTitle("");

            List<EasyMap> actions = new ArrayList<>();
            for (int y = 0; y < 3; y++) {
                EasyMap btnAction = new EasyMap();
                btnAction.setName(String.format("%02d:00", jamFlag));
                btnAction.setValue(String.format("pilih jam %02d:00", jamFlag));
                actions.add(btnAction);
                jamFlag++;
                if (jamFlag > jamFinish) {
                    break;
                }
            }

            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
            sb.append(buttonBuilder.build()).append(CONSTANT_SPLIT_SYNTAX);
        }

        return sb.toString();
    }

    private String CarouselJamPraktek(JSONArray jampraktek) {
        int jampraktekleng = jampraktek.length();
        int jumlahbagi = (jampraktekleng / 3);
        int jumlahsisa = (jampraktekleng % 3);
        if (jumlahsisa > 0) {
            jumlahbagi++;
        }

//        int loopBtnAction = 0;
        StringBuilder sb = new StringBuilder();
        int index = 0;

        String totime2 = "";
        for (int x = 0; x < jumlahbagi; x++) {
            ButtonTemplate button = new ButtonTemplate();
            String titlejam = "";
            List<EasyMap> actions = new ArrayList<>();
            for (int y = 0; y < 3; y++) {
                if (index == jampraktekleng) {
                    titlejam += " - " + totime2;
                    break;
                }
                JSONObject jsono = jampraktek.getJSONObject(index);
                String fromtime = jsono.getString("from_time");
                totime2 = jsono.getString("to_time");
                EasyMap btnAction = new EasyMap();
                btnAction.setName(fromtime + " - " + totime2);
                btnAction.setValue(fromtime);
                actions.add(btnAction);
                if (y == 0) {
                    titlejam = fromtime;
                } else if (y == 2 || index == jampraktekleng) {
                    titlejam += " - " + totime2;
                }

                index++;
            }
            button.setTitle(titlejam);
            button.setSubTitle("Yang Tersedia :");

            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
            sb.append(buttonBuilder.build()).append(CONSTANT_SPLIT_SYNTAX);
        }

        return sb.toString();
    }

    public CarouselBuilder CarouselJam() {
        //Button 1
        ButtonTemplate button1 = new ButtonTemplate();
        button1.setTitle("08:00 - 10:00");
        button1.setSubTitle("");
        List<EasyMap> actions1 = new ArrayList<>();
        EasyMap butAction11 = new EasyMap();
        EasyMap butAction12 = new EasyMap();
        EasyMap butAction13 = new EasyMap();
        butAction11.setName("08:00");
        butAction11.setValue("pilih jam 08:00");
        actions1.add(butAction11);

        butAction12.setName("09:00");
        butAction12.setValue("pilih jam 09:00");
        actions1.add(butAction12);

        butAction13.setName("10:00");
        butAction13.setValue("pilih jam 10:00");
        actions1.add(butAction13);
        button1.setButtonValues(actions1);
        ButtonBuilder buttonBuilder1 = new ButtonBuilder(button1);

        //Button 2
        ButtonTemplate button2 = new ButtonTemplate();
        button2.setTitle("11:00 - 13:00");
        button2.setSubTitle("");
        List<EasyMap> actions2 = new ArrayList<>();
        EasyMap butAction21 = new EasyMap();
        EasyMap butAction22 = new EasyMap();
        EasyMap butAction23 = new EasyMap();

        butAction21.setName("11:00");
        butAction21.setValue("pilih jam 11:00");
        actions2.add(butAction21);

        butAction22.setName("12:00");
        butAction22.setValue("pilih jam 12:00");
        actions2.add(butAction22);

        butAction23.setName("13:00");
        butAction23.setValue("pilih jam 13:00");
        actions2.add(butAction23);
        button2.setButtonValues(actions2);
        ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);

        //Button 3
        ButtonTemplate button3 = new ButtonTemplate();
        button3.setTitle("14:00 - 16:00");
        button3.setSubTitle("");
        List<EasyMap> actions3 = new ArrayList<>();
        EasyMap butAction31 = new EasyMap();
        EasyMap butAction32 = new EasyMap();
        EasyMap butAction33 = new EasyMap();

        butAction31.setName("14:00");
        butAction31.setValue("pilih jam 14:00");
        actions3.add(butAction31);

        butAction32.setName("15:00");
        butAction32.setValue("pilih jam 15:00");
        actions3.add(butAction32);

        butAction33.setName("16:00");
        butAction33.setValue("pilih jam 16:00");
        actions3.add(butAction33);
        button3.setButtonValues(actions3);
        ButtonBuilder buttonBuilder3 = new ButtonBuilder(button3);

        //Button 4
        ButtonTemplate button4 = new ButtonTemplate();
        button4.setTitle("17:00 - 19:00");
        button4.setSubTitle("");
        List<EasyMap> actions4 = new ArrayList<>();
        EasyMap butAction41 = new EasyMap();
        EasyMap butAction42 = new EasyMap();
        EasyMap butAction43 = new EasyMap();

        butAction41.setName("17:00");
        butAction41.setValue("pilih jam 17:00");
        actions4.add(butAction41);

        butAction42.setName("18:00");
        butAction42.setValue("pilih jam 18:00");
        actions4.add(butAction42);

        butAction43.setName("19:00");
        butAction43.setValue("pilih jam 19:00");
        actions4.add(butAction43);
        button4.setButtonValues(actions4);
        ButtonBuilder buttonBuilder4 = new ButtonBuilder(button4);

        //Button 5
        ButtonTemplate button5 = new ButtonTemplate();
        button5.setTitle("20:00 - 21:00");
        button5.setSubTitle("");
        List<EasyMap> actions5 = new ArrayList<>();
        EasyMap butAction51 = new EasyMap();
        EasyMap butAction52 = new EasyMap();
        EasyMap butAction53 = new EasyMap();

        butAction51.setName("20:00");
        butAction51.setValue("pilih jam 20:00");
        actions5.add(butAction51);

        butAction52.setName("21:00");
        butAction52.setValue("pilih jam 21:00");
        actions5.add(butAction52);

        butAction53.setName("22:00");
        butAction53.setValue("pilih jam 22:00");
        actions5.add(butAction53);
        button5.setButtonValues(actions5);
        ButtonBuilder buttonBuilder5 = new ButtonBuilder(button5);

        CarouselBuilder carouselBuilder = new CarouselBuilder(buttonBuilder1.build(), buttonBuilder2.build(),
                buttonBuilder3.build(), buttonBuilder4.build(), buttonBuilder5.build());

        return carouselBuilder;
    }

    /**
     * Get Jam Praktek, Post to API Booking Siloam
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult doGetJamPraktekDokter(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String dokterid = getEasyMapValueByName(extensionRequest, "dokterid");
        String[] iddokter = dokterid.split(" ");
        String dokid = iddokter[1];

        String date = getEasyMapValueByName(extensionRequest, "tanggalpesan");
        String[] tanggalpesan = date.split("=");
        String jamtanggal = tanggalpesan[0];
        String tanggal = tanggalpesan[1];
        jamtanggal = jamtanggal.replace(" ", ":");

        String kategorijam = getEasyMapValueByName(extensionRequest, "kategorijam");
        String[] katjam = kategorijam.split(" ");
        String jam = katjam[2];

        String getDoctorByDoctorId = appProperties.getApiDoctorbydoctorid() + dokid;
        JSONArray results3 = GeneralExecuteAPI(getDoctorByDoctorId).getJSONArray("data");
        JSONObject jObj3 = results3.getJSONObject(0);
        String hospital = jObj3.getString("hospital_id");

        String getSchedule = appProperties.getApiDoctorappointment() + hospital + "/doctorId/" + dokid + "/date/" + tanggal;
        JSONArray results = GeneralExecuteAPI(getSchedule).getJSONArray("data");
        int leng = results.length();
        int index = 0;
        int indexmin = 0;
        int indexmax = 0;
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String fromtime = jObj.getString("from_time");
            if (jam.equals(fromtime.substring(0, 2))) {
                index = i;

                if (index == 0) {
                    indexmin = index;
                    indexmax = index + 5;
                } else if (index == leng - 1) {
                    indexmin = index - 5;
                    indexmax = index;
                } else {
                    indexmin = index - 5;
                    indexmax = index + 5;
                    if (indexmax > leng) {
                        indexmax = leng;
                    }
                    if (indexmin < 0) {
                        indexmin = 0;
                    }
                }
                for (int j = indexmin; j < indexmax; j++) {
                    JSONObject jObj2 = results.getJSONObject(j);
                    String fromtime2 = jObj2.getString("from_time");
                    Boolean isfull = jObj2.getBoolean("is_full");

                    if (isfull == false) {
                        //Buat Button
                        ButtonTemplate button = new ButtonTemplate();
                        button.setTitle(fromtime2);
                        button.setSubTitle("");
                        List<EasyMap> actions = new ArrayList<>();
                        EasyMap bookAction = new EasyMap();
                        bookAction.setName("Pilih " + fromtime2);
                        bookAction.setValue("praktek " + fromtime2);
                        actions.add(bookAction);
                        button.setButtonValues(actions);
                        ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                        String btnBuilder = buttonBuilder.build().toString();
                        sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                    }
                }
                break;
            }
        }

        if (sb.toString().equals("")) {
            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("kategorijam", "");
            extensionResult.setEntities(clearEntities);

            String dialog1 = "Maaf. Jam yang kamu pilih tidak tersedia dalam jadwal Dokter pada Hari Tersebut.";
            String dialog2 = "Silahkan pilih waktu kunjungan antara " + jamtanggal + " yang anda kehendaki.";

            String[] jampilihan = date.split("=");
            String arraydate = jampilihan[0];
            String newjam = arraydate.replace(" ", ":");
            String cb = CarouselJamDinamic(newjam);
            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + dialog2 + ParamSdk.SPLIT_CHAT + cb);
        } else {
            output.put(OUTPUT, sb.toString());

        }

        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    @Override
    public ExtensionResult doPostCreateAppointment(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        CreatePatient createPatient = new CreatePatient();
        CreateAppointment createAppointment = new CreateAppointment();

        String dokterid = getEasyMapValueByName(extensionRequest, "dokterid");
        String namapasien = getEasyMapValueByName(extensionRequest, "namapasien");
        String tanggallahir = getEasyMapValueByName(extensionRequest, "tanggallahir");
        String notelp = getEasyMapValueByName(extensionRequest, "notelp");
        String tanggalPesan = getEasyMapValueByName(extensionRequest, "tanggalpesan");
        String jamPraktek = getEasyMapValueByName(extensionRequest, "kategorijam");

        String jammenit = jamPraktek.replace(" ", ":");
//        String[] splitjam = jamPraktek.split(" ");
//        String jam = splitjam[1];
//        String menit = splitjam[2];
//        String jammenit = jam + ":" + menit;

        String[] iddokter = dokterid.split(" ");
        String dokid = iddokter[1];

        String[] tanggal = tanggalPesan.split("=");
//        String jamtanggal = tanggal[0];
//        String jampesan = jamtanggal.replace(" ", ":");
        String date = tanggal[1];

        String getDoctorByDoctorId = appProperties.getApiDoctorbydoctorid() + dokid;
        JSONArray results3 = GeneralExecuteAPI(getDoctorByDoctorId).getJSONArray("data");
        JSONObject jObj3 = results3.getJSONObject(0);
        String hospital = jObj3.getString("hospital_id");

        createPatient.setName(namapasien);
        createPatient.setDate_of_birth(tanggallahir);
        createPatient.setPhone_number(notelp);
        createPatient.setHospital_id(hospital);
        createPatient.setUser_id(appProperties.getUserId());
        String pasien = createPatient.build();
        String contactid = "";
        OkHttpUtil okHttpUtil = new OkHttpUtil();
        okHttpUtil.init(true);
        try {
            String url = appProperties.getCreatePatient();
            RequestBody body = RequestBody.create(JSON, pasien);
            Request request = new Request.Builder().url(url).post(body).addHeader("Content-Type", "application/json").build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            if (jsonobj.getInt("code") == 200) {
                JSONArray results = jsonobj.getJSONArray("data");
                JSONObject jObj = results.getJSONObject(0);
                contactid = jObj.getString("contact_id");
            } else {
                contactid = "no";

            }
        } catch (IOException | JSONException e) {
        }

        String getScheduleId = appProperties.getApiDoctorappointment() + hospital + "/doctorId/" + dokid + "/date/" + date;
        JSONArray results = GeneralExecuteAPI(getScheduleId).getJSONArray("data");
        int leng = results.length();
        String idschedule = "";
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String fromtime = jObj.getString("from_time");
            String scheduleid = jObj.getString("schedule_id");
            if (jammenit.equals(fromtime)) {
                idschedule = scheduleid;
                break;
            }
        }
        Map<String, String> sex = new HashMap<>();
        Map<String, String> city = new HashMap<>();
        Map<String, String> district = new HashMap<>();
        Map<String, String> subdistrict = new HashMap<>();
        Map<String, String> nationality = new HashMap<>();
        Map<String, String> emergency_contact_detail = new HashMap<>();

        String bookingid = null;
        String bookingno = null;
        String contact = null;
        String name = namapasien;
        String dob = tanggallahir;
        String nophone = notelp;
        String Adress1 = "Jakarta";
        String Adress2 = "";
        String email = "no-reply@siloamhospitals.com";
        String sexid = "1";
        String sexname = "Male";
        String cityid = "1";
        String cityname = "Jakarta";
        String districtid = "1 ";
        String districtname = "";
        String subdistrictid = "1";
        String subdistrictname = "";
        String nationalityid = "1";
        String nationalityname = "";
        String contact_name = "";
        String contact_phone_number = "";

        //Jika Pasien Lama//
        if (!contactid.equalsIgnoreCase("no")) {
            contact = contactid;
            name = null;
            dob = null;
            nophone = null;
            Adress1 = null;
            Adress2 = null;
            email = null;
            sexid = null;
            sexname = null;
            cityid = null;
            cityname = null;
            districtid = null;
            districtname = null;
            subdistrictid = null;
            subdistrictname = null;
            nationalityid = null;
            nationalityname = null;
            contact_name = null;
            contact_phone_number = null;
        }

        createAppointment.setBooking_id(bookingid);
        createAppointment.setBooking_type_id(appProperties.getBookingTypeId());
        createAppointment.setBooking_no(bookingno);
        createAppointment.setBooking_date(date);
        createAppointment.setBooking_time(jammenit);
        createAppointment.setNote("");
        createAppointment.setSchedule_id(idschedule);
        createAppointment.setHospital_id(hospital);
        createAppointment.setDoctor_id(dokid);
        createAppointment.setUser_id(appProperties.getUserId());
        createAppointment.setIs_waiting_list(false);
        createAppointment.setContact_id(contact);
        createAppointment.setName(name);
        createAppointment.setDate_of_birth(dob);
        createAppointment.setPhone_number(nophone);
        createAppointment.setAddress_line_1(Adress1);
        createAppointment.setAddress_line_2(Adress2);
        createAppointment.setEmail(email);

        sex.put("id", sexid);
        sex.put("name", sexname);
        createAppointment.setSex(sex);

        city.put("id", cityid);
        city.put("name", cityname);
        createAppointment.setCity(city);

        district.put("id", districtid);
        district.put("name", districtname);
        createAppointment.setDistrict(district);

        subdistrict.put("id", subdistrictid);
        subdistrict.put("name", subdistrictname);
        createAppointment.setSubdistrict(subdistrict);

        nationality.put("id", nationalityid);
        nationality.put("name", nationalityname);
        createAppointment.setNationality(nationality);

        emergency_contact_detail.put("contact_name", contact_name);
        emergency_contact_detail.put("contact_phone_number", contact_phone_number);
        createAppointment.setEmergency_contact_detail(emergency_contact_detail);

        JSONObject ca = new JSONObject(createAppointment);
        String appointment = ca.toString();
        try {
            String url = appProperties.getCreateAppointment();
            RequestBody body = RequestBody.create(JSON, appointment);
            Request request = new Request.Builder().url(url).post(body).addHeader("Content-Type", "application/json").build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());

            if (jsonobj.getInt("code") == 200) {
                JSONArray results2 = jsonobj.getJSONArray("data");
                JSONObject jObj = results2.getJSONObject(0);
                String booking_date = jObj.getString("booking_date");
                String booking_time = jObj.getString("booking_time");
                String patient_name = jObj.optString("contact_name");
                String doctor_name = jObj.getString("doctor_name");

                String dialog1 = "Terima kasih. \n"
                        + "Silvia telah berhasil mendaftarkan perjanjian Anda.";
                sb.append("Berikut ini data informasi untuk Anda. \n");
                sb.append("Nama : " + patient_name + "\n");
                sb.append("Nomor Telepon : " + nophone + "\n");
                sb.append("Nama Dokter : " + doctor_name + "\n");
                sb.append("Tanggal Pemesanan : " + booking_date + "\n");
                sb.append("Waktu Pemesanan : " + booking_time);
                output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());
            } else {
                String dialog1 = "Mohon maaf. {bot_name} belum bisa mendaftarkan perjanjian Anda.";
                output.put(OUTPUT, dialog1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);

//        Map<String, String> clearEntities = new HashMap<>();
//        clearEntities.put("contactId", sb.toString());
//        extensionResult.setEntities(clearEntities);
        return extensionResult;
    }

    //----------------------------//
    // get Dokter By Nama //
    @Override
    public ExtensionResult tanyaNama(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        sb.append("Silahkan ketik nama Dokter yang ingin kamu kunjungi. Untuk {bot_name} bantu carikan.");
        output.put(OUTPUT, sb.toString());
        extensionResult.setValue(output);

        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        return extensionResult;
    }

    @Override
    public ExtensionResult validasiNama(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        String namadokter = getEasyMapValueByName(extensionRequest, "namadokter");

        String getDoctorByName = appProperties.getApiDoctorbyname() + namadokter;
        int code = GeneralExecuteAPI(getDoctorByName).getInt("code");
        if (code == 200) {
            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("validasinama", "yes");
            extensionResult.setEntities(clearEntities);
        } else {
            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("namadokter", "");
            extensionResult.setEntities(clearEntities);
            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik tombol di bawah ini jika kamu ingin kembali ke Menu Utama. "
                    + "Atau ketikan kembali Nama Dokter yang ingin kamu kunjungi dengan benar.")
                    .add("Menu Utama", "menu utama").build();

            String dialog1 = "Mohon maaf, {bot_name} tidak dapat menemukan nama Dokter yang kamu ketikan.";
//            String dialog2 = "Silahkan ketik kembali nama Dokter yang ingin kamu kunjungi dengan benar.";

            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + quickReplyBuilder.string());
            extensionResult.setValue(output);

        }
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        return extensionResult;
    }

    @Override
    public ExtensionResult doGetDoctorByName(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String namadokter = getEasyMapValueByName(extensionRequest, "namadokter");

        String getDoctorByName = appProperties.getApiDoctorbyname() + namadokter;
        JSONArray results = GeneralExecuteAPI(getDoctorByName).getJSONArray("data");
        int leng = results.length();
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String doctorId = jObj.getString("doctor_id");

            String apiGetDokter = appProperties.getApiDoctorbydoctorid() + doctorId;
            JSONArray results2 = GeneralExecuteAPI(apiGetDokter).getJSONArray("data");
            int leng2 = results2.length();
            for (int j = 0; j < leng2; j++) {
                JSONObject jObj2 = results2.getJSONObject(j);
                String doctorName = jObj2.getString("doctor_name");
                String doctorSpecialist = jObj2.getString("doctor_specialist");
                String doctorHospitals = jObj2.getString("doctor_hospitals_unit");

                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(doctorName);
                button.setSubTitle(doctorSpecialist + "<br/>" + doctorHospitals);
                List<EasyMap> actions = new ArrayList<>();

                EasyMap LihatJadwal = new EasyMap();
                LihatJadwal.setName(doctorName);
                LihatJadwal.setValue("booking dokter id " + doctorId);
                actions.add(LihatJadwal);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }
        }
        output.put(OUTPUT, sb.toString());

        extensionResult.setValue(output);
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        return extensionResult;
    }
    //-----------------------//

    // get Jadwal by DokterId //
    @Override
    public ExtensionResult doGetScheduleByDoctorId(ExtensionRequest extensionRequest) {
        StringBuilder proctime = new StringBuilder();
        Date dnow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss SSS");
        proctime.append(ft.format(dnow) + " # ");

        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        String doctorId = getEasyMapValueByName(extensionRequest, "dokterid");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +1);
        String[] iddokter = doctorId.split(" ");
        String dokid = iddokter[1];

        String getDoctorByDoctorId = appProperties.getApiDoctorbydoctorid() + dokid;
        JSONArray results3 = GeneralExecuteAPI(getDoctorByDoctorId).getJSONArray("data");
        JSONObject jObj3 = results3.getJSONObject(0);
        String hospitalId = jObj3.getString("hospital_id");

        String schedule = appProperties.getApiDoctorschedule() + dokid + "/" + hospitalId;
        JSONArray results = GeneralExecuteAPI(schedule).getJSONArray("data");
        int leng = results.length();
        List<Integer> dayslist = new ArrayList<>();
        for (int i = 0; i < 7;) {
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

            //Cek Cuti
            String hasilcekcuti = CekCuti(dokid, date);
            if (hasilcekcuti.equalsIgnoreCase("tidak")) {
                String[] daypoint = new String[leng];
                String[] daypoint2 = new String[leng];

                //Cek Slot
                String scheduleTime = appProperties.getApiDoctorappointment() + hospitalId + "/doctorId/" + dokid + "/date/" + date;
                JSONObject jobj = GeneralExecuteAPI(scheduleTime);
                if (jobj.getInt("code") == 200) {
                    JSONArray results2 = jobj.getJSONArray("data");
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
                                daypoint2[x] = jadwal;
                            } else {
                                daypoint[x] = jadwal + " / " + daypoint[x];
                                daypoint2[x] = daypoint2[x] + "t" + jadwal;
                            }
                        }
                    }
                    i++;
                }

                if (dayslist.contains(kodeHari)) {
                    //Buat Button
                    ButtonTemplate button = new ButtonTemplate();
                    button.setTitle(hari + ", " + tanggal + " " + bulan + " " + tahun);
                    button.setSubTitle(daypoint[x] + " | " + available);
                    List<EasyMap> actions = new ArrayList<>();

                    EasyMap bookAction = new EasyMap();
                    bookAction.setName("Pilih");
                    bookAction.setValue(daypoint2[x] + "=" + date);
                    actions.add(bookAction);
                    button.setButtonValues(actions);
                    ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                    String btnBuilder = buttonBuilder.build().toString();
                    sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                }
            }

            x++;
            calendar.add(Calendar.DATE, +1);
        }
        dnow = new Date();
        proctime.append(ft.format(dnow));
        String stringbuild = sb.toString();
        if (sb.toString().isEmpty() || sb.toString().equalsIgnoreCase("")) {
            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("tanggalpesan", "1");
            clearEntities.put("kategorijam", "11");
            clearEntities.put("jampraktek", "2");
            clearEntities.put("namapasien", "3");
            clearEntities.put("tanggallahir", "4");
            clearEntities.put("notelp", "5");
            clearEntities.put("confirm", "6");
            extensionResult.setEntities(clearEntities);
            String StringOutput = "Maaf. {bot_name} tidak dapat menemukan Jadwal Dokter pilihan Kamu.";
            stringbuild = StringOutput;

        }
        output.put(OUTPUT, stringbuild);
        output.put("extra", proctime.toString());
        extensionResult.setValue(output);

        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        return extensionResult;
    }

    private String CekCuti(String id, String tanggal) {

        String cuti = "";
        String getLeaveDoctor = appProperties.getApiLeaveDoctor() + id;
        JSONArray leavearray = GeneralExecuteAPI(getLeaveDoctor).getJSONArray("data");
        int leaveleng = leavearray.length();
        for (int i = 0; i < leaveleng; i++) {
            JSONObject jobjcuti = leavearray.getJSONObject(i);
            String fromdate = jobjcuti.getString("from_date");
            String[] splitfromdate = fromdate.split("T");
            String awalcuti = splitfromdate[0];
            if (tanggal.equalsIgnoreCase(awalcuti)) {
                cuti = "iya";
                break;
            } else {
                cuti = "tidak";
            }
        }

        return cuti;
    }

    private String CekHospital(String id, JSONArray livehos) {

        String hospital = "";
        int leaveleng = livehos.length();
        for (int i = 0; i < leaveleng; i++) {
            JSONObject jobjlivehos = livehos.getJSONObject(i);
            String hospitalId = jobjlivehos.getString("hospital_id");
            if (id.equalsIgnoreCase(hospitalId)) {
                hospital = "ada";
                break;
            } else {
                hospital = "tidak";
            }
        }

        return hospital;
    }

    //-------------------------------------//
    // Get Dokter by Spesialis //
    @Override
    public ExtensionResult SiloamGetSpecialistbyName(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        ExtensionResult extensionResult = new ExtensionResult();
        int code = 0;

        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiSpecialist() + "?top10=true").get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray data = jsonobj.getJSONArray("data");
            int leng;
            leng = leng(code, data);
            sb = carospec(sb, leng, data);

        } catch (Exception e) {
        }

        Map<String, String> clearEntities = new HashMap<>();

        clearEntities.put("counter", "0");
        extensionResult.setEntities(clearEntities);

        String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin kamu tuju.";
        output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    public int leng(int code, JSONArray data) {
        int leng;
        switch (code) {
            // ubah jadi top 10 spesialis
            case 0:
                leng = 10;
                break;
            case 1:
                leng = 9;
                break;
            case 2:
                leng = 18;
                break;
            case 3:
                leng = 27;
                break;
            case 4:
                leng = 36;
                break;
            case 5:
                leng = 45;
                break;
            default:
                leng = data.length();
                break;
        }
        return leng;
    }

    private StringBuilder carospec(StringBuilder sb, int leng, JSONArray data) {
        if (leng == 10) {
            for (int i = 0; i < leng; i++) {
                JSONObject jObj = data.getJSONObject(i);
                String id_spesialis = jObj.getString("specialization_id");
                String nameEn = jObj.getString("name_en");
                String name = jObj.getString("name_id");
                //Buat Button 
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(name);
                button.setSubTitle(nameEn);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName(name);
                bookAction.setValue("spesialis id " + id_spesialis);
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }
        } else {
            int minus = 9;
            if (leng < 9) {
                minus = leng;
            }
            for (int i = leng - minus; i < leng; i++) {
                JSONObject jObj = data.getJSONObject(i);
                String id_spesialis = jObj.getString("specialization_id");
                String nameEn = jObj.getString("name_en");
                String name = jObj.getString("name_id");
                //Buat Button 
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(name);
                button.setSubTitle(nameEn);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName(name);
                bookAction.setValue("spesialis id " + id_spesialis);
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }
            int minus2 = 9;
            if (leng == minus2 || leng > minus2) {
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle("lainnya");
                button.setSubTitle("");
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName("Pilih");
                bookAction.setValue("lainnya");
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }
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

        clearEntities.put("namaspesialis", "set");
        clearEntities.put("counter", "0");
        extensionResult.setEntities(clearEntities);
        return extensionResult;
    }

    @Override
    public ExtensionResult SetKonfirmasiSpesialis(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> clearEntities = new HashMap<>();
        String konfirmasi = getEasyMapValueByName(extensionRequest, "spesialisid");
        String counter = getEasyMapValueByName(extensionRequest, "counter");

        String[] splitspesialis = konfirmasi.split(" ");
        String spesial1 = splitspesialis[0];

        int code = Integer.parseInt(counter);
        if (konfirmasi.equalsIgnoreCase("lainnya")) {
            code++;
            clearEntities.put("counter", code + "");
            clearEntities.put("spesialisid", "");
            extensionResult.setEntities(clearEntities);
            //------------------------------------------------------------------------//
            Map<String, String> output = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            try {
                OkHttpUtil okHttpUtil = new OkHttpUtil();
                okHttpUtil.init(true);
                Request request = new Request.Builder().url(appProperties.getApiSpecialist()).get().build();
                Response response = okHttpUtil.getClient().newCall(request).execute();
                JSONObject jsonobj = new JSONObject(response.body().string());
                JSONArray data = jsonobj.getJSONArray("data");
                int leng;
                leng = leng(code, data);
                sb = carospec(sb, leng, data);
            } catch (Exception e) {
            }
            String dialog = "Silakan pilih spesialisasi yang ingin dituju.";
            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());
            extensionResult.setValue(output);
        } else if (spesial1.equalsIgnoreCase("id")) {
            clearEntities.put("konfirmasi", "yes");
            extensionResult.setEntities(clearEntities);

            Map<String, String> output = new HashMap<>();
            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Silahkan kirim Lokasi kamu sekarang ya.")
                    .add("Kirim Lokasi", "location").build();
            output.put(OUTPUT, quickReplyBuilder.string());

            extensionResult.setValue(output);

        } else {
            clearEntities.put("spesialisid", "");
            extensionResult.setEntities(clearEntities);

            Map<String, String> output = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            try {
                OkHttpUtil okHttpUtil = new OkHttpUtil();
                okHttpUtil.init(true);
                Request request = new Request.Builder().url(appProperties.getApiSpecialistbyname() + konfirmasi).get().build();
                Response response = okHttpUtil.getClient().newCall(request).execute();
                JSONObject jsonobj = new JSONObject(response.body().string());
                if (jsonobj.getInt("code") == 200) {
                    JSONArray data = jsonobj.getJSONArray("data");
                    int leng;
                    leng = data.length();
                    sb = carospec(sb, leng, data);
                    String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin kamu kunjungi.";
                    output.put(OUTPUT, truecase + ParamSdk.SPLIT_CHAT + sb.toString());
                } else {
                    request = new Request.Builder().url(appProperties.getApiSpecialist() + "?top10=true").get().build();
                    response = okHttpUtil.getClient().newCall(request).execute();
                    jsonobj = new JSONObject(response.body().string());
                    JSONArray data = jsonobj.getJSONArray("data");
                    int leng;
                    leng = leng(code, data);
                    sb = carospec(sb, leng, data);
                    String falsecase1 = "Maaf {bot_name} tidak dapat menemukan spesialis yang kamu cari.";
                    String falsecase2 = "Silahkan pilih Spesialis dibawah ini. Atau ketik kembali nama spesialis yang kamu inginkan dengan benar.";
                    output.put(OUTPUT, falsecase1 + ParamSdk.SPLIT_CHAT + falsecase2 + ParamSdk.SPLIT_CHAT + sb.toString());
                }
                extensionResult.setValue(output);
            } catch (Exception e) {
            }
            //-----------------------------------------------------------------------------

        }
        return extensionResult;
    }

    @Override
    public ExtensionResult SpecialistHospitalTerdekat(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        double latitude = extensionRequest.getIntent().getTicket().getLatitude();
        double longitude = extensionRequest.getIntent().getTicket().getLongitude();
//        String lokasi = getEasyMapValueByName(extensionRequest, "lokasi");
//        String[] splitlokasi = lokasi.split(";");
//        String latitude = splitlokasi[0];
//        String longitude = splitlokasi[1];

        String apiHospitalDummy = appProperties.getDummyHospital();
//        String apiHospital = appProperties.getApiHospital();
        JSONArray results = GeneralExecuteAPI(apiHospitalDummy).getJSONArray("data");
        int leng = results.length();
        BigDecimal longitud;
        BigDecimal latitud;
        List<List<String>> data = new ArrayList<>();
        double hasil;
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String hospitalid = jObj.getString("hospital_id");
            String hospitalname = jObj.getString("hospital_name");
            longitud = jObj.getBigDecimal("longitude");
            latitud = jObj.getBigDecimal("latitude");
            String phonenumber = jObj.getString("phoneNumber");

            hasil = distanceInKilometers((Double.valueOf(latitude)), (Double.valueOf(longitude)), latitud.doubleValue(), longitud.doubleValue());
//            hasil = distanceInKilometers((Double.valueOf(latit)), (Double.valueOf(longi)), latitud.doubleValue(), longitud.doubleValue());
            List<String> jarak = new ArrayList<>();
            if (hasil < 30) {
                jarak.add(hasil + "");
                jarak.add(hospitalid);
                jarak.add(hospitalname);
                jarak.add(phonenumber);
                data.add(jarak);
            }
        }
        Collections.sort(data, new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                return Double.valueOf(o1.get(0)).compareTo(Double.valueOf(o2.get(0)));
            }
        });
        for (int j = 0; j < data.size(); j++) {
            String idhospital = data.get(j).get(1);
            String namehospital = data.get(j).get(2);

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setPictureLink(appProperties.getSiloamLogo());
            button.setTitle(namehospital);
            button.setSubTitle(namehospital);
            List<EasyMap> actions = new ArrayList<>();
            EasyMap bookAction = new EasyMap();
            bookAction.setName(namehospital);
            bookAction.setValue(idhospital + "");
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
    public ExtensionResult doGetDoctorBySpecialist(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        Map<String, String> output = new HashMap<>();
        String hospitalId = getEasyMapValueByName(extensionRequest, "hospitalid");
        String specialistId = getEasyMapValueByName(extensionRequest, "spesialisid");
        String[] splitspec = specialistId.split(" ");
        String idspecialis = splitspec[1];
        String apiGetDokter = appProperties.getApiDoctorbyhospitalIdSpecialist() + hospitalId + "&specialistId=" + idspecialis;
//        String apiGetDokter = appProperties.getDummyDoctor();
        JSONArray results = GeneralExecuteAPI(apiGetDokter).getJSONArray("data");
        int leng = results.length();
        if (leng >= 10) {
            leng = 10;
        }
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String doctorId = jObj.getString("doctor_id");
            String doctorName = jObj.getString("doctor_name");
            String doctorSpecialist = jObj.getString("doctor_specialist");
            String doctorHospitals = jObj.getString("doctor_hospitals_unit");

            //Buat Button
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle(doctorName);
            button.setSubTitle(doctorSpecialist + "<br/>" + doctorHospitals);
            List<EasyMap> actions = new ArrayList<>();

            EasyMap LihatJadwal = new EasyMap();
            LihatJadwal.setName(doctorName);
            LihatJadwal.setValue("booking dokter id " + doctorId);
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

    @Override
    public ExtensionResult doValidatePhone(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> clearEntities = new HashMap<>();
        String phone = getEasyMapValueByName(extensionRequest, "notelp");
        phone = phone.replace(" ", "");
        phone = phone.replace("(", "");
        phone = phone.replace(")", "");
        phone = phone.replace("-", "");
        phone = phone.replace(".", "");

        if (phone.matches("^[+0-9]*$")) {
            String preZero8 = phone.substring(0, 2);
            String prePlus62 = phone.substring(0, 4);

            if (phone.length() > 9 && phone.length() < 16) {
                if (prePlus62.equals("+628")) {
                    phone = phone.replace("+628", "08");
                    clearEntities.put("notelp", phone);
//                    clearEntities.put("notelp", addWordPhone(phone));
                    clearEntities.put("confirm", "confirm dong");
                } else if (!preZero8.equals("08")) {
                    clearEntities.put("notelp", "");
                } else {
                    clearEntities.put("notelp", phone);
//                    clearEntities.put("notelp", addWordPhone(phone));
                    clearEntities.put("confirm", "confirm dong");
                }
            } else {
                clearEntities.put("notelp", "");
            }
        } else {
            clearEntities.put("notelp", "");
        }
        extensionResult.setEntities(clearEntities);
        return extensionResult;
    }

    @Override
    public ExtensionResult doValidateDate(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        String sdate = getEasyMapValueByName(extensionRequest, "tanggallahir");
        Map<String, String> clearEntities = new HashMap<>();
        MonthBuilder monthBuilder = new MonthBuilder();
        String result = "";
        String[] arrDate = sdate.split(" ");
        boolean isNumeric;
        boolean isAllowed = true;

        try {
            if (arrDate.length == 1) {
                sdate = sdate.substring(0, 2) + " " + sdate.substring(2, 4) + " " + sdate.substring(4, 8);
                arrDate = sdate.split(" ");
            }

            // mengubah nama bulan ke dalam angka
            for (int i = 0; i < arrDate.length; i++) {
                isNumeric = arrDate[i].chars().allMatch(Character::isDigit);
                if (isNumeric == false) {
                    arrDate[i] = monthBuilder.toMonthNumber(arrDate[i]);
                }
            }

            int year = Integer.parseInt(arrDate[2]);
            int month = Integer.parseInt(arrDate[1]);
            int day = Integer.parseInt(arrDate[0]);

            String sDate1 = day + "/" + month + "/" + year;
            String sDate2 = "";
            if (month < 12) {
                month++;
                sDate2 = "01/" + month + "/" + year;
            } else {
                year++;
                sDate2 = "01/01/" + year;
            }

            //get last date of the month
            Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate2);
            Calendar c = Calendar.getInstance();
            c.setTime(date1);
            c.add(Calendar.DATE, -1);
            Date res = c.getTime();
            int day2 = res.getDate();

            //cek last date dgn tanggal yg diinputkan
            if (day > day2) {
                isAllowed = false;
            } //checking future date or no
            else {
                date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
                c.setTime(date1);
                Date date2 = new Date();
                if (date1.compareTo(date2) > 0) {
                    isAllowed = false;
                }
            }

            if (sdate.contains("-") || sdate.contains(".") || arrDate[2].length() != 4
                    || arrDate[0].length() != 2 || arrDate[1].length() != 2) {
                isAllowed = false;
            }

            if (isAllowed) {
                result = arrDate[2] + "-" + arrDate[1] + "-" + arrDate[0];
                clearEntities.put("tanggallahir", result);

            } else {
                clearEntities.put("tanggallahir", "");
            }
        } catch (Exception e) {
            clearEntities.put("tanggallahir", "");
        }
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        Map<String, String> output = new HashMap<>();
        extensionResult.setEntities(clearEntities);
        output.put(OUTPUT, result);
        return extensionResult;
    }

    @Override
    public ExtensionResult doClearDate(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        Map<String, String> clearEntities = new HashMap<>();

        String confirm = getEasyMapValueByName(extensionRequest, "confirm");
        if (confirm.equalsIgnoreCase("tidak")) {
            clearEntities.put("tanggal", null);
            clearEntities.put("confirm", null);
        } else {
            clearEntities.put("confirm2", "confirm2");
        }
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setEntities(clearEntities);
        return extensionResult;
    }

    // Menggunakan BPJS di Siloam
    @Override
    public ExtensionResult siloamMenggunakanBPJS(ExtensionRequest extensionRequest) {
        String tempat = getEasyMapValueByName(extensionRequest, "tempat");

        Map<String, String> output = new HashMap<>();
        StringBuilder respBuilder = new StringBuilder();

        switch (tempat) {
            case "Siloam Hospital Lippo Village":
            case "Siloam Hospital Kebon Jeruk":
            case "Siloam Hospital Surabaya":
            case "Siloam Hospital Lippo Cikarang":
            case "Siloam Hospital Jambi":
            case "Siloam Hospital Balikpapan":
            case "Siloam Hospital Semanggi":
            case "Siloam Hospital Manado":
            case "Siloam Hospital Makasar":
            case "Siloam Hospital Palembang":
            case "Rumah Sakit Umum Siloam Hospital Lippo Village":
            case "Siloam Hospital Cinere":
            case "Siloam Hospital Denpasar":
            case "Siloam Hospital Tb Simatupang":
            case "Siloam Hospital Kuta":
            case "Siloam Hospital Nusa Dua":
            case "Siloam Hospital Purwakarta":
            case "Siloam Hospital Asri":
            case "Siloam Hospital Kupang":
            case "Siloam Hospital Labuan Bajo":
            case "Siloam Hospital Buton":
            case "Siloam Hospital Samarinda":
            case "Siloam Hospital Bekasi Timur":
            case "Siloam Hospital Sentosa":
            case "Siloam Hospital Cirebon":
            case "Siloam Hospital Bangka Belitung":
            case "Siloam Hospital Bekasi Sepanjang Jaya":
            case "Siloam Hospital Lubuklinggau":
            case "Siloam Hospital Jember":
                respBuilder.append("BPJS kesehatan dapat digunakan di " + tempat);
                break;
            default:
                respBuilder.append("BPJS kesehatan belum dapat digunakan di " + tempat);
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

}
