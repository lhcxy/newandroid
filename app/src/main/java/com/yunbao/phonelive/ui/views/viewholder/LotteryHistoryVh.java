package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LotteryGetInfoBean;
import com.yunbao.phonelive.utils.DateUtil;

public class LotteryHistoryVh extends RecyclerView.ViewHolder {
    private ImageView iconIv;
    private TextView titleTv, contentTv, dateTv;
    private String contentStr;

    public LotteryHistoryVh(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lottery_history, parent, false));
        iconIv = itemView.findViewById(R.id.imageView2);
        titleTv = itemView.findViewById(R.id.textView3);
        contentTv = itemView.findViewById(R.id.textView4);
        dateTv = itemView.findViewById(R.id.history_date_tv);
        contentStr = itemView.getResources().getString(R.string.lottery_history_content_txt);
    }

    public void onBindData(LotteryGetInfoBean data) {
        if (data != null) {
            if (data.getProp_id() == 2) {
                iconIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_lottery_key_silver));
                titleTv.setText("白银钥匙");
            } else if (data.getProp_id() == 3) {
                iconIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_lottery_key_gold));
                titleTv.setText("黄金钥匙");
            } else {
                iconIv.setImageDrawable(itemView.getResources().getDrawable(R.mipmap.ic_lottery_key_cuprum));
                titleTv.setText("青铜钥匙");
            }
            contentTv.setText(String.format(contentStr, data.getNum(), data.getGiftname()));
            dateTv.setText(DateUtil.getYMDHMByTimeStamp(data.getCreate_time()));
        }
    }
}
