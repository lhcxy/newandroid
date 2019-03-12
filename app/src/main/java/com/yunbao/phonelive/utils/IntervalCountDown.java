package com.yunbao.phonelive.utils;

import android.os.Handler;
import android.os.Message;

/**
 * Created by cxf on 2017/8/5.
 */

public class IntervalCountDown {
    private Handler mHandler;
    private final int INTERVAL = 1000;//间隔的毫秒数,默认为1000毫秒
    private int mCount;

    /**
     * @param targetCount //目标次数，到达这个次数的时候取消定时器
     * @param callback    每次的回调
     */
    public IntervalCountDown(final int targetCount, final Callback callback) {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mCount++;
                if (mCount <= targetCount) {
                    if (callback != null) {
                        callback.callback(mCount);
                    }
                    if (mCount == targetCount) {
                        clear();
                    } else {
                        start();
                    }
                }
            }
        };
    }


    public void start() {
        mHandler.sendEmptyMessageDelayed(0, INTERVAL);
    }

    public void clear() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    public interface Callback<T> {
        void callback(int count);
    }
}
