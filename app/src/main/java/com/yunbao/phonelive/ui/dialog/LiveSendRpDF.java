package com.yunbao.phonelive.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.AutoText;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxRadioGroup;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAnchorActivity;
import com.yunbao.phonelive.activity.LiveAnchorHorizontalActivity;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.tools.KeyboardUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class LiveSendRpDF extends DialogFragment {

    private Context mContext;
    private View rootView;
    private CompositeDisposable disposable;

    /**
     * 主播发送红包编辑界面 dialog
     *
     * @param viewType 0==竖屏  1=横屏
     * @return
     */
    public static LiveSendRpDF newInstance(int viewType) {
        Bundle args = new Bundle();
        args.putInt("viewType", viewType);
        LiveSendRpDF fragment = new LiveSendRpDF();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private int viewType = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        disposable = new CompositeDisposable();
        if (getArguments() != null) {
            viewType = getArguments().getInt("viewType");
        }
        Dialog dialog = new Dialog(mContext, R.style.dialog);
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sendrp, null, false);
        if (viewType == 1) rootView.findViewById(R.id.dialog_cancel_iv).setVisibility(View.GONE);
        dialog.setContentView(rootView);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (viewType == 1) {
            params.width = mContext.getResources().getDisplayMetrics().widthPixels / 2;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        } else {
            params.width = mContext.getResources().getDisplayMetrics().widthPixels - DpUtil.dp2px(48);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
        }
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initView();
        super.onActivityCreated(savedInstanceState);
    }

    private LinearLayout twbContentLl, randomLl;
    private View twbTitleView, randomTitleView;
    private int rpType = 2;//1-随机红包,2-口令兔尾巴红包，3-口令自定义红包
    //    private int klType = 0; // 0= 自定义红包  1 =兔尾巴红包
    //    private TextView klLeftRb, klRightRb;
    //    口令红包
    private EditText titleEt, commandEt, timeEt;

    //随机红包
    private EditText coinCountEt, rpNumEt;
    private TextView unitTv;

    private void initView() {
        twbContentLl = rootView.findViewById(R.id.rp_content_twb_ll);
        randomLl = rootView.findViewById(R.id.rp_content_random_ll);
        twbTitleView = rootView.findViewById(R.id.rp_title_kl_v);
        randomTitleView = rootView.findViewById(R.id.rp_title_random_v);
//        klLeftRb = rootView.findViewById(R.id.dialog_kl_rp_type_left_rb);
//        klRightRb = rootView.findViewById(R.id.dialog_kl_rp_type_right_rb);
        titleEt = rootView.findViewById(R.id.rp_title_et);
        commandEt = rootView.findViewById(R.id.rp_command_tv);
        timeEt = rootView.findViewById(R.id.rp_time_et);
        unitTv = rootView.findViewById(R.id.rp_title_unit_tv);
        rpType = 2;
        coinCountEt = rootView.findViewById(R.id.rp_coin_count_et);
        rpNumEt = rootView.findViewById(R.id.rp_count_et);
//        klLeftRb.setChecked(true);

//        disposable.add(RxRadioGroup.checkedChanges(rootView.findViewById(R.id.dialog_kl_rp_type_rg)).subscribe(integer -> {
//            titleEt.setText("");
//            if (integer == R.id.dialog_kl_rp_type_left_rb) {
//                klType = 0;
//                titleEt.setKeyListener(TextKeyListener.getInstance());
//                unitTv.setVisibility(View.GONE);
//            } else {
//                klType = 1;
//                titleEt.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
//                unitTv.setVisibility(View.VISIBLE);
//            }
//        }));

        disposable.add(RxTextView.afterTextChangeEvents(timeEt).subscribe(textViewAfterTextChangeEvent -> {
            if (textViewAfterTextChangeEvent != null && textViewAfterTextChangeEvent.editable() != null && !TextUtils.isEmpty(textViewAfterTextChangeEvent.editable().toString().trim())) {
                Integer integer = Integer.valueOf(textViewAfterTextChangeEvent.editable().toString().trim());
                if (integer > 60) {
                    timeEt.setText("60");
                }
            }
        }));

        disposable.add(RxView.clicks(rootView.findViewById(R.id.rp_title_kl_ll)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (rpType != 2) {
                twbTitleView.setVisibility(View.VISIBLE);
                randomTitleView.setVisibility(View.INVISIBLE);
                twbContentLl.setVisibility(View.VISIBLE);
                randomLl.setVisibility(View.GONE);
                rpType = 2;
            }
        }));
        disposable.add(RxView.clicks(rootView.findViewById(R.id.rp_title_random_ll)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (rpType != 1) {
                twbTitleView.setVisibility(View.INVISIBLE);
                randomTitleView.setVisibility(View.VISIBLE);
                twbContentLl.setVisibility(View.GONE);
                randomLl.setVisibility(View.VISIBLE);
                rpType = 1;
            }
        }));
        disposable.add(RxView.clicks(rootView.findViewById(R.id.dialog_cancel_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> dismiss()));

        disposable.add(RxView.clicks(rootView.findViewById(R.id.rp_reset_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            titleEt.setText("");
            commandEt.setText("");
            timeEt.setText("");
            coinCountEt.setText("");
            rpNumEt.setText("");
        }));

        disposable.add(RxView.clicks(rootView.findViewById(R.id.rp_commit_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            commitRpInfo();
        }));

    }

    /**
     * 提交红包信息
     */
    private void commitRpInfo() {
        if (rpType == 2) {
            if (TextUtils.isEmpty(commandEt.getText().toString().trim())) {
                ToastUtil.show("请输入口令");
                return;
            }
            if (TextUtils.isEmpty(timeEt.getText().toString().trim())) {
                ToastUtil.show("请输入时间");
                return;
            }
            if (TextUtils.isEmpty(titleEt.getText().toString().trim())) {
//                if (klType == 0)
//                    ToastUtil.show("请输入名称");
                ToastUtil.show("请输入红包数量");
                return;
            }
            HttpUtil.sendCommandRp(2, titleEt.getText().toString().trim(),
                    commandEt.getText().toString().trim(), Integer.valueOf(timeEt.getText().toString().trim()), callback);
        } else {
            if (TextUtils.isEmpty(coinCountEt.getText().toString().trim())) {
                ToastUtil.show("请输入红包数量");
                return;
            }
            if (TextUtils.isEmpty(rpNumEt.getText().toString().trim())) {
                ToastUtil.show("请输入红包个数");
                return;
            }
            HttpUtil.sendRandomRP(Long.valueOf(coinCountEt.getText().toString().trim()), Long.valueOf(rpNumEt.getText().toString().trim()), callback);
        }
    }

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null && info.length > 0) {
                JSONObject jsonObject = JSON.parseObject(info[0]);
                if (jsonObject.containsKey("total")) {
                    AppConfig.getInstance().getUserBean().setCoin(String.valueOf(jsonObject.getLong("total")));
                    if (jsonObject.containsKey("time") && jsonObject.containsKey("type")) {
                        int type = jsonObject.getIntValue("type");
                        if (type == 1) {
                            SocketUtil.getInstance().sendRedPaper(jsonObject.getIntValue("type"), jsonObject.getIntValue("time"), "", "");
                        } else {
                            SocketUtil.getInstance().sendRedPaper(jsonObject.getIntValue("type"), jsonObject.getIntValue("time"), jsonObject.getString("koul"), jsonObject.getString("name"));
                            if (getActivity() != null && getActivity() instanceof LiveAnchorHorizontalActivity) {
                                ((LiveAnchorHorizontalActivity) getActivity()).onIntervalRpDuration(jsonObject.getIntValue("time"), type, jsonObject.getString("rid"));
                            } else if (getActivity() != null && getActivity() instanceof LiveAnchorActivity) {
                                ((LiveAnchorActivity) getActivity()).onIntervalRpDuration(jsonObject.getIntValue("time"), type, jsonObject.getString("rid"));
                            }
                        }
                        ToastUtil.show("发送成功！");
                        KeyboardUtil.hideSoftInput(titleEt);
                        dismiss();
                    } else ToastUtil.show(msg);
                } else ToastUtil.show(msg);
            } else {
                ToastUtil.show(msg);
            }
        }
    };

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
