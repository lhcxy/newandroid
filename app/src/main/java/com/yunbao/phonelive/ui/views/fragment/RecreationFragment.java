package com.yunbao.phonelive.ui.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.Response;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.activity.WebBannerActivity;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.SliderBean;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.helper.GridSpacingItemDecoration;
import com.yunbao.phonelive.ui.views.adapter.RecreationAdapter;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 娱乐直播
 */
public class RecreationFragment extends BaseLazyFragment implements RefreshLayout.OnRefreshListener {
    private NestedScrollView recreationNsv;
    private RecyclerView recreationRv;
    private Banner recreationBanner;
    private RefreshLayout refreshLayout;
    private RecreationAdapter mAdapter;
    private List<SliderBean> sliderList;
    private GridLayoutManager layoutManager;


    public static RecreationFragment newInstance() {
        RecreationFragment fragment = new RecreationFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_recreation;
    }

    private long lastClikTime = 0L;

    @Override
    protected void initView() {
        recreationNsv = findView(R.id.recreation_nsv);
        recreationRv = findView(R.id.recreation_rv);
        recreationBanner = findView(R.id.view_banner);
        refreshLayout = findView(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setScorllView(recreationNsv);
        mAdapter = new RecreationAdapter();
        layoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if (mAdapter.isNoMore() && mAdapter.getItemCount() - 1 == i) {
                    return 2;
                }
                return 1;
            }
        });
        recreationRv.setLayoutManager(layoutManager);
        recreationRv.setHasFixedSize(true);
        recreationRv.addItemDecoration(new GridSpacingItemDecoration(2, DpUtil.dp2px(8), true));
        mAdapter.setListener((OnItemClickListener<LiveBean>) (item, position) -> ((MainActivity) getActivity()).watchLive(item));
        recreationRv.setAdapter(mAdapter);

        recreationBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                ImgLoader.display(((SliderBean) path).getSlide_pic(), imageView, R.mipmap.bg_home_placeholder2);
            }
        });
        recreationBanner.setOnBannerListener(p -> {
            if (System.currentTimeMillis() - lastClikTime >= AppConfig.TAB_SCROLL_COUNT) {
                if (sliderList != null && sliderList.size() > p && !TextUtils.isEmpty(sliderList.get(p).getSlide_url())) {
                    Intent intent = new Intent(getContext(), WebBannerActivity.class);
                    intent.putExtra("url", sliderList.get(p).getSlide_url());
                    getContext().startActivity(intent);
                }
                lastClikTime = System.currentTimeMillis();
            }
        });
        refreshLayout.beginRefresh();
    }

    @Override
    protected void initData() {

    }

    private int pageNum = 1;
    private HttpCallback mLiveDataCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            List<LiveBean> liveList = JSON.parseArray(Arrays.toString(info), LiveBean.class);
            if (mAdapter == null) {
                mAdapter = new RecreationAdapter();
                mAdapter.setListener((OnItemClickListener<LiveBean>) (item, position) -> ((MainActivity) getActivity()).watchLive(item));
                recreationRv.setAdapter(mAdapter);
            }
            if (pageNum == 1) {
                mAdapter.setDatas(liveList);
            } else {
                mAdapter.addDatas(liveList);
            }
            if (liveList != null && liveList.size() >= 10) {
                refreshLayout.setCanLoadMore(true);
            } else
                refreshLayout.setCanLoadMore(false);
        }

        @Override
        public void onError(Response<JsonBean> response) {
            super.onError(response);
        }

        @Override
        public void onFinish() {
            if (refreshLayout != null) {
                refreshLayout.completeRefresh();
                refreshLayout.completeLoadMore();
            }
        }
    };


    private HttpCallback mBannerCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null && info.length > 0) {
                sliderList = JSON.parseArray(Arrays.toString(info), SliderBean.class);
                if (sliderList != null && sliderList.size() > 0) {
                    recreationBanner.setImages(sliderList);
                    recreationBanner.start();
                    recreationBanner.setVisibility(View.VISIBLE);
                } else {
                    recreationBanner.setVisibility(View.GONE);
                }
            } else recreationBanner.setVisibility(View.GONE);
        }
    };

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_NEW);
        HttpUtil.cancel(HttpUtil.GET_BANNER_LIST);
        super.onDestroyView();
    }


    @Override
    public void onRefresh() {
        pageNum = 1;
        if (refreshLayout != null) {
            refreshLayout.setCanLoadMore(true);
        }
        HttpUtil.getPleasureLive(pageNum, mLiveDataCallback);
        HttpUtil.getBannerList(3, mBannerCallback);
    }

    @Override
    public void onLoadMore() {
        pageNum++;
        HttpUtil.getPleasureLive(pageNum, mLiveDataCallback);
    }
}
