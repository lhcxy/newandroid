package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.GiftBean;
import com.yunbao.phonelive.ui.views.viewholder.ItemEmptyVh;
import com.yunbao.phonelive.ui.views.viewholder.MinePackageGiftItemVh;

import java.util.ArrayList;
import java.util.List;

public class MinePackageAdapter extends RecyclerView.Adapter {
    private List<GiftBean> datas;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (getItemViewType(i) == 0) {
            return new ItemEmptyVh(viewGroup, "包裹中暂无礼物");
        } else
            return new MinePackageGiftItemVh(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof MinePackageGiftItemVh) {
            ((MinePackageGiftItemVh) viewHolder).bindData(datas.get(i));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && (datas == null || datas.size() == 0)) {
            return 0;
        } else return 1;
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size() == 0 ? 1 : datas.size();
    }

    public void setDatas(List<GiftBean> list) {
        this.datas = list;
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }
        notifyDataSetChanged();
    }
}
