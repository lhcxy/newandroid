package com.yunbao.phonelive.ui.views.fragment;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.RankItemBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.views.adapter.ContributionListAdapter;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

public class LiveContributionFragment extends BaseLazyFragment {
    private int dataType = 0; //数据类型 0 = 日榜 1 = 周榜
    private ContributionListAdapter rankHomeListAdapter;
    private RadioGroup ranklistMoreRg;
    private RecyclerView ranklistMoreRv;
    private TextView ranklistMoreHintTv;
    private NestedScrollView ranklistMoreNsv;

    public static LiveContributionFragment newInstance(String touid) {

        Bundle args = new Bundle();
        args.putString("touid", touid);
        LiveContributionFragment fragment = new LiveContributionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_live_contribution_list;
    }

    private String touid = "";

    @Override
    protected void initView() {
        if (getArguments() != null) {
            touid = getArguments().getString("touid");
        }
        ranklistMoreRg = findView(R.id.ranklist_more_rg);
        ranklistMoreRv = findView(R.id.ranklist_more_rv);
        ranklistMoreHintTv = findView(R.id.ranklist_more_hint_tv);
        ranklistMoreNsv = findView(R.id.ranklist_more_nsv);
        ranklistMoreRg.check(R.id.ranklist_more_left_rb);
        ranklistMoreRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rankHomeListAdapter = new ContributionListAdapter();
        ranklistMoreRv.setAdapter(rankHomeListAdapter);
        ranklistMoreHintTv.setText(getResources().getString(R.string.contribution_list_day_hint));
        ranklistMoreRg.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.ranklist_more_left_rb) {
                selectDayData();
            } else {
                selectWeekData();
            }
            ranklistMoreNsv.scrollTo(0, 0);
        });
    }

    private void selectDayData() {
        dataType = 0;
        ranklistMoreHintTv.setText(getResources().getString(R.string.contribution_list_day_hint));
        initData();
    }

    private void selectWeekData() {
        dataType = 1;
        ranklistMoreHintTv.setText(getResources().getString(R.string.contribution_list_week_hint));
        initData();
    }

    private List<RankItemBean> weekDatas, dayDatas;
    public static final int GET_DATA_DURATION = 3600_000;

    @Override
    protected void initData() {
        if (dataType == 0) {
            if (dayDatas == null || dayDatas.size() <= 0) {
                HttpUtil.getDayRank(touid, networkCallback);
            } else {
                if (System.currentTimeMillis() - lastGetDayTime > GET_DATA_DURATION) {
                    HttpUtil.getDayRank(touid, networkCallback);
                } else
                    rankHomeListAdapter.setDatas(dayDatas);
            }
        } else {
            if (weekDatas == null || weekDatas.size() <= 0) {
                HttpUtil.getWeekRank(touid, networkCallback);
            } else {
                if (System.currentTimeMillis() - lastGetWeekTime > GET_DATA_DURATION) {
                    HttpUtil.getWeekRank(touid, networkCallback);
                } else
                    rankHomeListAdapter.setDatas(weekDatas);
            }
        }
    }

    /**
     * 收益榜 接口回调
     */
    private HttpCallback networkCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                List<RankItemBean> list = JSON.parseArray(Arrays.toString(info), RankItemBean.class);
                if (dataType == 0) {
                    dayDatas = list;
                    rankHomeListAdapter.setDatas(dayDatas);
                    lastGetDayTime = System.currentTimeMillis();
                } else {
                    weekDatas = list;
                    rankHomeListAdapter.setDatas(weekDatas);
                    lastGetWeekTime = System.currentTimeMillis();
                }
            } else {
                ToastUtil.show(msg);
            }
        }

        @Override
        public void onFinish() {
        }
    };

    private long lastGetDayTime = 0L; //最后一次获取日榜数据的时间
    private long lastGetWeekTime = 0L; //最后一次获取周榜数据的时间
}
