package com.yunbao.phonelive.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.ItemLiveHomeVh;
import com.yunbao.phonelive.ui.views.viewholder.ItemSearchVh;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cxf on 2017/8/9.
 */
public class SearchRvAdapter extends RecyclerView.Adapter {


    private List<LiveBean> mItemList;
    private OnItemClickListener<LiveBean> mOnItemClickListener;

    public SearchRvAdapter(List<LiveBean> itemList) {
        mItemList = itemList;
    }

    public SearchRvAdapter() {
    }

    public void setOnItemClickListener(OnItemClickListener<LiveBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void addDatas(List<LiveBean> itemList) {
        if (mItemList == null) {
            mItemList = new ArrayList<>();
        }
        mItemList.addAll(itemList);
        notifyDataSetChanged();
    }

    public void setData(List<LiveBean> itemList) {
        mItemList = itemList;
        notifyDataSetChanged();
    }

    public void clearData() {
        if (mItemList != null)
            mItemList.clear();
        mItemList = null;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemSearchVh(parent, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemSearchVh) {
            ((ItemSearchVh) holder).onBindData(mItemList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

}
