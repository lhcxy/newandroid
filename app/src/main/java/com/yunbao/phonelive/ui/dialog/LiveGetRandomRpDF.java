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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveRedPaperBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.tools.StringUtil;
import com.yunbao.phonelive.ui.views.anim.Rotate3dAnimation;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取随机红包
 */
public class LiveGetRandomRpDF extends DialogFragment {

    private Context mContext;
    private View rootView;
    private CompositeDisposable disposable;
    private ImageView coinIv;

    /**
     * 主播发送红包编辑界面 dialog
     *
     * @param isAttention 是否关注    0未关注  1 已关注
     * @return
     */
    public static LiveGetRandomRpDF newInstance(int isAttention, LiveRedPaperBean bean) {
        Bundle args = new Bundle();
        args.putInt("isAttention", isAttention);
        args.putSerializable("LiveRedPaperBean", bean);
        LiveGetRandomRpDF fragment = new LiveGetRandomRpDF();
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
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_get_random_rp, null, false);
        dialog.setContentView(rootView);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
//        int widthPixels = getResources().getDisplayMetrics().widthPixels;
//        params.width = widthPixels * 2 / 3;
        params.width = DpUtil.dp2px(224);
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

    private RelativeLayout firstView, secondView;
    private TextView getCoinNumTv;

    private void initView() {
        firstView = rootView.findViewById(R.id.dialog_rp_root_rl);
        secondView = rootView.findViewById(R.id.dialog_rp_root_second_rl);
        getCoinNumTv = rootView.findViewById(R.id.dialog_rp_get_coin_tv);
        coinIv = rootView.findViewById(R.id.dialog_rp_unopen_coin_iv);
        if (getArguments() != null) {
            bean = (LiveRedPaperBean) getArguments().getSerializable("LiveRedPaperBean");
            if (bean == null) {
                dismiss();
            }
        }
        disposable.add(RxView.clicks(rootView.findViewById(R.id.dialog_cancel_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> dismiss()));
        disposable.add(RxView.clicks(coinIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            getCoinRP();
        }));
        disposable.add(RxView.clicks(rootView.findViewById(R.id.dialog_rp_confirm_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            dismiss();
        }));
    }

    private void getCoinRP() {
        HttpUtil.getLiveRp(bean.getType(), bean.getUid(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                secondView.setVisibility(View.VISIBLE);
                firstView.setVisibility(View.GONE);
                if (code == 0) {
                    if (info != null && info.length > 0) {
                        JSONObject jsonObject = JSON.parseObject(info[0]);
                        if (jsonObject.containsKey("total")) {
                            String total = jsonObject.getString("total");
                            SpannableString string = new SpannableString(total + "人民币");
                            string.setSpan(new RelativeSizeSpan(1.2f), 0, total.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            getCoinNumTv.setText(string);
                        } else {
                            getCoinNumTv.setText(msg);
                        }
                    } else {
                        getCoinNumTv.setText(msg);
                    }
                } else {
                    getCoinNumTv.setText(msg);
                    ToastUtil.show(msg);
                }
            }
        });
    }

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
