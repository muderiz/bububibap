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
import com.imi.dolphin.sdkwebservice.model.CreateAppointment;
import com.imi.dolphin.sdkwebservice.model.CreatePatient;
import com.imi.dolphin.sdkwebservice.model.EasyMap;
import com.imi.dolphin.sdkwebservice.model.ExtensionRequest;
import com.imi.dolphin.sdkwebservice.model.ExtensionResult;
import com.imi.dolphin.sdkwebservice.model.MonthBuilder;
import com.imi.dolphin.sdkwebservice.param.ParamSdk;
import com.imi.dolphin.sdkwebservice.property.AppProperties;
import com.imi.dolphin.sdkwebservice.util.OkHttpUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    /**
     * Tipe Pencarian
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult TipePencarian(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();

        //Button 1
        ButtonTemplate button1 = new ButtonTemplate();
        button1.setTitle("Cek Jadwal Dokter");
        button1.setSubTitle("Berdasarkan :");
        List<EasyMap> actions1 = new ArrayList<>();
        EasyMap butAction11 = new EasyMap();
        EasyMap butAction12 = new EasyMap();
        EasyMap butAction13 = new EasyMap();

        butAction11.setName("Area");
        butAction11.setValue("area");
        actions1.add(butAction11);

        butAction12.setName("Nama");
        butAction12.setValue("nama");
        actions1.add(butAction12);

        butAction13.setName("Spesialis");
        butAction13.setValue("spesialis");
        actions1.add(butAction13);

        button1.setButtonValues(actions1);
        ButtonBuilder buttonBuilder1 = new ButtonBuilder(button1);

        CarouselBuilder carouselBuilder = new CarouselBuilder(buttonBuilder1.build());
        String dialog1 = " Silakan pilih tipe pencarian dokter yang diinginkan.";
        output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + carouselBuilder.build());
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
     * Method General Execute API
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
     * Nearest Hospital Method untuk mencari Rumah Sakit Siloam Terdekat
     * Berdasarkan Latitude Longitude yang sudah kirimkan oleh User
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

        String apiHospital = appProperties.getApiHospital();
        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
        int leng = results.length();
        List<List<String>> data = new ArrayList<>();
        double hasil;
        String imageUrl = "";
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String hospitalid = jObj.getString("hospital_id");
            String hospitalname = jObj.getString("hospital_name");
            Double longitud = jObj.getDouble("longitude");
            Double latitud = jObj.getDouble("latitude");
            String phonenumber = jObj.optString("phoneNumber");
            if (phonenumber.equals("")) {
                phonenumber = "+62211500181";
            }
            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                imageUrl = jObj.getString("image_url");
            } else {
                imageUrl = appProperties.getSiloamLogo();
            }
            if (latitud != 0 || longitud != 0) {
                hasil = distanceInKilometers((Double.valueOf(latitude)), (Double.valueOf(longitude)), latitud, longitud);
                List<String> jarak = new ArrayList<>();
                if (hasil < 40) {
                    jarak.add(hasil + "");
                    jarak.add(hospitalid);
                    jarak.add(hospitalname);
                    jarak.add(phonenumber);
                    jarak.add(imageUrl);
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
            String image = data.get(j).get(4);
            if (phonenum.equalsIgnoreCase("")) {
                phonenum = "000";
            }
            ButtonTemplate button = new ButtonTemplate();
            button.setPictureLink(image);
            button.setPicturePath(image);
            button.setTitle(namehospital);
            button.setSubTitle(namehospital);
            List<EasyMap> actions = new ArrayList<>();

            EasyMap direction = new EasyMap();
            EasyMap callAction = new EasyMap();
            EasyMap booknow = new EasyMap();

            booknow.setName("Book Now");
            booknow.setValue("jadwal dokter via booknow di area konter 0 di dummyarea di " + idhospital);
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
        if (sb.toString().isEmpty()) {
            String apiArea = appProperties.getApiArea();
            JSONArray resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
            int lengArea = resultsArea.length();
            for (int i = 0; i < lengArea; i++) {
                JSONObject jObj = resultsArea.getJSONObject(i);
                String areaId = jObj.getString("area_id");
                String areaName = jObj.getString("area_name");

                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(areaName);
                button.setSubTitle("");
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName(areaName);
                bookAction.setValue("jadwal dokter via nearbook di area konter 0 di " + areaId);
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }
            String dialog1 = "Mohon maaf Siloam Hospitals belum tersedia disekitar lokasi Anda. Silahkan pilih dari opsi berikut:";
            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());
        } else {
            String dialog1 = "Berikut adalah daftar Siloam Hospitals yang terdekat dengan Anda.";
            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());
        }

        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    /**
     * Distance in Kilometers Method ini di Pakai oleh Method Nearest Hospital
     * (doGetHospitalTerdekat) Untuk menghitung Jarak Kilometers berdasarkan
     * Longitude Latitude User Dengan Longitude Latitude masing masing Rumah
     * Sakit
     *
     * @param lat1
     * @param long1
     * @param lat2
     * @param long2
     * @return
     */
    public double distanceInKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;
        return 6371.01 * Math.acos(Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1));
    }

    /**
     * Call Hospital Method untuk Memberitahukan User nomor Telepon masing
     * masing Rumah Sakit
     *
     * @param extensionRequest
     * @return
     */
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

    /**
     * Days/Hari Method untuk Mengkonversikan Hari
     *
     * @param day
     * @return
     */
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

    /**
     * Months/Bulan Method untuk Mengkonversikan Bulan
     *
     * @param monthName
     * @return
     */
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

    /**
     * Carousel Jam Prakte Method untuk membuat Carousel Jam Praktek yang
     * Dinamis
     *
     * @param jampraktek Array Jam Praktek Dokter (Array 'jampraktek' Yang di
     * dapat dari Data Jam Praktek Dokter berdasarkan Tanggal Praktek)
     * @return
     */
    private String CarouselJamPraktek(JSONArray jampraktek) {
        int jampraktekleng = jampraktek.length();
        int jumlahbagi = (jampraktekleng / 3);
        int jumlahsisa = (jampraktekleng % 3);
        if (jumlahsisa > 0) {
            jumlahbagi++;
        }

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
     * Cek Cuti/ Check Leave Doctor Method untuk Mengecek apakah Status Dokter
     * sedang Cuti/Leave atau tidak
     *
     * @param id
     * @param tanggal
     * @return
     */
    private String CekCuti(String id, String tanggal) {
        String cuti = "";
        String getLeaveDoctor = appProperties.getApiLeaveDoctor() + id;
        JSONObject jobj = GeneralExecuteAPI(getLeaveDoctor);
        if (jobj.getString("message").equalsIgnoreCase("No Doctor Leave Found!")) {
            cuti = "tidak";
        } else {
            JSONArray leavearray = jobj.getJSONArray("data");
            int leaveleng = leavearray.length();
            for (int i = 0; i < leaveleng; i++) {
                JSONObject jobjcuti = leavearray.getJSONObject(i);
                String fromdate = jobjcuti.getString("from_date");
                String[] splitfromdate = fromdate.split("T");
                String awalcuti = splitfromdate[0];
                String todate = jobjcuti.getString("to_date");
                String[] splittodate = todate.split("T");
                String akhircuti = splittodate[0];
                if (tanggal.equalsIgnoreCase(awalcuti) || tanggal.equalsIgnoreCase(awalcuti)) {
                    cuti = "iya";
                    break;
                } else {
                    cuti = "tidak";
                }
            }
        }
        return cuti;
    }

    /**
     * Cek Hospital Method untuk Mengecek apakah Hospital/Rumah Sakit yang di
     * Pilih oleh User Termasuk ke Dalam list Hospital yang dapat di Lakukan
     * Online Booking
     *
     * @param id
     * @param livehos
     * @return
     */
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

    /**
     * Leng (Leng Speciality) Method ini dipakai untuk Menentukan Leng dari
     * total Data Specialist Yang ingin ditampilkan Digunakan ketika Hanya ingin
     * menampilkan 9 Carousel + 1 Carousel(Lainnya) Berdasarkan Index
     *
     * @param code
     * @param data
     * @return
     */
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

    /**
     * Carospec(Carousel Specialist) Method ini dipakai untuk membuat Carousel
     * Specialist General Agar bisa di pakai berbagai Method Lainnya
     *
     * @param sb
     * @param leng
     * @param data
     * @return
     */
    private StringBuilder carospec(StringBuilder sb, int leng, JSONArray data) {
        if (leng == 10) {
            for (int i = 0; i < leng; i++) {
                JSONObject jObj = data.getJSONObject(i);
                String id_spesialis = jObj.getString("specialization_id");
                String nameEn = jObj.getString("name_en");
                String name = jObj.getString("name_id");
                String imageUrl = "";
                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                    imageUrl = jObj.getString("image_url");
                } else {
                    imageUrl = appProperties.getSiloamLogo();
                }
                //Buat Button 

                String value = id_spesialis;
                ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, name, nameEn, name, value);
                String btnBuilder = buatBtnBuilder.build().toString();
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
                String imageUrl = "";
                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                    imageUrl = jObj.getString("image_url");
                } else {
                    imageUrl = appProperties.getSiloamLogo();
                }
                //Buat Button 
                String value = id_spesialis;
                ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, name, nameEn, name, value);
                String btnBuilder = buatBtnBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }
            int minus2 = 9;
            if (leng == minus2 || leng > minus2) {
                String imageUrl = appProperties.getSiloamLogo();
                String value = "lainnya";
                ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, "lainnya", "", "Pilih", value);
                String btnBuilder = buatBtnBuilder.build().toString();
                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
            }
        }
        return sb;
    }

    /**
     * Validate Phone Method untuk Memvalidasikan Nomor Telephone/Handphone yang
     * diinput Oleh User
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
            if (phone.length() > 9 && phone.length() < 16) {
                String preZero8 = phone.substring(0, 2);
                String prePlus62 = phone.substring(0, 4);
                if (prePlus62.equals("+628")) {
                    phone = phone.replace("+628", "08");
                    clearEntities.put("notelp", phone);
                    clearEntities.put("confirm", "confirm");
                } else if (!preZero8.equals("08")) {
                    clearEntities.put("notelp", "");
                } else {
                    clearEntities.put("notelp", phone);
                    clearEntities.put("confirm", "confirm");
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
     * Validate Date Method untuk Memvalidasikan Date Birth/Tanggal Lahir yang
     * diinputkan User
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
        sdate = sdate.replace("/", " ");
        int leng = sdate.length();
        if (!sdate.contains(" ")) {
            String sday = sdate.substring(0, 2);
            String syear = "";
            String smonth = "";

            //120192
            if (leng < 7) {
                smonth = sdate.substring(2, sdate.length() - 2);
                syear = sdate.substring(sdate.length() - 2, sdate.length());
            } //12011992
            else if (sdate.chars().allMatch(Character::isDigit)) {
                smonth = sdate.substring(2, sdate.length() - 4);
                syear = sdate.substring(sdate.length() - 4, sdate.length());
            } //12jan1983
            else if (!sdate.chars().allMatch(Character::isDigit)) {
                syear = sdate.substring(sdate.length() - 4, sdate.length());
                smonth = sdate.substring(2, sdate.length() - 4);

                //12jan83
                if (!syear.chars().allMatch(Character::isDigit)) {
                    smonth = sdate.substring(2, sdate.length() - 2);
                    syear = sdate.substring(sdate.length() - 2, sdate.length());
                }
            }
            sdate = sday + " " + smonth + " " + syear;
        }
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
            String newmonth = month + "";
            if (newmonth.length() < 2) {
                newmonth = "0" + newmonth;
                month = Integer.parseInt(newmonth);
                arrDate[1] = newmonth;
            }

            int day = Integer.parseInt(arrDate[0]);
            String newday = day + "";
            if (newday.length() < 2) {
                newday = "0" + newday;
                arrDate[0] = newday;
                day = Integer.parseInt(newday);
            }
            if (String.valueOf(year).length() < 3) {
                SimpleDateFormat parser = new SimpleDateFormat("yy");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
                String strYear = formatter.format(parser.parse(year + ""));
                arrDate[2] = strYear;
                year = Integer.parseInt(strYear);
            }
            //Cek Tanggal Terakhir di Bulan tersebut
            String sDate1 = arrDate[0] + "/" + arrDate[1] + "/" + year;
            String sDate2 = "";
            if (month < 12) {
                month++;
                String monthnew = month + "";
                if (monthnew.length() < 2) {
                    monthnew = "0" + monthnew;
                }
                sDate2 = "01/" + monthnew + "/" + year;
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
     * Menggunakan BPJS di Siloam Method untuk mengecek Apakah Rumah Sakit yang
     * diketikan User Bisa menggunakan BPJS
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult siloamMenggunakanBPJS(ExtensionRequest extensionRequest) {
        String tempat = getEasyMapValueByName(extensionRequest, "tempat");
        String newtempat = tempat.toLowerCase();
        Map<String, String> output = new HashMap<>();
        StringBuilder respBuilder = new StringBuilder();

        switch (newtempat) {
            case "lippo village":
            case "kebon jeruk":
            case "surabaya":
            case "lippo cikarang":
            case "jambi":
            case "balikpapan":
            case "semanggi":
            case "manado":
            case "makasar":
            case "palembang":
            case "cinere":
            case "denpasar":
            case "tb simatupang":
            case "kuta":
            case "nusa dua":
            case "purwakarta":
            case "asri":
            case "kupang":
            case "labuan bajo":
            case "buton":
            case "samarinda":
            case "bekasi timur":
            case "sentosa":
            case "cirebon":
            case "bangka belitung":
            case "bekasi sepanjang jaya":
            case "lubuklinggau":
            case "jember":
                respBuilder.append("BPJS kesehatan dapat digunakan di Siloam Hospital " + tempat);
                break;
            default:
                respBuilder.append("BPJS kesehatan belum dapat digunakan di Siloam Hospital " + tempat);
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

    /**
     * Button Builder General Method Umum untuk membuat Button yang ada dalam
     * Carousel. Untuk bisa di gunakan Banyak Method
     *
     * @param imageUrl
     * @param title
     * @param subtitle
     * @param name
     * @param value
     * @return
     */
    private ButtonBuilder btnbuilderGeneral(String imageUrl, String title, String subtitle, String name, String value) {
        ButtonTemplate button = new ButtonTemplate();
        button.setPictureLink(imageUrl);
        button.setPicturePath(imageUrl);
        button.setTitle(title);
        button.setSubTitle(subtitle);
        List<EasyMap> actions = new ArrayList<>();
        EasyMap bookAction = new EasyMap();
        bookAction.setName(name);
        bookAction.setValue(value);
        actions.add(bookAction);
        button.setButtonValues(actions);
        ButtonBuilder buttonBuilder = new ButtonBuilder(button);
        return buttonBuilder;
    }

    ///// New Booking Dokter /////
    /**
     * Konfirmasi Tipe Method ini dipakai untuk Mengecek Flow Booking Dokter apa
     * yang di Pilih User Terdapat Flow Booking by Area, Spesialis, Nama. Method
     * ini pun di gunakan untuk Meng-Set Entitas Beberapa Entitas
     *
     * @param extensionRequest
     * @return
     */
    @Override
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
        clearEntities.put("counter", "0");

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
                button.setSubTitle("");
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
            String apiSpecialis = appProperties.getApiSpecialist() + "?top10=true";
            JSONArray resultsSpec = GeneralExecuteAPI(apiSpecialis).getJSONArray("data");
            int leng;
            leng = leng(code, resultsSpec);
            sb = carospec(sb, leng, resultsSpec);
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
            String apiSpesilisName = appProperties.getApiSpecialistbyname() + tipe;
            JSONObject jobj2 = GeneralExecuteAPI(apiSpesilisName);
            String apiDokterName = appProperties.getApiDoctorbyname() + tipe;
            JSONObject jobj3 = GeneralExecuteAPI(apiDokterName);
            if (jobj1.getInt("code") == 200) {
                konfirmtipe = "area";
                kode = 1;
            } else if (jobj2.getInt("code") == 200) {
                konfirmtipe = "spesialis";
                kode = 2;
            } else if (jobj3.getInt("code") == 200) {
                konfirmtipe = "nama";
                kode = 3;
            }
            String imageUrl = "";

            switch (kode) {
                case 1:
                    JSONArray resultsArea = jobj1.getJSONArray("data");
                    int leng = resultsArea.length();
                    for (int i = 0; i < leng; i++) {
                        JSONObject jObj = resultsArea.getJSONObject(i);
                        String hospitalId = jObj.getString("hospital_id");
                        String hospitalName = jObj.getString("hospital_name");

                        if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                            imageUrl = jObj.getString("image_url");
                        } else {
                            imageUrl = appProperties.getSiloamLogo();
                        }
                        //Buat Button
                        String value = hospitalId;
                        ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, hospitalName, "", hospitalName, value);
                        String btnBuilder = buatBtnBuilder.build().toString();
                        sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                    }
                    output.put(OUTPUT, sb.toString());
                    clearEntities.put("konfirmtipe", konfirmtipe);

                    break;
                case 2:
                    clearEntities.put("konfirmtipe", konfirmtipe);
                    JSONArray resultsSpesialis = jobj2.getJSONArray("data");
                    int leng2 = resultsSpesialis.length();
                    if (leng2 > 1) {
                        for (int i = 0; i < leng2; i++) {
                            JSONObject jObj = resultsSpesialis.getJSONObject(i);
                            String id_spesialis = jObj.getString("specialization_id");
                            String nameEn = jObj.getString("name_en");
                            String nameId = jObj.getString("name_id");
                            // Buat Button 
                            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                imageUrl = jObj.getString("image_url");
                            } else {
                                imageUrl = appProperties.getSiloamLogo();
                            }

                            ButtonTemplate button = new ButtonTemplate();
                            button.setPictureLink(imageUrl);
                            button.setPicturePath(imageUrl);
                            button.setTitle(nameId);
                            button.setSubTitle(nameEn);
                            List<EasyMap> actions = new ArrayList<>();
                            EasyMap bookAction = new EasyMap();
                            bookAction.setName(nameId);
                            bookAction.setValue("spesialis id " + id_spesialis);
                            actions.add(bookAction);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                            String btnBuilder = buttonBuilder.build().toString();

                            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                        }
                        String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin Anda kunjungi.";
                        output.put(OUTPUT, truecase + ParamSdk.SPLIT_CHAT + sb.toString());

                    } else {
                        JSONObject jObj = resultsSpesialis.getJSONObject(0);
                        String id_spesialis = jObj.getString("specialization_id");
                        String nameId = jObj.getString("name_id");
                        clearEntities.put("step_satu", id_spesialis);

                        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Baiklah anda telah memilih Spesialis " + nameId + ". Silahkan kirim lokasi anda untuk pencarian Siloam terdekat "
                                + "atau silahkan ketik nama Siloam hospitals yang ingin dituju.")
                                .add("Kirim Lokasi", "location").build();
                        output.put(OUTPUT, quickReplyBuilder.string());
                    }

                    break;
                case 3:
                    clearEntities.put("konfirmtipe", konfirmtipe);
                    clearEntities.put("step_satu", "1");
                    clearEntities.put("step_dua", "bbbbcccc-ddee-ffgg-hhii-jjkkllmmnnoo ya");
                    clearEntities.put("step_tiga", "aaaaaaaa-bbcc-ddee-ffgg-hhiijjkkllmm");
                    JSONArray resultsNama = jobj3.getJSONArray("data");
                    int leng3 = resultsNama.length();
                    for (int i = 0; i < leng3; i++) {
                        JSONObject jObj = resultsNama.getJSONObject(i);
                        String doctorId = jObj.getString("doctor_id");

                        String apiGetDokter = appProperties.getApiDoctorbydoctorid() + doctorId;
                        JSONObject objApiDoctor = GeneralExecuteAPI(apiGetDokter);

                        JSONArray resultsDoctor = objApiDoctor.getJSONArray("data");
                        int lengDoctor = resultsDoctor.length();
                        for (int j = 0; j < lengDoctor; j++) {
                            JSONObject jObj2 = resultsDoctor.getJSONObject(j);
                            String doctorid = jObj.getString("doctor_id");
                            String doctorName = jObj2.getString("doctor_name");
                            String hospitalId = jObj2.getString("hospital_id");
                            String doctorSpecialist = jObj2.getString("doctor_specialist");
                            String doctorHospitals = jObj2.getString("doctor_hospitals_unit");

                            //Buat Button
                            ButtonTemplate button = new ButtonTemplate();
                            button.setTitle(doctorName);
                            button.setSubTitle(doctorSpecialist + "<br/>" + doctorHospitals);
                            List<EasyMap> actions = new ArrayList<>();
                            EasyMap bookAction = new EasyMap();
                            bookAction.setName(doctorName);
                            bookAction.setValue("dokter id " + doctorid + " di hos " + hospitalId);
                            actions.add(bookAction);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                            String btnBuilder = buttonBuilder.build().toString();
                            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                        }
                    }
                    String dialog = "Berikut pilihan Dokter yang {bot_name} temukan. Silahkan pilih Dokter yang Anda ingin kunjungi.";
                    output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());

                    break;
                default:
                    ButtonTemplate button1 = new ButtonTemplate();
                    button1.setTitle("Cek Jadwal Dokter");
                    button1.setSubTitle("Berdasarkan :");
                    List<EasyMap> actions1 = new ArrayList<>();
                    EasyMap butAction11 = new EasyMap();
                    EasyMap butAction12 = new EasyMap();
                    EasyMap butAction13 = new EasyMap();

                    butAction11.setName("Area");
                    butAction11.setValue("area");
                    actions1.add(butAction11);

                    butAction12.setName("Nama");
                    butAction12.setValue("nama");
                    actions1.add(butAction12);

                    butAction13.setName("Spesialis");
                    butAction13.setValue("spesialis");
                    actions1.add(butAction13);

                    button1.setButtonValues(actions1);
                    ButtonBuilder buttonBuilder1 = new ButtonBuilder(button1);

                    CarouselBuilder carouselBuilder = new CarouselBuilder(buttonBuilder1.build());
                    String dialog1 = " Silakan pilih tipe pencarian dokter yang diinginkan.";
                    output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + carouselBuilder.build());
                    clearEntities.put("tipe_pencarian", "");
                    clearEntities.put("konfirmtipe", "");
                    break;
            }
        }
        extensionResult.setValue(output);
        extensionResult.setEntities(clearEntities);
        return extensionResult;
    }

    /**
     * Set Step Dua Method untuk Menampilkan Output pada Entitas Step_Dua
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult setStepDua(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        Map<String, String> output = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String konfirmtipe = getEasyMapValueByName(extensionRequest, "konfirmtipe");
        String counter = getEasyMapValueByName(extensionRequest, "counter");
        String stepsatu = getEasyMapValueByName(extensionRequest, "step_satu");
        String steptiga = getEasyMapValueByName(extensionRequest, "step_tiga");
        Map<String, String> clearEntities = new HashMap<>();
        String imageUrl = "";
        String areaId = "";

        String lowerstepsatu = stepsatu.toLowerCase();

        String apiHospitalName = appProperties.getApiHospitalName() + lowerstepsatu;
        JSONObject jobjHospitalName = GeneralExecuteAPI(apiHospitalName);
        String stat = "";
        String newstepdua = "";

        if (jobjHospitalName.getInt("code") == 200) {
            JSONArray resultsHospitalName = jobjHospitalName.getJSONArray("data");
            JSONObject jObj;
            if (stepsatu.equalsIgnoreCase("lippo village")) {
                jObj = resultsHospitalName.getJSONObject(1);
            } else {
                jObj = resultsHospitalName.getJSONObject(0);
            }
            String hospitalId = jObj.getString("hospital_id");
            stat = "hospital";
            newstepdua = hospitalId;
        } else {
            String apiHospital = appProperties.getApiHospital();
            JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
            int lenghospital = results.length();
            for (int i = 0; i < lenghospital; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String hospitalId = jObj.getString("hospital_id");
                String hospitalName = jObj.getString("hospital_name");
                String hospitalName2 = hospitalName.toLowerCase();
                if (stepsatu.equalsIgnoreCase(hospitalId) || stepsatu.equalsIgnoreCase(hospitalName2)) {
                    stat = "hospital";
                    newstepdua = hospitalId;
                    break;
                }
            }
        }
        if (stat.equalsIgnoreCase("hospital")) {
            int code = Integer.parseInt(counter);
            String apiSpec = appProperties.getApiSpecialistbyHospital() + newstepdua + "&top10=true";
            JSONArray resultsSpec = GeneralExecuteAPI(apiSpec).getJSONArray("data");
            int leng;
            leng = leng(code, resultsSpec);
            sb = carospec(sb, leng, resultsSpec);

            String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin Anda tuju.";
            output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());

            clearEntities.put("step_dua", newstepdua);
            extensionResult.setEntities(clearEntities);

        } else // Cek Apakah Entitas Tipe Pencarian
        if (stepsatu.equalsIgnoreCase("area") || stepsatu.equalsIgnoreCase("spesialis") || stepsatu.equalsIgnoreCase("nama")) {
            String tipe = stepsatu;
            if (tipe.equalsIgnoreCase("area")) {
                imageUrl = appProperties.getSiloamLogo();
                String apiArea = appProperties.getApiArea();
                JSONArray results = GeneralExecuteAPI(apiArea).getJSONArray("data");
                int leng = results.length();
                for (int i = 0; i < leng; i++) {
                    JSONObject jObj = results.getJSONObject(i);
                    areaId = jObj.getString("area_id");
                    String areaName = jObj.getString("area_name");

                    //Buat Button
                    String value = areaId;
                    ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, areaName, "", areaName, value);
                    String btnBuilder = buatBtnBuilder.build().toString();
                    sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                }
                String dialog = "Silahkan pilih area yang ingin Anda tuju.";
                String area = sb.toString();
                output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + area);
                clearEntities.put("konfirmtipe", tipe);

                // Spesialis
            } else if (tipe.equalsIgnoreCase("spesialis")) {
                int code = 0;

                String apiSpecialis = appProperties.getApiSpecialist() + "?top10=true";
                JSONArray resultsSpec = GeneralExecuteAPI(apiSpecialis).getJSONArray("data");
                int leng;
                leng = leng(code, resultsSpec);
                sb = carospec(sb, leng, resultsSpec);
                String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin Anda tuju.";
                output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());
                clearEntities.put("konfirmtipe", tipe);

                // Nama
            } else if (tipe.equalsIgnoreCase("nama")) {
                sb.append("Silahkan ketik nama Dokter yang ingin Anda kunjungi. Untuk {bot_name} bantu carikan.");
                output.put(OUTPUT, sb.toString());
                clearEntities.put("konfirmtipe", tipe);
            }
            clearEntities.put("step_satu", "");
            clearEntities.put("step_dua", "");
            clearEntities.replace("tipe_pencarian", tipe);
        } else {
            switch (konfirmtipe.toLowerCase()) {
                // Get Hospital by Area
                case "area":
                    String konfirmArea = "";
                    String apiArea = appProperties.getApiArea();
                    JSONArray resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
                    int lengArea = resultsArea.length();
                    if (lowerstepsatu.equals("bali") || lowerstepsatu.equals("nusa") || lowerstepsatu.equals("nusa tenggara")) {
                        lowerstepsatu = "bali & nusa tenggara";
                    }
                    for (int i = 0; i < lengArea; i++) {
                        JSONObject jObjArea = resultsArea.getJSONObject(i);
                        String idArea = jObjArea.getString("area_id");
                        String nameArea = jObjArea.getString("area_name");
                        String lowername = nameArea.toLowerCase();
                        if (lowerstepsatu.equals(idArea) || lowerstepsatu.equals(lowername)) {
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

                            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                imageUrl = jObj.getString("image_url");
                            } else {
                                imageUrl = appProperties.getSiloamLogo();
                            }

                            //Buat Button
                            String value = hospitalId;
                            ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, hospitalName, "", hospitalName, value);
                            String btnBuilder = buatBtnBuilder.build().toString();
                            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                        }
                        String dialog = "Silahkan pilih rumah sakit yang ingin kamu tuju.";
                        output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());
                        clearEntities.put("step_satu", areaId);
                    } else {
                        clearEntities.put("step_satu", "");
                        for (int i = 0; i < lengArea; i++) {
                            JSONObject jObjArea = resultsArea.getJSONObject(i);
                            String IdArea = jObjArea.getString("area_id");
                            String NameArea = jObjArea.getString("area_name");
                            imageUrl = appProperties.getSiloamLogo();

                            //Buat Button
                            String value = IdArea;
                            ButtonBuilder buatBtnBuilder = btnbuilderGeneral("", NameArea, NameArea, NameArea, value);
                            String btnBuilder = buatBtnBuilder.build().toString();
                            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                        }

                        String dialog = "Maaf. {bot_name} tidak dapat menemukan Rumah Sakit Siloam berdasarkan Area yang Anda pilih atau ketikan. "
                                + "Silahkan pilih atau ketikan kembali Area yang ingin Anda tuju.";
                        output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());
                    }

                    break;
                // Get Location by Spesialis
                case "spesialis":
                    if (stepsatu.equalsIgnoreCase("")) {
                        if (!steptiga.equalsIgnoreCase("")) {
                            stepsatu = steptiga;
                            clearEntities.put("step_satu", steptiga);
                        }
                    }
                    String apiSpecialisName = appProperties.getApiSpecialistbyname() + stepsatu;
                    JSONObject jsonobjSpecName = GeneralExecuteAPI(apiSpecialisName);
                    if (jsonobjSpecName.getInt("code") == 200) {
                        JSONArray resultsSpecName = jsonobjSpecName.getJSONArray("data");
                        int leng2 = resultsSpecName.length();
                        if (leng2 > 1) {
                            for (int i = 0; i < leng2; i++) {
                                JSONObject jObj = resultsSpecName.getJSONObject(i);
                                String id_spesialis = jObj.getString("specialization_id");
                                String nameEn = jObj.getString("name_en");
                                String nameId = jObj.getString("name_id");

                                // Buat Button 
                                String value = "spesialis id " + id_spesialis;
                                ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, nameId, nameEn, nameId, value);
                                String btnBuilder = buatBtnBuilder.build().toString();

                                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                            }
                            String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin Anda kunjungi.";
                            output.put(OUTPUT, truecase + ParamSdk.SPLIT_CHAT + sb.toString());
                            clearEntities.put("step_satu", "");

                        } else {
                            JSONObject jObj = resultsSpecName.getJSONObject(0);
                            String id_spesialis = jObj.getString("specialization_id");
                            String nameId = jObj.getString("name_id");
                            clearEntities.put("step_satu", id_spesialis);

                            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Baiklah anda telah memilih Spesialis " + nameId + ". Silahkan kirim lokasi anda untuk pencarian Siloam terdekat "
                                    + "atau silahkan ketik nama Siloam hospitals yang ingin dituju.")
                                    .add("Kirim Lokasi", "location").build();
                            output.put(OUTPUT, quickReplyBuilder.string());
                        }
                    } else {
                        String spesialis1 = "";
                        String nameId = "";
                        String apiSpesilisId = appProperties.getApiSpecialist();
                        JSONObject jobj = GeneralExecuteAPI(apiSpesilisId);
                        JSONArray dataSpec = jobj.getJSONArray("data");
                        int lengspec = dataSpec.length();
                        for (int i = 0; i < lengspec; i++) {
                            JSONObject jObjSpec = dataSpec.getJSONObject(i);
                            String idSpec = jObjSpec.getString("specialization_id");
                            nameId = jObjSpec.getString("name_id");
                            if (stepsatu.equalsIgnoreCase(idSpec)) {
                                spesialis1 = "id";
                                break;
                            }
                        }

                        int code = Integer.parseInt(counter);
                        if (stepsatu.equalsIgnoreCase("lainnya")) {
                            code++;
                            clearEntities.put("counter", code + "");
                            clearEntities.put("step_satu", "");
                            //------------------------------------------------------------------------//
                            String apiSpecialis = appProperties.getApiSpecialist();
                            JSONArray resultsSpec = GeneralExecuteAPI(apiSpecialis).getJSONArray("data");
                            int leng;
                            leng = leng(code, resultsSpec);
                            sb = carospec(sb, leng, resultsSpec);

                            String dialog = "Silakan pilih spesialisasi yang ingin dituju.";
                            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());

                        } else if (spesialis1.equalsIgnoreCase("id")) {

                            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Baiklah anda telah memilih " + nameId + ". Silahkan kirim lokasi anda untuk pencarian Siloam terdekat "
                                    + "atau silahkan ketik nama Siloam hospitals yang ingin dituju.")
                                    .add("Kirim Lokasi", "location").build();
                            output.put(OUTPUT, quickReplyBuilder.string());

                        } else {
                            clearEntities.put("step_satu", "");
                            String apiSpecialis = appProperties.getApiSpecialistbyname() + stepsatu;
                            JSONObject jsonobjSpec = GeneralExecuteAPI(apiSpecialis);
                            JSONArray resultsSpec = jsonobjSpec.getJSONArray("data");
                            try {
                                OkHttpUtil okHttpUtil = new OkHttpUtil();
                                okHttpUtil.init(true);

                                if (jsonobjSpec.getInt("code") == 200) {
                                    int leng;
                                    leng = resultsSpec.length();
                                    sb = carospec(sb, leng, resultsSpec);
                                    String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin Anda kunjungi.";
                                    output.put(OUTPUT, truecase + ParamSdk.SPLIT_CHAT + sb.toString());

                                } else {
                                    String apiSpecialisTop = appProperties.getApiSpecialist() + "?top10=true";
                                    JSONObject jsonobjSpecTop = GeneralExecuteAPI(apiSpecialisTop);
                                    JSONArray resultsSpecTop = jsonobjSpecTop.getJSONArray("data");
                                    int leng;
                                    leng = leng(code, resultsSpecTop);
                                    sb = carospec(sb, leng, resultsSpecTop);
                                    String falsecase1 = "Maaf {bot_name} tidak dapat menemukan spesialis yang Anda cari.";
                                    String falsecase2 = "Silahkan pilih Spesialis dibawah ini. Atau ketik kembali nama spesialis yang Anda inginkan dengan benar.";
                                    output.put(OUTPUT, falsecase1 + ParamSdk.SPLIT_CHAT + falsecase2 + ParamSdk.SPLIT_CHAT + sb.toString());
                                }

                            } catch (Exception e) {
                            }
                        }
                    }
                    break;
                // Get Doctor By Nama

                case "nama":
                    stepsatu = stepsatu.toLowerCase().replace("dr. ", "").replace("dr ", "").replace("prof. ", "").replace("prof ", "").replace("profesor ", "").replace("professor ", "");
                    String apiDokterName = appProperties.getApiDoctorbyname() + stepsatu;
                    JSONObject jobj3 = GeneralExecuteAPI(apiDokterName);
                    JSONArray resultsNama = jobj3.getJSONArray("data");
                    int leng3 = resultsNama.length();
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
                            LihatJadwal.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                            actions.add(LihatJadwal);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                            String btnBuilder = buttonBuilder.build().toString();
                            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                        }
                    }
                    String dialog = "Berikut pilihan Dokter yang {bot_name} temukan. Silahkan pilih Dokter yang Anda ingin kunjungi.";
                    output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());

                    clearEntities.put("step_dua", "2");
                    clearEntities.put("step_tiga", "3");

                    break;
            }
        }
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setEntities(clearEntities);
        extensionResult.setValue(output);

        return extensionResult;
    }

    /**
     * Set Step Tiga Method untuk Menampikan Output pada Entitas Step Tiga
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult setStepTiga(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        Map<String, String> output = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String konfirmtipe = getEasyMapValueByName(extensionRequest, "konfirmtipe");
        String counter = getEasyMapValueByName(extensionRequest, "counter");
        String stepsatu = getEasyMapValueByName(extensionRequest, "step_satu");
        String stepdua = getEasyMapValueByName(extensionRequest, "step_dua");
        Map<String, String> clearEntities = new HashMap<>();
        String lowerstepdua = stepdua.toLowerCase();

        String imageUrl = "";
        String konfirmArea = "";

        // Cek Apakah Area
        String apiArea = appProperties.getApiArea();
        JSONArray resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
        int lengArea = resultsArea.length();
        if (lowerstepdua.equals("bali") || lowerstepdua.equals("nusa") || lowerstepdua.equals("nusa tenggara")) {
            lowerstepdua = "bali & nusa tenggara";
        }
        String areaId = "";
        for (int i = 0; i < lengArea; i++) {
            JSONObject jObjArea = resultsArea.getJSONObject(i);
            String idArea = jObjArea.getString("area_id");
            String nameArea = jObjArea.getString("area_name");
            String lowername = nameArea.toLowerCase();
            if (lowerstepdua.equals(idArea) || lowerstepdua.equals(lowername)) {
                areaId = idArea;
                konfirmArea = "benar";
                break;
            }
        }
        if (konfirmArea.equalsIgnoreCase("benar")) {
            String apiHospital = appProperties.getApiHospitalByArea() + areaId;
            JSONArray resultsHospitals = GeneralExecuteAPI(apiHospital).getJSONArray("data");
            int leng = resultsHospitals.length();
            for (int i = 0; i < leng; i++) {
                JSONObject jObj = resultsHospitals.getJSONObject(i);
                String hospitalId = jObj.getString("hospital_id");
                String hospitalName = jObj.getString("hospital_name");

                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                    imageUrl = jObj.getString("image_url");
                } else {
                    imageUrl = appProperties.getSiloamLogo();
                }

                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setPictureLink(imageUrl);
                button.setPicturePath(imageUrl);
                button.setTitle(hospitalName);
                button.setSubTitle("");
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
            String dialog = "Silahkan pilih rumah sakit yang ingin kamu tuju.";
            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());
            clearEntities.put("step_satu", areaId);
            clearEntities.put("step_dua", "");
            extensionResult.setEntities(clearEntities);

        } else if (lowerstepdua.equalsIgnoreCase("area") || lowerstepdua.equalsIgnoreCase("spesialis") || lowerstepdua.equalsIgnoreCase("nama")) {
            String tipe = stepdua;
            if (tipe.equalsIgnoreCase("area")) {
                imageUrl = appProperties.getSiloamLogo();
                int leng = resultsArea.length();
                for (int i = 0; i < leng; i++) {
                    JSONObject jObj = resultsArea.getJSONObject(i);
                    areaId = jObj.getString("area_id");
                    String areaName = jObj.getString("area_name");

                    //Buat Button
                    String value = areaId;
                    ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, areaName, areaName, areaName, value);
                    String btnBuilder = buatBtnBuilder.build().toString();
                    sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                }
                String dialog = "Silahkan pilih area yang ingin Anda tuju.";
                String area = sb.toString();
                output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + area);
                clearEntities.put("konfirmtipe", tipe);

                // Spesialis
            } else if (tipe.equalsIgnoreCase("spesialis")) {
                int code = 0;
                String apiSpecialis = appProperties.getApiSpecialist() + "?top10=true";
                JSONArray resultsSpec = GeneralExecuteAPI(apiSpecialis).getJSONArray("data");
                int leng;
                leng = leng(code, resultsSpec);
                sb = carospec(sb, leng, resultsSpec);
                String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin Anda tuju.";
                output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());
                clearEntities.put("konfirmtipe", tipe);

                // Nama
            } else if (tipe.equalsIgnoreCase("nama")) {
                sb.append("Silahkan ketik nama Dokter yang ingin Anda kunjungi. Untuk {bot_name} bantu carikan.");
                output.put(OUTPUT, sb.toString());
                clearEntities.put("konfirmtipe", tipe);

            }
            clearEntities.put("step_satu", "");
            clearEntities.put("step_dua", "");
            clearEntities.put("step_tiga", "");
            clearEntities.replace("tipe_pencarian", tipe);
            extensionResult.setEntities(clearEntities);
        } else {
            switch (konfirmtipe.toLowerCase()) {
                // Get Specialist by Hospital
                case "area":
                    String newstepdua = "";
                    String apiHospitalName = appProperties.getApiHospitalName() + lowerstepdua;
                    JSONObject jobj1 = GeneralExecuteAPI(apiHospitalName);
                    String stat = "";

                    if (jobj1.getInt("code") == 200) {
                        JSONArray resultsHospitalName = jobj1.getJSONArray("data");
                        JSONObject jObj;
                        if (stepdua.equalsIgnoreCase("lippo village")) {
                            jObj = resultsHospitalName.getJSONObject(1);
                        } else {
                            jObj = resultsHospitalName.getJSONObject(0);
                        }
                        String hospitalId = jObj.getString("hospital_id");
                        stat = "hospital";
                        newstepdua = hospitalId;
                    } else {
                        String apiHospital = appProperties.getApiHospital();
                        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
                        int lenghospital = results.length();
                        for (int i = 0; i < lenghospital; i++) {
                            JSONObject jObj = results.getJSONObject(i);
                            String hospitalId = jObj.getString("hospital_id");
                            String hospitalName = jObj.getString("hospital_name");
                            String hospitalName2 = hospitalName.toLowerCase();
                            if (stepdua.equalsIgnoreCase(hospitalId) || stepdua.equalsIgnoreCase(hospitalName2)) {
                                stat = "hospital";
                                newstepdua = hospitalId;
                                break;
                            }
                        }
                    }
                    if (stat.equalsIgnoreCase("hospital")) {
                        int code = Integer.parseInt(counter);
                        String apiSpec = appProperties.getApiSpecialistbyHospital() + newstepdua + "&top10=true";
                        JSONArray resultsSpec = GeneralExecuteAPI(apiSpec).getJSONArray("data");
                        int leng;
                        leng = leng(code, resultsSpec);
                        sb = carospec(sb, leng, resultsSpec);

                        String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin Anda tuju.";
                        output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());

                        clearEntities.put("step_dua", newstepdua);
                        extensionResult.setEntities(clearEntities);
                    } else {
                        clearEntities.put("step_dua", "");
                        String apiHosArea = appProperties.getApiHospitalByArea() + stepsatu;
                        JSONArray results2 = GeneralExecuteAPI(apiHosArea).getJSONArray("data");
                        int leng = results2.length();
                        for (int i = 0; i < leng; i++) {
                            JSONObject jObj = results2.getJSONObject(i);
                            String hospitalId = jObj.getString("hospital_id");
                            String hospitalName = jObj.getString("hospital_name");

                            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                imageUrl = jObj.getString("image_url");
                            } else {
                                imageUrl = appProperties.getSiloamLogo();
                            }

                            //Buat Button
                            ButtonTemplate button = new ButtonTemplate();
                            button.setPictureLink(imageUrl);
                            button.setTitle(hospitalName);
                            button.setSubTitle("");
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
                        String dialog = "Maaf {bot_name} tidak dapat menemukan rumah sakit yang anda cari. Silahkan pilih atau ketikan kembali rumah sakit yang ingin anda tuju.";
                        output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());
                        extensionResult.setEntities(clearEntities);
                    }

                    break;
                // Get Hospital by Location //
                case "spesialis":
                    String apiSpecialisId = appProperties.getApiSpecialistbyId() + lowerstepdua;
                    JSONObject jsonobjSpecId = GeneralExecuteAPI(apiSpecialisId);
                    if (jsonobjSpecId.getInt("code") == 200) {
                        JSONArray resultsSpecId = jsonobjSpecId.getJSONArray("data");
                        JSONObject jObj = resultsSpecId.getJSONObject(0);
                        String id_spesialis = jObj.getString("specialization_id");
                        String nameId = jObj.getString("name_id");

                        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Baiklah anda telah memilih " + nameId + ". Silahkan kirim lokasi anda untuk pencarian Siloam terdekat "
                                + "atau silahkan ketik nama Siloam hospitals yang ingin dituju.")
                                .add("Kirim Lokasi", "location").build();
                        output.put(OUTPUT, quickReplyBuilder.string());
                        clearEntities.put("step_satu", id_spesialis);
                        clearEntities.put("step_dua", "");
                        extensionResult.setEntities(clearEntities);
                    } else {
                        apiHospitalName = appProperties.getApiHospitalName() + lowerstepdua;
                        jobj1 = GeneralExecuteAPI(apiHospitalName);
                        String idhos = "";
                        String hospitalName = "";
                        if (jobj1.getInt("code") == 200) {
                            JSONArray resultsHospitlName = jobj1.getJSONArray("data");
                            int leng = resultsHospitlName.length();
                            for (int i = 0; i < leng; i++) {
                                JSONObject jObj = resultsHospitlName.getJSONObject(i);
                                idhos = jObj.getString("hospital_id");
                                hospitalName = jObj.getString("hospital_name");
                            }
                            String apiGetDokter2 = appProperties.getApiDoctorbyhospitalIdSpecialist() + idhos + "&specialistId=" + stepsatu;
                            JSONArray results2 = GeneralExecuteAPI(apiGetDokter2).getJSONArray("data");
                            int leng2 = results2.length();
                            if (leng2 >= 10) {
                                leng2 = 10;
                            }
                            for (int i = 0; i < leng2; i++) {
                                JSONObject jObj = results2.getJSONObject(i);
                                String doctorId = jObj.getString("doctor_id");
                                String hospitalId = jObj.getString("hospital_id");
                                String doctorName = jObj.getString("doctor_name");
                                String doctorSpecialist = jObj.getString("doctor_specialist");
                                String doctorHospitals = jObj.getString("doctor_hospitals_unit");
                                imageUrl = "";
                                //Buat Button
                                ButtonTemplate button = new ButtonTemplate();
                                button.setTitle(doctorName);
                                button.setSubTitle(doctorSpecialist + "<br/>" + doctorHospitals);
                                List<EasyMap> actions = new ArrayList<>();
                                EasyMap bookAction = new EasyMap();
                                bookAction.setName(doctorName);
                                bookAction.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                                actions.add(bookAction);
                                button.setButtonValues(actions);
                                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                                String btnBuilder = buttonBuilder.build().toString();
                                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                            }
                            String dialog = "Berikut adalah daftar dokter yang dapat Anda pilih di " + hospitalName + ". (Atau ketik Menu untuk kembali ke Menu Utama)";
                            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());

                            clearEntities.put("step_dua", lowerstepdua);
                            clearEntities.put("step_tiga", idhos);
                            extensionResult.setEntities(clearEntities);
                        } else {
                            double latitude = extensionRequest.getIntent().getTicket().getLatitude();
                            double longitude = extensionRequest.getIntent().getTicket().getLongitude();

                            String apiHospital = appProperties.getApiHospital();
                            JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
                            int leng = results.length();
                            List<List<String>> data = new ArrayList<>();
                            double hasil;
                            for (int i = 0; i < leng; i++) {
                                JSONObject jObj = results.getJSONObject(i);
                                String hospitalid = jObj.getString("hospital_id");
                                String hospitalname = jObj.getString("hospital_name");
                                Double longitud = jObj.getDouble("longitude");
                                Double latitud = jObj.getDouble("latitude");
                                String phonenumber = jObj.optString("phoneNumber");
                                if (phonenumber.equals("")) {
                                    phonenumber = "+62211500181";
                                }
                                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                    imageUrl = jObj.getString("image_url");
                                } else {
                                    imageUrl = appProperties.getSiloamLogo();
                                }
                                if (latitud != 0 || longitud != 0) {
                                    hasil = distanceInKilometers((Double.valueOf(latitude)), (Double.valueOf(longitude)), latitud, longitud);
                                    List<String> jarak = new ArrayList<>();
                                    if (hasil < 30) {
                                        jarak.add(hasil + "");
                                        jarak.add(hospitalid);
                                        jarak.add(hospitalname);
                                        jarak.add(phonenumber);
                                        jarak.add(imageUrl);
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
                                String image = data.get(j).get(4);

                                //Buat Button
                                String value = idhospital;
                                ButtonBuilder buatBtnBuilder = btnbuilderGeneral(image, namehospital, "", namehospital, value);
                                String btnBuilder = buatBtnBuilder.build().toString();
                                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);

                            }
                            String dialog = "Berikut adalah daftar Siloam Hospitals yang terdekat dengan Anda.";
                            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());
                        }
                    }
                    break;
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
     * Get Doctor Method untuk membuat Carousel List Doctor Berdasarkan id
     * Doctor dan id Hospital
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult newGetDoctor(ExtensionRequest extensionRequest) {
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
        String steptiga = getEasyMapValueByName(extensionRequest, "step_tiga");
        Map<String, String> clearEntities = new HashMap<>();
        String imageUrl = "";

        String apiArea = appProperties.getApiArea();
        JSONArray resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
        if (steptiga.equalsIgnoreCase("area") || steptiga.equalsIgnoreCase("spesialis") || steptiga.equalsIgnoreCase("nama")) {
            String tipe = steptiga;
            if (tipe.equalsIgnoreCase("area")) {
                imageUrl = appProperties.getSiloamLogo();
                int leng = resultsArea.length();
                for (int i = 0; i < leng; i++) {
                    JSONObject jObj = resultsArea.getJSONObject(i);
                    String areaId = jObj.getString("area_id");
                    String areaName = jObj.getString("area_name");

                    //Buat Button
                    String value = areaId;
                    ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, areaName, areaName, areaName, value);
                    String btnBuilder = buatBtnBuilder.build().toString();
                    sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                }
                String dialog = "Silahkan pilih area yang ingin Anda tuju.";
                String area = sb.toString();
                output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + area);
                clearEntities.put("konfirmtipe", tipe);

                // Spesialis
            } else if (tipe.equalsIgnoreCase("spesialis")) {
                int code = 0;
                String apiSpecialis = appProperties.getApiSpecialist() + "?top10=true";
                JSONArray resultsSpec = GeneralExecuteAPI(apiSpecialis).getJSONArray("data");
                int leng;
                leng = leng(code, resultsSpec);
                sb = carospec(sb, leng, resultsSpec);
                String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin Anda tuju.";
                output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());
                clearEntities.put("konfirmtipe", tipe);

                // Nama
            } else if (tipe.equalsIgnoreCase("nama")) {
                sb.append("Silahkan ketik nama Dokter yang ingin Anda kunjungi. Untuk {bot_name} bantu carikan.");
                output.put(OUTPUT, sb.toString());
                clearEntities.put("konfirmtipe", tipe);

            }
            clearEntities.put("step_satu", "");
            clearEntities.put("step_dua", "");
            clearEntities.put("step_tiga", "");
            clearEntities.replace("tipe_pencarian", tipe);
            clearEntities.put("doctorid", "");
            extensionResult.setEntities(clearEntities);

        } else {
            switch (konfirmtipe.toLowerCase()) {
                // Get Doctor by Area
                case "area":
                    String[] splitspesialis = steptiga.split(" ");
                    String spesial1 = splitspesialis[0];

                    String apiHospitalbyId = appProperties.getApiHospitalbyId() + spesial1;
                    JSONObject jobjHospital = GeneralExecuteAPI(apiHospitalbyId);

                    String statArea = "";

                    int lengArea = resultsArea.length();
                    for (int i = 0; i < lengArea; i++) {
                        JSONObject jObjArea = resultsArea.getJSONObject(i);
                        String idArea = jObjArea.getString("area_id");
                        String nameArea = jObjArea.getString("area_name");
                        if (spesial1.equalsIgnoreCase(idArea) || spesial1.equalsIgnoreCase(nameArea)) {
                            statArea = "area";
                            break;
                        }
                    }
                    if (statArea.equalsIgnoreCase("area")) {
                        String apiHospital = appProperties.getApiHospitalByArea() + spesial1;
                        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
                        int leng = results.length();
                        for (int i = 0; i < leng; i++) {
                            JSONObject jObj = results.getJSONObject(i);
                            String hospitalId = jObj.getString("hospital_id");
                            String hospitalName = jObj.getString("hospital_name");

                            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                imageUrl = jObj.getString("image_url");
                            } else {
                                imageUrl = appProperties.getSiloamLogo();
                            }

                            //Buat Button
                            String value = hospitalId;
                            ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, hospitalName, "", hospitalName, value);
                            String btnBuilder = buatBtnBuilder.build().toString();
                            sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                        }
                        String dialog = "Silahkan pilih rumah sakit yang ingin kamu tuju.";
                        output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());
                        clearEntities.put("step_satu", spesial1);
                        clearEntities.put("step_dua", "");
                        clearEntities.put("step_tiga", "");
                        clearEntities.put("doctorid", "");
                        extensionResult.setEntities(clearEntities);

                    } else if (jobjHospital.getInt("code") == 200) {
                        int code = Integer.parseInt(counter);
                        String apiSpec = appProperties.getApiSpecialistbyHospital() + spesial1 + "&top10=true";
                        JSONArray resultsSpec = GeneralExecuteAPI(apiSpec).getJSONArray("data");
                        int leng;
                        leng = leng(code, resultsSpec);
                        sb = carospec(sb, leng, resultsSpec);

                        String dialog1 = "Silahkan pilih atau ketikan nama Spesialis yang ingin Anda tuju.";
                        output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + sb.toString());

                        clearEntities.put("step_dua", spesial1);
                        clearEntities.put("step_tiga", "");
                        clearEntities.put("doctorid", "");
                        extensionResult.setEntities(clearEntities);

                    } else {
                        String apiSpesilisId = appProperties.getApiSpecialist();
                        JSONObject jobj = GeneralExecuteAPI(apiSpesilisId);

                        String apiSpesilisName = appProperties.getApiSpecialistbyname() + spesial1;
                        JSONObject jobj2 = GeneralExecuteAPI(apiSpesilisName);

                        if (jobj2.getInt("code") == 200) {
                            JSONArray resultsSpesialis = jobj2.getJSONArray("data");
                            JSONObject jObj = resultsSpesialis.getJSONObject(0);
                            String id_spesialis = jObj.getString("specialization_id");
                            spesial1 = id_spesialis;
                            clearEntities.put("step_tiga", id_spesialis);
                            extensionResult.setEntities(clearEntities);

                        } else {
                            JSONArray dataSpec = jobj.getJSONArray("data");
                            int lengspec = dataSpec.length();
                            for (int i = 0; i < lengspec; i++) {
                                JSONObject jObjSpec = dataSpec.getJSONObject(i);
                                String idSpec = jObjSpec.getString("specialization_id");
                                if (spesial1.equalsIgnoreCase(idSpec)) {
                                    spesial1 = idSpec;
                                    break;
                                }
                            }
                        }
                        String apiGetDokter = appProperties.getApiDoctorbyhospitalIdSpecialist() + stepdua + "&specialistId=" + spesial1;
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
                                imageUrl = "";
                                //Buat Button
                                ButtonTemplate button = new ButtonTemplate();
                                button.setTitle(doctorName);
                                button.setSubTitle(doctorSpecialist + "<br/>" + doctorHospitals);
                                List<EasyMap> actions = new ArrayList<>();
                                EasyMap bookAction = new EasyMap();
                                bookAction.setName(doctorName);
                                bookAction.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                                actions.add(bookAction);
                                button.setButtonValues(actions);
                                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                                String btnBuilder = buttonBuilder.build().toString();
                                sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                            }
                            String dialog = "Berikut adalah daftar dokter yang dapat Anda pilih. (Atau ketik Menu untuk kembali ke Menu Utama).";
                            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());
                        } else {
                            String dialog = "Maaf {bot_name} tidak menemukan daftar dokter yang anda cari. Mohon hubungi Contact Center +1500181";
                            output.put(OUTPUT, dialog);
                            clearEntities.replace("tanggalpesan", "13 00-16 00=2019-03-28");
                            clearEntities.replace("jampraktek", "10 00");
                            clearEntities.replace("namapasien", "Admin");
                            clearEntities.replace("tanggallahir", "1995-01-01");
                            clearEntities.replace("notelp", "081318151400");
                            clearEntities.replace("confirm", "yes");
                        }
                        extensionResult.setEntities(clearEntities);

                    }
                    break;
                case "spesialis":

                    String[] splitspesialis2 = stepsatu.split(" ");
                    String spesial2 = splitspesialis2[0];

                    String apiGetDokter2 = appProperties.getApiDoctorbyhospitalIdSpecialist() + steptiga + "&specialistId=" + spesial2;
                    JSONArray results2 = GeneralExecuteAPI(apiGetDokter2).getJSONArray("data");
                    int leng = results2.length();
                    if (leng >= 10) {
                        leng = 10;
                    }
                    for (int i = 0; i < leng; i++) {
                        JSONObject jObj = results2.getJSONObject(i);
                        String doctorId = jObj.getString("doctor_id");
                        String hospitalId = jObj.getString("hospital_id");
                        String doctorName = jObj.getString("doctor_name");
                        String doctorSpecialist = jObj.getString("doctor_specialist");
                        String doctorHospitals = jObj.getString("doctor_hospitals_unit");
                        imageUrl = "";
                        //Buat Button
                        ButtonTemplate button = new ButtonTemplate();
//                        button.setPictureLink("");
//                        button.setPicturePath("");
                        button.setTitle(doctorName);
                        button.setSubTitle(doctorSpecialist + "<br/>" + doctorHospitals);

                        List<EasyMap> actions = new ArrayList<>();
                        EasyMap bookAction = new EasyMap();
                        bookAction.setName(doctorName);
                        bookAction.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                        actions.add(bookAction);
                        button.setButtonValues(actions);
                        ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                        String btnBuilder = buttonBuilder.build().toString();
                        sb.append(btnBuilder).append(CONSTANT_SPLIT_SYNTAX);
                    }
                    String dialog = "Berikut adalah daftar dokter yang dapat Anda pilih. (Atau ketik Menu untuk kembali ke Menu Utama).";
                    output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + sb.toString());

                    break;
            }
        }
        extensionResult.setValue(output);
        return extensionResult;

    }

    /**
     * Get Schedule Doctor Method ini untuk menampilkan Carousel Jadwal dari
     * Dokter yang di Pilih pada Step Sebelumnya Berupa Tanggal Praktek Dokter
     * yang dipilih untuk 7 Hari kedepan di mulai Dari H+1
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult newGetScheduleDoctorId(ExtensionRequest extensionRequest) {
        StringBuilder proctime = new StringBuilder();
        Date dnow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss SSS");
        proctime.append(ft.format(dnow) + " # ");

        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        Map<String, String> clearEntities = new HashMap<>();

        String doctorId = getEasyMapValueByName(extensionRequest, "dokterid");
        String stepdua = getEasyMapValueByName(extensionRequest, "step_dua");
        String steptiga = getEasyMapValueByName(extensionRequest, "step_tiga");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +1);
        String[] iddokter = doctorId.split(" ");
        String dokid = iddokter[0];
        if (dokid.equalsIgnoreCase("id")) {
            dokid = iddokter[1];
        }

        String hosid = "";
        String[] idhos = stepdua.split(" ");
        hosid = idhos[0];
        if (hosid.equalsIgnoreCase("hos")) {
            hosid = idhos[1];
        }

        String schedule = appProperties.getApiDoctorschedule() + dokid + "/" + hosid;
        JSONObject jobjSchedule = GeneralExecuteAPI(schedule);
        int code = jobjSchedule.getInt("code");
        if (code == 200) {
            JSONArray results = jobjSchedule.getJSONArray("data");
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
                    String tanggaltitle = hari + ", " + tanggal + " " + bulan + " " + tahun;
                    if (dayslist.contains(kodeHari)) {
                        //Buat Button
                        ButtonTemplate button = new ButtonTemplate();
                        button.setTitle(tanggaltitle);
                        button.setSubTitle(daypoint[x] + " | " + available);
                        List<EasyMap> actions = new ArrayList<>();
                        EasyMap bookAction = new EasyMap();
                        bookAction.setName(tanggaltitle);
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
                clearEntities.replace("tanggalpesan", "13:00-16:00=2019-03-28");
                clearEntities.replace("jampraktek", "10:00");
                clearEntities.replace("namapasien", "Admin");
                clearEntities.replace("tanggallahir", "1995-01-01");
                clearEntities.replace("notelp", "081318151400");
                clearEntities.replace("confirm", "yes");

                sb.append("Maaf. {bot_name} tidak dapat menemukan Jadwal Dokter pilihan Anda.");
                output.put(OUTPUT, sb.toString());
                extensionResult.setEntities(clearEntities);

            } else {
                String getDoctorByDoctorId = appProperties.getApiDoctorbydoctorid() + dokid;
                JSONArray results3 = GeneralExecuteAPI(getDoctorByDoctorId).getJSONArray("data");
                JSONObject jObj3 = results3.getJSONObject(0);
                String doctorName = jObj3.getString("doctor_name");
                String dialog1 = "Berikut adalah detail jadwal praktik " + doctorName + ". (Atau ketik Menu untuk kembali ke Menu Utama).";
                output.put(OUTPUT, dialog1 + ParamSdk.SPLIT_CHAT + stringbuild);
            }
        } else {
            clearEntities.replace("step_tiga", steptiga);
            clearEntities.replace("tanggalpesan", "13:00-16:00=2019-03-28");
            clearEntities.replace("jampraktek", "10:00");
            clearEntities.replace("namapasien", "Admin");
            clearEntities.replace("tanggallahir", "1995-01-01");
            clearEntities.replace("notelp", "081318151400");
            clearEntities.replace("confirm", "yes");

            sb.append("Maaf Silvia tidak menemukan jadwal dokter yang anda cari. Mohon hubungi Siloam Hospital yang dituju.");
            output.put(OUTPUT, sb.toString());
            extensionResult.setEntities(clearEntities);

        }

        output.put("extra", proctime.toString());
        extensionResult.setValue(output);
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        return extensionResult;
    }

    /**
     * Get Jam Praktek Method ini untuk membuat Carousel Jam Pratek Dokter
     * Berdasarkan Tanggal Pesan/Tanggal Praktek Dokter yang dipilih User
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult newGetJamPraktek(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);

        Map<String, String> output = new HashMap<>();
        String doctorId = getEasyMapValueByName(extensionRequest, "dokterid");
        String[] iddokter = doctorId.split(" ");
        String dokid = iddokter[1];

        String hospitalId = getEasyMapValueByName(extensionRequest, "step_dua");
        String[] idhos = hospitalId.split(" ");
        String hosid = idhos[1];

        String tanggalpesan = getEasyMapValueByName(extensionRequest, "tanggalpesan");
        String[] jampilihan = tanggalpesan.split("=");
        String jam = jampilihan[0];
        String tanggal = jampilihan[1];
        String[] splittanggal = tanggal.split("-");
        String hari = splittanggal[2];
        String bulan = splittanggal[1];
        String tahun = splittanggal[0];
        String newdate = hari + "-" + bulan + "-" + tahun;

        //----------------//
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

            String getSchedule = appProperties.getApiDoctorappointment() + hosid + "/doctorId/" + dokid + "/date/" + tanggal;
            JSONArray arraySchedule = GeneralExecuteAPI(getSchedule).getJSONArray("data");
            String carojam = CarouselJamPraktek(arraySchedule);
            String dialog = "Anda telah memilih tanggal " + newdate + ". Silahkan pilih waktu kunjungan antara " + newsplit + " yang Anda kehendaki.";
            output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + carojam);
            extensionResult.setValue(output);
        } else {
            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("jampraktek", "10:00");
            clearEntities.put("namapasien", "admin");
            clearEntities.put("tanggallahir", "1999-01-01");
            clearEntities.put("notelp", "081318141513");
            clearEntities.put("confirm", "yes");
            extensionResult.setEntities(clearEntities);
        }
        return extensionResult;
    }

    /**
     * Tanya Nama Pasien Method untuk memberikan Question kepada User untuk
     * menginputkan Nama User Alasan di buatkan Method, Agar OUTPUT dari Method
     * ini bisa dipakai ketika Entitas 'namapasien' tidak sesuai Format, dan
     * Entitas tersebut akan di Clear. Lalu di tampilkan kembali Pertanyaan
     * untuk menginputkan Nama User Kembali.
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult tanyaNamaPasien(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        Map<String, String> clearEntities = new HashMap<>();
        String jampraktek = getEasyMapValueByName(extensionRequest, "jampraktek");

        if (jampraktek.contains("=")) {
            String[] jampilihan = jampraktek.split("=");
            String jam = jampilihan[0];
            String tanggal = jampilihan[1];
            String[] splittanggal = tanggal.split("-");
            String hari = splittanggal[2];
            String bulan = splittanggal[1];
            String tahun = splittanggal[0];
            String newdate = hari + "-" + bulan + "-" + tahun;

            String doctorId = getEasyMapValueByName(extensionRequest, "dokterid");
            String[] iddokter = doctorId.split(" ");
            String dokid = iddokter[1];

            String hospitalId = getEasyMapValueByName(extensionRequest, "step_dua");
            String[] idhos = hospitalId.split(" ");
            String hosid = idhos[1];

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

                String getSchedule = appProperties.getApiDoctorappointment() + hosid + "/doctorId/" + dokid + "/date/" + tanggal;
                JSONArray arraySchedule = GeneralExecuteAPI(getSchedule).getJSONArray("data");
                String carojam = CarouselJamPraktek(arraySchedule);
                String dialog = "Anda telah memilih tanggal " + newdate + ". Silahkan pilih waktu kunjungan antara " + newsplit + " yang Anda kehendaki.";
                output.put(OUTPUT, dialog + ParamSdk.SPLIT_CHAT + carojam);
                clearEntities.put("jampraktek", "");
                clearEntities.put("tanggalpesan", jampraktek);
                extensionResult.setEntities(clearEntities);

            } else {

                clearEntities.put("jampraktek", "10:00");
                clearEntities.put("namapasien", "admin");
                clearEntities.put("tanggallahir", "1999-01-01");
                clearEntities.put("notelp", "081318141513");
                clearEntities.put("confirm", "yes");
                extensionResult.setEntities(clearEntities);
            }

        } else if (jampraktek.contains(":")) {
            String dialog1 = "Untuk pembuatan perjanjian. Silakan ketik nama lengkap Anda.";
            output.put(OUTPUT, dialog1);
        }

        extensionResult.setValue(output);
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        return extensionResult;
    }

    /**
     * Validasi Nama Pasien Method untuk mengecek Apakah benar Format/yang di
     * ketikan User merupakan Nama Dan tidak Mengandung Angka atau Karakter
     *
     * @param extensionRequest
     * @return
     */
    @Override
    public ExtensionResult validasiNamaPasien(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        String namapasien = getEasyMapValueByName(extensionRequest, "namapasien");
        if (namapasien.matches("[\\d].*")) {
            Map<String, String> clearEntities = new HashMap<>();
            clearEntities.put("namapasien", "");
            extensionResult.setEntities(clearEntities);

            String dialog1 = "Maaf, Nama yang anda ketik mengandung Angka atau Karakter. Silahkan ketik kembali Nama lengkap anda.";
            output.put(OUTPUT, dialog1);
            extensionResult.setValue(output);
        } else {
            String dialog1 = "Silakan ketik tanggal lahir Anda dengan format (tanggal/bulan/tahun)";
            output.put(OUTPUT, dialog1);

            extensionResult.setValue(output);

        }
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        return extensionResult;
    }

    /**
     * Post Create Appointment Method untuk membentuk Body Message/Formatnya dan
     * lalu di Post Body Message Appointment tersebut ke API Siloam.
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
        String hospitalId = getEasyMapValueByName(extensionRequest, "step_dua");
        String namapasien = getEasyMapValueByName(extensionRequest, "namapasien");
        String tanggallahir = getEasyMapValueByName(extensionRequest, "tanggallahir");
        String notelp = getEasyMapValueByName(extensionRequest, "notelp");
        String tanggalPesan = getEasyMapValueByName(extensionRequest, "tanggalpesan");
        String jamPraktek = getEasyMapValueByName(extensionRequest, "jampraktek");

        String jammenit = jamPraktek.replace(" ", ":");

        String[] iddokter = dokterid.split(" ");
        String dokid = iddokter[1];

        String[] idhos = hospitalId.split(" ");
        String hospital = idhos[1];

        String[] tanggal = tanggalPesan.split("=");
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
        return extensionResult;
    }
}
