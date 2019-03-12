package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class CountryCodeBean implements Serializable {

    /**
     * en : Angola
     * zh : 安哥拉
     * locale : AO
     * code : 244
     */

    private String en;
    private String zh;
    private String locale;
    private int code;

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getZh() {
        return zh;
    }

    public void setZh(String zh) {
        this.zh = zh;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
