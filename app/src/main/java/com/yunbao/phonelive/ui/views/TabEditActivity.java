package com.yunbao.phonelive.ui.views;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.interfaces.TabChangeCallback;
import com.yunbao.phonelive.ui.helper.GridSpacingItemDecoration;
import com.yunbao.phonelive.ui.helper.TabChannelHelper;
import com.yunbao.phonelive.ui.helper.touchhelp.ItemTouchHelperCallback;
import com.yunbao.phonelive.ui.views.adapter.TabEditAdapter;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class TabEditActivity extends AbsActivity implements TabChangeCallback {
    private ArrayList<TabBean> showData = new ArrayList<>(), hideData = new ArrayList<>();
    public static final int TAB_RESULT_CODE = 0x012;
    private RecyclerView showRv, hideRv;
    public static final int SPAN_COUNT = 4;
    private TabEditAdapter tabhideAdapter;
    private TabEditAdapter tabshowAdapter;
    private TextView subTitleTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_tab_edit;
    }

    @Override
    protected void main() {
        initData();
        initView();
    }

    private boolean hasDataChange = false;

    private void initView() {
        ((TextView) findViewById(R.id.title)).setText("频道定制");
        showRv = findViewById(R.id.tab_edit_show_rv);
        hideRv = findViewById(R.id.tab_edit_unshow_rv);
        subTitleTv = findViewById(R.id.subtitle_tv);
        subTitleTv.setVisibility(View.VISIBLE);
        subTitleTv.setText("保存");
        showRv.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        showRv.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, DpUtil.dp2px(8), true));
        tabshowAdapter = new TabEditAdapter(showData);
        tabshowAdapter.setChangeCallBack(this);
        hideRv.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        hideRv.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, DpUtil.dp2px(8), true));
        tabhideAdapter = new TabEditAdapter(hideData);
        tabshowAdapter.setItemClickListener((item, position) -> {
            hasDataChange = true;
            tabshowAdapter.removeItem(position);
            tabhideAdapter.addItem(item);
        });
        tabhideAdapter.setItemClickListener((item, position) -> {
            hasDataChange = true;
            tabhideAdapter.removeItem(position);
            tabshowAdapter.addItem(item);
        });
        showRv.setAdapter(tabshowAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(tabshowAdapter));
        touchHelper.attachToRecyclerView(showRv);
        hideRv.setAdapter(tabhideAdapter);
        disposable.add(RxView.clicks(subTitleTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> onSave()));
    }

    private void onSave() {
        if (hasDataChange) {
            if (tabshowAdapter != null) {
                TabChannelHelper.getInstance().saveShowData(tabshowAdapter.getDatas());
            } else {
                TabChannelHelper.getInstance().saveShowData(showData);
            }
            if (tabhideAdapter != null) {
                TabChannelHelper.getInstance().saveHideData(tabhideAdapter.getDatas());
            } else {
                TabChannelHelper.getInstance().saveHideData(hideData);
            }
            Intent intent = new Intent();
            setResult(TAB_RESULT_CODE, intent);
        }
        finish();
    }

    private void initData() {
        showData = TabChannelHelper.getInstance().getShowData(this);
        hideData = TabChannelHelper.getInstance().getHideData();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hideData != null) {
            hideData.clear();
            hideData = null;
        }
        if (showData != null) {
            showData.clear();
            showData = null;
        }
        if (tabshowAdapter != null) {
            tabshowAdapter = null;
        }
        if (tabhideAdapter != null) {
            tabhideAdapter = null;
        }
    }

    @Override
    public void onChange() {
        hasDataChange = true;
    }
}
