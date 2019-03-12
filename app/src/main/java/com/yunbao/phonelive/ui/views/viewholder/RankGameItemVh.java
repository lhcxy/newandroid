package com.yunbao.phonelive.ui.views.viewholder;

import android.content.Intent;
import android.icu.util.ULocale;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
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
import com.yunbao.phonelive.custom.UserLevelView;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.tools.StringUtil;
import com.yunbao.phonelive.ui.views.LiveWatcherActivity;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

public class RankGameItemVh extends RecyclerView.ViewHolder {
    private ImageView rankAvatarRiv, avatarIndexIv;
    private TextView rankName, rankDesc, rankIndex, rankPrize;
    private ForegroundColorSpan giftNameSp;
    private LiveBean liveBean;
    private UserLevelView levelView;

    public RankGameItemVh(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_rank_game_list, parent, false));
        rankAvatarRiv = itemView.findViewById(R.id.item_rank_avatar_riv);
        rankName = itemView.findViewById(R.id.item_rank_name_tv);
        rankIndex = itemView.findViewById(R.id.item_rank_index_tv);
        rankDesc = itemView.findViewById(R.id.item_rank_desc_tv);
        rankPrize = itemView.findViewById(R.id.item_rank_prize_tv);
        levelView = itemView.findViewById(R.id.item_rank_level);
        avatarIndexIv = itemView.findViewById(R.id.item_rank_avatar_index_iv);
        rankPrize.setVisibility(View.GONE);
        giftNameSp = new ForegroundColorSpan(itemView.getResources().getColor(R.color.global));
//        RxView.clicks(itemView).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
//                .subscribe(o -> {
//                    if (liveBean == null || liveBean.getIslive() == 0) {
//                        ToastUtil.show("主播暂未开始直播!");
//                    } else {
//                        Intent intent;
//                        if ("0".equals(liveBean.getAnyway())) {
//                            intent = new Intent(itemView.getContext(), LiveAudienceActivity.class);
//                        } else {
//                            intent = new Intent(itemView.getContext(), LiveWatcherActivity.class);
//                        }
//                        intent.putExtra("liveBean", liveBean);
//                        itemView.getContext().startActivity(intent);
//                    }
//                });
    }

    public void onBindData(RankItemBean data, int type) {
        liveBean = data.getLiveInfo();
        rankPrize.setVisibility(View.GONE);
        rankIndex.setText(String.valueOf(getAdapterPosition() + 4));
        rankName.setText(data.getUser_nicename());
        levelView.setLevel(data.getLevel());
//        if (getAdapterPosition() == 0) {
//            avatarIndexIv.setVisibility(View.VISIBLE);
//            avatarIndexIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_rank_list_gold));
//            rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_one));
//            ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv, itemView.getResources().getColor(R.color.text_rank_list_one));
//        } else if (getAdapterPosition() == 1) {
//            avatarIndexIv.setVisibility(View.VISIBLE);
//            avatarIndexIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_rank_list_silver));
//            rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_two));
//            ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv, itemView.getResources().getColor(R.color.text_rank_list_two));
//        } else if (getAdapterPosition() == 2) {
//            avatarIndexIv.setVisibility(View.VISIBLE);
//            avatarIndexIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_rank_list_copper));
//            rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_three));
//            ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv, itemView.getResources().getColor(R.color.text_rank_list_three));
//        } else {
        avatarIndexIv.setVisibility(View.INVISIBLE);
        rankIndex.setTextColor(itemView.getResources().getColor(R.color.text_rank_list_other));
        ImgLoader.displayCircleBorder(data.getAvatar_thumb(), rankAvatarRiv, itemView.getResources().getColor(R.color.text_rank_list_other));
//        }
//        if (type == 3) {
//            SpannableString string = new SpannableString("获得探球币" + data.getTotal() + "个");
//            string.setSpan(giftNameSp, 5, string.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//            rankDesc.setText(string);
//        } else {
//            if (data.getListTimeType() == 0) {
//                if (data.getJiangli() > 0) {
//                    rankPrize.setVisibility(View.VISIBLE);
//                    rankPrize.setText("奖励人民币" + data.getJiangli() + "元");
//                }
//            } else {
//                if (data.getJiangli() > 0) {
//                    rankPrize.setVisibility(View.VISIBLE);
//                    rankPrize.setText("奖励探球币" + data.getJiangli() + "个");
//                }
//            }
        rankDesc.setText(StringUtil.getContributionStr(data.getTotal()) + " 爆米花");
//        }
    }
}
