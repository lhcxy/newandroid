package com.yunbao.phonelive.ui.views.fragment;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.tools.KeyboardUtil;
import com.yunbao.phonelive.ui.views.AnchorCertificateActivity;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

public class CertificatePhoneFragment extends BaseLazyFragment {
    private EditText mobileEt, smsEt;
    private RelativeLayout bindSuccessRl;
    private LinearLayout bindMobileLL;
    private TextView smsTv, commitTv;

    public static CertificatePhoneFragment newInstance() {
        CertificatePhoneFragment fragment = new CertificatePhoneFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_certificate_phone;
    }

    @Override
    protected void initView() {
        mobileEt = findView(R.id.bind_mobile_no_et);
        smsEt = findView(R.id.bind_mobile_sms_et);
        bindSuccessRl = findView(R.id.bind_success_rl);
        bindMobileLL = findView(R.id.bind_mobile_rl);
        smsTv = findView(R.id.bind_mobile_sms_Tv);
        commitTv = findView(R.id.bind_mobile_action_tv);
        initListener();
    }

    private void initListener() {
        disposable.add(RxView.clicks(smsTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> getSms()));
        disposable.add(RxView.clicks(commitTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (AppConfig.getInstance().getUserBean() != null && AppConfig.getInstance().getUserBean().getIsbind() == 1) {
                if (getActivity() instanceof AnchorCertificateActivity) {
                    ((AnchorCertificateActivity) getActivity()).jump2TakePhoto();
                }
            } else {
                onBindMobile();
            }
        }));
    }


    @Override
    protected void initData() {
        if (AppConfig.getInstance().getUserBean() != null && AppConfig.getInstance().getUserBean().getIsbind() == 1) {
            bindMobileLL.setVisibility(View.GONE);
            bindSuccessRl.setVisibility(View.VISIBLE);
            commitTv.setText("下一步");
        } else {
            bindMobileLL.setVisibility(View.VISIBLE);
            bindSuccessRl.setVisibility(View.GONE);
            commitTv.setText("提交");
        }
    }

    private void onBindMobile() {
        if (TextUtils.isEmpty(mobileEt.getText().toString())) {
            ToastUtil.show("请输入手机号码!");
            return;
        }
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

    public void getSmsCode(String bindPhone) {
        HttpUtil.getBindSms(bindPhone, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                getSmsDuration--;
                handler.sendMessage(handler.obtainMessage());
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
                    AppConfig.getInstance().getUserBean().setMobile(phoneNum);
                    AppConfig.getInstance().getUserBean().setIsbind(1);
                    commitTv.setText("下一步");
                } else ToastUtil.show(msg);
                getSmsDuration--;
                handler.sendMessage(handler.obtainMessage());
            }
        });
    }
}
