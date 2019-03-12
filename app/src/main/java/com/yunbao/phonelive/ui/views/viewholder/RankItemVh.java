package com.yunbao.phonelive.ui.views.viewholder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.RankItemBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.LiveWatcherActivity;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

public class RankItemVh extends RecyclerView.ViewHolder {
    private final OnItemClickListener listener;
    private ImageView rankAvatarRiv, avatarIndexIv, livingIv;
    private TextView rankName, rankDesc, rankAttetion, rankIndex;
    private int position = -1;
    private String uid = "";
    private LiveBean liveBean;

    public RankItemVh(ViewGroup parent, final OnItemClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_rank_list, parent, false));
        this.listener = listener;
        rankAvatarRiv = itemView.findViewById(R.id.item_rank_avatar_riv);
        rankName = itemView.findViewById(R.id.item_rank_name_tv);
        rankIndex = itemView.findViewById(R.id.item_rank_index_tv);
        rankDesc = itemView.findViewById(R.id.item_rank_desc_tv);
        rankAttetion = itemView.findViewById(R.id.item_rank_attention_tv);
        livingIv = itemView.findViewById(R.id.item_rank_living_iv);
        avatarIndexIv = itemView.findViewById(R.id.item_rank_avatar_index_iv);
        RxView.clicks(rankAttetion).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    HttpUtil.setAttention(uid, null);
                });
        RxView.clicks(itemView).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (liveBean == null || liveBean.getIslive() == 0) {
                        ToastUtil.show("啊哦，主播当前不在家呢！");
                    } else {
                        Intent intent;
                        if ("0".equals(liveBean.getAnyway())) {
                            intent = new Intent(itemView.getContext(), LiveAudienceActivity.class);
                        } else {
                            intent = new Intent(itemView.getContext(), LiveWatcherActivity.class);
                        }
                        intent.putExtra("liveBean", liveBean);
                        itemView.getContext().startActivity(intent);
                    }
                });
    }

    public void onBindData(RankItemBean data, int type) {
        liveBean = data.getLiveInfo();
        rankIndex.setText(String.valueOf(getAdapterPosition() + 4));
        rankName.setText(data.getUser_nicename());
//        if (getAdapterPosition() == 0) {
//            avatarIndexIv.setVisibility(View.VISIBLE);
//            avatarIndexIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_rank_list_gold));
//            rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_one));
//            ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv,itemView.getResources().getColor(R.color.text_rank_list_one));
//        } else if (getAdapterPosition() == 1) {
//            avatarIndexIv.setVisibility(View.VISIBLE);
//            avatarIndexIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_rank_list_silver));
//            rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_two));
//            ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv,itemView.getResources().getColor(R.color.text_rank_list_two));
//        } else if (getAdapterPosition() == 2) {
//            avatarIndexIv.setVisibility(View.VISIBLE);
//            avatarIndexIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_rank_list_copper));
//            rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_three));
//            ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv,itemView.getResources().getColor(R.color.text_rank_list_three));
//        } else {
        avatarIndexIv.setVisibility(View.INVISIBLE);
        rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_other));
        ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv, itemView.getResources().getColor(R.color.text_rank_list_other));
//        }
        if (data.getLiveInfo() == null) {
            livingIv.setVisibility(View.GONE);
        }else {
            if (data.getLiveInfo().getIslive() == 0) {
                livingIv.setVisibility(View.GONE);
            } else {
                livingIv.setVisibility(View.VISIBLE);
            }
            rankDesc.setText(data.getLiveInfo() != null ? TextUtils.isEmpty(data.getLiveInfo().getLivetag()) ? "" : data.getLiveInfo().getLivetag() : "");

            if (data.getLiveInfo() == null || data.getLiveInfo().getIsAttention() == 0) {
                rankAttetion.setText(itemView.getResources().getString(R.string.attention3));
                rankAttetion.setBackground(itemView.getResources().getDrawable(R.drawable.bg_ui_item_unattention));
                rankAttetion.setTextColor(itemView.getResources().getColor(R.color.app_selected_color));
            } else {
                rankAttetion.setText(itemView.getResources().getString(R.string.attention));
                rankAttetion.setBackground(itemView.getResources().getDrawable(R.drawable.bg_circle_line_cc));
                rankAttetion.setTextColor(itemView.getResources().getColor(R.color.text_color_cc));
            }
        }

        position = getAdapterPosition();
        uid = data.getUid();
    }
}
