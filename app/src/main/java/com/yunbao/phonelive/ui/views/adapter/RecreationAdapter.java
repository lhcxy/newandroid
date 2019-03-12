package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.NoMoreItemVh;
import com.yunbao.phonelive.ui.views.viewholder.RecreationLiveHomeVh;

import java.util.ArrayList;
import java.util.List;

public class RecreationAdapter extends RecyclerView.Adapter {

    private List<LiveBean> datas;

    public RecreationAdapter() {

    }

    private OnItemClickListener listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0x11)
            return new NoMoreItemVh(parent);
        else
            return new RecreationLiveHomeVh(parent, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecreationLiveHomeVh) {
            ((RecreationLiveHomeVh) holder).onBindData(datas.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= datas.size()) return 0x011;
        else return 0x01;
    }

    public boolean isNoMore() {
        return isNoMore;
    }

    private boolean isNoMore = false;

    @Override
    public int getItemCount() {
        if (isNoMore) {
            return datas == null ? 1 : datas.size() + 1;
        } else
            return datas == null ? 0 : datas.size();
    }

    public void setIsNoMore(boolean b) {
        isNoMore = b;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setDatas(List<LiveBean> data) {
        this.datas = data;
        isNoMore = false;
        notifyDataSetChanged();
    }

    public void addDatas(List<LiveBean> data) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        isNoMore = data == null || data.size() < 10;
        datas.addAll(data);
        notifyDataSetChanged();
    }
}
