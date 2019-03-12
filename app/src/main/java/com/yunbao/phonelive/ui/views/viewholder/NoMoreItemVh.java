package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.yunbao.phonelive.R;

public class NoMoreItemVh extends RecyclerView.ViewHolder {
    public NoMoreItemVh(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_no_more, parent, false));
    }
}
