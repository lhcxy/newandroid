package com.yunbao.phonelive;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.yunbao.phonelive.http.HttpUtil;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;

//import com.squareup.leakcanary.LeakCanary;


/**
 * Created by cxf on 2017/8/3.
 */

public class AppContext extends MultiDexApplication {
    public static AppContext sInstance;
    //public static RefWatcher sRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //sRefWatcher = LeakCanary.install(this);
        LeakCanary.install(this);
        CrashReport.initCrashReport(getApplicationContext(),"0c813ad7d6",false);
        //初始化萌颜
//        TiSDK.init(AppConfig.BEAUTY_KEY, this);
//        initTypeface();
        HttpUtil.init();
//        Phoenix.config()
//                .imageLoader((mContext, imageView, imagePath, type) -> Glide.with(mContext)
//                        .load(imagePath)
//                        .into(imageView));
        JPushInterface.setDebugMode(true);
        JMessageClient.setDebugMode(true);
        JPushInterface.init(this);
//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
//        builder.statusBarDrawable = R.drawable.jmessage_notification_icon;
//        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
//        builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
//        JPushInterface.setPushNotificationBuilder(1, builder);
        JMessageClient.init(this, true);



    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

//    private void initTypeface() {
//        try {
//            Field field = Typeface.class.getDeclaredField("SERIF");
//            field.setAccessible(true);
//            field.set(null, Typeface.createFromAsset(getAssets(), "fonts/DFYuanW7-GB2312.ttf"));
//            Log.e("//", "initTypeface:  success");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//            Log.e("//", "initTypeface:  NoSuchFieldException " + e.getMessage());
//        } catch (IllegalAccessException e) {
//            Log.e("//", "initTypeface: IllegalAccessException " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

}
