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

import com.google.gson.Gson;
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
import com.imi.dolphin.sdkwebservice.builder.ImageBuilder;
import com.imi.dolphin.sdkwebservice.builder.QuickReplyBuilder;
import com.imi.dolphin.sdkwebservice.model.Appointment;
import com.imi.dolphin.sdkwebservice.model.ButtonTemplate;
import com.imi.dolphin.sdkwebservice.model.EasyMap;
import com.imi.dolphin.sdkwebservice.model.ExtensionRequest;
import com.imi.dolphin.sdkwebservice.model.ExtensionResult;
import com.imi.dolphin.sdkwebservice.model.MonthBuilder;
import com.imi.dolphin.sdkwebservice.property.AppProperties;
import com.imi.dolphin.sdkwebservice.token.Token;
import com.imi.dolphin.sdkwebservice.util.OkHttpUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
//import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.Collections;
import java.util.Comparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author reja
 *
 */
@Service
public class ServiceImp implements IService {

    private static final Logger log = LogManager.getLogger(ServiceImp.class);
    public static final String OUTPUT = "output";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CONTACT_NAME = "contact_name";
    public static final String CONTACT_PHONE_NAME = "contact_phone_number";
    private Token userToken;
    private static final String SAMPLE_IMAGE_PATH = "https://goo.gl/SHdL8D";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String SPLIT = "&split&";

    private static final String QUICK_REPLY_SYNTAX = "{replies:title=";
    private static final String COMMA = ",";
    private static final String QUICK_REPLY_SYNTAX_SUFFIX = "}";
    private final String pathdir = System.getProperty("user.dir");
    @Autowired
    AppProperties appProp;

    @Autowired
    IMailService svcMailService;

    /**
     * Get parameter value from request body parameter
     *
     * @param extensionRequest Data Request
     * @param name Nama Entitas dari Dialog
     * @return String value dari Entitas
     */
    private String getEasyMapValueByName(ExtensionRequest extensionRequest, String name) {
        EasyMap easyMap = extensionRequest.getParameters().stream().filter(x -> x.getName().equals(name)).findAny()
                .orElse(null);
        if (easyMap != null) {
            return easyMap.getValue();
        }
        return "";
    }

    /**
     * Untuk koneksi ke Agent atau CS Dengan men-SetAgent : True
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
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

    @Override
    public ExtensionResult getImage(ExtensionRequest extensionRequest) {
        log.debug("getImage() extension request: {}", extensionRequest);
        Map<String, String> output = new HashMap<>();
//        String imagemap = sdkUtil.getEasyMapValueByName(extensionRequest, "param");

        ButtonTemplate image = new ButtonTemplate();
//        image.setPictureLink("https://github.com/muderiz/image/blob/master/Siloam%20Logo.png?raw=true");
        image.setPictureLink(appProp.getSiloamImageListSpesialis());
        image.setPicturePath(appProp.getSiloamImageListSpesialis());
//        image.setTitle(""");
//        image.setSubTitle("Test");
        ImageBuilder imageBuilder = new ImageBuilder(image);
        output.put(OUTPUT, imageBuilder.build());
//        output.put(OUTPUT, documentBuilder.build());

        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        log.debug("Output Image() extension request: {}", output);
        return extensionResult;
    }

    /**
     * Tipe Pencarian Bertujuan untuk menampilkan kembali Carousel Tipe
     * Pencarian ketika user memulai dari Intention Booking Doctor
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
     */
    @Override
    public ExtensionResult TipePencarian(ExtensionRequest extensionRequest) {
        log.debug("TipePencarian() extension request: {}", new Gson().toJson(extensionRequest, ExtensionRequest.class));
        Map<String, String> output = new HashMap<>();

        //Button 1
        ButtonTemplate button1 = new ButtonTemplate();
        button1.setPictureLink(appProp.getSiloamImageTipePencarian());
        button1.setPicturePath(appProp.getSiloamImageTipePencarian());
        button1.setTitle("Cari Dokter");
        button1.setSubTitle("Berdasarkan :");
        List<EasyMap> actions1 = new ArrayList<>();
        EasyMap butAction11 = new EasyMap();
        EasyMap butAction12 = new EasyMap();
        EasyMap butAction13 = new EasyMap();

        butAction12.setName("Nama Dokter");
        butAction12.setValue("nama");
        actions1.add(butAction12);

        butAction13.setName("Bidang Spesialis");
        butAction13.setValue("spesialis");
        actions1.add(butAction13);

        butAction11.setName("Rumah Sakit");
        butAction11.setValue("area");
        actions1.add(butAction11);

        button1.setButtonValues(actions1);
        ButtonBuilder buttonBuilder1 = new ButtonBuilder(button1);

        CarouselBuilder carouselBuilder = new CarouselBuilder(buttonBuilder1.build());
        String dialog1 = "Anda telah memilih untuk mencari dokter. Mohon pilih salah satu dari opsi di bawah ini untuk memudahkan pencarian Anda.:";

        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Untuk kembali ke menu utama. ketik \"menu\" atau klik tombol di bawah ini: ")
                .add("Menu Utama", "menu utama").build();
        output.put(OUTPUT, dialog1 + SPLIT + carouselBuilder.build() + SPLIT + quickReplyBuilder.string());
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    /**
     * Send Location
     *
     * Send Location Bertujuan untuk membuat Sebuah Quickreply untuk menanyakan
     * Lokasi user
     *
     * .add("Kirim Lokasi", "location") : untuk membuat Button Suggestion dengan
     * "Kirim Lokasi" sebagai Title dari Button dan "location" sebagai
     * Payload/Value dari Button. Bertujuan untuk menanyakan Lokasi User.
     *
     * .add("Pilih Area", "jadwal dokter area") : untuk membuat Button
     * Suggestion dengan "Pilih Area" sebagai Title dari Button dan "jadwal
     * dokter area" sebagai Payload/Value dari Button. Bertujuan untuk masuk ke
     * Dialog Jadwal Dokter by Area.
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
     */
    @Override
    public ExtensionResult doSendLocation(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Silahkan kirim lokasi Anda sekarang.")
                .add("Kirim Lokasi", "location").add("Pilih Area", "jadwal dokter area").build();
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
     * Bertujuan untuk mendapatkan Body Message dari API
     *
     * @param link berisi URL API yang ingin di Cek Body Messagenya
     * @return jsonobj : Return berupa JSon Object
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
            System.out.println(ex);
        }

