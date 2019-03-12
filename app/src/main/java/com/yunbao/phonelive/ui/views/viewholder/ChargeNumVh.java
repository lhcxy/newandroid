package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ChargeTakeBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

public class ChargeNumVh extends RecyclerView.ViewHolder {
    private TextView numTv, moneyTv;
    private ImageView etCheckIv, hotIv;
    private RelativeLayout rootRl;

    public ChargeNumVh(@NonNull ViewGroup parent, OnItemClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_charge_num, parent, false));
        numTv = itemView.findViewById(R.id.item_charge_num_tv);
        rootRl = itemView.findViewById(R.id.root_rl);
        moneyTv = itemView.findViewById(R.id.item_charge_money_tv);
        hotIv = itemView.findViewById(R.id.item_charge_num_hot_iv);
        etCheckIv = itemView.findViewById(R.id.item_charge_num_check_iv);
        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(null, getAdapterPosition());
            }
        });
    }

    public void onBindData(ChargeTakeBean money, int selectedIndex) {
        numTv.setText(money.getCoin() + "探球币");
        moneyTv.setText("¥" + money.getMoney() + "元");
        if (money.isHot()) {
            hotIv.setVisibility(View.VISIBLE);
        } else hotIv.setVisibility(View.INVISIBLE);
        if (selectedIndex == getAdapterPosition()) {
            rootRl.setBackground(itemView.getResources().getDrawable(R.drawable.bg_line_square_0b56_3));
            etCheckIv.setVisibility(View.VISIBLE);
        } else {
            etCheckIv.setVisibility(View.INVISIBLE);
            rootRl.setBackground(itemView.getResources().getDrawable(R.drawable.bg_line_square_cc_3));
        }
    }
}
