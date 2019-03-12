package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.yunbao.phonelive.bean.RankItemBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.fragment.RankListFragment;
import com.yunbao.phonelive.ui.views.viewholder.RankGameItemVh;
import com.yunbao.phonelive.ui.views.viewholder.RankItemVh;
import com.yunbao.phonelive.ui.views.viewholder.RankRichItemVh;

import java.util.List;


public class RankHomeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RankItemBean> datas;
    private OnItemClickListener listener;
    private int type = 0;  //type 0=明星主播  1 =人气主播  2=富豪实力榜  3=游戏达人榜  4= 牛牛排行榜

    public RankHomeListAdapter(List<RankItemBean> datas) {
        this.datas = datas;
    }

    public RankHomeListAdapter(int type) {
        this.type = type;
    }

    public RankHomeListAdapter() {
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setDatas(List<RankItemBean> datas) {
        this.datas = datas;
        if (this.datas != null) {
            for (RankItemBean data : this.datas) {
                if (data.getLiveInfo() != null) {
                    data.getLiveInfo().setAvatar(data.getAvatar());
                    data.getLiveInfo().setAvatar_thumb(data.getAvatar_thumb());
                    data.getLiveInfo().setId(data.getId());
                }
            }
            notifyDataSetChanged();
        }
    }

    public boolean isDatasEmpty() {
        return datas == null || datas.size() == 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (type) {
            case 0:
                return new RankItemVh(parent, listener);
            case 1:
                return new RankRichItemVh(parent);
            case 2:
                return new RankGameItemVh(parent);
            default:
                return new RankGameItemVh(parent);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RankItemVh) {
            ((RankItemVh) holder).onBindData(datas.get(position + 3), type);
        } else if (holder instanceof RankRichItemVh) {
            ((RankRichItemVh) holder).onBindData(datas.get(position + 3));
        } else if (holder instanceof RankGameItemVh) {
            ((RankGameItemVh) holder).onBindData(datas.get(position + 3), type);
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size() <= 3 ? 0 : datas.size() - 3;
//        return datas == null ? 0 : datas.size();
    }

    public void setAttention(String touid, int isAttention) {
        if (TextUtils.isEmpty(touid)) {
            return;
        }
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                if (touid.equals(datas.get(i).getUid())) {
                    if (datas.get(i).getLiveInfo() != null) {
                        datas.get(i).getLiveInfo().setIsAttention(isAttention);
//                        notifyItemChanged(i);
                        notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

    public void cleanDatas() {
        datas.clear();
        notifyDataSetChanged();
    }
}
