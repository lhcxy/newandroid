package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/8/12.
 */

public class LiveRecordBean implements Serializable {
    private String id;
    private String uid;
    private String nums;
    private String starttime;
    private String endtime;
    private String title;
    private String city;
    private String datestarttime;
    private String dateendtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDatestarttime() {
        return datestarttime;
    }

    public void setDatestarttime(String datestarttime) {
        this.datestarttime = datestarttime;
    }

    public String getDateendtime() {
        return dateendtime;
    }

    public void setDateendtime(String dateendtime) {
        this.dateendtime = dateendtime;
    }
}
