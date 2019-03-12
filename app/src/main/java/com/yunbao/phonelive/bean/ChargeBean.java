package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/9/21.
 */

public class ChargeBean  implements Serializable {
    private String id;
    private int coin;
    private String money;
    private String money_ios;
    private String product_id;
    private int give;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMoney_ios() {
        return money_ios;
    }

    public void setMoney_ios(String money_ios) {
        this.money_ios = money_ios;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getGive() {
        return give;
    }

    public void setGive(int give) {
        this.give = give;
    }
}
