package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;

/**
 * Created by cxf on 2017/8/11.
 * 搜索，个人主页的关注、粉丝列表
 */

public class AttentionAdapter extends RecyclerView.Adapter<AttentionAdapter.Vh> {

    private Context mContext;
    private List<LiveBean> mFollowDatas, mUnfollowDatas;
    private LayoutInflater mInflater;
    private String mAttentionStr;
    private String mNoAttentionStr;
    private Drawable mAttentionTextBg;
    private Drawable mNoAttentionTextBg;
    public static final String FLAG = "flag";
    private int followCount = 0, unFollowCount = 0;
//    private CommonCallback<Integer> attentionCallback;


    public AttentionAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mAttentionStr = "关注";
        mNoAttentionStr = "取消";
        mAttentionTextBg = ContextCompat.getDrawable(mContext, R.drawable.bg_ui_item_unattention);
        mNoAttentionTextBg = ContextCompat.getDrawable(mContext, R.drawable.bg_ui_item_attention);
//        attentionCallback = new CommonCallback<Integer>() {
//            @Override
//            public void callback(Integer isAttention) {
//                mBean.setIsattention(isAttention);
//                 notifyItemChanged(mPosition, FLAG);
//            }
//        };
    }

    public void setFollowDatas(List<LiveBean> list) {
        if (mFollowDatas == null) {
            mFollowDatas = new ArrayList<>();
        }
        if (mUnfollowDatas == null) {
            mUnfollowDatas = new ArrayList<>();
        }
        mFollowDatas.clear();
        if (list != null) {
            mFollowDatas = list;
        }
//        datasCount = 0;
        datasCount = mFollowDatas.size();
        datasCount += mUnfollowDatas.size();
        notifyDataSetChanged();
    }

    public void setUnFollowDatas(List<LiveBean> list) {
        if (mFollowDatas == null) {
            mFollowDatas = new ArrayList<>();
        }
        if (mUnfollowDatas == null) {
            mUnfollowDatas = new ArrayList<>();
        }
        mUnfollowDatas.clear();
        if (list != null) {
            mUnfollowDatas = list;
        }
//        datasCount = 0;
        datasCount = mUnfollowDatas.size();
        datasCount += mFollowDatas.size();
        notifyDataSetChanged();
    }


    public void clear() {
        if (mFollowDatas != null) {
            mFollowDatas.clear();
            notifyDataSetChanged();
        }
    }

    public String getGroupId(int position) {
        if (mFollowDatas != null && position >= 0 && mFollowDatas.size() > position) {
            return mFollowDatas.get(position).getbTitle();
        }
        if (mUnfollowDatas != null && position >= mFollowDatas.size() && mUnfollowDatas.size() + mFollowDatas.size() > position) {
            return mUnfollowDatas.get(position - mFollowDatas.size()).getbTitle();
        }
        return "";
    }

    public String getGroupFirstLine(int position) {
        if (mFollowDatas != null && position >= 0 && mFollowDatas.size() > position) {
            return mFollowDatas.get(position).getbTitle();
        }
        if (mUnfollowDatas != null && position >= mFollowDatas.size() && mUnfollowDatas.size() + mFollowDatas.size() > position) {
            return mUnfollowDatas.get(position - mFollowDatas.size()).getbTitle();
        }
        return "";
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_attention, parent, false), listener);

    }

    @Override
    public void onBindViewHolder(Vh vh, final int position) {

    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        String flag = payloads.size() > 0 ? (String) payloads.get(0) : null;
        if (position < mFollowDatas.size()) {
            vh.setData(mFollowDatas.get(position), position, flag);
        } else {
            vh.setData(mUnfollowDatas.get(position - mFollowDatas.size()), position, flag);
        }

    }

    int datasCount = 0;

    @Override
    public int getItemCount() {
        return datasCount;
    }

    private OnItemClickListener<LiveBean> listener;

    public void setListener(OnItemClickListener<LiveBean> listener) {
        this.listener = listener;
    }


    class Vh extends RecyclerView.ViewHolder {
        ImageView headImg;
        TextView name;
        TextView attention, isLiveTv;
        LiveBean mBean;
        int mPosition;

        public Vh(View itemView, OnItemClickListener<LiveBean> listeners) {
            super(itemView);
            headImg = (ImageView) itemView.findViewById(R.id.headImg);
            name = (TextView) itemView.findViewById(R.id.name);
            attention = (TextView) itemView.findViewById(R.id.attention);
            isLiveTv = (TextView) itemView.findViewById(R.id.is_live_tv);

            Disposable subscribe = RxView.clicks(attention)
                    .throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                    .subscribe(o -> HttpUtil.setAttention(mBean.getUid(), new CommonCallback<Integer>() {
                        @Override
                        public void callback(Integer isAttention) {
                            mBean.setIsAttention(isAttention);
                            notifyItemChanged(mPosition, FLAG);
                            if (isAttention == 0) {
//                                未关注
                                mBean.setbTitle("推荐关注");
                                mFollowDatas.remove(mPosition);
                                AttentionAdapter.this.notifyItemRemoved(mPosition);
                                mUnfollowDatas.add(0, mBean);
                                AttentionAdapter.this.notifyDataSetChanged();
                            } else {
//                                已关注
                                mBean.setbTitle("已关注");
                                mUnfollowDatas.remove(mPosition - mFollowDatas.size());
                                AttentionAdapter.this.notifyItemRemoved(mPosition - mFollowDatas.size());
                                mFollowDatas.add(mBean);
                                AttentionAdapter.this.notifyDataSetChanged();
                            }

                        }
                    }));

            Disposable touid = RxView.clicks(this.itemView).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                    .subscribe(o -> {
                        if (mBean.getIslive() == 1) {
                            listener.onItemClick(mBean, getAdapterPosition());
//                            Intent intent;
//                            if ("0".equals(mBean.getAnyway())) {
//                                intent = new Intent(itemView.getContext(), LiveAudienceActivity.class);
//                            } else {
//                                intent = new Intent(itemView.getContext(), LiveWatcherActivity.class);
//                            }
//                            intent.putExtra("liveBean", mBean);
//                            itemView.getContext().startActivity(intent);
                        } else {
                            ToastUtil.show("啊哦，主播当前不在家呢！");
                        }
//                        Intent intent = new Intent(mContext, UserInfoActivity.class);
//                        intent.putExtra("touid", mBean.getId());
//                        mContext.startActivity(intent);
                    });
        }

        void setData(LiveBean bean, int position, String flag) {
            mBean = bean;
            mPosition = position;
            if (flag == null) {
                ImgLoader.displayCircle(bean.getAvatar(), headImg);
                name.setText(bean.getUser_nicename());
            }
            isLiveTv.setVisibility(mBean.getIslive() == 1 ? View.VISIBLE : View.INVISIBLE);
            if (bean.getIsAttention() == 0) {
                attention.setText(mAttentionStr);
                attention.setBackground(mAttentionTextBg);
                attention.setTextColor(itemView.getResources().getColor(R.color.app_selected_color));
            } else {
                attention.setText(mNoAttentionStr);
                attention.setBackground(mNoAttentionTextBg);
                attention.setTextColor(itemView.getResources().getColor(R.color.footNotHintTextColor));
            }
        }
    }


}
