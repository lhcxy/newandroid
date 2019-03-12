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

import java.util.List;


/**
 * Created by cxf on 2017/8/9.
 */
public class HomeAttentionAdapter extends RecyclerView.Adapter<HomeAttentionAdapter.Vh> {

    private Context mContext;
    private List<LiveBean> mItemList;
    private LayoutInflater mInflater;
    private OnItemClickListener<LiveBean> mOnItemClickListener;

    public HomeAttentionAdapter(Context context, List<LiveBean> itemList) {
        mContext = context;
        mItemList = itemList;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setOnItemClickListener(OnItemClickListener<LiveBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setData(List<LiveBean> itemList) {
        mItemList = itemList;
        notifyDataSetChanged();
    }

    public void clearData() {
        mItemList.clear();
        notifyDataSetChanged();
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_home_hot, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, final int position) {
        vh.setData(mItemList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        LiveBean mBean;
        int mPosition;
        ImageView headImg;
        ImageView img;
        TextView name;
//        TextView city;
//        TextView nums;
        TextView title;
//        ImageView type;
//        ImageView game;
//        ImageView anchorLevel;//主播等级

        public Vh(View itemView) {
            super(itemView);
            headImg = (ImageView) itemView.findViewById(R.id.headImg);
            img = (ImageView) itemView.findViewById(R.id.img);
//            nums = (TextView) itemView.findViewById(R.id.nums);
            name = (TextView) itemView.findViewById(R.id.name);
//            city = (TextView) itemView.findViewById(R.id.city);
            title = (TextView) itemView.findViewById(R.id.title);
//            type = (ImageView) itemView.findViewById(R.id.live_type);
//            game = (ImageView) itemView.findViewById(R.id.game);
//            anchorLevel = (ImageView) itemView.findViewById(R.id.anchor_level);
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
            name.setText(bean.getUser_nicename());
//            city.setText(bean.getCity());
//            nums.setText(bean.getNums());
//            anchorLevel.setImageResource(IconUitl.getAnchorLiveDrawable(bean.getLevel_anchor()));
            if (!"".equals(bean.getTitle())) {
                if (title.getVisibility() == View.GONE) {
                    title.setVisibility(View.VISIBLE);
                    title.setText(bean.getTitle());
                }
            } else {
                if (title.getVisibility() == View.VISIBLE) {
                    title.setVisibility(View.GONE);
                }
            }
            ImgLoader.displayCircle(bean.getAvatar_thumb(), headImg);
            ImgLoader.display(bean.getThumb(), img, R.mipmap.bg_home_placeholder);
//            type.setImageResource(IconUitl.getLiveTypeDrawable(bean.getType()));
//            if (bean.getGame_action() == 0) {
//                game.setImageDrawable(null);
//            } else {
//                game.setImageResource(GameIconUitl.getLiveGame(bean.getGame_action()));
//            }
        }
    }


}
