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
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.utils.DpUtil;

/**
 * Created by cxf on 2017/9/18.
 */

public class TimeChargeFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private String mMessage;
    private TextView mContent;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm, null, false);
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        dialog.setContentView(mRootView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
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
        TextView titleView = (TextView) mRootView.findViewById(R.id.title);
        titleView.setText(getString(R.string.tip));
        mContent = (TextView) mRootView.findViewById(R.id.content);
        mContent.setText(mMessage);
        mRootView.findViewById(R.id.cancel_btn).setOnClickListener(this);
        mRootView.findViewById(R.id.confirm_btn).setOnClickListener(this);
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void showMessage() {
        mContent.setText(mMessage);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                dismiss();
                ((LiveAudienceActivity) mContext).onBackPressed();
                break;
            case R.id.confirm_btn:
                dismiss();
                ((LiveAudienceActivity) mContext).roomCharge();
                break;
        }
    }
}
