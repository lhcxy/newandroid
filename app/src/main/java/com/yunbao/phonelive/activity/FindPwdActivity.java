package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.IntervalCountDown;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.ValidateUitl;

/**
 * Created by cxf on 2017/9/28.
 * 找回密码
 */

public class FindPwdActivity extends AbsActivity {
    private TextView mValidateCodeBtn;
    private EditText mPhoneNum;
    private EditText mValidateCode;
    private EditText mPwd;
    private EditText mConfirmPwd;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_pwd;
    }

    @Override
    protected void main() {
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.find_pass));
        mValidateCodeBtn = (TextView) findViewById(R.id.btn_validate_code);
        mPhoneNum = (EditText) findViewById(R.id.phone_num);
        mValidateCode = (EditText) findViewById(R.id.validate_code);
        mPwd = (EditText) findViewById(R.id.pwd);
        mConfirmPwd = (EditText) findViewById(R.id.confirm_pwd);
    }

    public void findClick(View v) {
        switch (v.getId()) {
            case R.id.btn_validate_code:
                getValidateCode();
                break;
            case R.id.btn_find:
                findPwd();
                break;
        }
    }

    private void getValidateCode() {
        String phoneNum = mPhoneNum.getText().toString();
        if ("".equals(phoneNum)) {
            mPhoneNum.setError(getString(R.string.phone_num_empty));
            mPhoneNum.requestFocus();
            return;
        }
        HttpUtil.getValidateCode2(phoneNum, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show(getString(R.string.code_send));
                    mValidateCodeBtn.setEnabled(false);
                    new IntervalCountDown(60, new IntervalCountDown.Callback() {
                        @Override
                        public void callback(int count) {
                            int last = 60 - count;
                            mValidateCodeBtn.setText(last + " s");
                            if (last == 0) {
                                mValidateCodeBtn.setText(getString(R.string.send_validate_code));
                                mValidateCodeBtn.setEnabled(true);
                            }
                        }
                    }).start();
                } else {
                    ToastUtil.show(msg);
                }
            }
        });

    }

    private void findPwd() {
        final String phoneNum = mPhoneNum.getText().toString();
        if ("".equals(phoneNum)) {
            mPhoneNum.setError(getString(R.string.phone_num_empty));
            mPhoneNum.requestFocus();
            return;
        }
        if (!ValidateUitl.validateMobileNumber(phoneNum)) {
            mPhoneNum.setError(getString(R.string.phone_num_error));
            mPhoneNum.requestFocus();
            return;
        }
        String validateCode = mValidateCode.getText().toString();
        if ("".equals(validateCode)) {
            mValidateCode.setError(getString(R.string.validate_code_empty));
            mValidateCode.requestFocus();
            return;
        }
        final String pwd = mPwd.getText().toString();
        if ("".equals(pwd)) {
            mPwd.setError(getString(R.string.pwd_empty));
            mPwd.requestFocus();
            return;
        }
        if (!pwd.equals(mConfirmPwd.getText().toString())) {
            mConfirmPwd.setError(getString(R.string.pwd_confirm_error));
            mConfirmPwd.requestFocus();
            return;
        }

        HttpUtil.findPwd(phoneNum, pwd, pwd, validateCode, mCallback);
    }

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                ToastUtil.show(getString(R.string.reset_pwd_success));
                finish();
            }else{
                ToastUtil.show(msg);
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUitl.loadingDialog(mContext, getString(R.string.waiting));
        }
    };

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_VALIDATE_CODE2);
        HttpUtil.cancel(HttpUtil.FIND_PWD);
        super.onDestroy();
    }
}
