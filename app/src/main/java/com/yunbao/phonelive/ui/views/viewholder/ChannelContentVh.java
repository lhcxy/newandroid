package com.yunbao.phonelive.ui.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

import io.reactivex.functions.Consumer;

public class ChannelContentVh extends RecyclerView.ViewHolder {
    private TextView uNameTv, cTitleTv, wNumTv;
    private ImageView channerCoverIv;
    private int vIndex = -1;

    public ChannelContentVh(ViewGroup parent, OnItemClickListener<LiveBean> listener) {
//        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_live_cover_vh, parent, false));
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_list_home_live, parent, false));
        channerCoverIv = itemView.findViewById(R.id.item_live_cover_iv);
        cTitleTv = itemView.findViewById(R.id.item_live_rname_tv);
        uNameTv = itemView.findViewById(R.id.item_live_uname_tv);
        wNumTv = itemView.findViewById(R.id.item_live_wnum_tv);
        RxView.clicks(itemView).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (listener != null && vIndex != -1) {
                        listener.onItemClick(data, vIndex);
                    }
                });

    }

    private LiveBean data;

    public void onBindData(LiveBean data) {
//        vIndex = getAdapterPosition();
//        this.data = data;
//        wNumTv.setText(data.getFireNums());
//        uNameTv.setText(data.getUser_nicename());
//        if (TextUtils.isEmpty(data.getTitle())) {
//            cTitleTv.setText(data.getUser_nicename());
//        } else
//            cTitleTv.setText(data.getTitle());
//        ImgLoader.displayCenterCrop(data.getAvatar_thumb(), channerCoverIv);

        this.data = data;
        vIndex = getAdapterPosition();
        if (TextUtils.isEmpty(data.getThumb())) {
            if (data.getAnyway().equals("1")) {
                ImgLoader.displayRadius6(data.getAvatar_thumb(), channerCoverIv);
            } else {
                ImgLoader.display(data.getAvatar_thumb(), channerCoverIv);
            }
        } else {
            if (data.getAnyway().equals("1")) {
                ImgLoader.displayRadius6(data.getThumb(), channerCoverIv);
            } else {
                ImgLoader.display(data.getThumb(), channerCoverIv);
            }
        }
        if (TextUtils.isEmpty(data.getTitle())) {
            cTitleTv.setText(data.getUser_nicename());
        } else
            cTitleTv.setText(data.getTitle());
        uNameTv.setText(data.getUser_nicename());
        wNumTv.setText(data.getFireNums());
    }

}
