package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.MsgCenterListBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.DateUtil;

public class MsgCenterListVh extends RecyclerView.ViewHolder {
    private TextView titleTv, contentTv, timeTv, unReadTv;

    public MsgCenterListVh(@NonNull ViewGroup parent, OnItemClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_center_list, parent, false));

        titleTv = itemView.findViewById(R.id.item_title_tv);
        contentTv = itemView.findViewById(R.id.item_content_tv);
        timeTv = itemView.findViewById(R.id.item_time_tv);
        unReadTv = itemView.findViewById(R.id.item_unread_tv);
        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(null, getAdapterPosition());
            }
        });
    }

    public void onBindData(MsgCenterListBean data) {
        if (data != null) {
            if (getAdapterPosition() == 0) {
                if (!TextUtils.isEmpty(data.getStime()) && !data.getStime().equals("0")) {
                    timeTv.setText(DateUtil.getYMDHMByTimeStamp(data.getStime()));
                }
                contentTv.setText(data.getScont());
                titleTv.setText("系统消息");
                if (data.getSnum() <= 0) {
                    unReadTv.setVisibility(View.INVISIBLE);
                } else {
                    unReadTv.setVisibility(View.VISIBLE);
                    unReadTv.setText(String.valueOf(data.getSnum()));
                }
            } else {
                if (!TextUtils.isEmpty(data.getUtime()) && !data.getUtime().equals("0")) {
                    timeTv.setText(DateUtil.getYMDHMByTimeStamp(data.getUtime()));
                }
                contentTv.setText(data.getUcont());
                titleTv.setText("个人消息");
                if (data.getUnum() <= 0) {
                    unReadTv.setVisibility(View.INVISIBLE);
                } else {
                    unReadTv.setVisibility(View.VISIBLE);
                    unReadTv.setText(String.valueOf(data.getUnum()));
                }
            }
        }
    }
}
