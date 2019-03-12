package com.yunbao.phonelive.ui.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.yunbao.phonelive.R;

public class LiveEmptyVh extends RecyclerView.ViewHolder {
    public LiveEmptyVh(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ui_live_empty, parent, false));
    }


}
