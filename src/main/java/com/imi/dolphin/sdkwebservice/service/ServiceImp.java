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

import com.google.gson.Gson;
import com.imi.dolphin.sdkwebservice.builder.ButtonBuilder;
import com.imi.dolphin.sdkwebservice.builder.CarouselBuilder;
import com.imi.dolphin.sdkwebservice.builder.FormBuilder;
import com.imi.dolphin.sdkwebservice.builder.ImageBuilder;
import com.imi.dolphin.sdkwebservice.builder.QuickReplyBuilder;
import com.imi.dolphin.sdkwebservice.form.Datum;
import com.imi.dolphin.sdkwebservice.form.Formcuti;
import com.imi.dolphin.sdkwebservice.model.ButtonTemplate;
import com.imi.dolphin.sdkwebservice.model.EasyMap;
import com.imi.dolphin.sdkwebservice.model.ExtensionRequest;
import com.imi.dolphin.sdkwebservice.model.ExtensionResult;
import com.imi.dolphin.sdkwebservice.model.MailModel;
import com.imi.dolphin.sdkwebservice.param.ParamSdk;
import com.imi.dolphin.sdkwebservice.property.AppProperties;
import com.imi.dolphin.sdkwebservice.token.Token;
import com.imi.dolphin.sdkwebservice.util.OkHttpUtil;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    private static final String Image_cuti = "https://image.ibb.co/eAshTV/bot.jpg";
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

    public Datum getForm(String bearer, String ticketNumber) {
        Datum data = new Datum();
        String baseUrl = appProperties.getBaseUrl();
        String apiform = appProperties.getApiForm();
        String formId = appProperties.getFormIdCuti();
        String paramformId = "?formId=";
        String paramFieldName = "&fieldName=";
        String paramFieldValue = "&fieldValue=*";
        String paramStart = "&start=0";
        String paramCount = "&count=1";
        String fieldName = appProperties.getTicketNumber();

        //menggunakan ticket number
        String url = baseUrl + apiform + paramformId + formId + paramFieldName + fieldName + paramFieldValue + ticketNumber + paramStart + paramCount;

        // tidak pakai ticket number
//		String url = baseUrl + apiform + paramformId + formId + paramFieldName + fieldName + paramFieldValue
//				+ paramStart + paramCount;
        System.out.println(url);

        OkHttpUtil okHttpUtil = new OkHttpUtil();
        okHttpUtil.init(true);

        Request request = new Request.Builder().url(url).get().addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + bearer).build();

        try {
            Response response = okHttpUtil.getClient().newCall(request).execute();

            Formcuti fct = new Formcuti();
            String JsonString = response.body().string();
            // System.out.println("getform, Jsonstring : "+JsonString);

            Gson gson = new Gson();
            fct = gson.fromJson(JsonString, Formcuti.class);
            // System.out.println("From cuti data : "+fct);

            String jsonfct = gson.toJson(fct.getData());
            // System.out.println(jsonfct);

            // ambil json array
            JSONArray arrayJson = new JSONArray(jsonfct);
            // System.out.println("Array json dari form cuti data : "+arrayJson);
            JSONObject jsonObjek = arrayJson.getJSONObject(0);
            // System.out.println("Object json dari form cuti data : "+jsonObjek);

            data = gson.fromJson(jsonObjek.toString(), Datum.class);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Method Started Siloam
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult doGetStarted(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();

        //Nearest Hospitals(NH)
        ButtonTemplate button = new ButtonTemplate();
        button.setPictureLink(SAMPLE_IMAGE_PATH);
        button.setPicturePath(SAMPLE_IMAGE_PATH);
        button.setTitle("My Nearest Hospitals");
//        button.setSubTitle("");
        List<EasyMap> actions = new ArrayList<>();
        EasyMap bookActionNH = new EasyMap();
        EasyMap bookActionNH2 = new EasyMap();
        bookActionNH.setName("Find Hospitals");
        bookActionNH.setValue("");
        actions.add(bookActionNH);
        bookActionNH2.setName("Call Us");
        bookActionNH2.setValue("");
        actions.add(bookActionNH2);
        button.setButtonValues(actions);
        ButtonBuilder buttonBuilder = new ButtonBuilder(button);

        //Make an Appointment(MAP)
        ButtonTemplate button2 = new ButtonTemplate();
        button2.setPictureLink(SAMPLE_IMAGE_PATH);
        button2.setPicturePath(SAMPLE_IMAGE_PATH);
        button2.setTitle("Make an appointment");
//        button2.setSubTitle("");
        List<EasyMap> actions2 = new ArrayList<>();
        EasyMap bookActionMAP = new EasyMap();
        EasyMap bookActionMAP2 = new EasyMap();
        EasyMap bookActionMAP3 = new EasyMap();
        bookActionMAP.setName("Book Online");
        bookActionMAP.setValue("");
        actions2.add(bookActionMAP);
        bookActionMAP2.setName("By Phone");
        bookActionMAP2.setValue("");
        actions2.add(bookActionMAP2);
        bookActionMAP3.setName("Doctor Schedule");
        bookActionMAP3.setValue("");
        actions2.add(bookActionMAP3);
        button2.setButtonValues(actions2);
        ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);

        //Medical Check Up(MCU)
        ButtonTemplate button3 = new ButtonTemplate();
        button3.setPictureLink(SAMPLE_IMAGE_PATH);
        button3.setPicturePath(SAMPLE_IMAGE_PATH);
        button3.setTitle("Medical Check Up");
//        button3.setSubTitle(" ");
        List<EasyMap> actions3 = new ArrayList<>();
        EasyMap bookActionMCU = new EasyMap();
        EasyMap bookActionMCU2 = new EasyMap();
        bookActionMCU.setName("Find Package");
        bookActionMCU.setValue("");
        actions3.add(bookActionMCU);
        bookActionMCU2.setName("Preperation Guidelines");
        bookActionMCU2.setValue("");
        actions3.add(bookActionMCU2);
        button3.setButtonValues(actions3);
        ButtonBuilder buttonBuilder3 = new ButtonBuilder(button3);

        //Enquiry(Enq)
        ButtonTemplate button4 = new ButtonTemplate();
        button4.setPictureLink(SAMPLE_IMAGE_PATH);
        button4.setPicturePath(SAMPLE_IMAGE_PATH);
        button4.setTitle("Enquiry");
//        button4.setSubTitle("");
        List<EasyMap> actions4 = new ArrayList<>();
        EasyMap bookActionEnq = new EasyMap();
        bookActionEnq.setName("F.A.Q");
        bookActionEnq.setValue("");
        actions4.add(bookActionEnq);
        button4.setButtonValues(actions4);
        ButtonBuilder buttonBuilder4 = new ButtonBuilder(button4);

        //Feedback(Feed)
        ButtonTemplate button5 = new ButtonTemplate();
        button5.setPictureLink(SAMPLE_IMAGE_PATH);
        button5.setPicturePath(SAMPLE_IMAGE_PATH);
        button5.setTitle("Feedbacks");
//        button5.setSubTitle("");
        List<EasyMap> actions5 = new ArrayList<>();
        EasyMap bookActionFeed = new EasyMap();
        EasyMap bookActionFeed2 = new EasyMap();
        EasyMap bookActionFeed3 = new EasyMap();
        bookActionFeed.setName("Complaint");
        bookActionFeed.setValue("");
        actions5.add(bookActionFeed);
        bookActionFeed2.setName("Compliment");
        bookActionFeed2.setValue("");
        actions5.add(bookActionFeed2);
        bookActionFeed3.setName("Suggestion");
        bookActionFeed3.setValue("");
        actions5.add(bookActionFeed3);
        button5.setButtonValues(actions5);
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
        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Kirim lokasi kakak ya")
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
        button1.setTitle("Booking");
        List<EasyMap> actions1 = new ArrayList<>();
        EasyMap bookActionMCU = new EasyMap();
        bookActionMCU.setName("PILIH");
        bookActionMCU.setValue("booking");
        actions1.add(bookActionMCU);
        button1.setButtonValues(actions1);
        ButtonBuilder buttonBuilder1 = new ButtonBuilder(button1);

        ButtonTemplate button2 = new ButtonTemplate();
//        button2.setPictureLink(SAMPLE_IMAGE_PATH);
//        button2.setPicturePath(SAMPLE_IMAGE_PATH);
        button2.setTitle("Info Dokter");
        List<EasyMap> actions2 = new ArrayList<>();
        EasyMap bookActionEnq = new EasyMap();
        bookActionEnq.setName("PILIH");
        bookActionEnq.setValue("info dokter");
        actions2.add(bookActionEnq);
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

    //Doctor Schedule Flow Search By Area,Hospital,Specialist//
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

                //Create Button 
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(areaName);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName("Pilih");
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
        String areaId = getEasyMapValueByName(extensionRequest, "areaId");
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

                //Create Button 
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(hospitalName);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName("Pilih");
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

                //Create Button 
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
        String hospitalId = getEasyMapValueByName(extensionRequest, "hospitalId");
        String specialistId = getEasyMapValueByName(extensionRequest, "specialistId");
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url("https://52.148.90.132:5363/mobile/mysiloam/patientcommon/doctor/search/complete?hospitalId=" + hospitalId + "&specialistId=" + specialistId).get().build();
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

                AvailableToday.setName(available);
                AvailableToday.setValue(valueavailab);
                actions.add(AvailableToday);

                AvailableTomorrow.setName("Tomorrow");
                AvailableTomorrow.setValue(doctorId);
                actions.add(AvailableTomorrow);

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
    //----------------------------//

    @Override
    public ExtensionResult doGetDoctorSchedule(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String doctorId = getEasyMapValueByName(extensionRequest, "doctorId");
        String hospitalId = getEasyMapValueByName(extensionRequest, "hospitalId");
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getApiDoctorschedule() + doctorId + "/" + hospitalId).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
            int leng = results.length();

            for (int i = 0; i < leng; i++) {
                JSONObject jObj = results.getJSONObject(i);
                int daysnumber = jObj.getInt("doctor_schedule_day");
                String days = "";
                String fromtime = jObj.getString("doctor_schedule_from_time");
                String totime = jObj.getString("doctor_schedule_to_time");
                String dateF = fromtime.substring(11, 16);
                String dateT = totime.substring(11, 16);

                String Name = jObj.getString("doctor_schedule_name");

                if (daysnumber == 1) {
                    days = "Senin";
                } else if (daysnumber == 2) {
                    days = "Selasa";
                } else if (daysnumber == 3) {
                    days = "Rabu";
                } else if (daysnumber == 4) {
                    days = "Kamis";
                } else if (daysnumber == 5) {
                    days = "Jumat";
                } else if (daysnumber == 6) {
                    days = "Sabtu";
                } else if (daysnumber == 7) {
                    days = "Minggu";
                }

                //Buat Button 
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(Name);
                button.setSubTitle(days + ": \n" + dateF + "-" + dateT);
                List<EasyMap> actions = new ArrayList<>();

                EasyMap bookAction = new EasyMap();
                EasyMap callAction = new EasyMap();

                bookAction.setName("Book Online");
                bookAction.setValue("https://www.siloamhospitals.com");
                actions.add(bookAction);

                callAction.setName("By Phone");
                callAction.setValue("tel:1500181");
                actions.add(callAction);

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

    //Doctor Schedule Flow Search By Name//
    @Override
    public ExtensionResult doGetDoctorByName(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String nama = getEasyMapValueByName(extensionRequest, "nama");
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

                //Create Button 
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(doctorName);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName("Pilih");
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
    public ExtensionResult doGetDokterByDoctorId(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        String doctorId = getEasyMapValueByName(extensionRequest, "dokter");
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
                    valueavailab = "Today";
                }
                //Buat Button 
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(doctorName);
                button.setSubTitle(doctorSpecialist + "\n" + doctorHospitals);
                List<EasyMap> actions = new ArrayList<>();

                EasyMap AvailableToday = new EasyMap();
                EasyMap AvailableTomorrow = new EasyMap();

                AvailableToday.setName(available);
                AvailableToday.setValue(valueavailab);
                actions.add(AvailableToday);

                AvailableTomorrow.setName("Tomorrow");
                AvailableTomorrow.setValue(hospitalId);
                actions.add(AvailableTomorrow);

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

    //-------------------------------------//
    
    
    
    @Override
    public ExtensionResult doGetHospitalTerdekat(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        try {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            Request request = new Request.Builder().url(appProperties.getDummyHospital()).get().build();
            Response response = okHttpUtil.getClient().newCall(request).execute();
            JSONObject jsonobj = new JSONObject(response.body().string());
            JSONArray results = jsonobj.getJSONArray("data");
            int leng = results.length();

            for (int i = 0; i < leng; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String hospitalId = jObj.getString("hospital_id");
                String hospitalName = jObj.getString("hospital_name");
                String longitud = jObj.getString("longitude");
                String latitud = jObj.getString("latitude");

                //Create Button 
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(hospitalName);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName("Pilih");
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

    public double greatCircleInKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;

        return 6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1));
    }

}
