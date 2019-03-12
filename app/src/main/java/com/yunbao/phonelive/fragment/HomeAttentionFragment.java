package com.yunbao.phonelive.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.interfaces.MainEventListener;
import com.yunbao.phonelive.adapter.HomeAttentionAdapter;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/8/9.
 */

public class HomeAttentionFragment extends AbsFragment implements RefreshLayout.OnRefreshListener, MainEventListener {

    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mNoZhubo;//没有主播
    private View mLoadFailure;//加载失败
    private HomeAttentionAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_attention;
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
        HttpUtil.getFollow(mRefreshCallback);
    }

    private HttpCallback mRefreshCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (mLoadFailure != null && mLoadFailure.getVisibility() == View.VISIBLE) {
                mLoadFailure.setVisibility(View.GONE);
            }
            if (info.length > 0) {
                List<LiveBean> liveList = JSON.parseArray(Arrays.toString(info), LiveBean.class);
                if (liveList.size() > 0) {
                    if (mNoZhubo.getVisibility() == View.VISIBLE) {
                        mNoZhubo.setVisibility(View.GONE);
                    }
                } else {
                    if (mNoZhubo.getVisibility() == View.GONE) {
                        mNoZhubo.setVisibility(View.VISIBLE);
                    }
                }
                if (mAdapter == null) {
                    mAdapter = new HomeAttentionAdapter(mContext, liveList);
                    mAdapter.setOnItemClickListener(new OnItemClickListener<LiveBean>() {
                        @Override
                        public void onItemClick(LiveBean item, int position) {
                            ((MainActivity) getActivity()).watchLive(item);
                        }
                    });
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.setData(liveList);
                }
            } else {
                if (mAdapter != null) {
                    mAdapter.clearData();
                }
                if (mNoZhubo != null && mNoZhubo.getVisibility() == View.GONE) {
                    mNoZhubo.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onError(Response<JsonBean> response) {
            super.onError(response);
            if (mAdapter != null) {
                mAdapter.clearData();
            }
            if (mNoZhubo.getVisibility() == View.VISIBLE) {
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
        HttpUtil.cancel(HttpUtil.GET_FOLLOW);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
