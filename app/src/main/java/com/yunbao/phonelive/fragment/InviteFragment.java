package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.custom.PwdEditText;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

/**
 * Created by cxf on 2017/9/30.
 * 邀请码输入框
 */

public class InviteFragment extends DialogFragment implements PwdEditText.OnTextFinishListener, View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private TextView mName;
    private PwdEditText mPwdEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getContext();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_invite, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(300);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mName = (TextView) mRootView.findViewById(R.id.name);
        mName.setText("TO:" + getArguments().getString("name"));
        mPwdEditText = (PwdEditText) mRootView.findViewById(R.id.pwd_text);
        mPwdEditText.initStyle(R.drawable.bg_input_invite, 6, 1, 0xffffffff, 0xffffffff, 20);
        mPwdEditText.setOnTextFinishListener(this);
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
    }

    @Override
    public void onFinish(String content) {
        HttpUtil.setDistribut(content, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    dismiss();
                }
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.SET_DISTRIBUT);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
