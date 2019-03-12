package com.yunbao.phonelive.ui.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.view.RxView;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.SearchActivity;
import com.yunbao.phonelive.activity.WebBannerActivity;
import com.yunbao.phonelive.bean.RankAdBean;
import com.yunbao.phonelive.bean.RankItemBean;
import com.yunbao.phonelive.event.AttentionEvent;
import com.yunbao.phonelive.fragment.AbsFragment;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.RankMoreActivity;
import com.yunbao.phonelive.ui.views.adapter.RankHomeListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;

public class RankListFragment extends AbsFragment implements OnItemClickListener<String> {

    private RankHomeListAdapter rankStarAdapter, rankPopularityAdapter, rankRichAdapter, rankGameAdapter, rankNiuNiuAdapter;
    private RelativeLayout consumerTitleLl, profitTitleLl, richLl, gameLl, niuniuLl;
    public static final String TAG = RankListFragment.class.getSimpleName();
    private CompositeDisposable disposable;
    ArrayList<RankAdBean> mSliderList;
    private Banner mBanner;
    private RecyclerView rankStarRv, rankPopularityRv, rankRichRv, rankGameRv, rankNiuNiuRv;
    private LinearLayout emptyLl;

    public static RankListFragment newInstance() {
        RankListFragment fragment = new RankListFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_rank_list;
    }

    @Override
    protected void main() {
        disposable = new CompositeDisposable();
        initView();
        onBindBanner();
        initData();
    }

