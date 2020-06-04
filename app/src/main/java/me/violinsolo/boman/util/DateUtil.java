package me.violinsolo.boman.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
     * @param milliseconds timestamp in milliseconds value.
     * @param tz null means default timezone to be used.
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
     * @param milliseconds timestamp in milliseconds value.
     * @param tz null means default timezone to be used.
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

    // 为传输做准备的，可读时间[]和时间戳的转化
    /**
     * 将 毫秒时间戳 转化为 日期数组
     * 时间戳：1591259057447
     * 真实时间：2020-06-04T16:24:17:447Z
     * 日期数组：[2020, 6, 4, 16, 24, 17]
     *
     * @param milliseconds timestamp in milliseconds value.
     * @param tz null means default timezone to be used.
     * @return
     */
    public static int[] castIntArray2Millis(long milliseconds, TimeZone tz) {
        if (tz == null) {
            tz = TimeZone.getDefault();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        calendar.setTimeZone(tz);
        int year = calendar.get(Calendar.YEAR);
        // 取月份要加1
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        // 1-7分别代表 -- 星期日,星期一,星期二,星期三,星期四,星期五,星期六
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        return new int[]{year, month, day, hour, minute, second};
    }

    /**
     * 将 日期数组 转化为 毫秒时间戳
     * 日期数组：[2020, 6, 4, 16, 24, 17]
     * 2020-06-04T16:24:17:469Z
     * 时间戳：1591259057469
     *
     * @param timeArr
     * @param tz
     * @return
     */
    public static long castIntArray2Millis(int[] timeArr, TimeZone tz) {
        if (tz == null) {
            tz = TimeZone.getDefault();
        }
        if (timeArr.length != 6) {
            throw new IllegalArgumentException("Time Array length not format.");
        }

        Calendar calendar = Calendar.getInstance();
        // 存月份要减1
        calendar.set(timeArr[0], timeArr[1]-1, timeArr[2], timeArr[3], timeArr[4], timeArr[5]);
        calendar.setTimeZone(tz);

        return calendar.getTimeInMillis();
    }
}
