package com.yunbao.phonelive.utils;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.VerticalImageSpan;

/**
 * Created by cxf on 2017/8/22.
 * 聊天文字渲染器
 */

public class TextRender {

    private static ForegroundColorSpan sColorSpan1;
    private static ForegroundColorSpan sColorSpan2;
    private static ForegroundColorSpan sColorSpan3;
    private static ForegroundColorSpan sColorSpan4;
    private static ForegroundColorSpan sColorSpan5;
    private static ForegroundColorSpan sColorSpan6;
    private static ForegroundColorSpan sColorSpan7;
    private static ForegroundColorSpan sGiftColorSpan;
    // private static StyleSpan sStyleSpan;

    static {
        sColorSpan1 = new ForegroundColorSpan(0xff9dc9ff);//用户名颜色
        sColorSpan2 = new ForegroundColorSpan(0xffffffff);//系统消息头白色
        sColorSpan6 = new ForegroundColorSpan(0xff52e5d4);//系统消息体绿色
        sColorSpan3 = new ForegroundColorSpan(0xff41d483);//用户进房间的时候 没有vip 名字绿色
        sColorSpan4 = new ForegroundColorSpan(0xfff35ed9);//用户进房间的时候 有vip,没有坐骑，名字红色
        sColorSpan5 = new ForegroundColorSpan(0xfffef954);//用户进房间的时候 既有vip又有坐骑  名字黄色
        sColorSpan7 = new ForegroundColorSpan(0xffb0b0b0);//点亮颜色
        sGiftColorSpan = new ForegroundColorSpan(0xfff0ed3c);//送礼物的时候 字体黄色
        //sStyleSpan = new StyleSpan(Typeface.BOLD);//聊天栏文字 粗体
    }


