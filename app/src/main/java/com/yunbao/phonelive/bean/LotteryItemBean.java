package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class LotteryItemBean implements Serializable {

    /**
     * id : 4
     * num : 100
     * gifticon : http://qiniu.2yx.cm/20190110/5c36dec5d34cb.png
     * giftname : 探球币
     */

    private String id;
    private int num;
    private String gifticon;
    private String giftname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getGifticon() {
        return gifticon;
    }

    public void setGifticon(String gifticon) {
        this.gifticon = gifticon;
    }

    public String getGiftname() {
        return giftname;
    }

    public void setGiftname(String giftname) {
        this.giftname = giftname;
    }
}
