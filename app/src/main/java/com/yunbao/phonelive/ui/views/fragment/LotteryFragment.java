package com.yunbao.phonelive.ui.views.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LotteryBean;
import com.yunbao.phonelive.bean.LotteryItemBean;
import com.yunbao.phonelive.bean.LotteryResultBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.InputCallback;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.dialog.LotteryInputDF;
import com.yunbao.phonelive.ui.dialog.LotteryResultDF;
import com.yunbao.phonelive.ui.views.LotteryActivity;
import com.yunbao.phonelive.ui.views.LotteryHistoryActivity;
import com.yunbao.phonelive.ui.views.LotteryRuleActivity;
import com.yunbao.phonelive.ui.views.adapter.LotteryRecordRvAda;
import com.yunbao.phonelive.ui.widget.LotteryView;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LotteryFragment extends BaseLazyFragment {
    private LotteryView lotteryView;
    private RecyclerView recordRv;
    private LotteryRecordRvAda recordRvAda;

    public static LotteryFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        LotteryFragment fragment = new LotteryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_lottery;
    }

    int type = 1;

    @Override
    protected void initView() {
        lotteryView = findView(R.id.lotteryView2);
        Bundle arguments = getArguments();
        if (arguments != null) {
            type = arguments.getInt("type", 0);
            Log.e("//", "initData: " + type);
            lotteryView.setType(type);
        }

        recordRv = findView(R.id.lottery_record_rv);
        recordRvAda = new LotteryRecordRvAda();
        recordRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recordRv.setAdapter(recordRvAda);
        lotteryView.setlistener(choseIndex -> {
            if (data != null && data.getNum() > 0) {
                lotteryView.startRotation();
                HttpUtil.getLotteryResult(type + 1, "1", callback);
            } else ToastUtil.show("钥匙数量不足");
        });

        addDisposable(RxView.clicks(findView(R.id.lottery_history_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            startActivity(new Intent(getContext(), LotteryHistoryActivity.class));
        }));
        addDisposable(RxView.clicks(findView(R.id.textView2)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            // 规则
            startActivity(new Intent(getContext(), LotteryRuleActivity.class));
        }));
        addDisposable(RxView.clicks(findView(R.id.textView)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            // 批量
            if (inputDF == null) {
                inputDF = LotteryInputDF.newInstance(type);
                inputDF.setListener(inputCallback);
            }
            inputDF.show(getChildFragmentManager(), "batch_get");
//            LotteryResultDF lotteryResultDF = LotteryResultDF.newInstance(new ArrayList<>());
//            lotteryResultDF.show(getChildFragmentManager(), "result");
        }));

    }

    private InputCallback inputCallback = new InputCallback() {
        @Override
        public void onSetText(String str) {
            if (TextUtils.isEmpty(str)) {
                ToastUtil.show("请输入抽奖次数");
                return;
            }
            if (inputDF != null) {
                inputDF.dismiss();
            }
            HttpUtil.getLotteryResult(type + 1, str, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info != null && info.length > 0) {
                        LotteryResultBean lotteryResultBean = JSON.parseObject(info[0], LotteryResultBean.class);
                        if (lotteryResultBean != null) {
                            if (lotteryResultBean.getCinfo() != null) {
                                HashMap<String, LotteryItemBean> hashMap = new HashMap<>();
                                for (LotteryItemBean lotteryItemBean : lotteryResultBean.getCinfo()) {
                                    if (hashMap.containsKey(lotteryItemBean.getGiftname())) {
                                        LotteryItemBean lotteryItemBean1 = hashMap.get(lotteryItemBean.getGiftname());
                                        lotteryItemBean1.setNum(lotteryItemBean1.getNum() + lotteryItemBean.getNum());
                                    } else {
                                        hashMap.put(lotteryItemBean.getGiftname(), lotteryItemBean);
                                    }
                                }
                                ArrayList<LotteryItemBean> lotteryItemBeans = new ArrayList<>();
                                for (String s : hashMap.keySet()) {
                                    lotteryItemBeans.add(hashMap.get(s));
                                }
                                if (lotteryItemBeans.size() > 0) {
                                    LotteryResultDF lotteryResultDF = LotteryResultDF.newInstance(lotteryItemBeans);
                                    lotteryResultDF.show(getChildFragmentManager(), "result");
                                }
                            }
                            recordRvAda.setData(lotteryResultBean.getLinfo());
//                            ToastUtil.show("恭喜您获得" + lotteryResultBean.getGiftname());
                            if (getActivity() != null) {
                                if (getActivity() instanceof LotteryActivity) {
                                    ((LotteryActivity) getActivity()).setKeyNum(type, lotteryResultBean.getNum());
                                }
                            }
                        } else ToastUtil.show(msg);
                    } else {
                        ToastUtil.show(msg);
                    }
                }

                @Override
                public boolean showLoadingDialog() {
                    return true;
                }

                @Override
                public Dialog createLoadingDialog() {
                    return DialogUitl.loadingDialog(getContext(), getString(R.string.loading));
                }
            });
        }
    };
    private LotteryInputDF inputDF;

    @Override
    protected void initData() {

    }

    private void batchGet(String getNum) {

    }

    private LotteryBean data;
    private boolean isBinded = false;

    public void setData(LotteryBean lotteryBean) {
        data = lotteryBean;
        if (isAdded()) {
            isBinded = true;
            if (lotteryView != null) {
                lotteryView.bindData(data.getPrize_arr());
            }
            if (recordRvAda != null) {
                recordRvAda.setData(data.getLinfo());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isBinded && data != null) {
            isBinded = true;
            if (lotteryView != null) {
                lotteryView.bindData(data.getPrize_arr());
            }
            if (recordRvAda != null) {
                recordRvAda.setData(data.getLinfo());
            }
        }
    }


    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info != null && info.length > 0) {
                LotteryResultBean lotteryResultBean = JSON.parseObject(info[0], LotteryResultBean.class);
                if (lotteryResultBean != null) {
                    lotteryView.setResultById(lotteryResultBean.getId());
                    recordRvAda.setData(lotteryResultBean.getLinfo());
                    ToastUtil.show("恭喜您获得" + lotteryResultBean.getGiftname());
                    if (getActivity() != null) {
                        if (getActivity() instanceof LotteryActivity) {
                            ((LotteryActivity) getActivity()).setKeyNum(type, lotteryResultBean.getNum());
                        }
                    }
                } else ToastUtil.show(msg);
            } else {
                ToastUtil.show(msg);
            }
        }
    };

}
