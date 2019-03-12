package com.yunbao.phonelive.ui.views.viewholder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.custom.UserLevelView;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

/**
 * 直播聊天 item
 */
public class LiveChatDefVh extends RecyclerView.ViewHolder {
    private TextView contentTv;
    private ForegroundColorSpan nameSp, contentSp, sysMsgSp, giftNameSp;
    private ImageView giftIcon;
    private UserLevelView levelView;

    public LiveChatDefVh(@NonNull ViewGroup parent, OnItemClickListener<String> listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_chat, parent, false));
        contentTv = itemView.findViewById(R.id.live_chat_content_tv);
        giftIcon = itemView.findViewById(R.id.live_chat_icon_iv);
        levelView = itemView.findViewById(R.id.live_chat_level_ulv);

        nameSp = new ForegroundColorSpan(itemView.getResources().getColor(R.color.text_color_dd));
        contentSp = new ForegroundColorSpan(itemView.getResources().getColor(R.color.white));
        sysMsgSp = new ForegroundColorSpan(itemView.getResources().getColor(R.color.app_selected_color));
        giftNameSp = new ForegroundColorSpan(itemView.getResources().getColor(R.color.app_selected_color));
        contentTv.setOnClickListener(v -> {
            if (listener != null && data != null) {
                listener.onItemClick(data.getId(), getAdapterPosition());
            }
        });
    }

    private LiveChatBean data;

    public void bindData(LiveChatBean data) {
        //type =0是普通消息  1是系统消息 2是礼物消息
        if (data.getType() == 0) {
            if (!TextUtils.isEmpty(data.getContent())) {
                String uName = "";
                if (AppConfig.getInstance().getUserBean().getId().equals(data.getId())) {
                    uName = "我";
                } else {
                    uName = data.getUser_nicename();
                    this.data = data;
                }
                SpannableString spannableString = new SpannableString(uName + " : " + data.getContent());
                spannableString.setSpan(nameSp, 0, uName.length() + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannableString.setSpan(contentSp, uName.length() + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                contentTv.setText(spannableString);
                giftIcon.setVisibility(View.GONE);
                levelView.setVisibility(View.VISIBLE);
                levelView.setLevel(data.getLevel());
            }
        } else if (data.getType() == 1) {
            SpannableString string = new SpannableString(data.getContent());
            string.setSpan(sysMsgSp, 0, data.getContent().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            contentTv.setText(string);
            giftIcon.setVisibility(View.GONE);
            this.data = null;
        } else if (data.getType() == 2) {
            giftIcon.setVisibility(View.VISIBLE);
            SpannableString string = new SpannableString(data.getUser_nicename() + " 送给主播" + data.getGiftcount() + "个");
            string.setSpan(giftNameSp, 0, data.getUser_nicename().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            string.setSpan(sysMsgSp, string.length() - (String.valueOf(data.getGiftcount()).length() + 1), string.length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            contentTv.setText(string);
            ImgLoader.display(data.getGifticon(), giftIcon);
            levelView.setVisibility(View.VISIBLE);
            levelView.setLevel(data.getLevel());
            this.data = data;
        } else if (data.getType() == LiveChatBean.ENTER_ROOM) {
            levelView.setVisibility(View.VISIBLE);
            levelView.setLevel(data.getLevel());
            SpannableString string = new SpannableString(data.getUser_nicename() + "进入了直播间");
            string.setSpan(nameSp, 0, data.getUser_nicename().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            contentTv.setText(string);
//            contentTv.setText("data.getUser_nicename()+\"进入了直播间\"");
            giftIcon.setVisibility(View.GONE);
            this.data = null;
        }
    }
}
