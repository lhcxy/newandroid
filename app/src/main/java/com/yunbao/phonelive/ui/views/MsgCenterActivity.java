package com.yunbao.phonelive.ui.views;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.adapter.LiveWatcherVPAdapter;
import com.yunbao.phonelive.ui.views.fragment.MsgCenterDetailListFragment;
import com.yunbao.phonelive.ui.views.fragment.MsgCenterListFragment;
import com.yunbao.phonelive.ui.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MsgCenterActivity extends AbsActivity {
    private NoScrollViewPager noScrollViewPager;
    private ArrayList<Fragment> datas;
    private MsgCenterDetailListFragment msgCenterDetailListFragment;
    private ImageView searchIv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_msg_center;
    }

    @Override
    protected void main() {

        setTitle("消息");
        noScrollViewPager = findViewById(R.id.message_nsv);
        searchIv = findViewById(R.id.subtitle_iv);
        searchIv.setVisibility(View.VISIBLE);
        disposable.add(RxView.clicks(findViewById(R.id.title_back_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
                    onBackPressed();
                }
        ));
        disposable.add(RxView.clicks(searchIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
                    startActivity(new Intent(MsgCenterActivity.this, MsgSearchActivity.class));
                }
        ));
        datas = new ArrayList<>();
        MsgCenterListFragment msgCenterListFragment = MsgCenterListFragment.newInstance();
        msgCenterListFragment.setItemClickListener((item, position) -> {
            if (position == -1) {
                noScrollViewPager.setCurrentItem(1);
            } else {
                noScrollViewPager.setCurrentItem(2);
            }
        });
        noScrollViewPager.setOffscreenPageLimit(3);
        datas.add(msgCenterListFragment);
        msgCenterDetailListFragment = MsgCenterDetailListFragment.newInstance(0);
        datas.add(msgCenterDetailListFragment);
        datas.add(MsgCenterDetailListFragment.newInstance(1));
        LiveWatcherVPAdapter liveWatcherVPAdapter = new LiveWatcherVPAdapter(getSupportFragmentManager(), datas);
        noScrollViewPager.setAdapter(liveWatcherVPAdapter);

    }


    @Override
    public void onBackPressed() {
        if (noScrollViewPager.getCurrentItem() == 0) {
            finish();
        } else {
            noScrollViewPager.setCurrentItem(0);
        }
    }
}
