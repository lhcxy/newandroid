package com.yunbao.phonelive.ui.views;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.bean.HarvestHistoryBean;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.views.adapter.HarvestHistoryAdapter;

import java.util.Arrays;
import java.util.List;

public class HarvestHistoryActivity extends AbsActivity implements RefreshLayout.OnRefreshListener {
    private RefreshLayout refreshLayout;
    private RecyclerView harvestRv;
    private HarvestHistoryAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_harvest_history;
    }

    @Override
    protected void main() {
        setTitle(getString(R.string.my_harvest_history));
        refreshLayout = findViewById(R.id.refresh_layout);
        harvestRv = findViewById(R.id.harvest_history_rv);
        adapter = new HarvestHistoryAdapter();
        harvestRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        harvestRv.setAdapter(adapter);
        refreshLayout.setScorllView(harvestRv);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.beginRefresh();
    }

    private int pageNum = 1;

    private void getData() {
        HttpUtil.getHarvestHistory(pageNum, callback);
    }

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info != null) {
                if (!TextUtils.isEmpty(Arrays.toString(info))) {
                    List<HarvestHistoryBean> datas = JSON.parseArray(Arrays.toString(info), HarvestHistoryBean.class);
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
        getData();
    }

    @Override
    public void onLoadMore() {
        pageNum++;
        getData();
    }
}
