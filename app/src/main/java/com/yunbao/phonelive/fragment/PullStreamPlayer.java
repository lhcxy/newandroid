package com.yunbao.phonelive.fragment;

import android.view.View;

import com.yunbao.phonelive.fragment.AbsFragment;

/**
 * Created by cxf on 2017/10/23.
 */

public abstract class PullStreamPlayer extends AbsFragment {

    protected View mLoading;
    protected String mStreamUrl;
    protected boolean mStarted;
    protected boolean mPaused;//fragment生命周期的pause
    protected boolean mPausePlay;//播放器的pause
    protected boolean mDestoryed;//是否被销毁了
    protected int mReLoadCount;

    public abstract void play(String streamUrl);

    public abstract void pausePlay();

    public abstract void resumePlay();

    public abstract void release();
}
