package com.yunbao.phonelive.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.interfaces.MainEventListener;
import com.yunbao.phonelive.custom.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2017/8/8.
 */

public class HomeFragment extends AbsFragment implements MainEventListener {

    private ViewPagerIndicator mIndicator;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;
    private TextView mRedPoint;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void main() {
        initView();
    }

    private void initView() {
        mRedPoint = (TextView) mRootView.findViewById(R.id.red_point);
        mIndicator = (ViewPagerIndicator) mRootView.findViewById(R.id.indicator);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        String[] tabs = new String[]{getString(R.string.attention2), getString(R.string.hot), getString(R.string.newest)};
        mIndicator.setTitles(tabs);
        //设置导航条item的可见个数
        mIndicator.setVisibleChildCount(3);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new HomeAttentionFragment());
        mFragmentList.add(new HomeHotFragment());
        mFragmentList.add(new HomeNewFragment());
        mViewPager.setOffscreenPageLimit(3);
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
                //滑动到哪一页，加载那一页的数据
                ((MainEventListener) mFragmentList.get(position)).loadData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void loadData() {
        ((MainEventListener) mFragmentList.get(mViewPager.getCurrentItem())).loadData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            loadData();
        }
    }

    public void setUnReadCount(int count) {
        if (count > 0) {
            if (mRedPoint.getVisibility() == View.GONE) {
                mRedPoint.setVisibility(View.VISIBLE);
            }
            mRedPoint.setText(String.valueOf(count));
        } else {
            if (mRedPoint.getVisibility() == View.VISIBLE) {
                mRedPoint.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).onChildResume(0);
    }
}
