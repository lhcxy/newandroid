package com.yunbao.phonelive.utils;

import android.util.SparseIntArray;

import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/8/11.
 * 查找 用户等级、性别对应的图片和直播间飘心的图片
 */

public class IconUitl {
    private static SparseIntArray sAnchorLevelMap;//主播等级
    private static SparseIntArray sAnchorLiveLevelMap;//直播间主播等级
    private static SparseIntArray sAudienceLevelMap;//观众等级
    private static SparseIntArray sAudienceLiveLevelMap;//直播间观众等级
    private static SparseIntArray sSexMap;//性别 男 女
    private static SparseIntArray sHeartMap;//点亮几个心形图片
    private static SparseIntArray sLiveTypeMap;//直播间房间类型，普通，密码，门票，计时等
    private static SparseIntArray sVipTypeMap;//vip类型图标
    private static SparseIntArray sFloatHeartMap;//飘心动画图片

    static {
        sAnchorLevelMap = new SparseIntArray();
        sAnchorLevelMap.put(1, R.mipmap.anchor_level1);
        sAnchorLevelMap.put(2, R.mipmap.anchor_level2);
        sAnchorLevelMap.put(3, R.mipmap.anchor_level3);
        sAnchorLevelMap.put(4, R.mipmap.anchor_level4);
        sAnchorLevelMap.put(5, R.mipmap.anchor_level5);
        sAnchorLevelMap.put(6, R.mipmap.anchor_level6);
        sAnchorLevelMap.put(7, R.mipmap.anchor_level7);
        sAnchorLevelMap.put(8, R.mipmap.anchor_level8);
        sAnchorLevelMap.put(9, R.mipmap.anchor_level9);
        sAnchorLevelMap.put(10, R.mipmap.anchor_level10);
        sAnchorLevelMap.put(11, R.mipmap.anchor_level11);
        sAnchorLevelMap.put(12, R.mipmap.anchor_level12);
        sAnchorLevelMap.put(13, R.mipmap.anchor_level13);
        sAnchorLevelMap.put(14, R.mipmap.anchor_level14);
        sAnchorLevelMap.put(15, R.mipmap.anchor_level15);
        sAnchorLevelMap.put(16, R.mipmap.anchor_level16);
        sAnchorLevelMap.put(17, R.mipmap.anchor_level17);
        sAnchorLevelMap.put(18, R.mipmap.anchor_level18);
        sAnchorLevelMap.put(19, R.mipmap.anchor_level19);
        sAnchorLevelMap.put(20, R.mipmap.anchor_level20);

        sAnchorLiveLevelMap = new SparseIntArray();
        sAnchorLiveLevelMap.put(1, R.mipmap.anchor_levell1);
        sAnchorLiveLevelMap.put(2, R.mipmap.anchor_levell2);
        sAnchorLiveLevelMap.put(3, R.mipmap.anchor_levell3);
        sAnchorLiveLevelMap.put(4, R.mipmap.anchor_levell4);
        sAnchorLiveLevelMap.put(5, R.mipmap.anchor_levell5);
        sAnchorLiveLevelMap.put(6, R.mipmap.anchor_levell6);
        sAnchorLiveLevelMap.put(7, R.mipmap.anchor_levell7);
        sAnchorLiveLevelMap.put(8, R.mipmap.anchor_levell8);
        sAnchorLiveLevelMap.put(9, R.mipmap.anchor_levell9);
        sAnchorLiveLevelMap.put(10, R.mipmap.anchor_levell10);
        sAnchorLiveLevelMap.put(11, R.mipmap.anchor_levell11);
        sAnchorLiveLevelMap.put(12, R.mipmap.anchor_levell12);
        sAnchorLiveLevelMap.put(13, R.mipmap.anchor_levell13);
        sAnchorLiveLevelMap.put(14, R.mipmap.anchor_levell14);
        sAnchorLiveLevelMap.put(15, R.mipmap.anchor_levell15);
        sAnchorLiveLevelMap.put(16, R.mipmap.anchor_levell16);
        sAnchorLiveLevelMap.put(17, R.mipmap.anchor_levell17);
        sAnchorLiveLevelMap.put(18, R.mipmap.anchor_levell18);
        sAnchorLiveLevelMap.put(19, R.mipmap.anchor_levell19);
        sAnchorLiveLevelMap.put(20, R.mipmap.anchor_levell20);


        sAudienceLevelMap = new SparseIntArray();
        sAudienceLevelMap.put(1, R.mipmap.user_level1);
        sAudienceLevelMap.put(2, R.mipmap.user_level2);
        sAudienceLevelMap.put(3, R.mipmap.user_level3);
        sAudienceLevelMap.put(4, R.mipmap.user_level4);
        sAudienceLevelMap.put(5, R.mipmap.user_level5);
        sAudienceLevelMap.put(6, R.mipmap.user_level6);
        sAudienceLevelMap.put(7, R.mipmap.user_level7);
        sAudienceLevelMap.put(8, R.mipmap.user_level8);
        sAudienceLevelMap.put(9, R.mipmap.user_level9);
        sAudienceLevelMap.put(10, R.mipmap.user_level10);
        sAudienceLevelMap.put(11, R.mipmap.user_level11);
        sAudienceLevelMap.put(12, R.mipmap.user_level12);
        sAudienceLevelMap.put(13, R.mipmap.user_level13);
        sAudienceLevelMap.put(14, R.mipmap.user_level14);
        sAudienceLevelMap.put(15, R.mipmap.user_level15);
        sAudienceLevelMap.put(16, R.mipmap.user_level16);
        sAudienceLevelMap.put(17, R.mipmap.user_level17);
        sAudienceLevelMap.put(18, R.mipmap.user_level18);
        sAudienceLevelMap.put(19, R.mipmap.user_level19);
        sAudienceLevelMap.put(20, R.mipmap.user_level20);

        sAudienceLiveLevelMap = new SparseIntArray();
        sAudienceLiveLevelMap.put(1, R.mipmap.user_levell1);
        sAudienceLiveLevelMap.put(2, R.mipmap.user_levell2);
        sAudienceLiveLevelMap.put(3, R.mipmap.user_levell3);
        sAudienceLiveLevelMap.put(4, R.mipmap.user_levell4);
        sAudienceLiveLevelMap.put(5, R.mipmap.user_levell5);
        sAudienceLiveLevelMap.put(6, R.mipmap.user_levell6);
        sAudienceLiveLevelMap.put(7, R.mipmap.user_levell7);
        sAudienceLiveLevelMap.put(8, R.mipmap.user_levell8);
        sAudienceLiveLevelMap.put(9, R.mipmap.user_levell9);
        sAudienceLiveLevelMap.put(10, R.mipmap.user_levell10);
        sAudienceLiveLevelMap.put(11, R.mipmap.user_levell11);
        sAudienceLiveLevelMap.put(12, R.mipmap.user_levell12);
        sAudienceLiveLevelMap.put(13, R.mipmap.user_levell13);
        sAudienceLiveLevelMap.put(14, R.mipmap.user_levell14);
        sAudienceLiveLevelMap.put(15, R.mipmap.user_levell15);
        sAudienceLiveLevelMap.put(16, R.mipmap.user_levell16);
        sAudienceLiveLevelMap.put(17, R.mipmap.user_levell17);
        sAudienceLiveLevelMap.put(18, R.mipmap.user_levell18);
        sAudienceLiveLevelMap.put(19, R.mipmap.user_levell19);
        sAudienceLiveLevelMap.put(20, R.mipmap.user_levell20);


        sSexMap = new SparseIntArray();
        sSexMap.put(1, R.mipmap.icon_sex_male_selected);
        sSexMap.put(2, R.mipmap.icon_sex_female_selected);

        sHeartMap = new SparseIntArray();
        sHeartMap.put(1, R.mipmap.icon_heart_cyan);
        sHeartMap.put(2, R.mipmap.icon_heart_pink);
        sHeartMap.put(3, R.mipmap.icon_heart_red);
        sHeartMap.put(4, R.mipmap.icon_heart_yellow);
        sHeartMap.put(5, R.mipmap.icon_heart_cyan);

        sFloatHeartMap = new SparseIntArray();
        sFloatHeartMap.put(1, R.mipmap.icon_float_1);
        sFloatHeartMap.put(2, R.mipmap.icon_float_2);
        sFloatHeartMap.put(3, R.mipmap.icon_float_3);
        sFloatHeartMap.put(4, R.mipmap.icon_float_4);
        sFloatHeartMap.put(5, R.mipmap.icon_float_5);

        sLiveTypeMap = new SparseIntArray();
        sLiveTypeMap.put(0, R.mipmap.icon_live_type_normal);
        sLiveTypeMap.put(1, R.mipmap.icon_live_type_pwd);
        sLiveTypeMap.put(2, R.mipmap.icon_live_type_charge);
        sLiveTypeMap.put(3, R.mipmap.icon_live_type_time_charge);

        sVipTypeMap = new SparseIntArray();
        sVipTypeMap.put(1, R.mipmap.icon_vip_1);
        sVipTypeMap.put(2, R.mipmap.icon_vip_2);
    }

    public static int getAnchorDrawable(int level) {
        return sAnchorLevelMap.get(level);
    }

    public static int getAnchorLiveDrawable(int level) {
        return sAnchorLiveLevelMap.get(level);
    }

    public static int getAudienceDrawable(int level) {
        return sAudienceLevelMap.get(level);
    }

    public static int getAudienceLiveDrawable(int level) {
        return sAudienceLiveLevelMap.get(level);
    }

    public static int getSexDrawable(int sex) {
        return sSexMap.get(sex);
    }

    public static int getHeartDrawable(int heart) {
        return sHeartMap.get(heart);
    }

    public static int getFloatHeartDrawable(int heart) {
        return sFloatHeartMap.get(heart);
    }

    public static int getLiveTypeDrawable(int type) {
        return sLiveTypeMap.get(type);
    }

    public static int getVipType(int type) {
        return sVipTypeMap.get(type);
    }
}
