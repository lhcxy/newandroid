package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class ChargeTakeBean implements Serializable {
    private int coin;
    private String money;
    private boolean isHot = false;

    public ChargeTakeBean(int coin, String money, boolean isHot) {
        this.coin = coin;
        this.money = money;
        this.isHot = isHot;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }
}
