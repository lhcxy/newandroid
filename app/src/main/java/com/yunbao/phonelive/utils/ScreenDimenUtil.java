package com.yunbao.phonelive.utils;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.yunbao.phonelive.AppContext;

import java.lang.reflect.Field;

/**
 * Created by cxf on 2017/10/30.
 * 获取屏幕尺寸
 */

public class ScreenDimenUtil {

    private int mStatusBarHeight;//状态栏高度
    private Resources mResources;
    private Rect mContentViewRect;

    private static ScreenDimenUtil sInstance;

    private ScreenDimenUtil() {
        mResources = AppContext.sInstance.getResources();

        //网上找的办法，使用反射在DecoderView未绘制出来之前计算状态栏的高度
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            mStatusBarHeight = mResources.getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ScreenDimenUtil getInstance() {
        if (sInstance == null) {
            synchronized (ScreenDimenUtil.class) {
                if (sInstance == null) {
                    sInstance = new ScreenDimenUtil();
                }
            }
        }
        return sInstance;
    }


    /**
     * 获取contentView的宽高
     */
    public Rect getContentViewDimens() {
        if (mContentViewRect == null) {
            DisplayMetrics dm = mResources.getDisplayMetrics();
            mContentViewRect = new Rect(0, mStatusBarHeight, dm.widthPixels, dm.heightPixels);
        }
        return mContentViewRect;
    }

}
