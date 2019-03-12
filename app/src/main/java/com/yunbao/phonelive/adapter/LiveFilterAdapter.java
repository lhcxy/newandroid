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

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveFilterBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by cxf on 2017/9/1.
 */

public class LiveFilterAdapter extends RecyclerView.Adapter<LiveFilterAdapter.Vh> {

    private List<LiveFilterBean> mList;
    private LayoutInflater mInflater;
    private Drawable mCheckedDrawable;
    private OnItemClickListener<LiveFilterBean> mListener;
    private int mSelectedPosition;

    public LiveFilterAdapter(Context context, List<LiveFilterBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(AppContext.sInstance);
        mCheckedDrawable = ContextCompat.getDrawable(context, R.drawable.bg_live_filter_checked);
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_live_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, final int position) {
        final LiveFilterBean bean = mList.get(position);
        vh.img.setImageResource(bean.getImg());
        vh.text.setText(bean.getText());
        if (!vh.bindClickListener) {
            vh.bindClickListener = true;
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedPosition == position) {
                        return;
                    }
                    mSelectedPosition = position;
                    for (int i = 0; i < mList.size(); i++) {
                        if (i == mSelectedPosition) {
                            mList.get(i).setChecked(true);
                        } else {
                            mList.get(i).setChecked(false);
                        }
                    }
                    notifyDataSetChanged();
                    if (mListener != null) {
                        mListener.onItemClick(bean, position);
                    }
                }
            });
        }
        if (mSelectedPosition == position) {
            vh.img.setBackground(mCheckedDrawable);
        } else {
            vh.img.setBackground(null);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnItemClickListener(OnItemClickListener<LiveFilterBean> listener) {
        mListener = listener;
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text;
        boolean bindClickListener;

        public Vh(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
