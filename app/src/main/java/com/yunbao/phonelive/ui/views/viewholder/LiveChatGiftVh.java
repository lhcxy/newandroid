package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.glide.ImgLoader;

/**
 * 直播聊天 item
 */
public class LiveChatGiftVh extends RecyclerView.ViewHolder {
    private TextView contentTv, countTv;
    private ImageView giftIv;

    public LiveChatGiftVh(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_chat_gift, parent, false));
        contentTv = itemView.findViewById(R.id.live_chat_name_tv);
        giftIv = itemView.findViewById(R.id.live_chat_icon_iv);
        countTv = itemView.findViewById(R.id.live_chat_count_tv);
    }

    public void bindData(LiveChatBean data) {
        //type =0是普通消息  1是系统消息 2是礼物消息
        contentTv.setText(data.getUser_nicename() + "赠送");
        ImgLoader.display(data.getGifticon(), giftIv);
        countTv.setText(" X " + data.getGiftcount());
    }
}
