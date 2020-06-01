package me.violinsolo.testlibsapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/29 2:26 PM
 * @updateAt 2020/5/29 2:26 PM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class DateUtil {
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static String getCurrentTimeFormat() {
        String fromTimeZone = "GMT+8";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);//设置日期格式
        Date date = new Date();
        format.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
        String chinaDate = format.format(date);// new Date()为获取当前系统时间，也可使用当前时间戳
        return chinaDate;
    }

    public static String formatMillisToGMT(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(date);
    }

    public static String formatMillisToGMT8(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return simpleDateFormat.format(date);
    }

    public static String getDay(long milliseconds, TimeZone tz) {
        if (tz == null) {
            tz = TimeZone.getTimeZone("UTC+8");
        }
        Date date = new Date(milliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        simpleDateFormat.setTimeZone(tz);
        return simpleDateFormat.format(date);
    }

    /**
     * 动态响应中英文日期显示格式。
     * @param milliseconds
     * @param tz
     * @return  "18:39"
     */
    public static String getHourMinuteADV(long milliseconds, TimeZone tz) {
        if (tz == null) {
            tz = TimeZone.getDefault();
        }
        String fmt = "";
        Locale dft = Locale.getDefault();
        if (dft.getLanguage().equals(Locale.CHINESE.getLanguage())) {
            fmt = "HH:mm";  //18:39
        }else {
            fmt = "HH:mm";  //18:39
        }

        Date date = new Date(milliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt, Locale.getDefault());
        simpleDateFormat.setTimeZone(tz);
        return simpleDateFormat.format(date);
    }

    /**
     * 动态响应中英文日期显示格式。
     * @param milliseconds
     * @param tz
     * @return  "yyyy年MM月dd日 星期二", "Tus, Jun dd, yyyy"
     */
    public static String getDayADV(long milliseconds, TimeZone tz) {
        if (tz == null) {
            tz = TimeZone.getDefault();
        }
        String fmt = "";
        Locale dft = Locale.getDefault();
        if (dft.getLanguage().equals(Locale.CHINESE.getLanguage())) {
            fmt = "yyyy年MM月dd日 E";  //2020年05月29日 星期五
        }else {
            fmt = "E, MMM dd, yyyy";  //Fri, May 29, 2020
        }

        Date date = new Date(milliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt, Locale.getDefault());
        simpleDateFormat.setTimeZone(tz);
        return simpleDateFormat.format(date);
    }
}
