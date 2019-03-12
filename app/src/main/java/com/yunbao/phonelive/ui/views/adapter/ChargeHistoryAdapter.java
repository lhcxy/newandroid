package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.ChargeHistoryBean;
import com.yunbao.phonelive.bean.HarvestHistoryBean;
import com.yunbao.phonelive.ui.views.viewholder.ItemEmptyVh;
import com.yunbao.phonelive.ui.views.viewholder.ChargeHistoryVh;

import java.util.ArrayList;
import java.util.List;

public class ChargeHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChargeHistoryBean> datas;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (getItemViewType(i) == 0) {
            return new ItemEmptyVh(viewGroup, "暂无充值记录");
        } else
            return new ChargeHistoryVh(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ChargeHistoryVh) {
            ((ChargeHistoryVh) viewHolder).bindData(datas.get(i));
        }
    }

    public void setDatas(List<ChargeHistoryBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && (datas == null || datas.size() == 0)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return datas == null || datas.size() == 0 ? 1 : datas.size();
    }

    public void addDatas(List<ChargeHistoryBean> datas) {
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }
        if (datas != null && datas.size() > 0) {
            this.datas.addAll(datas);
            notifyDataSetChanged();
        }
    }

}
