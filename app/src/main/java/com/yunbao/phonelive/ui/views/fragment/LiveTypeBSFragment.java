package com.yunbao.phonelive.ui.views.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.event.LiveBsEvent;
import com.yunbao.phonelive.event.LiveTypeEvent;
import com.yunbao.phonelive.ui.helper.GridSpacingItemDecoration;
import com.yunbao.phonelive.ui.views.adapter.LiveTypeBSAdapter;
import com.yunbao.phonelive.utils.DpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LiveTypeBSFragment extends BottomSheetDialogFragment {

    private View rootView;

    private ImageView closeIv;
    private RecyclerView bottomSheetRv;
    private List<TabBean> datas;
    private LiveTypeBSAdapter adapter;

    public static LiveTypeBSFragment newInstance() {
        LiveTypeBSFragment fragment = new LiveTypeBSFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_live_type_bottomsheet, container, false);
        closeIv = rootView.findViewById(R.id.bottom_sheet_close_iv);
        bottomSheetRv = rootView.findViewById(R.id.bottom_sheet_rv);

        RxView.clicks(rootView.findViewById(R.id.bottom_sheet_close_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> dismiss());
        bottomSheetRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        bottomSheetRv.addItemDecoration(new GridSpacingItemDecoration(4, DpUtil.dp2px(16), false));
        if (adapter == null) {
            adapter = new LiveTypeBSAdapter(datas);
            if (index != -1)
                adapter.setSelectIndex(index);
        }
        adapter.setListener((item, position) -> {
            adapter.setSelectIndex(position);
            EventBus.getDefault().post(new LiveTypeEvent(position));
            dismiss();
        });
        bottomSheetRv.setAdapter(adapter);
        return rootView;
    }

    public void setDatas(List<TabBean> datas) {
        this.datas = datas;
        if (bottomSheetRv == null && rootView != null) {
            bottomSheetRv = rootView.findViewById(R.id.bottom_sheet_rv);
            if (bottomSheetRv != null) {
                bottomSheetRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
                bottomSheetRv.addItemDecoration(new GridSpacingItemDecoration(4, DpUtil.dp2px(8), false));
            }
        }
        if (adapter == null) {
            adapter = new LiveTypeBSAdapter(datas);
            adapter.setListener((item, position) -> {
                adapter.setSelectIndex(position);
                EventBus.getDefault().post(new LiveBsEvent(position));
                dismiss();
            });
            if (bottomSheetRv != null) {
                bottomSheetRv.setAdapter(adapter);
            }
        }
        if (adapter != null) {
            adapter.setDatas(datas);
        }
    }

    private int index = -1;

    public void setIndex(int typeIndex) {
        if (adapter != null) {
            adapter.setSelectIndex(typeIndex);
        } else index = typeIndex;
    }
}
