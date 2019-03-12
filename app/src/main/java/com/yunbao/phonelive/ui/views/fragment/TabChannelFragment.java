package com.yunbao.phonelive.ui.views.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.adapter.HomeHotAdapter;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.presenter.CheckLivePresenter;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.helper.GridSpacingItemDecoration;
import com.yunbao.phonelive.ui.views.adapter.ChannelContentAdapter;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.Arrays;
import java.util.List;

public class TabChannelFragment extends BaseLazyFragment implements RefreshLayout.OnRefreshListener {

    private RecyclerView channelRV;
    public static final int SPAN_COUNT = 2;
    private String title = "";
    private ChannelContentAdapter channelContentAdapter;
    private RelativeLayout emptyRootRl;
    private RefreshLayout mRefreshLayout;
    private GridLayoutManager layoutManager;

    public static TabChannelFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        TabChannelFragment fragment = new TabChannelFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_channel;
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            title = getArguments().getString("title", "");
        }
        emptyRootRl = findView(R.id.item_ui_live_root_rl);
        mRefreshLayout = findView(R.id.refresh_layout);
        channelRV = findView(R.id.tab_channel_guide_rv);
        layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        channelContentAdapter = new ChannelContentAdapter();
        channelContentAdapter.setOnItemClickListener((item, position) -> watchLive(item));
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if (channelContentAdapter.isNoMore() && channelContentAdapter.getItemCount() - 1 == i) {
                    return 2;
                }
                return 1;
            }
        });
        channelRV.setLayoutManager(layoutManager);
        channelRV.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, DpUtil.dp2px(8), true));

        channelRV.setAdapter(channelContentAdapter);
        mRefreshLayout.setScorllView(channelRV);
        mRefreshLayout.setOnRefreshListener(this);
    }

    List<LiveBean> datas;
    private int pageNum = 1;

    @Override
    protected void initData() {
        HttpUtil.getLiveByTag(title, pageNum, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    datas = JSON.parseArray(Arrays.toString(info), LiveBean.class);
                    if (datas != null && datas.size() > 0) {
                        if (channelContentAdapter == null) {
                            channelContentAdapter = new ChannelContentAdapter(datas);
                            channelContentAdapter.setOnItemClickListener((item, position) -> watchLive(item));
                            channelRV.setAdapter(channelContentAdapter);
                        }
                        if (pageNum == 1) channelContentAdapter.setData(datas);
                        else channelContentAdapter.addData(datas);
                        emptyRootRl.setVisibility(View.GONE);

                    } else {
                        if (pageNum == 1)
                            emptyRootRl.setVisibility(View.VISIBLE);
                    }
                    if (datas.size() >= 10) {
                        mRefreshLayout.setCanLoadMore(true);
                        channelContentAdapter.setIsNoMore(false);
                    } else {
                        mRefreshLayout.setCanLoadMore(false);
                        if (pageNum>1) {
                            channelContentAdapter.setIsNoMore(true);
                        }
                    }
                } else {
                    if (pageNum == 1) {
                        emptyRootRl.setVisibility(View.VISIBLE);
                    }
                    mRefreshLayout.setCanLoadMore(false);
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mRefreshLayout != null) {
                    mRefreshLayout.completeRefresh();
                    mRefreshLayout.completeLoadMore();
                }
            }
        });
    }

    private LiveBean bean;
    private static final int REQUEST_READ_PERMISSION = 101;//请求文件读写权限的请求码

    /**
     * 观众 观看直播
     */
    public void watchLive(LiveBean bean) {
        this.bean = bean;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
            } else {
                forwardLiveActivity(bean);
            }
        } else {
            forwardLiveActivity(bean);
        }
    }

    CheckLivePresenter mCheckLivePresenter;

    private void forwardLiveActivity(LiveBean bean) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(getContext());
        }
        mCheckLivePresenter.setSelectLiveBean(bean);
        mCheckLivePresenter.watchLive();
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        initData();
    }

    @Override
    public void onLoadMore() {
        pageNum++;
        initData();
    }
}
