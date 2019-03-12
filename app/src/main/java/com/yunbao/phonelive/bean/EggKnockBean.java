package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class EggKnockBean implements Serializable {

    /**
     * status : 1
     * msg : 成功
     * time : 1625
     * level : 4
     * coin : 488
     * ucoin : 950668285
     * ncoin : 1200
     * gl : 40
     * amount : 400
     */

    private int status;
    private String msg;
    private int time;
    private int level;
    private int coin;
    private int ucoin;
    private int ncoin;
    private int amount;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getUcoin() {
        return ucoin;
    }

    public void setUcoin(int ucoin) {
        this.ucoin = ucoin;
    }

    public int getNcoin() {
        return ncoin;
    }

    public void setNcoin(int ncoin) {
        this.ncoin = ncoin;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
