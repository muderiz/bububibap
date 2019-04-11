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
        butAction11.setValue("jadwal");
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

    @Override
    public ExtensionResult TipePencarian(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();

        //Button 1
        ButtonTemplate button1 = new ButtonTemplate();
        button1.setPictureLink(SAMPLE_IMAGE_PATH);
        button1.setPicturePath(SAMPLE_IMAGE_PATH);
        button1.setTitle("Pilih Dokter");
        button1.setSubTitle("Berdasarkan :");
        List<EasyMap> actions1 = new ArrayList<>();
        EasyMap butAction11 = new EasyMap();
        EasyMap butAction12 = new EasyMap();
        EasyMap butAction13 = new EasyMap();

        butAction11.setName("Area");
        butAction11.setValue("area");
        actions1.add(butAction11);

        butAction12.setName("Spesialis");
        butAction12.setValue("spesialis");
        actions1.add(butAction12);

        butAction13.setName("Nama Dokter");
        butAction13.setValue("nama");
        actions1.add(butAction13);

        button1.setButtonValues(actions1);
        ButtonBuilder buttonBuilder1 = new ButtonBuilder(button1);

        CarouselBuilder carouselBuilder = new CarouselBuilder(buttonBuilder1.build());

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

    /**
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult doSendLocationSpecialist(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        String konfirmasi = getEasyMapValueByName(extensionRequest, "konfirmasi");
        if (konfirmasi.equalsIgnoreCase("yes")) {
            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Silahkan kirim lokasi Anda sekarang ya. Untuk {bot_name} carikan Rumah Sakit Siloam yang Terdekat dengan Anda.")
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

    /**
     * Flow Rumah Sakit Terdekat
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult doGetHospitalTerdekat(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        double latitude = extensionRequest.getIntent().getTicket().getLatitude();
        double longitude = extensionRequest.getIntent().getTicket().getLongitude();
//        String longitude = getEasyMapValueByName(extensionRequest, "longitude");
//        String latitude = getEasyMapValueByName(extensionRequest, "latitude");

//        if Location setting is not available
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

            if (!longitud.equals("") && !latitud.equals("")) {
                hasil = distanceInKilometers((Double.valueOf(latitude)), (Double.valueOf(longitude)), latitud.doubleValue(), longitud.doubleValue());
                List<String> jarak = new ArrayList<>();
                if (hasil < 30) {
                    jarak.add(hasil + "");
                    jarak.add(hospitalid);
                    jarak.add(hospitalname);
                    jarak.add(phonenumber);
                    data.add(jarak);
                }
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
            dialog = "Berikut nomor telepon Hospital tersebut yang dapat Anda hubungi " + callhospital;
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
    /**
     * Doctor Schedule Flow Search By Area, Hospital, Specialist
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult doGetAreas(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
//        StringBuilder quickBuilder = new StringBuilder();

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
//        sb.append("Silahkan pilih area yang ingin Anda tuju.").append(COMMA);
//        sb.append(quickBuilder);
//        sb.replace(sb.toString().length() - 1, sb.toString().length(), "");
//        sb.append(QUICK_REPLY_SYNTAX_SUFFIX);

        String dialog = "Silahkan pilih area yang ingin Anda tuju.";
        String area = sb.toString();
        output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + area);
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    /**
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult doGetHospitalByArea(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String area = getEasyMapValueByName(extensionRequest, "areaid");

        String areaId = "";
        String konfirmArea = "";

        String apiArea = appProperties.getApiArea();
        JSONArray resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
        int lengArea = resultsArea.length();
        for (int i = 0; i < lengArea; i++) {
            JSONObject jObjArea = resultsArea.getJSONObject(i);
            String idArea = jObjArea.getString("area_id");
            String nameArea = jObjArea.getString("area_name");
            if (area.equalsIgnoreCase(idArea) || area.equalsIgnoreCase(nameArea)) {
                areaId = idArea;
                konfirmArea = "benar";
                break;
            }
        }

        if (konfirmArea.equalsIgnoreCase("benar")) {
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
        } else {
            Map<String, String> clearEntities = new HashMap<>();

            clearEntities.put("areaid", "");
            extensionResult.setEntities(clearEntities);

            for (int i = 0; i < lengArea; i++) {
                JSONObject jObjArea = resultsArea.getJSONObject(i);
                String IdAreaButton = jObjArea.getString("area_id");
                String NameAreaButton = jObjArea.getString("area_name");

                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(NameAreaButton);
                button.setSubTitle(NameAreaButton);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName(NameAreaButton);
                bookAction.setValue(IdAreaButton);

                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }

            String dialog = "Maaf. {bot_name} tidak dapat menemukan Rumah Sakit Siloam berdasarkan Area yang Anda pilih atau ketikan. "
                    + "Silahkan pilih atau ketikan kembali Area yang ingin Anda tuju.";
            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());

        }

        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    /**
     *
     * @param extensionRequest
     * @return
     */
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
                String hospitalName2 = hospitalName.toLowerCase();
                if (hospital.equalsIgnoreCase(hospitalId)) {
                    stat = "hospital";
                    break;
                }
                if (hospital.equalsIgnoreCase(hospitalName2)) {
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

            String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin Anda tuju.";
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

    /**
     *
     * @param extensionRequest
     * @return
     */
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
        String statusEntitas = "";

        String apiHospital = appProperties.getApiHospitalbyId() + spesialisid;
        int codeApi = GeneralExecuteAPI(apiHospital).getInt("code");
        if (codeApi == 200) {
            statusEntitas = "hospital";
        } else {
            String apiArea = appProperties.getApiArea();
            JSONArray resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
            int lengArea = resultsArea.length();
            for (int i = 0; i < lengArea; i++) {
                JSONObject jObj = resultsArea.getJSONObject(i);
                String areaId = jObj.getString("area_id");
                if (spesialisid.equalsIgnoreCase(areaId)) {
                    statusEntitas = "area";
                    break;
                }
            }
        }

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

        } else if (statusEntitas.equalsIgnoreCase("area")) {
            clearEntities.put("hospitalid", "");
            clearEntities.put("spesialisid", "");
            extensionResult.setEntities(clearEntities);

            String apiHospital2 = appProperties.getApiHospitalByArea() + spesialisid;
            Map<String, String> output = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            JSONArray results = GeneralExecuteAPI(apiHospital2).getJSONArray("data");
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

            extensionResult.setValue(output);

        } else if (statusEntitas.equalsIgnoreCase("hospital")) {
            clearEntities.put("spesialisid", "");
            extensionResult.setEntities(clearEntities);
            Map<String, String> output = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            try {
                OkHttpUtil okHttpUtil = new OkHttpUtil();
                okHttpUtil.init(true);
//                Request request = new Request.Builder().url(appProperties.getApiSpecialistbyHospital() + hospital).get().build();
                Request request = new Request.Builder().url(appProperties.getApiSpecialistbyHospital() + spesialisid + "&top10=true").get().build();
                Response response = okHttpUtil.getClient().newCall(request).execute();
                JSONObject jsonobj = new JSONObject(response.body().string());
                JSONArray data = jsonobj.getJSONArray("data");
                //code = code - 1;
                int leng;
                leng = leng(code, data);
                sb = carospec(sb, leng, data);

            } catch (Exception e) {
            }

            String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin Anda tuju.";
            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());
            extensionResult.setValue(output);
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
                    String falsecase1 = "Mohon maaf {bot_name} tidak menemukan specialist yang anda cari";
                    String falsecase2 = "Silahkan pilih Spesialis dibawah ini. Atau ketik kembali nama spesialis yang Anda inginkan dengan benar.";
                    output.put(OUTPUT, falsecase1 + ParamSdk.SPLIT_CHAT + falsecase2 + ParamSdk.SPLIT_CHAT + sb.toString());
                }
            } catch (Exception e) {
            }
            //-----------------------------------------------------------------------------

            extensionResult.setValue(output);
        }
        return extensionResult;
    }

    /**
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult SiloamGetDoctorByHospitalAndSpecialist(ExtensionRequest extensionRequest
    ) {
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        Map<String, String> output = new HashMap<>();
        String spesialisid = getEasyMapValueByName(extensionRequest, "spesialisid");
        String hospital = getEasyMapValueByName(extensionRequest, "hospitalid");
        String[] splitspesialis = spesialisid.split(" ");
        String spesial1 = splitspesialis[2];

        String apiGetDokter = appProperties.getApiDoctorbyhospitalIdSpecialist() + hospital + "&specialistId=" + spesial1;
//        String apiGetDokter = appProperties.getDummyDoctor();
        JSONObject jObjDoctor = GeneralExecuteAPI(apiGetDokter);
        if (jObjDoctor.getInt("code") == 200) {
            JSONArray results = jObjDoctor.getJSONArray("data");
            int leng = results.length();
            if (leng > 10) {
                leng = 10;
            }
            for (int i = 0; i < leng; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String doctorId = jObj.getString("doctor_id");
                String hospitalId = jObj.getString("hospital_id");
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
                LihatJadwal.setValue("booking dokter id " + doctorId + " di hos " + hospitalId);
                actions.add(LihatJadwal);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }
            output.put(OUTPUT, sb.toString());
        } else {
            String dialog = "Maaf {bot_name} tidak menemukan jadwal dokter yang anda cari. Mohon hubungi Siloam Hospital yang di tuju";
            output.put(OUTPUT, dialog);
        }

        extensionResult.setValue(output);
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        return extensionResult;
    }
    //--------------------------------------------------//

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

        String hospitalId = getEasyMapValueByName(extensionRequest, "hospitalid");
        String[] idhos = hospitalId.split(" ");
        String hosid = idhos[1];

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
            newsplit = split1 + " & " + split2;
        } else {
            newsplit = newjam;
        }

        String getLiveHospital = appProperties.getApiHospitalLive();
        JSONArray araylivehos = GeneralExecuteAPI(getLiveHospital).getJSONArray("data");

        String getDoctorByDoctorId = appProperties.getApiDoctorbydoctorid() + dokid;
        JSONArray results = GeneralExecuteAPI(getDoctorByDoctorId).getJSONArray("data");
        JSONObject jObj = results.getJSONObject(0);
        String doctorname = jObj.getString("doctor_name");

        String getNameHospital = appProperties.getApiHospitalbyId() + hosid;
        JSONArray results2 = GeneralExecuteAPI(getNameHospital).getJSONArray("data");
        JSONObject jObj2 = results2.getJSONObject(0);
        String hospitalName = jObj2.getString("hospital_name");

        //Cek Live Hospital
        String hasilcekhospital = CekHospital(hosid, araylivehos);
        if (hasilcekhospital.equalsIgnoreCase("ada")) {

//            String cb = CarouselJamDinamic(newjam);
            String getSchedule = appProperties.getApiDoctorappointment() + hosid + "/doctorId/" + dokid + "/date/" + tanggal;
            JSONArray arraySchedule = GeneralExecuteAPI(getSchedule).getJSONArray("data");
            String carojam = CarouselJamPraktek(arraySchedule);
            String dialog = "Silahkan pilih waktu kunjungan antara " + newsplit + " yang Anda kehendaki.";
            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + carojam);
            extensionResult.setValue(output);
        } else {
            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("kategorijam", "10 00");
            clearEntities.put("jampraktek", "1");
            clearEntities.put("namapasien", "admin");
            clearEntities.put("tanggallahir", "1999-01-01");
            clearEntities.put("notelp", "081318141513");
            clearEntities.put("confirm", "yes");
            extensionResult.setEntities(clearEntities);

        }

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

    /**
     *
     * @param jampraktek Array Jam Praktek Dokter
     * @return
     */
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

    /**
     * Get Jam Praktek, Post to API Booking Siloam
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult doGetJamPraktekDokter(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> output = new HashMap<>();

        String doctorId = getEasyMapValueByName(extensionRequest, "dokterid");
        String[] iddokter = doctorId.split(" ");
        String dokid = iddokter[1];

        String hospitalId = getEasyMapValueByName(extensionRequest, "hospitalid");
        String[] idhos = hospitalId.split(" ");
        String hosid = idhos[1];

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
            newsplit = split1 + " & " + split2;
        } else {
            newsplit = newjam;
        }

        String jampraktek = getEasyMapValueByName(extensionRequest, "kategorijam");
        String newjampraktek = jampraktek.replace(" ", ":");

        String getSchedule = appProperties.getApiDoctorappointment() + hosid + "/doctorId/" + dokid + "/date/" + tanggal;
        JSONArray arraySchedule = GeneralExecuteAPI(getSchedule).getJSONArray("data");
        int leng = arraySchedule.length();
        String statjampraktek = "";

        for (int i = 0; i < leng; i++) {
            JSONObject jsono = arraySchedule.getJSONObject(i);
            String fromtime = jsono.getString("from_time");
            String totime = jsono.getString("to_time");
            if (newjampraktek.equalsIgnoreCase(fromtime) || newjampraktek.equalsIgnoreCase(totime)) {
                statjampraktek = "ada";
                break;
            } else {
                statjampraktek = "tidak";
            }
        }
        //Cek Live Hospital
        if (statjampraktek.equalsIgnoreCase("tidak")) {
            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("kategorijam", "");
            extensionResult.setEntities(clearEntities);
//            String cb = CarouselJamDinamic(newjam);
            String carojam = CarouselJamPraktek(arraySchedule);
            String dialog = "Maaf {bot_name} tidak dapat menemukan Jam Praktek tersebut.";
            String dialog2 = "Silahkan pilih waktu kunjungan antara " + newsplit + " yang Anda kehendaki.";
            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + dialog2 + ParamSdk.SPLIT_CHAT + carojam);
            extensionResult.setValue(output);
        } else {
            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("jampraktek", "1");
            extensionResult.setEntities(clearEntities);
        }

        return extensionResult;
    }

    /**
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult doPostCreateAppointment(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        CreatePatient createPatient = new CreatePatient();
        CreateAppointment createAppointment = new CreateAppointment();

        String dokterid = getEasyMapValueByName(extensionRequest, "dokterid");
        String hospitalId = getEasyMapValueByName(extensionRequest, "hospitalid");
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

        String[] idhos = hospitalId.split(" ");
        String hospital = idhos[1];

        String[] tanggal = tanggalPesan.split("=");
//        String jamtanggal = tanggal[0];
//        String jampesan = jamtanggal.replace(" ", ":");
        String date = tanggal[1];

        String getLiveHospital = appProperties.getApiHospitalLive();
        JSONArray araylivehos = GeneralExecuteAPI(getLiveHospital).getJSONArray("data");

        String getDoctorByDoctorId = appProperties.getApiDoctorbydoctorid() + dokid;
        JSONArray arraydocname = GeneralExecuteAPI(getDoctorByDoctorId).getJSONArray("data");
        JSONObject jObjdocname = arraydocname.getJSONObject(0);
        String docname = jObjdocname.getString("doctor_name");

        String getNameHospital = appProperties.getApiHospitalbyId() + hospital;
        JSONArray arrayhospital = GeneralExecuteAPI(getNameHospital).getJSONArray("data");
        JSONObject jObjhospital = arrayhospital.getJSONObject(0);
        String hosname = jObjhospital.getString("hospital_name");

        //Cek Live Hospital
        String hasilcekhospital = CekHospital(hospital, araylivehos);
        if (hasilcekhospital.equalsIgnoreCase("tidak")) {

            String dialog1 = "Untuk melakukan reservasi Dokter " + docname + " di " + hosname + ".";
            String dialog2 = "Silahkan hubungi Call Center berikut: +1500181";

            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + dialog2);
        } else {
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
    /**
     * get Dokter By Nama
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult tanyaNama(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        sb.append("Silahkan ketik nama Dokter yang ingin Anda kunjungi. Untuk {bot_name} bantu carikan.");
        output.put(OUTPUT, sb.toString());
        extensionResult.setValue(output);

        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        return extensionResult;
    }

    /**
     *
     * @param extensionRequest
     * @return
     */
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
            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik tombol di bawah ini jika Anda ingin kembali ke Menu Utama. "
                    + "Atau ketikan kembali Nama Dokter yang ingin Anda kunjungi dengan benar.")
                    .add("Menu Utama", "menu utama").build();

            String dialog1 = "Mohon maaf, {bot_name} tidak dapat menemukan nama Dokter yang Anda ketikan.";
