package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class LotteryGetInfoBean implements Serializable {

    /**
     * user_nicename : 小猪佩奇啊
     * num : 1
     * giftname : 啤酒
     * gifticon : http://qiniu.2yx.cm/20181109/5be54ca0ae8cb.png
     * create_time : 1547110994
     */

    private String user_nicename;
    private String num;
    private String giftname;
    private String gifticon;
    private String create_time;
    /**
     * prop_id : 3
     */

    private int prop_id;

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getProp_id() {
        return prop_id;
    }

    public void setProp_id(int prop_id) {
        this.prop_id = prop_id;
    }
}
