package com.yunbao.phonelive.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveRedPaperBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 获取红包结果
 */
public class LiveRpResultDF extends DialogFragment {

    private Context mContext;
    private View rootView;
    private CompositeDisposable disposable;

    /**
     * 主播发送红包编辑界面 dialog
     *
     * @return
     */
    public static LiveRpResultDF newInstance(String name, String thumb, String total) {
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("thumb", thumb);
        args.putString("total", total);
        LiveRpResultDF fragment = new LiveRpResultDF();
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
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_command_rp_result, null, false);
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

    private ImageView avatarIv;
    private TextView nameTv, contentTv;

    private void initView() {
        avatarIv = rootView.findViewById(R.id.result_avatar_iv);
        nameTv = rootView.findViewById(R.id.result_name_tv);
        contentTv = rootView.findViewById(R.id.result_content_tv);

        if (getArguments() != null) {
            String name = getArguments().getString("name");
            String thumb = getArguments().getString("thumb");
            String total = getArguments().getString("total");
            ImgLoader.displayCircleWhiteBorder(thumb, avatarIv);
            nameTv.setText(name);
            contentTv.setText("获得" + total + "元人民币");
        } else dismiss();
        disposable.add(RxView.clicks(rootView.findViewById(R.id.dialog_cancel_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> dismiss()));
        disposable.add(RxView.clicks(rootView.findViewById(R.id.dialog_rp_confirm_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> dismiss()));
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
}
