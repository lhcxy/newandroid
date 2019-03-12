package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * 提现记录 bean
 */
public class HarvestHistoryBean implements Serializable {

    /**
     * id : 93
     * uid : 12447
     * money : 1000.00
     * votes : 100000
     * orderno : 12447_1540452130397
     * amount : 100.00
     * status : 0
     * addtime : 1540452130
     */

    private String id;
    private String uid;
    private float money;
    private String votes;
    private String orderno;
    private float amount;
    private int status;
    private long addtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }
}
