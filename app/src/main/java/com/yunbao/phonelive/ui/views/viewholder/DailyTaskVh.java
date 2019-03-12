package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.DailyTaskBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

public class DailyTaskVh extends RecyclerView.ViewHolder {
    private TextView title, status, subtitle, subtitleCash;
    private ImageView imgIv;

    public DailyTaskVh(@NonNull ViewGroup parent, OnItemClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_daily_task, parent, false));
        title = itemView.findViewById(R.id.daily_task_title_tv);
        status = itemView.findViewById(R.id.daily_task_status_tv);
        imgIv = itemView.findViewById(R.id.daily_task_img_iv);
        subtitle = itemView.findViewById(R.id.subtitle_tv);
        subtitleCash = itemView.findViewById(R.id.subtitle_cash_tv);
        status.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(null, getAdapterPosition());
            }
        });
    }

    public void bindData(DailyTaskBean data) {

        String titleStr = data.getTitle();
        subtitle.setText(String.format(itemView.getResources().getString(R.string.txt_daily_task), data.getCoin()));
        ImgLoader.display(data.getIcon(), imgIv);
        if ("6".equals(data.getTask_type())) {
            this.title.setText(data.getTitle());
            subtitleCash.setVisibility(View.INVISIBLE);
        } else {
            subtitleCash.setVisibility(View.VISIBLE);
            subtitleCash.setText(String.format(itemView.getResources().getString(R.string.txt_daily_cash_task), data.getCash()));
            if (data.getBalance() > 0) {
                titleStr += "   (" + data.getOknum() + "/" + data.getBalance() + ")";
            }
        }
        title.setText(titleStr);
        if (data.getStatus() == 0) {//待领取
            if (data.getCondition() == 0) {
//                if (data.getId().equals("1")) {
                status.setText("去完成");
//                } else if (data.getId().equals("2")) {
//                    status.setText("签到");
//                } else {
//                    status.setText("登陆");
//                }
            } else {
                status.setText("领取");
            }
            status.setBackground(itemView.getResources().getDrawable(R.drawable.bg_circle_line_00bb56));
            status.setTextColor(itemView.getResources().getColor(R.color.txt_color_0b65));
        } else {
            status.setText("已完成");
            status.setBackground(itemView.getResources().getDrawable(R.drawable.bg_circle_line_cc));
            status.setTextColor(itemView.getResources().getColor(R.color.text_color_cc));
        }
    }
}
