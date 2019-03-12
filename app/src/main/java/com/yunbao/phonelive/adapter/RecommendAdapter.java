package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.RecommendBean;
import com.yunbao.phonelive.glide.ImgLoader;

import java.util.List;

/**
 * Created by cxf on 2017/10/23.
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.Vh> {

    private Context mContext;
    private List<RecommendBean> mList;
    private LayoutInflater mInflater;
    private String mResult = "";
    private static final String FLAG = "flag";

    public RecommendAdapter(Context context, List<RecommendBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    public String getCheckList() {
        for (RecommendBean bean : mList) {
            if (bean.isChecked()) {
                mResult += bean.getId() + ",";
            }
        }
        if (mResult.length() > 0) {
            mResult = mResult.substring(0, mResult.length() - 1);
        }
        return mResult;
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_recommend, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {

    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        String flag = payloads.size() > 0 ? (String) payloads.get(0) : null;
        vh.setData(mList.get(position), position, flag);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView checkBox;
        TextView name;
        TextView fans;
        RecommendBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            checkBox = (ImageView) itemView.findViewById(R.id.checkbox);
            name = (TextView) itemView.findViewById(R.id.name);
            fans = (TextView) itemView.findViewById(R.id.fans);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBean.setChecked(!mBean.isChecked());
                    notifyItemChanged(mPosition, FLAG);
                }
            });
        }

        void setData(RecommendBean bean, int position, String flag) {
            mBean = bean;
            mPosition = position;
            if (flag == null) {
                ImgLoader.displayCircle(bean.getAvatar(), img);
                name.setText(bean.getUser_nicename());
                fans.setText(bean.getFans());
                if (mBean.isChecked()) {
                    checkBox.setImageResource(R.mipmap.icon_recommend_checked);
                } else {
                    checkBox.setImageResource(R.mipmap.icon_recommend_unchecked);
                }
            } else {
                if (mBean.isChecked()) {
                    checkBox.setImageResource(R.mipmap.icon_recommend_checked);
                } else {
                    checkBox.setImageResource(R.mipmap.icon_recommend_unchecked);
                }
            }
        }
    }
}
