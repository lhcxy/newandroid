package com.yunbao.phonelive.ui.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

import java.util.concurrent.TimeUnit;

public class ItemSearchVh extends RecyclerView.ViewHolder {
    private ImageView itemCoverIv;
    private TextView itemRNameTv, itemUNameTv, itemWNumTv, isLiveTv;
    private OnItemClickListener listener;
    private LiveBean data;
    private int vIndex = -1;

    public ItemSearchVh(ViewGroup parent, OnItemClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_list_home_live, parent, false));
        itemCoverIv = itemView.findViewById(R.id.item_live_cover_iv);
        itemRNameTv = itemView.findViewById(R.id.item_live_rname_tv);
        itemUNameTv = itemView.findViewById(R.id.item_live_uname_tv);
        itemWNumTv = itemView.findViewById(R.id.item_live_wnum_tv);
        isLiveTv = itemView.findViewById(R.id.is_live_tv);
        RxView.clicks(itemView).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (listener != null && vIndex != -1) {
                        listener.onItemClick(data, vIndex);
                    }
                });
    }

    public void onBindData(LiveBean data) {
        this.data = data;
        vIndex = getAdapterPosition();
        if (TextUtils.isEmpty(data.getThumb())) {
            ImgLoader.display(data.getAvatar_thumb(), itemCoverIv, R.mipmap.bg_home_placeholder);
        } else {
            ImgLoader.display(data.getThumb(), itemCoverIv, R.mipmap.bg_home_placeholder);
        }
        if (TextUtils.isEmpty(data.getTitle())) {
            itemRNameTv.setText(data.getUser_nicename());
        } else
            itemRNameTv.setText(data.getTitle());
        itemUNameTv.setText(data.getUser_nicename());
        itemWNumTv.setText(data.getFireNums());
        if (data.getIslive() == 1) {
            isLiveTv.setVisibility(View.VISIBLE);
        } else isLiveTv.setVisibility(View.GONE);
    }


}
