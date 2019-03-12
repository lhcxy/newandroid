package com.yunbao.phonelive.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.custom.ViewPagerIndicator;
import com.yunbao.phonelive.interfaces.MainEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/2/2.
 */

public class HomeListFragment extends AbsFragment implements MainEventListener {

    private ViewPagerIndicator mIndicator;
    private ViewPager mViewPager;
    private List<OrderListFragment> mFragmentList;
    private boolean mFirst1;
    private boolean mFirst2;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_list;
    }

    @Override
    protected void main() {
        mFirst1 = true;
        mFirst2 = true;
        mIndicator = (ViewPagerIndicator) mRootView.findViewById(R.id.indicator);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        String[] tabs = new String[]{getString(R.string.list_1), getString(R.string.list_2)};
        mIndicator.setTitles(tabs);
        mIndicator.setChangeSize(false);
        //设置导航条item的可见个数
        mIndicator.setVisibleChildCount(2);
        mFragmentList = new ArrayList<>();
        OrderListFragment f1 = new OrderListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(OrderListFragment.LIST_TYPE, OrderListFragment.PROFIT);
        f1.setArguments(bundle);
        mFragmentList.add(f1);
        OrderListFragment f2 = new OrderListFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt(OrderListFragment.LIST_TYPE, OrderListFragment.CONSUME);
        f2.setArguments(bundle2);
        mFragmentList.add(f2);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });
        mIndicator.setViewPager(mViewPager);
        mIndicator.setListener(new ViewPagerIndicator.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    if (mFirst2) {
                        mFirst2 = false;
                        mFragmentList.get(1).initData();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mFirst1) {
                mFirst1 = false;
                mFragmentList.get(0).initData();
            }
        }
    }

    @Override
    public void loadData() {

    }
}
