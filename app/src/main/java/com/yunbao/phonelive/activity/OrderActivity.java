package com.yunbao.phonelive.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.fragment.LiveOrderFragment;

/**
 * Created by cxf on 2017/8/12.
 * 映票排行榜
 */

public class OrderActivity extends AbsActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_fragment;
    }

    @Override
    protected void main() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        LiveOrderFragment fragment = new LiveOrderFragment();
        fragment.setArguments(getIntent().getExtras());
        ft.replace(R.id.replaced, fragment);
        ft.commit();
    }
}
