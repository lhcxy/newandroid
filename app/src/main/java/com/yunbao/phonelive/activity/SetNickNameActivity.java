package com.yunbao.phonelive.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.JIMUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

/**
 * Created by cxf on 2017/8/17.
 * 设置昵称
 */

public class SetNickNameActivity extends AbsActivity {

    private EditText mInput;
    private InputMethodManager imm;
    private String mContent;
    private TextView nameTv, nameCoinTv, hintTv;
    private ForegroundColorSpan giftNameSp;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_nickname;
    }

    @Override
    protected void main() {
        mInput = (EditText) findViewById(R.id.input);
        nameTv = findViewById(R.id.user_name_tv);
        nameCoinTv = findViewById(R.id.change_name_coin_tv);
        hintTv = findViewById(R.id.change_name_hint_tv);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setTitle(getString(R.string.update_nickname));
        giftNameSp = new ForegroundColorSpan(getResources().getColor(R.color.app_selected_color));
        addDisposable(RxView.clicks(findViewById(R.id.btn_save)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> save()));
        addDisposable(RxView.clicks(findViewById(R.id.jump_charge)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            startActivity(new Intent(SetNickNameActivity.this, ChargeActivity.class));
        }));
        hintTv.setText(Html.fromHtml(getString(R.string.change_name_rule)));
        getChangePayInfo();
    }

    private void save() {
        imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        mContent = mInput.getText().toString();
        if (TextUtils.isEmpty(mContent)) {
            ToastUtil.show(getString(R.string.nickname_empty));
            return;
        }
        if (mContent.length() > (20)) {
            ToastUtil.show(getString(R.string.nickname_length_error));
            return;
        }
        String fields = "{\"" + "user_nicename" + "\":\"" + mContent + "\"}";
        HttpUtil.updateFields(fields, mCallback);
    }

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                Intent intent = getIntent();
                JIMUtil.getInstance().updateNickName(mContent);
                intent.putExtra("name", mContent);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                ToastUtil.show(msg);
            }
        }
    };

    @Override
    public void onBackPressed() {
        imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.UPDATE_FIELDS);
        super.onDestroy();
    }

    private void getChangePayInfo() {
        HttpUtil.getChangeNameConfig(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    JSONObject object = JSON.parseObject(info[0]);
                    int namecoin = object.getIntValue("namecoin");
                    String user_nicename = object.getString("user_nicename");
                    nameTv.setText("昵称:" + user_nicename);
                    String coin = object.getString("coin");
                    if (!TextUtils.isEmpty(coin)) {
                        AppConfig.getInstance().getUserBean().setCoin(coin);
                    }
                    SpannableString roomIdStr;
                    if (namecoin == 0) {
                        roomIdStr = new SpannableString("本次修改昵称免费");
                        roomIdStr.setSpan(giftNameSp, 6, roomIdStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    } else {
                        roomIdStr = new SpannableString(String.format(getResources().getString(R.string.change_coin_txt), namecoin));
                        roomIdStr.setSpan(giftNameSp, 9, roomIdStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                    nameCoinTv.setText(roomIdStr);
                }
            }
        });
    }
}
