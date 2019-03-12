package com.yunbao.phonelive.ui.views.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.ui.views.fragment.HomeRecommendFragment;
import com.yunbao.phonelive.ui.views.fragment.TabChannelFragment;
import com.yunbao.phonelive.ui.views.fragment.TabChannelGuideFragment;

import java.util.List;


public class ClassifyPagerAdapter extends FragmentStatePagerAdapter {

    private List<TabBean> datas;

    public ClassifyPagerAdapter(FragmentManager fm, List<TabBean> datas) {
        super(fm);
        this.datas = datas;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return TabChannelGuideFragment.newInstance();
        }
        return TabChannelFragment.newInstance(datas.get(position - 1).getId());
    }

    public void changeDatas(List<TabBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return datas == null ? 1 : datas.size() + 1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "分类";
        } else {
            return datas.get(position - 1).getName();
        }
    }
}
