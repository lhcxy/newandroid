package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ChatUserBean;
import com.yunbao.phonelive.custom.ItemSlideHelper;
import com.yunbao.phonelive.custom.UserLevelView;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.IconUitl;

import java.util.List;

/**
 * Created by cxf on 2017/6/29.
 */

public class EMChatListAdapter extends RecyclerView.Adapter<EMChatListAdapter.Vh> implements ItemSlideHelper.Callback {

    private List<ChatUserBean> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private RecyclerView mRecyclerView;

    public EMChatListAdapter(Context context, List<ChatUserBean> list) {
        mContext = context;
        mList = list;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mLayoutInflater.inflate(R.layout.item_list_chat_conversation, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, final int position) {
        vh.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void setList(List<ChatUserBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addList(List<ChatUserBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public int insertItem(ChatUserBean bean) {
        mList.add(0,bean);
        int position = mList.size() - 1;
        notifyDataSetChanged();
        return position;
    }

    public ChatUserBean removeItem(String touid) {
        ChatUserBean bean = null;
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

    public void updateItem(ChatUserBean bean) {
        for (int i = 0; i < mList.size(); i++) {
            if (bean == mList.get(i)) {
                notifyItemChanged(i);
                break;
            }
        }
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
        //        ImageView sex;
//        ImageView anchorLevel;
//        ImageView level;
        TextView name;
        TextView msg;
        TextView redPoint;
        TextView time;
        View deleteBtn;
        ChatUserBean mBean;
        //        UserLevelView userLevelView;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            headImg = (ImageView) itemView.findViewById(R.id.headImg);
//            sex = (ImageView) itemView.findViewById(R.id.sex);
//            anchorLevel = (ImageView) itemView.findViewById(R.id.anchor_level);
//            level = (ImageView) itemView.findViewById(R.id.user_level);
            name = (TextView) itemView.findViewById(R.id.name);
            redPoint = (TextView) itemView.findViewById(R.id.red_point);
            msg = (TextView) itemView.findViewById(R.id.msg);
            time = (TextView) itemView.findViewById(R.id.time);
            deleteBtn = itemView.findViewById(R.id.btn_delete);
//            userLevelView = itemView.findViewById(R.id.level);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(mBean, mPosition);
                    }
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.remove(mPosition);
                    notifyItemRemoved(mPosition);
                    notifyItemRangeChanged(mPosition, mList.size());
                    if (mOnItemRemoveListener != null) {
                        mOnItemRemoveListener.onItemClick(mBean, mPosition);
                    }
                }
            });
        }

        void setData(ChatUserBean bean, int position) {
            mBean = bean;
            mPosition = position;
            ImgLoader.displayCircleWhiteBorder(bean.getAvatar(), headImg);
//            userLevelView.setLevel(bean.getLevel());
//            sex.setImageResource(IconUitl.getSexDrawable(bean.getSex()));
//            anchorLevel.setImageResource(IconUitl.getAnchorDrawable(bean.getLevel_anchor()));
//            level.setImageResource(IconUitl.getAudienceDrawable(bean.getLevel()));
            name.setText(bean.getUser_nicename());
            msg.setText(bean.getLastMessage());
            time.setText(bean.getLastTime());
            int count = bean.getUnReadCount();
            if (count > 0) {
                if (redPoint.getVisibility() == View.GONE) {
                    redPoint.setVisibility(View.VISIBLE);
                }
                redPoint.setText(count + "");
            } else {
                if (redPoint.getVisibility() == View.VISIBLE) {
                    redPoint.setVisibility(View.GONE);
                }
            }
        }
    }


    private OnItemClickListener<ChatUserBean> mListener;

    public void setOnItemClickListener(OnItemClickListener<ChatUserBean> listener) {
        mListener = listener;
    }

    private OnItemClickListener<ChatUserBean> mOnItemRemoveListener;

    public void setOnItemRemoveListener(OnItemClickListener<ChatUserBean> listener) {
        mOnItemRemoveListener = listener;
    }

}
