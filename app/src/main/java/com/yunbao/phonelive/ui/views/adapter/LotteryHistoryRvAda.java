package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.LotteryGetInfoBean;
import com.yunbao.phonelive.ui.views.viewholder.LotteryHistoryVh;

import java.util.ArrayList;
import java.util.List;

public class LotteryHistoryRvAda extends RecyclerView.Adapter<LotteryHistoryVh> {
    private List<LotteryGetInfoBean> datas;

    @NonNull
    @Override
    public LotteryHistoryVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LotteryHistoryVh(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull LotteryHistoryVh lotteryHistoryVh, int i) {
        lotteryHistoryVh.onBindData(datas.get(i));
    }

    public void addData(List<LotteryGetInfoBean> datas) {
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }
}
