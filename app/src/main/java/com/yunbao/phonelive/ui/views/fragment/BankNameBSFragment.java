package com.yunbao.phonelive.ui.views.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.helper.GridSpacingItemDecoration;
import com.yunbao.phonelive.ui.views.adapter.BankChooseBSAdapter;
import com.yunbao.phonelive.ui.views.adapter.LiveTypeBSAdapter;
import com.yunbao.phonelive.utils.DpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BankNameBSFragment extends BottomSheetDialogFragment {

    private View rootView;

    private RecyclerView bottomSheetRv;
    private String[] datas;
    private BankChooseBSAdapter adapter;

    public static BankNameBSFragment newInstance() {
        BankNameBSFragment fragment = new BankNameBSFragment();
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
        rootView = inflater.inflate(R.layout.dialog_bank_choose_bottomsheet, container, false);
        bottomSheetRv = rootView.findViewById(R.id.bottom_sheet_rv);
        datas = getResources().getStringArray(R.array.banks_name);
        bottomSheetRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        if (adapter == null) {
            adapter = new BankChooseBSAdapter();
            adapter.setDatas(datas);
        }
        adapter.setListener((item, position) -> {
            if (listener != null) {
                listener.onItemClick(item, position);
            }
            dismiss();
        });
        bottomSheetRv.setAdapter(adapter);
        return rootView;
    }

    private OnItemClickListener<String> listener;

    public void setListener(OnItemClickListener<String> listener) {
        this.listener = listener;
    }
}
