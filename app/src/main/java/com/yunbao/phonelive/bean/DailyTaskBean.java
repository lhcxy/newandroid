package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class DailyTaskBean implements Serializable {


    /**
     * id : 9
     * task_type : 1
     * title : 赠送爆米花
     * rewind_type : 2
     * coin : 100
     * state : 0
     * cash : 15
     * icon : http://qiniu.2yx.cm/20181211/5c0f8e3fc0347.png
     * condition : 0
     * status : 0
     */

    private String id;
    private String task_type;
    private String title;
    private String rewind_type;
    private String coin;
    private String state;
    private String cash;
    private String icon;
    private int condition;
    private int status;
    /**
     * balance : 1
     * oknum : 0
     */

    private int balance; //总数
    private int oknum; //次数

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRewind_type() {
        return rewind_type;
    }

    public void setRewind_type(String rewind_type) {
        this.rewind_type = rewind_type;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getOknum() {
        return oknum;
    }

    public void setOknum(int oknum) {
        this.oknum = oknum;
    }
}
