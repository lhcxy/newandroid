package com.yunbao.phonelive.custom.music;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ksyun.media.streamer.kit.KSYStreamer;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveLrcBean;


import java.io.IOException;


/**
 * Created by cxf on 2017/9/16.
 */

public class MusicPlayer implements View.OnClickListener, View.OnTouchListener {
    private ViewGroup mParent;
    private View mView;
    private MediaPlayer mPlayer;//播放器声音为0，只用来读取歌曲的进度
    private boolean hasSourceData;//是否设置了资源
    private boolean isStarted;//是否开始了播放
    private boolean isPaused;//是否暂停了

    private LiveLrcBean mLrcBean;//全部歌词
    private LrcTextView mLrcTextView;//歌词控件
    private TextView mBtnEnd;//关闭按钮
    private TextView mTimeTextView;//时间
    private Handler mHandler;
    private LiveLrcBean.LrcItem mCurLrcItem;//当前正在显示的歌词
    private LiveLrcBean.LrcItem mNextLrcItem;//下一句歌词
    private Runnable mOnClose;//关闭时候的回调
    private KSYStreamer mStreamer;//流对象，包含有音乐播放器，来播放音乐，同时混合
    private String mSource;//歌曲资源 即歌曲的file的path
    private int mParentWidth;
    private int mParentHeight;
    private float mLastX;
    private float mLastY;
    private Context mContext;

    public MusicPlayer(ViewGroup parent, KSYStreamer streamer, Runnable onClose) {
        mParent = parent;
        mContext = parent.getContext();
        mParentWidth = mParent.getWidth();
        mParentHeight = mParent.getHeight();
        mStreamer = streamer;
        mOnClose = onClose;
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_live_lrc, mParent, false);
        mView.setOnTouchListener(this);
        mBtnEnd = (TextView) mView.findViewById(R.id.btn_end);
        mBtnEnd.setOnClickListener(this);
        mTimeTextView = (TextView) mView.findViewById(R.id.time);
        mLrcTextView = (LrcTextView) mView.findViewById(R.id.lrc);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (!isStarted || !hasSourceData || mPlayer == null || mView == null) {
                    return;
                }
                switch (msg.what) {
                    case 0:
                        refreshTime();
                        break;
                    case 1:
                        refreshLrc();
                        break;
                    case 2:
                        getNextLrcItem();
                        break;
                    case 3:
                        mCurLrcItem = mNextLrcItem;
                        showLrc();
                        getNextLrcItem();
                        break;
                }
            }
        };
        initPlayer();
    }

    private void initPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(mOnPreparedListener);
        mPlayer.setOnErrorListener(mOnErrorListener);
        mPlayer.setOnInfoListener(mOnInfoListener);
        mStreamer.setEnableAudioMix(true);
        mStreamer.getAudioPlayerCapture().setVolume(0.5f);
    }


    @Override
    public boolean onTouch(View v, MotionEvent e) {
        float x = e.getRawX();
        float y = e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (dx != 0) {
                    float targetX = mView.getTranslationX() + dx;
                    if (targetX < 0) {
                        targetX = 0;
                    }
                    int rightLimit = mParentWidth - mView.getWidth();
                    if (targetX > rightLimit) {
                        targetX = rightLimit;
                    }
                    mView.setTranslationX(targetX);
                }
                if (dy != 0) {
                    float targetY = mView.getTranslationY() + dy;
                    if (targetY < 0) {
                        targetY = 0;
                    }
                    int bottomLimit = mParentHeight - mView.getHeight();
                    if (targetY > bottomLimit) {
                        targetY = bottomLimit;
                    }
                    mView.setTranslationY(targetY);
                }
        }
        mLastX = x;
        mLastY = y;
        return true;
    }

    /**
     * 每一秒刷新一次时间
     */
    private void refreshTime() {
        if (mHandler != null && !isPaused) {
            String time = getDurationText(mPlayer.getCurrentPosition());
            mTimeTextView.setText(time);
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    /**
     * 每0.3秒刷新一次歌词进度
     */
    private void refreshLrc() {
        if (mHandler != null && !isPaused && mCurLrcItem != null) {
            int p = mPlayer.getCurrentPosition();
            float cur = p - mCurLrcItem.getStartTime();
            if (cur > 0 && mCurLrcItem.getLrc().length() > 0) {
                float rate = cur / mCurLrcItem.getDuration();
                mLrcTextView.setProgress(rate);
            }
            mHandler.sendEmptyMessageDelayed(1, 300);
        }
    }

    /**
     * 显示歌词
     */
    private void showLrc() {
        String lrc = mCurLrcItem.getLrc();
        mLrcTextView.setProgress(0);
        mLrcTextView.setText(lrc);
    }

    /**
     * 获取第一句歌词
     */
    private void getFristLrcItem() {
        if (mLrcBean != null && mHandler != null && !isPaused) {
            mCurLrcItem = mLrcBean.getFristLrcItem();
            if (mCurLrcItem == null) {
                return;
            }
            showLrc();
            mHandler.sendEmptyMessageDelayed(1, mCurLrcItem.getStartTime());
            mHandler.sendEmptyMessageDelayed(2, mCurLrcItem.getStartTime());
        }
    }

    /**
     * 获取下一句歌词
     */
    private void getNextLrcItem() {
        if (mLrcBean != null && mHandler != null && !isPaused) {
            mNextLrcItem = mLrcBean.getNextLrcItem();
            int p = mPlayer.getCurrentPosition();
            int delayTime = mNextLrcItem.getStartTime() - p;
            if (delayTime > 0) {
                mHandler.sendEmptyMessageDelayed(3, delayTime);
            } else {
                if (mNextLrcItem.isFirstLrc()) {
                    mHandler.sendEmptyMessageDelayed(3, mPlayer.getDuration() - p);
                } else {
                    mHandler.sendEmptyMessage(3);
                }
            }
        }
    }

    /**
     * 切后台后回来执行
     */
    private void resumeNextLrcItem() {
        if (mNextLrcItem != null && mLrcBean != null && mHandler != null && !isPaused) {
            int p = mPlayer.getCurrentPosition();
            int delayTime = mNextLrcItem.getStartTime() - p;
            if (delayTime > 0) {
                mHandler.sendEmptyMessageDelayed(3, delayTime);
            } else {
                mHandler.sendEmptyMessageDelayed(3, mPlayer.getDuration() - p);
            }
        }
    }


    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mView == null) {
                return;
            }
            mp.start();
            mStreamer.startBgm(mSource, true);
            isStarted = true;
            mHandler.sendEmptyMessageDelayed(0, 1000);
            getFristLrcItem();
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
//            L.e("MusicPlayer", "onError--->what--->" + what + "extra--->" + extra);
            return false;
        }
    };

    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
