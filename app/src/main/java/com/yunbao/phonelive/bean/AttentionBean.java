package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/8/11.
 * 搜索结果列表和主页关注列表 数据实体类
 */

public class AttentionBean implements Serializable {
    private String id;
    private String user_nicename;
    private String avatar;
    private int sex;
    private String signature;
    private float votestotal;
    private int level;
    private int level_anchor;
    private int isattention;
    private String bTitle;
    /**
     * uid : 12350
     * avatar_thumb : http://qiniutxdemo.yunbaozhibo.com/20180723/5b55af6bbcf2f.jpg
     * title : 辣眼睛
     * stream : 12350_1532342138
     * pull : http://p3gl6e2wo.bkt.clouddn.com/18.3.mp4
     * thumb : http://qiniutxdemo.yunbaozhibo.com/20180723/5b55af6bbcf2f.jpg
     * isvideo : 1
     * fans : 粉丝 · 10
     * firenum : 3689900
     * islive : 1
     * isattention : 0
     */

    private String uid;
    private String avatar_thumb;
    private String title;
    private String stream;
    private String pull;
    private String thumb;
    private String isvideo;
    private String fans;
    private int firenum;
    private int islive;
    private String anyway;

    public String getAnyway() {
        return anyway;
    }

    public void setAnyway(String anyway) {
        this.anyway = anyway;
    }

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public float getVotestotal() {
        return votestotal;
    }

    public void setVotestotal(float votestotal) {
        this.votestotal = votestotal;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel_anchor() {
        return level_anchor;
    }

    public void setLevel_anchor(int level_anchor) {
        this.level_anchor = level_anchor;
    }

    public int getIsattention() {
        return isattention;
    }

    public void setIsattention(int isattention) {
        this.isattention = isattention;
    }

    public String getbTitle() {
        return bTitle;
    }

    public void setbTitle(String bTitle) {
        this.bTitle = bTitle;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getPull() {
        return pull;
    }

    public void setPull(String pull) {
        this.pull = pull;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getIsvideo() {
        return isvideo;
    }

    public void setIsvideo(String isvideo) {
        this.isvideo = isvideo;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public int getFirenum() {
        return firenum;
    }

    public void setFirenum(int firenum) {
        this.firenum = firenum;
    }

    public int getIslive() {
        return islive;
    }

    public void setIslive(int islive) {
        this.islive = islive;
    }

}
