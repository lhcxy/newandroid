package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/8/19.
 * 主播直播间 功能列表 实体类
 */

public class FunctionBean  implements Serializable {

    public static final int TIME = 0;//切换房间类型 计时收费等
    public static final int MEI_YAN = 1;//美颜滤镜
    public static final int CAMERA = 2;//切换摄像头
    public static final int MUSIC = 3;//背景音乐
    public static final int GAME = 4;//游戏
    public static final int AUCTION = 5;//竞拍
    public static final int FLASH = 6;//闪光灯

    private int id;
    private int icon;

    public FunctionBean(int id, int icon) {
        this.id = id;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
