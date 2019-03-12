package com.yunbao.phonelive.ui.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;

import java.util.concurrent.TimeUnit;

import static com.yunbao.phonelive.utils.SharedPreferencesUtil.SETTING_ISONLY_WIFI;

public class CommonSettingActivity extends AbsActivity {
    private ImageView onlyWifiIv;
    private boolean isOnlyWifi = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_common_setting;
    }

    @Override
    protected void main() {
        onlyWifiIv = findViewById(R.id.common_setting_only_wifi_iv);
        ((TextView) findViewById(R.id.title)).setText(getResources().getText(R.string.common_setting));
        isOnlyWifi = SharedPreferencesUtil.getInstance().getBoolean(SETTING_ISONLY_WIFI);
        onlyWifiIv.setVisibility(isOnlyWifi ? View.VISIBLE : View.INVISIBLE);
        disposable.add(RxView.clicks(findViewById(R.id.common_setting_only_wifi_rl)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            isOnlyWifi = !isOnlyWifi;
            onlyWifiIv.setVisibility(isOnlyWifi ? View.VISIBLE : View.INVISIBLE);
        }));

    }

    @Override
    protected void onPause() {
        SharedPreferencesUtil.getInstance().saveBoolean(SETTING_ISONLY_WIFI, isOnlyWifi);
        AppConfig.getInstance().setOnlyWifi(isOnlyWifi);
        super.onPause();
    }
}
