package com.yunbao.phonelive.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.im.IMUtil;
import com.yunbao.phonelive.ui.TestActivity;
import com.yunbao.phonelive.ui.views.LotteryActivity;
import com.yunbao.phonelive.utils.IntervalCountDown;
import com.yunbao.phonelive.utils.JPushUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.LoginUtil;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.sharesdk.framework.ShareSDK;

/**
 * 启动页面
 */
public class LauncherActivity extends AbsActivity {

    @Override
    protected int getLayoutId() {

        return R.layout.activity_launcher;
    }

    @Override
    public void preOnCreate() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.preOnCreate();
    }

    @Override
    protected void main() {
        //开启倒计时
        startCountDown();
        readUidAndToken();
        //初始化http

        //初始化极光推送
        JPushUtil.init();
        //初始化sharedSdk
        ShareSDK.initSDK(AppContext.sInstance);
        //初始化IM
//        JMessageClient.init(AppContext.sInstance);
        AppConfig.getInstance().setLaunched(true);
    }


    /**
     * 启动定时器，3秒后跳转
     */
    private void startCountDown() {
        final int targetCount = 2;
        new IntervalCountDown(targetCount, count -> {
            L.e("LauncherActivity 定时器-->" + count);
            Bundle jpusheventBundle = LauncherActivity.this.getIntent().getBundleExtra("jpusheventBundle");
            if (count == targetCount) {
//                readUidAndToken();

                if (getIntent() != null && getIntent().getData() != null) {
                    Uri data = getIntent().getData();
                    String roomId = data.getQueryParameter("roomId");
                    String stream = data.getQueryParameter("stream");
                    Log.e("//", roomId + "startCountDown: " + stream);
                    if (!TextUtils.isEmpty(roomId) && !TextUtils.isEmpty(stream)) {
//                        ToastUtil.show(roomId);
                        MainActivity.startMainActivity(LauncherActivity.this, roomId, stream);
                        finish();
                    } else {
                        MainActivity.startMainActivity(LauncherActivity.this, jpusheventBundle);
                        finish();
                    }
                } else {
                    MainActivity.startMainActivity(LauncherActivity.this, jpusheventBundle);
                    finish();
                }


//                    startActivity(new Intent(LauncherActivity.this, LiveReportActivity.class));
//                    finish();
//                    MainActivity.startMainActivity(LauncherActivity.this, LauncherActivity.this.getIntent().getBundleExtra("jpusheventBundle"));
//                    finish();
            }
        }).start();
    }

    /**
     * 从SharedPreferences中读取用户uid和token，
     * 如果有，验证uid和token
     * 如果没有，则跳转到登录页面
     */
    private void readUidAndToken() {
        String[] uidAndToken = SharedPreferencesUtil.getInstance().readUidAndToken();
        if (uidAndToken != null) {
            validateUidAndToken(uidAndToken[0], uidAndToken[1]);
        }
//        else {
//            L.e("不存在用户信息-->跳转到登录页面");
////            LoginUtil.forwardLogin();
//        }
    }

    /**
     * 验证uid和token是否过期
     */
    private void validateUidAndToken(final String uid, final String token) {
        HttpUtil.ifToken(uid, token, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    //token没有过期
                    AppConfig.getInstance().setUid(uid);
                    AppConfig.getInstance().setToken(token);
                    LoginUtil.startThridLibray();//启动三方库 IM 极光等
                    MainActivity.startMainActivity(LauncherActivity.this, LauncherActivity.this.getIntent().getBundleExtra("jpusheventBundle"));
//                    startActivity(new Intent(LauncherActivity.this, TestActivity.class));
                    finish();
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                AppConfig.getInstance().setUid("");
                AppConfig.getInstance().setToken("");
                MainActivity.startMainActivity(LauncherActivity.this, LauncherActivity.this.getIntent().getBundleExtra("jpusheventBundle"));
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.IF_TOKEN);
        super.onDestroy();
    }
}
