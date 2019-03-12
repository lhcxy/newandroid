package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.MsgCenterDetailBean;
import com.yunbao.phonelive.ui.views.viewholder.ItemEmptyVh;
import com.yunbao.phonelive.ui.views.viewholder.ItemMsgCenterDetail;

import java.util.ArrayList;
import java.util.List;

public class MsgCenterDetailRvAdapter extends RecyclerView.Adapter {
    private List<MsgCenterDetailBean> datas;

    public void setDatas(List<MsgCenterDetailBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void addDatas(List<MsgCenterDetailBean> datas) {
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 0x01) {
            return new ItemMsgCenterDetail(viewGroup);
        } else return new ItemEmptyVh(viewGroup, "暂无消息");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ItemMsgCenterDetail) {
            ((ItemMsgCenterDetail) viewHolder).onBindData(datas.get(i));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (datas == null || datas.size() == 0) {
            return 0x11;
        } else return 0x01;
    }

    @Override
    public int getItemCount() {
        return datas == null ? 1 : datas.size();
    }
}
