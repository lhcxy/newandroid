package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.LotteryGetInfoBean;
import com.yunbao.phonelive.ui.views.viewholder.LotteryRecordVh;

import java.util.List;

public class LotteryRecordRvAda extends RecyclerView.Adapter<LotteryRecordVh> {
    @NonNull
    @Override
    public LotteryRecordVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LotteryRecordVh(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull LotteryRecordVh viewHolder, int i) {
        viewHolder.onBindData(datas.get(i));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    private List<LotteryGetInfoBean> datas;

    public void setData(List<LotteryGetInfoBean> linfo) {
        datas = linfo;
        notifyDataSetChanged();
    }
}
