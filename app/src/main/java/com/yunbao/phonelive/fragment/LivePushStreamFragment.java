package com.yunbao.phonelive.fragment;

import android.hardware.Camera;
import android.opengl.GLSurfaceView;

import com.ksyun.media.streamer.encoder.VideoEncodeFormat;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautyProFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautySmoothFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautySpecialEffectsFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgFilterBase;
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilterMgt;
import com.ksyun.media.streamer.framework.AVConst;
import com.ksyun.media.streamer.kit.KSYStreamer;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.ksyun.media.streamer.logstats.StatsLogReport;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAnchorActivity;
import com.yunbao.phonelive.activity.LiveAnchorHorizontalActivity;
import com.yunbao.phonelive.event.CameraEvent;
import com.yunbao.phonelive.event.CameraZoomEvent;
import com.yunbao.phonelive.event.FlashEvent;
import com.yunbao.phonelive.event.LivingBeautyEvent;
import com.yunbao.phonelive.event.LivingEvent;
import com.yunbao.phonelive.event.NetKBEvent;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.utils.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by cxf on 2017/8/31.
 * 主播直播间推流的fragment
 */

public class LivePushStreamFragment extends AbsFragment {
    private static final int CAP_RESOLUTION = StreamerConstants.VIDEO_RESOLUTION_360P;//采集分辨率
    //    private static final int CAP_RESOLUTION = StreamerConstants.VIDEO_RESOLUTION_480P;//采集分辨率
    private static final int PREVIEW_RESOLUTION = StreamerConstants.VIDEO_RESOLUTION_360P;//预览分辨率
    private static final int VIDEO_RESOLUTION = StreamerConstants.VIDEO_RESOLUTION_360P;//推流分辨率
    private static final int ENCODE_TYPE = AVConst.CODEC_ID_AVC;//H264
    private static final int ENCODE_METHOD = StreamerConstants.ENCODE_METHOD_SOFTWARE;//软编
    private static final int ENCODE_SCENE = VideoEncodeFormat.ENCODE_SCENE_SHOWSELF;//秀场模式
    private static final int ENCODE_PROFILE = VideoEncodeFormat.ENCODE_PROFILE_LOW_POWER;//低功耗
    private static final int FRAME_RATE = 15;//采集帧率
    private static final int VIDEO_BITRATE = 800;//视频码率
    private static final int AUDIO_BITRATE = 48;//音频码率
    private GLSurfaceView mCameraPreView;//预览的控件
    private KSYStreamer mStreamer;//金山推流器
    private String mStreamUrl;//推流的地址
    private boolean mStarted;//是否推流成功了
    private boolean mPaused;//是否在推流中切后台了
    private boolean mStoped;//是否停止了推流
    private ImgFilterBase mFilter;//美颜 滤镜
    private float mMopiVal;//磨皮
    private float mMeibaiVal;//美白
    private float mHongRunVal;//红润
    //    private MusicPlayer mMusicPlayer;
    private Disposable subscribe;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_push_stream;
    }

    @Override
    protected void main() {
        EventBus.getDefault().register(this);
        mCameraPreView = mRootView.findViewById(R.id.camera_preview);
        mStreamUrl = getArguments().getString("streamUrl");
        orientation = getArguments().getInt("orientation");
        cameraType = getArguments().getInt("cameraType");

        mStreamer = new KSYStreamer(mContext);
        mStreamer.setUrl(mStreamUrl);//推流地址
        mStreamer.setPreviewFps(FRAME_RATE);//预览采集帧率
        mStreamer.setTargetFps(FRAME_RATE);//推流采集帧率
        mStreamer.setVideoKBitrate(VIDEO_BITRATE * 3 / 4, VIDEO_BITRATE, VIDEO_BITRATE / 4);//视频码率
        mStreamer.setAudioKBitrate(AUDIO_BITRATE);//音频码率
//        mStreamer.setCameraCaptureResolution(CAP_RESOLUTION);//采集分辨率
        mStreamer.setPreviewResolution(PREVIEW_RESOLUTION);//预览分辨率
        mStreamer.setTargetResolution(VIDEO_RESOLUTION);//推流分辨率
//        mStreamer.setVideoCodecId(ENCODE_TYPE);//H264
        mStreamer.setEncodeMethod(ENCODE_METHOD);//软编
//        mStreamer.setVideoEncodeScene(ENCODE_SCENE);//秀场模式
        mStreamer.setVideoEncodeProfile(VideoEncodeFormat.ENCODE_PROFILE_HIGH_PERFORMANCE);
        mStreamer.setAudioChannels(2);//双声道推流
        mStreamer.setEnableRepeatLastFrame(false);  // 切后台的时候不使用最后一帧
        mStreamer.setEnableAutoRestart(true, 3000); // 自动重启推流
        mStreamer.setFrontCameraMirror(true);
        mStreamer.setOnInfoListener(mOnInfoListener);
        mStreamer.setOnErrorListener(mOnErrorListener);
        mStreamer.setOnLogEventListener(mOnLogEventListener);
        mStreamer.setDisplayPreview(mCameraPreView);
        mStreamer.setRotateDegrees(orientation == 0 ? 0 : 90);
        mStreamer.setCameraFacing(cameraType);
//        if (getActivity() instanceof LiveAnchorActivity) {
//            ((LiveAnchorActivity) getActivity()).enableFlash(cameraType == 1);
//        }
        mStreamer.startCameraPreview();//启动预览
        mStreamer.startStream();
        filter = new ImgBeautySmoothFilter(mStreamer.getGLRender(), getContext());
        //金山自带的美颜
        setDefaultFilter();
        EventBus.getDefault().post(new LivingEvent(0));
        getUploadKB();

    }

    //    Camera.Parameters parameters;
    int maxZoom = 0;
    private int currentZoom = 1;

    public int getMaxCameraZoom() {
        if (mStreamer != null) {
            if (maxZoom == 0) {
                if (mStreamer != null && mStreamer.getCameraCapture() != null && mStreamer.getCameraCapture().getCameraParameters() != null) {
                    try {
                        maxZoom = mStreamer.getCameraCapture().getCameraParameters().getMaxZoom();
                    } catch (Exception e) {
                    }
                }
            }
            return maxZoom;
        } else {
            return maxZoom;
        }
    }

    public void setCameraZoom(int cameraZoom) {
        if (mStreamer != null) {
            mStreamer.getCameraCapture().getCameraParameters().setZoom(cameraZoom);
        }
    }

    int orientation = 0;

    public void setStreamerOrientation(int orientation) {
        this.orientation = orientation;
        if (mStreamer != null) {
            mStreamer.setRotateDegrees(orientation);
        }
    }

    int cameraType = 1;

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
        if (mStreamer != null) {
            mStreamer.setCameraFacing(cameraType);
        }
    }

    private KSYStreamer.OnInfoListener mOnInfoListener = new KSYStreamer.OnInfoListener() {
        @Override
        public void onInfo(int what, int msg1, int msg2) {
            switch (what) {
                case 1000://初始化完毕
                    L.e("mStearm--->初始化完毕");
                    break;
                case 0://推流成功
                    L.e("mStearm--->推流成功");
                    if (mContext instanceof LiveAnchorActivity) {
                        ((LiveAnchorActivity) mContext).changeLive();
                    } else if (mContext instanceof LiveAnchorHorizontalActivity) {
                        ((LiveAnchorHorizontalActivity) mContext).changeLive();
                    }
                    mStarted = true;
                    break;
            }
        }
    };

    private KSYStreamer.OnErrorListener mOnErrorListener = new KSYStreamer.OnErrorListener() {
        @Override
        public void onError(int what, int msg1, int msg2) {
            switch (what) {
                case -1009://推流url域名解析失败
                    L.e("mStearm--->推流url域名解析失败");
                    break;
                case -1006://网络连接失败，无法建立连接
                    L.e("mStearm--->网络连接失败，无法建立连接");
                    break;
                case -1010://跟RTMP服务器完成握手后,推流失败
                    L.e("mStearm--->跟RTMP服务器完成握手后,推流失败");
                    break;
                case -1007://网络连接断开
                    L.e("mStearm--->网络连接断开");
                    break;
                case -2004://音视频采集pts差值超过5s
                    L.e("mStearm--->音视频采集pts差值超过5s");
                    break;
                case -1004://编码器初始化失败
                    L.e("mStearm--->编码器初始化失败");
                    break;
                case -1003://视频编码失败
                    L.e("mStearm--->视频编码失败");
                    break;
                case -1008://音频初始化失败
                    L.e("mStearm--->音频初始化失败");
                    break;
                case -1011://音频编码失败
                    L.e("mStearm--->音频编码失败");
                    break;
                case -2001: //摄像头未知错误
                    L.e("mStearm--->摄像头未知错误");
                    break;
                case -2002://打开摄像头失败
                    L.e("mStearm--->打开摄像头失败");
                    break;
                case -2003://录音开启失败
                    L.e("mStearm--->录音开启失败");
                    break;
                case -2005://录音开启未知错误
                    L.e("mStearm--->录音开启未知错误");
                    break;
                case -2006://系统Camera服务进程退出
                    L.e("mStearm--->系统Camera服务进程退出");
                    break;
                case -2007://Camera服务异常退出
                    L.e("mStearm--->Camera服务异常退出");
                    break;
            }
            switch (what) {
                case -2001:
                case -2002:
                case -2006:
                case -2007:
                    mStreamer.stopCameraPreview();
                    break;
            }
        }
    };

    private StatsLogReport.OnLogEventListener mOnLogEventListener = new StatsLogReport.OnLogEventListener() {
        @Override
        public void onLogEvent(StringBuilder singleLogContent) {
            //打印推流信息
            //L.e("mStearm--->" + singleLogContent.toString());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (mStoped) {
            return;
        }
        if (mPaused) {
            mPaused = false;
            mStreamer.onResume();
            SocketUtil.getInstance().sendSystemMessage(mContext.getString(R.string.anchor_come_back));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mStoped) {
            return;
        }
        if (mStarted) {
            mStreamer.onPause();
            // 切后台时，将SDK设置为离屏推流模式，继续采集camera数据
            mStreamer.setOffscreenPreview(mStreamer.getPreviewWidth(), mStreamer.getPreviewHeight());
            mPaused = true;
        }
        SocketUtil.getInstance().sendSystemMessage(mContext.getString(R.string.anchor_leave));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopPushStream();
    }

    /**
     * 停止推流
     */
    public void stopPushStream() {
        EventBus.getDefault().post(new LivingEvent(1));
        if (mStoped) {
            return;
        }
        if (subscribe != null) {
            subscribe.dispose();
            subscribe = null;
        }
        mStoped = true;
        if (mStreamer != null) {
            mStreamer.stopStream();
            mStreamer.stopCameraPreview();
            mStreamer.release();
            mStreamer.setOnInfoListener(null);
            mStreamer.setOnErrorListener(null);
            mStreamer.setOnLogEventListener(null);
            mStreamer = null;
        }
        mFilter = null;
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (mStreamer != null) {
            mStreamer.switchCamera();
            if (getActivity() instanceof LiveAnchorActivity) {
//                ((LiveAnchorActivity) getActivity()).enableFlash(mStreamer.getCameraFacing() == 1);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeCameraZoom(CameraZoomEvent event) {
        if (event != null && mStreamer != null && mStreamer.getCameraCapture() != null && null != mStreamer.getCameraCapture().getCameraParameters()) {
            Camera.Parameters cameraParameters = mStreamer.getCameraCapture().getCameraParameters();
            int zoomStep = cameraParameters.getMaxZoom() / 5;
            int zoom = event.getZoom();
            cameraParameters.setZoom(zoom == 1 ? 0 : zoom * zoomStep);
            mStreamer.getCameraCapture().setCameraParameters(cameraParameters);
        }
    }


    private boolean mIsFlashOpened = false;

    /**
     * 打开闪光灯
     */
    public void toggleFlash() {
        if (mStreamer != null) {
            if (mIsFlashOpened) {
                // 关闭闪光灯
                mStreamer.toggleTorch(false);
                mIsFlashOpened = false;
            } else {
                // 开启闪光灯
                mStreamer.toggleTorch(true);
                mIsFlashOpened = true;
            }
        }

    }

    /*********************金山自带的美颜*******************/

    /**
     * 设置默认滤镜
     */
    public void setDefaultFilter() {
        //设置美颜模式
        mStreamer.getImgTexFilterMgt().setFilter(mStreamer.getGLRender(), ImgTexFilterMgt.KSY_FILTER_BEAUTY_PRO3);
        if (mFilter == null) {
            List<ImgFilterBase> filters = mStreamer.getImgTexFilterMgt().getFilter();
            if (filters != null && filters.size() > 0) {
                mFilter = filters.get(0);
                mMopiVal = mFilter.getGrindRatio();
                mMeibaiVal = mFilter.getWhitenRatio();
                mHongRunVal = mFilter.getRuddyRatio();
            }
        } else {
            List<ImgFilterBase> filters = mStreamer.getImgTexFilterMgt().getFilter();
            if (filters != null && filters.size() > 0) {
                mFilter = filters.get(0);
            }
            mFilter.setGrindRatio(mMopiVal);
            mFilter.setWhitenRatio(mMeibaiVal);
            mFilter.setRuddyRatio(mHongRunVal);
        }
    }

    /**
     * 创建特殊滤镜  等
     *
     * @param id 1 原图 2 清新 3 靓丽 4 甜美 5 怀旧
     */
    public void setSpecialFilter(int id) {
        if (id == 1) {
            setDefaultFilter();
            return;
        }
        int type = 0;
        switch (id) {
            case 2:
                type = ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_FRESHY;
                break;
            case 3:
                type = ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_BEAUTY;
                break;
            case 4:
                type = ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_SWEETY;
                break;
            case 5:
                type = ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_SEPIA;
                break;
        }
        ImgBeautySpecialEffectsFilter effectsFilter = new ImgBeautySpecialEffectsFilter(
                mStreamer.getGLRender(), mContext, type);
        mStreamer.getImgTexFilterMgt().setFilter(effectsFilter);
    }

    /**
     * 获取当前美颜的数值
     *
     * @return
     */
    public int[] getBeautyData() {
        int[] data = null;
        if (mFilter != null) {
            data = new int[3];
            data[0] = (int) (mMopiVal * 100);//磨皮
            data[1] = (int) (mMeibaiVal * 100);//美白
            data[2] = (int) (mHongRunVal * 100);//红润
        }
        return data;
    }

    /**
     * 设置美颜数值
     *
     * @param type 美颜的类型   0 磨皮 1 美白 2 红润
     * @param val  0~1 的float数值
     */
    public void setBeautyData(int type, float val) {
        switch (type) {
            case 0:
                mMopiVal = val;
                L.e("磨皮--->" + mMopiVal);
                mFilter.setGrindRatio(mMopiVal);
                break;
            case 1:
                mMeibaiVal = val;
                L.e("美白--->" + mMeibaiVal);
                mFilter.setWhitenRatio(mMeibaiVal);
                break;
            case 2:
                mHongRunVal = val;
                L.e("红润--->" + mHongRunVal);
                mFilter.setRuddyRatio(mHongRunVal);
                break;
        }

    }

    public void setOriginCameraPreView() {
        if (mStreamer != null && mCameraPreView != null) {
            mStreamer.onPause();
            mStreamer.setDisplayPreview((GLSurfaceView) null);
            mStreamer.setDisplayPreview(mCameraPreView);
            mStreamer.onResume();
        }
    }

    public void setCameraPreView(GLSurfaceView surfaceView) {
        if (mStreamer != null) {
            mStreamer.setDisplayPreview(surfaceView);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void choseCamera(CameraEvent event) {
        switchCamera();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setFlash(FlashEvent event) {
        toggleFlash();
    }

    ImgBeautySmoothFilter filter;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setEuteary(LivingBeautyEvent event) {

//        mStreamer.getImgTexFilterMgt().setFilter(mStreamer.getGLRender(),
//                ImgTexFilterMgt.KSY_FILTER_BEAUTY_PRO3);
//        ImgFilterBase filter = new ImgBeautySoftFilter(mStreamer.getGLRender());
//        mStreamer.getImgTexFilterMgt().replaceFilter(null, new ImgBeautySoftFilter(mStreamer.getGLRender()));
        if (isopen) {
            mStreamer.getImgTexFilterMgt().setFilter(new ImgBeautyProFilter(mStreamer.getGLRender(), getContext()));
        } else {
            mStreamer.getImgTexFilterMgt().setFilter(new ImgBeautyProFilter(mStreamer.getGLRender(), getContext(), 3));
        }
        isopen = !isopen;
    }

    private int beautyType = 0;

    private boolean isopen = false;

    public void setCapture() {
//        mStreamer.setCameraCaptureResolution();
        // set CameraHintView to show focus rect and zoom ratio
//        mCameraHintView.setVisibility(View.VISIBLE);
//        cameraTouchHelper.setCameraHintView(mCameraHintView);
//        mStreamer.setCameraCaptureResolution( mStreamer.getCameraCapture().getCameraDisplayOrientation());

    }

    public void getUploadKB() {
        subscribe = Observable.interval(1, 1, TimeUnit.SECONDS)
                .observeOn(Schedulers.newThread())
                .subscribe(aLong -> {
                    if (mStreamer != null) {
                        int uploadedKBytes = mStreamer.getRtmpPublisher().getCurrentUploadKBitrate();
                        EventBus.getDefault().post(new NetKBEvent(uploadedKBytes / 8));
                    }
                });

    }


    public void openFilter() {

    }


    public void setZoom(float zoom) {
        if (mStreamer != null && mStreamer.getCameraCapture() != null) {
            Camera.Parameters cameraParameters = mStreamer.getCameraCapture().getCameraParameters();
            if (cameraParameters != null) {
                cameraParameters.setZoom((int) zoom);
                mStreamer.getCameraCapture().setCameraParameters(cameraParameters);
            }
        }
    }
}
