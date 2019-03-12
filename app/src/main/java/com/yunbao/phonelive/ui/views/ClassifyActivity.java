package com.yunbao.phonelive.ui.views;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.event.TabChannelEvent;
import com.yunbao.phonelive.ui.helper.TabChannelHelper;
import com.yunbao.phonelive.ui.views.adapter.ClassifyPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


public class ClassifyActivity extends AbsActivity {

    private ViewPager classifyVp;
    private TabLayout classifyTabl;
    private TextView classifyTabTitleTv;
    private View classifyTabTitleLine;
    private ArrayList<TabBean> datas;

    public static void jump2ClassifyActivity(Context context, String channelId) {
        Intent intent = new Intent(context, ClassifyActivity.class);
        intent.putExtra("channelId", channelId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_classify;
    }

    private String channelId;

    @Override
    protected void main() {
        if (getIntent() != null) {
            channelId = getIntent().getStringExtra("channelId");
        }
        datas = TabChannelHelper.getInstance().getFullData(this);
        ((TextView) findViewById(R.id.title)).setText("分类筛选");
        classifyVp = findViewById(R.id.classify_tab_vp);
        classifyTabl = findViewById(R.id.classify_tab_title_tabl);
        classifyTabTitleTv = findViewById(R.id.classify_tab_title_tv);
        classifyTabTitleLine = findViewById(R.id.classify_tab_title_v);
        initVP();
        initTabLayout();
        EventBus.getDefault().register(this);
        classifyTabTitleTv.setText("分类");
        classifyTabTitleTv.setTextColor(getResources().getColor(R.color.textColor));
        classifyTabTitleTv.setOnClickListener(view -> classifyVp.setCurrentItem(0));

        if (datas != null) {
            for (int i = 0; i < datas.size(); i++) {
                if (datas.get(i).getId().equals(channelId)) {
                    tabIndex = i;
                }
            }
        }
        classifyTabl.postDelayed(() -> classifyTabl.getTabAt(tabIndex).select(), 100);
    }

    private int tabIndex = 0;

    private void initTabLayout() {
        if (datas != null) {
            classifyTabl.setTabMode(datas.size() <= AppConfig.TAB_SCROLL_COUNT ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE);
            for (int i = 0; i < datas.size(); i++) {
                TabLayout.Tab tab = classifyTabl.newTab();
                tab.setCustomView(R.layout.item_ui_classify_title_tab);
                ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setText(datas.get(i).getName());
                classifyTabl.addTab(tab, i);
            }
        }

        classifyTabl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            private int lastSelectIndex = -1;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.VISIBLE);
                tabIndex = tab.getPosition();
                classifyVp.setCurrentItem(tabIndex + 1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.VISIBLE);
                lastSelectIndex = tab.getPosition();
                classifyVp.setCurrentItem(lastSelectIndex + 1);
            }
        });
    }

    private void initVP() {
        classifyVp.setOffscreenPageLimit(4);
        classifyVp.setAdapter(new ClassifyPagerAdapter(getSupportFragmentManager(), datas));
        classifyVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    classifyTabTitleLine.setVisibility(View.VISIBLE);
                    classifyTabl.getTabAt(tabIndex).getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.INVISIBLE);
                } else {
                    classifyTabTitleLine.setVisibility(View.INVISIBLE);
                    classifyTabl.getTabAt(position - 1).select();
                    if (position == 1) {
                        classifyTabl.getTabAt(tabIndex).getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChannelChangeEvent(TabChannelEvent event) {
        if (classifyVp != null) {
            if (event.getSelectedPosion() < datas.size()) {
                classifyTabl.getTabAt(event.getSelectedPosion()).select();
//                    classifyVp.setCurrentItem(event.getSelectedPosion()+1);
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
