package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

public class ItemMsgSearchVh extends RecyclerView.ViewHolder {
    private OnItemClickListener<Integer> listener;
    private ImageView avatar;
    private TextView nameTv;

    public ItemMsgSearchVh(@NonNull ViewGroup parent, OnItemClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_msg_search, parent, false));
        this.listener = listener;
        avatar = itemView.findViewById(R.id.item_rank_avatar_riv);
        nameTv = itemView.findViewById(R.id.item_rank_name_tv);
        itemView.findViewById(R.id.item_rank_index_tv).setVisibility(View.GONE);

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(2, getAdapterPosition());
            }
        });
    }

    public void onBindData(UserBean data) {
        ImgLoader.displayCircleWhiteBorder(data.getAvatar(), avatar);
        nameTv.setText(data.getUser_nicename());
    }
}
