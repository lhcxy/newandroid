package com.yunbao.phonelive.ui.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.lzy.okgo.model.Response;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.activity.WebBannerActivity;
import com.yunbao.phonelive.adapter.HomeHotAdapter;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.SliderBean;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.event.HomeTabEvent;
import com.yunbao.phonelive.event.TabChannelEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.interfaces.MainEventListener;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.helper.CommonItemDecoration;
import com.yunbao.phonelive.ui.helper.GridSpacingItemDecoration;
import com.yunbao.phonelive.ui.views.ClassifyActivity;
import com.yunbao.phonelive.ui.views.RankMoreActivity;
import com.yunbao.phonelive.ui.views.adapter.RecommendHotLiverAdapter;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 首页推荐
 */
public class HomeRecommendFragment extends BaseLazyFragment implements MainEventListener, RefreshLayout.OnRefreshListener {
    private RecyclerView homeRv, homeLiverRv, homeAllRv, footBallRv, basketBallRv, tableRv;
    private RefreshLayout mRefreshLayout;
    private HomeHotAdapter mAdapter, mAllAdapter, mFootBallAdapter, mBasketBallAdapter, mTableAdapter;
    private NestedScrollView scrollView;
    private Banner mBanner;
    private RecommendHotLiverAdapter liverAdapter;
    List<SliderBean> sliderList;
    private long lastClikTime = 0L;
    private GridLayoutManager layoutManager;
    private RelativeLayout footRl, basketRl, tableRl;


    public static HomeRecommendFragment newInstance() {
        HomeRecommendFragment fragment = new HomeRecommendFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_home_recommend;
    }

    @Override
    protected void initView() {
        homeRv = findView(R.id.new_home_rv);
        homeLiverRv = findView(R.id.new_home_liver_rv);
        homeAllRv = findView(R.id.new_home_all_rv);
        mBanner = findView(R.id.view_banner);
        scrollView = findView(R.id.new_home_nsv);
        footBallRv = findView(R.id.new_home_football_rv);
        basketBallRv = findView(R.id.new_home_basketball_rv);
        tableRv = findView(R.id.new_home_table_rv);
        mRefreshLayout = findView(R.id.new_home_refresh_layout);
        footRl = findView(R.id.title_football_rl);
        basketRl = findView(R.id.title_basketball_rl);
        tableRl = findView(R.id.title_table_rl);

        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                ImgLoader.display(((SliderBean) path).getSlide_pic(), imageView, R.mipmap.bg_home_placeholder2);
            }
        });
        mBanner.setOnBannerListener(p -> {
            if (System.currentTimeMillis() - lastClikTime >= AppConfig.TAB_SCROLL_COUNT) {
                if (sliderList != null && sliderList.size() > p && !TextUtils.isEmpty(sliderList.get(p).getSlide_url())) {
                    Intent intent = new Intent(getContext(), WebBannerActivity.class);
                    intent.putExtra("url", sliderList.get(p).getSlide_url());
                    getContext().startActivity(intent);
                }
                lastClikTime = System.currentTimeMillis();
            }

        });
        initRecyclerview();

//        disposable.add(RxView.clicks(findView(R.id.home_recommend_classify)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
//                .subscribe(o -> getContext().startActivity(new Intent(getContext(), ClassifyActivity.class))));
    }

    @Override
    protected void initData() {
        pageNum = 1;
        mRefreshLayout.setCanLoadMore(true);
        HttpUtil.getHot(mRefreshCallback);
        HttpUtil.getLiveByTag("6", 1, footCall);//足球
        HttpUtil.getLiveByTag("7", 1, basketCall);//篮球
        HttpUtil.getLiveByTag("11", 1, tableCall);//台球
        HttpUtil.getBannerList(2, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    sliderList = JSON.parseArray(Arrays.toString(info), SliderBean.class);
                    if (sliderList != null && sliderList.size() > 0) {
                        mBanner.setImages(sliderList);
                        mBanner.start();
                    } else {
                        mBanner.setVisibility(View.GONE);
                    }
                } else mBanner.setVisibility(View.GONE);
            }
        });
        getAllLiveByPage();
    }


    private void getAllLiveByPage() {
        HttpUtil.getAllLiveByPage(pageNum, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    List datas = JSON.parseArray(Arrays.toString(info), LiveBean.class);
                    if (datas != null && datas.size() > 0) {
                        if (mAllAdapter == null) {
                            mAllAdapter = new HomeHotAdapter();
                            mAllAdapter.setOnItemClickListener((item, position) -> {
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).watchLive(item);
                                }
                            });
                            homeAllRv.setAdapter(mAllAdapter);
                        } else {
                            mAllAdapter.setIsNoMore(true);
                        }
                        if (pageNum == 1) {
                            mAllAdapter.setData(datas);
                        } else {
                            mAllAdapter.addDatas(datas);
                        }
                    }