//            String dialog2 = "Silahkan ketik kembali nama Dokter yang ingin Anda kunjungi dengan benar.";

            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + quickReplyBuilder.string());
            extensionResult.setValue(output);

        }
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        return extensionResult;
    }

    /**
     *
     * @param extensionRequest
     * @return
     */
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
                String hospitalId = jObj2.getString("hospital_id");
                String doctorSpecialist = jObj2.getString("doctor_specialist");
                String doctorHospitals = jObj2.getString("doctor_hospitals_unit");

                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(doctorName);
                button.setSubTitle(doctorSpecialist + "<br/>" + doctorHospitals);
                List<EasyMap> actions = new ArrayList<>();

                EasyMap LihatJadwal = new EasyMap();
                LihatJadwal.setName(doctorName);
                LihatJadwal.setValue("booking dokter id " + doctorId + " di hos " + hospitalId);
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

    /**
     * get Jadwal by DokterId
     *
     * @param extensionRequest
     * @return
     */
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
        String hospitalId = getEasyMapValueByName(extensionRequest, "hospitalid");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +1);
        String[] iddokter = doctorId.split(" ");
        String dokid = iddokter[1];
        String[] idhos = hospitalId.split(" ");
        String hosid = idhos[1];

        String getDoctorByDoctorId = appProperties.getApiDoctorbydoctorid() + dokid;
        JSONArray results3 = GeneralExecuteAPI(getDoctorByDoctorId).getJSONArray("data");
        JSONObject jObj3 = results3.getJSONObject(0);
        String doctorName = jObj3.getString("doctor_name");

        String schedule = appProperties.getApiDoctorschedule() + dokid + "/" + hosid;
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
                String scheduleTime = appProperties.getApiDoctorappointment() + hosid + "/doctorId/" + dokid + "/date/" + date;
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
                String tanggalttitle = hari + ", " + tanggal + " " + bulan + " " + tahun;
                if (dayslist.contains(kodeHari)) {
                    //Buat Button
                    ButtonTemplate button = new ButtonTemplate();
                    button.setTitle(tanggalttitle);
                    button.setSubTitle(daypoint[x] + " | " + available);
                    List<EasyMap> actions = new ArrayList<>();

                    EasyMap bookAction = new EasyMap();
                    bookAction.setName(tanggalttitle);
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
            String StringOutput = "Maaf. {bot_name} tidak dapat menemukan Jadwal Dokter pilihan Anda.";
            stringbuild = StringOutput;
            output.put(OUTPUT, stringbuild);

        } else {
            String dialog1 = "Berikut adalah detail jadwal praktik " + doctorName + ". (Atau ketik Menu untuk kembali ke Menu Utama).";
            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + stringbuild);

        }
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
    /**
     * Get Dokter by Spesialis
     *
     * @param extensionRequest
     * @return
     */
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

        String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin Anda tuju.";
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

    /**
     *
     * @param extensionRequest
     * @return
     */
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

    /**
     *
     * @param extensionRequest
     * @return
     */
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
            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Silahkan kirim Lokasi Anda sekarang ya.")
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
                    String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin Anda kunjungi.";
                    output.put(OUTPUT, truecase + ParamSdk.SPLIT_CHAT + sb.toString());
                } else {
                    request = new Request.Builder().url(appProperties.getApiSpecialist() + "?top10=true").get().build();
                    response = okHttpUtil.getClient().newCall(request).execute();
                    jsonobj = new JSONObject(response.body().string());
                    JSONArray data = jsonobj.getJSONArray("data");
                    int leng;
                    leng = leng(code, data);
                    sb = carospec(sb, leng, data);
                    String falsecase1 = "Maaf {bot_name} tidak dapat menemukan spesialis yang Anda cari.";
                    String falsecase2 = "Silahkan pilih Spesialis dibawah ini. Atau ketik kembali nama spesialis yang Anda inginkan dengan benar.";
                    output.put(OUTPUT, falsecase1 + ParamSdk.SPLIT_CHAT + falsecase2 + ParamSdk.SPLIT_CHAT + sb.toString());
                }
                extensionResult.setValue(output);
            } catch (Exception e) {
            }
            //-----------------------------------------------------------------------------

        }
        return extensionResult;
    }

    /**
     *
     * @param extensionRequest
     * @return
     */
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

    /**
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult doGetDoctorBySpecialist(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        Map<String, String> output = new HashMap<>();
        String hospital = getEasyMapValueByName(extensionRequest, "hospitalid");
        String specialistId = getEasyMapValueByName(extensionRequest, "spesialisid");
        String[] splitspec = specialistId.split(" ");
        String idspecialis = splitspec[1];
        String apiGetDokter = appProperties.getApiDoctorbyhospitalIdSpecialist() + hospital + "&specialistId=" + idspecialis;
//        String apiGetDokter = appProperties.getDummyDoctor();
        JSONArray results = GeneralExecuteAPI(apiGetDokter).getJSONArray("data");
        int leng = results.length();
        if (leng >= 10) {
            leng = 10;
        }
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String doctorId = jObj.getString("doctor_id");
            String hospitalId = jObj.getString("hospital_id");
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
            LihatJadwal.setValue("booking dokter id " + doctorId + " di hos " + hospitalId);
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

    /**
     *
     * @param extensionRequest
     * @return
     */
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

    /**
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult doValidateDate(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        String sdate = getEasyMapValueByName(extensionRequest, "tanggallahir");
        Map<String, String> clearEntities = new HashMap<>();
        MonthBuilder monthBuilder = new MonthBuilder();
        String result = "";
        sdate = sdate.replace("-", " ");
        String[] arrDate = sdate.split(" ");
        boolean isNumeric;
        boolean isAllowed = true;

        try {
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
            if (String.valueOf(year).length() < 3) {
                SimpleDateFormat parser = new SimpleDateFormat("yy");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
                String strYear = formatter.format(parser.parse(year + ""));
                arrDate[2] = strYear;
                year = Integer.parseInt(strYear);
            }
            //Cek Tanggal Terakhir di Bulan tersebut
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
            //sdate.contains("-") || sdate.contains(".") || 
            if (arrDate[2].length() != 4 || arrDate[0].length() != 2 || arrDate[1].length() != 2) {
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

    /**
     *
     * @param extensionRequest
     * @return
     */
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

    /**
     * Menggunakan BPJS di Siloam
     *
     * @param extensionRequest
     * @return
     */
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

    //---------------------------//
    ///// New Booking Dokter /////
    /**
     *
     * @param extensionRequest
     * @return
     */
    public ExtensionResult SetKonfirmasiTipe(ExtensionRequest extensionRequest) {

        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> output = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String tipe = getEasyMapValueByName(extensionRequest, "tipe_pencarian");
        String konfirmtipe = tipe;
        Map<String, String> clearEntities = new HashMap<>();

        // Area
        if (tipe.equalsIgnoreCase("area")) {
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
            String dialog = "Silahkan pilih area yang ingin Anda tuju.";
            String area = sb.toString();
            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + area);
            clearEntities.put("konfirmtipe", konfirmtipe);

            // Spesialis
        } else if (tipe.equalsIgnoreCase("spesialis")) {
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

            String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin Anda tuju.";
            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());
            clearEntities.put("konfirmtipe", konfirmtipe);

            // Nama
        } else if (tipe.equalsIgnoreCase("nama")) {
            sb.append("Silahkan ketik nama Dokter yang ingin Anda kunjungi. Untuk {bot_name} bantu carikan.");
            output.put(OUTPUT, sb.toString());
            clearEntities.put("konfirmtipe", konfirmtipe);

            // Cek Free Typing
        } else {
            int kode = 0;
            String apiHospitalName = appProperties.getApiHospitalName() + tipe;
            JSONObject jobj1 = GeneralExecuteAPI(apiHospitalName);
            if (jobj1.getInt("code") == 200) {
                konfirmtipe = "area";
                kode = 1;
            }
            String apiSpesilisName = appProperties.getApiSpecialistbyname() + tipe;
            JSONObject jobj2 = GeneralExecuteAPI(apiSpesilisName);
            if (jobj2.getInt("code") == 200) {
                konfirmtipe = "spesialis";
                kode = 2;
            }
            String apiDokterName = appProperties.getApiDoctorbyname() + tipe;
            JSONObject jobj3 = GeneralExecuteAPI(apiDokterName);
            if (jobj3.getInt("code") == 200) {
                konfirmtipe = "nama";
                kode = 3;
            }
            switch (kode) {
                case 1:
                    JSONArray resultsArea = jobj1.getJSONArray("data");
                    int leng = resultsArea.length();
                    for (int i = 0; i < leng; i++) {
                        JSONObject jObj = resultsArea.getJSONObject(i);
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
                    clearEntities.put("konfirmtipe", konfirmtipe);

                    break;
                case 2:
                    JSONArray resultsSpesialis = jobj2.getJSONArray("data");
                    int leng2 = resultsSpesialis.length();
                    sb = carospec(sb, leng2, resultsSpesialis);
                    String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin Anda kunjungi.";
                    output.put(OUTPUT, truecase + ParamSdk.SPLIT_CHAT + sb.toString());
                    clearEntities.put("konfirmtipe", konfirmtipe);

                    break;
                case 3:
                    JSONArray resultsNama = jobj3.getJSONArray("data");
                    int leng3 = jobj3.length();
                    for (int i = 0; i < leng3; i++) {
                        JSONObject jObj = resultsNama.getJSONObject(i);
                        String doctorId = jObj.getString("doctor_id");

                        String apiGetDokter = appProperties.getApiDoctorbydoctorid() + doctorId;
                        JSONArray resultsDoctor = GeneralExecuteAPI(apiGetDokter).getJSONArray("data");
                        int lengDoctor = resultsDoctor.length();
                        for (int j = 0; j < lengDoctor; j++) {
                            JSONObject jObj2 = resultsDoctor.getJSONObject(j);
                            String doctorName = jObj2.getString("doctor_name");
                            String hospitalId = jObj2.getString("hospital_id");
                            String doctorSpecialist = jObj2.getString("doctor_specialist");
                            String doctorHospitals = jObj2.getString("doctor_hospitals_unit");

                            //Buat Button
                            ButtonTemplate button = new ButtonTemplate();
                            button.setTitle(doctorName);
                            button.setSubTitle(doctorSpecialist + "<br/>" + doctorHospitals);
                            List<EasyMap> actions = new ArrayList<>();

                            EasyMap LihatJadwal = new EasyMap();
                            LihatJadwal.setName(doctorName);
                            LihatJadwal.setValue("booking dokter id " + doctorId + " di hos " + hospitalId);
                            actions.add(LihatJadwal);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                            String btnBuilder = buttonBuilder.build().toString();
                            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                        }
                    }
                    output.put(OUTPUT, sb.toString());
                    clearEntities.put("konfirmtipe", konfirmtipe);
                    clearEntities.put("step_satu", "1");
                    clearEntities.put("konfirmasispesialis", "yes");
                    clearEntities.put("step_dua", "2");
                    clearEntities.put("step_tiga", "3");

                    break;
                default:
                    clearEntities.put("tipe_pencarian", "");
                    clearEntities.put("konfirmtipe", "");

                    break;
            }
        }
        clearEntities.put("counter", "0");
        extensionResult.setEntities(clearEntities);
        extensionResult.setValue(output);
        return extensionResult;
    }

    public ExtensionResult setStepDua(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> output = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String konfirmtipe = getEasyMapValueByName(extensionRequest, "konfirmtipe");
        String counter = getEasyMapValueByName(extensionRequest, "counter");
        String stepsatu = getEasyMapValueByName(extensionRequest, "step_satu");
        Map<String, String> clearEntities = new HashMap<>();

        // Get Hospital by Area
        if (konfirmtipe.equalsIgnoreCase("area")) {
            String areaId = "";
            String konfirmArea = "";

            String apiArea = appProperties.getApiArea();
            JSONArray resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
            int lengArea = resultsArea.length();
            for (int i = 0; i < lengArea; i++) {
                JSONObject jObjArea = resultsArea.getJSONObject(i);
                String idArea = jObjArea.getString("area_id");
                String nameArea = jObjArea.getString("area_name");
                if (stepsatu.equalsIgnoreCase(idArea) || stepsatu.equalsIgnoreCase(nameArea)) {
                    areaId = idArea;
                    konfirmArea = "benar";
                    break;
                }
            }
            if (konfirmArea.equalsIgnoreCase("benar")) {
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
            } else {
                clearEntities.put("step_satu", "");
                for (int i = 0; i < lengArea; i++) {
                    JSONObject jObjArea = resultsArea.getJSONObject(i);
                    String IdAreaButton = jObjArea.getString("area_id");
                    String NameAreaButton = jObjArea.getString("area_name");

                    //Buat Button
                    ButtonTemplate button = new ButtonTemplate();
                    button.setTitle(NameAreaButton);
                    button.setSubTitle(NameAreaButton);
                    List<EasyMap> actions = new ArrayList<>();
                    EasyMap bookAction = new EasyMap();
                    bookAction.setName(NameAreaButton);
                    bookAction.setValue(IdAreaButton);

                    actions.add(bookAction);
                    button.setButtonValues(actions);
                    ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                    String btnBuilder = buttonBuilder.build().toString();
                    sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                }

                String dialog = "Maaf. {bot_name} tidak dapat menemukan Rumah Sakit Siloam berdasarkan Area yang Anda pilih atau ketikan. "
                        + "Silahkan pilih atau ketikan kembali Area yang ingin Anda tuju.";
                output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());
            }

            // Get Location by Spesialis //
        } else if (konfirmtipe.equalsIgnoreCase("spesialis")) {
            String spesialis1 = "";
            String apiSpesilisId = appProperties.getApiSpecialist();
            JSONObject jobj = GeneralExecuteAPI(apiSpesilisId);
            JSONArray dataSpec = jobj.getJSONArray("data");
            int lengspec = dataSpec.length();
            for (int i = 0; i < lengspec; i++) {
                JSONObject jObjSpec = dataSpec.getJSONObject(i);
                String idSpec = jObjSpec.getString("specialization_id");
                String nameArea = jObjSpec.getString("name_id");
                if (stepsatu.equalsIgnoreCase(idSpec)) {
                    spesialis1 = "id";
                    break;
                }
            }

            int code = Integer.parseInt(counter);
            if (stepsatu.equalsIgnoreCase("lainnya")) {
                code++;
                clearEntities.put("counter", code + "");
                clearEntities.put("stepsatu", "");
                //------------------------------------------------------------------------//
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
            } else if (spesialis1.equalsIgnoreCase("id")) {
                clearEntities.put("konfirmasispesialis", "yes");

                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Silahkan kirim Lokasi Anda sekarang ya.")
                        .add("Kirim Lokasi", "location").build();
                output.put(OUTPUT, quickReplyBuilder.string());

            } else {
                clearEntities.put("stepsatu", "");
                try {
                    OkHttpUtil okHttpUtil = new OkHttpUtil();
                    okHttpUtil.init(true);
                    Request request = new Request.Builder().url(appProperties.getApiSpecialistbyname() + stepsatu).get().build();
                    Response response = okHttpUtil.getClient().newCall(request).execute();
                    JSONObject jsonobj = new JSONObject(response.body().string());
                    if (jsonobj.getInt("code") == 200) {
                        JSONArray data = jsonobj.getJSONArray("data");
                        int leng;
                        leng = data.length();
                        sb = carospec(sb, leng, data);
                        String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin Anda kunjungi.";
                        output.put(OUTPUT, truecase + ParamSdk.SPLIT_CHAT + sb.toString());
                    } else {
                        request = new Request.Builder().url(appProperties.getApiSpecialist() + "?top10=true").get().build();
                        response = okHttpUtil.getClient().newCall(request).execute();
                        jsonobj = new JSONObject(response.body().string());
                        JSONArray data = jsonobj.getJSONArray("data");
                        int leng;
                        leng = leng(code, data);
                        sb = carospec(sb, leng, data);
                        String falsecase1 = "Maaf {bot_name} tidak dapat menemukan spesialis yang Anda cari.";
                        String falsecase2 = "Silahkan pilih Spesialis dibawah ini. Atau ketik kembali nama spesialis yang Anda inginkan dengan benar.";
                        output.put(OUTPUT, falsecase1 + ParamSdk.SPLIT_CHAT + falsecase2 + ParamSdk.SPLIT_CHAT + sb.toString());
                    }
                } catch (Exception e) {
                }
            }
        }
        clearEntities.put("counter", "0");
        extensionResult.setEntities(clearEntities);
        extensionResult.setValue(output);
        return extensionResult;
    }

    public ExtensionResult setStepTiga(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> output = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String konfirmtipe = getEasyMapValueByName(extensionRequest, "konfirmtipe");
        String counter = getEasyMapValueByName(extensionRequest, "counter");
        String stepsatu = getEasyMapValueByName(extensionRequest, "step_satu");
        String stepdua = getEasyMapValueByName(extensionRequest, "step_dua");
        Map<String, String> clearEntities = new HashMap<>();

        // Get Hospital by Area
        if (konfirmtipe.equalsIgnoreCase("area")) {
           
            
            // Get Location by Spesialis //
        } else if (konfirmtipe.equalsIgnoreCase("spesialis")) {
            String spesialis1 = "";
            String apiSpesilisId = appProperties.getApiSpecialist();
            JSONObject jobj = GeneralExecuteAPI(apiSpesilisId);
            JSONArray dataSpec = jobj.getJSONArray("data");
            int lengspec = dataSpec.length();
            for (int i = 0; i < lengspec; i++) {
                JSONObject jObjSpec = dataSpec.getJSONObject(i);
                String idSpec = jObjSpec.getString("specialization_id");
                String nameArea = jObjSpec.getString("name_id");
                if (stepsatu.equalsIgnoreCase(idSpec)) {
                    spesialis1 = "id";
                    break;
                }
            }

            int code = Integer.parseInt(counter);
            if (stepsatu.equalsIgnoreCase("lainnya")) {
                code++;
                clearEntities.put("counter", code + "");
                clearEntities.put("stepsatu", "");
                //------------------------------------------------------------------------//
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
            } else if (spesialis1.equalsIgnoreCase("id")) {
                clearEntities.put("konfirmasispesialis", "yes");

                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Silahkan kirim Lokasi Anda sekarang ya.")
                        .add("Kirim Lokasi", "location").build();
                output.put(OUTPUT, quickReplyBuilder.string());

            } else {
                clearEntities.put("stepsatu", "");
                try {
                    OkHttpUtil okHttpUtil = new OkHttpUtil();
                    okHttpUtil.init(true);
                    Request request = new Request.Builder().url(appProperties.getApiSpecialistbyname() + stepsatu).get().build();
                    Response response = okHttpUtil.getClient().newCall(request).execute();
                    JSONObject jsonobj = new JSONObject(response.body().string());
                    if (jsonobj.getInt("code") == 200) {
                        JSONArray data = jsonobj.getJSONArray("data");
                        int leng;
                        leng = data.length();
                        sb = carospec(sb, leng, data);
                        String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin Anda kunjungi.";
                        output.put(OUTPUT, truecase + ParamSdk.SPLIT_CHAT + sb.toString());
                    } else {
                        request = new Request.Builder().url(appProperties.getApiSpecialist() + "?top10=true").get().build();
                        response = okHttpUtil.getClient().newCall(request).execute();
                        jsonobj = new JSONObject(response.body().string());
                        JSONArray data = jsonobj.getJSONArray("data");
                        int leng;
                        leng = leng(code, data);
                        sb = carospec(sb, leng, data);
                        String falsecase1 = "Maaf {bot_name} tidak dapat menemukan spesialis yang Anda cari.";
                        String falsecase2 = "Silahkan pilih Spesialis dibawah ini. Atau ketik kembali nama spesialis yang Anda inginkan dengan benar.";
                        output.put(OUTPUT, falsecase1 + ParamSdk.SPLIT_CHAT + falsecase2 + ParamSdk.SPLIT_CHAT + sb.toString());
                    }
                } catch (Exception e) {
                }
            }
        }
        clearEntities.put("counter", "0");
        extensionResult.setEntities(clearEntities);
        extensionResult.setValue(output);
        return extensionResult;
    }
}
