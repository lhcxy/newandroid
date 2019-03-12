package com.yunbao.phonelive.utils;

import com.yunbao.phonelive.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/9/24.
 * 获取帧动画的每一帧的图片资源
 */

public class FrameAnimUtil {

//    private static List<Integer> sFireFlowersAnim;//烟花帧动画
//    private static List<Integer> sWuGuiAnim;//乌龟坐骑进场动画
//    private static List<Integer> sSaoBaAnim;//扫把坐骑进场动画
//    private static List<Integer> sMaoLvAnim;//毛驴坐骑进场动画
//    private static List<Integer> sShipAnim;//船帧动画
//    private static List<Integer> sMeteorAnim;//流星帧动画
//    private static List<Integer> sLoveAnim;//钻石帧动画
//    private static List<Integer> sSeaAnim;//海天帧动画
//
//    static {
//        sFireFlowersAnim = Arrays.asList(
//                R.mipmap.fireworks_1,
//                R.mipmap.fireworks_2,
//                R.mipmap.fireworks_3,
//                R.mipmap.fireworks_4,
//                R.mipmap.fireworks_5,
//                R.mipmap.fireworks_6,
//                R.mipmap.fireworks_7,
//                R.mipmap.fireworks_8,
//                R.mipmap.fireworks_9,
//                R.mipmap.fireworks_10,
//                R.mipmap.fireworks_flower1,
//                R.mipmap.fireworks_flower2,
//                R.mipmap.fireworks_flower3,
//                R.mipmap.fireworks_flower4,
//                R.mipmap.gift_heart_1,
//                R.mipmap.gift_heart_2,
//                R.mipmap.gift_heart_3,
//                R.mipmap.gift_heart_4,
//                R.mipmap.gift_heart_5,
//                R.mipmap.gift_heart_6,
//                R.mipmap.gift_heart_7,
//                R.mipmap.gift_heart_8,
//                R.mipmap.gift_heart_9,
//                R.mipmap.gift_heart_10,
//                R.mipmap.gift_heart_11,
//                R.mipmap.gift_heart_12,
//                R.mipmap.gift_heart_13,
//                R.mipmap.gift_heart_14,
//                R.mipmap.gift_heart_15,
//                R.mipmap.gift_heart_16,
//                R.mipmap.gift_heart_17,
//                R.mipmap.gift_heart_18,
//                R.mipmap.gift_heart_19,
//                R.mipmap.gift_heart_20
//        );
//
//        sWuGuiAnim = Arrays.asList(
//                R.mipmap.wg_01,
//                R.mipmap.wg_02,
//                R.mipmap.wg_03,
//                R.mipmap.wg_04,
//                R.mipmap.wg_05,
//                R.mipmap.wg_06,
//                R.mipmap.wg_07,
//                R.mipmap.wg_08,
//                R.mipmap.wg_09,
//                R.mipmap.wg_10,
//                R.mipmap.wg_11,
//                R.mipmap.wg_12,
//                R.mipmap.wg_13,
//                R.mipmap.wg_14,
//                R.mipmap.wg_15,
//                R.mipmap.wg_16,
//                R.mipmap.wg_17,
//                R.mipmap.wg_18,
//                R.mipmap.wg_19,
//                R.mipmap.wg_20,
//                R.mipmap.wg_21,
//                R.mipmap.wg_22
//        );
//
//        sSaoBaAnim = Arrays.asList(
//                R.mipmap.sb_01,
//                R.mipmap.sb_02,
//                R.mipmap.sb_03,
//                R.mipmap.sb_04,
//                R.mipmap.sb_05,
//                R.mipmap.sb_06,
//                R.mipmap.sb_07,
//                R.mipmap.sb_08,
//                R.mipmap.sb_09,
//                R.mipmap.sb_10,
//                R.mipmap.sb_11,
//                R.mipmap.sb_12,
//                R.mipmap.sb_13,
//                R.mipmap.sb_14,
//                R.mipmap.sb_15,
//                R.mipmap.sb_16,
//                R.mipmap.sb_17,
//                R.mipmap.sb_18,
//                R.mipmap.sb_19,
//                R.mipmap.sb_20,
//                R.mipmap.sb_22,
//                R.mipmap.sb_22
//        );
//
//        sMaoLvAnim = Arrays.asList(
//                R.mipmap.ml_01,
//                R.mipmap.ml_02,
//                R.mipmap.ml_03,
//                R.mipmap.ml_04,
//                R.mipmap.ml_05,
//                R.mipmap.ml_06,
//                R.mipmap.ml_07,
//                R.mipmap.ml_08,
//                R.mipmap.ml_09,
//                R.mipmap.ml_10,
//                R.mipmap.ml_11,
//                R.mipmap.ml_12,
//                R.mipmap.ml_13,
//                R.mipmap.ml_14,
//                R.mipmap.ml_15,
//                R.mipmap.ml_16,
//                R.mipmap.ml_17,
//                R.mipmap.ml_18,
//                R.mipmap.ml_19,
//                R.mipmap.ml_20,
//                R.mipmap.ml_22,
//                R.mipmap.ml_22
//        );
//
//
//        sShipAnim = Arrays.asList(
//                R.drawable.ship_001,
//                R.drawable.ship_002,
//                R.drawable.ship_003,
//                R.drawable.ship_004,
//                R.drawable.ship_005,
//                R.drawable.ship_006,
//                R.drawable.ship_007,
//                R.drawable.ship_008,
//                R.drawable.ship_009,
//                R.drawable.ship_010,
//                R.drawable.ship_011,
//                R.drawable.ship_012,
//                R.drawable.ship_013,
//                R.drawable.ship_014,
//                R.drawable.ship_015,
//                R.drawable.ship_016,
//                R.drawable.ship_017,
//                R.drawable.ship_018,
//                R.drawable.ship_019,
//                R.drawable.ship_020,
//                R.drawable.ship_021,
//                R.drawable.ship_022,
//                R.drawable.ship_023,
//                R.drawable.ship_024,
//                R.drawable.ship_025,
//                R.drawable.ship_026,
//                R.drawable.ship_027,
//                R.drawable.ship_028,
//                R.drawable.ship_029,
//                R.drawable.ship_030,
//                R.drawable.ship_031,
//                R.drawable.ship_032,
//                R.drawable.ship_033,
//                R.drawable.ship_034,
//                R.drawable.ship_035,
//                R.drawable.ship_036,
//                R.drawable.ship_037,
//                R.drawable.ship_038,
//                R.drawable.ship_039,
//                R.drawable.ship_040,
//                R.drawable.ship_041,
//                R.drawable.ship_042,
//                R.drawable.ship_043,
//                R.drawable.ship_044,
//                R.drawable.ship_045,
//                R.drawable.ship_046,
//                R.drawable.ship_047,
//                R.drawable.ship_048,
//                R.drawable.ship_049,
//                R.drawable.ship_050,
//                R.drawable.ship_051,
//                R.drawable.ship_052,
//                R.drawable.ship_053,
//                R.drawable.ship_054,
//                R.drawable.ship_055,
//                R.drawable.ship_056,
//                R.drawable.ship_057,
//                R.drawable.ship_058,
//                R.drawable.ship_059,
//                R.drawable.ship_060,
//                R.drawable.ship_061,
//                R.drawable.ship_062,
//                R.drawable.ship_063,
//                R.drawable.ship_064,
//                R.drawable.ship_065,
//                R.drawable.ship_066,
//                R.drawable.ship_067,
//                R.drawable.ship_068,
//                R.drawable.ship_069,
//                R.drawable.ship_070,
//                R.drawable.ship_071,
//                R.drawable.ship_072,
//                R.drawable.ship_073
//        );
//
//        sMeteorAnim = Arrays.asList(
//                R.drawable.g_meteor_01,
//                R.drawable.g_meteor_02,
//                R.drawable.g_meteor_03,
//                R.drawable.g_meteor_04,
//                R.drawable.g_meteor_05,
//                R.drawable.g_meteor_06,
//                R.drawable.g_meteor_07,
//                R.drawable.g_meteor_08,
//                R.drawable.g_meteor_09,
//                R.drawable.g_meteor_10,
//                R.drawable.g_meteor_11,
//                R.drawable.g_meteor_12,
//                R.drawable.g_meteor_13,
//                R.drawable.g_meteor_14,
//                R.drawable.g_meteor_15,
//                R.drawable.g_meteor_16,
//                R.drawable.g_meteor_17,
//                R.drawable.g_meteor_18,
//                R.drawable.g_meteor_19,
//                R.drawable.g_meteor_20,
//                R.drawable.g_meteor_21,
//                R.drawable.g_meteor_22,
//                R.drawable.g_meteor_23,
//                R.drawable.g_meteor_24,
//                R.drawable.g_meteor_25,
//                R.drawable.g_meteor_26,
//                R.drawable.g_meteor_27,
//                R.drawable.g_meteor_28,
//                R.drawable.g_meteor_29,
//                R.drawable.g_meteor_30,
//                R.drawable.g_meteor_31,
//                R.drawable.g_meteor_32,
//                R.drawable.g_meteor_33,
//                R.drawable.g_meteor_34,
//                R.drawable.g_meteor_35,
//                R.drawable.g_meteor_36,
//                R.drawable.g_meteor_37,
//                R.drawable.g_meteor_38,
//                R.drawable.g_meteor_39,
//                R.drawable.g_meteor_40,
//                R.drawable.g_meteor_41,
//                R.drawable.g_meteor_42,
//                R.drawable.g_meteor_43
//        );
//
//        sLoveAnim = Arrays.asList(
//                R.drawable.love_01,
//                R.drawable.love_02,
//                R.drawable.love_03,
//                R.drawable.love_04,
//                R.drawable.love_05,
//                R.drawable.love_06,
//                R.drawable.love_07,
//                R.drawable.love_08,
//                R.drawable.love_09,
//                R.drawable.love_10,
//                R.drawable.love_11,
//                R.drawable.love_12,
//                R.drawable.love_13,
//                R.drawable.love_14,
//                R.drawable.love_15,
//                R.drawable.love_16,
//                R.drawable.love_17,
//                R.drawable.love_18,
//                R.drawable.love_19,
//                R.drawable.love_20,
//                R.drawable.love_21,
//                R.drawable.love_22,
//                R.drawable.love_23,
//                R.drawable.love_24,
//                R.drawable.love_25,
//                R.drawable.love_26,
//                R.drawable.love_27,
//                R.drawable.love_28,
//                R.drawable.love_29,
//                R.drawable.love_30,
//                R.drawable.love_31,
//                R.drawable.love_32,
//                R.drawable.love_33,
//                R.drawable.love_34,
//                R.drawable.love_35,
//                R.drawable.love_36,
//                R.drawable.love_37,
//                R.drawable.love_38,
//                R.drawable.love_39,
//                R.drawable.love_40,
//                R.drawable.love_41,
//                R.drawable.love_42
//        );
//
//        sSeaAnim = Arrays.asList(
//                R.drawable.sea_01,
//                R.drawable.sea_02,
//                R.drawable.sea_03,
//                R.drawable.sea_04,
//                R.drawable.sea_05,
//                R.drawable.sea_06,
//                R.drawable.sea_07,
//                R.drawable.sea_08,
//                R.drawable.sea_09,
//                R.drawable.sea_10,
//                R.drawable.sea_11,
//                R.drawable.sea_12,
//                R.drawable.sea_13,
//                R.drawable.sea_14,
//                R.drawable.sea_15,
//                R.drawable.sea_16,
//                R.drawable.sea_17,
//                R.drawable.sea_18,
//                R.drawable.sea_19,
//                R.drawable.sea_20,
//                R.drawable.sea_21,
//                R.drawable.sea_22,
//                R.drawable.sea_23,
//                R.drawable.sea_24,
//                R.drawable.sea_25,
//                R.drawable.sea_26,
//                R.drawable.sea_27,
//                R.drawable.sea_28,
//                R.drawable.sea_29,
//                R.drawable.sea_30,
//                R.drawable.sea_31,
//                R.drawable.sea_32,
//                R.drawable.sea_33,
//                R.drawable.sea_34,
//                R.drawable.sea_35,
//                R.drawable.sea_36,
//                R.drawable.sea_37,
//                R.drawable.sea_38,
//                R.drawable.sea_39,
//                R.drawable.sea_40,
//                R.drawable.sea_41,
//                R.drawable.sea_42,
//                R.drawable.sea_43,
//                R.drawable.sea_44,
//                R.drawable.sea_45,
//                R.drawable.sea_46,
//                R.drawable.sea_47,
//                R.drawable.sea_48,
//                R.drawable.sea_49,
//                R.drawable.sea_50,
//                R.drawable.sea_51,
//                R.drawable.sea_52,
//                R.drawable.sea_53,
//                R.drawable.sea_54,
//                R.drawable.sea_55,
//                R.drawable.sea_56,
//                R.drawable.sea_57,
//                R.drawable.sea_58,
//                R.drawable.sea_59,
//                R.drawable.sea_60,
//                R.drawable.sea_61,
//                R.drawable.sea_62,
//                R.drawable.sea_63,
//                R.drawable.sea_64,
//                R.drawable.sea_65,
//                R.drawable.sea_66,
//                R.drawable.sea_67,
//                R.drawable.sea_68,
//                R.drawable.sea_69,
//                R.drawable.sea_70,
//                R.drawable.sea_71,
//                R.drawable.sea_72,
//                R.drawable.sea_73,
//                R.drawable.sea_74,
//                R.drawable.sea_75,
//                R.drawable.sea_76,
//                R.drawable.sea_77,
//                R.drawable.sea_78,
//                R.drawable.sea_79,
//                R.drawable.sea_80,
//                R.drawable.sea_81,
//                R.drawable.sea_82,
//                R.drawable.sea_83,
//                R.drawable.sea_84,
//                R.drawable.sea_85,
//                R.drawable.sea_86,
//                R.drawable.sea_87,
//                R.drawable.sea_88,
//                R.drawable.sea_89,
//                R.drawable.sea_90,
//                R.drawable.sea_91,
//                R.drawable.sea_92,
//                R.drawable.sea_93
//        );
//    }
//
//    public static List<Integer> getFireFlowersAnim() {
//        return sFireFlowersAnim;
//    }
//
//    public static List<Integer> getWuGuiAnim() {
//        return sWuGuiAnim;
//    }
//
//    public static List<Integer> getSaoBaAnim() {
//        return sSaoBaAnim;
//    }
//
//    public static List<Integer> getMaoLvAnim() {
//        return sMaoLvAnim;
//    }
//
//    public static List<Integer> getShipAnim() {
//        return sShipAnim;
//    }
//
//    public static List<Integer> getMeteorAnim() {
//        return sMeteorAnim;
//    }
//
//    public static List<Integer> getLoveAnim() {
//        return sLoveAnim;
//    }
//
//    public static List<Integer> getSeaAnim() {
//        return sSeaAnim;
//    }
}
