package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.CerDescTypeVh;

import java.util.List;

public class CertificateDescRvAdapter extends RecyclerView.Adapter<CerDescTypeVh> {
    private List<TabBean> datas;
    private int selectedIndex = -1;
    private OnItemClickListener listener;

    public void setDatas(List<TabBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CerDescTypeVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CerDescTypeVh(viewGroup, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CerDescTypeVh viewHolder, int i) {
        viewHolder.onBindData(datas.get(i), selectedIndex);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }
}
