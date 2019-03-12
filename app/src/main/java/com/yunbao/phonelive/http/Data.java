package com.yunbao.phonelive.http;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by cxf on 2017/8/5.
 */

public class Data implements Serializable {
    private int code;
    private String msg;
    private String[] info;
    private String casktoken;
    private int gettype = 0;
    private int total = 0;
    private int cash = 0;

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String[] getInfo() {
        return info;
    }

    public void setInfo(String[] info) {
        this.info = info;
    }

    public String getCasktoken() {
        return casktoken;
    }

    public void setCasktoken(String casktoken) {
        this.casktoken = casktoken;
    }

    @Override
    public String toString() {
        return "Data{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", info=" + Arrays.toString(info) +
                ", casktoken='" + casktoken + '\'' +
                '}';
    }

    public int getGettype() {
        return gettype;
    }

    public void setGettype(int gettype) {
        this.gettype = gettype;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
