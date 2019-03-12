package com.yunbao.phonelive.ui.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.youth.banner.loader.ImageLoader;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * 标签定制  viewholder
 */

public class TabVh extends RecyclerView.ViewHolder {
    private TextView channelTv;
    private ImageView channnelIv;
    private TabBean data;
    private long lastTime = 0;
    private int resultCode = 0x012;

    public TabVh(ViewGroup parent, final OnItemClickListener itemClickListener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab_channel_guide_vh, parent, false));
        channelTv = itemView.findViewById(R.id.tab_channel_guide_vh_tv);
        channnelIv = itemView.findViewById(R.id.tab_channel_guide_vh_iv);
        RxView.clicks(itemView).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (null != itemClickListener) {
                        itemClickListener.onItemClick(data, getAdapterPosition());
                    }
                });


    }

    public void onBindData(TabBean data) {
        this.data = data;
        channelTv.setText(data.getName());
        ImgLoader.displayTab(data.getIcon(), channnelIv);
    }

}