    /**
     * 渲染消息体
     *
     * @param bean
     * @return
     */
    public static SpannableStringBuilder createChat(LiveChatBean bean) {
        int type = bean.getType();
        SpannableStringBuilder builder = null;
        if (type == LiveChatBean.SYSTEM) {
            builder = new SpannableStringBuilder();
            String res = WordUtil.getString(R.string.live_room_message) + "：" + bean.getContent();
            builder.append(res);
            int index = res.indexOf("：") + 1;
            builder.setSpan(sColorSpan2, 0, index, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            builder.setSpan(sColorSpan6, index, res.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        } else if (type == LiveChatBean.ENTER_ROOM) {
            builder = new SpannableStringBuilder();
            String res = bean.getUser_nicename() + " " + WordUtil.getString(R.string.enter_room_2);
            builder.append(res);
            int index = res.indexOf(" ") + 1;
            builder.setSpan(sColorSpan2, 0, index, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            builder.setSpan(sColorSpan6, index, res.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        } else {
            //生成前缀
            builder = createPrefix(bean.getVipType(), bean.getLiangName(), bean.getLevel());
            //获取前缀的长度
            int prefixLength = builder.length();
            //生成消息体
            String content = bean.getUser_nicename() + "：" + bean.getContent();
            //计算出冒号的index  前缀长度和冒号index之间的内容就是用户的昵称
            int index = prefixLength + content.indexOf("：") + 1;
            if (type == LiveChatBean.NORMAL) {
                //普通消息的时候，看看是不是点亮
                int heart = bean.getHeart();
                if (heart > 0) {
                    content += " ";
                }
                builder.append(content);
                int length = builder.length();
                //给用户名上色
                builder.setSpan(sColorSpan1, prefixLength, index, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                if (heart > 0) {
                    //给消息体加粗
                    builder.setSpan(sColorSpan7, index, length - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    //添加点亮后面的心
                    Drawable heartDrawable = ContextCompat.getDrawable(AppContext.sInstance, IconUitl.getHeartDrawable(heart));
                    heartDrawable.setBounds(0, 0, DpUtil.dp2px(18), DpUtil.dp2px(18));
                    VerticalImageSpan heartSpan = new VerticalImageSpan(heartDrawable);
                    builder.setSpan(heartSpan, length - 1, length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
//                else {
//                    builder.setSpan(sStyleSpan, index, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                }
            } else if (type == LiveChatBean.GIFT) {
                builder.append(content);
                //给用户名上色
                builder.setSpan(sColorSpan1, prefixLength, index, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                //设置礼物字体的红色
                builder.setSpan(sGiftColorSpan, index, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        return builder;
    }

    /**
     * @return 生成前缀，vip 靓号 等级图标等
     */
    private static SpannableStringBuilder createPrefix(int vipType, String liangNum, int level) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int levelIndex = 0;
        String prefix = "  ";
        if (vipType != 0) {
            prefix += "  ";
            levelIndex += 2;
        }
        if (!"0".equals(liangNum)) {
            prefix += "  ";
            levelIndex += 2;
        }
        builder.append(prefix);
        if (vipType != 0) {
            //设置vip图标
            int tempVipType = IconUitl.getVipType(vipType);
            if(tempVipType==0)
            {
                tempVipType=1;
            }
            Drawable levelDrawable = ContextCompat.getDrawable(AppContext.sInstance, tempVipType);
            levelDrawable.setBounds(0, 0, DpUtil.dp2px(14), DpUtil.dp2px(14));
            VerticalImageSpan imageSpan = new VerticalImageSpan(levelDrawable);
            builder.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
//        if (!"0".equals(liangNum)) {
//            //设置靓号图标
//            Drawable levelDrawable = ContextCompat.getDrawable(AppContext.sInstance, R.mipmap.icon_liang_num);
//            levelDrawable.setBounds(0, 0, DpUtil.dp2px(14), DpUtil.dp2px(14));
//            VerticalImageSpan imageSpan = new VerticalImageSpan(levelDrawable);
//            builder.setSpan(imageSpan, levelIndex - 2, levelIndex - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        }
        //设置等级图标
//        Drawable levelDrawable = ContextCompat.getDrawable(AppContext.sInstance, IconUitl.getAudienceDrawable(level));
//        levelDrawable.setBounds(0, 0, DpUtil.dp2px(13), DpUtil.dp2px(13));
//        VerticalImageSpan imageSpan = new VerticalImageSpan(levelDrawable);
//        builder.setSpan(imageSpan, levelIndex, levelIndex + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return builder;
    }


    public static SpannableStringBuilder createEnterRoom(UserBean bean) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
//        int levelIndex = 0;
//        String prefix = "  ";
//        int vipType = bean.getVip().getType();
//        if (vipType != 0) {
//            prefix += "  ";
//            levelIndex += 2;
//        }
//        builder.append(prefix);
//        if (vipType != 0) {
//            //设置vip图标
//            Drawable levelDrawable = ContextCompat.getDrawable(AppContext.sInstance, IconUitl.getVipType(vipType));
//            levelDrawable.setBounds(0, 0, DpUtil.dp2px(20), DpUtil.dp2px(20));
//            VerticalImageSpan imageSpan = new VerticalImageSpan(levelDrawable);
//            builder.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        }
        //设置等级图标
//        Drawable levelDrawable = ContextCompat.getDrawable(AppContext.sInstance, IconUitl.getAudienceDrawable(bean.getLevel()));
//        levelDrawable.setBounds(0, 0, DpUtil.dp2px(13), DpUtil.dp2px(13));
//        VerticalImageSpan imageSpan = new VerticalImageSpan(levelDrawable);
//        builder.setSpan(imageSpan, levelIndex, levelIndex + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        int prefixLength = builder.length();
        builder.append(bean.getUser_nicename() + WordUtil.getString(R.string.enter_room));
//        if (vipType == 0) {//没有vip
            //设置用户名颜色
            builder.setSpan(sColorSpan3, prefixLength, prefixLength + bean.getUser_nicename().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        } else {
//            if (bean.getCar().getId() != 0) {//有坐骑
//                builder.setSpan(sColorSpan5, prefixLength, prefixLength + bean.getUser_nicename().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//            } else {//没有坐骑
//                builder.setSpan(sColorSpan4, prefixLength, prefixLength + bean.getUser_nicename().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//            }
//        }
        return builder;
    }

}
