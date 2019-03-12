package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.DailyTaskBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.DailyTaskVh;

import java.util.List;

public class DailyTaskRVAdapter extends RecyclerView.Adapter<DailyTaskVh> {
    private List<DailyTaskBean> datas;
    private OnItemClickListener listener;

    @NonNull
    @Override
    public DailyTaskVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DailyTaskVh(viewGroup, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyTaskVh viewHolder, int i) {
        viewHolder.bindData(datas.get(i));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setDatas(List<DailyTaskBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
