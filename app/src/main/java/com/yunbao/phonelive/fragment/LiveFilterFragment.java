package com.yunbao.phonelive.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAnchorActivity;
import com.yunbao.phonelive.adapter.LiveFilterAdapter;
import com.yunbao.phonelive.bean.LiveFilterBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.WordUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/9/1.
 */

public class LiveFilterFragment extends AbsFragment implements OnItemClickListener<LiveFilterBean> {

    private RecyclerView mRecyclerView;
    private LiveFilterAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_filter;
    }

    @Override
    protected void main() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        if (mAdapter == null) {
            List<LiveFilterBean> list = Arrays.asList(
                    new LiveFilterBean(1, WordUtil.getString(R.string.yuantu), R.mipmap.icon_filter_yuantu, true),
                    new LiveFilterBean(2, WordUtil.getString(R.string.qingxin), R.mipmap.icon_filter_qingxin, false),
                    new LiveFilterBean(3, WordUtil.getString(R.string.liangli), R.mipmap.icon_filter_liangli, false),
                    new LiveFilterBean(4, WordUtil.getString(R.string.tianmei), R.mipmap.icon_filter_tianmei, false),
                    new LiveFilterBean(5, WordUtil.getString(R.string.huaijiu), R.mipmap.icon_filter_huaijiu, false)
            );
            mAdapter = new LiveFilterAdapter(mContext, list);
            mAdapter.setOnItemClickListener(this);
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(LiveFilterBean bean, int position) {
        ((LiveAnchorActivity) mContext).setSpecialFilter(bean.getId());
    }
}
