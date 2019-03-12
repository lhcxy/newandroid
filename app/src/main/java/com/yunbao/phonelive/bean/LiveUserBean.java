package com.yunbao.phonelive.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class LiveUserBean implements Serializable {

    /**
     * id : 12447
     * user_login : 17750611071
     * user_nicename : 手机用户1071
     * avatar : http://qiniu.2yx.cm/20180906155743_567b5da1269fefbd129db8bcd92093b2?imageView2/2/w/600/h/600
     * avatar_thumb : http://qiniu.2yx.cm/20180906155743_567b5da1269fefbd129db8bcd92093b2?imageView2/2/w/200/h/200
     * sex : 2
     * coin : 103944
     * carrot : 7
     * firenum : 1707
     * fans : 0
     * room_title : 推荐
     * room_notice : …
     * content : …
     * showid : 1537248718
     */

    private String id;
    private String user_login;
    private String user_nicename;
    private String avatar;
    private String avatar_thumb;
    private String sex;
    private String coin;
    private int carrot;
    private int firenum;
    private String fans;
    private String room_title;
    private String room_notice;
    private String content;
    private String showid;
    private String livetag;
    /**
     * cask : 1
     * ctype : 1
     * count : 10
     * pnum : 0
     * end_time : 179
     */

    private int cask;
    private int ctype;
    private int count;
    private int pnum;
    private int end_time;
    private int level = 0;
    private int level_anchor = 0;
    private float exp_anchor = 0;
    private int exp_up = 0;
    /**
     * carrot : 0
     * tcnum : 0
     */
    private int tcnum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_login() {
        return user_login;
    }

    public void setUser_login(String user_login) {
        this.user_login = user_login;
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

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public int getCarrot() {
        return carrot;
    }

    public void setCarrot(int carrot) {
        this.carrot = carrot;
    }

    public int getFirenum() {
        return firenum;
    }

    public String getFireNumStr() {
        if (firenum >= 10000) {
            return String.valueOf(new BigDecimal((float) firenum / 10000).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue()) + "万";
        }
        return String.valueOf(firenum);
    }

    public void setFirenum(int firenum) {
        this.firenum = firenum;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getRoom_title() {
        return room_title;
    }

    public void setRoom_title(String room_title) {
        this.room_title = room_title;
    }

    public String getRoom_notice() {
        return room_notice;
    }

    public void setRoom_notice(String room_notice) {
        this.room_notice = room_notice;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getShowid() {
        return showid;
    }

    public void setShowid(String showid) {
        this.showid = showid;
    }

    public int getCask() {
        return cask;
    }

    public void setCask(int cask) {
        this.cask = cask;
    }

    public int getCtype() {
        return ctype;
    }

    public void setCtype(int ctype) {
        this.ctype = ctype;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPnum() {
        return pnum;
    }

    public void setPnum(int pnum) {
        this.pnum = pnum;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }


    public int getTcnum() {
        return tcnum;
    }

    public void setTcnum(int tcnum) {
        this.tcnum = tcnum;
    }

    public String getLivetag() {
        return livetag;
    }

    public void setLivetag(String livetag) {
        this.livetag = livetag;
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

    public float getExp_anchor() {
        return exp_anchor;
    }

    public void setExp_anchor(float exp_anchor) {
        this.exp_anchor = exp_anchor;
    }

    public int getExp_up() {
        return exp_up;
    }

    public void setExp_up(int exp_up) {
        this.exp_up = exp_up;
    }
}