        return jsonobj;
    }

    /**
     * Nearest Hospital
     *
     * Method untuk mencari Rumah Sakit Siloam Terdekat Berdasarkan Latitude
     * Longitude yang sudah kirimkan oleh User
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
     */
    @Override
    public ExtensionResult doGetHospitalTerdekat(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();

        // Get Long Lat Berdasarkan ID user //
        Double latitude = extensionRequest.getIntent().getTicket().getLatitude();
        Double longitude = extensionRequest.getIntent().getTicket().getLongitude();
//        String latitude = getEasyMapValueByName(extensionRequest, "latitude");
//        String longitude = getEasyMapValueByName(extensionRequest, "longitude");

        // ------------------------------- //
        System.out.println(latitude);
        System.out.println(longitude);
        String apiHospital = appProp.getApiBaseUrl() + appProp.getApiHospital();
        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
        int leng = results.length();
        List<List<String>> data = new ArrayList<>();
        double hasil;
        String imageUrl = "";
        for (int i = 0; i < leng; i++) {
            JSONObject jObj = results.getJSONObject(i);
            String hospitalid = jObj.getString("hospital_id");
            String hospitalname = jObj.getString("name");
            Double longitud = jObj.getDouble("longitude");
            Double latitud = jObj.getDouble("latitude");
            String phonenumber = jObj.optString("phoneNumber");
            hospitalname = hospitalname.replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
            if (phonenumber.equals("")) {
                phonenumber = "+62211500181";
            }
            if (!jObj.optString("image_url").equals("")) {
                imageUrl = jObj.optString("image_url");
            } else {
                imageUrl = appProp.getSiloamLogo();
            }
            if (latitud != 0 || longitud != 0) {
                hasil = distanceInKilometers(latitude, longitude, latitud, longitud);
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

        // Untuk membuat Carousel dengan 3 buah Button Action //
        for (int j = 0; j < data.size(); j++) {
            String idhospital = data.get(j).get(1);
            String namehospital = data.get(j).get(2);
            String phonenum = data.get(j).get(3);
            String image = data.get(j).get(4);
            if (phonenum.equalsIgnoreCase("")) {
                phonenum = "000";
            } else {
                phonenum = namehospital + "&" + phonenum;
            }

            ButtonTemplate button = new ButtonTemplate();
            button.setPictureLink(image);
            button.setPicturePath(image);
            button.setTitle(namehospital);
            button.setSubTitle("");
            List<EasyMap> actions = new ArrayList<>();

            EasyMap direction = new EasyMap();
            EasyMap callAction = new EasyMap();
            EasyMap booknow = new EasyMap();

            booknow.setName("Cari Dokter");
            booknow.setValue("jadwal dokter via booknow di area konter 0 di 33334444-5555-6666-7777-888888999999 di " + idhospital);
            actions.add(booknow);
            button.setButtonValues(actions);

            callAction.setName("Hubungi RS");
            callAction.setValue("mau call hospital nomor " + phonenum + " ya");
            actions.add(callAction);
            button.setButtonValues(actions);

            direction.setName("Petunjuk Arah");
            direction.setValue(appProp.getGoogleMapQuery() + namehospital);
            actions.add(direction);
            button.setButtonValues(actions);

            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

            String btnBuilder = buttonBuilder.build().toString();
            sb.append(btnBuilder).append(SPLIT);

        }
        // --------------------------------------- //

        // Kondisi ketika sb(String Builder) yang di gunakan untuk membuat JSON Carousel di atas Kosong atau Tidak. 
        // Akan null/kosong ketika Pencarian by Long Lat tidak di temukan Data Rumah Sakit terdekat
        if (sb.toString().isEmpty()) {
            String apiArea = appProp.getApiBaseUrl() + appProp.getApiArea();
            JSONArray resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
            int lengArea = resultsArea.length();
            for (int i = 0; i < lengArea; i++) {
                JSONObject jObj = resultsArea.getJSONObject(i);
                String areaId = jObj.getString("area_id");
                String areaName = jObj.getString("name");

                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                    imageUrl = jObj.optString("image_url");
                    button.setPictureLink(imageUrl);
                    button.setPicturePath(imageUrl);
                } else {
                    imageUrl = appProp.getSiloamLogo();
                }

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
                sb.append(btnBuilder).append(SPLIT);
            }
            String dialog1 = "Mohon maaf Siloam Hospitals belum tersedia disekitar lokasi Anda. Silahkan pilih dari opsi berikut:";
            output.put(OUTPUT, dialog1 + SPLIT + sb.toString());
        } else {
            String dialog1 = "Berikut adalah daftar RS Siloam yang terdekat dengan Anda. Bila Anda telah menemukan RS yang Anda cari, silahkan klik menu yang tersedia untuk membuat janji temu, menghubungi RS, atau untuk petunjuk arah.";
            output.put(OUTPUT, dialog1 + SPLIT + sb.toString());
        }
        // ------------------------------------------------------------------------------------------------------- //
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        extensionResult.setValue(output);
        return extensionResult;
    }

    /**
     * Distance in Kilometers
     *
     * Method ini di Pakai oleh Method Nearest Hospital (doGetHospitalTerdekat)
     * Untuk menghitung Jarak Kilometers berdasarkan Longitude Latitude User
     * Dengan Longitude Latitude masing masing Rumah Sakit
     *
     * @param lat1 Latitude User
     * @param long1 Longitude User
     * @param lat2 Latitude get From API
     * @param long2 Longtude get From API
     * @return Body JSON to Chatbot
     */
    private double distanceInKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;
        return 6371.01 * Math.acos(Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1));
    }

    /**
     * Call Hospital
     *
     * Method untuk Memberitahukan User nomor Telepon masing masing Rumah Sakit
     *
     * @param extensionRequest Data Request
     * @return Body JSON From Chatbot
     */
    @Override
    public ExtensionResult doCallHospital(ExtensionRequest extensionRequest) {
        Map<String, String> output = new HashMap<>();
        String callhospital = getEasyMapValueByName(extensionRequest, "callhospital");
        String[] splitCall = callhospital.split("&amp;");
        String hospitalname = splitCall[0];
        String hospitalcall = splitCall[1];
        String dialog = "";
        hospitalname = hospitalname.replace("Telp ", "").replace("telp ", "");
        if (hospitalcall.equalsIgnoreCase("000")) {
            dialog = "Maaf. nomor telepon Hospital tersebut belum tersedia.";
        } else {
            dialog = "Nomor telepon " + hospitalname + " yang dapat Anda hubungi : " + hospitalcall;
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
     * Days/Hari
     *
     * Method untuk Mengkonversikan Hari dari Nama Hari ke dalam Angka.
     *
     * @param day Nama Hari
     * @return int Kode Hari
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

    private String NamaHari(String day) {
        String Namahariindo = "";
        switch (day) {
            case "Mon":
                Namahariindo = "Senin";
                break;
            case "Tue":
                Namahariindo = "Selasa";
                break;
            case "Wed":
                Namahariindo = "Rabu";
                break;
            case "Thu":
                Namahariindo = "Kamis";
                break;
            case "Fri":
                Namahariindo = "Jumat";
                break;
            case "Sat":
                Namahariindo = "Sabtu";
                break;
            case "Sun":
                Namahariindo = "Minggu";
                break;

        }
        return Namahariindo;
    }

    private String NamaBulan(String bulan) {
        String Namabulanindo = "";
        switch (bulan) {
            case "Jan":
                Namabulanindo = "Jan";
                break;
            case "Feb":
                Namabulanindo = "Feb";
                break;
            case "Mar":
                Namabulanindo = "Mar";
                break;
            case "Apr":
                Namabulanindo = "Apr";
                break;
            case "May":
                Namabulanindo = "Mei";
                break;
            case "Jun":
                Namabulanindo = "Jun";
                break;
            case "Jul":
                Namabulanindo = "Jul";
                break;
            case "Aug":
                Namabulanindo = "Agu";
                break;
            case "Sep":
                Namabulanindo = "Sep";
                break;
            case "Oct":
                Namabulanindo = "Okt";
                break;
            case "Nov":
                Namabulanindo = "Nov";
                break;
            case "Dec":
                Namabulanindo = "Des";
                break;

        }
        return Namabulanindo;
    }

    /**
     * Months/Bulan
     *
     * Method untuk Mengkonversikan Bulan dari Nama Bulan ke dalam Angka.
     *
     * @param monthName Nama Bulan
     * @return int Kode Bulan
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
     * Carousel Jam Praktek
     *
     * Method untuk membuat Carousel Jam Praktek yang Dinamis
     *
     * @param jampraktek Array Jam Praktek Dokter (Array 'jampraktek' Yang di
     * dapat dari Data Jam Praktek Dokter berdasarkan Tanggal Praktek)
     * @return String CarouselJam
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
        String titlejam = "";
        for (int x = 0; x < jumlahbagi; x++) {
            ButtonTemplate button = new ButtonTemplate();
            List<EasyMap> actions = new ArrayList<>();
            for (int y = 0; y < 3;) {
                if (index == jampraktekleng) {
                    titlejam += " - " + totime2;
                    break;
                }
                JSONObject jsono = jampraktek.getJSONObject(index);
                String fromtime = jsono.getString("from_time");
                fromtime = fromtime.substring(0, 5);
                totime2 = jsono.getString("to_time");
                totime2 = totime2.substring(0, 5);
                Boolean isFull = jsono.getBoolean("is_full");
                if (isFull.booleanValue() == false) {
                    EasyMap btnAction = new EasyMap();
                    btnAction.setName(fromtime + " - " + totime2);
                    btnAction.setValue(fromtime);
                    actions.add(btnAction);
                    if (y == 0) {
                        titlejam = fromtime;
                    } else if (y == 2 || index == jampraktekleng) {
                        titlejam += " - " + totime2;
                    }
                    y++;
                }
                index++;
            }
            button.setTitle("Yang Tersedia :");
            button.setSubTitle("");

            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
            sb.append(buttonBuilder.build()).append(SPLIT);
        }

        return sb.toString();
    }

    /**
     * Cek Cuti/ Check Leave Doctor
     *
     * Method untuk Mengecek apakah Status Dokter sedang Cuti/Leave atau tidak
     * base on API Leave
     *
     * @param jobj JSON Object get From API
     * @param tanggal Tanggal Cek Cuti
     * @return String Cuti Atau Tidaknya
     */
    private String CekCuti(JSONObject jobj, String tanggal) {
        String cuti = "";

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
            int datenowFromdate = 0;
            int datenowTodate = 0;
            try {
                //get last date of the month
                Date awalcut = new SimpleDateFormat("yyyy-MM-dd").parse(awalcuti);
                Date akhircut = new SimpleDateFormat("yyyy-MM-dd").parse(akhircuti);
                Date nowdat = new SimpleDateFormat("yyyy-MM-dd").parse(tanggal);

                //cek last date dgn tanggal yg diinputkan
                datenowFromdate = nowdat.compareTo(awalcut);
                datenowTodate = nowdat.compareTo(akhircut);

            } catch (Exception e) {
                System.out.println(e);
            }

            if ((datenowFromdate == 1 && datenowTodate == 1) || (datenowFromdate == -1 && datenowTodate == -1)) {
                cuti = "tidak";
            } else {
                cuti = "iya";
                break;
            }
        }
        return cuti;
    }

    /**
     * Cek Hospital
     *
     * Method untuk Mengecek apakah Hospital/Rumah Sakit yang di Pilih oleh User
     * Termasuk ke Dalam list Hospital yang dapat di Lakukan Online Booking
     *
     * @param id ID Hospital terpilih
     * @param livehos List Hospital yang dapat Melakukan Appointment Online
     * @return Status Hospital
     */
    private String CekHospital(String id, JSONArray livehos) {
        String hospital = "";
        int liveleng = livehos.length();
        for (int i = 0; i < liveleng; i++) {
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
     * Leng (Leng Speciality)
     *
     * Method ini dipakai untuk Menentukan Leng dari total Data Specialist Yang
     * ingin ditampilkan Digunakan ketika Hanya ingin menampilkan 9 Carousel + 1
     * Carousel(Lainnya) Berdasarkan Index
     *
     * @param code Hardcode code by Counter
     * @param data Array dari API Spesialis
     * @return int leng Array
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
     * Carospec(Carousel Specialist)
     *
     * Method ini dipakai untuk membuat Carousel Specialist General Agar bisa di
     * pakai berbagai Method Lainnya
     *
     * @param sb String Builder untuk Return
     * @param leng Jumlah Array
     * @param data Array dari API
     * @return sb String Builder untuk Create Carousel Specialis
     */
    private StringBuilder carospec(StringBuilder sb, int leng, JSONArray data) {
        if (leng == 10) {
            for (int i = 0; i < leng; i++) {
                JSONObject jObj = data.getJSONObject(i);
                String id_spesialis = jObj.getString("speciality_id");
                String nameEn = jObj.getString("speciality_name_en");
                String name = jObj.getString("speciality_name");
                String imageUrl = "";
                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                    imageUrl = jObj.optString("image_url");
                } else {
                    imageUrl = appProp.getSiloamLogo();
                }
                // Buat Button 
                String value = id_spesialis;
                ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, nameEn, "", name, value);
                String btnBuilder = buatBtnBuilder.build().toString();
                sb.append(btnBuilder).append(SPLIT);
            }
        } else {
            int minus = 9;
            if (leng < 9) {
                minus = leng;
            }
            for (int i = leng - minus; i < leng; i++) {
                JSONObject jObj = data.getJSONObject(i);
                String id_spesialis = jObj.getString("speciality_id");
                String nameEn = jObj.getString("speciality_name_en");
                String name = jObj.getString("speciality_name");
                String imageUrl = "";
                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                    imageUrl = jObj.optString("image_url");
                } else {
                    imageUrl = appProp.getSiloamLogo();
                }
                //Buat Button 
                String value = id_spesialis;
                ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, nameEn, "", name, value);
                String btnBuilder = buatBtnBuilder.build().toString();
                sb.append(btnBuilder).append(SPLIT);
            }
            int minus2 = 9;
            if (leng == minus2 || leng > minus2) {
                String imageUrl = appProp.getSiloamLogo();
                String value = "lainnya";
                ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, "lainnya", "", "Pilih", value);
                String btnBuilder = buatBtnBuilder.build().toString();
                sb.append(btnBuilder).append(SPLIT);
            }
        }
        return sb;
    }

    /**
     * Validate Phone
     *
     * Method untuk Memvalidasikan Nomor Telephone/Handphone yang diinput Oleh
     * User
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
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
     * Validate Date
     *
     * Method untuk Memvalidasikan Date Birth/Tanggal Lahir yang diinputkan User
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
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
     * Menggunakan BPJS di Siloam
     *
     * Method untuk mengecek Apakah Rumah Sakit yang diketikan User Bisa
     * menggunakan BPJS
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
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
     * Button Builder General
     *
     * Method Umum untuk membuat Button yang ada dalam Carousel. Untuk bisa di
     * gunakan Banyak Method
     *
     * @param imageUrl URL / Path dari Image
     * @param title Title Button
     * @param subtitle Subtitle Button
     * @param name Label Button Action
     * @param value Value Button Action
     * @return Button Builder
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
     * Konfirmasi Tipe
     *
     * Method ini dipakai untuk Mengecek Flow Booking Dokter apa yang di Pilih
     * User Terdapat Flow Booking by Area, Spesialis, Nama. Method ini pun di
     * gunakan untuk Meng-Set Entitas Beberapa Entitas
     *
     * Jika user typing (contoh :" Jadwal Dokter Billy") : Jadwal Dokter sebagai
     * Interntion dan Billy sebagai Entitas tipe_pencarian. lalu akan di Cek
     * apakah Billy merupakan Nama Dokter/Nama Hospital/Nama Specialist. Jika
     * Billy merupakan Nama, maka Entitas Konfirmasi Tipe akan di set valuenya
     * dengan "Nama". Maka akan masuk ke Flow Nama.
     *
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
     */
    @Override
    public ExtensionResult SetKonfirmasiTipe(ExtensionRequest extensionRequest) {
        log.debug("SetKonfirmasiTipe() extension request: {}", new Gson().toJson(extensionRequest, ExtensionRequest.class));
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
        tipe = tipe.toLowerCase().replace("dokter ", "").replace("prof ", "").replace("siloam ", "");

        // Area
        // Ini akan berlaku ketika String tipe(tipe_pencarian) bervalue String "Area"
        if (tipe.equalsIgnoreCase("area") || tipe.equalsIgnoreCase("booknow")) {
            String apiArea = appProp.getApiBaseUrl() + appProp.getApiArea();
            konfirmtipe = "area";
            JSONArray results = GeneralExecuteAPI(apiArea).getJSONArray("data");
            int leng = results.length();
            for (int i = 0; i < leng; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String areaId = jObj.getString("area_id");
                String areaName = jObj.getString("name");
                String imageUrl = "";
//                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
//                    imageUrl = jObj.optString("image_url");
//                } else {
//                    imageUrl = appProperties.getSiloamLogo();
//                }
                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                    imageUrl = jObj.optString("image_url");
                    button.setPictureLink(imageUrl);
                    button.setPicturePath(imageUrl);
                } else {
                    imageUrl = appProp.getSiloamLogo();
                }

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
                sb.append(btnBuilder).append(SPLIT);
            }
            String dialog = "Silahkan cari rumah sakit Siloam berdasarkan Area yang ingin Anda tuju.";
            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                    .add("Menu Utama", "menu utama").build();
            output.put(OUTPUT, dialog + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
            clearEntities.put("konfirmtipe", konfirmtipe);

            // Spesialis
            // Ini akan berlaku ketika String tipe(tipe_pencarian) bervalue String "Spesialis"
        } else if (tipe.equalsIgnoreCase("spesialis")) {
            int code = 0;
            String apiSpecialis = appProp.getApiBaseUrl() + appProp.getApiSpecialistTop();
            JSONArray resultsSpec = GeneralExecuteAPI(apiSpecialis).getJSONArray("data");
            int leng;
            leng = leng(code, resultsSpec);
            sb = carospec(sb, leng, resultsSpec);

            String dialog1 = "Berikut adalah nama-nama spesialisasi yang ada di RS Siloam. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.  ";
            String dialog2 = "Ketik bidang spesialisasi apabila tidak menemukan opsi pilihan Anda\n Contoh: \"Penyakit Dalam\" atau \"Gigi\" \n\n"
                    + "Atau klik salah satu opsi di bawah ini untuk melihat daftar spesialisasi di RS Siloam.";

            // Button Menu
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle("");
            button.setSubTitle(" ");
            List<EasyMap> actions = new ArrayList<>();
            EasyMap bookAction = new EasyMap();
            bookAction.setName("List Spesialis");
            bookAction.setValue(appProp.getSiloamImageListSpesialis());
            actions.add(bookAction);
            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
            String imagebuilder = buttonBuilder.build().toString();

            ButtonTemplate button2 = new ButtonTemplate();
            button2.setTitle("");
            button2.setSubTitle(" ");
            List<EasyMap> actions2 = new ArrayList<>();
            EasyMap bookAction2 = new EasyMap();
            bookAction2.setName("Menu Utama");
            bookAction2.setValue("menu utama");
            actions2.add(bookAction2);
            button2.setButtonValues(actions2);
            ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
            String imagebuilder2 = buttonBuilder2.build().toString();
            // ----------//
            output.put(OUTPUT, dialog1 + SPLIT + sb.toString() + SPLIT + dialog2 + SPLIT + imagebuilder + SPLIT + imagebuilder2);
            clearEntities.put("konfirmtipe", konfirmtipe);

            // Nama
            // Ini akan berlaku ketika String tipe(tipe_pencarian) bervalue String "Spesialis"
        } else if (tipe.equalsIgnoreCase("nama")) {

            sb.append("Silahkan ketik nama dokter yang ingin Anda cari.\n\n sebagai contoh: \"Budi\" atau \"Budi Chandra\"");
            output.put(OUTPUT, sb.toString());
            clearEntities.put("konfirmtipe", konfirmtipe);

            // Cek Free Typing
            // Ini akan berlaku ketika String tipe(tipe_pencarian) bervalue tidak equals dengan String "Area/Nama/Spesialis"
        } else {
            int kode = 0;
            if (tipe.equalsIgnoreCase("bali") || tipe.equalsIgnoreCase("nusa") || tipe.equalsIgnoreCase("nusa tenggara")) {
                tipe = "bali & nusa tenggara";
            }

            String apiSpesilisName = appProp.getApiBaseUrl() + appProp.getApiSpecialistbyname() + tipe;
            JSONObject jobj2 = GeneralExecuteAPI(apiSpesilisName);

            String apiHospitalName = appProp.getApiBaseUrl() + appProp.getApiHospitalName() + tipe;
            JSONObject jobj1 = GeneralExecuteAPI(apiHospitalName);

            String apiDokterName = appProp.getApiBaseUrl() + appProp.getApiDoctorbyname() + tipe;
            JSONObject jobj3 = GeneralExecuteAPI(apiDokterName);

            if (jobj3.getString("status").equalsIgnoreCase("OK") && !jobj3.getJSONArray("data").isNull(0)) {
                konfirmtipe = "nama";
                kode = 3;
            } else if (jobj2.getString("status").equalsIgnoreCase("OK") && !jobj2.getJSONArray("data").isNull(0)) {
                konfirmtipe = "spesialis";
                kode = 2;
            } else if (jobj1.getString("status").equalsIgnoreCase("OK") && !jobj1.getJSONArray("data").isNull(0)) {
                konfirmtipe = "area";
                kode = 1;
            }

            String imageUrl = "";

            switch (kode) {
                case 1:
                    JSONArray resultsHospital = jobj1.getJSONArray("data");
                    int leng = resultsHospital.length();
                    for (int i = 0; i < leng; i++) {
                        JSONObject jObj = resultsHospital.getJSONObject(i);
                        String hospitalId = jObj.getString("hospital_id");
                        String hospitalName = jObj.getString("name");
                        hospitalName = hospitalName.replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                        if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                            imageUrl = jObj.optString("image_url");
                        } else {
                            imageUrl = appProp.getSiloamLogo();
                        }
                        // Buat Button
                        String value = hospitalId;
                        ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, hospitalName, "", hospitalName, value);
                        String btnBuilder = buatBtnBuilder.build().toString();
                        sb.append(btnBuilder).append(SPLIT);
                    }

                    String dialoghospital = "Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi. Bila Anda sudah menemukan RS Siloam yang Anda cari, mohon klik tombol pilihan Anda.";
                    QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                            .add("Menu Utama", "menu utama").build();
                    output.put(OUTPUT, dialoghospital + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
                    clearEntities.put("konfirmtipe", konfirmtipe);
                    clearEntities.put("step_satu", "hospital");

                    break;
                case 2:
                    clearEntities.put("konfirmtipe", konfirmtipe);
                    JSONArray resultsSpesialis = jobj2.getJSONArray("data");
                    int leng2 = resultsSpesialis.length();
                    if (leng2 > 1) {
                        for (int i = 0; i < leng2; i++) {
                            JSONObject jObj = resultsSpesialis.getJSONObject(i);
                            String id_spesialis = jObj.getString("speciality_id");
                            String nameEn = jObj.getString("speciality_name_en");
                            String nameId = jObj.getString("speciality_name");
                            // Buat Button 
                            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                imageUrl = jObj.optString("image_url");
                            } else {
                                imageUrl = appProp.getSiloamLogo();
                            }

                            ButtonTemplate button = new ButtonTemplate();
                            button.setPictureLink(imageUrl);
                            button.setPicturePath(imageUrl);
                            button.setTitle(nameEn);
                            button.setSubTitle("");
                            List<EasyMap> actions = new ArrayList<>();
                            EasyMap bookAction = new EasyMap();
                            bookAction.setName(nameId);
                            bookAction.setValue(id_spesialis);
                            actions.add(bookAction);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                            String btnBuilder = buttonBuilder.build().toString();

                            sb.append(btnBuilder).append(SPLIT);
                        }
                        String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin Anda kunjungi.";
                        output.put(OUTPUT, truecase + SPLIT + sb.toString());

                    } else {
                        JSONObject jObj = resultsSpesialis.getJSONObject(0);
                        String id_spesialis = jObj.getString("speciality_id");
                        String nameId = jObj.getString("speciality_name");
                        clearEntities.put("step_satu", id_spesialis);
                        clearEntities.put("counter", id_spesialis);
                        // Buat Button
                        ButtonTemplate button = new ButtonTemplate();
                        button.setTitle("");
                        button.setSubTitle(" ");
                        List<EasyMap> actions = new ArrayList<>();
                        EasyMap bookAction = new EasyMap();
                        bookAction.setName("List Rumah Sakit");
                        bookAction.setValue(appProp.getSiloamImageListHospitals());
                        actions.add(bookAction);
                        button.setButtonValues(actions);
                        ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                        String buttonbuilder1 = buttonBuilder.build();

                        ButtonTemplate button2 = new ButtonTemplate();
                        button2.setTitle("");
                        button2.setSubTitle(" ");
                        List<EasyMap> actions2 = new ArrayList<>();
                        EasyMap bookAction2 = new EasyMap();
                        bookAction2.setName("Pilih Rumah Sakit");
                        bookAction2.setValue("areaspec");
                        actions2.add(bookAction2);
                        button2.setButtonValues(actions2);
                        ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
                        String buttonbuilder2 = buttonBuilder2.build();

                        String buttonAll = buttonbuilder1 + SPLIT + buttonbuilder2;

                        String dialog = "Anda telah memilih " + nameId + ". Silahkan ketik nama RS Siloam yang ingin dikunjungi untuk menampilkan daftar dokter " + nameId
                                + " di RS tersebut. \nContoh: \"Siloam Lippo Village\" atau \"Lippo Village\" \n\nKlik salah satu opsi di bawah ini untuk melihat daftar RS Siloam";
//                        QuickReplyBuilder quickReplyBuilder2 = new QuickReplyBuilder.Builder("Atau klik di bawah ini untuk melihat RS Siloam yang terdekat dan pastikan pengaturan lokasi Anda sudah Aktif")
//                                .add("Rumah Sakit Terdekat", "location").build();
//                        output.put(OUTPUT, dialog + SPLIT + buttonAll + SPLIT + quickReplyBuilder2.string());
                        output.put(OUTPUT, dialog + SPLIT + buttonAll);
                    }

                    break;
                case 3:
                    clearEntities.put("konfirmtipe", konfirmtipe);
                    clearEntities.put("step_satu", tipe);
                    clearEntities.put("step_dua", "bbbbcccc-ddee-ffgg-hhii-jjkkllmmnnoo");
                    clearEntities.put("step_tiga", "aaaaaaaa-bbcc-ddee-ffgg-hhiijjkkllmm");

                    JSONArray resultsNama = jobj3.getJSONArray("data");
                    int leng3 = resultsNama.length();
                    if (leng3 >= 15) {
                        leng3 = 15;
                    }
                    String tampungiddokter = "";
                    for (int i = 0; i < leng3; i++) {
                        JSONObject jObj = resultsNama.getJSONObject(i);
                        String doctorId = jObj.getString("doctor_id");
                        if (tampungiddokter.equalsIgnoreCase(doctorId)) {
                            break;
                        } else {
                            tampungiddokter = doctorId;
                        }
                        String apiGetDokter = appProp.getApiBaseUrl() + appProp.getApiDoctorbydoctorid() + doctorId;
                        JSONObject objApiDoctor = GeneralExecuteAPI(apiGetDokter);
                        List<String> listidDokter = new ArrayList<>();
                        JSONArray resultsDoctor = objApiDoctor.getJSONArray("data");
                        int lengDoctor = resultsDoctor.length();
                        for (int j = 0; j < lengDoctor; j++) {
                            JSONObject jObj2 = resultsDoctor.getJSONObject(j);
                            String doctorid = jObj2.getString("doctor_id");
                            String doctorName = jObj2.getString("name");
                            String hospitalId = jObj2.getString("hospital_id");
                            if (listidDokter.contains(hospitalId)) {
                            } else {
                                listidDokter.add(hospitalId);
                                String doctorSpecialist = jObj2.getString("specialization_name");
                                String doctorHospitals = jObj2.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");

                                //Buat Button
                                ButtonTemplate button = new ButtonTemplate();
                                button.setTitle(doctorSpecialist);
                                button.setSubTitle(doctorHospitals);
                                List<EasyMap> actions = new ArrayList<>();
                                EasyMap bookAction = new EasyMap();
                                bookAction.setName(doctorName);
                                bookAction.setValue("dokter id " + doctorid + " di hos " + hospitalId);
                                actions.add(bookAction);
                                button.setButtonValues(actions);
                                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                                String btnBuilder = buttonBuilder.build().toString();
                                sb.append(btnBuilder).append(SPLIT);
                            }

                        }
                    }
                    String dialog = "Berikut adalah beberapa dokter yang sesuai dengan pencarian Anda. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.";
                    sb.append("Tidak menemukan dokter yang Anda cari?\n Silahkan coba lagi dengan mengetik nama dokter, sebagai contoh: \"Budi\" atau \"Budi Chandra\"").append(SPLIT);
                    QuickReplyBuilder quickReplyBuilder2 = new QuickReplyBuilder.Builder("Bila terdapat kesulitan. Anda juga dapat memilih salah satu Opsi di bawah ini:")
                            .add("Hubungi Call Center", "callhospitalcenter").add("Hubungi RS Siloam", "caritelponsiloam").add("Menu Utama", "menu utama").build();
                    sb.append(quickReplyBuilder2.string());
                    output.put(OUTPUT, dialog + SPLIT + sb.toString());

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
                    String dialog1 = "Mohon maaf, Silahkan ketik kembali atau pilih kembali opsi berikut.";
                    output.put(OUTPUT, dialog1 + SPLIT + carouselBuilder.build());
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
     * Set Step Dua
     *
     * Method untuk Menampilkan Output pa`da Entitas Step_Dua
     *
     * String konfirmtipe = getEasyMapValueByName(extensionRequest,
     * "konfirmtipe") : di perlukan untuk get Tipe Flow
     *
     * String counter = getEasyMapValueByName(extensionRequest, "counter") : di
     * perlukan untuk code counter jika Spesialis
     *
     * String stepsatu = getEasyMapValueByName(extensionRequest, "step_satu") :
     * di perlukan untuk mengambil Inputan/Ketikan User
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
     */
    @Override
    public ExtensionResult setStepDua(ExtensionRequest extensionRequest) {
        ExtensionResult extensionResult = new ExtensionResult();
        Map<String, String> output = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String konfirmtipe = getEasyMapValueByName(extensionRequest, "konfirmtipe");
        String counter = getEasyMapValueByName(extensionRequest, "counter");
        String stepsatu = getEasyMapValueByName(extensionRequest, "step_satu");
//        String steptiga = getEasyMapValueByName(extensionRequest, "step_tiga");
        Map<String, String> clearEntities = new HashMap<>();
        String imageUrl = "";
        String areaId = "";
        System.out.println(stepsatu);

        // Tujuan Lower Step, karna setiap inputan user yang masuk BOT akan Kapital pada huruf pertama //
        String lowerstepsatu = stepsatu.toLowerCase().replace("dokter ", "").replace("prof ", "").replace("siloam ", "");
        String statstep_satu = "";

        String apiHospitalName = appProp.getApiBaseUrl() + appProp.getApiHospitalName() + lowerstepsatu;
        JSONObject jobjHospitalName = GeneralExecuteAPI(apiHospitalName);

        String apiDokterName = appProp.getApiBaseUrl() + appProp.getApiDoctorbyname() + lowerstepsatu;
        JSONObject jobj3 = GeneralExecuteAPI(apiDokterName);

        if (jobj3.getString("status").equalsIgnoreCase("OK") && !jobj3.getJSONArray("data").isNull(0)) {
            statstep_satu = "nama";
        } else if (jobjHospitalName.getString("status").equalsIgnoreCase("OK") && !jobjHospitalName.getJSONArray("data").isNull(0)) {
            statstep_satu = "hospital";
        }
        String stat = "";
        String newstepdua = "";
        String hospitalname = "";
        if (statstep_satu.equalsIgnoreCase("hospital")) {
            JSONArray resultsHospitalName = jobjHospitalName.getJSONArray("data");
            JSONObject jObj;
//            if (stepsatu.equalsIgnoreCase("lippo village")) {
//                jObj = resultsHospitalName.getJSONObject(1);
//            } else {
//                jObj = resultsHospitalName.getJSONObject(0);
//            }
            jObj = resultsHospitalName.getJSONObject(0);

            String hospitalId = jObj.getString("hospital_id");
            hospitalname = jObj.getString("name");

            stat = "hospital";
            newstepdua = hospitalId;
        } else {
            String apiHospital = appProp.getApiBaseUrl() + appProp.getApiHospital();
            JSONObject jobObject = GeneralExecuteAPI(apiHospital);
            JSONArray results = jobObject.getJSONArray("data");
            int lenghospital = results.length();
            for (int i = 0; i < lenghospital; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String hospitalId = jObj.getString("hospital_id");
                String hospitalName = jObj.getString("name").toLowerCase();
                if (stepsatu.equalsIgnoreCase(hospitalId) || stepsatu.equalsIgnoreCase(hospitalName)) {
                    hospitalname = hospitalName.replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                    stat = "hospital";
                    newstepdua = hospitalId;
                    break;
                }
            }
        }
        if (lowerstepsatu.equalsIgnoreCase("area") || lowerstepsatu.equalsIgnoreCase("spesialis") || lowerstepsatu.equalsIgnoreCase("nama")) {
            String tipe = lowerstepsatu;
            // Area
            // Ini akan berlaku ketika String step_satu bervalue String "Area"
            if (tipe.equalsIgnoreCase("area")) {
                imageUrl = appProp.getSiloamLogo();
                String apiArea = appProp.getApiBaseUrl() + appProp.getApiArea();
                JSONArray results = GeneralExecuteAPI(apiArea).getJSONArray("data");
                int leng = results.length();
                for (int i = 0; i < leng; i++) {
                    JSONObject jObj = results.getJSONObject(i);
                    areaId = jObj.getString("area_id");
                    String areaName = jObj.getString("name");

                    //Buat Button
                    ButtonTemplate button = new ButtonTemplate();
                    if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                        imageUrl = jObj.optString("image_url");
                        button.setPictureLink(imageUrl);
                        button.setPicturePath(imageUrl);
                    }
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
                    sb.append(btnBuilder).append(SPLIT);
                }
                String dialog = "Silahkan cari rumah sakit Siloam berdasarkan Area yang ingin Anda tuju.";
                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                        .add("Menu Utama", "menu utama").build();
                output.put(OUTPUT, dialog + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
                clearEntities.put("konfirmtipe", tipe);

                // Spesialis
                // Ini akan berlaku ketika String step_satu bervalue String "Spesialis"
            } else if (tipe.equalsIgnoreCase("spesialis")) {
                int code = 0;

                String apiSpecialis = appProp.getApiBaseUrl() + appProp.getApiSpecialistTop();
                JSONArray resultsSpec = GeneralExecuteAPI(apiSpecialis).getJSONArray("data");
                int leng;
                leng = leng(code, resultsSpec);
                sb = carospec(sb, leng, resultsSpec);
//                String dialog1 = "Silahkan pilih atau ketik nama Spesialis yang ingin Anda tuju.";
                String dialog1 = "Berikut adalah nama-nama spesialisasi yang ada di RS Siloam. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.  ";
                String dialog2 = "Ketik bidang spesialisasi apabila tidak menemukan opsi pilihan Anda\n Contoh: \"Penyakit Dalam\" atau \"Gigi\" \n\n"
                        + "Atau klik salah satu opsi di bawah ini untuk melihat daftar spesialisasi di RS Siloam.";

                // Button Menu
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle("");
                button.setSubTitle(" ");
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName("List Spesialis");
                bookAction.setValue(appProp.getSiloamImageListSpesialis());
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                String imagebuilder = buttonBuilder.build().toString();

                ButtonTemplate button2 = new ButtonTemplate();
                button2.setTitle("");
                button2.setSubTitle(" ");
                List<EasyMap> actions2 = new ArrayList<>();
                EasyMap bookAction2 = new EasyMap();
                bookAction2.setName("Menu Utama");
                bookAction2.setValue("menu utama");
                actions2.add(bookAction2);
                button2.setButtonValues(actions2);
                ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
                String imagebuilder2 = buttonBuilder2.build().toString();
                // ----------//
                output.put(OUTPUT, dialog1 + SPLIT + sb.toString() + SPLIT + dialog2 + SPLIT + imagebuilder + SPLIT + imagebuilder2);
                clearEntities.put("konfirmtipe", tipe);

                // Nama
                // Ini akan berlaku ketika String step_satu bervalue String "Spesialis"
            } else if (tipe.equalsIgnoreCase("nama")) {
                sb.append("Silahkan ketik nama Dokter yang ingin Anda kunjungi. Untuk {bot_name} bantu carikan.");
                output.put(OUTPUT, sb.toString());
                clearEntities.put("konfirmtipe", tipe);
            }
            clearEntities.put("step_satu", "");
            clearEntities.put("step_dua", "");
            clearEntities.put("tipe_pencarian", tipe);

        } else if (stat.equalsIgnoreCase("hospital")) {
            int code = 0;
            String apiSpec = appProp.getApiBaseUrl() + appProp.getApiSpecialistTop();
            JSONArray resultsSpec = GeneralExecuteAPI(apiSpec).getJSONArray("data");
            int leng;
            leng = leng(code, resultsSpec);
            sb = carospec(sb, leng, resultsSpec);

            String dialog1 = "Anda telah memilih " + hospitalname + ". Berikut adalah nama-nama spesialisasi yang ada di RS Siloam. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.  ";
            String dialog2 = "Ketik bidang spesialisasi apabila tidak menemukan opsi pilihan Anda\n Contoh: \"Penyakit Dalam\" atau \"Gigi\" \n\n"
                    + "Atau klik salah satu opsi di bawah ini untuk melihat daftar spesialisasi di RS Siloam.";

            // Button Menu
            ButtonTemplate button = new ButtonTemplate();
            button.setTitle("");
            button.setSubTitle(" ");
            List<EasyMap> actions = new ArrayList<>();
            EasyMap bookAction = new EasyMap();
            bookAction.setName("List Spesialis");
            bookAction.setValue(appProp.getSiloamImageListSpesialis());
            actions.add(bookAction);
            button.setButtonValues(actions);
            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
            String imagebuilder = buttonBuilder.build().toString();

            ButtonTemplate button2 = new ButtonTemplate();
            button2.setTitle("");
            button2.setSubTitle(" ");
            List<EasyMap> actions2 = new ArrayList<>();
            EasyMap bookAction2 = new EasyMap();
            bookAction2.setName("Menu Utama");
            bookAction2.setValue("menu utama");
            actions2.add(bookAction2);
            button2.setButtonValues(actions2);
            ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
            String imagebuilder2 = buttonBuilder2.build().toString();
            // ----------//
            output.put(OUTPUT, dialog1 + SPLIT + sb.toString() + SPLIT + dialog2 + SPLIT + imagebuilder + SPLIT + imagebuilder2);

            clearEntities.put("step_dua", newstepdua);
            extensionResult.setEntities(clearEntities);

        } else {
            switch (konfirmtipe.toLowerCase()) {
                // Get Hospital by Area
                case "area":
                    String konfirmArea = "";
                    String apiArea = appProp.getApiBaseUrl() + appProp.getApiArea();
                    JSONArray resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
                    int lengArea = resultsArea.length();
                    if (lowerstepsatu.equals("bali") || lowerstepsatu.equals("nusa") || lowerstepsatu.equals("nusa tenggara")) {
                        lowerstepsatu = "bali & nusa tenggara";
                    }
                    for (int i = 0; i < lengArea; i++) {
                        JSONObject jObjArea = resultsArea.getJSONObject(i);
                        String idArea = jObjArea.getString("area_id");
                        String nameArea = jObjArea.getString("name");
                        String lowername = nameArea.toLowerCase();
                        if (lowerstepsatu.equals(idArea) || lowerstepsatu.equals(lowername)) {
                            areaId = idArea;
                            konfirmArea = "benar";
                            break;
                        }
                    }
                    if (konfirmArea.equalsIgnoreCase("benar")) {
                        String apiHospital = appProp.getApiBaseUrl() + appProp.getApiHospitalByArea() + areaId;
                        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
                        int leng = results.length();
                        for (int i = 0; i < leng; i++) {
                            JSONObject jObj = results.getJSONObject(i);
                            String hospitalId = jObj.getString("hospital_id");
                            String hospitalName = jObj.getString("name");
                            hospitalName = hospitalName.replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                imageUrl = jObj.optString("image_url");
                            } else {
                                imageUrl = appProp.getSiloamLogo();
                            }
                            //Buat Button
                            String value = hospitalId;
                            ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, hospitalName, "", hospitalName, value);
                            String btnBuilder = buatBtnBuilder.build().toString();
                            sb.append(btnBuilder).append(SPLIT);
                        }
                        String dialog = "Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi. Bila Anda sudah menemukan RS Siloam yang Anda cari, mohon klik tombol pilihan Anda.";
                        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                                .add("Menu Utama", "menu utama").build();
                        output.put(OUTPUT, dialog + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
                        clearEntities.put("step_satu", areaId);
                    } else {
                        clearEntities.put("step_satu", "");
                        for (int i = 0; i < lengArea; i++) {
                            JSONObject jObjArea = resultsArea.getJSONObject(i);
                            String IdArea = jObjArea.getString("area_id");
                            String NameArea = jObjArea.getString("name");
                            imageUrl = appProp.getSiloamLogo();
                            if (!jObjArea.optString("image_url").equalsIgnoreCase("")) {
                                imageUrl = jObjArea.optString("image_url");

                            }
                            //Buat Button
                            ButtonTemplate button = new ButtonTemplate();
                            button.setPictureLink(imageUrl);
                            button.setPicturePath(imageUrl);
                            button.setTitle(NameArea);
                            button.setSubTitle("");
                            List<EasyMap> actions = new ArrayList<>();
                            EasyMap bookAction = new EasyMap();
                            bookAction.setName(NameArea);
                            bookAction.setValue(IdArea);
                            actions.add(bookAction);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                            String btnBuilder = buttonBuilder.build().toString();
                            sb.append(btnBuilder).append(SPLIT);

                        }

                        String dialog = "Maaf. {bot_name} tidak dapat menemukan Rumah Sakit Siloam berdasarkan Area yang Anda pilih atau ketik. "
                                + "Silahkan pilih atau ketik kembali Area yang ingin Anda tuju.";
                        output.put(OUTPUT, dialog + SPLIT + sb.toString());
                    }

                    break;
                // Get Location by Spesialis
                case "spesialis":
                    lowerstepsatu = lowerstepsatu.replace("spesialis ", "");
                    String apiSpecialisName = appProp.getApiBaseUrl() + appProp.getApiSpecialistbyname() + lowerstepsatu;
                    JSONObject jsonobjSpecName = GeneralExecuteAPI(apiSpecialisName);
                    if (jsonobjSpecName.getString("status").equalsIgnoreCase("OK") && !jsonobjSpecName.getJSONArray("data").isNull(0)) {
                        JSONArray resultsSpecName = jsonobjSpecName.getJSONArray("data");
                        int leng2 = resultsSpecName.length();
                        if (leng2 > 1) {
                            for (int i = 0; i < leng2; i++) {
                                JSONObject jObj = resultsSpecName.getJSONObject(i);
                                String id_spesialis = jObj.getString("speciality_id");
                                String nameEn = jObj.getString("speciality_name_en");
                                String nameId = jObj.getString("speciality_name");

                                // Buat Button 
                                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                    imageUrl = jObj.optString("image_url");
                                } else {
                                    imageUrl = appProp.getSiloamLogo();
                                }

                                ButtonTemplate button = new ButtonTemplate();
                                button.setPictureLink(imageUrl);
                                button.setPicturePath(imageUrl);
                                button.setTitle(nameEn);
                                button.setSubTitle("");
                                List<EasyMap> actions = new ArrayList<>();
                                EasyMap bookAction = new EasyMap();
                                bookAction.setName(nameId);
                                bookAction.setValue(id_spesialis);
                                actions.add(bookAction);
                                button.setButtonValues(actions);
                                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                                String btnBuilder = buttonBuilder.build().toString();

//                                String value = "spesialis id " + id_spesialis;
//                                ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, nameId, nameEn, nameId, value);
//                                String btnBuilder = buatBtnBuilder.build().toString();
                                sb.append(btnBuilder).append(SPLIT);
                            }
                            String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin Anda kunjungi.";
                            output.put(OUTPUT, truecase + SPLIT + sb.toString());
                            clearEntities.put("step_satu", "");

                        } else {
                            JSONObject jObj = resultsSpecName.getJSONObject(0);
                            String id_spesialis = jObj.getString("speciality_id");
                            String nameId = jObj.getString("speciality_name");
                            clearEntities.put("step_satu", id_spesialis);
                            clearEntities.put("counter", id_spesialis);
                            // Buat Button
                            ButtonTemplate button = new ButtonTemplate();
                            button.setTitle("");
                            button.setSubTitle(" ");
                            List<EasyMap> actions = new ArrayList<>();
                            EasyMap bookAction = new EasyMap();
                            bookAction.setName("List Rumah Sakit");
                            bookAction.setValue(appProp.getSiloamImageListHospitals());
                            actions.add(bookAction);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                            String buttonbuilder1 = buttonBuilder.build();

                            ButtonTemplate button2 = new ButtonTemplate();
                            button2.setTitle("");
                            button2.setSubTitle(" ");
                            List<EasyMap> actions2 = new ArrayList<>();
                            EasyMap bookAction2 = new EasyMap();
                            bookAction2.setName("Pilih Rumah Sakit");
                            bookAction2.setValue("areaspec");
                            actions2.add(bookAction2);
                            button2.setButtonValues(actions2);
                            ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
                            String buttonbuilder2 = buttonBuilder2.build();

                            String buttonAll = buttonbuilder1 + SPLIT + buttonbuilder2;

                            String dialog = "Anda telah memilih " + nameId + ". Silahkan ketik nama RS Siloam yang ingin dikunjungi untuk menampilkan daftar dokter " + nameId
                                    + " di RS tersebut. \nContoh: \"Siloam Lippo Village\" atau \"Lippo Village\" \n\nKlik salah satu opsi di bawah ini untuk melihat daftar RS Siloam";
//                            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Atau klik di bawah ini untuk melihat RS Siloam yang terdekat dan pastikan pengaturan lokasi Anda sudah Aktif")
//                                    .add("Rumah Sakit Terdekat", "location").build();
//                            output.put(OUTPUT, dialog + SPLIT + buttonAll + SPLIT + quickReplyBuilder.string());
                            output.put(OUTPUT, dialog + SPLIT + buttonAll);
                        }
                    } else {
                        String spesialis1 = "";
                        String nameId = "";
                        String apiSpesilisId = appProp.getApiBaseUrl() + appProp.getApiSpecialist();
                        JSONObject jobj = GeneralExecuteAPI(apiSpesilisId);
                        JSONArray dataSpec = jobj.getJSONArray("data");
                        int lengspec = dataSpec.length();
                        for (int i = 0; i < lengspec; i++) {
                            JSONObject jObjSpec = dataSpec.getJSONObject(i);
                            String idSpec = jObjSpec.getString("speciality_id");
                            nameId = jObjSpec.getString("speciality_name");
                            if (stepsatu.equalsIgnoreCase(idSpec)) {
                                spesialis1 = "id";
                                break;
                            }
                        }

                        if (stepsatu.equalsIgnoreCase("lainnya")) {
                            int code = 0;
                            if (counter.equalsIgnoreCase("0")) {
                                code = Integer.parseInt(counter);
                            }
                            code++;
                            clearEntities.put("counter", code + "");
                            clearEntities.put("step_satu", "");
                            //------------------------------------------------------------------------//
                            String apiSpecialis = appProp.getApiBaseUrl() + appProp.getApiSpecialist();
                            JSONArray resultsSpec = GeneralExecuteAPI(apiSpecialis).getJSONArray("data");
                            int leng;
                            leng = leng(code, resultsSpec);
                            sb = carospec(sb, leng, resultsSpec);

                            String dialog = "Silahkan pilih spesialisasi yang ingin dituju.";
                            output.put(OUTPUT, dialog + SPLIT + sb.toString());

                        } else if (spesialis1.equalsIgnoreCase("id")) {

                            // Buat Button
                            ButtonTemplate button = new ButtonTemplate();
                            button.setTitle("");
                            button.setSubTitle(" ");
                            List<EasyMap> actions = new ArrayList<>();
                            EasyMap bookAction = new EasyMap();
                            bookAction.setName("List Rumah Sakit");
                            bookAction.setValue(appProp.getSiloamImageListHospitals());
                            actions.add(bookAction);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                            String buttonbuilder1 = buttonBuilder.build();

                            ButtonTemplate button2 = new ButtonTemplate();
                            button2.setTitle("");
                            button2.setSubTitle(" ");
                            List<EasyMap> actions2 = new ArrayList<>();
                            EasyMap bookAction2 = new EasyMap();
                            bookAction2.setName("Pilih Rumah Sakit");
                            bookAction2.setValue("areaspec");
                            actions2.add(bookAction2);
                            button2.setButtonValues(actions2);
                            ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
                            String buttonbuilder2 = buttonBuilder2.build();

                            String buttonAll = buttonbuilder1 + SPLIT + buttonbuilder2;

                            String dialog = "Anda telah memilih " + nameId + ". Silahkan ketik nama RS Siloam yang ingin dikunjungi untuk menampilkan daftar dokter " + nameId
                                    + " di RS tersebut. \nContoh: \"Siloam Lippo Village\" atau \"Lippo Village\" \n\nKlik salah satu opsi di bawah ini untuk melihat daftar RS Siloam";
//                            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Atau klik di bawah ini untuk melihat RS Siloam yang terdekat dan pastikan pengaturan lokasi Anda sudah Aktif")
//                                    .add("Rumah Sakit Terdekat", "location").build();
//                            output.put(OUTPUT, dialog + SPLIT + buttonAll + SPLIT + quickReplyBuilder.string());
                            output.put(OUTPUT, dialog + SPLIT + buttonAll);
                            clearEntities.put("step_satu", stepsatu);
                            clearEntities.put("counter", stepsatu);
                        } else {
                            String apiSpecialis = appProp.getApiBaseUrl() + appProp.getApiSpecialistbyname() + lowerstepsatu;
                            JSONObject jsonobjSpec = GeneralExecuteAPI(apiSpecialis);
                            Double latitude = extensionRequest.getIntent().getTicket().getLatitude();
                            Double longitude = extensionRequest.getIntent().getTicket().getLongitude();

//                            Double latitude = null;
//                            Double longitude = null;
                            System.out.println(latitude + "stepdua");
                            System.out.println(longitude);
                            try {
                                OkHttpUtil okHttpUtil = new OkHttpUtil();
                                okHttpUtil.init(true);

                                if (jsonobjSpec.getString("status").equalsIgnoreCase("OK") && !jsonobjSpec.getJSONArray("data").isNull(0)) {
                                    int leng;
                                    JSONArray resultsSpec = jsonobjSpec.getJSONArray("data");
                                    leng = resultsSpec.length();
                                    sb = carospec(sb, leng, resultsSpec);
                                    clearEntities.put("step_satu", "");

                                    String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin Anda kunjungi.";
                                    output.put(OUTPUT, truecase + SPLIT + sb.toString());
                                } else if (latitude != null) {

                                    String apiHospital = appProp.getApiBaseUrl() + appProp.getApiHospital();
                                    JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
                                    int leng = results.length();
                                    List<List<String>> data = new ArrayList<>();
                                    double hasil;
                                    for (int i = 0; i < leng; i++) {
                                        JSONObject jObj = results.getJSONObject(i);
                                        String hospitalid = jObj.getString("hospital_id");
                                        String hospitalnameLoc = jObj.getString("name");
                                        Double longitud = jObj.getDouble("longitude");
                                        Double latitud = jObj.getDouble("latitude");
                                        String phonenumber = jObj.optString("phoneNumber");
                                        hospitalnameLoc = hospitalnameLoc.replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                                        if (phonenumber.equals("")) {
                                            phonenumber = "+62211500181";
                                        }
                                        if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                            imageUrl = jObj.optString("image_url");
                                        } else {
                                            imageUrl = appProp.getSiloamLogo();
                                        }
                                        if (latitud != 0 || longitud != 0) {
                                            hasil = distanceInKilometers(latitude, longitude, latitud, longitud);
                                            List<String> jarak = new ArrayList<>();
                                            if (hasil < 30) {
                                                jarak.add(hasil + "");
                                                jarak.add(hospitalid);
                                                jarak.add(hospitalnameLoc);
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
                                        sb.append(btnBuilder).append(SPLIT);

                                    }
                                    if (sb.toString().isEmpty()) {
                                        apiArea = appProp.getApiBaseUrl() + appProp.getApiArea();
                                        resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
                                        lengArea = resultsArea.length();
                                        for (int i = 0; i < lengArea; i++) {
                                            JSONObject jObj = resultsArea.getJSONObject(i);
                                            String area_Id = jObj.getString("area_id");
                                            String area_Name = jObj.getString("name");

                                            //Buat Button
                                            ButtonTemplate button = new ButtonTemplate();
                                            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                                imageUrl = jObj.optString("image_url");
                                                button.setPictureLink(imageUrl);
                                                button.setPicturePath(imageUrl);
                                            } else {
                                                imageUrl = appProp.getSiloamLogo();
                                            }
                                            button.setTitle(area_Name);
                                            button.setSubTitle("");
                                            List<EasyMap> actions = new ArrayList<>();
                                            EasyMap bookAction = new EasyMap();
                                            bookAction.setName(area_Name);
                                            bookAction.setValue(area_Id);
                                            actions.add(bookAction);
                                            button.setButtonValues(actions);
                                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                                            String btnBuilder = buttonBuilder.build().toString();
                                            sb.append(btnBuilder).append(SPLIT);
                                        }

                                        String dialog1 = "Mohon maaf Siloam Hospitals belum tersedia disekitar lokasi Anda. Silahkan pilih dari opsi berikut:";
                                        output.put(OUTPUT, dialog1 + SPLIT + sb.toString());
                                        clearEntities.put("konfirmtipe", "area");
                                        clearEntities.put("step_satu", "");
                                        clearEntities.put("step_dua", "");
//                                        extensionResult.setEntities(clearEntities);

                                    } else {
                                        String dialog1 = "Berikut adalah daftar RS Siloam yang terdekat dengan Anda. Bila Anda telah menemukan RS yang Anda cari, silahkan klik untuk membuat janji temu.";
//                                        clearEntities.put("step_satu", stepsatu);
                                        clearEntities.put("step_satu", counter);
                                        clearEntities.put("step_dua", "dumylocation");
                                        output.put(OUTPUT, dialog1 + SPLIT + sb.toString());
                                    }
                                } else {
                                    int code = 0;
                                    if (counter.equalsIgnoreCase("0")) {
                                        code = Integer.parseInt(counter);
                                    }
//                                    String apiSpecialisTop = appProp.getApiBaseUrl() + appProp.getApiSpecialistTop();
//                                    JSONObject jsonobjSpecTop = GeneralExecuteAPI(apiSpecialisTop);
//                                    JSONArray resultsSpecTop = jsonobjSpecTop.getJSONArray("data");
//                                    int leng;
//                                    leng = leng(code, resultsSpecTop);
//                                    sb = carospec(sb, leng, resultsSpecTop);
                                    ButtonTemplate image = new ButtonTemplate();
                                    image.setTitle("");
                                    image.setSubTitle("Klik dibawah ini untuk melihat List Bidang Spesialis");
                                    List<EasyMap> actions = new ArrayList<>();
                                    EasyMap bookAction = new EasyMap();
                                    bookAction.setName("List Spesialis");
                                    bookAction.setValue(appProp.getSiloamImageListSpesialis());
                                    actions.add(bookAction);
                                    image.setButtonValues(actions);
                                    ButtonBuilder buttonBuilder = new ButtonBuilder(image);
                                    String imagebuilder = buttonBuilder.build().toString();
                                    String falsecase1 = "Mohon maaf {bot_name} tidak menemukan Spesialis yang Anda cari";
                                    String falsecase2 = "Ketik kembali bidang spesialisasi yang ingin Anda cari.\n Contoh: \"Penyakit Dalam\" atau \"dermatologi\"";
                                    QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                                            .add("Menu Utama", "menu utama").build();
                                    output.put(OUTPUT, falsecase1 + SPLIT + imagebuilder + SPLIT + falsecase2 + SPLIT + quickReplyBuilder.string());
                                    clearEntities.put("step_satu", "");
                                    System.out.println(output);
                                }

                            } catch (Exception e) {
                            }
                        }
                    }
                    break;

                // Get Doctor By Nama
                case "nama":
//                    stepsatu = stepsatu.toLowerCase().replace("dokter ", "").replace("prof ", "");

                    if (jobj3.getString("status").equalsIgnoreCase("OK") && !jobj3.getJSONArray("data").isNull(0)) {
                        JSONArray resultsNama = jobj3.getJSONArray("data");
                        int leng3 = resultsNama.length();
                        if (leng3 >= 15) {
                            leng3 = 15;
                        }
                        String tampungiddokter = "";
                        for (int i = 0; i < leng3; i++) {
                            JSONObject jObj = resultsNama.getJSONObject(i);
                            String doctorId = jObj.getString("doctor_id");
                            if (tampungiddokter.equalsIgnoreCase(doctorId)) {
                                break;
                            } else {
                                tampungiddokter = doctorId;
                            }
                            String apiGetDokter = appProp.getApiBaseUrl() + appProp.getApiDoctorbydoctorid() + doctorId;
                            JSONArray resultsDoctor = GeneralExecuteAPI(apiGetDokter).getJSONArray("data");
                            int lengDoctor = resultsDoctor.length();
                            for (int j = 0; j < lengDoctor; j++) {
                                JSONObject jObj2 = resultsDoctor.getJSONObject(j);
                                String doctorName = jObj2.getString("name");
                                String hospitalId = jObj2.getString("hospital_id");
                                String doctorSpecialist = jObj2.getString("specialization_name");
                                String doctorHospitals = jObj2.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");

                                //Buat Button
                                ButtonTemplate button = new ButtonTemplate();
                                button.setTitle(doctorSpecialist);
                                button.setSubTitle(doctorHospitals);
                                List<EasyMap> actions = new ArrayList<>();

                                EasyMap LihatJadwal = new EasyMap();
                                LihatJadwal.setName(doctorName);
                                LihatJadwal.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                                actions.add(LihatJadwal);
                                button.setButtonValues(actions);
                                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                                String btnBuilder = buttonBuilder.build().toString();
                                sb.append(btnBuilder).append(SPLIT);
                            }
                        }

                        String dialog = "Berikut adalah beberapa dokter yang sesuai dengan pencarian Anda. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.";
                        sb.append("Tidak menemukan dokter yang Anda cari?\n Silahkan coba lagi dengan mengetik nama dokter, sebagai contoh: \"Budi\" atau \"Budi Chandra\"").append(SPLIT);
                        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Bila terdapat kesulitan. Anda juga dapat memilih salah satu Opsi di bawah ini:")
                                .add("Hubungi Call Center", "callhospitalcenter").add("Hubungi RS Siloam", "caritelponsiloam").add("Menu Utama", "menu utama").build();
                        sb.append(quickReplyBuilder.string());
                        output.put(OUTPUT, dialog + SPLIT + sb.toString());

                        clearEntities.put("step_dua", "2");
                        clearEntities.put("step_tiga", "3");
                    } else {
                        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Bila terdapat kesulitan. Anda juga dapat memilih salah satu Opsi di bawah ini:")
                                .add("Hubungi Call Center", "callhospitalcenter").add("Hubungi RS Siloam", "caritelponsiloam").add("Menu Utama", "menu utama").build();
                        sb.append("Mohon Maaf, {bot_name} tidak menemukan nama dokter berdasarkan permintaan Anda.").append(SPLIT);
                        sb.append("Silahkan ketik kembali [Nama Dokter] yang ingin Anda kunjungi.\n\n contoh: \"Budi\" atau \"Budi Chandra\"").append(SPLIT);
                        sb.append(quickReplyBuilder.string());

                        output.put(OUTPUT, sb.toString());
                        clearEntities.put("step_satu", "");
                    }

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
     * Set Step Tiga
     *
     * Method untuk Menampikan Output pada Entitas Step Tiga
     *
     * String konfirmtipe = getEasyMapValueByName(extensionRequest,
     * "konfirmtipe") : di perlukan untuk get Tipe Flow
     *
     * String counter = getEasyMapValueByName(extensionRequest, "counter") : di
     * perlukan untuk code counter jika Spesialis
     *
     * String stepsatu = getEasyMapValueByName(extensionRequest, "step_satu") :
     * di perlukan untuk mengambil Inputan/Ketikan User
     *
     * String stepdua = getEasyMapValueByName(extensionRequest, "step_dua"); :
     * di perlukan untuk mengambil Inputan/Ketikan User
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
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

        // Tujuan Lower Step, karna setiap inputan user yang masuk BOT akan Kapital pada huruf pertama //
        String lowerstepdua = stepdua.toLowerCase().replace("siloam hospitals ", "").replace("siloam ", "");
        String imageUrl = "";
        String konfirmArea = "";

        // Cek Apakah Area
        String apiArea = appProp.getApiBaseUrl() + appProp.getApiArea();
        JSONArray resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
        int lengArea = resultsArea.length();
        if (lowerstepdua.equals("bali") || lowerstepdua.equals("nusa") || lowerstepdua.equals("nusa tenggara")) {
            lowerstepdua = "bali & nusa tenggara";
        }

        String areaId = "";
        for (int i = 0; i < lengArea; i++) {
            JSONObject jObjArea = resultsArea.getJSONObject(i);
            String idArea = jObjArea.getString("area_id");
            String nameArea = jObjArea.getString("name");
            String lowername = nameArea.toLowerCase();
            if (lowerstepdua.equals(idArea) || lowerstepdua.equals(lowername)) {
                areaId = idArea;
                konfirmArea = "benar";
                break;
            }
        }
        if (konfirmArea.equalsIgnoreCase("benar")) {
            String apiHospital = appProp.getApiBaseUrl() + appProp.getApiHospitalByArea() + areaId;
            JSONArray resultsHospitals = GeneralExecuteAPI(apiHospital).getJSONArray("data");
            int leng = resultsHospitals.length();
            for (int i = 0; i < leng; i++) {
                JSONObject jObj = resultsHospitals.getJSONObject(i);
                String hospitalId = jObj.getString("hospital_id");
                String hospitalName = jObj.getString("name");
                hospitalName = hospitalName.replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                    imageUrl = jObj.optString("image_url");
                } else {
                    imageUrl = appProp.getSiloamLogo();
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
                sb.append(btnBuilder).append(SPLIT);
            }
            String dialog = "Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi. Bila Anda sudah menemukan RS Siloam yang Anda cari, mohon klik tombol pilihan Anda.";
            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                    .add("Menu Utama", "menu utama").build();
            output.put(OUTPUT, dialog + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
            clearEntities.put("step_satu", areaId);
            clearEntities.put("step_dua", "");
            extensionResult.setEntities(clearEntities);

        } else if (lowerstepdua.equalsIgnoreCase("area") || lowerstepdua.equalsIgnoreCase("spesialis") || lowerstepdua.equalsIgnoreCase("nama")) {
            String tipe = stepdua;

            // Area
            // Ini akan berlaku ketika String step_dua bervalue String "Area"
            if (tipe.equalsIgnoreCase("area")) {
                imageUrl = appProp.getSiloamLogo();
                int leng = resultsArea.length();
                for (int i = 0; i < leng; i++) {
                    JSONObject jObj = resultsArea.getJSONObject(i);
                    areaId = jObj.getString("area_id");
                    String areaName = jObj.getString("name");

                    //Buat Button
                    ButtonTemplate button = new ButtonTemplate();
                    if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                        imageUrl = jObj.optString("image_url");
                        button.setPictureLink(imageUrl);
                        button.setPicturePath(imageUrl);
                    }
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
                    sb.append(btnBuilder).append(SPLIT);
                }
                String dialog = "Silahkan cari rumah sakit Siloam berdasarkan Area yang ingin Anda tuju.";

                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                        .add("Menu Utama", "menu utama").build();
                output.put(OUTPUT, dialog + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
                clearEntities.put("konfirmtipe", tipe);

                // Spesialis
                // Ini akan berlaku ketika String step_dua bervalue String "Spesialis"
            } else if (tipe.equalsIgnoreCase("spesialis")) {
                int code = 0;
                String apiSpecialis = appProp.getApiBaseUrl() + appProp.getApiSpecialistTop();
                JSONArray resultsSpec = GeneralExecuteAPI(apiSpecialis).getJSONArray("data");
                int leng;
                leng = leng(code, resultsSpec);
                sb = carospec(sb, leng, resultsSpec);
                String dialog1 = "Berikut adalah nama-nama spesialisasi yang ada di RS Siloam. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.  ";
                String dialog2 = "Ketik bidang spesialisasi apabila tidak menemukan opsi pilihan Anda\n Contoh: \"Penyakit Dalam\" atau \"Gigi\" \n\n"
                        + "Atau klik salah satu opsi di bawah ini untuk melihat daftar spesialisasi di RS Siloam.";

                // Button Menu
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle("");
                button.setSubTitle(" ");
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName("List Spesialis");
                bookAction.setValue(appProp.getSiloamImageListSpesialis());
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                String imagebuilder = buttonBuilder.build().toString();

                ButtonTemplate button2 = new ButtonTemplate();
                button2.setTitle("");
                button2.setSubTitle(" ");
                List<EasyMap> actions2 = new ArrayList<>();
                EasyMap bookAction2 = new EasyMap();
                bookAction2.setName("Menu Utama");
                bookAction2.setValue("menu utama");
                actions2.add(bookAction2);
                button2.setButtonValues(actions2);
                ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
                String imagebuilder2 = buttonBuilder2.build().toString();
                // ----------//
                output.put(OUTPUT, dialog1 + SPLIT + sb.toString() + SPLIT + dialog2 + SPLIT + imagebuilder + SPLIT + imagebuilder2);
                clearEntities.put("konfirmtipe", tipe);

                // Nama
                // Ini akan berlaku ketika String step_dua bervalue String "Spesialis"
            } else if (tipe.equalsIgnoreCase("nama")) {
                sb.append("Silahkan ketik nama Dokter yang ingin Anda kunjungi. Untuk {bot_name} bantu carikan.");
                output.put(OUTPUT, sb.toString());
                clearEntities.put("konfirmtipe", tipe);

            }
            clearEntities.put("step_satu", "");
            clearEntities.put("step_dua", "");
            clearEntities.put("step_tiga", "");
            clearEntities.put("tipe_pencarian", tipe);
            extensionResult.setEntities(clearEntities);
        } else {
            switch (konfirmtipe.toLowerCase()) {
                // Get Specialist by Hospital
                case "area":
                    String newstepdua = "";

                    String apiHospitalName = appProp.getApiBaseUrl() + appProp.getApiHospitalName() + lowerstepdua;
                    JSONObject jobj1 = GeneralExecuteAPI(apiHospitalName);
                    String stat = "";

                    if (jobj1.getString("status").equalsIgnoreCase("OK") && !jobj1.getJSONArray("data").isNull(0)) {
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
                        String apiHospital = appProp.getApiBaseUrl() + appProp.getApiHospital();
                        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
                        int lenghospital = results.length();
                        for (int i = 0; i < lenghospital; i++) {
                            JSONObject jObj = results.getJSONObject(i);
                            String hospitalId = jObj.getString("hospital_id");
                            String hospitalName = jObj.getString("name");
                            String hospitalName2 = hospitalName.toLowerCase();
                            if (stepdua.equalsIgnoreCase(hospitalId) || stepdua.equalsIgnoreCase(hospitalName2)) {
                                stat = "hospital";
                                newstepdua = hospitalId;
                                break;
                            }
                        }
                    }
                    if (stat.equalsIgnoreCase("hospital")) {
                        int code = 0;
                        if (counter.equalsIgnoreCase("0")) {
                            code = Integer.parseInt(counter);
                        }
                        String apiSpec = appProp.getApiBaseUrl() + appProp.getApiSpecialistTop();
                        JSONArray resultsSpec = GeneralExecuteAPI(apiSpec).getJSONArray("data");
                        int leng;
                        leng = leng(code, resultsSpec);
                        sb = carospec(sb, leng, resultsSpec);

                        String dialog1 = "Berikut adalah nama-nama spesialisasi yang ada di RS Siloam. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.  ";
                        String dialog2 = "Ketik bidang spesialisasi apabila tidak menemukan opsi pilihan Anda\n Contoh: \"Penyakit Dalam\" atau \"Gigi\" \n\n"
                                + "Atau klik salah satu opsi di bawah ini untuk melihat daftar spesialisasi di RS Siloam.";

                        // Button Menu
                        ButtonTemplate button = new ButtonTemplate();
                        button.setTitle("");
                        button.setSubTitle(" ");
                        List<EasyMap> actions = new ArrayList<>();
                        EasyMap bookAction = new EasyMap();
                        bookAction.setName("List Spesialis");
                        bookAction.setValue(appProp.getSiloamImageListSpesialis());
                        actions.add(bookAction);
                        button.setButtonValues(actions);
                        ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                        String imagebuilder = buttonBuilder.build().toString();

                        ButtonTemplate button2 = new ButtonTemplate();
                        button2.setTitle("");
                        button2.setSubTitle(" ");
                        List<EasyMap> actions2 = new ArrayList<>();
                        EasyMap bookAction2 = new EasyMap();
                        bookAction2.setName("Menu Utama");
                        bookAction2.setValue("menu utama");
                        actions2.add(bookAction2);
                        button2.setButtonValues(actions2);
                        ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
                        String imagebuilder2 = buttonBuilder2.build().toString();
                        // ----------//
                        output.put(OUTPUT, dialog1 + SPLIT + sb.toString() + SPLIT + dialog2 + SPLIT + imagebuilder + SPLIT + imagebuilder2);

                        clearEntities.put("step_dua", newstepdua);
                        extensionResult.setEntities(clearEntities);
                    } else {
                        clearEntities.put("step_dua", "");
                        String apiHosArea = appProp.getApiBaseUrl() + appProp.getApiHospitalByArea() + stepsatu;
                        JSONArray results2 = GeneralExecuteAPI(apiHosArea).getJSONArray("data");
                        int leng = results2.length();
                        for (int i = 0; i < leng; i++) {
                            JSONObject jObj = results2.getJSONObject(i);
                            String hospitalId = jObj.getString("hospital_id");
                            String hospitalName = jObj.getString("name");
                            hospitalName = hospitalName.replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                imageUrl = jObj.optString("image_url");
                            } else {
                                imageUrl = appProp.getSiloamLogo();
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
                            sb.append(btnBuilder).append(SPLIT);
                        }
                        String dialog = "Maaf {bot_name} tidak dapat menemukan rumah sakit yang anda cari. Silahkan pilih atau ketik kembali rumah sakit yang ingin anda tuju.";
                        output.put(OUTPUT, dialog + SPLIT + sb.toString());
                        extensionResult.setEntities(clearEntities);
                    }

                    break;
                // Get Hospital by Location //
                case "spesialis":
                    if (stepdua.equalsIgnoreCase("areaspec")) {
                        int leng = resultsArea.length();
                        for (int i = 0; i < leng; i++) {
                            JSONObject jObj = resultsArea.getJSONObject(i);
                            areaId = jObj.getString("area_id");
                            String areaName = jObj.getString("name");

                            //Buat Button
                            ButtonTemplate button = new ButtonTemplate();
                            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                imageUrl = jObj.optString("image_url");
                                button.setPictureLink(imageUrl);
                                button.setPicturePath(imageUrl);
                            } else {
                                imageUrl = appProp.getSiloamLogo();

                            }
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
                            sb.append(btnBuilder).append(SPLIT);
                        }
                        String dialog = "Silahkan cari rumah sakit Siloam berdasarkan Area yang ingin Anda tuju.";
//                        String area = sb.toString();
                        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                                .add("Menu Utama", "menu utama").build();
                        output.put(OUTPUT, dialog + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
                        clearEntities.put("step_satu", "");
                        clearEntities.put("step_dua", "");
                        clearEntities.put("step_tiga", stepsatu);
                        clearEntities.put("konfirmtipe", "area");
                        extensionResult.setEntities(clearEntities);
                    } else {
                        String apiSpecialisId = appProp.getApiBaseUrl() + appProp.getApiSpecialistbyId() + lowerstepdua;
                        JSONObject jsonobjSpecId = GeneralExecuteAPI(apiSpecialisId);
                        if (jsonobjSpecId.getString("status").equalsIgnoreCase("OK") && !jsonobjSpecId.getJSONArray("data").isNull(0)) {
                            JSONArray resultsSpecId = jsonobjSpecId.getJSONArray("data");
                            JSONObject jObj = resultsSpecId.getJSONObject(0);
                            String id_spesialis = jObj.getString("speciality_id");
                            String nameId = jObj.getString("speciality_name");

                            // Buat Button
                            ButtonTemplate button = new ButtonTemplate();
                            button.setTitle("");
                            button.setSubTitle(" ");
                            List<EasyMap> actions = new ArrayList<>();
                            EasyMap bookAction = new EasyMap();
                            bookAction.setName("List Rumah Sakit");
                            bookAction.setValue(appProp.getSiloamImageListHospitals());
                            actions.add(bookAction);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                            String buttonbuilder1 = buttonBuilder.build();

                            ButtonTemplate button2 = new ButtonTemplate();
                            button2.setTitle("");
                            button2.setSubTitle(" ");
                            List<EasyMap> actions2 = new ArrayList<>();
                            EasyMap bookAction2 = new EasyMap();
                            bookAction2.setName("Pilih Rumah Sakit");
                            bookAction2.setValue("areaspec");
                            actions2.add(bookAction2);
                            button2.setButtonValues(actions2);
                            ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
                            String buttonbuilder2 = buttonBuilder2.build();

                            String buttonAll = buttonbuilder1 + SPLIT + buttonbuilder2;

                            String dialog = "Anda telah memilih " + nameId + ". Silahkan ketik nama RS Siloam yang ingin dikunjungi untuk menampilkan daftar dokter " + nameId
                                    + " di RS tersebut. \nContoh: \"Siloam Lippo Village\" atau \"Lippo Village\" \n\nKlik salah satu opsi di bawah ini untuk melihat daftar RS Siloam";
//                            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Atau klik di bawah ini untuk melihat RS Siloam yang terdekat dan pastikan pengaturan lokasi Anda sudah Aktif")
//                                    .add("Rumah Sakit Terdekat", "location").build();
//                            output.put(OUTPUT, dialog + SPLIT + buttonAll + SPLIT + quickReplyBuilder.string());
                            output.put(OUTPUT, dialog + SPLIT + buttonAll);

                            clearEntities.put("step_satu", id_spesialis);
                            clearEntities.put("step_dua", "");
                            extensionResult.setEntities(clearEntities);
                        } else {
                            apiHospitalName = appProp.getApiBaseUrl() + appProp.getApiHospitalName() + lowerstepdua;
                            jobj1 = GeneralExecuteAPI(apiHospitalName);
                            String idhos = "";
                            String hospitalName = "";
                            if (jobj1.getString("status").equalsIgnoreCase("OK") && !jobj1.getJSONArray("data").isNull(0)) {
                                JSONArray resultsHospitlName = jobj1.getJSONArray("data");
                                int leng = resultsHospitlName.length();
                                for (int i = 0; i < leng; i++) {
                                    JSONObject jObj = resultsHospitlName.getJSONObject(i);
                                    idhos = jObj.getString("hospital_id");
                                    hospitalName = jObj.getString("name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                                }
                                String apiGetDokter2 = appProp.getApiBaseUrl() + appProp.getApiDoctorbyhospitalIdSpecialist() + idhos
                                        + appProp.getApiDoctorbyhospitalIdSpecialist_2() + stepsatu;
                                JSONArray results2 = GeneralExecuteAPI(apiGetDokter2).getJSONArray("data");
                                int leng2 = results2.length();
                                if (leng2 >= 10) {
                                    leng2 = 10;
                                }
                                for (int i = 0; i < leng2; i++) {
                                    JSONObject jObj = results2.getJSONObject(i);
                                    String doctorId = jObj.getString("doctor_id");
                                    String hospitalId = jObj.getString("hospital_id");
                                    String doctorName = jObj.getString("name");
                                    String doctorSpecialist = jObj.getString("specialization_name");
                                    String doctorHospitals = jObj.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                                    imageUrl = "";
                                    //Buat Button
                                    ButtonTemplate button = new ButtonTemplate();
                                    button.setTitle(doctorSpecialist);
                                    button.setSubTitle(doctorHospitals);
                                    List<EasyMap> actions = new ArrayList<>();
                                    EasyMap bookAction = new EasyMap();
                                    bookAction.setName(doctorName);
                                    bookAction.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                                    actions.add(bookAction);
                                    button.setButtonValues(actions);
                                    ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                                    String btnBuilder = buttonBuilder.build().toString();
                                    sb.append(btnBuilder).append(SPLIT);
                                }
                                apiSpecialisId = appProp.getApiBaseUrl() + appProp.getApiSpecialistbyId() + stepsatu;
                                JSONObject jSONObject = GeneralExecuteAPI(apiSpecialisId);
                                JSONArray resultsSpecId = jSONObject.getJSONArray("data");
                                JSONObject jObj = resultsSpecId.getJSONObject(0);
                                String nameSpec = jObj.getString("speciality_name");

                                String dialog = "Silahkan geser dan klik dokter spesialis " + nameSpec + " di " + hospitalName;
                                sb.append("Tidak menemukan dokter yang Anda cari?\n\n Silahkan coba lagi dengan mengetik nama dokter, sebagai contoh: \"Budi\" atau \"Budi Chandra\"").append(SPLIT);
                                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Bila terdapat kesulitan. Anda juga dapat memilih salah satu Opsi di bawah ini:")
                                        .add("Hubungi Call Center", "callhospitalcenter").add("Hubungi RS Siloam", "caritelponsiloam").add("Menu Utama", "menu utama").build();
                                sb.append(quickReplyBuilder.string());
                                output.put(OUTPUT, dialog + SPLIT + sb.toString());

                                clearEntities.put("step_dua", lowerstepdua);
                                clearEntities.put("step_tiga", idhos);
                                extensionResult.setEntities(clearEntities);
                            } else {
                                Double latitude = extensionRequest.getIntent().getTicket().getLatitude();
                                Double longitude = extensionRequest.getIntent().getTicket().getLongitude();
                                if (longitude == null || latitude == null) {
                                    apiArea = appProp.getApiBaseUrl() + appProp.getApiArea();
                                    resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
                                    lengArea = resultsArea.length();
                                    for (int i = 0; i < lengArea; i++) {
                                        JSONObject jObj = resultsArea.getJSONObject(i);
                                        areaId = jObj.getString("area_id");
                                        String areaName = jObj.getString("name");

                                        //Buat Button
                                        ButtonTemplate button = new ButtonTemplate();
                                        if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                            imageUrl = jObj.optString("image_url");
                                            button.setPictureLink(imageUrl);
                                            button.setPicturePath(imageUrl);
                                        } else {
                                            imageUrl = appProp.getSiloamLogo();

                                        }
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
                                        sb.append(btnBuilder).append(SPLIT);
                                    }

                                    String dialog = "Mohon maaf, Silvia tidak menemukan nama Siloam Hospitals yang Anda ketik.\n"
                                            + "Silahkan cari rumah sakit Siloam berdasarkan Area yang ingin anda tuju.";
//                                    String area = sb.toString();
                                    QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                                            .add("Menu Utama", "menu utama").build();
                                    output.put(OUTPUT, dialog + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
                                    clearEntities.put("step_satu", "");
                                    clearEntities.put("step_dua", "");
                                    clearEntities.put("step_tiga", stepsatu);
                                    clearEntities.put("konfirmtipe", "area");
                                    extensionResult.setEntities(clearEntities);
                                } else {
                                    System.out.println(latitude + "steptiga");
                                    System.out.println(longitude);
                                    String apiHospital = appProp.getApiBaseUrl() + appProp.getApiHospital();
                                    JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
                                    int leng = results.length();
                                    List<List<String>> data = new ArrayList<>();
                                    double hasil;
                                    for (int i = 0; i < leng; i++) {
                                        JSONObject jObj = results.getJSONObject(i);
                                        String hospitalid = jObj.getString("hospital_id");
                                        String hospitalname = jObj.getString("name");
                                        Double longitud = jObj.getDouble("longitude");
                                        Double latitud = jObj.getDouble("latitude");
                                        String phonenumber = jObj.optString("phoneNumber");
                                        hospitalname = hospitalname.replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                                        if (phonenumber.equals("")) {
                                            phonenumber = "+62211500181";
                                        }
                                        if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                            imageUrl = jObj.optString("image_url");
                                        } else {
                                            imageUrl = appProp.getSiloamLogo();
                                        }
                                        if (latitud != 0 || longitud != 0) {
                                            hasil = distanceInKilometers(latitude, longitude, latitud, longitud);
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
                                        sb.append(btnBuilder).append(SPLIT);

                                    }
                                    if (sb.toString().isEmpty()) {
                                        apiArea = appProp.getApiBaseUrl() + appProp.getApiArea();
                                        resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
                                        lengArea = resultsArea.length();
                                        for (int i = 0; i < lengArea; i++) {
                                            JSONObject jObj = resultsArea.getJSONObject(i);
                                            String area_Id = jObj.getString("area_id");
                                            String area_Name = jObj.getString("name");

                                            //Buat Button
                                            ButtonTemplate button = new ButtonTemplate();
                                            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                                imageUrl = jObj.optString("image_url");
                                                button.setPictureLink(imageUrl);
                                                button.setPicturePath(imageUrl);
                                            } else {
                                                imageUrl = appProp.getSiloamLogo();
                                            }
                                            button.setTitle(area_Name);
                                            button.setSubTitle("");
                                            List<EasyMap> actions = new ArrayList<>();
                                            EasyMap bookAction = new EasyMap();
                                            bookAction.setName(area_Name);
                                            bookAction.setValue(area_Id);
                                            actions.add(bookAction);
                                            button.setButtonValues(actions);
                                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                                            String btnBuilder = buttonBuilder.build().toString();
                                            sb.append(btnBuilder).append(SPLIT);
                                        }

                                        String dialog1 = "Mohon maaf Siloam Hospitals belum tersedia disekitar lokasi Anda. Silahkan pilih dari opsi berikut:";
                                        output.put(OUTPUT, dialog1 + SPLIT + sb.toString());
                                        clearEntities.put("konfirmtipe", "area");
                                        clearEntities.put("step_satu", "");
                                        clearEntities.put("step_dua", "");
                                        extensionResult.setEntities(clearEntities);

                                    } else {
                                        clearEntities.put("step_satu", counter);
                                        String dialog1 = "Berikut adalah daftar RS Siloam yang terdekat dengan Anda. Bila Anda telah menemukan RS yang Anda cari, silahkan klik untuk membuat janji temu.";
                                        output.put(OUTPUT, dialog1 + SPLIT + sb.toString());
                                    }
                                }

//                            
                            }
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
     * Get Doctor
     *
     * Method untuk membuat Carousel List Doctor Berdasarkan id Doctor dan id
     * Hospital
     *
     * String konfirmtipe = getEasyMapValueByName(extensionRequest,
     * "konfirmtipe") : di perlukan untuk get Tipe Flow
     *
     * String counter = getEasyMapValueByName(extensionRequest, "counter") : di
     * perlukan untuk code counter jika Spesialis
     *
     * String stepsatu = getEasyMapValueByName(extensionRequest, "step_satu") :
     * di perlukan untuk mengambil Inputan/Ketikan User
     *
     * String stepdua = getEasyMapValueByName(extensionRequest, "step_dua"); :
     * di perlukan untuk mengambil Inputan/Ketikan User
     *
     * String steptiga = getEasyMapValueByName(extensionRequest, "step_tiga"); :
     * di perlukan untuk mengambil Inputan/Ketikan User
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
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
        System.out.println(stepsatu);
        System.out.println(stepdua);
        System.out.println(steptiga);
        String apiArea = appProp.getApiBaseUrl() + appProp.getApiArea();
        JSONArray resultsArea = GeneralExecuteAPI(apiArea).getJSONArray("data");
        if (steptiga.equalsIgnoreCase("area") || steptiga.equalsIgnoreCase("spesialis") || steptiga.equalsIgnoreCase("nama")) {
            String tipe = steptiga;

            // Area
            // Ini akan berlaku ketika String step_tiga bervalue String "Area"
            if (tipe.equalsIgnoreCase("area")) {
                imageUrl = appProp.getSiloamLogo();
                int leng = resultsArea.length();
                for (int i = 0; i < leng; i++) {
                    JSONObject jObj = resultsArea.getJSONObject(i);
                    String areaId = jObj.getString("area_id");
                    String areaName = jObj.getString("name");

                    //Buat Button
                    ButtonTemplate button = new ButtonTemplate();
                    if (jObj.optString("image_url").equals("")) {
                        imageUrl = jObj.optString("image_url");
                        button.setPictureLink(imageUrl);
                        button.setPicturePath(imageUrl);
                    }

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
                    sb.append(btnBuilder).append(SPLIT);

                }
                String dialog = "Silahkan cari rumah sakit Siloam berdasarkan Area yang ingin Anda tuju.";
                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                        .add("Menu Utama", "menu utama").build();
                output.put(OUTPUT, dialog + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
                clearEntities.put("konfirmtipe", tipe);

                // Spesialis
                // Ini akan berlaku ketika String step_tiga bervalue String "Spesialis"
            } else if (tipe.equalsIgnoreCase("spesialis")) {
                int code = 0;
                String apiSpecialis = appProp.getApiBaseUrl() + appProp.getApiSpecialistTop();
                JSONArray resultsSpec = GeneralExecuteAPI(apiSpecialis).getJSONArray("data");
                int leng;
                leng = leng(code, resultsSpec);
                sb = carospec(sb, leng, resultsSpec);
                String dialog1 = "Berikut adalah nama-nama spesialisasi yang ada di RS Siloam. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.  ";
                String dialog2 = "Ketik bidang spesialisasi apabila tidak menemukan opsi pilihan Anda\n Contoh: \"Penyakit Dalam\" atau \"Gigi\" \n\n"
                        + "Atau klik salah satu opsi di bawah ini untuk melihat daftar spesialisasi di RS Siloam.";

                // Button Menu
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle("");
                button.setSubTitle(" ");
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName("List Spesialis");
                bookAction.setValue(appProp.getSiloamImageListSpesialis());
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                String imagebuilder = buttonBuilder.build().toString();

                ButtonTemplate button2 = new ButtonTemplate();
                button2.setTitle("");
                button2.setSubTitle(" ");
                List<EasyMap> actions2 = new ArrayList<>();
                EasyMap bookAction2 = new EasyMap();
                bookAction2.setName("Menu Utama");
                bookAction2.setValue("menu utama");
                actions2.add(bookAction2);
                button2.setButtonValues(actions2);
                ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
                String imagebuilder2 = buttonBuilder2.build().toString();
                // ----------//
                output.put(OUTPUT, dialog1 + SPLIT + sb.toString() + SPLIT + dialog2 + SPLIT + imagebuilder + SPLIT + imagebuilder2);
                clearEntities.put("konfirmtipe", tipe);

                // Nama
                // Ini akan berlaku ketika String step_tiga bervalue String "Spesialis"
            } else if (tipe.equalsIgnoreCase("nama")) {
                sb.append("Silahkan ketik nama Dokter yang ingin Anda kunjungi. Untuk {bot_name} bantu carikan.");
                output.put(OUTPUT, sb.toString());
                clearEntities.put("konfirmtipe", tipe);

            }
            clearEntities.put("step_satu", "");
            clearEntities.put("step_dua", "");
            clearEntities.put("step_tiga", "");
            clearEntities.put("tipe_pencarian", tipe);
            clearEntities.put("doctorid", "");
            extensionResult.setEntities(clearEntities);

        } else {
            switch (konfirmtipe.toLowerCase()) {
                // Get Doctor by Area
                case "area":
                    stepdua = stepdua.toLowerCase();
                    String apiHospitalName1 = appProp.getApiBaseUrl() + appProp.getApiHospitalName() + stepdua;
                    JSONObject jobjHos = GeneralExecuteAPI(apiHospitalName1);

                    if (jobjHos.getString("status").equalsIgnoreCase("OK") && !jobjHos.getJSONArray("data").isNull(0)) {
                        JSONArray resultsHospitalName = jobjHos.getJSONArray("data");
                        JSONObject jObj;
                        if (stepdua.equalsIgnoreCase("lippo village")) {
                            jObj = resultsHospitalName.getJSONObject(1);
                        } else {
                            jObj = resultsHospitalName.getJSONObject(0);
                        }
                        String hospitalId = jObj.getString("hospital_id");
                        stepdua = hospitalId;
                    } else {
                        String apiHospital = appProp.getApiBaseUrl() + appProp.getApiHospital();
                        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
                        int lenghospital = results.length();
                        for (int i = 0; i < lenghospital; i++) {
                            JSONObject jObj = results.getJSONObject(i);
                            String hospitalId = jObj.getString("hospital_id");
                            String hospitalName = jObj.getString("name");
                            String hospitalName2 = hospitalName.toLowerCase();
                            if (stepdua.equalsIgnoreCase(hospitalId) || stepdua.equalsIgnoreCase(hospitalName2)) {
                                stepdua = hospitalId;
                                break;
                            }
                        }
                    }

                    String[] splitspesialis = steptiga.split(" ");
                    String spesial1 = splitspesialis[0];

                    String apiHospitalbyId = appProp.getApiBaseUrl() + appProp.getApiHospitalbyId() + spesial1;
                    JSONObject jobjHospital = GeneralExecuteAPI(apiHospitalbyId);

                    String statArea = "";

                    int lengArea = resultsArea.length();
                    for (int i = 0; i < lengArea; i++) {
                        JSONObject jObjArea = resultsArea.getJSONObject(i);
                        String idArea = jObjArea.getString("area_id");
                        String nameArea = jObjArea.getString("name");
                        if (spesial1.equalsIgnoreCase(idArea) || spesial1.equalsIgnoreCase(nameArea)) {
                            statArea = "area";
                            break;
                        }
                    }
                    if (statArea.equalsIgnoreCase("area")) {
                        String apiHospital = appProp.getApiBaseUrl() + appProp.getApiHospitalByArea() + spesial1;
                        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
                        int leng = results.length();
                        for (int i = 0; i < leng; i++) {
                            JSONObject jObj = results.getJSONObject(i);
                            String hospitalId = jObj.getString("hospital_id");
                            String hospitalName = jObj.getString("name");
                            hospitalName = hospitalName.replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                            if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                imageUrl = jObj.optString("image_url");
                            } else {
                                imageUrl = appProp.getSiloamLogo();
                            }

                            //Buat Button
                            String value = hospitalId;
                            ButtonBuilder buatBtnBuilder = btnbuilderGeneral(imageUrl, hospitalName, "", hospitalName, value);
                            String btnBuilder = buatBtnBuilder.build().toString();
                            sb.append(btnBuilder).append(SPLIT);
                        }
                        String dialog = "Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi. Bila Anda sudah menemukan RS Siloam yang Anda cari, mohon klik tombol pilihan Anda.";
                        QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                                .add("Menu Utama", "menu utama").build();
                        output.put(OUTPUT, dialog + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
                        clearEntities.put("step_satu", spesial1);
                        clearEntities.put("step_dua", "");
                        clearEntities.put("step_tiga", "");
                        clearEntities.put("doctorid", "");
                        extensionResult.setEntities(clearEntities);

                    } else if (jobjHospital.getString("status").equalsIgnoreCase("OK") && !jobjHospital.getJSONArray("data").isNull(0)) {
                        int code = 0;
                        if (counter.equalsIgnoreCase("0")) {
                            code = Integer.parseInt(counter);
                        }
                        String apiSpec = appProp.getApiBaseUrl() + appProp.getApiSpecialistTop();
                        JSONArray resultsSpec = GeneralExecuteAPI(apiSpec).getJSONArray("data");
                        int leng;
                        leng = leng(code, resultsSpec);
                        sb = carospec(sb, leng, resultsSpec);

                        String dialog1 = "Berikut adalah nama-nama spesialisasi yang ada di RS Siloam. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.  ";
                        String dialog2 = "Ketik bidang spesialisasi apabila tidak menemukan opsi pilihan Anda\n Contoh: \"Penyakit Dalam\" atau \"Gigi\" \n\n"
                                + "Atau klik salah satu opsi di bawah ini untuk melihat daftar spesialisasi di RS Siloam.";

                        // Button Menu
                        ButtonTemplate button = new ButtonTemplate();
                        button.setTitle("");
                        button.setSubTitle(" ");
                        List<EasyMap> actions = new ArrayList<>();
                        EasyMap bookAction = new EasyMap();
                        bookAction.setName("List Spesialis");
                        bookAction.setValue(appProp.getSiloamImageListSpesialis());
                        actions.add(bookAction);
                        button.setButtonValues(actions);
                        ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                        String imagebuilder = buttonBuilder.build().toString();

                        ButtonTemplate button2 = new ButtonTemplate();
                        button2.setTitle("");
                        button2.setSubTitle(" ");
                        List<EasyMap> actions2 = new ArrayList<>();
                        EasyMap bookAction2 = new EasyMap();
                        bookAction2.setName("Menu Utama");
                        bookAction2.setValue("menu utama");
                        actions2.add(bookAction2);
                        button2.setButtonValues(actions2);
                        ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
                        String imagebuilder2 = buttonBuilder2.build().toString();
                        // ----------//
                        output.put(OUTPUT, dialog1 + SPLIT + sb.toString() + SPLIT + dialog2 + SPLIT + imagebuilder + SPLIT + imagebuilder2);

                        clearEntities.put("step_dua", spesial1);
                        clearEntities.put("step_tiga", "");
                        clearEntities.put("doctorid", "");
                        extensionResult.setEntities(clearEntities);

                    } else {
                        String apiSpesilisId = appProp.getApiBaseUrl() + appProp.getApiSpecialist();
                        JSONObject jobj = GeneralExecuteAPI(apiSpesilisId);

                        String apiSpesilisName = appProp.getApiBaseUrl() + appProp.getApiSpecialistbyname() + steptiga.toLowerCase();
                        JSONObject jobj2 = GeneralExecuteAPI(apiSpesilisName);
                        String jumlahleng = "";
                        if (jobj2.getString("status").equalsIgnoreCase("OK") && !jobj2.getJSONArray("data").isNull(0)) {
                            JSONArray resultsSpesialis = jobj2.getJSONArray("data");
                            if (resultsSpesialis.length() > 1) {
                                jumlahleng = "lebih";
                            } else {
                                JSONObject jObj = resultsSpesialis.getJSONObject(0);
                                String id_spesialis = jObj.getString("speciality_id");
                                spesial1 = id_spesialis;
                                clearEntities.put("step_tiga", id_spesialis);
                                extensionResult.setEntities(clearEntities);
                            }

                        } else {
                            JSONArray dataSpec = jobj.getJSONArray("data");
                            int lengspec = dataSpec.length();
                            for (int i = 0; i < lengspec; i++) {
                                JSONObject jObjSpec = dataSpec.getJSONObject(i);
                                String idSpec = jObjSpec.getString("speciality_id");
                                if (spesial1.equalsIgnoreCase(idSpec)) {
                                    spesial1 = idSpec;
                                    break;
                                }
                            }
                        }
                        if (jumlahleng.equalsIgnoreCase("lebih")) {
                            JSONArray resultsSpesialis = jobj2.getJSONArray("data");
                            int leng2 = resultsSpesialis.length();

                            for (int i = 0; i < leng2; i++) {
                                JSONObject jObj = resultsSpesialis.getJSONObject(i);
                                String id_spesialis = jObj.getString("speciality_id");
                                String nameEn = jObj.getString("speciality_name_en");
                                String nameId = jObj.getString("speciality_name");
                                // Buat Button 
                                if (!jObj.optString("image_url").equalsIgnoreCase("")) {
                                    imageUrl = jObj.optString("image_url");
                                } else {
                                    imageUrl = appProp.getSiloamLogo();
                                }

                                ButtonTemplate button = new ButtonTemplate();
                                button.setPictureLink(imageUrl);
                                button.setPicturePath(imageUrl);
                                button.setTitle(nameEn);
                                button.setSubTitle("");
                                List<EasyMap> actions = new ArrayList<>();
                                EasyMap bookAction = new EasyMap();
                                bookAction.setName(nameId);
                                bookAction.setValue(id_spesialis);
                                actions.add(bookAction);
                                button.setButtonValues(actions);
                                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                                String btnBuilder = buttonBuilder.build().toString();

                                sb.append(btnBuilder).append(SPLIT);
                            }
                            String truecase = "Berikut Spesialis yang {bot_name} dapat temukan. Silahkan pilih Spesialis yang ingin Anda kunjungi.";
                            output.put(OUTPUT, truecase + SPLIT + sb.toString());
                            clearEntities.put("step_tiga", "");

                        } else {
                            String apiGetDokter = appProp.getApiBaseUrl() + appProp.getApiDoctorbyhospitalIdSpecialist() + stepdua
                                    + appProp.getApiDoctorbyhospitalIdSpecialist_2() + spesial1;
                            JSONObject jObjDoctor = GeneralExecuteAPI(apiGetDokter);
                            if (jObjDoctor.getString("status").equalsIgnoreCase("OK") && !jObjDoctor.getJSONArray("data").isNull(0)) {
                                JSONArray results = jObjDoctor.getJSONArray("data");
                                int leng = results.length();
                                if (leng > 10) {
                                    leng = 10;
                                }
                                for (int i = 0; i < leng; i++) {
                                    JSONObject jObj = results.getJSONObject(i);
                                    String doctorId = jObj.getString("doctor_id");
                                    String hospitalId = jObj.getString("hospital_id");
                                    String doctorName = jObj.getString("name");
                                    String doctorSpecialist = jObj.getString("specialization_name");
                                    String doctorHospitals = jObj.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                                    imageUrl = "";
                                    //Buat Button
                                    ButtonTemplate button = new ButtonTemplate();
                                    button.setTitle(doctorSpecialist);
                                    button.setSubTitle(doctorHospitals);
                                    List<EasyMap> actions = new ArrayList<>();
                                    EasyMap bookAction = new EasyMap();
                                    bookAction.setName(doctorName);
                                    bookAction.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                                    actions.add(bookAction);
                                    button.setButtonValues(actions);
                                    ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                                    String btnBuilder = buttonBuilder.build().toString();
                                    sb.append(btnBuilder).append(SPLIT);
                                }
                                String dialog = "Berikut adalah beberapa dokter yang sesuai dengan pencarian Anda. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.";
                                sb.append("Tidak menemukan dokter yang Anda cari?\n Silahkan coba lagi dengan mengetik nama dokter, sebagai contoh: \"Budi\" atau \"Budi Chandra\"").append(SPLIT);
                                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Bila terdapat kesulitan. Anda juga dapat memilih salah satu Opsi di bawah ini:")
                                        .add("Hubungi Call Center", "callhospitalcenter").add("Hubungi RS Siloam", "caritelponsiloam").add("Menu Utama", "menu utama").build();
                                sb.append(quickReplyBuilder.string());
                                output.put(OUTPUT, dialog + SPLIT + sb.toString());
//                                String dialog = "Berikut adalah daftar dokter yang dapat Anda pilih.";
//                                output.put(OUTPUT, dialog + SPLIT + sb.toString());
                            } else {
                                ButtonTemplate image = new ButtonTemplate();
                                image.setTitle("");
                                image.setSubTitle("Klik dibawah ini untuk melihat List Bidang Spesialis");
                                List<EasyMap> actions = new ArrayList<>();
                                EasyMap bookAction = new EasyMap();
                                bookAction.setName("List Spesialis");
                                bookAction.setValue(appProp.getSiloamImageListSpesialis());
                                actions.add(bookAction);
                                image.setButtonValues(actions);
                                ButtonBuilder buttonBuilder = new ButtonBuilder(image);
                                String imagebuilder = buttonBuilder.build().toString();
                                String falsecase1 = "Mohon maaf {bot_name} tidak menemukan Spesialis yang Anda cari";
                                String falsecase2 = "Ketik kembali bidang spesialisasi yang ingin Anda cari.\n Contoh: \"Penyakit Dalam\" atau \"dermatologi\"";
                                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik opsi dibawah untuk ke Menu Utama")
                                        .add("Menu Utama", "menu utama").build();
                                output.put(OUTPUT, falsecase1 + SPLIT + imagebuilder + SPLIT + falsecase2 + SPLIT + quickReplyBuilder.string());
                                clearEntities.put("step_tiga", "");

                            }
                        }

                        extensionResult.setEntities(clearEntities);

                    }
                    break;
                // Get Doctor by Spesialis
                case "spesialis":

                    steptiga = steptiga.toLowerCase().replace("siloam hospitals ", "").replace("siloam ", "");
                    String apiHospitalName = appProp.getApiBaseUrl() + appProp.getApiHospitalName() + steptiga;
                    JSONObject jobj1 = GeneralExecuteAPI(apiHospitalName);
                    String idhos = "";
                    String hospitalName = "";
                    if (jobj1.getString("status").equalsIgnoreCase("OK") && !jobj1.getJSONArray("data").isNull(0)) {
                        JSONArray resultsHospitalName = jobj1.getJSONArray("data");
                        JSONObject jObj;
                        if (steptiga.equalsIgnoreCase("lippo village")) {
                            jObj = resultsHospitalName.getJSONObject(1);
                        } else {
                            jObj = resultsHospitalName.getJSONObject(0);
                        }
                        String hospitalId = jObj.getString("hospital_id");
                        idhos = hospitalId;
                    } else {
                        String apiHospital = appProp.getApiBaseUrl() + appProp.getApiHospital();
                        JSONArray results = GeneralExecuteAPI(apiHospital).getJSONArray("data");
                        int lenghospital = results.length();
                        for (int i = 0; i < lenghospital; i++) {
                            JSONObject jObj = results.getJSONObject(i);
                            String hospitalId = jObj.getString("hospital_id");
                            hospitalName = jObj.getString("name");
                            String hospitalName2 = hospitalName.toLowerCase();
                            if (steptiga.equalsIgnoreCase(hospitalId) || steptiga.equalsIgnoreCase(hospitalName2)) {
                                idhos = hospitalId;
                                break;
                            }
                        }
                    }
                    String[] splitspesialis2 = stepsatu.split(" ");
                    String spesial2 = splitspesialis2[0];

                    if (!counter.equalsIgnoreCase("0")) {
                        spesial2 = counter.toLowerCase();
                    }
                    String apiGetDokter2 = appProp.getApiBaseUrl() + appProp.getApiDoctorbyhospitalIdSpecialist() + idhos
                            + appProp.getApiDoctorbyhospitalIdSpecialist_2() + spesial2;

                    JSONArray results2 = GeneralExecuteAPI(apiGetDokter2).getJSONArray("data");
                    int leng = results2.length();
                    if (leng >= 10) {
                        leng = 10;
                    }
                    for (int i = 0; i < leng; i++) {
                        JSONObject jObj = results2.getJSONObject(i);
                        String doctorId = jObj.getString("doctor_id");
                        String hospitalId = jObj.getString("hospital_id");
                        String doctorName = jObj.getString("name");
                        String doctorSpecialist = jObj.getString("specialization_name");
                        String doctorHospitals = jObj.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                        imageUrl = "";
                        //Buat Button
                        ButtonTemplate button = new ButtonTemplate();
//                        button.setPictureLink("");
//                        button.setPicturePath("");
                        button.setTitle(doctorSpecialist);
                        button.setSubTitle(doctorHospitals);

                        List<EasyMap> actions = new ArrayList<>();
                        EasyMap bookAction = new EasyMap();
                        bookAction.setName(doctorName);
                        bookAction.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                        actions.add(bookAction);
                        button.setButtonValues(actions);
                        ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                        String btnBuilder = buttonBuilder.build().toString();
                        sb.append(btnBuilder).append(SPLIT);
                    }
                    String dialog = "Berikut adalah beberapa dokter yang sesuai dengan pencarian Anda. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.";
                    sb.append("Tidak menemukan dokter yang Anda cari?\n Silahkan coba lagi dengan mengetik nama dokter, sebagai contoh: \"Budi\" atau \"Budi Chandra\"").append(SPLIT);
                    QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Bila terdapat kesulitan. Anda juga dapat memilih salah satu Opsi di bawah ini:")
                            .add("Hubungi Call Center", "callhospitalcenter").add("Hubungi RS Siloam", "caritelponsiloam").add("Menu Utama", "menu utama").build();
                    sb.append(quickReplyBuilder.string());
                    output.put(OUTPUT, dialog + SPLIT + sb.toString());
//                    String dialog = "Berikut adalah daftar dokter yang dapat Anda pilih.";
//                    output.put(OUTPUT, dialog + SPLIT + sb.toString());
                    clearEntities.put("step_tiga", idhos);
                    extensionResult.setEntities(clearEntities);
                    break;
            }
        }
        extensionResult.setValue(output);
        return extensionResult;

    }

    /**
     * Get Schedule Doctor
     *
     * Method ini untuk menampilkan Carousel Jadwal dari Dokter yang di Pilih
     * pada Step Sebelumnya Berupa Tanggal Praktek Dokter yang dipilih untuk 7
     * Hari kedepan di mulai Dari H+1
     *
     * String tipe = getEasyMapValueByName(extensionRequest, "tipe_pencarian") :
     * Untuk mengambil inputan User sebagai Parameter pencarian. Jika User
     * ByPass Intention contoh:"Jadwal Dokter (Billy)"
     *
     * String konfirmtipe = getEasyMapValueByName(extensionRequest,
     * "konfirmtipe") : di perlukan untuk get Tipe Flow
     *
     * String counter = getEasyMapValueByName(extensionRequest, "counter") : di
     * perlukan untuk code counter jika Spesialis
     *
     * String stepsatu = getEasyMapValueByName(extensionRequest, "step_satu") :
     * di perlukan untuk mengambil Inputan/Ketikan User
     *
     * String stepdua = getEasyMapValueByName(extensionRequest, "step_dua"); :
     * untuk get ID Hospital sesuai Dokter Pilihan yang mereplace Data Lama(Jika
     * Ada)
     *
     * String steptiga = getEasyMapValueByName(extensionRequest, "step_tiga") :
     * di perlukan untuk mengambil Inputan/Ketikan User
     *
     * String doctorId = getEasyMapValueByName(extensionRequest, "dokterid"):
     * untuk get ID Doctor sesuai Dokter Pilihan User
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
     */
    @Override
    public ExtensionResult newGetScheduleDoctorId(ExtensionRequest extensionRequest) {
        log.debug("newGetScheduleDoctorId() extension request: {}", new Gson().toJson(extensionRequest, ExtensionRequest.class));
        StringBuilder proctime = new StringBuilder();
        Date dnow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss SSS");
        proctime.append(ft.format(dnow) + " # ");
        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        Map<String, String> clearEntities = new HashMap<>();

        String tipe = getEasyMapValueByName(extensionRequest, "tipe_pencarian");
        String konfirmtipe = getEasyMapValueByName(extensionRequest, "konfirmtipe");
        String doctorId = getEasyMapValueByName(extensionRequest, "dokterid");
        String stepsatu = getEasyMapValueByName(extensionRequest, "step_satu");
        String counter = getEasyMapValueByName(extensionRequest, "counter");
        String stepdua = getEasyMapValueByName(extensionRequest, "step_dua");
        String steptiga = getEasyMapValueByName(extensionRequest, "step_tiga");
//        String testdate = getEasyMapValueByName(extensionRequest, "testdate");
        // Kodingan Awal
        System.out.println("Schedule");
        System.out.println(stepsatu);
        System.out.println(stepdua);
        System.out.println(steptiga);
        System.out.println(counter);
        System.out.println(doctorId);
        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, +1);
        String[] iddokter = doctorId.split(" ");
        String dokid = iddokter[0];
        if (dokid.equalsIgnoreCase("id") || dokid.equalsIgnoreCase("dokter")) {
            dokid = iddokter[1];
        }

        String hosid = "";
        String[] idhos = stepdua.split(" ");
        hosid = idhos[0];
        if (hosid.equalsIgnoreCase("hos")) {
            hosid = idhos[1];
        }

        String apiDokterName = appProp.getApiBaseUrl() + appProp.getApiDoctorbyname() + dokid;
        JSONObject jobjDoctorName = GeneralExecuteAPI(apiDokterName);
        String getNameHospital = appProp.getApiBaseUrl() + appProp.getApiHospitalbyId() + dokid;
        JSONObject jsonobjHospital = GeneralExecuteAPI(getNameHospital);
        String apiSpecialisId = appProp.getApiBaseUrl() + appProp.getApiSpecialistbyId() + dokid;
        JSONObject jsonobjSpecId = GeneralExecuteAPI(apiSpecialisId);
        System.out.println(dokid);

        if (jobjDoctorName.getString("status").equalsIgnoreCase("OK") && !jobjDoctorName.getJSONArray("data").isNull(0)) {
            JSONArray resultsNama = jobjDoctorName.getJSONArray("data");
            int leng3 = resultsNama.length();
            if (leng3 >= 15) {
                leng3 = 15;
            }
            for (int j = 0; j < leng3; j++) {
                JSONObject jObj2 = resultsNama.getJSONObject(j);
                String doctorid = jObj2.getString("doctor_id");
                String doctorName = jObj2.getString("name");
                String hospitalId = jObj2.getString("hospital_id");
                String doctorSpecialist = jObj2.getString("specialization_name");
                String doctorHospitals = jObj2.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");

                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(doctorSpecialist);
                button.setSubTitle(doctorHospitals);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName(doctorName);
                bookAction.setValue("dokter id " + doctorid + " di hos " + hospitalId);
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(SPLIT);
            }

            String dialog = "Berikut adalah beberapa dokter yang sesuai dengan pencarian Anda. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.";
            sb.append("Tidak menemukan dokter yang Anda cari?\n Silahkan coba lagi dengan mengetik nama dokter, sebagai contoh: \"Budi\" atau \"Budi Chandra\"").append(SPLIT);
            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Bila terdapat kesulitan. Anda juga dapat memilih salah satu Opsi di bawah ini:")
                    .add("Hubungi Call Center", "callhospitalcenter").add("Hubungi RS Siloam", "caritelponsiloam").add("Menu Utama", "menu utama").build();
            sb.append(quickReplyBuilder.string());
            output.put(OUTPUT, dialog + SPLIT + sb.toString());
            clearEntities.put("step_satu", doctorId);
            clearEntities.put("dokterid", "");

        } else if (jsonobjHospital.getString("status").equalsIgnoreCase("OK") && !jsonobjHospital.getJSONArray("data").isNull(0)) {
            String spesial2 = "";
            if (stepsatu.equalsIgnoreCase("Indonesia")) {
                spesial2 = counter;
            } else {
                spesial2 = stepsatu.toLowerCase();
            }

            String apiGetDokter2 = appProp.getApiBaseUrl() + appProp.getApiDoctorbyhospitalIdSpecialist() + dokid.toLowerCase()
                    + appProp.getApiDoctorbyhospitalIdSpecialist_2() + spesial2;
            JSONArray results2 = GeneralExecuteAPI(apiGetDokter2).getJSONArray("data");
            int leng = results2.length();
            if (leng >= 10) {
                leng = 10;
            }
            for (int i = 0; i < leng; i++) {
                JSONObject jObj = results2.getJSONObject(i);
                String doctorid = jObj.getString("doctor_id");
                String hospitalId = jObj.getString("hospital_id");
                String doctorName = jObj.getString("name");
                String doctorSpecialist = jObj.getString("specialization_name");
                String doctorHospitals = jObj.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
//                imageUrl = "";
                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
//                        button.setPictureLink("");
//                        button.setPicturePath("");
                button.setTitle(doctorSpecialist);
                button.setSubTitle(doctorHospitals);

                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName(doctorName);
                bookAction.setValue("dokter id " + doctorid + " di hos " + hospitalId);
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(SPLIT);
            }
            if (sb.toString().isEmpty()) {
                String dialog = "Maaf {bot_name} tidak menemukan daftar dokter yang anda cari. Mohon hubungi Contact Center 1-500-181";
                output.put(OUTPUT, dialog);
                clearEntities.put("tanggalpesan", "13 00-16 00=2019-03-28");
                clearEntities.put("jampraktek", "10 00");
                clearEntities.put("namapasien", "Admin");
                clearEntities.put("tanggallahir", "1995-01-01");
                clearEntities.put("notelp", "081318151400");
                clearEntities.put("confirm", "yes");
            } else {
                String dialog = "Berikut adalah beberapa dokter yang sesuai dengan pencarian Anda. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.";
                sb.append("Tidak menemukan dokter yang Anda cari?\n Silahkan coba lagi dengan mengetik nama dokter, sebagai contoh: \"Budi\" atau \"Budi Chandra\"").append(SPLIT);
                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Bila terdapat kesulitan. Anda juga dapat memilih salah satu Opsi di bawah ini:")
                        .add("Hubungi Call Center", "callhospitalcenter").add("Hubungi RS Siloam", "caritelponsiloam").add("Menu Utama", "menu utama").build();
                sb.append(quickReplyBuilder.string());
                output.put(OUTPUT, dialog + SPLIT + sb.toString());
//                String dialog = "Berikut adalah daftar dokter yang dapat Anda pilih.";
//                output.put(OUTPUT, dialog + SPLIT + sb.toString());
                clearEntities.put("step_tiga", dokid);
                clearEntities.put("dokterid", "");
            }

        } else if (jsonobjSpecId.getString("status").equalsIgnoreCase("OK") && !jsonobjSpecId.getJSONArray("data").isNull(0)) {
            JSONArray resultsSpecId = jsonobjSpecId.getJSONArray("data");
            JSONObject jObj = resultsSpecId.getJSONObject(0);
            String id_spesialis = jObj.getString("speciality_id");
//            String nameId = jObj.getString("speciality_name");
            if (konfirmtipe.equalsIgnoreCase("spesialis")) {
                hosid = steptiga.toLowerCase();
            }

            String apiGetDokter2 = appProp.getApiBaseUrl() + appProp.getApiDoctorbyhospitalIdSpecialist() + hosid
                    + appProp.getApiDoctorbyhospitalIdSpecialist_2() + id_spesialis;
            JSONArray results2 = GeneralExecuteAPI(apiGetDokter2).getJSONArray("data");
            int leng = results2.length();
            if (leng >= 10) {
                leng = 10;
            }
            for (int i = 0; i < leng; i++) {
                JSONObject jObj2 = results2.getJSONObject(i);
                String doctorId2 = jObj2.getString("doctor_id");
                String hospitalId = jObj2.getString("hospital_id");
                String doctorName = jObj2.getString("name");
                String doctorSpecialist = jObj2.getString("specialization_name");
                String doctorHospitals = jObj2.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
//                imageUrl = "";
                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
//                        button.setPictureLink("");
//                        button.setPicturePath("");
                button.setTitle(doctorSpecialist);
                button.setSubTitle(doctorHospitals);

                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName(doctorName);
                bookAction.setValue("dokter id " + doctorId2 + " di hos " + hospitalId);
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(SPLIT);
            }
            if (sb.toString().isEmpty()) {
                int code = 0;
                String apiSpecialis = appProp.getApiBaseUrl() + appProp.getApiSpecialistTop();
                JSONArray resultsSpec = GeneralExecuteAPI(apiSpecialis).getJSONArray("data");

                int lengSpec;
                lengSpec = leng(code, resultsSpec);
                sb = carospec(sb, lengSpec, resultsSpec);
                String dialog1 = "Maaf {bot_name} tidak menemukan daftar spesialisasi yang anda cari. Berikut adalah nama-nama spesialisasi yang ada di RS Siloam. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.  ";
                String dialog2 = "Ketik bidang spesialisasi apabila tidak menemukan opsi pilihan Anda\n Contoh: \"Penyakit Dalam\" atau \"Gigi\" \n\n"
                        + "Atau klik salah satu opsi di bawah ini untuk melihat daftar spesialisasi di RS Siloam.";

                // Button Menu
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle("");
                button.setSubTitle(" ");
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName("List Spesialis");
                bookAction.setValue(appProp.getSiloamImageListSpesialis());
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                String imagebuilder = buttonBuilder.build().toString();

                ButtonTemplate button2 = new ButtonTemplate();
                button2.setTitle("");
                button2.setSubTitle(" ");
                List<EasyMap> actions2 = new ArrayList<>();
                EasyMap bookAction2 = new EasyMap();
                bookAction2.setName("Menu Utama");
                bookAction2.setValue("menu utama");
                actions2.add(bookAction2);
                button2.setButtonValues(actions2);
                ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
                String imagebuilder2 = buttonBuilder2.build().toString();
                // ----------//
                output.put(OUTPUT, dialog1 + SPLIT + sb.toString() + SPLIT + dialog2 + SPLIT + imagebuilder + SPLIT + imagebuilder2);
                clearEntities.put("step_satu", "");
                clearEntities.put("dokterid", "");
            } else {
                String dialog = "Berikut adalah beberapa dokter yang sesuai dengan pencarian Anda. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.";
                sb.append("Tidak menemukan dokter yang Anda cari?\n Silahkan coba lagi dengan mengetik nama dokter, sebagai contoh: \"Budi\" atau \"Budi Chandra\"").append(SPLIT);
                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Bila terdapat kesulitan. Anda juga dapat memilih salah satu Opsi di bawah ini:")
                        .add("Hubungi Call Center", "callhospitalcenter").add("Hubungi RS Siloam", "caritelponsiloam").add("Menu Utama", "menu utama").build();
                sb.append(quickReplyBuilder.string());
                output.put(OUTPUT, dialog + SPLIT + sb.toString());
//                String dialog = "Berikut adalah daftar dokter yang dapat Anda pilih.";
//                output.put(OUTPUT, dialog + SPLIT + sb.toString());
                clearEntities.put("counter", dokid);
                clearEntities.put("step_tiga", dokid);
                clearEntities.put("dokterid", "");
            }

        } else {
            String hasilcekcuti;
            String getLeaveDoctor = appProp.getApiBaseUrl() + appProp.getApiLeaveDoctor() + hosid + appProp.getApiLeaveDoctor_2() + dokid;
            JSONObject jobjCuti = GeneralExecuteAPI(getLeaveDoctor);

            String schedule = appProp.getApiBaseUrl() + appProp.getApiDoctorschedule() + hosid + appProp.getApiDoctorschedule_2() + dokid;
            JSONObject jobjSchedule = GeneralExecuteAPI(schedule);
            if (jobjSchedule.getString("status").equalsIgnoreCase("OK") && !jobjSchedule.getJSONArray("data").isNull(0)) {
                JSONArray results = jobjSchedule.getJSONArray("data");
                int leng = results.length();
                List<Integer> dayslist = new ArrayList<>();
                for (int i = 0; i < 14;) {
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
                    if (jobjCuti.getString("status").equalsIgnoreCase("OK") && jobjCuti.getJSONArray("data").isNull(0)) {
                        hasilcekcuti = "tidak";
                    } else {
                        hasilcekcuti = CekCuti(jobjCuti, date);
                    }
//                String hasilcekcuti = CekCuti(dokid, testdate);
                    if (hasilcekcuti.equalsIgnoreCase("tidak")) {
                        String[] daypoint = new String[leng];
                        String[] daypoint2 = new String[leng];

                        //Cek Slot
                        String scheduleTime = appProp.getApiBaseUrl() + appProp.getApiDoctorappointment() + hosid
                                + appProp.getApiDoctorappointment_2() + dokid + appProp.getApiDoctorappointment_3() + date;
                        JSONObject jobj = GeneralExecuteAPI(scheduleTime);
                        if (jobj.getString("status").equalsIgnoreCase("OK") && !jobj.getJSONArray("data").isNull(0)) {
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
                                int daysnumber = jObj.getInt("day");
                                dayslist.add(daysnumber);
                                String fromtime = jObj.getString("from_time");
                                String totime = jObj.getString("to_time");
                                String dateF = fromtime.substring(0, 5);
                                String dateT = totime.substring(0, 5);
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

                        }
                        String tanggaltitle = hari + ", " + tanggal + " " + bulan + " " + tahun;
                        if (dayslist.contains(kodeHari) && available.equalsIgnoreCase("Available")) {
                            //Buat Button
                            ButtonTemplate button = new ButtonTemplate();
                            button.setTitle(daypoint[x]);
                            button.setSubTitle(available);
                            List<EasyMap> actions = new ArrayList<>();
                            EasyMap bookAction = new EasyMap();
                            bookAction.setName(tanggaltitle);
                            bookAction.setValue(daypoint2[x] + "=" + date);
                            actions.add(bookAction);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                            String btnBuilder = buttonBuilder.build().toString();
                            sb.append(btnBuilder).append(SPLIT);
                            i++;
                        }

                    }
                    x++;
                    calendar.add(Calendar.DATE, +1);
                }
                String harijam = "";
                String summarySchedule = "";

                int hariangka = 0;
                for (int k = 0; k < leng; k++) {
                    JSONObject jObj = results.getJSONObject(k);
                    int daysnumber = jObj.getInt("day");
                    String fromtime = jObj.getString("from_time");
                    String totime = jObj.getString("to_time");
                    String dateF = fromtime.substring(0, 5);
                    String dateT = totime.substring(0, 5);
                    String jadwal = dateF + "-" + dateT;
                    switch (daysnumber) {
                        case 1:
                            harijam = "\nSenin : ";
                            break;
                        case 2:
                            harijam = "\nSelasa : ";
                            break;
                        case 3:
                            harijam = "\nRabu : ";
                            break;
                        case 4:
                            harijam = "\nKamis : ";
                            break;
                        case 5:
                            harijam = "\nJumat : ";
                            break;
                        case 6:
                            harijam = "\nSabtu : ";
                            break;
                        case 7:
                            harijam = "\nMinggu : ";
                            break;

                    }
                    if (daysnumber == hariangka) {
                        summarySchedule = summarySchedule + " & " + jadwal;
                    } else {
                        summarySchedule = summarySchedule + harijam + jadwal;
                        hariangka = daysnumber;
                    }

                }
                dnow = new Date();
                proctime.append(ft.format(dnow));
                String stringbuild = sb.toString();
                if (sb.toString().isEmpty() || sb.toString().equalsIgnoreCase("")) {

                    switch (konfirmtipe.toLowerCase()) {
                        case "area":
                            String apiGetDokter = appProp.getApiBaseUrl() + appProp.getApiDoctorbyhospitalIdSpecialist() + hosid + appProp.getApiDoctorbyhospitalIdSpecialist_2() + steptiga;
                            JSONObject jObjDoctor = GeneralExecuteAPI(apiGetDokter);
                            if (jObjDoctor.getString("status").equalsIgnoreCase("OK") && !jObjDoctor.getJSONArray("data").isNull(0)) {
                                JSONArray resultsArea = jObjDoctor.getJSONArray("data");
                                int lengArea = resultsArea.length();
                                if (lengArea > 10) {
                                    lengArea = 10;
                                }
                                for (int i = 0; i < lengArea; i++) {
                                    JSONObject jObj = resultsArea.getJSONObject(i);
                                    doctorId = jObj.getString("doctor_id");
                                    String hospitalId = jObj.getString("hospital_id");
                                    String doctorName = jObj.getString("name");
                                    String doctorSpecialist = jObj.getString("specialization_name");
                                    String doctorHospitals = jObj.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                                    //Buat Button
                                    ButtonTemplate button = new ButtonTemplate();
                                    button.setTitle(doctorSpecialist);
                                    button.setSubTitle(doctorHospitals);
                                    List<EasyMap> actions = new ArrayList<>();
                                    EasyMap bookAction = new EasyMap();
                                    bookAction.setName(doctorName);
                                    bookAction.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                                    actions.add(bookAction);
                                    button.setButtonValues(actions);
                                    ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                                    String btnBuilder = buttonBuilder.build().toString();
                                    sb.append(btnBuilder).append(SPLIT);
                                }
                            }
                            break;

                        case "spesialis":
                            String[] splitspesialis2 = counter.split(" ");
                            String spesial2 = splitspesialis2[0];

                            String apiGetDokter2 = appProp.getApiBaseUrl() + appProp.getApiDoctorbyhospitalIdSpecialist() + hosid + appProp.getApiDoctorbyhospitalIdSpecialist_2() + spesial2;
                            JSONArray results2 = GeneralExecuteAPI(apiGetDokter2).getJSONArray("data");
                            int lengSpec = results2.length();
                            if (lengSpec >= 10) {
                                lengSpec = 10;
                            }
                            for (int i = 0; i < lengSpec; i++) {
                                JSONObject jObj = results2.getJSONObject(i);
                                doctorId = jObj.getString("doctor_id");
                                String hospitalId = jObj.getString("hospital_id");
                                String doctorName = jObj.getString("name");
                                String doctorSpecialist = jObj.getString("specialization_name");
                                String doctorHospitals = jObj.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                                //Buat Button
                                ButtonTemplate button = new ButtonTemplate();
                                button.setTitle(doctorSpecialist);
                                button.setSubTitle(doctorHospitals);

                                List<EasyMap> actions = new ArrayList<>();
                                EasyMap bookAction = new EasyMap();
                                bookAction.setName(doctorName);
                                bookAction.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                                actions.add(bookAction);
                                button.setButtonValues(actions);
                                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                                String btnBuilder = buttonBuilder.build().toString();
                                sb.append(btnBuilder).append(SPLIT);
                            }
                            break;
                        case "nama":

                            String apiDokterNama = appProp.getApiBaseUrl() + appProp.getApiDoctorbyname() + tipe.toLowerCase();
                            JSONObject jobj3 = GeneralExecuteAPI(apiDokterNama);
                            JSONArray resultsNama = jobj3.getJSONArray("data");
                            int leng3 = resultsNama.length();
                            if (leng3 >= 15) {
                                leng3 = 15;
                            }
                            for (int i = 0; i < leng3; i++) {
                                JSONObject jObj = resultsNama.getJSONObject(i);
                                doctorId = jObj.getString("doctor_id");

                                apiGetDokter = appProp.getApiBaseUrl() + appProp.getApiDoctorbydoctorid() + doctorId;
                                JSONObject objApiDoctor = GeneralExecuteAPI(apiGetDokter);

                                JSONArray resultsDoctor = objApiDoctor.getJSONArray("data");
                                int lengDoctor = resultsDoctor.length();
                                for (int j = 0; j < lengDoctor; j++) {
                                    JSONObject jObj2 = resultsDoctor.getJSONObject(j);
                                    String doctorid = jObj2.getString("doctor_id");
                                    String doctorName = jObj2.getString("name");
                                    String hospitalId = jObj2.getString("hospital_id");
                                    String doctorSpecialist = jObj2.getString("specialization_name");
                                    String doctorHospitals = jObj2.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");

                                    //Buat Button
                                    ButtonTemplate button = new ButtonTemplate();
                                    button.setTitle(doctorSpecialist);
                                    button.setSubTitle(doctorHospitals);
                                    List<EasyMap> actions = new ArrayList<>();
                                    EasyMap bookAction = new EasyMap();
                                    bookAction.setName(doctorName);
                                    bookAction.setValue("dokter id " + doctorid + " di hos " + hospitalId);
                                    actions.add(bookAction);
                                    button.setButtonValues(actions);
                                    ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                                    String btnBuilder = buttonBuilder.build().toString();
                                    sb.append(btnBuilder).append(SPLIT);
                                }
                            }
                            break;

                    }

                    String dialog = "Mohon maaf jadwal Dokter pilihan Anda belum terdaftar di sistem kami. Silahkan pilih dokter lain :";
                    QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Atau pilih Menu di bawah ini.")
                            .add("Cek Jadwal Dokter", "lain dokter").add("Menu Utama", "menu utama").build();
                    output.put(OUTPUT, dialog + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
                    clearEntities.put("dokterid", "");
                    clearEntities.put("tanggalpesan", "");
                } else {
                    String getDoctorByDoctorId = appProp.getApiBaseUrl() + appProp.getApiDoctorbydoctorid() + dokid;
                    JSONObject results3 = GeneralExecuteAPI(getDoctorByDoctorId);
                    JSONArray arraydoc = results3.getJSONArray("data");
                    String doctorName = "";

                    if (results3.getString("status").equalsIgnoreCase("OK") && !results3.getJSONArray("data").isNull(0)) {
                        JSONObject jObj3 = arraydoc.getJSONObject(0);
                        doctorName = jObj3.getString("name");
                    }
                    String getNameHospitalbyID = appProp.getApiBaseUrl() + appProp.getApiHospitalbyId() + hosid;
                    JSONObject jsonobjHospitalName = GeneralExecuteAPI(getNameHospitalbyID);
                    JSONArray arrayhos = jsonobjHospitalName.getJSONArray("data");
                    String hospitalName = "";
                    if (results3.getString("status").equalsIgnoreCase("OK") && !results3.getJSONArray("data").isNull(0)) {
                        JSONObject jObj3 = arrayhos.getJSONObject(0);
                        hospitalName = jObj3.getString("name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                    }
                    String dialog1 = "Jadwal praktik " + doctorName + ", di " + hospitalName + ".\n";
//                    String dialog2 = "Silahkan klik jadwal dibawah untuk pembuatan janji temu:";
//                    QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Atau klik Menu untuk kembali ke halaman utama")
                    QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Apakah Anda ingin dibuatkan janji temu dokter?")
                            .add("Buat Janji", "xyz").add("Menu Utama", "menu utama").build();

                    output.put(OUTPUT, dialog1 + summarySchedule + SPLIT + quickReplyBuilder.string());
                }
            } else {

                switch (konfirmtipe.toLowerCase()) {
                    case "area":
                        String apiGetDokter = appProp.getApiBaseUrl() + appProp.getApiDoctorbyhospitalIdSpecialist() + hosid + appProp.getApiDoctorbyhospitalIdSpecialist_2() + steptiga;
                        JSONObject jObjDoctor = GeneralExecuteAPI(apiGetDokter);
                        if (jObjDoctor.getString("status").equalsIgnoreCase("OK") && !jObjDoctor.getJSONArray("data").isNull(0)) {
                            JSONArray resultsArea = jObjDoctor.getJSONArray("data");
                            int lengArea = resultsArea.length();
                            if (lengArea > 10) {
                                lengArea = 10;
                            }
                            for (int i = 0; i < lengArea; i++) {
                                JSONObject jObj = resultsArea.getJSONObject(i);
                                doctorId = jObj.getString("doctor_id");
                                String hospitalId = jObj.getString("hospital_id");
                                String doctorName = jObj.getString("name");
                                String doctorSpecialist = jObj.getString("specialization_name");
                                String doctorHospitals = jObj.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                                //Buat Button
                                ButtonTemplate button = new ButtonTemplate();
                                button.setTitle(doctorSpecialist);
                                button.setSubTitle(doctorHospitals);
                                List<EasyMap> actions = new ArrayList<>();
                                EasyMap bookAction = new EasyMap();
                                bookAction.setName(doctorName);
                                bookAction.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                                actions.add(bookAction);
                                button.setButtonValues(actions);
                                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                                String btnBuilder = buttonBuilder.build().toString();
                                sb.append(btnBuilder).append(SPLIT);
                            }
                        }
                        break;

                    case "spesialis":
                        String[] splitspesialis2 = stepsatu.split(" ");
                        String spesial2 = splitspesialis2[0];

                        if (spesial2.equalsIgnoreCase("Indonesia")) {
                            spesial2 = counter.toLowerCase();
                        }

                        String apiGetDokter2 = appProp.getApiBaseUrl() + appProp.getApiDoctorbyhospitalIdSpecialist() + hosid + appProp.getApiDoctorbyhospitalIdSpecialist_2() + spesial2;
                        JSONArray results2 = GeneralExecuteAPI(apiGetDokter2).getJSONArray("data");
                        int lengSpec = results2.length();
                        if (lengSpec >= 10) {
                            lengSpec = 10;
                        }
                        for (int i = 0; i < lengSpec; i++) {
                            JSONObject jObj = results2.getJSONObject(i);
                            doctorId = jObj.getString("doctor_id");
                            String hospitalId = jObj.getString("hospital_id");
                            String doctorName = jObj.getString("name");
                            String doctorSpecialist = jObj.getString("specialization_name");
                            String doctorHospitals = jObj.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
                            //Buat Button
                            ButtonTemplate button = new ButtonTemplate();
                            button.setTitle(doctorSpecialist);
                            button.setSubTitle(doctorHospitals);

                            List<EasyMap> actions = new ArrayList<>();
                            EasyMap bookAction = new EasyMap();
                            bookAction.setName(doctorName);
                            bookAction.setValue("dokter id " + doctorId + " di hos " + hospitalId);
                            actions.add(bookAction);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                            String btnBuilder = buttonBuilder.build().toString();
                            sb.append(btnBuilder).append(SPLIT);
                        }

                        break;

                    case "nama":
                        String apiDokterNama = appProp.getApiBaseUrl() + appProp.getApiDoctorbyname() + stepsatu.toLowerCase();
                        JSONObject jobj3 = GeneralExecuteAPI(apiDokterNama);
                        JSONArray resultsNama = jobj3.getJSONArray("data");
                        int leng3 = resultsNama.length();
                        if (leng3 >= 15) {
                            leng3 = 15;
                        }
                        for (int i = 0; i < leng3; i++) {
                            JSONObject jObj = resultsNama.getJSONObject(i);
                            doctorId = jObj.getString("doctor_id");

                            apiGetDokter = appProp.getApiBaseUrl() + appProp.getApiDoctorbydoctorid() + doctorId;
                            JSONObject objApiDoctor = GeneralExecuteAPI(apiGetDokter);

                            JSONArray resultsDoctor = objApiDoctor.getJSONArray("data");
                            int lengDoctor = resultsDoctor.length();
                            for (int j = 0; j < lengDoctor; j++) {
                                JSONObject jObj2 = resultsDoctor.getJSONObject(j);
                                String doctorid = jObj2.getString("doctor_id");
                                String doctorName = jObj2.getString("name");
                                String hospitalId = jObj2.getString("hospital_id");
                                String doctorSpecialist = jObj2.getString("specialization_name");
                                String doctorHospitals = jObj2.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");

                                //Buat Button
                                ButtonTemplate button = new ButtonTemplate();
                                button.setTitle(doctorSpecialist);
                                button.setSubTitle(doctorHospitals);
                                List<EasyMap> actions = new ArrayList<>();
                                EasyMap bookAction = new EasyMap();
                                bookAction.setName(doctorName);
                                bookAction.setValue("dokter id " + doctorid + " di hos " + hospitalId);
                                actions.add(bookAction);
                                button.setButtonValues(actions);
                                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                                String btnBuilder = buttonBuilder.build().toString();
                                sb.append(btnBuilder).append(SPLIT);
                            }
                        }
                        break;
                }
                String getDoctorByDoctorId = appProp.getApiBaseUrl() + appProp.getApiDoctorbydoctorid() + dokid;
                JSONObject results3 = GeneralExecuteAPI(getDoctorByDoctorId);
                JSONArray arraydoc = results3.getJSONArray("data");
                String doctorName = "";
                if (results3.getString("status").equalsIgnoreCase("OK") && !results3.getJSONArray("data").isNull(0)) {
                    JSONObject jObj3 = arraydoc.getJSONObject(0);
                    doctorName = jObj3.getString("name");
                }
                // Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle("");
                button.setSubTitle(" ");
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName("Hubungi Call Center");
                bookAction.setValue("callhospitalcenter");
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);
                String buttonbuilder1 = buttonBuilder.build();

                ButtonTemplate button2 = new ButtonTemplate();
                button2.setTitle("");
                button2.setSubTitle(" ");
                List<EasyMap> actions2 = new ArrayList<>();
                EasyMap bookAction2 = new EasyMap();
                bookAction2.setName("Hubungi RS Siloam");
                bookAction2.setValue("caritelponsiloam");
                actions2.add(bookAction2);
                button2.setButtonValues(actions2);
                ButtonBuilder buttonBuilder2 = new ButtonBuilder(button2);
                String buttonbuilder2 = buttonBuilder2.build();

                ButtonTemplate button3 = new ButtonTemplate();
                button3.setTitle("");
                button3.setSubTitle(" ");
                List<EasyMap> actions3 = new ArrayList<>();
                EasyMap bookAction3 = new EasyMap();
                bookAction3.setName("Menu Utama");
                bookAction3.setValue("menu utama");
                actions3.add(bookAction3);
                button3.setButtonValues(actions3);
                ButtonBuilder buttonBuilder3 = new ButtonBuilder(button3);
                String buttonbuilder3 = buttonBuilder3.build();
                String buttonAll = buttonbuilder1 + SPLIT + buttonbuilder2 + SPLIT + buttonbuilder3;

                String dialog1 = "Mohon maaf, jadwal " + doctorName + "  belum terdaftar di sistem kami.\n Bila Anda ingin membuat janji temu, dapat langsung menghubungi call center atau RS Siloam secara langsung.\n";
                String dialog2 = "Atau Anda dapat mencari dokter lain dengan mengklik nama dokter di bawah ini";
                String dialog3 = "Tidak menemukan dokter yang Anda cari?\n\n Silahkan coba lagi dengan mengetik nama dokter, sebagai contoh: \"Budi\" atau \"Budi Chandra\"";

                output.put(OUTPUT, dialog1 + SPLIT + buttonAll + SPLIT + dialog2 + SPLIT + sb.toString() + SPLIT + dialog3);
                clearEntities.put("dokterid", "");
                clearEntities.put("tanggalpesan", "");
            }
        }

//        output.put("extra", proctime.toString());
        extensionResult.setValue(output);
        extensionResult.setEntities(clearEntities);

        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        log.debug("newGetScheduleDoctorId() extensionResult: {}", new Gson().toJson(extensionResult));

        return extensionResult;
    }

    /**
     * Get Jam Praktek
     *
     * Method ini untuk membuat Carousel Jam Pratek Dokter Berdasarkan Tanggal
     * Pesan/Tanggal Praktek Dokter yang dipilih User
     *
     * String hospitalId = getEasyMapValueByName(extensionRequest, "step_dua");
     * : untuk get ID Hospital sesuai Dokter Pilihan user
     *
     * String tanggalpesan = getEasyMapValueByName(extensionRequest,
     * "tanggalpesan") : Untuk get Tanggal Praktek Dokter Pilihan User
     *
     * String doctorId = getEasyMapValueByName(extensionRequest, "dokterid"):
     * untuk get ID Doctor sesuai Dokter Pilihan User
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
     */
    @Override
    public ExtensionResult newGetJamPraktek(ExtensionRequest extensionRequest) {
        log.debug("newGetJamPraktek() extension request: {}", new Gson().toJson(extensionRequest, ExtensionRequest.class));
        ExtensionResult extensionResult = new ExtensionResult();
        extensionResult.setAgent(false);
        extensionResult.setRepeat(false);
        extensionResult.setSuccess(true);
        extensionResult.setNext(true);
        Map<String, String> clearEntities = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        Map<String, String> output = new HashMap<>();
        String doctorId = getEasyMapValueByName(extensionRequest, "dokterid");
        String[] iddokter = doctorId.split(" ");
        String dokid = "";
        if (iddokter[0].equalsIgnoreCase("id")) {
            dokid = iddokter[1];
        } else {
            dokid = iddokter[0];
        }
        String hospitalId = getEasyMapValueByName(extensionRequest, "step_dua");
        String[] idhos = hospitalId.split(" ");
        String hosid = "";
        if (idhos[0].equalsIgnoreCase("hos")) {
            hosid = idhos[1];
        } else {
            hosid = idhos[0];
        }

        String tanggalpesan = getEasyMapValueByName(extensionRequest, "tanggalpesan");

        String apiDokterName = appProp.getApiBaseUrl() + appProp.getApiDoctorbyname() + tanggalpesan.toLowerCase();
        JSONObject jobjDoctorName = GeneralExecuteAPI(apiDokterName);
        if (jobjDoctorName.getString("status").equalsIgnoreCase("OK") && !jobjDoctorName.getJSONArray("data").isNull(0)) {
            JSONArray resultsNama = jobjDoctorName.getJSONArray("data");
            int leng3 = resultsNama.length();
            if (leng3 >= 15) {
                leng3 = 15;
            }
            for (int j = 0; j < leng3; j++) {
                JSONObject jObj2 = resultsNama.getJSONObject(j);
                String doctorid = jObj2.getString("doctor_id");
                String doctorName = jObj2.getString("name");
                hospitalId = jObj2.getString("hospital_id");
                String doctorSpecialist = jObj2.getString("specialization_name");
                String doctorHospitals = jObj2.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");

                //Buat Button
                ButtonTemplate button = new ButtonTemplate();
                button.setTitle(doctorSpecialist);
                button.setSubTitle(doctorHospitals);
                List<EasyMap> actions = new ArrayList<>();
                EasyMap bookAction = new EasyMap();
                bookAction.setName(doctorName);
                bookAction.setValue("dokter id " + doctorid + " di hos " + hospitalId);
                actions.add(bookAction);
                button.setButtonValues(actions);
                ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                String btnBuilder = buttonBuilder.build().toString();
                sb.append(btnBuilder).append(SPLIT);
            }

            String dialog = "Berikut adalah beberapa dokter yang sesuai dengan pencarian Anda. Silahkan menggeser menu dari kiri ke kanan untuk menampilkan semua opsi.";
            sb.append("Tidak menemukan dokter yang Anda cari?\n Silahkan coba lagi dengan mengetik nama dokter, sebagai contoh: \"Budi\" atau \"Budi Chandra\"").append(SPLIT);
            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Bila terdapat kesulitan. Anda juga dapat memilih salah satu Opsi di bawah ini:")
                    .add("Hubungi Call Center", "callhospitalcenter").add("Hubungi RS Siloam", "caritelponsiloam").add("Menu Utama", "menu utama").build();
            sb.append(quickReplyBuilder.string());
            output.put(OUTPUT, dialog + SPLIT + sb.toString());
            clearEntities.put("step_satu", tanggalpesan);
            clearEntities.put("dokterid", "");
            clearEntities.put("tanggalpesan", "");
            extensionResult.setValue(output);
            extensionResult.setEntities(clearEntities);
            // Kondisi Ketika Dokter tidak tersedia dan User memilih Dokter Lain
        } else if (tanggalpesan.equalsIgnoreCase("lain dokter")) {
            clearEntities.put("tipe_pencarian", "");
            clearEntities.put("konfirmtipe", "");
            clearEntities.put("counter", "");
            clearEntities.put("step_satu", "");
            clearEntities.put("step_dua", "");
            clearEntities.put("step_tiga", "");
            clearEntities.put("dokterid", "");
            clearEntities.put("tanggalpesan", "");
            clearEntities.put("jampraktek", "");

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
            String dialog1 = "Silahkan pilih tipe pencarian dokter yang diinginkan.";
            output.put(OUTPUT, dialog1 + SPLIT + carouselBuilder.build());
            extensionResult.setValue(output);
            extensionResult.setEntities(clearEntities);

        } else if (tanggalpesan.contains("=")) {
            String[] jampilihan = tanggalpesan.split("=");
            String jam = jampilihan[0];
            String tanggal = jampilihan[1];
            String[] splittanggal = tanggal.split("-");
            String hari = splittanggal[2];
            String bulan = splittanggal[1];
            String tahun = splittanggal[0];
            String newdate = hari + "-" + bulan + "-" + tahun;

            //----------------//
//            String newsplit = "";
//            String newjam = jam.replace(" ", ":");
//            if (newjam.contains("t")) {
//                String[] jamsplit = newjam.split("t");
//                String split1 = jamsplit[0];
//                String split2 = jamsplit[1];
//                newsplit = split1 + " & " + split2;
//            } else {
//                newsplit = newjam;
//            }
            String getLiveHospital = appProp.getApiBaseUrl() + appProp.getApiHospitalLive();
            JSONArray araylivehos = GeneralExecuteAPI(getLiveHospital).getJSONArray("data");

            //Cek Live Hospital
            String hasilcekhospital = CekHospital(hosid, araylivehos);
            if (hasilcekhospital.equalsIgnoreCase("ada")) {

                String getSchedule = appProp.getApiBaseUrl() + appProp.getApiDoctorappointment() + hosid
                        + appProp.getApiDoctorappointment_2() + dokid + appProp.getApiDoctorappointment_3() + tanggal;
                JSONArray arraySchedule = GeneralExecuteAPI(getSchedule).getJSONArray("data");
                String carojam = CarouselJamPraktek(arraySchedule);
                String dialog = "Mohon klik waktu kunjungan pilihan Anda pada tanggal " + newdate;
                output.put(OUTPUT, dialog + SPLIT + carojam);
                extensionResult.setValue(output);
            } else {
                clearEntities.put("jampraktek", "10:00");
                clearEntities.put("namapasien", "admin");
                clearEntities.put("tanggallahir", "1999-01-01");
                clearEntities.put("notelp", "081318141513");
                clearEntities.put("confirm", "yes");
                extensionResult.setEntities(clearEntities);
            }
        } else {
//            String getDoctorByDoctorId = appProp.getApiBaseUrl() + appProp.getApiDoctorbydoctorid() + dokid;
//            JSONObject results3 = GeneralExecuteAPI(getDoctorByDoctorId);
//            JSONArray arraydoc = results3.getJSONArray("data");
            String doctorName = "";
//            String getNameHospitalbyID = appProp.getApiBaseUrl() + appProp.getApiHospitalbyId() + hosid;
//            JSONObject jsonobjHospitalName = GeneralExecuteAPI(getNameHospitalbyID);
//            JSONArray arrayhos = jsonobjHospitalName.getJSONArray("data");
            String hospitalName = "";
            String hasilcekcuti;
            String getLeaveDoctor = appProp.getApiBaseUrl() + appProp.getApiLeaveDoctor() + hosid + appProp.getApiLeaveDoctor_2() + dokid;
            JSONObject jobjCuti = GeneralExecuteAPI(getLeaveDoctor);

            String schedule = appProp.getApiBaseUrl() + appProp.getApiDoctorschedule() + hosid + appProp.getApiDoctorschedule_2() + dokid;
            JSONObject jobjSchedule = GeneralExecuteAPI(schedule);
            if (jobjSchedule.getString("status").equalsIgnoreCase("OK") && !jobjSchedule.getJSONArray("data").isNull(0)) {
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
                    if (jobjCuti.getString("status").equalsIgnoreCase("OK") && jobjCuti.getJSONArray("data").isNull(0)) {
                        hasilcekcuti = "tidak";
                    } else {
                        hasilcekcuti = CekCuti(jobjCuti, date);
                    }
//                String hasilcekcuti = CekCuti(dokid, testdate);
                    if (hasilcekcuti.equalsIgnoreCase("tidak")) {
                        String[] daypoint = new String[leng];
                        String[] daypoint2 = new String[leng];

                        //Cek Slot
                        String scheduleTime = appProp.getApiBaseUrl() + appProp.getApiDoctorappointment() + hosid
                                + appProp.getApiDoctorappointment_2() + dokid + appProp.getApiDoctorappointment_3() + date;
                        JSONObject jobj = GeneralExecuteAPI(scheduleTime);
                        if (jobj.getString("status").equalsIgnoreCase("OK") && !jobj.getJSONArray("data").isNull(0)) {
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
                                int daysnumber = jObj.getInt("day");
                                dayslist.add(daysnumber);
                                String fromtime = jObj.getString("from_time");
                                String totime = jObj.getString("to_time");
                                String dateF = fromtime.substring(0, 5);
                                String dateT = totime.substring(0, 5);
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

                        }
                        String NamaHari = NamaHari(hari);
                        String NamaBulan = NamaBulan(bulan);
                        String tanggaltitle = NamaHari + ", " + tanggal + " " + NamaBulan + " " + tahun;
                        if (dayslist.contains(kodeHari) && available.equalsIgnoreCase("Available")) {
                            //Buat Button
                            ButtonTemplate button = new ButtonTemplate();
                            button.setTitle(daypoint[x]);
                            button.setSubTitle(available);
                            List<EasyMap> actions = new ArrayList<>();
                            EasyMap bookAction = new EasyMap();
                            bookAction.setName(tanggaltitle);
                            bookAction.setValue(daypoint2[x] + "=" + date);
                            actions.add(bookAction);
                            button.setButtonValues(actions);
                            ButtonBuilder buttonBuilder = new ButtonBuilder(button);

                            String btnBuilder = buttonBuilder.build().toString();
                            sb.append(btnBuilder).append(SPLIT);
                            i++;

                        }

                    }
                    x++;
                    calendar.add(Calendar.DATE, +1);
                }

//                if (results3.getString("status").equalsIgnoreCase("OK") && !results3.getJSONArray("data").isNull(0)) {
//                    JSONObject jObj3 = arraydoc.getJSONObject(0);
                JSONObject jObj = results.getJSONObject(0);
                doctorName = jObj.getString("doctor_name");
//                }

//                if (results3.getString("status").equalsIgnoreCase("OK") && !results3.getJSONArray("data").isNull(0)) {
//                    JSONObject jObj3 = arrayhos.getJSONObject(0);
                hospitalName = jObj.getString("hospital_name").replace("Rumah Sakit Umum", "RSU").replace("Hospitals", "");
//                }
                String dialog1 = "Jadwal praktik " + doctorName + ", di " + hospitalName + ".\n\n Silahkan klik waktu kunjungan yang diinginkan:";
                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Atau klik Menu untuk kembali ke halaman utama")
                        //                QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Apakah Anda ingin dibuatkan janji temu dokter?")
                        .add("Menu Utama", "menu utama").build();

                output.put(OUTPUT, dialog1 + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
                clearEntities.put("tanggalpesan", "");
                extensionResult.setValue(output);
                extensionResult.setEntities(clearEntities);

            }

        }
        log.debug("newGetJamPraktek() extensionResult: {}", new Gson().toJson(extensionResult));
        return extensionResult;
    }

    /**
     * Tanya Nama Pasien
     *
     * Method untuk memberikan Question kepada User untuk menginputkan Nama User
     * Alasan di buatkan Method, Agar OUTPUT dari Method ini bisa dipakai ketika
     * Entitas 'namapasien' tidak sesuai Format, dan Entitas tersebut akan di
     * Clear. Lalu di tampilkan kembali Pertanyaan untuk menginputkan Nama User
     * Kembali.
     *
     * String jampraktek = getEasyMapValueByName(extensionRequest,
     * "jampraktek"): untuk Jam Praktek Pilihan User atau Typing From User
     *
     * String doctorId = getEasyMapValueByName(extensionRequest, "dokterid"):
     * untuk get ID Doctor sesuai Dokter Pilihan User
     *
     * String hospitalId = getEasyMapValueByName(extensionRequest, "step_dua"):
     * untuk get ID Doctor sesuai Dokter Pilihan User
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
     */
    @Override
    public ExtensionResult tanyaNamaPasien(ExtensionRequest extensionRequest
    ) {
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

//            String newsplit = "";
//            String newjam = jam.replace(" ", ":");
//            if (newjam.contains("t")) {
//                String[] jamsplit = newjam.split("t");
//                String split1 = jamsplit[0];
//                String split2 = jamsplit[1];
//                newsplit = split1 + " & " + split2;
//            } else {
//                newsplit = newjam;
//            }
            String getLiveHospital = appProp.getApiBaseUrl() + appProp.getApiHospitalLive();
            JSONArray araylivehos = GeneralExecuteAPI(getLiveHospital).getJSONArray("data");

            //Cek Live Hospital
            String hasilcekhospital = CekHospital(hosid, araylivehos);
            if (hasilcekhospital.equalsIgnoreCase("ada")) {

                String getSchedule = appProp.getApiBaseUrl() + appProp.getApiDoctorappointment() + hosid
                        + appProp.getApiDoctorappointment_2() + dokid + appProp.getApiDoctorappointment_3() + tanggal;
                JSONArray arraySchedule = GeneralExecuteAPI(getSchedule).getJSONArray("data");
                String carojam = CarouselJamPraktek(arraySchedule);
                String dialog = "Mohon klik waktu kunjungan pilihan Anda pada tanggal " + newdate;
                output.put(OUTPUT, dialog + SPLIT + carojam);
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
            String dialog1 = "Baik, Silvia akan membuatkan janji temu dokter untuk Anda pada waktu yang telah Anda pilih. \n Silahkan ketik nama lengkap Anda sesuai KTP untuk melanjutkan.";
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
     * Validasi Nama Pasien
     *
     * Method untuk mengecek Apakah benar Format/yang di ketikan User merupakan
     * Nama Dan tidak Mengandung Angka atau Karakter
     *
     * String namapasien = getEasyMapValueByName(extensionRequest,
     * "namapasien"): untuk get Nama Pasien yang di inputkan Oleh User, jika
     * Menggandung Angka Maka akan melakukan Bertanya Kembali
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
     */
    @Override
    public ExtensionResult validasiNamaPasien(ExtensionRequest extensionRequest
    ) {
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
            String dialog1 = "Terima kasih " + namapasien + ". Mohon ketik tanggal lahir Anda dengan format (dd/mm/yyyy), sebagai contoh : 31/12/1999";
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
     * Post Create Appointment
     *
     * Method untuk membentuk Body Message/Formatnya dan lalu di Post Body
     * Message Appointment tersebut ke API Siloam.
     *
     * String stepdua = getEasyMapValueByName(extensionRequest, "step_dua"); :
     * untuk get ID Hospital sesuai Dokter Pilihan yang mereplace Data Lama(Jika
     * Ada)
     *
     * String doctorId = getEasyMapValueByName(extensionRequest, "dokterid"):
     * untuk get ID Doctor sesuai Dokter Pilihan User
     *
     * String namapasien = getEasyMapValueByName(extensionRequest,
     * "namapasien"): untuk get Nama Pasien Inputan User
     *
     * String tanggallahir = getEasyMapValueByName(extensionRequest,
     * "tanggallahir"): untuk get Tanggl Lahir User by Inputan User
     *
     * String notelp = getEasyMapValueByName(extensionRequest, "notelp"): untuk
     * get Nomor Telepon User by Inputan User
     *
     * String tanggalPesan = getEasyMapValueByName(extensionRequest,
     * "tanggalpesan"): untuk get Tanggal Praktek Dokter by Pilihan User
     *
     * String jamPraktek = getEasyMapValueByName(extensionRequest,
     * "jampraktek"): untuk get Jam Praktek Dokter by Pilihan User
     *
     *
     * @param extensionRequest Data Request
     * @return Body JSON to Chatbot
     */
    @Override
    public ExtensionResult doPostCreateAppointment(ExtensionRequest extensionRequest
    ) {
        log.debug("doPostCreateAppointment() extension request: {}", new Gson().toJson(extensionRequest, ExtensionRequest.class));

        Map<String, String> output = new HashMap<>();
        ExtensionResult extensionResult = new ExtensionResult();
        StringBuilder sb = new StringBuilder();
        Appointment appointmentJson = new Appointment();

        String email = "appointment@gmail.com";
        String dokterid = getEasyMapValueByName(extensionRequest, "dokterid");
        String hospitalId = getEasyMapValueByName(extensionRequest, "step_dua");
        String namapasien = getEasyMapValueByName(extensionRequest, "namapasien");
        String tanggallahir = getEasyMapValueByName(extensionRequest, "tanggallahir");
        String notelp = getEasyMapValueByName(extensionRequest, "notelp");
        String tanggalPesan = getEasyMapValueByName(extensionRequest, "tanggalpesan");
        String jamPraktek = getEasyMapValueByName(extensionRequest, "jampraktek");

        String jammenit = jamPraktek.replace(" ", ":");

        String[] iddokter = dokterid.split(" ");
        String dokid = iddokter[0];
        if (dokid.equalsIgnoreCase("id")) {
            dokid = iddokter[1];
        }

        String[] idhos = hospitalId.split(" ");
        String hospital = idhos[0];
        if (hospital.equalsIgnoreCase("hos")) {
            hospital = idhos[1];

        }

        String[] tanggal = tanggalPesan.split("=");
        String date = tanggal[1];

        String getLiveHospital = appProp.getApiBaseUrl() + appProp.getApiHospitalLive();
        JSONArray araylivehos = GeneralExecuteAPI(getLiveHospital).getJSONArray("data");

        //Cek Live Hospital
        String hasilcekhospital = CekHospital(hospital, araylivehos);
        if (hasilcekhospital.equalsIgnoreCase("tidak")) {
            String getNameHospital = appProp.getApiBaseUrl() + appProp.getApiHospitalbyId() + hospital;
            JSONObject jsonobjHospital = GeneralExecuteAPI(getNameHospital);
            JSONArray arrayHost = jsonobjHospital.getJSONArray("data");
            JSONObject jSONObject = arrayHost.getJSONObject(0);
            String hosname = jSONObject.getString("name");

            String dialog1 = "Mohon maaf, namun {bot_name} belum dapat membuatkan janji temu untuk " + hosname + ".";
            String dialog2 = "Mohon menghubungi call center kami di 1-500-181 atau RS Siloam secara langsung.";
            QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Atau klik MENU untuk kembali ke halaman utama.")
                    .add("Menu Utama", "menu utama").build();
            output.put(OUTPUT, dialog1 + SPLIT + dialog2 + SPLIT + quickReplyBuilder.string());
        } else {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            okHttpUtil.init(true);
            String getScheduleId = appProp.getApiBaseUrl() + appProp.getApiDoctorappointment() + hospital
                    + appProp.getApiDoctorappointment_2() + dokid + appProp.getApiDoctorappointment_3() + date;
            JSONArray results = GeneralExecuteAPI(getScheduleId).getJSONArray("data");
            int leng = results.length();
            String idschedule = "";
            for (int i = 0; i < leng; i++) {
                JSONObject jObj = results.getJSONObject(i);
                String fromtime = jObj.getString("from_time");
                String newfromtime = fromtime.substring(0, 5);
                String scheduleid = jObj.getString("schedule_id");
                if (jammenit.equals(newfromtime)) {
                    idschedule = scheduleid;
                    break;
                }
            }

            appointmentJson.setChannelId(appProp.getChannelId());
            appointmentJson.setAppointmentDate(date);
            appointmentJson.setAppointmentFromTime(jammenit);
            appointmentJson.setScheduleId(idschedule);
            appointmentJson.setHospitalId(hospital);
            appointmentJson.setDoctorId(dokid);
            appointmentJson.setUserId(appProp.getUserId());
            appointmentJson.setIsWaitingList(Boolean.parseBoolean(appProp.getIsWaitingList()));
            appointmentJson.setName(namapasien);
            appointmentJson.setBirthDate(tanggallahir);
            appointmentJson.setPhoneNumber1(notelp);
            appointmentJson.setEmailAddress("");
            appointmentJson.setSource(appProp.getSource());
            appointmentJson.setUserName(appProp.getUsernameBook());

            JSONObject ca = new JSONObject(appointmentJson);
            String appointment = ca.toString();
            try {
                String url = appProp.getApiBaseUrl() + appProp.getCreateAppointment();
                System.out.println(appointment);
                System.out.println(url);

                // Create Body JSON For POST to API Appointment SILOAM //
                RequestBody body = RequestBody.create(JSON, appointment);
                Request request = new Request.Builder().url(url).post(body).addHeader("Content-Type", "application/json").build();
                Response response = okHttpUtil.getClient().newCall(request).execute();
                JSONObject jsonobj = new JSONObject(response.body().string());

                if (jsonobj.getString("message").equalsIgnoreCase("Success - Post Appointment")) {
                    JSONObject jObj = jsonobj.getJSONObject("data");
                    String appointment_date = jObj.getString("appointment_date");
                    String fromtime = jObj.getString("appointment_from_time");
                    String totime = jObj.getString("appointment_to_time");
                    String patient_name = jObj.optString("contact_name");
                    String patient_phone = jObj.optString("phone_number");
                    String doctor_name = jObj.getString("doctor_name");
                    String hospital_name = jObj.getString("hospital_name");

                    fromtime = fromtime.substring(0, 5);
                    totime = totime.substring(0, 5);
                    String appointment_time = fromtime + " - " + totime;

                    // Create Layout Summary After Post Appointment //
                    String dialog1 = "Terima kasih, " + namapasien + "\n {bot_name} telah berhasil mendaftarkan janji temu Anda.";
                    sb.append("Berikut ringkasan informasi untuk janji temu Anda: \n");
                    sb.append("Nama : " + patient_name + "\n");
                    sb.append("Nomor Ponsel : " + patient_phone + "\n");
                    sb.append("Nama Dokter : " + doctor_name + "\n");
                    sb.append("Tanggal : " + appointment_date + "\n");
                    sb.append("Waktu : " + appointment_time + "\n");
                    sb.append("Lokasi : " + hospital_name + "\n\n");
                    sb.append("Untuk melihat status janji temu Anda, silahkan download aplikasi MySiloam di Google store & Apps Store.");
                    QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Apakah ada yang {bot_name} dapat bantu lagi?")
                            .add("Iya", "Menu Utama").add("Tidak", "endappointment").build();

                    output.put(OUTPUT, dialog1 + SPLIT + sb.toString() + SPLIT + quickReplyBuilder.string());
                } else {
                    String getNameHospital = appProp.getApiBaseUrl() + appProp.getApiHospitalbyId() + hospital;
                    JSONObject jsonobjHospital = GeneralExecuteAPI(getNameHospital);
                    JSONArray arrayHost = jsonobjHospital.getJSONArray("data");
                    JSONObject jSONObject = arrayHost.getJSONObject(0);
                    String hosname = jSONObject.getString("name");

                    String dialog1 = "Mohon maaf, namun {bot_name} belum dapat membuatkan janji temu untuk " + hosname + ".";
                    String dialog2 = "Mohon menghubungi call center kami di 1-500-181 atau RS Siloam secara langsung.";
                    QuickReplyBuilder quickReplyBuilder = new QuickReplyBuilder.Builder("Klik MENU untuk kembali ke menu utama.")
                            .add("Menu Utama", "menu utama").build();
                    output.put(OUTPUT, dialog1 + SPLIT + dialog2 + SPLIT + quickReplyBuilder.string());

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
        log.debug("doPostCreateAppointment() extension request: {}", new Gson().toJson(extensionResult));

        return extensionResult;
    }

//    private Object Logger(Level SEVERE, String name, IOException ex) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
