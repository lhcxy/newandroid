package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.TextRender;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxf on 2017/8/22.
 */

public class LiveChatListAdapter extends RecyclerView.Adapter<LiveChatListAdapter.Vh> {

    private Context mContext;
    private List<LiveChatBean> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener<LiveChatBean> mOnItemClickListener;
    private RecyclerView mRecyclerView;
    //private Typeface mTypeface;

    public LiveChatListAdapter(Context context) {
        mContext = context;
        mList = new LinkedList<>();
        mInflater = LayoutInflater.from(mContext);
        //mTypeface= FontUtil.getInstance().getTypeface();
    }

    public void setOnItemClickListener(OnItemClickListener<LiveChatBean> listener) {
        mOnItemClickListener = listener;
    }

    public void clear(){
        mList.clear();
        notifyDataSetChanged();
    }

    public void insertItem(LiveChatBean bean) {
        mList.add(bean);
        if (mList.size() > 30) {
            mList = mList.subList(10, mList.size());
            notifyItemRangeRemoved(0, 10);
            notifyItemRangeChanged(0, mList.size());
        } else {
            int position = mList.size() - 1;
            notifyItemInserted(position);
            notifyItemRangeChanged(position, mList.size());
        }
        mRecyclerView.smoothScrollToPosition(mList.size() - 1);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_live_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, final int position) {
        vh.setData(mList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView tv;
        LiveChatBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
//            tv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mOnItemClickListener != null) {
//                        mOnItemClickListener.onItemClick(mBean, mPosition);
//                    }
//                }
//            });
        }

        void setData(LiveChatBean bean,int position){
            mBean=bean;
            mPosition=position;
            //tv.setTypeface(mTypeface);
            tv.setText(TextRender.createChat(bean));
        }
    }
}
