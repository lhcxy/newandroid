package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

public class CerDescTypeVh extends RecyclerView.ViewHolder {
    private TextView typeTv;

    public CerDescTypeVh(@NonNull ViewGroup parent, OnItemClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cer_dest_type, parent, false));
        typeTv = itemView.findViewById(R.id.cer_desc_type_tv);
        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(null, getAdapterPosition());
            }
        });

    }

    public void onBindData(TabBean data, int selectedIndex) {
        typeTv.setText(data.getName());
        if (selectedIndex == getAdapterPosition()) {
            typeTv.setTextColor(itemView.getResources().getColor(R.color.app_selected_color));
            typeTv.setBackground(itemView.getResources().getDrawable(R.drawable.bg_white_circle_line_ff3871));
        } else {
            typeTv.setTextColor(itemView.getResources().getColor(R.color.text_color_4c));
            typeTv.setBackground(itemView.getResources().getDrawable(R.drawable.bg_white_circle));
        }
    }
}
