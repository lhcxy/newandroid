package com.yunbao.phonelive.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.SearchRvAdapter;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.custom.NoAlphaItemAnimator;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.presenter.CheckLivePresenter;
import com.yunbao.phonelive.ui.helper.GridSpacingItemDecoration;
import com.yunbao.phonelive.ui.views.adapter.MsgSearchHistoryRvAda;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/8/10.
 * 主页搜索页面
 */

public class SearchActivity extends AbsActivity implements View.OnClickListener, OnItemClickListener<Integer> {

    private EditText mEditText;
    private View mClear;
    private View mNoResult;//没有结果
    private RecyclerView mRecyclerView;
    //    private AttentionAdapter mAdapter;
    private InputMethodManager imm;
    private boolean isSearching;
    private static final int MAX_COUNT = 3;  //搜索历史最多显示条数

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void main() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditText = (EditText) findViewById(R.id.search_input);
        mEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                search();
                return true;
            }
            return false;
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (mClear.getVisibility() == View.GONE) {
                        mClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mClear.getVisibility() == View.VISIBLE) {
                        mClear.setVisibility(View.GONE);
                    }
                    historyLl.setVisibility(View.VISIBLE);
                    mNoResult.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mClear = findViewById(R.id.btn_clear);
        mClear.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, DpUtil.dp2px(16), true));

        mAdapter = new SearchRvAdapter();
        mAdapter.setOnItemClickListener((item, position) -> {
//            if (getActivity() instanceof MainActivity) {
            watchLive(item);
//            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mNoResult = findViewById(R.id.no_result);
        initSearchView();
    }

    SearchRvAdapter mAdapter;

    @Override
    public void onBackPressed() {
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        mEditText.setText("");
        if (mAdapter != null) {
            mAdapter.clearData();
        }
        mNoResult.setVisibility(View.INVISIBLE);
        imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
        mEditText.requestFocus();
    }

    private void search() {
        if (isSearching) {
            return;
        }
        isSearching = true;
        String key = mEditText.getText().toString();
        if ("".equals(key)) {
            return;
        }
        addSearchHistory(key);
        historyLl.setVisibility(View.GONE);
        HttpUtil.search(key, mHttpCallback);
    }

    private HttpCallback mHttpCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            List<LiveBean> list = JSON.parseArray(Arrays.toString(info), LiveBean.class);
            if (list.size() > 0) {
                if (mNoResult.getVisibility() == View.VISIBLE) {
                    mNoResult.setVisibility(View.INVISIBLE);
                }
                if (mAdapter == null) {
                    mAdapter = new SearchRvAdapter(list);
                    mAdapter.setOnItemClickListener((item, position) -> {
                        watchLive(item);
                    });
                    mRecyclerView.setAdapter(mAdapter);
                }
                mAdapter.setData(list);

            } else {
                if (mAdapter != null) {
                    mAdapter.clearData();
                }
                if (mNoResult.getVisibility() == View.INVISIBLE) {
                    mNoResult.setVisibility(View.VISIBLE);
                }
                ToastUtil.show(getString(R.string.search_no_result));
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            isSearching = false;
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUitl.loadingDialog(mContext, getString(R.string.search_ing));
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.SET_ATTENTION);
        HttpUtil.cancel(HttpUtil.SEARCH);
        super.onDestroy();
        mAdapter = null;
        mRecyclerView = null;
        imm = null;
    }

    private static final int REQUEST_READ_PERMISSION = 101;//请求文件读写权限的请求码
    private LiveBean bean;
    private CheckLivePresenter mCheckLivePresenter;

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

    private List<String> historyDatas;
    private MsgSearchHistoryRvAda historyAdapter;

    private void saveHistory() {
        SharedPreferencesUtil.getInstance().saveString("homeSearchHistory", JSON.toJSONString(historyDatas));
    }

    private void getHistory() {
        String searchHistory = SharedPreferencesUtil.getInstance().getString("homeSearchHistory");
        if (!TextUtils.isEmpty(searchHistory)) {
            historyDatas = JSON.parseArray(searchHistory, String.class);
        } else historyDatas = new ArrayList<>();
    }

    private void addSearchHistory(String content) {
        historyDatas.add(0, content);
        if (historyDatas.size() > MAX_COUNT) {
            historyDatas.remove(MAX_COUNT);
        }
        historyAdapter.notifyDataSetChanged();
    }

    private RecyclerView historyRv;
    private LinearLayout historyLl;

    private void initSearchView() {
        historyRv = findViewById(R.id.mgs_search_history_rv);
        historyLl = findViewById(R.id.msg_search_history_ll);
        getHistory();
        historyAdapter = new MsgSearchHistoryRvAda(historyDatas);
        historyAdapter.setListener(this);
        historyRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        historyRv.setAdapter(historyAdapter);
    }

    @Override
    public void onItemClick(Integer item, int position) {
        if (item == 0) {
            //搜索历史
            mEditText.setText(historyDatas.get(position));
            search();
        } else if (item == 1) {
            //删除历史
            historyDatas.remove(position);
            historyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        saveHistory();
        super.onPause();
    }
}
