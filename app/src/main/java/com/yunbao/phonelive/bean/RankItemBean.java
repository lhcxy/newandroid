package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2018/2/2.
 */

public class RankItemBean implements Serializable {
    //    private String totalcoin; //贡献金币
    private String id; //用户id
    private String user_nicename; //用户昵称
    private String avatar_thumb; //用户头像


    //    private int levelAnchor;
//    private int level;


    private String total;
    private String avatar;
    //    private String sex;
    private int type;

    private LiveBean liveInfo;
    private String jiangli="";
    private int listTimeType; //0= 周榜 1=月榜
    private int level;
    /**
     * total : 30003
     * ototal : null
     * avatar : /default.jpg
     * sex : 2
     * type : 1
     */


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUid() {
        return id;
    }

    public void setUid(String uid) {
        this.id = uid;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }


    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public LiveBean getLiveInfo() {
        return liveInfo;
    }

    public void setLiveInfo(LiveBean liveInfo) {
        this.liveInfo = liveInfo;
    }

    public String getJiangli() {
        return jiangli;
    }

    public void setJiangli(String jiangli) {
        this.jiangli = jiangli;
    }

    public int getListTimeType() {
        return listTimeType;
    }

    public void setListTimeType(int listTimeType) {
        this.listTimeType = listTimeType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
