package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

public class MsgSearchHistoryVh extends RecyclerView.ViewHolder {
    private TextView contentTv;
    private OnItemClickListener<Integer> listener;

    public MsgSearchHistoryVh(@NonNull ViewGroup parent, OnItemClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_search_history, parent, false));
        this.listener = listener;
        contentTv = itemView.findViewById(R.id.history_content_tv);
        itemView.findViewById(R.id.history_clear_iv).setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(1, getAdapterPosition());
            }
        });
        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(0, getAdapterPosition());
            }
        });
    }

    public void onBindData(String content) {
        contentTv.setText(content);
    }
}
