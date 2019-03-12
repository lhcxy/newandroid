package com.yunbao.phonelive.ui.views;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.tools.KeyboardUtil;
import com.yunbao.phonelive.ui.tools.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 更换绑定手机
 */
public class ChangeMobileActivity extends AbsActivity {
    private TextView actionTv, smsCodeTv;
    private EditText phoneEt, smsEt;
    private ConstraintLayout constraintLayout;
    private ConstraintSet applyConstraintSet = new ConstraintSet();
    private ConstraintSet resetConstraintSet = new ConstraintSet();
    private Disposable subscribe;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_change_mobile_less;
    }

    @Override
    protected void main() {
        setTitle("验证手机");
        initView();
    }


    private void initView() {
        actionTv = findViewById(R.id.bind_mobile_action_tv);
        smsCodeTv = findViewById(R.id.bind_mobile_sms_Tv);
        smsEt = findViewById(R.id.bind_mobile_sms_et);
        phoneEt = findViewById(R.id.bind_mobile_no_et);
        constraintLayout = findViewById(R.id.root_view);
        resetConstraintSet.clone(constraintLayout);
        applyConstraintSet.clone(this, R.layout.activity_ui_change_mobile);
        initCallback();
    }

    private void initCallback() {
        addDisposable(RxView.clicks(actionTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(v -> {
            if (mStep == 0) {
                unbindPhone();
            } else if (mStep == 1) {
                bindPhone();
            }
        }));
        addDisposable(RxView.clicks(smsCodeTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(v -> {
            getSms();
        }));
    }

    public static final long DURATION = 60L;

    private void showPhoneInputViews() {
        resetSmsView();
        TransitionManager.beginDelayedTransition(constraintLayout);
        applyConstraintSet.applyTo(constraintLayout);
        setTitle("更换绑定手机");
        actionTv.setText("绑定手机");
        phoneEt.requestFocus();
        smsEt.setText("");
    }

    private void intervalTime() {
        if (!canGetSms) {
            return;
        }
        if (subscribe != null) {
            subscribe.dispose();
        }
        getSms();
        subscribe = Observable.intervalRange(0, DURATION, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aLong -> smsCodeTv.setText((DURATION - aLong) + "后重新获取"))
                .doOnComplete(() -> resetSmsView())
                .subscribe();
        disposable.add(subscribe);
        canGetSms = false;
    }

    private boolean canGetSms = true;

    private void resetSmsView() {
        canGetSms = true;
        subscribe.dispose();
        smsCodeTv.setText("获取验证码");
    }

    private int mStep = 0;

    public void unbindPhone() {
        String smsCode = smsEt.getText().toString().trim();
        if (TextUtils.isEmpty(smsCode)) {
            ToastUtil.show("请输入短信验证码!");
            return;
        }
        HttpUtil.bindPhone(AppConfig.getInstance().getUserBean().getMobile(), smsCode, 1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    showPhoneInputViews();
                    ToastUtil.show("验证成功!");
                    mStep = 1;
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    public void bindPhone() {
        String phoneNum = phoneEt.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.show("请输入新手机号码!");
            return;
        }
        String smsCode = smsEt.getText().toString().trim();
        if (TextUtils.isEmpty(smsCode)) {
            ToastUtil.show("请输入短信验证码!");
            return;
        }
        HttpUtil.bindPhone(phoneNum, smsCode, 2, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("成功绑定新手机号码!");
                    mStep = 2;
                    finish();
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    public void getSms() {
        if (mStep == 0) {
            getSmsCode(AppConfig.getInstance().getUserBean().getMobile());
        } else {
            if (TextUtils.isEmpty(phoneEt.getText().toString().trim())) {
                ToastUtil.show("请输入手机号码!");
                return;
            }
            KeyboardUtil.hideSoftInput(phoneEt);
            getSmsCode(phoneEt.getText().toString().trim());
        }
    }

    private void getSmsCode(String bindPhone) {
        HttpUtil.getBindSms(bindPhone, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                intervalTime();
            }
        });
    }


}
