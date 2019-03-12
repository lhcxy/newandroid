package com.yunbao.phonelive.activity;

import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

/**
 * Created by cxf on 2017/8/18.
 * 设置页面  重置密码
 */

public class ResetPwdActivity extends AbsActivity {

    private EditText mOldPwd;
    private EditText mNewPwd;
    private EditText mConfirmPwd;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_pwd;
    }

    @Override
    protected void main() {
        setTitle(getString(R.string.reset_pwd));
        mOldPwd = (EditText) findViewById(R.id.old_pwd);
        mNewPwd = (EditText) findViewById(R.id.new_pwd);
        mConfirmPwd = (EditText) findViewById(R.id.confirm_pwd);
    }

    public void resetPwdClick(View v) {
        String oldPwd = mOldPwd.getText().toString();
        if ("".equals(oldPwd)) {
            mOldPwd.setError(getString(R.string.please_input_old_password));
            mOldPwd.requestFocus();
            return;
        }
        String newPwd = mNewPwd.getText().toString();
        if ("".equals(newPwd)) {
            mNewPwd.setError(getString(R.string.please_input_new_password));
            mNewPwd.requestFocus();
            return;
        }
        if (!newPwd.equals(mConfirmPwd.getText().toString())) {
            mConfirmPwd.setError(getString(R.string.pwd_confirm_error));
            mConfirmPwd.requestFocus();
            return;
        }

        HttpUtil.updatePass(oldPwd, newPwd, newPwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                    finish();
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.UPDATE_PASS);
        super.onDestroy();
    }
}
