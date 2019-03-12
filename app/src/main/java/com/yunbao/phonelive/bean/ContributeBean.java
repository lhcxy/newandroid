package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/8/12.
 */

public class ContributeBean  implements Serializable {
    private String uid;
    private float total;
    private String avatar;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
