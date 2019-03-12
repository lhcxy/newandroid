package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.LiveTypeBSVh;

import java.util.List;

public class LiveTypeBSAdapter extends RecyclerView.Adapter<LiveTypeBSVh> {
    private List<TabBean> datas;
    private OnItemClickListener<TabBean> listener;
    private int selectIndex;

    public LiveTypeBSAdapter(List<TabBean> datas) {
        this.datas = datas;
        selectIndex = 0;
    }

    @NonNull
    @Override
    public LiveTypeBSVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LiveTypeBSVh(viewGroup, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveTypeBSVh viewHolder, int i) {
        viewHolder.onBindData(datas.get(i), i == selectIndex);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setListener(OnItemClickListener<TabBean> listener) {
        this.listener = listener;
    }

    public void setDatas(List<TabBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void setSelectIndex(int position) {
        if (position >= 0 && position < getItemCount()) {
            int temp = selectIndex;
            selectIndex = position;
            notifyItemChanged(temp);
            notifyItemChanged(selectIndex);
        }
    }

}
