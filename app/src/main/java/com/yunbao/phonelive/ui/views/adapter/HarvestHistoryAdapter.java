package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.HarvestHistoryBean;
import com.yunbao.phonelive.ui.views.viewholder.HarvestHistoryVh;
import com.yunbao.phonelive.ui.views.viewholder.ItemEmptyVh;

import java.util.ArrayList;
import java.util.List;

/**
 * 提现记录
 */
public class HarvestHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HarvestHistoryBean> datas;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (getItemViewType(i) == 0) {
            return new ItemEmptyVh(viewGroup, "暂无提现记录");
        } else
            return new HarvestHistoryVh(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof HarvestHistoryVh) {
            ((HarvestHistoryVh) viewHolder).bindData(datas.get(i));
        }
    }

    public void setDatas(List<HarvestHistoryBean> datas) {
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

    public void addDatas(List<HarvestHistoryBean> datas) {
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }
        if (datas != null && datas.size() > 0) {
            this.datas.addAll(datas);
            notifyDataSetChanged();
        }
    }
}
