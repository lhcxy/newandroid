package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class LiveChannelDetailBean implements Serializable {
    /**
     * isForbid : false  是否禁播
     * match : 直播   子频道名称
     */

    private boolean isForbid;
    private String match;

    public boolean isIsForbid() {
        return isForbid;
    }

    public void setIsForbid(boolean isForbid) {
        this.isForbid = isForbid;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }
}
