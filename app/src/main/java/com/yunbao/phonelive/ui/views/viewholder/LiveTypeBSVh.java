package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

import java.util.concurrent.TimeUnit;

public class LiveTypeBSVh extends RecyclerView.ViewHolder {
    private TextView content;

    public LiveTypeBSVh(@NonNull ViewGroup parent, OnItemClickListener<TabBean> listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_type_bs, parent, false));
        content = itemView.findViewById(R.id.item_live_content_tv);
        RxView.clicks(content).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (listener != null) {
                        listener.onItemClick(null, getAdapterPosition());
                    }
                });
    }

    public void onBindData(TabBean data, boolean isChecked) {
        content.setSelected(isChecked);
        content.setText(data.getName());
    }

}
