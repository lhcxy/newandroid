package com.yunbao.phonelive.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.DailyTaskBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.views.adapter.DailyTaskRVAdapter;
import com.yunbao.phonelive.utils.ToastUtil;

import java.math.BigDecimal;
import java.util.List;

public class TaskNewFragment extends BaseLazyFragment {
    private RecyclerView dailyTaskRv;
    private DailyTaskRVAdapter adapter;
    private List<DailyTaskBean> datas;

    public static TaskNewFragment newInstance() {
        TaskNewFragment fragment = new TaskNewFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_msg_list_recyclerview;
    }

    @Override
    protected void initView() {
        dailyTaskRv = findView(R.id.root_rv);
        dailyTaskRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new DailyTaskRVAdapter();
        adapter.setListener((item, position) -> {
            if (position < datas.size()) {
                excuteTask(position);
            }
        });
        dailyTaskRv.setAdapter(adapter);
    }

    @Override
    protected void initData() {

    }

    public void setDatas(List<DailyTaskBean> datas) {
        this.datas = datas;
        if (adapter != null) {
            Log.e("//", "setDatas: adapter  not null");
            adapter.setDatas(this.datas);
            adapter.notifyDataSetChanged();
        } else Log.e("//", "setDatas: adapter  is null");
    }

    public void excuteTask(int position) {
        HttpUtil.excuteDailyTask(datas.get(position).getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    datas.get(position).setStatus(1);
                    adapter.notifyDataSetChanged();
                    if (TextUtils.isEmpty(AppConfig.getInstance().getUserBean().getCarrot())) {
                        AppConfig.getInstance().getUserBean().setCarrot(datas.get(position).getCoin());
                    } else {
                        BigDecimal bigDecimal = new BigDecimal(datas.get(position).getCoin());
                        BigDecimal bigDecimal1 = new BigDecimal(AppConfig.getInstance().getUserBean().getCarrot());
                        AppConfig.getInstance().getUserBean().setCarrot(bigDecimal.add(bigDecimal1).toString());
                    }
                    ToastUtil.show("领取成功!");
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }
}
