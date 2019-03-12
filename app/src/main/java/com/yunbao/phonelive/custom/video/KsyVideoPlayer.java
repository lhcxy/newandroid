package com.yunbao.phonelive.custom.video;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.player.KSYTextureView;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;

import java.io.IOException;

/**
 * Created by cxf on 2017/8/13.
 */

public class KsyVideoPlayer extends RelativeLayout implements VideoController.VideoControllerListener {

    private Context mContext;
    private KSYTextureView mTextureView;
    private String mUrl;
    private View mLoadingView;
    private boolean mPaused;
    private boolean mStarted;
    private VideoController mVideoController;
    private long mCurPosition;
    private long mTotalDuration;
    private int mCurProgress;
    private Handler mHandler;
    private boolean isBufferCompleted;
    private int mVideoControllerHeight;
    private int mHeight;
    private boolean isVideoControllerShowed = true;
    private boolean animating;
    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;

    public KsyVideoPlayer(Context context) {
        this(context, null);
    }

    public KsyVideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KsyVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setBackgroundColor(0xb3000000);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextureView = new KSYTextureView(mContext);
        mTextureView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mTextureView);
        mLoadingView = LayoutInflater.from(mContext).inflate(R.layout.view_loading, this, false);
        LayoutParams params1 = (LayoutParams) mLoadingView.getLayoutParams();
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mLoadingView);
        mVideoController = new VideoController(mContext);
        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mVideoController.setLayoutParams(params2);
        addView(mVideoController);
        mVideoController.setControllerListener(this);
        mVideoController.post(new Runnable() {
            @Override
            public void run() {
                mVideoControllerHeight = mVideoController.getHeight();
                L.e("mVideoControllerHeight--->" + mVideoControllerHeight);
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (animating) {
                    return;
                }
                if (isVideoControllerShowed) {
                    hideVideoController();
                } else {
                    showVideoController();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
    }

    private void showVideoController() {
        if (mShowAnimator == null) {
            mShowAnimator = ObjectAnimator.ofFloat(mVideoController, "y", mHeight, mHeight - mVideoControllerHeight);
            mShowAnimator.setDuration(200);
            mShowAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animating = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isVideoControllerShowed = true;
                    animating = false;
                    if(mHandler!=null){
                        mHandler.sendEmptyMessageDelayed(1, 3000);
                    }
                }
            });
        }
        mShowAnimator.start();

    }

    private void hideVideoController() {
        if (mHideAnimator == null) {
            mHideAnimator = ObjectAnimator.ofFloat(mVideoController, "y", mHeight - mVideoControllerHeight, mHeight);
            mHideAnimator.setDuration(200);
            mHideAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animating = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isVideoControllerShowed = false;
                    animating = false;
                }
            });
        }
        mHideAnimator.start();
        if(mHandler!=null){
            mHandler.removeMessages(1);
        }
    }

    public void setDataSource(String url) {
        mUrl = url;
    }

    public void start() {
        mTextureView.setKeepScreenOn(true);
        mTextureView.setOnPreparedListener(mOnPreparedListener);
        mTextureView.setOnErrorListener(mOnErrorListener);
        mTextureView.setOnInfoListener(mOnInfoListener);
        mTextureView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mTextureView.setScreenOnWhilePlaying(true);
        mTextureView.setBufferSize(10);
        mTextureView.setTimeout(5000, 5000);
        mTextureView.setVolume(1f, 1f);
        mTextureView.setLooping(true);//循环播放
        mTextureView.setDecodeMode(KSYMediaPlayer.KSYDecodeMode.KSY_DECODE_MODE_AUTO);
        mTextureView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        try {
            mTextureView.setDataSource(mUrl);
            mTextureView.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            if (mLoadingView.getVisibility() == View.VISIBLE) {
                mLoadingView.setVisibility(View.GONE);
            }
            mTextureView.start();
            mTotalDuration = mTextureView.getDuration();
            mVideoController.setTotalDuration(getDurationText(mTotalDuration));
            mStarted = true;
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 0:
                            if (!mTextureView.isPlaying()) {
                                return;
                            }
                            mCurPosition = mTextureView.getCurrentPosition();
                            mVideoController.setCurDuration(getDurationText(mCurPosition));
                            mCurProgress = (int) (mCurPosition * 100f / mTotalDuration);
                            mVideoController.setProgress(mCurProgress);
                            mHandler.sendEmptyMessageDelayed(0, 1000);
                            break;
                        case 1:
                            if (isVideoControllerShowed&&mTextureView.isPlaying()) {
                                hideVideoController();
                            }
                            break;
                    }

                }
            };
            mHandler.sendEmptyMessageDelayed(0, 1000);
            mHandler.sendEmptyMessageDelayed(1, 3000);
        }
    };


    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            switch (i) {
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_START://缓冲开始
                    if (mLoadingView.getVisibility() == View.GONE) {
                        mLoadingView.setVisibility(View.VISIBLE);
                    }
                    break;
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_END://缓冲结束
                    if (mLoadingView.getVisibility() == View.VISIBLE) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                    break;
            }
            return false;
        }
    };


    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            if (mLoadingView.getVisibility() == View.VISIBLE) {
                mLoadingView.setVisibility(View.GONE);
            }
            L.e("OnErrorListener--->" + what);
            switch (what) {
                case -10011:
                case -10002:
                    ToastUtil.show(mContext.getString(R.string.network_error));
                    break;
                case -10007:
                case -10008:
                    ToastUtil.show(mContext.getString(R.string.mp4_error));
                    break;
            }
            return false;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            if (!isBufferCompleted) {
                mVideoController.setSecondaryProgress(i);
                if (i == 100) {
                    isBufferCompleted = true;
                }
            }
        }
    };


    public void resume() {
        if (mTextureView != null && mPaused) {
            mPaused = false;
            mTextureView.runInForeground();
            mTextureView.start();
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    @Override
    public void play() {
        mTextureView.start();
        mVideoController.setCurDuration(getDurationText(mCurPosition));
        mVideoController.setProgress(mCurProgress);
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(0, 1000);
            mHandler.sendEmptyMessageDelayed(1, 3000);
        }
    }

    @Override
    public void pausePlay() {
        mTextureView.pause();
        if (mHandler != null) {
            mHandler.removeMessages(0);
            mHandler.removeMessages(1);
        }
    }

    public void pause() {
        if (mTextureView != null) {
            mPaused = true;
            mTextureView.runInBackground(false);
            if (mHandler != null) {
                mHandler.removeMessages(0);
            }
        }
    }

    @Override
    public void seekTo(int progress) {
        if (!mStarted) {
            return;
        }
        mCurProgress = progress;
        mCurPosition = (long) (mTotalDuration * mCurProgress / 100f);
        mVideoController.setCurDuration(getDurationText(mCurPosition));
        mTextureView.seekTo(mCurPosition);
    }


    public void release() {
        if (mTextureView != null && mStarted) {
            mTextureView.stop();
            mTextureView.reset();
            mTextureView.release();
        }
        mTextureView = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
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
