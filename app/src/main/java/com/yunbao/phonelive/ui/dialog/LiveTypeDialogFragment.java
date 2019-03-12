package com.yunbao.phonelive.ui.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.custom.loopview.LoopView;
import com.yunbao.phonelive.ui.helper.TabChannelHelper;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LiveTypeDialogFragment extends DialogFragment {
    public static LiveTypeDialogFragment newInstance() {
        LiveTypeDialogFragment fragment = new LiveTypeDialogFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private LoopView loopView;
    private int mIndex = 0;
    ArrayList<TabBean> fullData;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_live_type, null);
        loopView = view.findViewById(R.id.dialog_loopview_content);
        fullData = TabChannelHelper.getInstance().getFullData(getContext());
        RxView.clicks(view.findViewById(R.id.dialog_cancel)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> dismiss());

        RxView.clicks(view.findViewById(R.id.dialog_confirm)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
//            EventBus.getDefault().post(new LiveTypeEvent(fullData.get(mIndex).getId(), fullData.get(mIndex).getName()));
            dismiss();
        });
        loopView.setListener(index -> mIndex = index);

        ArrayList<String> loopData = new ArrayList<>();
        for (TabBean fullDatum : fullData) {
            loopData.add(fullDatum.getName());
        }
        loopView.setItems(loopData);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
