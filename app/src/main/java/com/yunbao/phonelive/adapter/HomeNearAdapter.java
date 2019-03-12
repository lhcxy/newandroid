package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.IconUitl;

import java.util.List;


/**
 * Created by cxf on 2017/8/9.
 */
public class HomeNearAdapter extends RecyclerView.Adapter<HomeNearAdapter.Vh> {

    private Context mContext;
    private List<LiveBean> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener<LiveBean> mOnItemClickListener;

    public HomeNearAdapter(Context context, List<LiveBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setOnItemClickListener(OnItemClickListener<LiveBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setData(List<LiveBean> itemList) {
        mList = itemList;
        notifyDataSetChanged();
    }

    public void clearData() {
        mList.clear();
        notifyDataSetChanged();
    }


    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_home_near, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {
        vh.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        LiveBean mBean;
        int mPosition;
        ImageView img;
//        ImageView type;
        TextView name;
//        TextView distance;
        TextView city;

        public Vh(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
//            type = (ImageView) itemView.findViewById(R.id.live_type);
            name = (TextView) itemView.findViewById(R.id.name);
//            distance = (TextView) itemView.findViewById(R.id.distance);
            city = (TextView) itemView.findViewById(R.id.city);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mBean, mPosition);
                    }
                }
            });
        }

        void setData(LiveBean bean, int position) {
            mBean = bean;
            mPosition = position;
            ImgLoader.display(bean.getThumb(), img, R.mipmap.bg_home_placeholder);
            name.setText(bean.getUser_nicename());
//            type.setImageResource(IconUitl.getLiveTypeDrawable(bean.getType()));
            city.setText(bean.getCity());
//            distance.setText(bean.getDistance());
        }
    }


}