//            L.e("MusicPlayer", "onInfo--->what--->" + what + "extra--->" + extra);
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        destroy();
        if (mOnClose != null) {
            mOnClose.run();
        }
    }

    public void play(String source, LiveLrcBean bean) {
        mSource = source;
        if (mView.getParent() == null) {
            mParent.addView(mView);
        }
        mLrcBean = bean;
        if (mLrcBean == null || !mLrcBean.hasLrc()) {
            mLrcTextView.setProgress(0);
            mLrcTextView.setText(mContext.getString(R.string.lrc_not_found));
        }
        if (hasSourceData) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
            hasSourceData = false;
            isStarted = false;
            mHandler.removeCallbacksAndMessages(null);
            mTimeTextView.setText("00:00");
        }
        try {
            mPlayer.setDataSource(source);
            mPlayer.prepareAsync();
            mPlayer.setLooping(true);
            mPlayer.setVolume(0, 0);
            hasSourceData = true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, mContext.getResources().getString(R.string.mp3_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    public void pause() {
        if (isStarted) {
            isPaused = true;
            mPlayer.pause();
            mHandler.removeCallbacksAndMessages(null);
            mStreamer.getAudioPlayerCapture().getMediaPlayer().pause();
        }
    }

    public void resume() {
        if (isStarted && isPaused) {
            isPaused = false;
            mPlayer.start();
            refreshTime();
            refreshLrc();
            resumeNextLrcItem();
            mStreamer.getAudioPlayerCapture().getMediaPlayer().start();
        }
    }

    public void destroy() {
        mStreamer.stopBgm();
        mStreamer.setEnableAudioMix(false);
        isPaused = true;
        isStarted = false;
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mParent.removeView(mView);
        mPlayer.release();
        mOnPreparedListener = null;
        mOnErrorListener = null;
        mOnInfoListener = null;
        mPlayer = null;
    }

    /**
     * 把一个long类型的总毫秒数转成时长
     */
    private String getDurationText(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        String s = "";
        if (hours > 0) {
            if (hours < 10) {
                s += "0" + hours + ":";
            } else {
                s += hours + ":";
            }
        }
        if (minutes > 0) {
            if (minutes < 10) {
                s += "0" + minutes + ":";
            } else {
                s += minutes + ":";
            }
        } else {
            s += "00" + ":";
        }
        if (seconds > 0) {
            if (seconds < 10) {
                s += "0" + seconds;
            } else {
                s += seconds;
            }
        } else {
            s += "00";
        }
        return s;
    }

}
