package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.MainLiverVh;

import java.util.List;

public class RecommendHotLiverAdapter extends RecyclerView.Adapter<MainLiverVh> {
    private List<LiveBean> datas;
    private OnItemClickListener<LiveBean> listener;

    public void setDatas(List<LiveBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener<LiveBean> listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public MainLiverVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MainLiverVh(viewGroup, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MainLiverVh viewHolder, int i) {
        viewHolder.onBindData(datas.get(i));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }
}
