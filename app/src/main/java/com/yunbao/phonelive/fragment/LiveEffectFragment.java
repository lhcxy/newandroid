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
import com.yunbao.phonelive.utils.DpUtil;

/**
 * Created by cxf on 2017/9/1.
 * 主播直播间美颜,滤镜等效果
 */

public class LiveEffectFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private FragmentManager mFragmentManager;
    private Fragment mBeautyFragment;
    private Fragment mFilterFragment;
    private int mCurItem;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_live_effect, null, false);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(250);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.bottomToTopAnim);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_live_effect, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRootView.findViewById(R.id.btn_back).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_beauty).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_filter).setOnClickListener(this);
        mFragmentManager = getChildFragmentManager();
        if (mBeautyFragment == null) {
            mBeautyFragment = new LiveBeautyFragment();
        }
        if (mFilterFragment == null) {
            mFilterFragment = new LiveFilterFragment();
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.replaced, mBeautyFragment);
        ft.add(R.id.replaced, mFilterFragment);
        if (mCurItem == 0) {
            ft.show(mBeautyFragment).hide(mFilterFragment).commit();
        } else if (mCurItem==1){
            ft.show(mFilterFragment).hide(mBeautyFragment).commit();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                dismiss();
                break;
            case R.id.btn_beauty:
                toggle1();
                break;
            case R.id.btn_filter:
                toggle2();
                break;
        }
    }

    private void toggle1() {
        mCurItem = 0;
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.show(mBeautyFragment).hide(mFilterFragment).commit();
    }

    private void toggle2() {
        mCurItem = 1;
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.show(mFilterFragment).hide(mBeautyFragment).commit();
    }
}
