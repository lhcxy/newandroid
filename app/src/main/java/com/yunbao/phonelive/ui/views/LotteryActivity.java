package com.yunbao.phonelive.ui.views;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.bean.LotteryBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.views.fragment.LotteryFragment;
import com.yunbao.phonelive.ui.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LotteryActivity extends AbsActivity {
    private NoScrollViewPager contentNsvp;
    private int selectedIndex = 0;
    private View line1, line2, line3;
    private TextView cuprumTitle, silverTitle, goldTitle;
    private LotteryFragment cuprumLottery;
    private LotteryFragment silverLottery;
    private LotteryFragment goldLottery;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lottery;
    }

    @Override
    protected void main() {
        setTitle("酒桶大抽奖");
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        goldTitle = findViewById(R.id.title_gold_tv);
        silverTitle = findViewById(R.id.title_silver_tv);
        cuprumTitle = findViewById(R.id.title_cuprum_tv);

        contentNsvp = findViewById(R.id.lottery_content_nsvp);


        addDisposable(RxView.clicks(findViewById(R.id.title_cuprum_rl)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (selectedIndex == 0) {
                return;
            }
            selectedIndex = 0;
            initLine();
            contentNsvp.setCurrentItem(selectedIndex);
        }));
        addDisposable(RxView.clicks(findViewById(R.id.title_silver_rl)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (selectedIndex == 1) {
                return;
            }
            selectedIndex = 1;
            initLine();
            contentNsvp.setCurrentItem(selectedIndex);
        }));
        addDisposable(RxView.clicks(findViewById(R.id.title_gold_rl)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (selectedIndex == 2) {
                return;
            }
            selectedIndex = 2;
            initLine();
            contentNsvp.setCurrentItem(selectedIndex);
        }));
        contentNsvp.setOffscreenPageLimit(3);
        contentNsvp.setHasScrollAnimation(false);
        cuprumLottery = LotteryFragment.newInstance(0);
        silverLottery = LotteryFragment.newInstance(1);
        goldLottery = LotteryFragment.newInstance(2);
        fragments = new ArrayList<>();
        fragments.add(cuprumLottery);
        fragments.add(silverLottery);
        fragments.add(goldLottery);
        contentNsvp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        getData();
    }

    private ArrayList<Fragment> fragments;

    private void initLine() {
        line1.setVisibility(View.INVISIBLE);
        line2.setVisibility(View.INVISIBLE);
        line3.setVisibility(View.INVISIBLE);
        if (selectedIndex == 1) {
            line2.setVisibility(View.VISIBLE);
        } else if (selectedIndex == 2) {
            line3.setVisibility(View.VISIBLE);
        } else {
            line1.setVisibility(View.VISIBLE);
        }
    }

    private void getData() {
        HttpUtil.getLotteryInfo(1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    LotteryBean lotteryBean = JSON.parseObject(info[0], LotteryBean.class);
                    Log.e("//", "onSuccess: " + (lotteryBean == null));
                    cuprumLottery.setData(lotteryBean);
                    cuprumTitle.setText("x" + lotteryBean.getNum());
                }
            }
        });
        HttpUtil.getLotteryInfo(2, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    LotteryBean lotteryBean = JSON.parseObject(info[0], LotteryBean.class);
                    Log.e("//", "onSuccess: " + (lotteryBean == null));
                    silverLottery.setData(lotteryBean);
                    silverTitle.setText("x" + lotteryBean.getNum());
                }
            }
        });
        HttpUtil.getLotteryInfo(3, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    LotteryBean lotteryBean = JSON.parseObject(info[0], LotteryBean.class);
                    Log.e("//", "onSuccess: " + (lotteryBean == null));
                    goldLottery.setData(lotteryBean);
                    goldTitle.setText("x" + lotteryBean.getNum());
                }
            }
        });

    }

    public void setKeyNum(int type, int num) {
        if (type == 1) {
            silverTitle.setText("x" + num);
        } else if (type == 2) {
            goldTitle.setText("x" + num);
        } else {
            cuprumTitle.setText("x" + num);
        }
    }
}
