package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/8/22.
 * 发弹幕的实体类
 */

public class ReceiveDanMuBean implements Serializable {
    private String uid;
    private String giftid;
    private String giftcount;
    private String totalcoin;
    private String giftname;
    private String gifticon;
    private int level;
    private String coin;
    private String votestotal;
    private String uname;
    private String uhead;
    private String content;
    private int danmuType;

    private String liveName;

    public int getDanmuType() {
        return danmuType;
    }

    public void setDanmuType(int danmuType) {
        this.danmuType = danmuType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGiftid() {
        return giftid;
    }

    public void setGiftid(String giftid) {
        this.giftid = giftid;
    }

    public String getGiftcount() {
        return giftcount;
    }

    public void setGiftcount(String giftcount) {
        this.giftcount = giftcount;
    }

    public String getTotalcoin() {
        return totalcoin;
    }

    public void setTotalcoin(String totalcoin) {
        this.totalcoin = totalcoin;
    }

    public String getGiftname() {
        return giftname;
    }

    public void setGiftname(String giftname) {
        this.giftname = giftname;
    }

    public String getGifticon() {
        return gifticon;
    }

    public void setGifticon(String gifticon) {
        this.gifticon = gifticon;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getVotestotal() {
        return votestotal;
    }

    public void setVotestotal(String votestotal) {
        this.votestotal = votestotal;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUhead() {
        return uhead;
    }

    public void setUhead(String uhead) {
        this.uhead = uhead;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLiveName() {
        return liveName;
    }

    public void setLiveName(String liveName) {
        this.liveName = liveName;
    }
}
