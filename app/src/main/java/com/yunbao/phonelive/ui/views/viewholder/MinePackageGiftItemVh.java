package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.GiftBean;
import com.yunbao.phonelive.glide.ImgLoader;

public class MinePackageGiftItemVh extends RecyclerView.ViewHolder {
    private ImageView giftIv;
    private TextView giftCountTv, giftNameTv;

    public MinePackageGiftItemVh(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_mine_package_gift, parent, false));
        giftIv = itemView.findViewById(R.id.mine_gift_icon);
        giftCountTv = itemView.findViewById(R.id.mine_gift_count);
        giftNameTv = itemView.findViewById(R.id.mine_gift_name);
    }

    public void bindData(GiftBean data) {
        ImgLoader.display(data.getGifticon(), giftIv);
        giftCountTv.setText(data.getNum() + "个");
        giftNameTv.setText(data.getGiftname());
    }

}
