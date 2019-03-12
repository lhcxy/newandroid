package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;
import com.yunbao.phonelive.event.ConnEvent;
import com.yunbao.phonelive.receiver.ConnectivityReceiver;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2017/10/22.
 */

public class NetWorkErrorFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private TextView mCountDown;
    private TextView mContent;
    private TextView mBtn;
    private Handler mHandler;
    private int mCount = 50;
    private boolean mReConnSuccess;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_net_error, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog);
        dialog.setContentView(mRootView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        mCountDown = (TextView) dialog.findViewById(R.id.count_down);
        mContent = (TextView) dialog.findViewById(R.id.content);
        mBtn = (TextView) dialog.findViewById(R.id.btn);
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
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mCount--;
                if (mCount > 0) {
                    mCountDown.setText(String.valueOf(mCount));
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                } else {
                    if (ConnectivityReceiver.isNetworkAvailable()) {
                        reConnSuccess();
                    } else {
                        mReConnSuccess = false;
                        ToastUtil.show(WordUtil.getString(R.string.net_work_reconn_fail));
                        dismiss();
                    }
                }
            }
        };
        mBtn.setOnClickListener(this);
        EventBus.getDefault().register(this);
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void reConnSuccess() {
        mHandler.removeCallbacksAndMessages(null);
        mReConnSuccess = true;
        ToastUtil.show(WordUtil.getString(R.string.net_work_reconn_success));
        dismiss();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkEvent(ConnEvent e) {
        if (e.getCode() == ConnEvent.CONN_OK) {
            reConnSuccess();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        EventBus.getDefault().unregister(this);
        if (!mReConnSuccess) {
            ((LiveActivity) mContext).onNetWorkErrorCloseRoom();
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
