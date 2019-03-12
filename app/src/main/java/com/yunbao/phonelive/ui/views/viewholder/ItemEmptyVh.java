package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;

/**
 * 列表为空时展示的空界面
 */
public class ItemEmptyVh extends RecyclerView.ViewHolder {
    private TextView emptyTv;

    public ItemEmptyVh(@NonNull ViewGroup parent, String content) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_live_empty, parent, false));
        emptyTv = itemView.findViewById(R.id.empty_tv);
        emptyTv.setText(content);
    }

}
