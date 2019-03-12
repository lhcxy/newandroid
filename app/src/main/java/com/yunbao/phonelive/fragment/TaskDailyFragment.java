package com.yunbao.phonelive.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.DailyTaskBean;
import com.yunbao.phonelive.http.Data;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.views.adapter.DailyTaskRVAdapter;
import com.yunbao.phonelive.utils.ToastUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TaskDailyFragment extends BaseLazyFragment {
    private RecyclerView dailyTaskRv;
    private DailyTaskRVAdapter adapter;
    private List<DailyTaskBean> datas;
    private ProgressBar progressBar;
    private TextView progressTv;
    private ImageView taskFinishIv;

    public static TaskDailyFragment newInstance() {
        TaskDailyFragment fragment = new TaskDailyFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_daily_task_recyclerview;
    }

    @Override
    protected void initView() {
        dailyTaskRv = findView(R.id.root_rv);
        progressBar = findView(R.id.progress_pb);
        progressTv = findView(R.id.progressbar_tv);
        taskFinishIv = findView(R.id.task_finish_iv);
        dailyTaskRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new DailyTaskRVAdapter();
        adapter.setListener((item, position) -> {
            if (position < datas.size()) {
                excuteTask(position);
            }
        });
        dailyTaskRv.setAdapter(adapter);
        disposable.add(RxView.clicks(taskFinishIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (progress >= 100 && getType == 0) {
                getGift();
            } else {
                if (progress < 100 && getType == 0) {
                    ToastUtil.show("还没达到领取标注");
                }
            }
        }));
    }

    private void getGift() {
        HttpUtil.getDailyTaskGift(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    getType = 1;
                    JSONObject object = JSON.parseObject(info[0]);
                    int coin = 0, carrot = 0;
                    if (object.containsKey("coin")) { //探球币
                        coin = object.getIntValue("coin");
                    }
                    if (object.containsKey("carrot")) { //爆米花
                        carrot = object.getIntValue("carrot");
                    }
                    String str = "恭喜您完成每日任务";
                    if (coin > 0) {
                        str += ",获得" + coin + "探球币";
                        if (TextUtils.isEmpty(AppConfig.getInstance().getUserBean().getCoin())) {
                            AppConfig.getInstance().getUserBean().setCoin(String.valueOf(coin));
                        } else {
                            BigDecimal bigDecimal = new BigDecimal(AppConfig.getInstance().getUserBean().getCoin());
                            AppConfig.getInstance().getUserBean().setCoin(bigDecimal.add(new BigDecimal(coin)).toString());
                        }
                    }
                    if (carrot > 0) {
                        str += ",获得" + carrot + "爆米花.";
                        if (TextUtils.isEmpty(AppConfig.getInstance().getUserBean().getCarrot())) {
                            AppConfig.getInstance().getUserBean().setCarrot(String.valueOf(carrot));
                        } else {
                            BigDecimal bigDecimal = new BigDecimal(AppConfig.getInstance().getUserBean().getCarrot());
                            AppConfig.getInstance().getUserBean().setCarrot(bigDecimal.add(new BigDecimal(carrot)).toString());
                        }
                    }
                    ToastUtil.show(str);
                    taskFinishIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_daily_task_end_finish));
                } else
                    ToastUtil.show(msg);
            }
        });
    }

    @Override
    protected void initData() {
        if (progressBar != null) {
            progressBar.setProgress(this.progress);
        }
        if (progressTv != null) {
            progressTv.setText(this.progress + "/100");
        }
        if (taskFinishIv != null) {
            if (getType == 0)
                taskFinishIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_daily_task_end_unfinish));
            else
                taskFinishIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_daily_task_end_finish));
        }

        if (adapter != null) {
            adapter.setDatas(this.datas);
            adapter.notifyDataSetChanged();
        }
    }

    public void setDatas(List<DailyTaskBean> datas) {
        this.datas = datas;
        if (adapter != null) {
            adapter.setDatas(this.datas);
            adapter.notifyDataSetChanged();
        }
    }

    private int progress = 0;
    private int getType = 0;

    public void setProgress(int progress, int getType) {
        this.progress = progress >= 100 ? 100 : progress;
        this.getType = getType;
        if (progressBar != null) {
            progressBar.setProgress(this.progress);
        }
        if (taskFinishIv != null) {
            if (getType == 0)
                taskFinishIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_daily_task_end_unfinish));
            else
                taskFinishIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_daily_task_end_finish));
        }
        if (progressTv != null) {
            progressTv.setText(this.progress + "/100");
        }
    }


    public void excuteTask(int position) {
        HttpUtil.excuteDailyTask(datas.get(position).getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {

            }

            @Override
            public void onSuccessStr(int code, String msg, Data data) {
                if (code == 0) {
                    datas.get(position).setStatus(1);
                    adapter.notifyDataSetChanged();
                    addProgress(data.getCash());
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

    private void addProgress(int progress2) {
        progress += progress2;
        progress = progress >= 100 ? 100 : progress;
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
        if (progressTv != null) {
            progressTv.setText(progress + "/100");
        }
    }
}
