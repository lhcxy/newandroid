package com.yunbao.phonelive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.event.ConnEvent;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.LoginUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/7/23.
 */

public class ConnectivityReceiver extends BroadcastReceiver {
    private final String TAG = "网络发生变化";
    public static boolean sNetWorkBroken;//网络是否断开了


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: ---->" + intent);
        NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        if (info == null) {
            return;
        }
        String netWorkType = null;
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            netWorkType = "手机移动网络";
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            netWorkType = "wifi网络";
        }
        switch (info.getState()) {
            case CONNECTED:
                Log.e(TAG, "onReceive: ------>成功连接到" + netWorkType);
                if (sNetWorkBroken) {
                    sNetWorkBroken = false;
                    Log.e(TAG, "onReceive: ------>重连成功" + netWorkType);
                    EventBus.getDefault().post(new ConnEvent(ConnEvent.CONN_OK));
                    HttpUtil.init();
                    String uid = AppConfig.getInstance().getUid();
                    if (!"".equals(uid)) {
                        LoginUtil.startThridLibray();
//                        LocationUtil.getInstance().startLocation();//开启高德定位
                        if (!"".equals(AppConfig.getInstance().getToken())) {
                            AppConfig.getInstance().refreshUserInfo();//刷新用户信息
                        }
                        HttpUtil.getConfig(null);//获取配置信息
                    }
                    Log.e(TAG, "onReceive: ------>发送有网的事件");
                }

                break;
            case DISCONNECTED:
                Log.e(TAG, "onReceive: ------>与" + netWorkType + "断开连接");
                sNetWorkBroken = true;
                EventBus.getDefault().post(new ConnEvent(ConnEvent.CONN_ERROR));
                Log.e(TAG, "onReceive: ------>发送断网的事件");
                break;
        }
    }


    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.sInstance.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if (networkInfos != null && networkInfos.length > 0) {
                for (int i = 0; i < networkInfos.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
