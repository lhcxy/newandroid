package com.yunbao.phonelive.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.interfaces.ChoseCallback;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;

public class ImageChooseDF extends DialogFragment {

    private CompositeDisposable disposable;

    public static ImageChooseDF newInstance() {
        Bundle args = new Bundle();
        ImageChooseDF fragment = new ImageChooseDF();
        fragment.setArguments(args);
        return fragment;
    }

    private Context mContext;
    private View mRootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.pop_image_choose_menus, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        disposable = new CompositeDisposable();
        initView();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private boolean hasContent = false;

    private void initView() {

        disposable.add(RxView.clicks(mRootView.findViewById(R.id.pop_photo_cancel_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            dismiss();
        }));
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.pop_take_photo_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (callback != null) {
                callback.onChose(0);
            }
            dismiss();
        }));
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.pop_take_album_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (callback != null) {
                callback.onChose(1);
            }
            dismiss();
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private ChoseCallback callback;

    public void setCallback(ChoseCallback callback) {
        this.callback = callback;
    }
}
