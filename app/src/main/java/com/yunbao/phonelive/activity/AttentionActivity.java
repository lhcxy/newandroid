package com.yunbao.phonelive.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.AttentionAdapter;
import com.yunbao.phonelive.bean.AttentionBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.custom.NoAlphaItemAnimator;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.presenter.CheckLivePresenter;
import com.yunbao.phonelive.ui.helper.AttentionTitleDecoration;
import com.yunbao.phonelive.ui.helper.DecorationCallback;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/8/12.
 * 关注 粉丝 页面
 */

public class AttentionActivity extends AbsActivity implements OnItemClickListener<LiveBean> {

    private String mTouid;
    private String mType;//0是关注 1是粉丝
    private View mNoResult;
    private int mSex;//对方的性别，1男  2女
    private RecyclerView mRecyclerView;
    private AttentionAdapter mAdapter;
    private TextView mResultTextView;
//    private List<AttentionBean> datas;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_attention;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        mTouid = intent.getStringExtra("touid");
        mType = intent.getStringExtra("type");
        mSex = intent.getIntExtra("sex", 0);
        if ("0".equals(mType)) {
            setTitle(getString(R.string.attention2));
        } else if ("1".equals(mType)) {
            setTitle(getString(R.string.fans));
        }
        initView();
        initData();
    }

    private void initView() {
        mNoResult = findViewById(R.id.no_result);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());
        mAdapter = new AttentionAdapter(mContext);
        mAdapter.setListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new AttentionTitleDecoration(this, new DecorationCallback() {
            @Override
            public String getGroupId(int position) {
                return mAdapter.getGroupId(position);
            }

            @Override
            public String getGroupFirstLine(int position) {
                return mAdapter.getGroupFirstLine(position);
            }
        }));
        mResultTextView = (TextView) findViewById(R.id.result_text);
        if ("0".equals(mType)) {
            mResultTextView.setText(getString(R.string.he_not_have_attention));
        } else if ("1".equals(mType)) {
            mResultTextView.setText(getString(R.string.he_not_have_fans));
        }
    }

    private void initData() {
        if ("0".equals(mType)) {
            HttpUtil.getFollowsList(mTouid, mHttpCallback);
            HttpUtil.getRecommend(mRecommendFollow);
        } else if ("1".equals(mType)) {
//            HttpUtil.getFansList(mTouid, mHttpCallback);
        }

    }

    private HttpCallback mHttpCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            List<LiveBean> list = JSON.parseArray(Arrays.toString(info), LiveBean.class);
            if (list.size() > 0) {
                if (mAdapter == null) {
                    mAdapter = new AttentionAdapter(mContext);
                    mRecyclerView.setAdapter(mAdapter);
                }
//                if (datas == null) {
//                    datas = new ArrayList<>();
//                }
                for (LiveBean attentionBean : list) {
                    attentionBean.setbTitle("已关注");
                }
//                datas.addAll(list);
                mAdapter.setFollowDatas(list);
            } else {
                if (mAdapter != null) {
                    mAdapter.clear();
                }

            }
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUitl.loadingDialog(mContext);
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public void onFinish() {
            super.onFinish();
            netStatus += 1;
            isNetFinish();
        }
    };


    private HttpCallback mRecommendFollow = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            List<LiveBean> list = JSON.parseArray(Arrays.toString(info), LiveBean.class);
            if (list.size() > 0) {
                if (mAdapter == null) {
                    mAdapter = new AttentionAdapter(mContext);
                    mRecyclerView.setAdapter(mAdapter);
                }
                for (LiveBean attentionBean : list) {
                    attentionBean.setbTitle("推荐关注");
                }
//                if (datas == null) {
//                    datas = new ArrayList<>();
//                }

//                datas.addAll(list);
                mAdapter.setUnFollowDatas(list);
            }

        }

        @Override
        public void onFinish() {
            super.onFinish();
            netStatus += 1;
            isNetFinish();
        }
    };

    private int netStatus = 0;

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.SET_ATTENTION);
        HttpUtil.cancel(HttpUtil.GET_RECOMMEND);
        HttpUtil.cancel(HttpUtil.GET_FOLLOWS_LIST);
        HttpUtil.cancel(HttpUtil.GET_FANS_LIST);
        mAdapter = null;
        mRecyclerView = null;
        super.onDestroy();
    }

    private void isNetFinish() {
        if (netStatus >= 2) {
            if (mAdapter != null && mAdapter.getItemCount() > 0) {
                mNoResult.setVisibility(View.GONE);
            } else {
                if (mNoResult.getVisibility() == View.GONE) {
                    mNoResult.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private static final int REQUEST_READ_PERMISSION = 101;//请求文件读写权限的请求码
    LiveBean bean;

    public void watchLive(LiveBean bean) {
        this.bean = bean;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.setSelectLiveBean(bean);
        mCheckLivePresenter.watchLive();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) {
            ToastUtil.show(getString(R.string.permission_refused));
            return;
        }
        switch (requestCode) {
            case REQUEST_READ_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.show(getString(R.string.storage_permission_refused));
                } else {
                    if (bean != null)
                        forwardLiveActivity(bean);
                }
                break;
        }
    }

    @Override
    public void onItemClick(LiveBean item, int position) {
        watchLive(item);
    }
}
