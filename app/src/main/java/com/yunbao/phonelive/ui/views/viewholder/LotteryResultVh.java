package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LotteryItemBean;
import com.yunbao.phonelive.glide.ImgLoader;

public class LotteryResultVh extends RecyclerView.ViewHolder {
    private TextView content_tv;
    private ImageView iconIv;

    public LotteryResultVh(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lottery_batch_result, parent, false));
        iconIv = itemView.findViewById(R.id.imageView3);
        content_tv = itemView.findViewById(R.id.content_tv);
    }

    public void onBindData(LotteryItemBean data) {
        ImgLoader.display(data.getGifticon(), iconIv);
        content_tv.setText(data.getGiftname() + "*" + data.getNum());
    }
}
