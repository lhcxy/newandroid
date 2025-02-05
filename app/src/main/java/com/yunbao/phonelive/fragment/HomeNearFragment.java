package com.yunbao.phonelive.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.adapter.HomeNearAdapter;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.glide.ItemDecoration;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.interfaces.MainEventListener;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/8/9.
 */

public class HomeNearFragment extends AbsFragment implements RefreshLayout.OnRefreshListener, MainEventListener {

    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mNoZhubo;//没有主播
    private View mLoadFailure;//加载失败
    private HomeNearAdapter mAdapter;
    private TextView mRedPoint;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_near;
    }

    @Override
    protected void main() {
        initView();
    }

    private void initView() {
        mRedPoint = (TextView) mRootView.findViewById(R.id.red_point);
        mRefreshLayout = (RefreshLayout) mRootView.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        //网格的分割线
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 4, 4);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mNoZhubo = mRootView.findViewById(R.id.no_zhubo);
        mLoadFailure = mRootView.findViewById(R.id.load_failure);
        mRefreshLayout.setScorllView(mRecyclerView);
    }

    @Override
    public void loadData() {
        initData();
    }

    private void initData() {
        HttpUtil.getNear(AppConfig.getInstance().getLng(), AppConfig.getInstance().getLat(), mCallback);
    }

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (mLoadFailure != null && mLoadFailure.getVisibility() == View.VISIBLE) {
                mLoadFailure.setVisibility(View.GONE);
            }
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
                mAdapter = new HomeNearAdapter(mContext, liveList);
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
        }
    }


    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_NEAR);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setUnReadCount(int count) {
        if (count > 0) {
            if (mRedPoint.getVisibility() == View.GONE) {
                mRedPoint.setVisibility(View.VISIBLE);
            }
            mRedPoint.setText(String.valueOf(count));
        } else {
            if (mRedPoint.getVisibility() == View.VISIBLE) {
                mRedPoint.setVisibility(View.GONE);
            }
        }
    }
}
