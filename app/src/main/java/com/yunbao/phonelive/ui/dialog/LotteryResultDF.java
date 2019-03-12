package com.yunbao.phonelive.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.hyphenate.util.DensityUtil;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LotteryItemBean;
import com.yunbao.phonelive.ui.base.BaseDialogFragment;
import com.yunbao.phonelive.ui.helper.GridSpacingItemDecoration;
import com.yunbao.phonelive.ui.views.viewholder.LotteryResultVh;
import com.yunbao.phonelive.utils.DpUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;

public class LotteryResultDF extends BaseDialogFragment {

    private CompositeDisposable disposable;
    private Context mContext;
    private View mRootView;
    private RecyclerView recyclerView;

    public static LotteryResultDF newInstance(ArrayList<LotteryItemBean> viewType) {
        Bundle args = new Bundle();
        LotteryResultDF fragment = new LotteryResultDF();
        args.putSerializable("viewType", viewType);
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_lottery_result, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels - DensityUtil.dip2px(mContext, 72);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        disposable = new CompositeDisposable();
        initView();
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    private void initView() {
        recyclerView = mRootView.findViewById(R.id.recyclerView2);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, DpUtil.dp2px(8), true));
        RecyclerView.Adapter adapter = new RecyclerView.Adapter<LotteryResultVh>() {
            @NonNull
            @Override
            public LotteryResultVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new LotteryResultVh(viewGroup);
            }

            @Override
            public void onBindViewHolder(@NonNull LotteryResultVh viewHolder, int i) {
                viewHolder.onBindData(datas.get(i));
            }

            @Override
            public int getItemCount() {
                return datas == null ? 0 : datas.size();
            }
        };
        recyclerView.setAdapter(adapter);
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.lottery_close_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> dismiss()));
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.result_know_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> dismiss()));
        if (getArguments() != null) {
            datas = (ArrayList<LotteryItemBean>) getArguments().getSerializable("viewType");
            adapter.notifyDataSetChanged();
        }
    }

    private ArrayList<LotteryItemBean> datas;

    @Override
    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroy();
    }
}
