package com.yunbao.phonelive.utils;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.im.IMUtil;
import com.yunbao.phonelive.im.JIMUtil;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


/**
 * Created by cxf on 2017/8/3.
 */

public class JPushUtil {

    public static final String TAG = "极光推送";
    public static boolean isSetAlians;

    public static void init() {

        if (!AppConfig.isUnlogin()) {
//            setAlias(AppConfig.getInstance().getUid());

            setAlias(AppConfig.getInstance().getUid());//给极光推送设置别名
            IMUtil.getInstance().init(IMUtil.JIM);
//            IMUtil.getInstance().loginClient(AppConfig.getInstance().getUid());//登录IM
        }
    }

    public static void setAlias(String uid) {
        if (JPushInterface.isPushStopped(AppContext.sInstance)) {
            JPushInterface.resumePush(AppContext.sInstance);
        }
        if (!isSetAlians) {
            JPushInterface.setAlias(AppContext.sInstance, JIMUtil.PREFIX + uid, new TagAliasCallback() {

                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    L.e(TAG, "设置别名---->" + s);
                    isSetAlians = true;
                }
            });
        }
    }

    public static void stopPush() {
        JPushInterface.stopPush(AppContext.sInstance);
        isSetAlians = false;
    }
}
