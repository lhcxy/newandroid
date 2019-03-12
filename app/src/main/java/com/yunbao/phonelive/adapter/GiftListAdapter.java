package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.GiftBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by cxf on 2017/8/19.
 */

public class GiftListAdapter extends RecyclerView.Adapter<GiftListAdapter.Vh> {

    private Context mContext;
    private List<GiftBean> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener<GiftBean> mOnItemClickListener;
    private String mCoinName, mBmh;
    private Drawable mCheckedDrawable;
    public static final String CHECKED = "checked";
    public static final String UNCHECKED = "unchecked";

    public GiftListAdapter(Context context, List<GiftBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
        mCoinName = context.getResources().getString(R.string.coin_name);
        mBmh = context.getResources().getString(R.string.coin_bmh_name);
        mCheckedDrawable = ContextCompat.getDrawable(context, R.drawable.bg_item_gift_selected);
    }

    public void setmList(List<GiftBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_gird_gift, parent, false));
    }

    public void setOnItemClickListener(OnItemClickListener<GiftBean> listener) {
        mOnItemClickListener = listener;
    }


    @Override
    public void onBindViewHolder(Vh vh, final int position) {
    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        String flag = payloads.size() > 0 ? (String) payloads.get(0) : null;
        vh.setData(mList.get(position), position, flag);
    }

    class Vh extends RecyclerView.ViewHolder {
        View warp;
        ImageView checkbox;
        ImageView lian;
        ImageView icon;
        TextView price;
        TextView name;
        GiftBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            warp = itemView.findViewById(R.id.wrap);
            checkbox = (ImageView) itemView.findViewById(R.id.checkbox);
            lian = (ImageView) itemView.findViewById(R.id.lian);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            price = (TextView) itemView.findViewById(R.id.price);
            name = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mBean, mPosition);
                }
            });
        }

        void setData(GiftBean bean, int position, String flag) {
            mBean = bean;
            mPosition = position;
            if (flag == null) {
                ImgLoader.displayNoAnimate(mBean.getGifticon(), icon);
                name.setText(mBean.getGiftname());
                if (bean.isPackageGift()) {
                    price.setText(mBean.getNum() + "个");
                } else {
                    if (bean.getCtype() == 1) {
                        price.setText(mBean.getNeedcoin() + mCoinName);
                    } else {
                        price.setText(mBean.getNeedcoin() + mBmh);
                    }
                }

//                if (bean.getType() == 1) {
//                    if (lian.getVisibility() != View.VISIBLE) {
//                        lian.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    if (lian.getVisibility() != View.INVISIBLE) {
//                        lian.setVisibility(View.INVISIBLE);
//                    }
//                }
//                if (bean.isChecked()) {
//                    checked();
//                } else {
//                    unChecked();
//                }
            } else {
//                if (CHECKED.equals(flag)) {
//                    checked();
//                } else if (UNCHECKED.equals(flag)) {
//                    unChecked();
//                }
            }
        }
    }
}
