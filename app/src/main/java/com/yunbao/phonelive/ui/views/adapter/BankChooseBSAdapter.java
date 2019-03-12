package com.yunbao.phonelive.ui.views.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.BankChooseBSVh;
import com.yunbao.phonelive.ui.views.viewholder.LiveTypeBSVh;

import java.util.List;

public class BankChooseBSAdapter extends RecyclerView.Adapter<BankChooseBSVh> {
    private String[] datas;
    private OnItemClickListener<String> listener;
    private Context mContext;

    public BankChooseBSAdapter() {
    }

    @NonNull
    @Override
    public BankChooseBSVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BankChooseBSVh(viewGroup, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull BankChooseBSVh viewHolder, int i) {
        viewHolder.onBindData(datas[i]);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.length;
    }

    public void setListener(OnItemClickListener<String> listener) {
        this.listener = listener;
    }

    public void setDatas(String[] datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }
}
