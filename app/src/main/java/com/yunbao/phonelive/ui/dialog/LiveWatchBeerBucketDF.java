package com.yunbao.phonelive.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveBeerBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.ChoseCallback;
import com.yunbao.phonelive.ui.tools.StringUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LiveWatchBeerBucketDF extends DialogFragment {

    private CompositeDisposable disposable;
    private String touid;
    private Disposable timerSubscribe;

    public static LiveWatchBeerBucketDF newInstance(String touid) {
        Bundle args = new Bundle();
        args.putString("touid", touid);
        LiveWatchBeerBucketDF fragment = new LiveWatchBeerBucketDF();
        fragment.setArguments(args);
        return fragment;
    }

    private Context mContext;
    private View mRootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_live_watch_beer_bucket, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            touid = getArguments().getString("touid", "");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        disposable = new CompositeDisposable();
        colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.app_selected_color));
        initView();
        initListener();
        getData();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getData() {
        HttpUtil.getLiveInfo(touid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    LiveBeerBean data = JSON.parseObject(info[0], LiveBeerBean.class);
                    if (data != null) {
                        if (data.getCask() == 0) {
                            showUnopenView();
                        } else if (data.getCask() == 1) {
                            showProceedView(data.getCtype());
                            beerNum = data.getCount() - data.getPnum();
                            beerTime = data.getEnd_time();
                            refreshTitle();
                            setTimmer();
                        } else {
                            showFinishView(data.getCtype());
                        }
                        if (isAdded()) {
                            if (data.getCtype() == 1) {
                                titleTv.setText("青铜酒桶");
                                titleProTv.setText("青铜酒桶");
                            } else if (data.getCtype() == 2) {
                                titleTv.setText("白银酒桶");
                                titleProTv.setText("白银酒桶");
                            } else {
                                titleTv.setText("黄金酒桶");
                                titleProTv.setText("黄金酒桶");
                            }
                        }
                    }
                }
            }
        });
    }

    private void initListener() {
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.send_gift_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (listener != null) {
                listener.onChose(0);
            }
            dismiss();
        }));

        disposable.add(RxView.clicks(mRootView.findViewById(R.id.lottery_rl)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (listener != null) {
                listener.onChose(1);
            }
            dismiss();
        }));

        disposable.add(RxView.clicks(mRootView.findViewById(R.id.dialog_cancel)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            dismiss();
        }));

    }

    private ForegroundColorSpan colorSpan;
    private RelativeLayout unopenRl, finishRl, proceedRl; // 未开启，结束，进行中
    private TextView proceedTitleTv, proceedTimeTv, titleTv, titleProTv;
    private ImageView beerIconIv, beerFinishIv;

    private void initView() {
        unopenRl = mRootView.findViewById(R.id.beer_bucket_unopen_rl);
        finishRl = mRootView.findViewById(R.id.beer_bucket_finish_rl);
        proceedRl = mRootView.findViewById(R.id.beer_bucket_proceed_rl);
        proceedTitleTv = mRootView.findViewById(R.id.beer_bucket_proceed_num_tv);
        proceedTimeTv = mRootView.findViewById(R.id.beer_bucket_proceed_time_tv);
        beerIconIv = mRootView.findViewById(R.id.beer_type_iv);
        beerFinishIv = mRootView.findViewById(R.id.beer_bucket_finish_iv);
        titleTv = mRootView.findViewById(R.id.beer_bucket_finish_tv);
        titleProTv = mRootView.findViewById(R.id.beer_bucket_proceed_tv);
    }

    private int beerNum = 0;

    private void refreshTitle() {
        if (this.isAdded()) {
            SpannableString string = new SpannableString(String.format(getResources().getString(R.string.txt_beer_bucket_title_count), beerNum));
            string.setSpan(colorSpan, 2, String.valueOf(beerNum).length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            string.setSpan(new RelativeSizeSpan(1.3f), 2, String.valueOf(beerNum).length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            proceedTitleTv.setText(string);
        }

    }

    private void refreshTime(int timer) {
        if (this.isAdded()) {
            String s = StringUtil.secToTime(timer);
            SpannableString string = new SpannableString(String.format(getResources().getString(R.string.txt_beer_bucket_time_count), s));
            string.setSpan(colorSpan, string.length() - s.length(), string.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            string.setSpan(new RelativeSizeSpan(1.2f), string.length() - s.length(), string.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            proceedTimeTv.setText(string);
        }
    }

    private void showUnopenView() {
        if (isAdded()) {
            if (unopenRl != null)
                unopenRl.setVisibility(View.VISIBLE);
            if (finishRl != null)
                finishRl.setVisibility(View.GONE);
            if (proceedRl != null)
                proceedRl.setVisibility(View.GONE);
            if (beerIconIv != null)
                beerIconIv.setVisibility(View.GONE);
        }
    }

    private void showFinishView(int cType) {
        if (isAdded()) {
            if (unopenRl != null)
                unopenRl.setVisibility(View.GONE);
            if (finishRl != null)
                finishRl.setVisibility(View.VISIBLE);
            if (proceedRl != null)
                proceedRl.setVisibility(View.GONE);
            if (beerIconIv != null)
                beerIconIv.setVisibility(View.VISIBLE);
            if (beerFinishIv != null) {
                if (cType == 1) {
                    beerFinishIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_liver_beer_bucket_finish_qt));
                    beerIconIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_beer_bucket_finish_copper));
                } else if (cType == 2) {
                    beerFinishIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_liver_beer_bucket_finish_by));
                    beerIconIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_beer_bucket_finish_silver));
                } else {
                    beerFinishIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_liver_beer_bucket_finish_hj));
                    beerIconIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_beer_bucket_finish_gold));
                }
            }
        }

    }

    private void showProceedView(int cType) {
        if (isAdded()) {
            if (unopenRl != null)
                unopenRl.setVisibility(View.GONE);
            if (finishRl != null)
                finishRl.setVisibility(View.GONE);
            if (proceedRl != null)
                proceedRl.setVisibility(View.VISIBLE);
            if (beerIconIv != null)
                beerIconIv.setVisibility(View.VISIBLE);

            if (cType == 1) {
                beerIconIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_beer_bucket_open_copper));
            } else if (cType == 2) {
                beerIconIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_beer_bucket_open_silver));
            } else {
                beerIconIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_beer_bucket_open_gold));
            }
        }
    }

    private ChoseCallback listener;

    public void setListener(ChoseCallback listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
        HttpUtil.cancel("getLiveInfo");
    }

    public void setStepData(LiveBeerBean data) {
        if (data != null) {
            if (data.getCask() == 0) {
                showUnopenView();
            } else if (data.getCask() == 1) {
                showProceedView(data.getCtype());
                beerNum = data.getCount() - data.getPnum();
                beerTime = data.getEnd_time();
                refreshTitle();
                setTimmer();
            } else {
                showFinishView(data.getCtype());
            }
            if (data.getCtype() == 1) {
                titleTv.setText("青铜酒桶");
                titleProTv.setText("青铜酒桶");
            } else if (data.getCtype() == 2) {
                titleTv.setText("白银酒桶");
                titleProTv.setText("白银酒桶");
            } else {
                titleTv.setText("黄金酒桶");
                titleProTv.setText("黄金酒桶");
            }
        }
    }

    private int beerTime = 0;

    private void setTimmer() {
        if (beerTime == 0) {
            return;
        }
        if (timerSubscribe != null) {
            timerSubscribe.dispose();
        }
        if (disposable == null) {
            disposable = new CompositeDisposable();
        }
        timerSubscribe = Flowable
                .intervalRange(0, beerTime, 0, 1, TimeUnit.SECONDS)//从1输出到20，延时0s，间隔1s
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aLong -> refreshTime(beerTime - aLong.intValue()))
                .doOnComplete(() -> showUnopenView())
                .subscribe();
        disposable.add(timerSubscribe);
    }

}