    private void initData() {

        HttpUtil.getRankBannerList(3, adCallback);
        HttpUtil.getHomeBdList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                Log.e(TAG, "onSuccess: " + Arrays.toString(info));
                if (info != null && info.length > 0) {
                    emptyLl.setVisibility(View.GONE);
                    JSONObject jsonObject = JSON.parseObject(info[0]);
                    String mxwlist = jsonObject.getString("mxwlist");//明星主播
                    if (!TextUtils.isEmpty(mxwlist)) {
                        List<RankItemBean> rankItemBeans = JSON.parseArray(mxwlist, RankItemBean.class);
                        if (rankItemBeans != null && rankItemBeans.size() > 0) {
                            rankStarAdapter.setDatas(rankItemBeans);
                            rankStarRv.setVisibility(View.VISIBLE);
                            profitTitleLl.setVisibility(View.VISIBLE);
                        } else {
                            rankStarRv.setVisibility(View.GONE);
                            profitTitleLl.setVisibility(View.GONE);
                        }
                    } else {
                        rankStarRv.setVisibility(View.GONE);
                        profitTitleLl.setVisibility(View.GONE);
                    }
                    String hxwlist = jsonObject.getString("hxwlist");//人气主播
                    if (!TextUtils.isEmpty(hxwlist)) {
                        List<RankItemBean> rankItemBeans = JSON.parseArray(hxwlist, RankItemBean.class);
                        if (rankItemBeans != null && rankItemBeans.size() > 0) {
                            rankPopularityAdapter.setDatas(rankItemBeans);
                            rankPopularityRv.setVisibility(View.VISIBLE);
                            consumerTitleLl.setVisibility(View.VISIBLE);
                        } else {
                            rankPopularityRv.setVisibility(View.GONE);
                            consumerTitleLl.setVisibility(View.GONE);
                        }
                    } else {
                        rankPopularityRv.setVisibility(View.GONE);
                        consumerTitleLl.setVisibility(View.GONE);
                    }
                    String fhwlist = jsonObject.getString("fhwlist");//富豪
                    if (!TextUtils.isEmpty(fhwlist)) {
                        List<RankItemBean> rankItemBeans = JSON.parseArray(fhwlist, RankItemBean.class);
                        if (rankItemBeans != null && rankItemBeans.size() > 0) {
                            rankRichAdapter.setDatas(rankItemBeans);
                            rankRichRv.setVisibility(View.VISIBLE);
                            richLl.setVisibility(View.VISIBLE);
                        } else {
                            rankRichRv.setVisibility(View.GONE);
                            richLl.setVisibility(View.GONE);
                        }
                    } else {
                        rankRichRv.setVisibility(View.GONE);
                        richLl.setVisibility(View.GONE);
                    }
                    String yxlist = jsonObject.getString("yxlist");//富豪
                    if (!TextUtils.isEmpty(yxlist)) {
                        List<RankItemBean> rankItemBeans = JSON.parseArray(yxlist, RankItemBean.class);
                        if (rankItemBeans != null && rankItemBeans.size() > 0) {
                            rankGameAdapter.setDatas(rankItemBeans);
                            rankGameRv.setVisibility(View.VISIBLE);
                            gameLl.setVisibility(View.VISIBLE);
                        } else {
                            rankGameRv.setVisibility(View.GONE);
                            gameLl.setVisibility(View.GONE);
                        }
                    } else {
                        rankGameRv.setVisibility(View.GONE);
                        gameLl.setVisibility(View.GONE);
                    }
                    String nwlist = jsonObject.getString("nwlist");//富豪
                    if (!TextUtils.isEmpty(nwlist)) {
                        List<RankItemBean> rankItemBeans = JSON.parseArray(nwlist, RankItemBean.class);
                        if (rankItemBeans != null && rankItemBeans.size() > 0) {
                            rankNiuNiuAdapter.setDatas(rankItemBeans);
                            rankNiuNiuRv.setVisibility(View.VISIBLE);
                            niuniuLl.setVisibility(View.VISIBLE);
                        } else {
                            rankNiuNiuRv.setVisibility(View.GONE);
                            niuniuLl.setVisibility(View.GONE);
                        }
                    } else {
                        rankNiuNiuRv.setVisibility(View.GONE);
                        niuniuLl.setVisibility(View.GONE);
                    }
                } else {
                    emptyLl.setVisibility(View.VISIBLE);
                }
            }
        });
        EventBus.getDefault().register(this);
    }

    private HttpCallback adCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null && info.length > 0) {
                mSliderList = new Gson().fromJson(Arrays.toString(info), new TypeToken<ArrayList<RankAdBean>>() {
                }.getType());
                mBanner.setVisibility(View.VISIBLE);
                mBanner.setImages(mSliderList);
                mBanner.start();
            } else {
                mBanner.setVisibility(View.GONE);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    };

    private void onBindBanner() {
        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                ImgLoader.display(((RankAdBean) path).getThumb(), imageView, R.mipmap.bg_home_placeholder2);
            }
        });
        mBanner.setOnBannerListener(p -> {
            if (mSliderList != null && mSliderList.size() > p && !TextUtils.isEmpty(mSliderList.get(p).getUrl())) {
                Intent intent = new Intent(mContext, WebBannerActivity.class);
                intent.putExtra("url", mSliderList.get(p).getUrl());
                mContext.startActivity(intent);
            }
        });
    }

    private void initView() {
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.rank_list_title_search))
                .throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> startActivity(new Intent(mContext, SearchActivity.class))));

        mBanner = mRootView.findViewById(R.id.rank_list_banner);
        rankStarRv = mRootView.findViewById(R.id.rank_star_rv);
        rankRichRv = mRootView.findViewById(R.id.rank_rich_rv);
        rankGameRv = mRootView.findViewById(R.id.rank_game_rv);
        rankNiuNiuRv = mRootView.findViewById(R.id.rank_niuniu_rv);
        rankPopularityRv = mRootView.findViewById(R.id.rank_popularity_rv);
        emptyLl = mRootView.findViewById(R.id.rank_list_empty_ll);
        profitTitleLl = mRootView.findViewById(R.id.ranklist_star_more_ll);
        consumerTitleLl = mRootView.findViewById(R.id.ranklist_popularity_more_ll);
        richLl = mRootView.findViewById(R.id.ranklist_rich_more_ll);
        gameLl = mRootView.findViewById(R.id.ranklist_game_more_ll);
        niuniuLl = mRootView.findViewById(R.id.ranklist_niuniu_more_ll);

        rankStarRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rankPopularityRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rankRichRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rankGameRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rankNiuNiuRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rankStarAdapter = new RankHomeListAdapter(0);
        rankPopularityAdapter = new RankHomeListAdapter(1);
        rankRichAdapter = new RankHomeListAdapter(2);
        rankGameAdapter = new RankHomeListAdapter(3);
        rankNiuNiuAdapter = new RankHomeListAdapter(4);
        rankStarAdapter.setListener(this);
        rankPopularityAdapter.setListener(this);
        rankRichAdapter.setListener(this);
        rankGameAdapter.setListener(this);
        rankNiuNiuAdapter.setListener(this);
        rankStarRv.setAdapter(rankStarAdapter);
        rankPopularityRv.setAdapter(rankPopularityAdapter);
        rankRichRv.setAdapter(rankRichAdapter);
        rankGameRv.setAdapter(rankGameAdapter);
        rankNiuNiuRv.setAdapter(rankNiuNiuAdapter);
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.ranklist_star_more_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> RankMoreActivity.jump2RankListMore(getContext(), 0)));
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.ranklist_popularity_more_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> RankMoreActivity.jump2RankListMore(getContext(), 1)));
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.ranklist_rich_more_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> RankMoreActivity.jump2RankListMore(getContext(), 2)));
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.ranklist_game_more_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> RankMoreActivity.jump2RankListMore(getContext(), 3)));
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.ranklist_niuniu_more_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> RankMoreActivity.jump2RankListMore(getContext(), 4)));

    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        HttpUtil.cancel(HttpUtil.PROFIT_LIST);
        HttpUtil.cancel(HttpUtil.CONSUME_LIST);
        HttpUtil.cancel(HttpUtil.GET_RANK_BANNER_LIST);
        EventBus.getDefault().unregister(this);
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
        super.onDestroyView();
    }


    @Override
    public void onItemClick(String uId, int position) {
        HttpUtil.setAttention(uId, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(AttentionEvent message) {
        if (rankPopularityAdapter != null) {
            rankPopularityAdapter.setAttention(message.getTouid(), message.getIsAttention());
        }
        if (rankStarAdapter != null) {
            rankStarAdapter.setAttention(message.getTouid(), message.getIsAttention());
        }
    }


}
