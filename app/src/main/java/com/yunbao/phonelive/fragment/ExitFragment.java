package com.yunbao.phonelive.fragment;

import android.app.Activity;
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

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.DpUtil;

/**
 * Created by cxf on 2017/10/23.
 */

public class ExitFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_exit, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(340);
        params.height = DpUtil.dp2px(180);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRootView.findViewById(R.id.btn_confirm).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                if (mContext == null || (((Activity) mContext).isFinishing() || (((Activity) mContext).isDestroyed()))) {
                    return;
                }
                dismiss();
                ((Activity) mContext).finish();
                break;
            case R.id.btn_cancel:
                if (mContext == null || (((Activity) mContext).isFinishing() || (((Activity) mContext).isDestroyed()))) {
                    return;
                }
                dismiss();
                break;
        }
    }
}
