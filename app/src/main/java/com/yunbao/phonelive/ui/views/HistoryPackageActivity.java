package com.yunbao.phonelive.ui.views;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;

import java.util.concurrent.TimeUnit;

public class HistoryPackageActivity extends AbsActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_history_package;
    }

    @Override
    protected void main() {
        disposable.add(RxView.clicks(findViewById(R.id.title_back_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> finish()));

    }
}
