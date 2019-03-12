package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class LiveRedPaperBean implements Serializable {

    /**
     * _method_ : RedPaper
     * action : 0
     * koul : 111
     * time : 60
     * title : 111兔尾巴
     * type : 2
     * uid : 12450
     */

    private String koul;
    private int time;
    private String title;
    private int type;
    private String uid;
    private String name="";
    public String getKoul() {
        return koul;
    }

    public void setKoul(String koul) {
        this.koul = koul;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
