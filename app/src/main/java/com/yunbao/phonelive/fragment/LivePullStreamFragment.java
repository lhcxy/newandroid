package com.yunbao.phonelive.fragment;

import android.graphics.Rect;
import android.view.View;
import android.widget.RelativeLayout;

import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.player.KSYTextureView;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ScreenDimenUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import java.io.IOException;

/**
 * Created by cxf on 2017/8/31.
 * 观众直播间拉流的fragment,使用金山云播视频播放器
 */

public class LivePullStreamFragment extends PullStreamPlayer {

    private KSYTextureView mTextureView;
    private Rect mContentViewRect;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_pull_stream;
    }

    @Override
    protected void main() {
        mTextureView = (KSYTextureView) mRootView.findViewById(R.id.player);
        mTextureView.setKeepScreenOn(true);
        mTextureView.setOnPreparedListener(mOnPreparedListener);
        mTextureView.setOnErrorListener(mOnErrorListener);
        mTextureView.setOnInfoListener(mOnInfoListener);
        mTextureView.setScreenOnWhilePlaying(true);
        mTextureView.setTimeout(5000, 5000);
        mTextureView.setVolume(1f, 1f);
        mTextureView.setLooping(true);//循环播放
        mTextureView.setDecodeMode(KSYMediaPlayer.KSYDecodeMode.KSY_DECODE_MODE_AUTO);
        mTextureView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        mTextureView.setBufferTimeMax(5.0f);
//        mTextureView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
//        mTextureView.setRotateDegree(90);
        mLoading = mRootView.findViewById(R.id.loading);
        mContentViewRect = ScreenDimenUtil.getInstance().getContentViewDimens();
    }

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(IMediaPlayer mp) {
            float videoWidth = mp.getVideoWidth();
            float videoHeight = mp.getVideoHeight();
            float rate = videoWidth / videoHeight;
            if (rate > ((float) mContentViewRect.width()) / mContentViewRect.height()) {//视频的宽高比大于屏幕的宽高比，要重新布局
                L.e("播放器", "视频的宽高比大于屏幕的宽高比，要重新布局");
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTextureView.getLayoutParams();
                params.width = mContentViewRect.width();
                params.height = (int) (mContentViewRect.width() / rate);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                mTextureView.setLayoutParams(params);
                mTextureView.requestLayout();
            }
            mStarted = true;
            if (!mDestoryed) {
                mTextureView.start();
                ((LiveAudienceActivity) getActivity()).videoLoadSucess();
                ((LiveAudienceActivity) getActivity()).hideBackgroundImage();
            } else {
                mTextureView.stop();
                mTextureView.reset();
                mTextureView.release();
                mTextureView.setOnPreparedListener(null);
                mTextureView.setOnErrorListener(null);
                mTextureView.setOnInfoListener(null);
            }
        }
    };

    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int info, int extra) {
            if (mLoading == null) {
                return false;
            }
            switch (info) {
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_START://缓冲开始
                    ToastUtil.show(WordUtil.getString(R.string.net_work_error));
                    mLoading.setVisibility(View.VISIBLE);
                    break;
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_END://缓冲结束
                    mLoading.setVisibility(View.GONE);
                    break;
                case IMediaPlayer.MEDIA_INFO_RELOADED:// reload成功后会有消息回调
                    if (mReLoadCount > 0) {
                        ToastUtil.show(WordUtil.getString(R.string.reConn_success));
                    }
                    mReLoadCount = 0;
                    break;
            }
            return false;
        }
    };


    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            L.e("OnErrorListener--->" + what);
            switch (what) {
                //网络较差播放过程中触发设置超时时间，报错退出
                case -10011://播放http+mp4点播流，弱网环境
                case -10002://播放http(mp4/flv/hls)点播流，无效wifi环境,连接无效wifi，播放rtmp直播流
                case -10004://播放rtmp直播流，弱网环境
                case -1004://播播http+flv点播流，播放过程中断网
                    if (mTextureView != null && mReLoadCount < 3) {
                        mTextureView.reload(mStreamUrl, false);
                        mReLoadCount++;
                        ToastUtil.show(mContext.getString(R.string.video_conn_error) + mReLoadCount + mContext.getString(R.string.video_reConn));
                    } else {
                        ToastUtil.show(mContext.getString(R.string.reConn_failure));
                    }
                    break;
                case -10007:
                case -10008://播放无效的http地址,超时设置足够长
                case 1:
                    ToastUtil.show(mContext.getString(R.string.mp4_error));
                    break;
            }
            return false;
        }
    };


    public void play(String streamUrl) {
        if (mTextureView == null) {
            return;
        }
        mStreamUrl = streamUrl;
        //mStreamUrl = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
        if (mStarted) {
            //用这种办法播放新视频
            mTextureView.reload(mStreamUrl, true, KSYMediaPlayer.KSYReloadMode.KSY_RELOAD_MODE_FAST);
        }
        try {
            mTextureView.setDataSource(mStreamUrl);

            mTextureView.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTextureView != null && !mPaused) {
            mPaused = true;
            if (!mPausePlay) {
                mTextureView.runInBackground(false);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mTextureView != null && mPaused) {
            mPaused = false;
            if (!mPausePlay) {
                mTextureView.runInForeground();
                mTextureView.start();
            }
        }
    }

    @Override
    public void pausePlay() {
        if (mTextureView != null && !mPausePlay) {
            mPausePlay = true;
            mTextureView.runInBackground(false);
        }
    }

    @Override
    public void resumePlay() {
        if (mTextureView != null && mPausePlay) {
            mPausePlay = false;
            mTextureView.runInForeground();
            mTextureView.start();
        }
    }

    @Override
    public void onDestroy() {
        mDestoryed = true;
        release();
        super.onDestroy();
    }

    @Override
    public void release() {
        if (mStarted) {
            mTextureView.stop();
            mTextureView.reset();
            mTextureView.release();
            mTextureView.setOnPreparedListener(null);
            mTextureView.setOnErrorListener(null);
            mTextureView.setOnInfoListener(null);
        }
    }

}
