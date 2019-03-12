package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * 主播开启 酒桶任务
 */
public class LiveBeerBean implements Serializable {

    /**
     * cask : 1
     * count : 10
     * ctype : 1
     * end_time : 180
     * pnum : 0
     * uid : 12447
     */

    private int cask;
    private int count;
    private int ctype;
    private int end_time;
    private int pnum;
    private String uid;
    private String info;
    private String liveid;
    private String id;
    public int getCask() {
        return cask;
    }

    public void setCask(int cask) {
        this.cask = cask;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCtype() {
        return ctype;
    }

    public void setCtype(int ctype) {
        this.ctype = ctype;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public int getPnum() {
        return pnum;
    }

    public void setPnum(int pnum) {
        this.pnum = pnum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLiveid() {
        return liveid;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

