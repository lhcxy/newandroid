package com.yunbao.phonelive;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.RefreshUserInfoEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.IMUtil;
import com.yunbao.phonelive.utils.LoginUtil;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;

import cn.jpush.im.android.eventbus.EventBus;

/**
 * Created by cxf on 2017/8/4.
 */

public class AppConfig {
    //控制 全部tablayout 数量，数量小于TAB_SCORLL_COUNT 则tablayout 不可滚动
    public static final int TAB_SCROLL_COUNT = 4;
    //   周榜月榜
    public static final String WEEK = "week";
    public static final String MONTH = "month";

    //重复点击间隔时长
    public static final long CLICK_DURATION = 600L;
    public static final int TQB_RATE = 10;
    //域名
//    public static final String HOST = "http://zhibo.2yx.cm";
    public static final String HOST = "http://www.hunanyoutu.com";
    public static final String URI = "/api/public";
    //socket的端口号
    public String mSocketServer = HOST + ":19968";
//    public String mSocketServer = HOST + ":19969";
//    public String mSocketServer = "http://testzhibo.2yx.cm:19968";

    public static final String DCMI_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

    //下载贴纸的时候保存的路径
    public static final String VIDEO_TIE_ZHI_PATH = DCMI_PATH + "/phoneLive/tieZhi/";

    //下载音乐时候，在sd卡存储音乐的路径
    public static final String MUSIC_PATH = DCMI_PATH + "/phoneLive/music/";
    //直播间进场的动画gif的存储路径
    public static final String GIF_PATH = DCMI_PATH + "/phoneLive/gif/";

    public static final String APP_PATH = DCMI_PATH + "/phoneLive/";

    //支付宝回调地址
    public static final String ALI_PAY_NOTIFY_URL = HOST + "/Appapi/Pay/notify_ali";

    //萌颜鉴权码
    public static final String BEAUTY_KEY = "f9b7b0e82f1a4160b29247c98c79d7b1";


    private static AppConfig sInstance;

    private AppConfig() {

    }

    public static AppConfig getInstance() {
        if (sInstance == null) {
            synchronized (AppConfig.class) {
                if (sInstance == null) {
                    sInstance = new AppConfig();
                    sInstance.setOnlyWifi(SharedPreferencesUtil.getInstance().getBoolean(SharedPreferencesUtil.SETTING_ISONLY_WIFI));
                }
            }
        }
        return sInstance;
    }

    public static boolean isUnlogin() {
        return TextUtils.isEmpty(SharedPreferencesUtil.getInstance().readUserInfo());
    }

    private boolean isOnlyWifi = false; //wifi下观看

    private ConfigBean mConfigBean;

    //当前登录的用户的信息
    private UserBean mUserBean;

    //这个值就是getBaseUserInfo接口里返回的info[0]
    private String mUserInfo;

    //app是否启动的标识,极光推送用到
    private boolean mLaunched;

    private String mUid = "";
    private String mToken = "";
    //用户当前所在的经度
    private String mLng = "";
    //用户当前所在的纬度
    private String mLat = "";
    //当前所在的省
    private String mProvince = "";
    //当前所在的城市
    private String mCity = "";
    //是否忽略未读消息
    private boolean mIgnoreUnReadMessage;

    private boolean mIMLogined;//IM是否登录成功

    public void reset() {
//        mConfigBean = null;
        mUserBean = null;
        mUid = "";
        mToken = "";
        mLng = "";
        mLat = "";
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public String getVersion() {
        try {
            PackageManager manager = AppContext.sInstance.getPackageManager();
            PackageInfo info = manager.getPackageInfo(AppContext.sInstance.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取当前用户对象
     *
     * @return
     */
    public UserBean getUserBean() {
        if (mUserBean == null) {//如果用户对象为空，则从SharedPreferences中获取
            mUserInfo = SharedPreferencesUtil.getInstance().readUserInfo();
            if (!"".equals(mUserInfo)) {
                mUserBean = JSON.parseObject(mUserInfo, UserBean.class);
            } else {//如果SharedPreferences还没有，则跳转到登录页面
//                LoginUtil.forwardLogin();
            }
        }
        return mUserBean;
    }

    public void refreshUserInfo() {
        if (AppConfig.isUnlogin()) {
            return;
        }
        HttpUtil.getBaseInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    saveUserInfo(info[0]);
                }
            }
        });
    }

    public void saveUserInfo(String userInfo) {
        mUserInfo = userInfo;
        mUserBean = JSON.parseObject(userInfo, UserBean.class);
        SharedPreferencesUtil.getInstance().saveUserInfo(userInfo);
        EventBus.getDefault().post(new RefreshUserInfoEvent());
    }

    public String getUserInfo() {
        if (mUserInfo == null) {
            mUserInfo = SharedPreferencesUtil.getInstance().readUserInfo();
        }
        return mUserInfo;
    }


    public ConfigBean getConfig() {
        if (mConfigBean == null) {
            String s = SharedPreferencesUtil.getInstance().readConfig();
            if (!TextUtils.isEmpty(s)) {
                mConfigBean = JSON.parseObject(s, ConfigBean.class);
            }
        }
        return mConfigBean;
    }

    public void setConfig(ConfigBean bean) {
        mConfigBean = bean;
    }

    public boolean isLaunched() {
        return mLaunched;
    }

    public void setLaunched(boolean launched) {
        mLaunched = launched;
    }

    public void setIgnoreMessage(boolean ignoreMessage) {
        if (mIgnoreUnReadMessage == ignoreMessage) {
            return;
        }
        mIgnoreUnReadMessage = ignoreMessage;
        if (ignoreMessage) {
            IMUtil.getInstance().ignoreUnReadMessage();
        }
        SharedPreferencesUtil.getInstance().saveIgnoreMessage(ignoreMessage);
    }

    public void getIgnoreMessage() {
        boolean ignoreMessage = SharedPreferencesUtil.getInstance().readIgnoreMessage();
        setIgnoreMessage(ignoreMessage);
    }

    public String getSocketServer() {
        return mSocketServer;
    }

    public void setSocketServer(String socketServer) {
        mSocketServer = socketServer;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getLng() {
        return mLng;
    }

    public void setLng(String lng) {
        mLng = lng;
    }

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        mProvince = province;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public boolean isIMLogined() {
        return mIMLogined;
    }

    public void setIMLogined(boolean IMLogined) {
        mIMLogined = IMLogined;
    }

    public boolean isOnlyWifi() {
        return isOnlyWifi;
    }

    public void setOnlyWifi(boolean onlyWifi) {
        isOnlyWifi = onlyWifi;
    }
}
