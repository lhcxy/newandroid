package com.yunbao.phonelive.ui.views;

import android.content.Intent;
import android.os.Parcel;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.bean.GiftBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.helper.MyGridLayoutManger;
import com.yunbao.phonelive.ui.views.adapter.MinePackageAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MinePackageActivity extends AbsActivity {
    private RecyclerView packageRv;
    private MinePackageAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_package;
    }

    @Override
    protected void main() {
        packageRv = findViewById(R.id.mine_package_rv);

        packageRv.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new MinePackageAdapter();
        packageRv.setAdapter(adapter);
        disposable.add(RxView.clicks(findViewById(R.id.title_back_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> finish()));

        //历史道具
        disposable.add(RxView.clicks(findViewById(R.id.title_history_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> startActivity(new Intent(MinePackageActivity.this, HistoryPackageActivity.class))));
        initData();
    }

    private void initData() {
        HttpUtil.getGiftPackage(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<GiftBean> list = JSON.parseArray(Arrays.toString(info), GiftBean.class);
                if (list == null || list.size() == 0) {
                    packageRv.setLayoutManager(new LinearLayoutManager(MinePackageActivity.this, LinearLayoutManager.VERTICAL,false));
                }
                adapter.setDatas(list);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
