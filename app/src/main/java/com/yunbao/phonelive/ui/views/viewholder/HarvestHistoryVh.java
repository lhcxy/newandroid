package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ChargeHistoryBean;
import com.yunbao.phonelive.bean.HarvestHistoryBean;
import com.yunbao.phonelive.utils.DateUtil;

/**
 * 提现记录item
 */
public class HarvestHistoryVh extends RecyclerView.ViewHolder {
    private TextView titleTv, stateTv, numTv, dateTv;

    public HarvestHistoryVh(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_harvest_history, parent, false));
        titleTv = itemView.findViewById(R.id.charge_history_title_tv);
        stateTv = itemView.findViewById(R.id.charge_history_state_tv);
        numTv = itemView.findViewById(R.id.charge_history_nums_tv);
        dateTv = itemView.findViewById(R.id.charge_history_date_tv);
    }

    public void bindData(HarvestHistoryBean chargeHistoryBean) {
        titleTv.setText(String.format(itemView.getResources().getString(R.string.txt_harvest_history_item), chargeHistoryBean.getVotes()));
        numTv.setText(String.format(itemView.getResources().getString(R.string.txt_harvest_history_item_money), chargeHistoryBean.getMoney() - chargeHistoryBean.getAmount()));
        if (0 == chargeHistoryBean.getStatus()) {
            stateTv.setText("审核中");
            stateTv.setTextColor(itemView.getResources().getColor(R.color.global));
        } else if (1 == chargeHistoryBean.getStatus()) {
            stateTv.setText("成功");
            stateTv.setTextColor(itemView.getResources().getColor(R.color.green));
        } else {
            stateTv.setText("失败");
            stateTv.setTextColor(itemView.getResources().getColor(R.color.text_color_fc3c));
        }
        dateTv.setText(DateUtil.getYMDHMByTimeStamp(chargeHistoryBean.getAddtime()));
    }
}
