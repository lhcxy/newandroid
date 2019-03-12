package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/8/22.
 */

public class LiveChatBean implements Serializable {

    public static final int NORMAL = 0;
    public static final int SYSTEM = 1;
    public static final int GIFT = 2;
    public static final int ENTER_ROOM = 3;

    private String id;
    private String user_nicename;
    private int level;
    private String content;
    private int heart;
    private int type; //0是普通消息  1是系统消息 2是礼物消息
    private String liangName;
    private int vipType;

    private String gifticon;
    private String giftid;
    private int giftcount;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getGifticon() {
        return gifticon;
    }

    public void setGifticon(String gifticon) {
        this.gifticon = gifticon;
    }

    public String getGiftid() {
        return giftid;
    }

    public void setGiftid(String giftid) {
        this.giftid = giftid;
    }

    public int getGiftcount() {
        return giftcount;
    }

    public void setGiftcount(int giftcount) {
        this.giftcount = giftcount;
    }
}
