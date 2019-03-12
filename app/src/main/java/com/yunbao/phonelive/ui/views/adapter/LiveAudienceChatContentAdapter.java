package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.LiveChatDefVh;
import com.yunbao.phonelive.ui.views.viewholder.LiveChatHorizontalVh;

import java.util.ArrayList;
import java.util.List;

public class LiveAudienceChatContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<LiveChatBean> datas;

    public LiveAudienceChatContentAdapter() {
        this.datas = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        switch (getItemViewType(i)) {
//            case GIFT:
//                return new LiveChatGiftVh(viewGroup);
//            default:
        return new LiveChatDefVh(viewGroup, listener);
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof LiveChatDefVh) {
            ((LiveChatDefVh) viewHolder).bindData(datas.get(i));
        }
//        else if (viewHolder instanceof LiveChatGiftVh) {
//            ((LiveChatGiftVh) viewHolder).bindData(datas.get(i));
//        }
    }

    public void addData(LiveChatBean data) {
        datas.add(data);
        notifyItemInserted(datas.size());
    }

//    @Override
//    public int getItemViewType(int position) {
//        return datas.get(position).getType();
//    }


    private OnItemClickListener<String> listener;

    public void setItemClickListener(OnItemClickListener<String> listener) {
        this.listener = listener;

    }

    public void clear() {
        datas = null;
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }
}
