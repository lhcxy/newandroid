package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveMusicBean;
import com.yunbao.phonelive.custom.ItemSlideHelper;
import com.yunbao.phonelive.custom.ProgressTextView;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.List;

/**
 * Created by cxf on 2017/9/2.
 */

public class LiveMusicAdapter extends RecyclerView.Adapter<LiveMusicAdapter.Vh> implements ItemSlideHelper.Callback {

    private Context mContext;
    private List<LiveMusicBean> mList;
    private LayoutInflater mInflater;
    private ActionListener mActionListener;
    private RecyclerView mRecyclerView;

    public LiveMusicAdapter(Context context, List<LiveMusicBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(AppContext.sInstance);
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_live_music, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {
        vh.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(List<LiveMusicBean> list) {
        mList = list;
        notifyDataSetChanged();
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


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }


    class Vh extends RecyclerView.ViewHolder {
        TextView music;
        TextView artist;
        ProgressTextView btn;
        LiveMusicBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            music = (TextView) itemView.findViewById(R.id.music);
            artist = (TextView) itemView.findViewById(R.id.artist);
            btn = (ProgressTextView) itemView.findViewById(R.id.btn_download);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActionListener != null) {
                        mActionListener.onDownLoad(btn, mBean, mPosition);
                    }
                }
            });
            itemView.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.remove(mPosition);
                    notifyItemRemoved(mPosition);
                    notifyItemRangeChanged(mPosition, mList.size());
                    if (mActionListener != null) {
                        mActionListener.onRemove(mBean);
                    }
                }
            });
        }

        void setData(LiveMusicBean bean, int position) {
            mBean = bean;
            mPosition = position;
            music.setText(bean.getAudio_name());
            artist.setText(bean.getArtist_name());
            switch (bean.getStatus()) {
                case LiveMusicBean.PRO_NOT:
                    btn.setLoadingStatus(ProgressTextView.PRO_NOT, 0);
                    break;
                case LiveMusicBean.PRO_ING:
                    btn.setLoadingStatus(ProgressTextView.PRO_ING, bean.getProgress());
                    break;
                case LiveMusicBean.PRO_END:
                    btn.setLoadingStatus(ProgressTextView.PRO_END, 100);
                    break;
            }
        }
    }

    public interface ActionListener {
        void onDownLoad(ProgressTextView textView, LiveMusicBean bean, int position);

        void onRemove(LiveMusicBean bean);
    }
}
