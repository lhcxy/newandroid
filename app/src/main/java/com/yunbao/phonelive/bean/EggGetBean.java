package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class EggGetBean implements Serializable {

    /**
     * type : 0
     * level : 4
     * coin : 488
     * ucoin : 950668773
     * msg : 领取成功
     */

    private int type;
    private String level;
    private String coin;
    private int ucoin;
    private String msg;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public int getUcoin() {
        return ucoin;
    }

    public void setUcoin(int ucoin) {
        this.ucoin = ucoin;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
