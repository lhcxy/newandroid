package com.yunbao.phonelive.ui.views.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.SearchActivity;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.event.HomeTabEvent;
import com.yunbao.phonelive.fragment.AbsFragment;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.JIMUtil;
import com.yunbao.phonelive.ui.helper.TabChannelHelper;
import com.yunbao.phonelive.ui.views.MsgCenterActivity;
import com.yunbao.phonelive.ui.views.TabEditActivity;
import com.yunbao.phonelive.ui.views.adapter.NewHomePagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.model.Message;

public class NewHomeFragment extends AbsFragment {

    private ViewPager classifyVp;
    private TabLayout classifyTabl;
    private TextView classifyTabTitleTv;
    private View classifyTabTitleLine;
    private List<TabBean> datas;
    private NewHomePagerAdapter adapter;
    private View unReadV;

    public static NewHomeFragment newInstance() {
        NewHomeFragment fragment = new NewHomeFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_new_home;
    }


    @Override
    protected void main() {
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
//        datas = TabChannelHelper.getInstance().getShowData(getContext());
        classifyVp = findView(R.id.classify_tab_vp);
        classifyTabl = findView(R.id.classify_tab_title_tabl);
        classifyTabTitleTv = findView(R.id.classify_tab_title_tv);
        classifyTabTitleLine = findView(R.id.classify_tab_title_v);
        unReadV = findView(R.id.user_msg_unread_v);
//        disposable.add(RxView.clicks(mRootView.findViewById(R.id.new_home_tl_edit_iv)).
//                throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
//                .subscribe(o -> {
//                    Intent intent = new Intent(getContext(), TabEditActivity.class);
//                    startActivityForResult(intent, 11);
//                }));
        initVP();
        initTabLayout();
        classifyTabTitleTv.setText("推荐");
        classifyTabTitleTv.setOnClickListener(view -> classifyVp.setCurrentItem(0));
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.new_home_tl_search_iv))
                .throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> startActivity(new Intent(mContext, SearchActivity.class))));

        disposable.add(RxView.clicks(findView(R.id.user_msg_rl)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (unReadV.getVisibility() == View.VISIBLE) {
                        unReadV.setVisibility(View.INVISIBLE);
                    }
                    startActivity(new Intent(getContext(), MsgCenterActivity.class));
                }));

        if (!AppConfig.isUnlogin() && (AppConfig.getInstance().getUserBean().getMnum() > 0 || JIMUtil.getInstance().hasUnreade())) {
            unReadV.setVisibility(View.VISIBLE);
        } else unReadV.setVisibility(View.INVISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(Message message) {
        if (!AppConfig.isUnlogin() && (AppConfig.getInstance().getUserBean().getMnum() > 0 || JIMUtil.getInstance().hasUnreade())) {
            unReadV.setVisibility(View.VISIBLE);
        } else unReadV.setVisibility(View.INVISIBLE);
    }

    private void initVP() {
        classifyVp.setOffscreenPageLimit(4);
        adapter = new NewHomePagerAdapter(getChildFragmentManager(), datas);
        classifyVp.setAdapter(adapter);
        classifyVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    classifyTabTitleLine.setVisibility(View.VISIBLE);
                    classifyTabTitleTv.setTextColor(getResources().getColor(R.color.text_color_333));
                    classifyTabTitleTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                    classifyTabTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    if (classifyTabl != null && classifyTabl.getTabAt(tabIndex) != null &&
                            classifyTabl.getTabAt(tabIndex).getCustomView() != null && classifyTabl.getTabAt(tabIndex).getCustomView().findViewById(R.id.classify_tab_title_v) != null) {
                        classifyTabl.getTabAt(tabIndex).getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.INVISIBLE);
                        TextView tv = (TextView) classifyTabl.getTabAt(tabIndex).getCustomView().findViewById(R.id.classify_tab_title_tv);
                        if (tv != null) {
                            tv.setTextColor(getResources().getColor(R.color.text_color_88));
                            tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//常规
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        }
                    }
                } else {
                    classifyTabTitleLine.setVisibility(View.INVISIBLE);
                    classifyTabTitleTv.setTextColor(getResources().getColor(R.color.text_color_88));
                    classifyTabTitleTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
                    classifyTabTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    classifyTabl.getTabAt(position - 1).select();
                    if (classifyTabl != null && classifyTabl.getTabAt(tabIndex) != null &&
                            classifyTabl.getTabAt(position - 1).getCustomView() != null && classifyTabl.getTabAt(position - 1).getCustomView().findViewById(R.id.classify_tab_title_v) != null) {
                        classifyTabl.getTabAt(position - 1).getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.VISIBLE);
                        TextView tv = (TextView) classifyTabl.getTabAt(tabIndex).getCustomView().findViewById(R.id.classify_tab_title_tv);
                        if (tv != null) {
                            tv.setTextColor(getResources().getColor(R.color.text_color_333));
                            tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        }

                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private int tabIndex = 0;

    private void initTabLayout() {
        classifyTabl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            private int lastSelectIndex = -1;

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
                if (tabIndex + 1 < adapter.getCount())
                    classifyVp.setCurrentItem(tabIndex + 1);
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
                if (tab.getCustomView() != null && tab.getCustomView().findViewById(R.id.classify_tab_title_v) != null)
                    tab.getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.VISIBLE);
                if (tab.getCustomView() != null && tab.getCustomView().findViewById(R.id.classify_tab_title_tv) != null) {
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextColor(getResources().getColor(R.color.text_color_333));
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                    ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                }
                lastSelectIndex = tab.getPosition();
                classifyVp.setCurrentItem(lastSelectIndex + 1);
            }
        });
        getLiveTag();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == TabEditActivity.TAB_RESULT_CODE) {
            datas.clear();
            datas = TabChannelHelper.getInstance().getShowData(getContext());
            addTabItem();
//            int currentItem = classifyVp.getCurrentItem();
            adapter.changeDatas(datas);
//            if (datas == null || currentItem >= datas.size()) {
            classifyVp.setCurrentItem(0);
            classifyTabTitleLine.setVisibility(View.VISIBLE);
            classifyTabTitleTv.setTextColor(getResources().getColor(R.color.text_color_333));
            classifyTabTitleTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
            classifyTabTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//            }
//            else {
//                classifyVp.setCurrentItem(currentItem);
//            }
        }
    }

    private void addTabItem() {
        classifyTabl.removeAllTabs();
        if (datas != null && datas.size() > 0) {
            classifyTabl.setTabMode(datas.size() <= AppConfig.TAB_SCROLL_COUNT ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE);
            for (int i = 0; i < datas.size(); i++) {
                TabLayout.Tab tab = classifyTabl.newTab();
                tab.setCustomView(R.layout.item_ui_classify_title_tab);
                ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setText(datas.get(i).getName());
                tab.getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.INVISIBLE);
                ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextColor(getResources().getColor(R.color.text_color_88));
                ((TextView) tab.getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                classifyTabl.addTab(tab, i);
            }
            classifyTabl.getTabAt(tabIndex).getCustomView().findViewById(R.id.classify_tab_title_v).setVisibility(View.INVISIBLE);
            ((TextView) classifyTabl.getTabAt(tabIndex).getCustomView().findViewById(R.id.classify_tab_title_tv)).setTextColor(getResources().getColor(R.color.text_color_88));
        }
    }

    public void getLiveTag() {
        HttpUtil.getAllLiveTag(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    TabChannelHelper.getInstance().saveFullData(getContext(), JSON.parseArray(Arrays.toString(info), TabBean.class));
                    datas = TabChannelHelper.getInstance().getShowData(getContext());
                    adapter.changeDatas(datas);
                    addTabItem();
                    classifyVp.setCurrentItem(0);
                    classifyTabTitleLine.setVisibility(View.VISIBLE);
                    classifyTabTitleTv.setTextColor(getResources().getColor(R.color.text_color_333));
                    classifyTabTitleTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                    classifyTabTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChoseTab(HomeTabEvent message) {
        if (datas != null) {
            for (int i = 0; i < datas.size(); i++) {
                if (message.getTabId().equals(datas.get(i).getId())) {
                    classifyVp.setCurrentItem(i + 1);
                    break;
                }
            }
        }
    }

}
