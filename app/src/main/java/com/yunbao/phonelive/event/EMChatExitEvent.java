package com.yunbao.phonelive.event;

import com.yunbao.phonelive.bean.UserBean;

/**
 * Created by cxf on 2017/8/14.
 */

public class EMChatExitEvent {
    private String lastMsg;
    private String lastTime;
    private String touid;
    private int isAttention;
    private UserBean userBean;
    public EMChatExitEvent(String lastMsg, String lastTime, String touid, int isAttention) {
        this.lastMsg = lastMsg;
        this.lastTime = lastTime;
        this.touid = touid;
        this.isAttention = isAttention;
    }

    public int getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(int isAttention) {
        this.isAttention = isAttention;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getTouid() {
        return touid;
    }

    public void setTouid(String touid) {
        this.touid = touid;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
