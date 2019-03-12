package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/8/19.
 * 礼物列表的实体类
 */

public class GiftBean  implements Serializable {
    private String id;
    private int type;
    private String giftname;
    private String needcoin;
    private String gifticon;
    private String giftid;
    private int ctype = 1;
    private boolean checked;
    private int page;
    private int position;
    private String evensend;//连送的标识
    private boolean isPackageGift = false;
    private String count;
    private String num; //包裹礼物数量

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGiftname() {
        return giftname;
    }

    public void setGiftname(String giftname) {
        this.giftname = giftname;
    }

    public String getNeedcoin() {
        return needcoin;
    }

    public void setNeedcoin(String needcoin) {
        this.needcoin = needcoin;
    }

    public String getGifticon() {
        return gifticon;
    }

    public void setGifticon(String gifticon) {
        this.gifticon = gifticon;
    }

    public String getEvensend() {
        return evensend;
    }

    public void setEvensend(String evensend) {
        this.evensend = evensend;
    }

    public boolean isPackageGift() {
        return isPackageGift;
    }

    public void setPackageGift(boolean packageGift) {
        isPackageGift = packageGift;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getGiftid() {
        return giftid;
    }

    public void setGiftid(String giftid) {
        this.giftid = giftid;
    }

    public int getCtype() {
        return ctype;
    }

    public void setCtype(int ctype) {
        this.ctype = ctype;
    }
}
