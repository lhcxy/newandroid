package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class MsgCenterListBean implements Serializable {


    /**
     * snum : 0
     * stime : 1542708246
     * scont : 在使用strip_tags函数会传回错误，而htmlspecialchars不会有错误出现，依然后转换为HTML实体。
     * utime : 0
     * ucont : 暂无消息
     * unum : 0
     */

    private int snum;
    private String stime;
    private String scont;
    private String utime;
    private String ucont;
    private int unum;

    public int getSnum() {
        return snum;
    }

    public void setSnum(int snum) {
        this.snum = snum;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getScont() {
        return scont;
    }

    public void setScont(String scont) {
        this.scont = scont;
    }

    public String getUtime() {
        return utime;
    }

    public void setUtime(String utime) {
        this.utime = utime;
    }

    public String getUcont() {
        return ucont;
    }

    public void setUcont(String ucont) {
        this.ucont = ucont;
    }

    public int getUnum() {
        return unum;
    }

    public void setUnum(int unum) {
        this.unum = unum;
    }
}
