package com.yunbao.phonelive.ui.views;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.ta.utdid2.android.utils.StringUtils;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.tools.KeyboardUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

/**
 * 绑定手机
 */
public class BindMobileActivity extends AbsActivity {
    private EditText mobileEt, smsEt;
    private RelativeLayout bindSuccessRl;
    private LinearLayout bindMobileLL;
    private TextView smsTv;
    private int intentType = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_bind_mobile;
    }

    @Override
    protected void main() {
        setTitle("绑定手机");
        mobileEt = findViewById(R.id.bind_mobile_no_et);
//        verificationEt = findViewById(R.id.bind_mobile_verification_et);
        if (getIntent() != null) {
            intentType = getIntent().getIntExtra("intentType", 0);
        }
        smsEt = findViewById(R.id.bind_mobile_sms_et);
        bindSuccessRl = findViewById(R.id.bind_success_rl);
        bindMobileLL = findViewById(R.id.bind_mobile_rl);
        smsTv = findViewById(R.id.bind_mobile_sms_Tv);
        if (AppConfig.getInstance().getUserBean() != null && AppConfig.getInstance().getUserBean().getIsbind() == 1) {
            bindMobileLL.setVisibility(View.GONE);
            bindSuccessRl.setVisibility(View.VISIBLE);
        }
        initListener();
    }

    private void initListener() {
        disposable.add(RxView.clicks(findViewById(R.id.title_back_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> finish()));

        disposable.add(RxView.clicks(smsTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> getSms()));

        disposable.add(RxView.clicks(findViewById(R.id.bind_mobile_action_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> onBindMobile()));

//        disposable.add(RxView.clicks(findViewById(R.id.bind_mobile_country_title_rl)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> choseCountryCode()));
    }

    private void choseCountryCode() {
        startActivityForResult(new Intent(this, ChoseCountryCodeActivity.class), 0x01);
    }

    private void onBindMobile() {
        if (TextUtils.isEmpty(mobileEt.getText().toString())) {
            ToastUtil.show("请输入手机号码!");
            return;
        }
//        if (TextUtils.isEmpty(verificationEt.getText().toString())) {
//            ToastUtil.show("请输入图形验证码!");
//            return;
//        }
        if (TextUtils.isEmpty(smsEt.getText().toString())) {
            ToastUtil.show("请输入短信验证码");
            return;
        }
        KeyboardUtil.hideSoftInput(smsEt);
        bindPhone(mobileEt.getText().toString().trim(), smsEt.getText().toString().trim());
    }

    public void getSms() {
        if (getSmsDuration == 60) {
            if (TextUtils.isEmpty(mobileEt.getText().toString().trim())) {
                ToastUtil.show("请输入手机号码!");
                return;
            }
            KeyboardUtil.hideSoftInput(mobileEt);
            getSmsCode(mobileEt.getText().toString().trim());
        }
    }

    private int getSmsDuration = 60;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (getSmsDuration > 0) {
                handler.sendMessageDelayed(handler.obtainMessage(), 1000L);
                getSmsDuration--;
                if (smsTv != null) {
                    smsTv.setText(getSmsDuration + "s后重新获取");
                }
            } else {
                getSmsDuration = 60;
                smsTv.setText("发送验证码");
            }
        }
    };

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == ChoseCountryCodeActivity.CCC_SUCCESS_CODE && data != null) {
//            CountryCodeBean result = (CountryCodeBean) data.getSerializableExtra("result");
//            if (result != null) {
//                countryCodeTv.setText(result.getZh() + "\t\t+" + result.getCode());
//            }
//        }
//    }

    public void getSmsCode(String bindPhone) {
        HttpUtil.getBindSms2(bindPhone, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    getSmsDuration--;
                    handler.sendMessage(handler.obtainMessage());
                }else
                    ToastUtil.show(msg);

            }
        });
    }

    public void bindPhone(String phoneNum, String smsCode) {
        HttpUtil.bindPhone(phoneNum, smsCode,1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("绑定成功!");
                    bindMobileLL.setVisibility(View.GONE);
                    bindSuccessRl.setVisibility(View.VISIBLE);
                    AppConfig.getInstance().getUserBean().setIsbind(1);
                    if (intentType == 1) {
                        finish();
                    }
                } else ToastUtil.show(msg);
                getSmsDuration--;
                handler.sendMessage(handler.obtainMessage());
            }
        });
    }
}
