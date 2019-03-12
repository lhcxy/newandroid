package com.yunbao.phonelive.utils;

import android.os.Handler;
import android.os.Message;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.SharedSdkBean;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by cxf on 2017/8/29.
 * sharedSDK登录 分享
 */

public class SharedSdkUitl {

    private static final int CODE_SUCCESS = 200;//成功
    private static final int CODE_ERROR = 300;//失败
    private static final int CODE_CANCEL = 400;//取消

    private static SharedSdkUitl sInstance;
    private PlatformActionListener mPlatformActionListener;
    private Handler mHandler;
    private ShareListener mListener;

    private SharedSdkUitl() {
        mPlatformActionListener = new PlatformActionListener() {

            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Message msg = Message.obtain();
                msg.what = CODE_SUCCESS;
                msg.obj = platform;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Message msg = Message.obtain();
                msg.what = CODE_ERROR;
                msg.obj = platform;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Message msg = Message.obtain();
                msg.what = CODE_CANCEL;
                msg.obj = platform;
                mHandler.sendMessage(msg);
            }
        };
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Platform platform = (Platform) msg.obj;
                switch (msg.what) {
                    case CODE_SUCCESS:
                        if (mListener != null) {
                            mListener.onSuccess(platform);
                        }
                        break;
                    case CODE_ERROR:
                        if (mListener != null) {
                            mListener.onError(platform);
                        }
                        break;
                    case CODE_CANCEL:
                        if (mListener != null) {
                            mListener.onCancel(platform);
                        }
                        break;
                }
            }
        };
    }

    public static SharedSdkUitl getInstance() {
        if (sInstance == null) {
            synchronized (SharedSdkUitl.class) {
                if (sInstance == null) {
                    sInstance = new SharedSdkUitl();
                }
            }
        }
        return sInstance;
    }

    public void releaseShareListener() {
        mListener = null;
    }


    /**
     * 登录
     *
     * @param platType 平台类型
     * @param callback 登录的回调
     */
    public void login(String platType, ShareListener callback) {
        String platName = null;
        switch (platType) {
            case SharedSdkBean.QQ:
                platName = QQ.NAME;
                break;
            case SharedSdkBean.WX:
                platName = Wechat.NAME;
                break;
            case SharedSdkBean.FACEBOOK:
                platName = Facebook.NAME;
                break;
            case SharedSdkBean.TWITTER:
                platName = Twitter.NAME;
                break;
        }
        if (platName == null) {
            return;
        }
        mListener = callback;
        Platform platform = null;
        try {
            platform = ShareSDK.getPlatform(platName);
            platform.setPlatformActionListener(mPlatformActionListener);
            platform.SSOSetting(false);
            platform.removeAccount(true);
//            if (Wechat.NAME.equals(platName)) {
//                platform.authorize();
//            }
            platform.showUser(null);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(platform);
        }

    }

    /**
     * 分享
     *
     * @param platType 平台类型
     * @param des      分享的描述
     * @param title    分享的标题
     * @param url      分享的链接
     * @param callback 分享的回调
     */
    public void share(String platType, String title, String des, String imgUrl, String url, ShareListener callback) {
        String platName = null;
        switch (platType) {
            case SharedSdkBean.QQ:
                platName = QQ.NAME;
                break;
            case SharedSdkBean.QZONE:
                platName = QZone.NAME;
                break;
            case SharedSdkBean.WX:
                platName = Wechat.NAME;
                break;
            case SharedSdkBean.WX_PYQ:
                platName = WechatMoments.NAME;
                break;
            case SharedSdkBean.FACEBOOK:
                platName = Facebook.NAME;
                break;
            case SharedSdkBean.TWITTER:
                platName = Twitter.NAME;
                break;
        }
        if (platName == null) {
            return;
        }
        mListener = callback;
        OnekeyShare oks = new OnekeyShare();
        oks.setSilent(true);
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(platName);
        oks.setTitle(title);
        oks.setText(des);
        oks.setImageUrl(imgUrl);
        oks.setUrl(url);
        oks.setSiteUrl(url);
        oks.setTitleUrl(url);
        oks.setSite(WordUtil.getString(R.string.app_name));
        oks.setCallback(mPlatformActionListener);
        oks.show(AppContext.sInstance);
    }


    public interface ShareListener {
        void onSuccess(Platform platform);

        void onError(Platform platform);

        void onCancel(Platform platform);
    }

}
