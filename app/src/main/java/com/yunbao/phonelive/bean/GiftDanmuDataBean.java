package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class GiftDanmuDataBean implements Serializable {

    private StackGiftBean bean;
    private LiveBean liveBean;
    private String uName;  //赠送者名称
    private EggKnockBean eggInfo;

    public GiftDanmuDataBean(String uName, EggKnockBean eggInfo, LiveBean liveBean) {
        this.liveBean = liveBean;
        this.uName = uName;
        this.eggInfo = eggInfo;
    }

    public GiftDanmuDataBean(StackGiftBean bean, LiveBean liveBean) {
        this.bean = bean;
        this.liveBean = liveBean;
    }

    public StackGiftBean getBean() {
        return bean;
    }

    public void setBean(StackGiftBean bean) {
        this.bean = bean;
    }

    public LiveBean getLiveBean() {
        return liveBean;
    }

    public void setLiveBean(LiveBean liveBean) {
        this.liveBean = liveBean;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public EggKnockBean getEggInfo() {
        return eggInfo;
    }

    public void setEggInfo(EggKnockBean eggInfo) {
        this.eggInfo = eggInfo;
    }
}
