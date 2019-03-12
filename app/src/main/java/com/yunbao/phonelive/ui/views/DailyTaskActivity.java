package com.yunbao.phonelive.ui.views;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.bean.DailyTaskBean;
import com.yunbao.phonelive.fragment.TaskDailyFragment;
import com.yunbao.phonelive.fragment.TaskNewFragment;
import com.yunbao.phonelive.http.Data;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.views.adapter.LiveWatcherVPAdapter;
import com.yunbao.phonelive.ui.widget.NoScrollViewPager;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DailyTaskActivity extends AbsActivity {

    private LinearLayout newLl, dailyLl;
    private TextView newTitleTv, dailyTitleTv;
    private View newLine, dailyLine;
    private NoScrollViewPager viewPager;
    private List<DailyTaskBean> datas;
    private TaskDailyFragment taskDailyFragment;
    private TaskNewFragment newFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_daily_task;
    }

    private int seletedIndex = 0;

    @Override
    protected void main() {
        newLl = findViewById(R.id.subtitle_new_ll);
        dailyLl = findViewById(R.id.subtitle_daily_task_ll);
        newTitleTv = findViewById(R.id.subtitle_new_title_tv);
        dailyTitleTv = findViewById(R.id.subtitle_daily_task_title_tv);
        newLine = findViewById(R.id.subtitle_new_line_v);
        dailyLine = findViewById(R.id.subtitle_daily_task_line_v);
        viewPager = findViewById(R.id.viewPager);
        ((TextView) findViewById(R.id.title)).setText("我的任务");
        findViewById(R.id.title_line_v).setVisibility(View.INVISIBLE);
        disposable.add(RxView.clicks(findViewById(R.id.title_back_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> finish()));


        disposable.add(RxView.clicks(findViewById(R.id.subtitle_new_ll)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (seletedIndex == 0) return;
            seletedIndex = 0;
            subtitleStatus();
        }));
        disposable.add(RxView.clicks(findViewById(R.id.subtitle_daily_task_ll)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (seletedIndex == 1) return;
            seletedIndex = 1;
            subtitleStatus();
        }));


//        private RecyclerView dailyTaskRv;
//        private DailyTaskRVAdapter adapter;

//        dailyTaskRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        adapter = new DailyTaskRVAdapter();
//        dailyTaskRv.setAdapter(adapter);
//        adapter.setListener((item, position) -> {
//            if (position < datas.size()) {
//                excuteTask(position);
//            }
//        });

        taskDailyFragment = TaskDailyFragment.newInstance();
        newFragment = TaskNewFragment.newInstance();
        fragments = new ArrayList<>();
        fragments.add(newFragment);
        fragments.add(taskDailyFragment);
        viewPager.setAdapter(new LiveWatcherVPAdapter(getSupportFragmentManager(), fragments));
        getData();
    }

    List<Fragment> fragments;

    private void subtitleStatus() {
        if (seletedIndex == 0) {
            newTitleTv.setTextColor(getResources().getColor(R.color.text_color_333));
            newTitleTv.setTypeface(Typeface.DEFAULT_BOLD);
            dailyTitleTv.setTypeface(Typeface.DEFAULT);
            dailyTitleTv.setTextColor(getResources().getColor(R.color.text_color_88));
            newLine.setVisibility(View.VISIBLE);
            dailyLine.setVisibility(View.INVISIBLE);
        } else {
            newTitleTv.setTextColor(getResources().getColor(R.color.text_color_88));
            dailyTitleTv.setTypeface(Typeface.DEFAULT_BOLD);
            newTitleTv.setTypeface(Typeface.DEFAULT);
            dailyTitleTv.setTextColor(getResources().getColor(R.color.text_color_333));
            newLine.setVisibility(View.INVISIBLE);
            dailyLine.setVisibility(View.VISIBLE);
        }
        viewPager.setCurrentItem(seletedIndex);
    }

    public void getData() {
        HttpUtil.getDailyTaskList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {

            }

            @Override
            public void onSuccessStr(int code, String msg, Data data) {
                if (data != null) {
                    int total = data.getTotal();
                    int gettype = data.getGettype();
                    if (data.getInfo() != null && data.getInfo().length > 0) {
                        List<DailyTaskBean> all = JSON.parseArray(Arrays.toString(data.getInfo()), DailyTaskBean.class);
//                    adapter.setDatas(datas);
                        if (all != null) {
                            ArrayList<DailyTaskBean> newDatas = new ArrayList<>();
                            ArrayList<DailyTaskBean> dailyDatas = new ArrayList<>();
                            for (DailyTaskBean o : all) {
                                if ("6".equals(o.getTask_type())) {
                                    newDatas.add(o);
                                } else {
                                    dailyDatas.add(o);
                                }
                            }

                            if (newFragment != null) {
                                newFragment.setDatas(newDatas);
                            }
                            if (taskDailyFragment != null) {
                                taskDailyFragment.setDatas(dailyDatas);
                                taskDailyFragment.setProgress(total, gettype);
                            }
                        }
                    }
                } else {
                    finish();
                    ToastUtil.show("网络异常请稍后重试");
                }

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

//    public void excuteTask(int position) {
//        HttpUtil.excuteDailyTask(datas.get(position).getId(), new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                if (code == 0) {
//                    datas.get(position).setStatus(1);
//                    adapter.notifyDataSetChanged();
//                    EventBus.getDefault().post(new RefreshUserInfoEvent());
//                    ToastUtil.show("领取成功!");
//                } else {
//                    ToastUtil.show(msg);
//                }
//            }
//        });
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.cancel(HttpUtil.GET_DAILY_TASK);
        HttpUtil.cancel(HttpUtil.EXCUTE_DAILY_TASK);
    }
}
