package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.MsgSearchHistoryVh;

import java.util.List;

public class MsgSearchHistoryRvAda extends RecyclerView.Adapter<MsgSearchHistoryVh> {

    private List<String> datas;
    private OnItemClickListener listener;

    public MsgSearchHistoryRvAda(List<String> datas) {
        this.datas = datas;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MsgSearchHistoryVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MsgSearchHistoryVh(viewGroup, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgSearchHistoryVh viewHolder, int i) {
        viewHolder.onBindData(datas.get(i));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }
}
