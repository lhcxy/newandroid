package com.phonelive.game.game;

import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/10/6.
 */

public class ChooseGameBean {
    private int order;//顺序
    private String name;//游戏名称
    private int img;//游戏图标
    private String method;//socket方法

    public ChooseGameBean(int order, String name, int img, String method) {
        this.order = order;
        this.name = name;
        this.img = img;
        this.method = method;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public static ChooseGameBean createCameBean(int gameSwitch) {
        ChooseGameBean bean = null;
        switch (gameSwitch) {
            case GameConst.GAME_SWITCH_NIU_ZAI:
                bean = new ChooseGameBean(4, "开心牛仔", R.mipmap.icon_nz, GameConst.SOKCET_GAME_NIU_ZAI);
                break;
            case GameConst.GAME_SWITCH_JIN_HUA:
                bean = new ChooseGameBean(1, "智勇三张", R.mipmap.icon_zjh, GameConst.SOKCET_GAME_JIN_HUA);
                break;
            case GameConst.GAME_SWITCH_HAI_DAO:
                bean = new ChooseGameBean(2, "海盗船长", R.mipmap.icon_hd, GameConst.SOKCET_GAME_HAI_DAO);
                break;
            case GameConst.GAME_SWITCH_ER_BA_BEI:
                bean = new ChooseGameBean(5, "二八贝", R.mipmap.icon_ebb, GameConst.SOKCET_GAME_ER_BA_BEI);
                break;
            case GameConst.GAME_SWITCH_LUCK_PAN:
                bean = new ChooseGameBean(3, "幸运转盘", R.mipmap.icon_zp, GameConst.SOKCET_GAME_LUCK_PAN);
                break;
        }
        return bean;
    }
}
