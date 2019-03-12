package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAnchorActivity;

/**
 * Created by cxf on 2017/9/1.
 * 主播直播间美颜,滤镜等效果
 */

public class LiveMeiYanFragment extends DialogFragment {

    private Context mContext;
    private View mRootView;
    LiveAnchorActivity mLiveAnchorActivity;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        mLiveAnchorActivity=(LiveAnchorActivity) getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_meiyan, null, false);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.bottomToTopAnim);
        return dialog;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_meiyan, container, false);
//        ((TiControlView)mRootView.findViewById(R.id.ti_control_layout)).setOnEventListener(mLiveAnchorActivity.mPushStreamFragment.getTiSDKManager().initUIEventListener(new TiSDKManager.UIClickListener() {
//            @Override
//            public void onTakeShutter() {
//            }
//            @Override
//            public void onSwitchCamera() {
//            }
//            @Override
//            public void onCloseCtrolView() {
//                dismiss();
//                mLiveAnchorActivity.mPushStreamFragment.setFilter();
//            }
//        }));

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
