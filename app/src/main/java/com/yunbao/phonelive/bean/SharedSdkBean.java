package com.yunbao.phonelive.bean;

import android.support.annotation.DrawableRes;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.WordUtil;

import java.io.Serializable;

/**
 * Created by cxf on 2017/9/28.
 */

public class SharedSdkBean implements Serializable {
    private String type;
    private int drawable;
    private String title;
    private boolean checked;
    private int drawableSelected;

    public SharedSdkBean() {
    }

    public SharedSdkBean(String type, int drawable, String title) {
        this.type = type;
        this.drawable = drawable;
        this.title = title;
    }

    public SharedSdkBean(String type, int drawable, String title, int drawableSelected) {
        this.type = type;
        this.drawable = drawable;
        this.title = title;
        this.drawableSelected = drawableSelected;
    }

    public int getDrawableSelected() {
        return drawableSelected;
    }

    public void setDrawableSelected(int drawableSelected) {
        this.drawableSelected = drawableSelected;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public static final String QQ = "qq";//qq
    public static final String QZONE = "qzone";//qq空间
    public static final String WX = "wx";//微信
    public static final String WX_PYQ = "wchat";//微信朋友圈
    public static final String FACEBOOK = "facebook";//脸书
    public static final String TWITTER = "twitter";//推特
    public static final String WEIBO = "weibo";//推特

    public static SharedSdkBean create(String type) {
        SharedSdkBean bean = null;
        switch (type) {
            case QQ:
                bean = new SharedSdkBean(type, R.mipmap.icon_plat_qq_selected, "QQ", R.mipmap.icon_plat_qq);
//                bean = new SharedSdkBean(type, R.mipmap.icon_plat_qq, "QQ");
                break;
            case QZONE:
                bean = new SharedSdkBean(type, R.mipmap.icon_plat_qzone, WordUtil.getString(R.string.qzone), R.mipmap.icon_plat_qzone);
//                bean = new SharedSdkBean(type, R.mipmap.icon_plat_qzone, WordUtil.getString(R.string.qzone));
                break;
            case WEIBO:
                bean = new SharedSdkBean(type, R.mipmap.ic_ui_live_share_sina_selected, WordUtil.getString(R.string.weibo), R.mipmap.ic_ui_live_share_sina);
//                bean = new SharedSdkBean(type, R.mipmap.icon_plat_weibo, WordUtil.getString(R.string.weibo));
                break;
            case WX:
                bean = new SharedSdkBean(type, R.mipmap.icon_plat_wx_selected, WordUtil.getString(R.string.wechat), R.mipmap.icon_plat_wx);
//                bean = new SharedSdkBean(type, R.mipmap.icon_plat_wx, WordUtil.getString(R.string.wechat));
                break;
            case WX_PYQ:
                bean = new SharedSdkBean(type, R.mipmap.icon_plat_wxpyq, WordUtil.getString(R.string.pengyouquan), R.mipmap.icon_plat_wxpyq_selected);
//                bean = new SharedSdkBean(type, R.mipmap.icon_plat_wxpyq, WordUtil.getString(R.string.pengyouquan));
                break;
            case FACEBOOK:
                bean = new SharedSdkBean(type, R.mipmap.icon_plat_facebook, "facebook", R.mipmap.icon_plat_facebook);
                break;
            case TWITTER:
                bean = new SharedSdkBean(type, R.mipmap.icon_plat_twitter, "twitter", R.mipmap.icon_plat_twitter);
                break;
        }
        return bean;
    }


    public static SharedSdkBean createLogin(String type) {
        SharedSdkBean bean = null;
        switch (type) {
            case QQ:
                bean = new SharedSdkBean(type, R.mipmap.ic_ui_qq_login_normal, "");
                break;
            case QZONE:
                bean = new SharedSdkBean(type, R.mipmap.icon_plat_qzone, "");
                break;
            case WEIBO:
                bean = new SharedSdkBean(type, R.mipmap.ic_ui_sina_login_normal, "");
                break;
            case WX:
                bean = new SharedSdkBean(type, R.mipmap.ic_ui_wx_login_normal, "");
                break;
            case WX_PYQ:
                bean = new SharedSdkBean(type, R.mipmap.icon_plat_wxpyq, "");
                break;
            case FACEBOOK:
                bean = new SharedSdkBean(type, R.mipmap.icon_plat_facebook, "");
                break;
            case TWITTER:
                bean = new SharedSdkBean(type, R.mipmap.icon_plat_twitter, "");
                break;
        }
        return bean;
    }
}
