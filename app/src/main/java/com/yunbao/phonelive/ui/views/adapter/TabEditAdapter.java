package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.interfaces.TabChangeCallback;
import com.yunbao.phonelive.ui.helper.touchhelp.RVTouchSwapInterface;
import com.yunbao.phonelive.ui.views.TabEditActivity;
import com.yunbao.phonelive.ui.views.viewholder.TabVh;

import java.util.ArrayList;
import java.util.Collections;

public class TabEditAdapter extends RecyclerView.Adapter<TabVh> implements RVTouchSwapInterface {

    private ArrayList<TabBean> datas;
    private OnItemClickListener itemClickListener;

    public TabEditAdapter(ArrayList<TabBean> datas) {
        if (this.datas != null) {
            datas.clear();
        }
        this.datas = datas;
    }

    public void removeItem(int removeIndex) {
        if (datas != null && removeIndex >= 0 && datas.size() > removeIndex) {
            datas.remove(removeIndex);
            notifyItemRemoved(removeIndex);
        }
    }

    public ArrayList<TabBean> getDatas() {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        return datas;
    }

    public void addItem(TabBean data) {
        if (data != null && datas != null) {
            datas.add(data);
            notifyItemInserted(datas.size() - 1);
        }
    }


    @NonNull
    @Override
    public TabVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TabVh(parent, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TabVh holder, int position) {
        holder.onBindData(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setItemClickListener(OnItemClickListener<TabBean> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onDataSwap(int startIndex, int endIndex) {
        Collections.swap(datas, startIndex, endIndex);
        notifyItemMoved(startIndex, endIndex);
        if (changeCallBack != null) {
            changeCallBack.onChange();
        }
    }

    TabChangeCallback changeCallBack;

    public void setChangeCallBack(TabChangeCallback changeCallBack) {
        this.changeCallBack = changeCallBack;
    }
}
