package com.yunbao.phonelive.ui.views;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.activity.EditProfileActivity;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

public class FeedBackActivity extends AbsActivity {
    private EditText contentEt;
    private TextView title, commitTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_feedback;
    }

    @Override
    protected void main() {
        contentEt = findViewById(R.id.feedback_content_et);
        title = findViewById(R.id.title);
        title.setText("意见反馈");

        disposable.add(RxView.clicks(findViewById(R.id.feedback_commit_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> commitFeedback()));
        disposable.add(RxView.clicks(findViewById(R.id.title_back_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> finish()));
    }

    private void commitFeedback() {
        if (TextUtils.isEmpty(contentEt.getText().toString().trim())) {
            ToastUtil.show("请输入您的宝贵意见!");
            return;
        }

        if (AppConfig.isUnlogin()) {
            unloginHint(title);
            return;
        }
        HttpUtil.commitFeedback(contentEt.getText().toString().trim(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("意见提交成功，感谢您的反馈!");
                    finish();
                }
            }
        });
    }


}
