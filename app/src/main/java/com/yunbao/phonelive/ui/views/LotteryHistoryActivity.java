package com.yunbao.phonelive.ui.views;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.bean.LotteryGetInfoBean;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.ui.views.adapter.LotteryHistoryRvAda;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;

public class LotteryHistoryActivity extends AbsActivity {

    private LotteryHistoryRvAda adapter;
    private RefreshLayout refreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lottery_history;
    }

    private RecyclerView contentRv;

    @Override
    protected void main() {
        setTitle("历史记录");
        refreshLayout = findViewById(R.id.body_refl);
        contentRv = findViewById(R.id.body_rv);
        contentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new LotteryHistoryRvAda();
        contentRv.setAdapter(adapter);
        callback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    List<LotteryGetInfoBean> datas = JSON.parseArray(Arrays.toString(info), LotteryGetInfoBean.class);
                    if (adapter != null) {
                        adapter.addData(datas);
                    }
                }
                refreshLayout.completeLoadMore();
                if (code == 1001) {
                    refreshLayout.setCanLoadMore(false);
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                refreshLayout.setCanLoadMore(false);
                refreshLayout.completeLoadMore();
            }
        };
        refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.completeRefresh();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getData();
            }
        });
        refreshLayout.setCanLoadMore(true);
        refreshLayout.setScorllView(contentRv);
        getData();
    }

    private int pageNum = 1;

    private void getData() {
        HttpUtil.getLotteryHistory(pageNum, callback);
    }

    private HttpCallback callback;
}
