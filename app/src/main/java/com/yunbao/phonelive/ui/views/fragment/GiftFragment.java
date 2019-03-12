package com.yunbao.phonelive.ui.views.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.GiftListAdapter;
import com.yunbao.phonelive.bean.GiftBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;

import java.util.ArrayList;
import java.util.List;

public class GiftFragment extends BaseLazyFragment {
    private GiftListAdapter giftListAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_gift_list;
    }

    private List<GiftBean> mList;
    private RecyclerView giftListRv;
    private ProgressBar mLoadingView;

    @Override
    protected void initView() {
        giftListRv = findView(R.id.gift_list_rv);
        mLoadingView = findView(R.id.loading);
        giftListAdapter = new GiftListAdapter(getContext(), mList);
        giftListRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        giftListRv.setAdapter(giftListAdapter);
    }

    @Override
    protected void initData() {
        HttpUtil.getGiftList(mCallback);
    }

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            JSONObject obj = JSON.parseObject(info[0]);
            List<GiftBean> list = JSON.parseArray(obj.getString("giftlist"), GiftBean.class);
            if (mList == null) {
                mList = new ArrayList<>();
                mList.addAll(list);
                if (giftListAdapter != null) {
                    giftListAdapter.setmList(mList);
                }
            }
        }

        @Override
        public void onFinish() {
            mLoadingView.setVisibility(View.GONE);
        }
    };

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
        giftListAdapter.setOnItemClickListener(this.listener);
    }

    private OnItemClickListener listener;
}
