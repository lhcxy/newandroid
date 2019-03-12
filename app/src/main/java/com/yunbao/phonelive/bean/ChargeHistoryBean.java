package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class ChargeHistoryBean implements Serializable {


    /**
     * id : 981
     * uid : 12444
     * touid : 12444
     * money : 6.00
     * coin : 600
     * coin_give : 0
     * orderno : 12444_12444_0910095346_5920
     * trade_no : 20180920171895362472690970159395
     * status : 0  =待支付  1=已支付
     * addtime : 1536544426
     * type : 2     1支付宝支付2微信支付
     * ambient : 2  1pc端2安卓3ios
     * charge_type : 1 1章鱼币充值2余额充值
     */

    private String id;
    private String uid;
    private String touid;
    private String money;
    private String coin;
    private String coin_give;
    private String orderno;
    private String trade_no;
    private String status;
    private String addtime;
    private String type;
    private String ambient;
    private String charge_type;

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

    public String getTouid() {
        return touid;
    }

    public void setTouid(String touid) {
        this.touid = touid;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getCoin_give() {
        return coin_give;
    }

    public void setCoin_give(String coin_give) {
        this.coin_give = coin_give;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmbient() {
        return ambient;
    }

    public void setAmbient(String ambient) {
        this.ambient = ambient;
    }

    public String getCharge_type() {
        return charge_type;
    }

    public void setCharge_type(String charge_type) {
        this.charge_type = charge_type;
    }
}
