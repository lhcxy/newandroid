package com.yunbao.phonelive.ui.views;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.bean.ChargeHistoryBean;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.views.adapter.ChargeHistoryAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ChargeHistoryActivity extends AbsActivity implements RefreshLayout.OnRefreshListener {
    private TextView title;
    private int pageNum = 1;
    private RecyclerView historyRv;
    private ChargeHistoryAdapter adapter;
    private RefreshLayout refreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_charge_history;
    }

    @Override
    protected void main() {
        title = findViewById(R.id.title);
        title.setText("充值记录");
        refreshLayout = findViewById(R.id.refresh_layout);
        historyRv = findViewById(R.id.charge_history_rv);
        historyRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ChargeHistoryAdapter();
//        historyRv.addItemDecoration(new CommonItemDecoration());
        historyRv.setAdapter(adapter);
        refreshLayout.setScorllView(historyRv);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.beginRefresh();
        disposable.add(RxView.clicks(findViewById(R.id.title_back_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> finish()));
    }

    private void getHistoryData() {
        HttpUtil.getChargeHistory(pageNum, callback);
    }

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null) {
                if (!TextUtils.isEmpty(Arrays.toString(info))) {
                    List<ChargeHistoryBean> datas = JSON.parseArray(Arrays.toString(info), ChargeHistoryBean.class);
                    if (pageNum == 1) {
                        adapter.setDatas(datas);
                    } else {
                        adapter.addDatas(datas);
                    }
                }
                if (info.length >= 10) {
                    refreshLayout.setCanLoadMore(true);
                } else {
                    refreshLayout.setCanLoadMore(false);
                }
            } else {
                refreshLayout.setCanLoadMore(false);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (pageNum == 1) {
                refreshLayout.completeRefresh();
            } else {
                refreshLayout.completeLoadMore();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.cancel(HttpUtil.GET_CHARGE_HISTORY);
        callback = null;
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        refreshLayout.setCanLoadMore(true);
        getHistoryData();
    }

    @Override
    public void onLoadMore() {
        pageNum++;
        getHistoryData();
    }
}
