package com.yunbao.phonelive.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cxf on 2017/10/10.
 * 把long转成时间字符串，环信聊天用到
 */

public class DateUtil {

    private static Calendar sCalendar;
    private static SimpleDateFormat sSdf;

    static {
        sCalendar = Calendar.getInstance();
        sSdf = new SimpleDateFormat("MM-dd HH:mm");
    }

    public static String getDateString(long time) {
        sCalendar.setTimeInMillis(time);
        return sSdf.format(sCalendar.getTime());
    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分00秒"）	 * 	 * @param time	 * @return
     */
    public static String getYMDHMByTimeStamp(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        @SuppressWarnings("unused")
                int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    public static String getYMDHMByTimeStamp(long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String times = sdr.format(new Date(time * 1000L));
        return times;
    }
}
