package com.yunbao.phonelive.event;

/**
 * Created by cxf on 2017/8/14.
 */

public class ConnEvent {

    public static final int CONN_ERROR = 0;//网络未连接
    public static final int CONN_OK = 1;//网络连接正常

    private int code;//  1 连网  0 断网

    public ConnEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
