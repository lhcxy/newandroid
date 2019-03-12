package com.yunbao.phonelive.activity;

import android.content.Intent;
import android.view.View;
import android.widget.RadioButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

/**
 * Created by cxf on 2017/8/17.
 */

public class SetSexActivity extends AbsActivity {

    private RadioButton mNan;
    private RadioButton mNv;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_sex;
    }

    @Override
    protected void main() {
        setTitle(getString(R.string.update_sex));
        mNan = (RadioButton) findViewById(R.id.nan);
        mNv = (RadioButton) findViewById(R.id.nv);
        showResult(getIntent().getIntExtra("sex", 1));
    }

    public void setSexClick(View v) {
        switch (v.getId()) {
            case R.id.nan:
                updateSex(1);
                break;
            case R.id.nv:
                updateSex(2);
                break;
        }
    }

    private void showResult(int sex) {
        if (sex == 1) {
            mNan.setChecked(true);
        } else {
            mNv.setChecked(true);
        }
    }

    private void updateSex(final int sex) {
        HttpUtil.updateFields("{\"sex\":\"" + sex + "\"}", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                ToastUtil.show(obj.getString("msg"));
                if (code == 0) {
                    showResult(sex);
                    Intent intent = getIntent();
                    intent.putExtra("sex", sex);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.UPDATE_FIELDS);
        super.onDestroy();
    }
}
