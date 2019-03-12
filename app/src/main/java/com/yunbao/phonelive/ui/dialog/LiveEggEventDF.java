package com.yunbao.phonelive.ui.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.EggKnockBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.base.BaseDialogFragment;
import com.yunbao.phonelive.ui.tools.StringUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LiveEggEventDF extends BaseDialogFragment {

    private Context mContext;
    private View rootView;
    private CompositeDisposable disposable;
    private int isAttention = 0;

    /**
     * 主播发送红包编辑界面 dialog
     *
     * @return
     */
    public static LiveEggEventDF newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        LiveEggEventDF fragment = new LiveEggEventDF();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        disposable = new CompositeDisposable();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_knock_egg_view, null, false);
        dialog.setContentView(rootView);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (getArguments() != null) {
            int type = getArguments().getInt("type", 0);
            if (type == 0) {
                int widthPixels = getResources().getDisplayMetrics().widthPixels;
                int heightPixels = getResources().getDisplayMetrics().heightPixels;
                params.width = widthPixels;
                params.height = heightPixels - (widthPixels * 9 / 16 + DpUtil.dp2px(40));
            } else {
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
        }
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initView();
        super.onActivityCreated(savedInstanceState);
    }

    private ImageView knockEggIv, resultCancelIv;
    private TextView intervalTimeTv, hintTv, resultContentTv, eggLevelTv;
    private RelativeLayout resultContentRl;
    private ForegroundColorSpan globalColorSpan, blueColorSpan;

    private void initView() {
        knockEggIv = rootView.findViewById(R.id.knock_egg_action_iv);
        intervalTimeTv = rootView.findViewById(R.id.knock_egg_interval_tv);
        hintTv = rootView.findViewById(R.id.knock_egg_hint_tv);
        resultContentRl = rootView.findViewById(R.id.result_content_rl);
        resultContentTv = rootView.findViewById(R.id.result_content_tv);
        resultCancelIv = rootView.findViewById(R.id.result_cancel_iv);
        eggLevelTv = rootView.findViewById(R.id.knock_egg_level_tv);
        globalColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.text_color_ffe57c));
        blueColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.text_color_5acdee));
        disposable.add(RxView.clicks(rootView.findViewById(R.id.dialog_cancel_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> dismiss()));
        disposable.add(RxView.clicks(rootView.findViewById(R.id.knock_egg_comfirm_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
//            获取奖励
            if (AppConfig.isUnlogin()) {
                unloginHint(resultCancelIv);
                return;
            }
            if (eggBean != null && eggBean.getLevel() > 0) {
                onShowResultView();
            } else ToastUtil.show("等级为0，无法领取奖励");
        }));
        disposable.add(RxView.clicks(rootView.findViewById(R.id.knock_egg_rule_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
//            规则
            ToastUtil.show("敬请期待");
        }));
        disposable.add(RxView.clicks(rootView.findViewById(R.id.result_close_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> resultContentRl.setVisibility(View.GONE)));
        disposable.add(RxView.clicks(resultCancelIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> resultContentRl.setVisibility(View.GONE)));
        disposable.add(RxView.clicks(rootView.findViewById(R.id.result_confirm_iv)).throttleFirst(300L, TimeUnit.MILLISECONDS).subscribe(o -> {
//            确认获取奖励
            if (AppConfig.isUnlogin()) {
                unloginHint(resultCancelIv);
                return;
            }
            if (resultConfirmType == 0) {
                getEggResult();
            } else {
                resultContentRl.setVisibility(View.GONE);
                getInitEggInfo();
            }
        }));
        disposable.add(RxView.clicks(knockEggIv).throttleFirst(350L, TimeUnit.MILLISECONDS).subscribe(o -> {
//            砸蛋
            if (resultContentRl.getVisibility() == View.VISIBLE)
                return;
            onKnockEgg();
        }));
        getInitEggInfo();
    }

    private void onShowResultView() {
//        if (AppConfig.isUnlogin()) {
//            unloginHint(resultCancelIv);
//            return;
//        }
        if (eggBean != null && eggBean.getLevel() > 0) {
            resultContentRl.setVisibility(View.VISIBLE);
            resultContentTv.setText("确定收获？收获可获得" + eggBean.getCoin() + "探球币");
        } else ToastUtil.show("等级为0，无法领取奖励");

    }

    private int resultConfirmType = 0; //0= 获取结果   1=关闭结果界面 重置数据

    private void onShowResultContent(int resultCoin) {
        resultConfirmType = 1;
        resultCancelIv.setVisibility(View.GONE);
        resultContentTv.setText("收获成功！领取" + resultCoin + "探球币");
    }


    private EggKnockBean eggBean;

    private void setAlphaZeroAimation() {

        //创建透明度动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(knockEggIv, "alpha", 1.0f, 0.3f);
        //动画集合
        AnimatorSet set = new AnimatorSet();
        //添加动画
        set.play(alpha);
        //设置时间等
        set.setDuration(300);
        set.start();
        //动画监听
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (eggBean != null) {
                    setEggLevelImg(eggBean.getLevel());
                    eggLevelTv.setText("LV." + eggBean.getLevel());
                }
                setAlphaShowAimation();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
    }

    private void setAlphaShowAimation() {
        //创建透明度动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(knockEggIv, "alpha", 0.3f, 1f);
        //动画集合
        AnimatorSet set = new AnimatorSet();
        //添加动画
        set.play(alpha);
        //设置时间等
        set.setDuration(300);
        set.start();
        //动画监听
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
    }


    private void getInitEggInfo() {
        resultConfirmType = 0;
        if (resultCancelIv != null) {
            resultCancelIv.setVisibility(View.VISIBLE);
        }
        HttpUtil.getInitEggInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    eggBean = JSON.parseObject(info[0], EggKnockBean.class);
                    onBindData();
                } else {
                    if (!TextUtils.isEmpty(msg)) {
                        ToastUtil.show(msg);
                    }
                    dismiss();
                }
            }
        });
    }

    private void getEggResult() {
        HttpUtil.getEggResult(eggBean.getCoin(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    EggKnockBean eggGetBean = JSON.parseObject(info[0], EggKnockBean.class);

                    if (eggGetBean != null) {
                        onShowResultContent(eggGetBean.getCoin());
                        AppConfig.getInstance().getUserBean().setCoin(String.valueOf(eggGetBean.getUcoin()));
                    }
                }
            }
        });
    }

    private void onBindData() {
        if (eggBean != null) {
            setAlphaZeroAimation();
            if (eggBean.getLevel() > 0) {
//                hintTv.setText("本次砸蛋消耗" + eggBean.getAmount() + "兔尾巴，下一级可收获" + eggBean.getNcoin() + "兔尾巴");
                SpannableString string = new SpannableString("本次砸蛋消耗" + eggBean.getAmount() + "探球币，下一级可收获" + eggBean.getNcoin() + "探球币");
                string.setSpan(globalColorSpan, 6, String.valueOf(eggBean.getAmount()).length() + 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                string.setSpan(blueColorSpan, String.valueOf(eggBean.getAmount()).length() + 16, String.valueOf(eggBean.getNcoin()).length() + String.valueOf(eggBean.getAmount()).length() + 16, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                hintTv.setText(string);
            } else {
                hintTv.setText("暂无收益可领取，赶紧去砸蛋吧");
            }
            duration = eggBean.getTime();
            onIntervalRpDuration();
        }
    }

    private Disposable rpSubscirbe;
    private int duration = 0;

    private void onKnockEgg() {
        if (AppConfig.isUnlogin()) {
            unloginHint(resultCancelIv);
            return;
        }
        HttpUtil.onKnockEgg(eggBean.getLevel(), eggBean.getAmount(), knockCallBack);
    }

    private HttpCallback knockCallBack = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if ((code == 0 || code == 2 || code == 3) && info != null && info.length > 0) {
                eggBean = JSON.parseObject(info[0], EggKnockBean.class);
                if (eggBean != null) {
                    onBindData();
                    if (code == 2) {
                        onShowResultContent(eggBean.getCoin());
                    }
                    if (eggBean.getStatus() == 1 && eggBean.getLevel() > 5) {
                        SocketUtil.getInstance().sendEggDanmu(eggBean, mLiveBean);
                    }
                    AppConfig.getInstance().getUserBean().setCoin(String.valueOf(eggBean.getUcoin()));
                }
            } else {
                if (!TextUtils.isEmpty(msg))
                    ToastUtil.show(msg);
                dismiss();
            }
        }
    };

    public void onIntervalRpDuration() {
        if (duration <= 0) return;
        if (rpSubscirbe != null) {
            rpSubscirbe.dispose();
        }
        rpSubscirbe = Observable.intervalRange(0, duration, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aLong -> intervalTimeTv.setText(StringUtil.secToTime(duration--)))
                .doOnComplete(() -> {
                    getEggResult();
                    if (rpSubscirbe != null) {
                        rpSubscirbe.dispose();
                    }
                })
                .subscribe();
        disposable.add(rpSubscirbe);
    }

    private void setEggLevelImg(int level) {
        if (knockEggIv != null && isAdded()) {
            int imageResId;
            switch (level) {
                case 1:
                    imageResId = R.mipmap.ic_egg_level_1;
                    break;
                case 2:
                    imageResId = R.mipmap.ic_egg_level_2;
                    break;
                case 3:
                    imageResId = R.mipmap.ic_egg_level_3;
                    break;
                case 4:
                    imageResId = R.mipmap.ic_egg_level_4;
                    break;
                case 5:
                    imageResId = R.mipmap.ic_egg_level_5;
                    break;
                case 6:
                    imageResId = R.mipmap.ic_egg_level_6;
                    break;
                case 7:
                    imageResId = R.mipmap.ic_egg_level_7;
                    break;
                case 8:
                    imageResId = R.mipmap.ic_egg_level_8;
                    break;
                case 9:
                    imageResId = R.mipmap.ic_egg_level_9;
                    break;
                default:
                    imageResId = R.mipmap.ic_egg_level_0;
                    break;
            }
            knockEggIv.setImageDrawable(getResources().getDrawable(imageResId));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
        super.onDestroy();
    }

    private LiveBean mLiveBean;

    public void setLiveInfo(LiveBean mLiveBean) {
        this.mLiveBean = mLiveBean;
    }
}
