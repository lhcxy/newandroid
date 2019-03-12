package com.yunbao.phonelive.ui.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.activity.HarvestActivity;
import com.yunbao.phonelive.bean.SharedSdkBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.tools.KeyboardUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.SharedSdkUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 绑定微信
 */
public class BindWechatActivity extends AbsActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_bind_wechat;
    }

    private TextView actionTv, hintTv;
    private ImageView statusIv;

    @Override
    protected void main() {
        setTitle("绑定微信");
        actionTv = findViewById(R.id.bind_mobile_action_tv);
        hintTv = findViewById(R.id.empty_tv);
        statusIv = findViewById(R.id.empty_iv);
        findViewById(R.id.title_line_v).setVisibility(View.INVISIBLE);
        initListener();
    }

    int wxtype = 0;
    private String wxName = "";

    private void initListener() {

        disposable.add(RxView.clicks(actionTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {

            if (wxtype == 0) {
                onBindWx();
            } else {
                unBindWx();
            }


        }));

        if (AppConfig.getInstance().getUserBean() != null) {
            wxtype = AppConfig.getInstance().getUserBean().getWxtype();
            openId = AppConfig.getInstance().getUserBean().getWxopenid();
            wxName = AppConfig.getInstance().getUserBean().getWeixin();
            if (wxtype == 0) {
                actionTv.setText("绑定微信");
                hintTv.setText("");
            } else {
                actionTv.setText("解绑微信");
                hintTv.setText(wxName);
            }
        } else finish();

    }


    private Dialog mLoginAuthDialog;

    private void onBindWx() {
        if (mLoginAuthDialog == null) {
            mLoginAuthDialog = DialogUitl.loginAuthDialog(this);
        }
        mLoginAuthDialog.show();
        SharedSdkUitl.getInstance().login(SharedSdkBean.WX, mShareListener);
    }


    private void unBindWx() {

        DialogUitl.confirmNoTitleDialog(this, "已绑定微信" + wxName + "，请问是否改绑？", "改绑", true, new DialogUitl.Callback() {
            @Override
            public void confirm(Dialog dialog) {
                if (mLoginAuthDialog == null) {
                    mLoginAuthDialog = DialogUitl.loginAuthDialog(BindWechatActivity.this);
                }
                mLoginAuthDialog.show();
                wxName = "";
                HttpUtil.onBindWx(openId, 2, "", callback);
            }

            @Override
            public void cancel(Dialog dialog) {
            }
        }).show();


    }


    //第三方登录回调
    private SharedSdkUitl.ShareListener mShareListener = new SharedSdkUitl.ShareListener() {
        @Override
        public void onSuccess(Platform platform) {
            ToastUtil.show(getString(R.string.auth_success));
            final PlatformDb platDB = platform.getDb();
            if (platDB.getPlatformNname().equals(Wechat.NAME)) {
                openId = platDB.get("unionid");
                wxName = platDB.get("nickname");
                HttpUtil.onBindWx(openId, 1, wxName, callback);
            }
        }

        @Override
        public void onError(Platform platform) {
            if (mLoginAuthDialog != null) {
                mLoginAuthDialog.dismiss();
            }
            ToastUtil.show(getString(R.string.auth_failure));
        }

        @Override
        public void onCancel(Platform platform) {
            if (mLoginAuthDialog != null) {
                mLoginAuthDialog.dismiss();
            }
            ToastUtil.show(getString(R.string.auth_cancle));
        }
    };

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                wxtype = wxtype == 0 ? 1 : 0;
                if (wxtype == 0) {
                    actionTv.setText("绑定微信");
                    hintTv.setText("");
                } else {
                    actionTv.setText("解绑微信");
                    hintTv.setText(wxName);
                }
                AppConfig.getInstance().getUserBean().setWxtype(wxtype);
                AppConfig.getInstance().getUserBean().setWxopenid(wxtype == 0 ? "" : openId);
                AppConfig.getInstance().getUserBean().setWeixin(wxName);
            }
            ToastUtil.show(msg);
            if (wxtype == 0) {
                finish();
            }
        }

        @Override
        public void onFinish() {
            if (mLoginAuthDialog != null) {
                mLoginAuthDialog.dismiss();
            }
            super.onFinish();
        }
    };


    private String openId = "";


}
