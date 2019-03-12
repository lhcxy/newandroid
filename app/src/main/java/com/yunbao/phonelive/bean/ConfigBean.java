package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/8/5.
 */

public class ConfigBean implements Serializable {
    private String apk_ver;
    private String apk_url;
    private String apk_des;
    private String wx_siteurl;
    private String app_android;
    private String share_title;
    private String share_des;
    private String name_coin;
    private String name_votes;
    private int enter_tip_level;
    private String maintain_switch;
    private String maintain_tips;
    private String[] live_time_coin;
    private String[] login_type;
    private String[][] live_type;
    private String[] share_type;
    private String im_server;


    public String getIm_server() {
        return im_server;
    }

    public void setIm_server(String im_server) {
        this.im_server = im_server;
    }

    public String getApk_ver() {
        return apk_ver;
    }

    public void setApk_ver(String apk_ver) {
        this.apk_ver = apk_ver;
    }

    public String getApk_url() {
        return apk_url;
    }

    public void setApk_url(String apk_url) {
        this.apk_url = apk_url;
    }

    public String getWx_siteurl() {
        return wx_siteurl;
    }

    public String getApk_des() {
        return apk_des;
    }

    public void setApk_des(String apk_des) {
        this.apk_des = apk_des;
    }

    public void setWx_siteurl(String wx_siteurl) {
        this.wx_siteurl = wx_siteurl;
    }

    public String getApp_android() {
        return app_android;
    }

    public void setApp_android(String app_android) {
        this.app_android = app_android;
    }

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_des() {
        return share_des;
    }

    public void setShare_des(String share_des) {
        this.share_des = share_des;
    }

    public String getName_coin() {
        return "探球币";
    }

    public void setName_coin(String name_coin) {
        this.name_coin = name_coin;
    }

    public String getName_votes() {
        return "探球币";
    }

    public void setName_votes(String name_votes) {
        this.name_votes = name_votes;
    }

    public int getEnter_tip_level() {
        return enter_tip_level;
    }

    public void setEnter_tip_level(int enter_tip_level) {
        this.enter_tip_level = enter_tip_level;

    }

    public String getMaintain_switch() {
        return maintain_switch;
    }

    public void setMaintain_switch(String maintain_switch) {
        this.maintain_switch = maintain_switch;
    }

    public String getMaintain_tips() {
        return maintain_tips;
    }

    public void setMaintain_tips(String maintain_tips) {
        this.maintain_tips = maintain_tips;
    }

    public String[] getLive_time_coin() {
        return live_time_coin;
    }

    public void setLive_time_coin(String[] live_time_coin) {
        this.live_time_coin = live_time_coin;
    }

    public String[] getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String[] login_type) {
        this.login_type = login_type;
    }

    public String[][] getLive_type() {
        return live_type;
    }

    public void setLive_type(String[][] live_type) {
        this.live_type = live_type;
    }

    public String[] getShare_type() {
        return share_type;
    }

    public void setShare_type(String[] share_type) {
        this.share_type = share_type;
    }
}
