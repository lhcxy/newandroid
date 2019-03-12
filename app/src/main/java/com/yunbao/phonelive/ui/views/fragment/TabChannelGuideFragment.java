package com.yunbao.phonelive.ui.views.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.event.TabChannelEvent;
import com.yunbao.phonelive.fragment.AbsFragment;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.helper.GridSpacingItemDecoration;
import com.yunbao.phonelive.ui.views.adapter.TabChannelGuideAdapter;
import com.yunbao.phonelive.utils.DpUtil;

import cn.jpush.im.android.eventbus.EventBus;

/**
 * 分类导航界面
 */
public class TabChannelGuideFragment extends AbsFragment {

    public static final int SPAN_COUNT = 4;
    private RecyclerView channelRv;

    public static TabChannelGuideFragment newInstance() {

        Bundle args = new Bundle();
        TabChannelGuideFragment fragment = new TabChannelGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_channel;
    }

    @Override
    protected void main() {
        channelRv = mRootView.findViewById(R.id.tab_channel_guide_rv);

        channelRv.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));

        channelRv.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, DpUtil.dp2px(8), true));
        TabChannelGuideAdapter tabChannelGuideAdapter = new TabChannelGuideAdapter(getContext());

        channelRv.setAdapter(tabChannelGuideAdapter);

    }


}
