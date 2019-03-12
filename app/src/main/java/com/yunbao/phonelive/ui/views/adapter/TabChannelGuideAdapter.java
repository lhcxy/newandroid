package com.yunbao.phonelive.ui.views.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.event.TabChannelEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.helper.TabChannelHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class TabChannelGuideAdapter extends RecyclerView.Adapter<TabChannelGuideAdapter.ItemTabChannel> {

    private ArrayList<TabBean> datas;

    public TabChannelGuideAdapter(Context mContext) {
        datas = TabChannelHelper.getInstance().getFullData(mContext);
    }

    @NonNull
    @Override
    public TabChannelGuideAdapter.ItemTabChannel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemTabChannel(parent);
    }


    @Override
    public void onBindViewHolder(@NonNull TabChannelGuideAdapter.ItemTabChannel holder, int position) {
        holder.onBindData(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }


    class ItemTabChannel extends RecyclerView.ViewHolder {
        private TextView channelTv;
        private int position = -1;
        private ImageView channelIv;

        public ItemTabChannel(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab_channel_guide_vh, parent, false));
            channelTv = itemView.findViewById(R.id.tab_channel_guide_vh_tv);
            channelIv = itemView.findViewById(R.id.tab_channel_guide_vh_iv);
            itemView.setOnClickListener(view -> {
                if (position != -1) {
                    EventBus.getDefault().post(new TabChannelEvent(position));
                }
            });

        }


        public void onBindData(TabBean data) {
            this.position = getAdapterPosition();
            channelTv.setText(data.getName());
            ImgLoader.displayTab(data.getIcon(), channelIv);
        }

    }


}
