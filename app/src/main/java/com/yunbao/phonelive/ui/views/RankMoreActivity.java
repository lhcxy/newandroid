package com.yunbao.phonelive.ui.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.interfaces.ChoseCallback;
import com.yunbao.phonelive.ui.views.adapter.LiveWatcherVPAdapter;
import com.yunbao.phonelive.ui.views.fragment.RankListMoreFragment;
import com.yunbao.phonelive.ui.views.pop.RankTypePop;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 榜单-更多
 * type 0 =收益榜  1 = 消费榜
 */
public class RankMoreActivity extends AbsActivity {
    private TabLayout titleTl;
    private ViewPager rankMoreVp;
    private int rankType;
    private ArrayList<String> datas;

    private int lastSelectIndex = -1;
    private int tabIndex = 0;
    private TextView subtitleTv;

    /**
     * 跳转更多榜单
     *
     * @param context
     * @param type    0 = 明星主播  1 = 人气主播  2= 富豪实力  3=游戏大人 4=牛牛排行榜
     */
    public static void jump2RankListMore(Context context, int type) {
        Intent intent = new Intent(context, RankMoreActivity.class);
        intent.putExtra("RankType", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_rank_more_list;
    }

    @Override
    protected void main() {
        initView();
        setTitle("探球星秀榜");
        initData();
    }

    private void initData() {
        if (getIntent() != null) {
            rankType = getIntent().getIntExtra("RankType", 0);
        }
        rankMoreVp.setCurrentItem(rankType);
        Objects.requireNonNull(titleTl.getTabAt(0)).select();
    }

    private RankTypePop pop;
    private int typeIndex = 0;

    private void initView() {
        titleTl = findViewById(R.id.rank_more_title_tl);
        rankMoreVp = findViewById(R.id.rank_more_vp);
        subtitleTv = findViewById(R.id.subtitle_tv);
        pop = new RankTypePop(RankMoreActivity.this);
        pop.setCallback(choseIndex -> {
            typeIndex = choseIndex;
            if (typeIndex == 0) {
                subtitleTv.setText("周榜");
            } else {
                subtitleTv.setText("月榜");
            }
            if (tabIndex == 0) {
                startF.setWeekType(typeIndex);
            } else if (tabIndex == 1) {
                richF.setWeekType(typeIndex);
            } else {
                eggF.setWeekType(typeIndex);
            }
        });
        disposable.add(RxView.clicks(subtitleTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (pop == null) {
                pop = new RankTypePop(RankMoreActivity.this);
            }
            pop.showAtBottom(subtitleTv, typeIndex);
        }));
        addTabItem();
        titleTl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null && tab.getCustomView().findViewById(R.id.classify_tab_title_v) != null)
                    tab.getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.VISIBLE);
                if (tab.getCustomView() != null && tab.getCustomView().findViewById(R.id.classify_tab_title_tv) != null) {
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextColor(getResources().getColor(R.color.text_color_333));
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                }
                tabIndex = tab.getPosition();
                if (tabIndex < fragmentPagerAdapter.getCount())
                    rankMoreVp.setCurrentItem(tabIndex);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null && tab.getCustomView().findViewById(R.id.classify_tab_title_v) != null)
                    tab.getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.INVISIBLE);
                if (tab.getCustomView() != null && tab.getCustomView().findViewById(R.id.classify_tab_title_tv) != null) {
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextColor(getResources().getColor(R.color.text_color_88));
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                if (tab.getCustomView() != null && tab.getCustomView().findViewById(R.id.classify_tab_title_v) != null)
//                    tab.getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.VISIBLE);
//                if (tab.getCustomView() != null && tab.getCustomView().findViewById(R.id.classify_tab_title_tv) != null) {
//                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextColor(getResources().getColor(R.color.text_color_333));
//                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
//                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//                }
//                lastSelectIndex = tab.getPosition();
//                rankMoreVp.setCurrentItem(lastSelectIndex);
            }
        });
        onBindViewPager();
    }

    private void addTabItem() {
        getTabDatas();
        titleTl.removeAllTabs();
        if (datas != null && datas.size() > 0) {
            titleTl.setTabMode(datas.size() <= AppConfig.TAB_SCROLL_COUNT ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE);
            for (int i = 0; i < datas.size(); i++) {
                TabLayout.Tab tab = titleTl.newTab();
                tab.setCustomView(R.layout.item_ui_classify_title_tab);
                ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setText(datas.get(i));
                if (i == 0) {
                    tab.getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.VISIBLE);
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextColor(getResources().getColor(R.color.text_color_333));
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                } else {
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextColor(getResources().getColor(R.color.text_color_88));
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tab.getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.INVISIBLE);
                }
                titleTl.addTab(tab, i);
            }
        }
    }

    FragmentStatePagerAdapter fragmentPagerAdapter;

    private void onBindViewPager() {
        fragmentPagerAdapter = new LiveWatcherVPAdapter(getSupportFragmentManager(), fragments);
        rankMoreVp.setOffscreenPageLimit(3);
        rankMoreVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                Objects.requireNonNull(titleTl.getTabAt(position)).select();
                tabIndex = position;
                if (tabIndex == 0) {
                    startF.setWeekType(typeIndex);
                } else if (tabIndex == 1) {
                    richF.setWeekType(typeIndex);
                } else {
                    eggF.setWeekType(typeIndex);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        rankMoreVp.setAdapter(fragmentPagerAdapter);
    }

    private ArrayList<Fragment> fragments;
    private RankListMoreFragment startF, richF, eggF;

    private void getTabDatas() {
        datas = new ArrayList<>();
        fragments = new ArrayList<>();

        startF = RankListMoreFragment.newInstance(0);
        richF = RankListMoreFragment.newInstance(1);
        eggF = RankListMoreFragment.newInstance(2);
        fragments.add(startF);
        fragments.add(richF);
        fragments.add(eggF);

        datas.add("明星主播榜");
        datas.add("综合土豪榜");
        datas.add("欢乐砸蛋榜");
    }


}
