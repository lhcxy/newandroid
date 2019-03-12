package com.yunbao.phonelive.ui.tools;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");

        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getContributionStr(String str) {
        if (isNumeric(str)) {
            if (str.length() > 4) {
                Long aDouble = Long.valueOf(str);
                if (aDouble < 1000000L) {
                    return String.valueOf(new BigDecimal(aDouble / 10000.0).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()) + "万";
                } else if (aDouble > 1000000L) {
                    return String.valueOf(new BigDecimal(aDouble / 1000000.0).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()) + "百万";
                }
            }
        }
        return str;
    }
}
