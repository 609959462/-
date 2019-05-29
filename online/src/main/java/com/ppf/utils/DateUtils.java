package com.ppf.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateUtils {

    public   static  final String FORMATE_DEFAULT="yyyy-MM-dd HH:mm:ss";

    /**
     * Date-->string
     * */

    public static  String  dateToStr(Date date, String formate){

        if (date == null){
            return null;
        }

        DateTime dateTime=new DateTime(date);
        return dateTime.toString(formate);
    }
    public static  String  dateToStr(Date date){

        DateTime dateTime=new DateTime(date);
        return dateTime.toString(FORMATE_DEFAULT);
    }

    /**
     * string-->Date
     * */
    public static  Date  strToDate(String str){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(FORMATE_DEFAULT);
        DateTime dateTime=dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }
    public static  Date  strToDate(String str,String format){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(format);
        DateTime dateTime=dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }
}
