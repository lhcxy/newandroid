package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.IconUitl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cxf on 2017/8/27.
 * 直播间观众列表
 */

public class LiveUserAdapter extends RecyclerView.Adapter<LiveUserAdapter.Vh> {
    private Context mContext;
    private List<UserBean> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener<UserBean> mListener;
    private UserComparator mComparator;

    public LiveUserAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(mContext);
        mComparator = new UserComparator();
    }


    public void refreshList(List<UserBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public void addUserList(List<UserBean> list) {
        int position = mList.size();
        mList.addAll(list);
        notifyItemRangeInserted(position, list.size());
        notifyItemRangeChanged(position, list.size());
    }


    public void insertItem(UserBean bean) {
        mList.add(bean);
        Collections.sort(mList, mComparator);
        notifyDataSetChanged();
    }

    public void insertList(List<UserBean> list) {
        mList.addAll(list);
        Collections.sort(mList, mComparator);
        notifyDataSetChanged();
    }

    public void removeItem(String touid) {
        for (int i = 0; i < mList.size(); i++) {
            UserBean bean = mList.get(i);
            if (touid.equals(bean.getId())) {
                mList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, mList.size());
                break;
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener<UserBean> listener) {
        mListener = listener;
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_live_user, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, final int position) {
        vh.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public boolean hasUser(String uid){
        for(UserBean u:mList){
            if(uid.equals(u.getId())){
                return true;
            }
        }
        return false;
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView headImg;
        ImageView level;
        UserBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            headImg = (ImageView) itemView.findViewById(R.id.headImg);
            level = (ImageView) itemView.findViewById(R.id.level);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(mBean, mPosition);
                    }
                }
            });
        }

        void setData(UserBean bean, int position) {
            mBean = bean;
            mPosition = position;
            ImgLoader.display(bean.getAvatar(), headImg);
            level.setImageResource(IconUitl.getAudienceLiveDrawable(bean.getLevel()));
        }
    }


    //按等级降序进行排序
    private static class UserComparator implements Comparator<UserBean> {
        @Override
        public int compare(UserBean u1, UserBean u2) {
            return u2.getLevel() - u1.getLevel();
        }
    }
}
