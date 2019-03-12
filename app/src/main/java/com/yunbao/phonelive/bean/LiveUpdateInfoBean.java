package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class LiveUpdateInfoBean implements Serializable {

    /**
     * carrot : 70
     * fans : 6
     * id : 12348
     * total : 1470470
     * vip_type : 0
     */

    private String carrot;
    private String fans;
    private String id;
    private int total;
    private String vip_type;

    public String getCarrot() {
        return carrot;
    }

    public void setCarrot(String carrot) {
        this.carrot = carrot;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getVip_type() {
        return vip_type;
    }

    public void setVip_type(String vip_type) {
        this.vip_type = vip_type;
    }
}
