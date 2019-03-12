package com.yunbao.phonelive.utils;

import android.os.Handler;
import android.os.Message;

/**
 * Created by cxf on 2017/8/5.
 */

public class TimeOutCountDown {
    private Handler mHandler;
    private long mMilliseconds;

    /**
     * @param milliseconds //延时的时间，单位是毫秒，时间到会执行回调方法，并且取消定时器
     * @param callback     回调方法
     */
    public TimeOutCountDown(long milliseconds, final Callback callback) {
        mMilliseconds = milliseconds;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (callback != null) {
                    callback.callback();
                }
                clear();
            }
        };
    }

    public void start() {
        mHandler.sendEmptyMessageDelayed(0, mMilliseconds);
    }

    public void clear() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    public interface Callback<T> {
        void callback();
    }
}
