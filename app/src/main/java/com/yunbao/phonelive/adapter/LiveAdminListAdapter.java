package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.ItemSlideHelper;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.IconUitl;

import java.util.List;

/**
 * Created by cxf on 2017/8/26.
 */

public class LiveAdminListAdapter extends RecyclerView.Adapter implements ItemSlideHelper.Callback {

    private List<UserBean> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private RecyclerView mRecyclerView;

    public LiveAdminListAdapter(Context context, List<UserBean> list) {
        mContext = context;
        mList = list;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public UserBean removeItem(String touid) {
        UserBean bean = null;
        for (int i = 0; i < mList.size(); i++) {
            bean = mList.get(i);
            if (touid.equals(bean.getId())) {
                mList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, mList.size());
                break;
            }
        }
        return bean;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_list_live_admin, parent, false);
        return new Vh(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Vh vh = (Vh) holder;
        final UserBean bean = mList.get(position);
        ImgLoader.displayCircle(bean.getAvatar(), vh.headImg);
        vh.sex.setImageResource(IconUitl.getSexDrawable(bean.getSex()));
        vh.anchorLevel.setImageResource(IconUitl.getAnchorDrawable(bean.getLevel_anchor()));
        vh.level.setImageResource(IconUitl.getAudienceDrawable(bean.getLevel()));
        vh.name.setText(bean.getUser_nicename());
        vh.signature.setText(bean.getSignature());
        vh.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemRemoveListener != null) {
                    mOnItemRemoveListener.onItemClick(mList.get(position), position);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.addOnItemTouchListener(new ItemSlideHelper(mContext, this));
    }

    @Override
    public int getHorizontalRange(RecyclerView.ViewHolder holder) {
        return DpUtil.dp2px(90);
    }

    @Override
    public RecyclerView.ViewHolder getChildViewHolder(View childView) {
        return mRecyclerView.getChildViewHolder(childView);
    }

    @Override
    public View findTargetView(float x, float y) {
        return mRecyclerView.findChildViewUnder(x, y);
    }


    class Vh extends RecyclerView.ViewHolder {
        ImageView headImg;
        ImageView sex;
        ImageView anchorLevel;
        ImageView level;
        TextView name;
        TextView signature;
        View deleteBtn;

        public Vh(View itemView) {
            super(itemView);
            headImg = (ImageView) itemView.findViewById(R.id.headImg);
            sex = (ImageView) itemView.findViewById(R.id.sex);
            anchorLevel = (ImageView) itemView.findViewById(R.id.anchor_level);
            level = (ImageView) itemView.findViewById(R.id.user_level);
            name = (TextView) itemView.findViewById(R.id.name);
            signature = (TextView) itemView.findViewById(R.id.signature);
            deleteBtn = itemView.findViewById(R.id.btn_delete);
        }
    }

    private OnItemClickListener<UserBean> mOnItemRemoveListener;

    public void setOnItemRemoveListener(OnItemClickListener<UserBean> listener) {
        mOnItemRemoveListener = listener;
    }

}
