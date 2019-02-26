/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imi.dolphin.sdkwebservice.model;

/**
 *
 * @author Nande
 */
public class MonthBuilder {
    public String toMonthNumber(String month){
        String result = "";
        switch(month){
            case "jan":
            case "Jan":
            case "januari":
            case "Januari":
            case "january":
            case "January":
                result = "01";
                break;
            case "feb":
            case "Feb":
            case "februari":
            case "Februari":
            case "february":
            case "February":
                result = "02";
                break;
            case "mar":
            case "Mar":
            case "maret":
            case "Maret":
            case "march":
            case "March":
                result = "03";
                break;
            case "apr":
            case "Apr":
            case "april":
            case "April":
                result = "04";
                break;
            case "mei":
            case "Mei":
            case "may":
            case "May":
                result = "05";
                break;
            case "jun":
            case "Jun":
            case "juni":
            case "Juni":
            case "june":
            case "June":
                result = "06";
                break;
            case "jul":
            case "Jul":
            case "juli":
            case "Juli":
            case "july":
            case "July":
                result = "07";
                break;
            case "aug":
            case "Aug":
            case "agustus":
            case "Agustus":
            case "august":
            case "August":
                result = "08";
                break;
            case "sep":
            case "Sep":
            case "september":
            case "September":
                result = "09";
                break;
            case "oct":
            case "Oct":
            case "okt":
            case "Okt":
            case "oktober":
            case "Oktober":
            case "october":
            case "October":
                result = "10";
                break;
            case "nov":
            case "Nov":
            case "november":
            case "November":
                result = "11";
                break;
            case "des":
            case "Des":
            case "dec":
            case "Dec":
            case "desember":
            case "Desember":
            case "december":
            case "December":
                result = "12";
                break;
        }
        return result;
    }
}
