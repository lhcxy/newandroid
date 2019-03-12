package com.yunbao.phonelive.event;

public class NetKBEvent {
    private int uploadKb;

    public NetKBEvent(int uploadKb) {
        this.uploadKb = uploadKb;
    }

    public int getUploadKb() {
        return uploadKb;
    }

    public void setUploadKb(int uploadKb) {
        this.uploadKb = uploadKb;
    }
}
