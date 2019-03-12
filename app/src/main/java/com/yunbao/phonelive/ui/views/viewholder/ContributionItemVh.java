package com.yunbao.phonelive.ui.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.RankItemBean;
import com.yunbao.phonelive.custom.UserLevelView;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

public class ContributionItemVh extends RecyclerView.ViewHolder {
    private ImageView rankAvatarRiv, rankIv, avatarIndexIv;
    private TextView rankName, rankIndex;
    private UserLevelView userLevelView;

    public ContributionItemVh(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_contribution_list, parent, false));
        rankAvatarRiv = itemView.findViewById(R.id.item_rank_avatar_riv);
        rankName = itemView.findViewById(R.id.item_rank_name_tv);
        rankIndex = itemView.findViewById(R.id.item_rank_index_tv);
        rankIv = itemView.findViewById(R.id.item_rank_Iv);
    }

    public ContributionItemVh(ViewGroup parent, boolean isWhite) {
        super(LayoutInflater.from(parent.getContext()).inflate(isWhite ? R.layout.item_ui_contribution_list_white : R.layout.item_ui_contribution_list, parent, false));
        rankAvatarRiv = itemView.findViewById(R.id.item_rank_avatar_riv);
        rankName = itemView.findViewById(R.id.item_rank_name_tv);
        rankIndex = itemView.findViewById(R.id.item_rank_index_tv);
        avatarIndexIv = itemView.findViewById(R.id.item_rank_avatar_index_iv);
        rankIv = itemView.findViewById(R.id.item_rank_Iv);
        userLevelView = itemView.findViewById(R.id.item_level);
    }

    public void onBindData(RankItemBean data) {
        rankIndex.setText(String.valueOf(getAdapterPosition() + 1));
        if (getAdapterPosition() == 0) {
            avatarIndexIv.setVisibility(View.VISIBLE);
            avatarIndexIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_rank_list_gold));
            rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_one));
            ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv, itemView.getResources().getColor(R.color.text_rank_list_one));
        } else if (getAdapterPosition() == 1) {
            avatarIndexIv.setVisibility(View.VISIBLE);
            avatarIndexIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_rank_list_silver));
            rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_two));
            ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv, itemView.getResources().getColor(R.color.text_rank_list_two));
        } else if (getAdapterPosition() == 2) {
            avatarIndexIv.setVisibility(View.VISIBLE);
            avatarIndexIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_rank_list_copper));
            rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_three));
            ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv, itemView.getResources().getColor(R.color.text_rank_list_three));
        } else {
            avatarIndexIv.setVisibility(View.INVISIBLE);
            rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_other));
            ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv, itemView.getResources().getColor(R.color.text_rank_list_other));
        }
        rankName.setText(data.getUser_nicename());
        userLevelView.setLevel(data.getLevel());
        if (data.getType() == 1) {
            rankIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_liver_rank_up));
        } else {
            rankIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_liver_rank_down));
        }
    }
}
