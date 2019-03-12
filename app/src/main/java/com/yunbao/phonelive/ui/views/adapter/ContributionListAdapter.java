package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.RankItemBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.viewholder.ContributionItemVh;
import com.yunbao.phonelive.ui.views.viewholder.RankItemVh;

import java.util.List;

/**
 * 贡献榜 adapter
 */
public class ContributionListAdapter extends RecyclerView.Adapter<ContributionItemVh> {
    private List<RankItemBean> datas;
    private boolean isWhite = false;
    public ContributionListAdapter(List<RankItemBean> datas) {
        this.datas = datas;
    }

    public ContributionListAdapter() {
    }

    public ContributionListAdapter(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public void setDatas(List<RankItemBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public boolean isDatasEmpty() {
        return datas == null || datas.size() == 0;
    }

    @NonNull
    @Override
    public ContributionItemVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContributionItemVh(parent, isWhite);
    }

    @Override
    public void onBindViewHolder(@NonNull ContributionItemVh holder, int position) {
        holder.onBindData(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

}
