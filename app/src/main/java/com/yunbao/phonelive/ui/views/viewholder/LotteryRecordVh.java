package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LotteryGetInfoBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.utils.DateUtil;

public class LotteryRecordVh extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView titleTv, dateTv, numTv;

    public LotteryRecordVh(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lottery_record, parent, false));
        imageView = itemView.findViewById(R.id.imageView);
        titleTv = itemView.findViewById(R.id.lottery_title_tv);
        numTv = itemView.findViewById(R.id.lottery_num_tv);
        dateTv = itemView.findViewById(R.id.lottery_date_tv);

    }

    public void onBindData(LotteryGetInfoBean data) {
        titleTv.setText(data.getUser_nicename());
        ImgLoader.display(data.getGifticon(), imageView);
        numTv.setText("x" + data.getNum());
        dateTv.setText(DateUtil.getYMDHMByTimeStamp(data.getCreate_time()));
    }
}
