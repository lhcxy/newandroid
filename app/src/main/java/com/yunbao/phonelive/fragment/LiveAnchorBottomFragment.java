package com.yunbao.phonelive.fragment;

import android.view.View;

import com.yunbao.phonelive.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/10/9.
 */

public class LiveAnchorBottomFragment extends LiveBottomFragment {

//    private View GameBtn;//关闭游戏的按钮

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bottom_anchor;
    }

    @Override
    protected void main() {
        super.main();
//        GameBtn = mRootView.findViewById(R.id.btn_close_game);
        EventBus.getDefault().register(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
