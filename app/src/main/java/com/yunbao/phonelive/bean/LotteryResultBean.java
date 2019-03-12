package com.yunbao.phonelive.bean;

import java.io.Serializable;
import java.util.List;

public class LotteryResultBean implements Serializable {

    /**
     * num : 7
     * id : 4
     * gifticon : http://qiniu.2yx.cm/20190110/5c36dec5d34cb.png
     * giftname : 探球币×100
     * cinfo : ["探球币×100"]
     */

    private int num;
    private String id;
    private String gifticon;
    private String giftname;
    private List<LotteryItemBean> cinfo;
    private List<LotteryGetInfoBean> linfo;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<LotteryItemBean> getCinfo() {
        return cinfo;
    }

    public void setCinfo(List<LotteryItemBean> cinfo) {
        this.cinfo = cinfo;
    }

    public List<LotteryGetInfoBean> getLinfo() {
        return linfo;
    }

    public void setLinfo(List<LotteryGetInfoBean> linfo) {
        this.linfo = linfo;
    }
}
