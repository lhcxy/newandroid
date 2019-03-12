package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.CountryCodeBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

import java.util.concurrent.TimeUnit;

public class ItemChoseCCVh extends RecyclerView.ViewHolder {
    private TextView countryEnTv, countryZhTv, countryCodeTv;

    public ItemChoseCCVh(@NonNull ViewGroup parent, OnItemClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_chose_country_code, parent, false));
        countryCodeTv = itemView.findViewById(R.id.chose_country_code_tv);
        countryZhTv = itemView.findViewById(R.id.chose_country_code_zh_tv);
        countryEnTv = itemView.findViewById(R.id.chose_country_code_en_tv);
        RxView.clicks(itemView).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (listener != null) {
                listener.onItemClick(null, getAdapterPosition());
            }
        });
    }

    public void bindData(CountryCodeBean data) {
        countryCodeTv.setText("+" + String.valueOf(data.getCode()));
        countryZhTv.setText(data.getZh());
        countryEnTv.setText(data.getEn());
    }
}
