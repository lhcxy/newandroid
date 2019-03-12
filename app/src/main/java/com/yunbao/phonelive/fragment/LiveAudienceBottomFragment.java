package com.yunbao.phonelive.fragment;

import android.view.View;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/10/9.
 */

public class LiveAudienceBottomFragment extends LiveBottomFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bottom_audience;
    }

    @Override
    protected void main() {
        super.main();
        if (AppConfig.getInstance().getConfig() == null || AppConfig.getInstance().getConfig().getShare_type() == null || AppConfig.getInstance().getConfig().getShare_type().length == 0) {
            mRootView.findViewById(R.id.btn_share).setVisibility(View.GONE);
        }
    }


}
