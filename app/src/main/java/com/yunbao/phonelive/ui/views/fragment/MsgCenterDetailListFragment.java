package com.yunbao.phonelive.ui.views.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.MsgCenterRvAdapter;
import com.yunbao.phonelive.bean.MsgCenterDetailBean;
import com.yunbao.phonelive.bean.MsgCenterListBean;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.views.adapter.MsgCenterDetailRvAdapter;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 消息中心详情列表
 */
public class MsgCenterDetailListFragment extends BaseLazyFragment {

    private MsgCenterDetailRvAdapter adapter;
    private RecyclerView rootRv;
    private RefreshLayout refreshLayout;
    List<MsgCenterDetailBean> msgCenterListBeans;

    public static MsgCenterDetailListFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        MsgCenterDetailListFragment fragment = new MsgCenterDetailListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_msg_list_recyclerview;
    }

    @Override
    protected void initView() {
        rootRv = findView(R.id.root_rv);
        refreshLayout = findView(R.id.root_rl);
        rootRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new MsgCenterDetailRvAdapter();
        rootRv.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getData();
            }
        });
        refreshLayout.setCanLoadMore(true);
        refreshLayout.setScorllView(rootRv);
    }

    private int type = 0, pageNum = 1;

    @Override
    protected void initData() {
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
        pageNum = 1;
        getData();
    }

    private void getData() {
        HttpUtil.getMsgDetailList(type, pageNum, callback);
    }

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null && info.length > 0) {
                List<MsgCenterDetailBean> msgCenterListBeans = JSON.parseArray(Arrays.toString(info), MsgCenterDetailBean.class);
                if (msgCenterListBeans != null && msgCenterListBeans.size() >= 10) {
                    refreshLayout.setCanLoadMore(true);
                } else refreshLayout.setCanLoadMore(false);
                adapter.addDatas(msgCenterListBeans);
            } else {
                if (!TextUtils.isEmpty(msg)) {
                    ToastUtil.show(msg);
                }
                refreshLayout.setCanRefresh(false);
                refreshLayout.setCanLoadMore(false);
            }
        }

        @Override
        public void onError(Response<JsonBean> response) {
            super.onError(response);
            refreshLayout.setCanLoadMore(false);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (pageNum == 1) refreshLayout.completeRefresh();
            else refreshLayout.completeLoadMore();
        }
    };


}
