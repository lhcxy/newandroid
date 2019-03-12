package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/8/22.
 * 送礼物的实体类
 */

public class ReceiveGiftBean  implements Serializable {
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
    private String evensend;
    private int mCount=1;
    private String liangName;
    private int vipType;
    private LiveBeerBean cask;

    private String casktoken;

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

    public String getEvensend() {
        return evensend;
    }

    public void setEvensend(String evensend) {
        this.evensend = evensend;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }


    public String getLiangName() {
        return liangName;
    }

    public void setLiangName(String liangName) {
        this.liangName = liangName;
    }

    public int getVipType() {
        return vipType;
    }

    public void setVipType(int vipType) {
        this.vipType = vipType;
    }

    public LiveBeerBean getCask() {
        return cask;
    }

    public void setCask(LiveBeerBean cask) {
        this.cask = cask;
    }

    public String getCasktoken() {
        return casktoken;
    }

    public void setCasktoken(String casktoken) {
        this.casktoken = casktoken;
    }
}
