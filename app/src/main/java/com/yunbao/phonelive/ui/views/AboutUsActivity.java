package com.yunbao.phonelive.ui.views;

import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;

public class AboutUsActivity extends AbsActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void main() {
        TextView version = findViewById(R.id.about_us_version_tv);
        ((TextView) findViewById(R.id.title)).setText("关于我们");
        version.append(AppConfig.getInstance().getVersion());
    }
}
