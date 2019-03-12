package com.yunbao.phonelive.ui.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.RankItemBean;
import com.yunbao.phonelive.custom.UserLevelView;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.ui.tools.StringUtil;

/**
 * 富豪实力榜
 */
public class RankRichItemVh extends RecyclerView.ViewHolder {
    private ImageView rankAvatarRiv, avatarIndexIv;
    private TextView rankName, rankIndex, awardTv;
    private LiveBean liveBean;
    private UserLevelView userLevelView;

    public RankRichItemVh(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_rank_rich_list, parent, false));
        rankAvatarRiv = itemView.findViewById(R.id.item_rank_avatar_riv);
        rankName = itemView.findViewById(R.id.item_rank_name_tv);
        rankIndex = itemView.findViewById(R.id.item_rank_index_tv);
        awardTv = itemView.findViewById(R.id.item_rank_attention_tv);
        userLevelView = itemView.findViewById(R.id.item_rank_level);
        avatarIndexIv = itemView.findViewById(R.id.item_rank_avatar_index_iv);
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

    public void onBindData(RankItemBean data) {
        liveBean = data.getLiveInfo();
        rankIndex.setText(String.valueOf(getAdapterPosition() + 4));
        userLevelView.setLevel(data.getLevel());
        awardTv.setText(StringUtil.getContributionStr(data.getTotal()) + " 贡献");
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
//        ImgLoader.displayCircleWhiteBorder(data.getAvatar_thumb(), rankAvatarRiv);
        rankName.setText(data.getUser_nicename());
    }
}
