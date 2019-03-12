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

import com.hyphenate.util.DateUtils;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ChargeHistoryBean;
import com.yunbao.phonelive.utils.DateUtil;

public class ChargeHistoryVh extends RecyclerView.ViewHolder {
    private TextView titleTv, systemTv, stateTv, numTv, dateTv, idTv;
    private ForegroundColorSpan giftNameSp;

    public ChargeHistoryVh(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_charge_history, parent, false));
        titleTv = itemView.findViewById(R.id.charge_history_title_tv);
        systemTv = itemView.findViewById(R.id.charge_history_system_tv);
        stateTv = itemView.findViewById(R.id.charge_history_state_tv);
        numTv = itemView.findViewById(R.id.charge_history_nums_tv);
        dateTv = itemView.findViewById(R.id.charge_history_date_tv);
        idTv = itemView.findViewById(R.id.charge_history_id_tv);
        giftNameSp = new ForegroundColorSpan(itemView.getResources().getColor(R.color.app_selected_color));
    }

    public void bindData(ChargeHistoryBean chargeHistoryBean) {
        titleTv.setText(String.format(itemView.getResources().getString(R.string.txt_charge_history_title), chargeHistoryBean.getCoin()));
        idTv.setText(String.format(itemView.getResources().getString(R.string.txt_charge_history_trade), chargeHistoryBean.getOrderno()));
        SpannableString string = new SpannableString(String.format(itemView.getResources().getString(R.string.txt_charge_history_recharge), chargeHistoryBean.getMoney()));
        string.setSpan(giftNameSp, 5, string.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        numTv.setText(string);
        if ("1".equals(chargeHistoryBean.getAmbient())) {
            systemTv.setText("PC");
            systemTv.setTextColor(itemView.getResources().getColor(R.color.global));
            systemTv.setBackground(itemView.getResources().getDrawable(R.drawable.bg_line_global_circle_2));
        } else if ("2".equals(chargeHistoryBean.getAmbient())) {
            systemTv.setText("Android");
            systemTv.setTextColor(itemView.getResources().getColor(R.color.green));
            systemTv.setBackground(itemView.getResources().getDrawable(R.drawable.bg_line_green_circle_2));
        } else {
            systemTv.setText("IOS");
            systemTv.setTextColor(itemView.getResources().getColor(R.color.blue));
            systemTv.setBackground(itemView.getResources().getDrawable(R.drawable.bg_line_blue_circle_2));
        }
        stateTv.setText("0".equals(chargeHistoryBean.getStatus()) ? "失败" : "成功");
        stateTv.setTextColor(itemView.getResources().getColor("0".equals(chargeHistoryBean.getStatus()) ? R.color.text_color_fc3c : R.color.green));
        if (!TextUtils.isEmpty(chargeHistoryBean.getAddtime())) {
            dateTv.setText(DateUtil.getYMDHMByTimeStamp(chargeHistoryBean.getAddtime()));
        }

    }
}
