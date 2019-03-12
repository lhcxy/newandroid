package com.yunbao.phonelive.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.adapter.HomeHotAdapter;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.SliderBean;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.interfaces.MainEventListener;

import java.util.List;

/**
 * Created by cxf on 2017/8/9.
 */

public class HomeHotFragment extends AbsFragment implements RefreshLayout.OnRefreshListener, MainEventListener {

    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mNoZhubo;//没有主播
    private View mLoadFailure;//加载失败
    private HomeHotAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_hot;
    }

    @Override
    protected void main() {
        initView();
    }

    private void initView() {
        mRefreshLayout = (RefreshLayout) mRootView.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mNoZhubo = mRootView.findViewById(R.id.no_zhubo);
        mLoadFailure = mRootView.findViewById(R.id.load_failure);
        mRefreshLayout.setScorllView(mRecyclerView);
    }

    private void initData() {
        HttpUtil.getHot(mRefreshCallback);
    }

    private HttpCallback mRefreshCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (mLoadFailure != null && mLoadFailure.getVisibility() == View.VISIBLE) {
                mLoadFailure.setVisibility(View.GONE);
            }
            JSONObject obj = JSON.parseObject(info[0]);
            List<SliderBean> sliderList = JSON.parseArray(obj.getString("slide"), SliderBean.class);
            List<LiveBean> liveList = JSON.parseArray(obj.getString("list"), LiveBean.class);
            if (liveList.size() > 0) {
                if (mNoZhubo != null && mNoZhubo.getVisibility() == View.VISIBLE) {
                    mNoZhubo.setVisibility(View.GONE);
                }
            } else {
                if (mNoZhubo != null && mNoZhubo.getVisibility() == View.GONE) {
                    mNoZhubo.setVisibility(View.VISIBLE);
                }
            }
            if (mAdapter == null) {
                mAdapter = new HomeHotAdapter( liveList);
                mAdapter.setOnItemClickListener((item, position) -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).watchLive(item);
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setData(liveList);
            }
        }

        @Override
        public void onError(Response<JsonBean> response) {
            super.onError(response);
            if (mAdapter != null) {
                mAdapter.clearData();
            }
            if (mNoZhubo != null && mNoZhubo.getVisibility() == View.VISIBLE) {
                mNoZhubo.setVisibility(View.GONE);
            }
            if (mLoadFailure != null && mLoadFailure.getVisibility() == View.GONE) {
                mLoadFailure.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFinish() {
            if (mRefreshLayout != null) {
                mRefreshLayout.completeRefresh();
            }
        }
    };

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void loadData() {
        initData();
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_HOT);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
