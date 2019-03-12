package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.ItemMsgSearchVh;

import java.util.List;

public class MsgSearchContentRvAda extends RecyclerView.Adapter<ItemMsgSearchVh> {
    private List<UserBean> datas;
    private OnItemClickListener<Integer> listener;

    public MsgSearchContentRvAda(List<UserBean> datas) {
        this.datas = datas;
    }

    public void setListener(OnItemClickListener<Integer> listener) {
        this.listener = listener;
    }

    public void setDatas(List<UserBean> datas) {
        this.datas = datas;
    }

    @NonNull
    @Override
    public ItemMsgSearchVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ItemMsgSearchVh(viewGroup, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemMsgSearchVh itemMsgSearchVh, int i) {
        itemMsgSearchVh.onBindData(datas.get(i));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }
}