//                    mRefreshLayout.completeLoadMore();
                    if (datas != null && datas.size() >= 10) {
                        mRefreshLayout.setCanLoadMore(true);
                    } else {
                        mRefreshLayout.setCanLoadMore(false);
                    }
                } else {
                    mAllAdapter.setIsNoMore(true);
                    mRefreshLayout.setCanLoadMore(false);
                }

            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                ToastUtil.show("加载失败，请检查网络");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (pageNum == 1) {
                    mRefreshLayout.completeRefresh();
                } else mRefreshLayout.completeLoadMore();
            }
        });
    }


    List<LiveBean> footDatas, basketBallDatas, tableDatas;

    private void initRecyclerview() {
        homeRv.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        homeRv.addItemDecoration(new GridSpacingItemDecoration(2, DpUtil.dp2px(8), true));
        footBallRv.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        footBallRv.addItemDecoration(new GridSpacingItemDecoration(2, DpUtil.dp2px(8), true));
        basketBallRv.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        basketBallRv.addItemDecoration(new GridSpacingItemDecoration(2, DpUtil.dp2px(8), true));
        tableRv.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        tableRv.addItemDecoration(new GridSpacingItemDecoration(2, DpUtil.dp2px(8), true));

        mFootBallAdapter = new HomeHotAdapter(footDatas);
        mFootBallAdapter.isShowNoMore(false);
        mFootBallAdapter.setOnItemClickListener((item, position) -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).watchLive(item);
            }
        });
        mBasketBallAdapter = new HomeHotAdapter(basketBallDatas);
        mBasketBallAdapter.isShowNoMore(false);
        mBasketBallAdapter.setOnItemClickListener((item, position) -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).watchLive(item);
            }
        });
        mTableAdapter = new HomeHotAdapter(tableDatas);
        mTableAdapter.isShowNoMore(false);
        mTableAdapter.setOnItemClickListener((item, position) -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).watchLive(item);
            }
        });
        footBallRv.setAdapter(mFootBallAdapter);
        basketBallRv.setAdapter(mBasketBallAdapter);
        tableRv.setAdapter(mTableAdapter);


        mAdapter = new HomeHotAdapter(datas);
        mAdapter.setOnItemClickListener((item, position) -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).watchLive(item);
            }
        });
        mAllAdapter = new HomeHotAdapter(datas);
        mAllAdapter.setOnItemClickListener((item, position) -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).watchLive(item);
            }
        });
        layoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        homeAllRv.setLayoutManager(layoutManager);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if (mAllAdapter.isNoMore() && mAllAdapter.getItemCount() - 1 == i) {
                    return 2;
                }
                return 1;
            }
        });
        homeAllRv.addItemDecoration(new GridSpacingItemDecoration(2, DpUtil.dp2px(8), true));
        homeAllRv.setAdapter(mAllAdapter);
        homeRv.setAdapter(mAdapter);
        homeLiverRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeLiverRv.addItemDecoration(new CommonItemDecoration(DpUtil.dp2px(12), DpUtil.dp2px(2)));
        liverAdapter = new RecommendHotLiverAdapter();
        liverAdapter.setDatas(datas);
        liverAdapter.setOnItemClickListener((item, position) -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).watchLive(item);
            }
        });
        homeLiverRv.setAdapter(liverAdapter);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setScorllView(scrollView);

        disposable.add(RxView.clicks(findView(R.id.home_football_more_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            EventBus.getDefault().post(new HomeTabEvent("6"));
        }));
        disposable.add(RxView.clicks(findView(R.id.home_basketball_more_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            EventBus.getDefault().post(new HomeTabEvent("7"));
        }));
        disposable.add(RxView.clicks(findView(R.id.home_table_more_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            EventBus.getDefault().post(new HomeTabEvent("11"));
        }));
        disposable.add(RxView.clicks(findView(R.id.main_home_title_hot_onwer_more_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            RankMoreActivity.jump2RankListMore(getContext(), 0);
        }));

    }


    private HttpCallback footCall = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null && info.length > 0) {
                footDatas = JSON.parseArray(Arrays.toString(info), LiveBean.class);
                if (footDatas != null && footDatas.size() > 0) {
                    footRl.setVisibility(View.VISIBLE);
                    footBallRv.setVisibility(View.VISIBLE);
                    if (mFootBallAdapter == null) {
                        mFootBallAdapter = new HomeHotAdapter(footDatas);
                        mFootBallAdapter.setIsNoMore(false);
                        mFootBallAdapter.setOnItemClickListener((item, position) -> {
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).watchLive(item);
                            }
                        });
                        footBallRv.setAdapter(mFootBallAdapter);
                    } else {
                        mFootBallAdapter.setData(footDatas);
                    }
                } else {
                    footRl.setVisibility(View.GONE);
                    footBallRv.setVisibility(View.GONE);
                }
            } else {
                footRl.setVisibility(View.GONE);
                footBallRv.setVisibility(View.GONE);
            }
        }
    };

    private HttpCallback basketCall = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null && info.length > 0) {
                basketBallDatas = JSON.parseArray(Arrays.toString(info), LiveBean.class);
                if (basketBallDatas != null && basketBallDatas.size() > 0) {
                    basketRl.setVisibility(View.VISIBLE);
                    basketBallRv.setVisibility(View.VISIBLE);
                    if (mBasketBallAdapter == null) {
                        mBasketBallAdapter = new HomeHotAdapter(basketBallDatas);
                        mBasketBallAdapter.setIsNoMore(false);
                        mBasketBallAdapter.setOnItemClickListener((item, position) -> {
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).watchLive(item);
                            }
                        });
                        basketBallRv.setAdapter(mBasketBallAdapter);
                    } else {
                        mBasketBallAdapter.setData(basketBallDatas);
                    }

                } else {
                    basketRl.setVisibility(View.GONE);
                    basketBallRv.setVisibility(View.GONE);
                }
            } else {
                basketRl.setVisibility(View.GONE);
                basketBallRv.setVisibility(View.GONE);
            }

        }
    };


    private HttpCallback tableCall = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null && info.length > 0) {
                tableDatas = JSON.parseArray(Arrays.toString(info), LiveBean.class);
                if (tableDatas != null && tableDatas.size() > 0) {
                    tableRl.setVisibility(View.VISIBLE);
                    tableRv.setVisibility(View.VISIBLE);
                    if (mTableAdapter == null) {
                        mTableAdapter = new HomeHotAdapter(tableDatas);
                        mTableAdapter.setIsNoMore(false);
                        mTableAdapter.setOnItemClickListener((item, position) -> {
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).watchLive(item);
                            }
                        });
                        tableRv.setAdapter(mTableAdapter);
                    } else {
                        mTableAdapter.setData(tableDatas);
                    }

                } else {
                    tableRl.setVisibility(View.GONE);
                    tableRv.setVisibility(View.GONE);
                }
            } else {
                tableRl.setVisibility(View.GONE);
                tableRv.setVisibility(View.GONE);

            }
        }
    };


    List<LiveBean> datas;
    private HttpCallback mRefreshCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null && info.length > 0) {
                JSONObject parse = JSON.parseObject(info[0]);
                parse.getString("list");
                datas = JSON.parseArray(parse.getString("list"), LiveBean.class);
                if (datas != null && datas.size() > 0) {
                    if (mAdapter == null) {
                        mAdapter = new HomeHotAdapter(datas);
                        mAdapter.setOnItemClickListener((item, position) -> {
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).watchLive(item);
                            }
                        });
                        homeRv.setAdapter(mAdapter);
                    } else {
                        mAdapter.setData(datas);
                    }
                    if (liverAdapter != null) {
                        liverAdapter.setDatas(datas);
                    }
                }
            }
        }

        @Override
        public void onError(Response<JsonBean> response) {
            super.onError(response);
            if (mAdapter != null) {
                mAdapter.clearData();
            }
        }

        @Override
        public void onFinish() {
            if (mRefreshLayout != null) {
                mRefreshLayout.completeRefresh();
            }
        }
    };

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onLoadMore() {
        pageNum++;
        getAllLiveByPage();
    }

    private int pageNum = 1;
    private boolean mFirst = true;

    @Override
    public void loadData() {
        if (mFirst) {
            mFirst = false;
            initData();
        }
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_HOT);
        HttpUtil.cancel(HttpUtil.GET_BANNER_LIST);
        super.onDestroyView();
    }
}
