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
import com.yunbao.phonelive.ui.views.viewholder.NoMoreItemVh;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cxf on 2017/8/9.
 */
public class HomeHotAdapter extends RecyclerView.Adapter {

    private final int HEADER_TYPE = 0;
    private final int NORMAL_TYPE = 1;

    private List<LiveBean> mItemList;
    private OnItemClickListener<LiveBean> mOnItemClickListener;

    public HomeHotAdapter(List<LiveBean> itemList) {
        mItemList = itemList;
    }

    public HomeHotAdapter() {
    }

    public void setOnItemClickListener(OnItemClickListener<LiveBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void addDatas(List<LiveBean> itemList) {
        if (mItemList == null) {
            mItemList = new ArrayList<>();
        }
        if (isShowNoMore)
            isNoMore = itemList == null || itemList.size() < 10;
        mItemList.addAll(itemList);
        notifyDataSetChanged();
    }

    public void setData(List<LiveBean> itemList) {
        mItemList = itemList;
        if (isShowNoMore)
            isNoMore = mItemList.size() < 10;
        notifyDataSetChanged();
    }

    private boolean isShowNoMore = true;

    public void isShowNoMore(boolean isShowNoMore) {
        this.isShowNoMore = isShowNoMore;
    }

    public void clearData() {
        if (mItemList != null)
            mItemList.clear();
        mItemList = null;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0x01) {
            return new ItemLiveHomeVh(parent, mOnItemClickListener);
        } else return new NoMoreItemVh(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemLiveHomeVh) {
            ((ItemLiveHomeVh) holder).onBindData(mItemList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mItemList.size()==0){
            return 0;
        }
        if (position >= mItemList.size()) {
            return 0x011;
        } else return 0x01;
    }

    public boolean isNoMore() {
        return isNoMore;
    }

    private boolean isNoMore = false;

    @Override
    public int getItemCount() {
        if (isNoMore && isShowNoMore) {
            return mItemList == null ? 1 : mItemList.size() + 1;
        } else
            return mItemList == null ? 0 : mItemList.size();
    }

    public void setIsNoMore(boolean b) {
        isNoMore = b;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LiveBean mBean;
        int mPosition;
        ImageView headImg;
        ImageView img;
        TextView name;
        TextView city;
        TextView nums;
        TextView title;
        ImageView type;
        ImageView game;
        ImageView anchorLevel;//主播等级

        public ViewHolder(View itemView) {
            super(itemView);
            headImg = (ImageView) itemView.findViewById(R.id.headImg);
            img = (ImageView) itemView.findViewById(R.id.img);
            nums = (TextView) itemView.findViewById(R.id.nums);
            name = (TextView) itemView.findViewById(R.id.name);
            city = (TextView) itemView.findViewById(R.id.city);
            title = (TextView) itemView.findViewById(R.id.title);
            type = (ImageView) itemView.findViewById(R.id.live_type);
            game = (ImageView) itemView.findViewById(R.id.game);
            anchorLevel = (ImageView) itemView.findViewById(R.id.anchor_level);
            itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mBean, mPosition);
                }
            });
        }

        void setData(LiveBean bean, int position) {
            mBean = bean;
            mPosition = position;
            name.setText(bean.getUser_nicename());
            city.setText(bean.getCity());
            if (!"".equals(bean.getTitle())) {
                if (title.getVisibility() == View.GONE) {
                    title.setVisibility(View.VISIBLE);
                }
                title.setText(bean.getTitle());
            } else {
                if (title.getVisibility() == View.VISIBLE) {
                    title.setVisibility(View.GONE);
                }
            }
            ImgLoader.displayCircle(bean.getAvatar_thumb(), headImg);
            if (TextUtils.isEmpty(bean.getThumb())) {
                ImgLoader.display(bean.getAvatar_thumb(), img, R.mipmap.bg_home_placeholder);
            } else {
                ImgLoader.display(bean.getThumb(), img, R.mipmap.bg_home_placeholder);
            }
        }
    }


}
