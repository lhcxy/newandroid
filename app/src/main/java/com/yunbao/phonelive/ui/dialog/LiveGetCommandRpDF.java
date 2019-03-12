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
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveRedPaperBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.tools.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LiveGetCommandRpDF extends DialogFragment {

    private Context mContext;
    private View rootView;
    private CompositeDisposable disposable;
    private int isAttention = 0;

    /**
     * 主播发送红包编辑界面 dialog
     *
     * @param isAttention 是否关注    0未关注  1 已关注
     * @return
     */
    public static LiveGetCommandRpDF newInstance(int isAttention, LiveRedPaperBean bean) {
        Bundle args = new Bundle();
        args.putInt("isAttention", isAttention);
        args.putSerializable("LiveRedPaperBean", bean);
        LiveGetCommandRpDF fragment = new LiveGetCommandRpDF();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        disposable = new CompositeDisposable();
        Dialog dialog = new Dialog(mContext, R.style.dialog);
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_get_rp, null, false);
        dialog.setContentView(rootView);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initView();
        super.onActivityCreated(savedInstanceState);
    }

    private LinearLayout bottomFollowLl;

    private TextView commandDescTv, timeTv, titleTv;

    private void initView() {
        bottomFollowLl = rootView.findViewById(R.id.dialog_get_follow_ll);
        commandDescTv = rootView.findViewById(R.id.dialog_get_command_desc_tv);
        timeTv = rootView.findViewById(R.id.dialog_get_time_tv);
        titleTv = rootView.findViewById(R.id.dialog_get_title_tv);
        disposable.add(RxView.clicks(rootView.findViewById(R.id.dialog_get_follow_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            //follow
            if (bean != null) {
                HttpUtil.setAttention(bean.getUid(), new CommonCallback<Integer>() {
                    @Override
                    public void callback(Integer obj) {
                        isAttention = obj;
                        if (isAttention == 0) {
                            bottomFollowLl.setVisibility(View.VISIBLE);
                        } else {
                            bottomFollowLl.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }));
        disposable.add(RxView.clicks(rootView.findViewById(R.id.dialog_get_commit_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            // TODO: 2018/11/15 commit
            if (isAttention == 0) {
                ToastUtil.show("请先关注主播!");
            } else {
                if (bean != null) {
                    HttpUtil.getLiveRp(bean.getType(), bean.getUid(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                SocketUtil.getInstance().sendChatMsg(bean.getKoul());
                                dismiss();
                            } else {
                                ToastUtil.show(msg);
                            }
                        }
                    });
                } else dismiss();
            }

        }));

        disposable.add(RxView.clicks(rootView.findViewById(R.id.dialog_cancel_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> dismiss()));
//        String title = getArguments().getString("title");

        if (getArguments() != null) {
            isAttention = getArguments().getInt("isAttention");
            if (isAttention == 0) {
                bottomFollowLl.setVisibility(View.VISIBLE);
            } else {
                bottomFollowLl.setVisibility(View.GONE);
            }
            bean = (LiveRedPaperBean) getArguments().getSerializable("LiveRedPaperBean");
            if (bean != null) {
                ForegroundColorSpan giftNameSp = new ForegroundColorSpan(getResources().getColor(R.color.yellow));
                SpannableString string = new SpannableString(bean.getTitle() + "元人民币");
                string.setSpan(giftNameSp, 0, bean.getTitle().length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                string.setSpan(new RelativeSizeSpan(1.1f), 0, bean.getTitle().length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                titleTv.setText(string);

                commandDescTv.setText(bean.getKoul());
                duration = bean.getTime();
                subscribe = Observable.intervalRange(0, duration, 0, 1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(aLong -> timeTv.setText(StringUtil.secToTime(duration--)))
                        .doOnComplete(() -> subscribe.dispose())
                        .subscribe();
                disposable.add(subscribe);

            } else dismiss();
        } else dismiss();

    }

    Disposable subscribe;
    private int duration = 0;
    private LiveRedPaperBean bean;

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
}
