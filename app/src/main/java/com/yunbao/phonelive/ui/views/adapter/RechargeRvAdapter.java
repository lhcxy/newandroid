package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.ChargeTakeBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.ChargeNumVh;

import java.util.ArrayList;

public class RechargeRvAdapter extends RecyclerView.Adapter<ChargeNumVh> {
    private ArrayList<ChargeTakeBean> datas;
    private OnItemClickListener listener;
    private int selectedIndex = 0;


    public RechargeRvAdapter(OnItemClickListener listener) {
        datas = new ArrayList<>();
        datas.add(new ChargeTakeBean(100, "10", false));
        datas.add(new ChargeTakeBean(500, "50", true));
        datas.add(new ChargeTakeBean(1000, "100", true));
        datas.add(new ChargeTakeBean(5000, "500", false));
        datas.add(new ChargeTakeBean(10000, "1000", false));

        this.listener = listener;
    }

    @NonNull
    @Override
    public ChargeNumVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ChargeNumVh(viewGroup, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChargeNumVh viewHolder, int i) {
        viewHolder.onBindData(datas.get(i), selectedIndex);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    public String get(int i) {
        if (i < datas.size())
            return datas.get(i).getMoney();
        else return "0";
    }
}
