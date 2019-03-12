package com.yunbao.phonelive.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.MsgCenterListBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.MsgCenterListVh;

public class MsgCenterRvAdapter extends RecyclerView.Adapter<MsgCenterListVh> {
    private MsgCenterListBean data;

    public void setData(MsgCenterListBean data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MsgCenterListVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MsgCenterListVh(viewGroup, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgCenterListVh viewHolder, int i) {
        viewHolder.onBindData(data);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : 2;
    }

    private OnItemClickListener listener;

    public void setItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void clearUnread(int position) {
        if (position == 0) {
            data.setSnum(0);
            notifyDataSetChanged();
        } else {
            data.setUnum(0);
            notifyDataSetChanged();
        }

    }
}
