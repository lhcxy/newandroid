package com.yunbao.phonelive.ui.views.viewholder;

import android.support.v7.widget.RecyclerView;
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

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainLiverVh extends RecyclerView.ViewHolder {
    private ImageView avatarIv;

    private TextView nameTv;
    private LiveBean data;
    private int position = -1;

    public MainLiverVh(ViewGroup parent, OnItemClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_main_liver, parent, false));
        avatarIv = itemView.findViewById(R.id.main_liver_avatar_iv);
        nameTv = itemView.findViewById(R.id.main_liver_name_tv);
        RxView.clicks(itemView).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (listener != null && position >= 0) {
                        listener.onItemClick(this.data, this.position);
                    }
                });
    }

    public void onBindData(LiveBean data) {
        position = getAdapterPosition();
        this.data = data;
        if (data != null) {
            ImgLoader.displayCircleWhiteBorder(data.getAvatar(), avatarIv);
            nameTv.setText(data.getUser_nicename());
        }

    }

}
