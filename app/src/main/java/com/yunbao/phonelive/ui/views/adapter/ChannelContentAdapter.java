package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.ChannelContentVh;
import com.yunbao.phonelive.ui.views.viewholder.LiveEmptyVh;
import com.yunbao.phonelive.ui.views.viewholder.NoMoreItemVh;

import java.util.ArrayList;
import java.util.List;


public class ChannelContentAdapter extends RecyclerView.Adapter {

    List<LiveBean> datas;

    public ChannelContentAdapter(List<LiveBean> datas) {
        this.datas = datas;
    }

    public ChannelContentAdapter() {
        this.datas = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == EMPTY) {
//            return new LiveEmptyVh(parent);
//        }
        if (viewType == 0x11) {
            return new NoMoreItemVh(parent);
        } else
            return new ChannelContentVh(parent, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChannelContentVh) {
            ((ChannelContentVh) holder).onBindData(datas.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (isNoMore) {
            return datas == null ? 1 : datas.size() + 1;
        } else
            return datas == null ? 0 : datas.size();
    }

    public void setData(List<LiveBean> data) {
        this.datas = data;
        isNoMore = false;
        notifyDataSetChanged();
    }

    public boolean isNoMore() {
        return isNoMore;
    }

    private boolean isNoMore = false;

    public void setIsNoMore(boolean b) {
        isNoMore = b;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= datas.size()) {
            return 0x011;
        } else return 0x01;
    }

    public void addData(List<LiveBean> data) {
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }
        this.datas.addAll(data);
        isNoMore = datas.size() >= 10 && (data == null || data.size() < 10);
        notifyDataSetChanged();
    }

    private OnItemClickListener<LiveBean> mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<LiveBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
