package com.yunbao.phonelive.ui.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.interfaces.ChoseCallback;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;

public class ChargeTypeBsF extends BottomSheetDialogFragment {
    public static ChargeTypeBsF newInstance() {
        ChargeTypeBsF fragment = new ChargeTypeBsF();
        return fragment;
    }

    CompositeDisposable disposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposable = new CompositeDisposable();
        setStyle(BottomSheetDialogFragment.STYLE_NO_TITLE, R.style.CustomBottomSheetDialogTheme);
    }

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_charge_type, container, false);
        disposable.add(RxView.clicks(rootView.findViewById(R.id.type_ali)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (callback != null) {
                callback.onChose(0);
            }
            dismiss();
        }));
        disposable.add(RxView.clicks(rootView.findViewById(R.id.type_wechat)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (callback != null) {
                callback.onChose(1);
            }
            dismiss();
        }));
        return rootView;
    }

    private ChoseCallback callback;

    public void setCallback(ChoseCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }
}
