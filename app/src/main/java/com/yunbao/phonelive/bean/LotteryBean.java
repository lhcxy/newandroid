package com.yunbao.phonelive.bean;

import java.io.Serializable;
import java.util.List;

public class LotteryBean implements Serializable {
    private int num = 0;
    private int tid;
    private List<LotteryItemBean> prize_arr;
    private List<LotteryGetInfoBean> linfo;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public List<LotteryItemBean> getPrize_arr() {
        return prize_arr;
    }

    public void setPrize_arr(List<LotteryItemBean> prize_arr) {
        this.prize_arr = prize_arr;
    }

    public List<LotteryGetInfoBean> getLinfo() {
        return linfo;
    }

    public void setLinfo(List<LotteryGetInfoBean> linfo) {
        this.linfo = linfo;
    }
}
