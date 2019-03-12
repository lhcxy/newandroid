package com.yunbao.phonelive.ui.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.jakewharton.rxbinding2.view.RxView;
import com.ksyun.media.streamer.encoder.VideoEncodeFormat;
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilterBase;
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilterMgt;
import com.ksyun.media.streamer.kit.KSYStreamer;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.event.CameraEvent;
import com.yunbao.phonelive.event.FlashEvent;
import com.yunbao.phonelive.ui.views.fragment.PreLiveReadyFragment;
import com.yunbao.phonelive.ui.views.fragment.PreLiveSettingFragment;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.TimeUnit;


/**
 * Base streaming activity.
 */

public class LiveCameraActivity extends AbsActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String TAG = "BaseCameraActivity";

    protected static final int PERMISSION_REQUEST_CAMERA_AUDIOREC = 1;
    protected static final String START_STREAM = "开始直播";
    protected static final String STOP_STREAM = "停止直播";

    protected GLSurfaceView mGLSurfaceView;
    //    protected Chronometer mChronometer;
    protected ImageView mFlashView;

    protected BaseStreamConfig mConfig;
    protected boolean mIsLandscape;
    protected boolean mIsFlashOpened;
    protected boolean mStreaming;
    protected boolean mIsChronometerStarted;
    protected String mDebugInfo = "";
    protected boolean mHWEncoderUnsupported;
    protected boolean mSWEncoderUnsupported;

    protected KSYStreamer mStreamer;
    protected Handler mMainHandler;
    protected Timer mTimer;
    private FragmentManager fragmentManager;
    private PreLiveSettingFragment settingFragment;

//    protected String mSdcardPath = Environment.getExternalStorageDirectory().getPath();
    //    protected String mLogoPath = "file://" + mSdcardPath + "/test.png";
//    protected String mBgImagePath = "assets://bg.jpg";
//    private static String defJson = "{\"mAudioKBitrate\":48,\"mAutoStart\":false,\"mCameraFacing\":0,\"mEncodeMethod\":3,\"mFrameRate\":15.0,\"mOrientation\":1,\"mShowDebugInfo\":true,\"mTargetResolution\":0,\"mUrl\":\"rtmp://29865.livepush2.myqcloud.com/live/29865_12447_1536129797?bizid\\u003d29865\\u0026txSecret\\u003d8904e867f4c7e25cdae675fc2fc59552\\u0026txTime\\u003d5b8fa535\",\"mVideoKBitrate\":800}";

    public static class BaseStreamConfig {
        public String mUrl = "";
        public int mCameraFacing = 1;
        public float mFrameRate = 15.0f;
        public int mVideoKBitrate = 800;
        public int mAudioKBitrate = 48;
        public int mTargetResolution = 0;
        public int mOrientation = 1;
        public int mEncodeMethod = 3;
        public boolean mAutoStart = false;
        public boolean mShowDebugInfo = true;

        public BaseStreamConfig fromJson(String json) {
            return new GsonBuilder().create().fromJson(json, this.getClass());
        }

        public String toJson() {
            return new GsonBuilder().create().toJson(this);
        }
    }

    public static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("config", new BaseStreamConfig().toJson());
        context.startActivity(intent);
    }

    protected BaseStreamConfig getConfig(Bundle bundle) {
//        return new BaseStreamConfig().fromJson(bundle.getString("config"));
        return new BaseStreamConfig();
    }

    @Override
    public void preOnCreate() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.preOnCreate();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_live_camera;
    }

    @Override
    protected void main() {
//        setContentView(getLayoutId());
        findViews();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 4.4以上系统，自动隐藏导航栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        mMainHandler = new Handler();
        mStreamer = new KSYStreamer(this);
        mConfig = getConfig(getIntent().getExtras());
        initUI();
        if (mConfig != null) {
            config();
        }
        enableBeautyFilter();
        showWaterMark();
        // 是否自动开始推流
        if (mConfig.mAutoStart) {
            startStream();
        }
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.live_camera_fl, new PreLiveReadyFragment());
        fragmentTransaction.commitNow();
        EventBus.getDefault().register(this);

    }

    public void backReadyView() {
        fragmentManager.popBackStack();
    }

    public void showSettingView() {
        if (settingFragment == null) {
            settingFragment = new PreLiveSettingFragment();
        }
        fragmentManager.beginTransaction().replace(R.id.live_camera_fl, settingFragment).addToBackStack(null).commitAllowingStateLoss();
    }


    protected void setDisplayPreview() {
        mStreamer.setDisplayPreview(mGLSurfaceView);
    }

    private void findViews() {
        mGLSurfaceView = findViewById(R.id.gl_surface_view);
        mFlashView = findViewById(R.id.flash);
        RxView.clicks(findViewById(R.id.start_stream_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> onStartStreamClick());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // 4.4以上系统，自动隐藏导航栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    protected void initUI() {
        // empty here
    }

    protected void config() {
        // 设置推流URL地址
        if (!TextUtils.isEmpty(mConfig.mUrl)) {
            mStreamer.setUrl(mConfig.mUrl);
        }
        // 设置推流分辨率
        mStreamer.setPreviewResolution(mConfig.mTargetResolution);
        mStreamer.setTargetResolution(mConfig.mTargetResolution);
        // 设置编码方式（硬编、软编）
        mStreamer.setEncodeMethod(mConfig.mEncodeMethod);
        // 硬编模式下默认使用高性能模式(high profile)
        if (mConfig.mEncodeMethod == StreamerConstants.ENCODE_METHOD_HARDWARE) {
            mStreamer.setVideoEncodeProfile(VideoEncodeFormat.ENCODE_PROFILE_HIGH_PERFORMANCE);
        }
        // 设置推流帧率
        if (mConfig.mFrameRate > 0) {
            mStreamer.setPreviewFps(mConfig.mFrameRate);
            mStreamer.setTargetFps(mConfig.mFrameRate);
        }
        // 设置推流视频码率，三个参数分别为初始码率、最高码率、最低码率
        int videoBitrate = mConfig.mVideoKBitrate;
        if (videoBitrate > 0) {
            mStreamer.setVideoKBitrate(videoBitrate * 3 / 4, videoBitrate, videoBitrate / 4);
        }
        // 设置音频码率
        if (mConfig.mAudioKBitrate > 0) {
            mStreamer.setAudioKBitrate(mConfig.mAudioKBitrate);
        }
        // 设置视频方向（横屏、竖屏）
        if (mConfig.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mIsLandscape = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mStreamer.setRotateDegrees(90);
        } else if (mConfig.mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mIsLandscape = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mStreamer.setRotateDegrees(0);
        }

        // 选择前后摄像头
        mStreamer.setCameraFacing(mConfig.mCameraFacing);

        // 设置预览View
        setDisplayPreview();
        // 设置回调处理函数
        mStreamer.setOnInfoListener(mOnInfoListener);
        mStreamer.setOnErrorListener(mOnErrorListener);
        // 禁用后台推流时重复最后一帧的逻辑（这里我们选择切后台使用背景图推流的方式）
        mStreamer.setEnableRepeatLastFrame(false);
    }

    protected void handleOnResume() {
        // 调用KSYStreamer的onResume接口
        mStreamer.onResume();
        // 停止背景图采集
        mStreamer.stopImageCapture();
        // 开启摄像头采集
        startCameraPreviewWithPermCheck();
        // 如果onPause中切到了DummyAudio模块，可以在此恢复
        mStreamer.setUseDummyAudioCapture(false);
    }

    protected void handleOnPause() {
        // 调用KSYStreamer的onPause接口
        mStreamer.onPause();
        // 停止摄像头采集，然后开启背景图采集，以实现后台背景图推流功能
        mStreamer.stopCameraPreview();
//        mStreamer.startImageCapture(mBgImagePath);
        // 如果希望App切后台后，停止录制主播端的声音，可以在此切换为DummyAudio采集，
        // 该模块会代替mic采集模块产生静音数据，同时释放占用的mic资源
        mStreamer.setUseDummyAudioCapture(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handleOnPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理相关资源
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
        mStreamer.release();
    }

    //start streaming
    protected void startStream() {
        mStreamer.startStream();
        mStreaming = true;
    }

    // stop streaming
    protected void stopStream() {
        mStreamer.stopStream();
        mStreaming = false;
    }

    /**
     * 添加水印
     */
    protected void showWaterMark() {
//        if (!mIsLandscape) {
//            mStreamer.showWaterMarkLogo(mLogoPath, 0.08f, 0.04f, 0.20f, 0, 0.8f);
//            mStreamer.showWaterMarkTime(0.03f, 0.01f, 0.35f, Color.WHITE, 1.0f);
//        } else {
//            mStreamer.showWaterMarkLogo(mLogoPath, 0.05f, 0.09f, 0, 0.20f, 0.8f);
//            mStreamer.showWaterMarkTime(0.01f, 0.03f, 0.22f, Color.WHITE, 1.0f);
//        }
    }

    protected void hideWaterMark() {
        mStreamer.hideWaterMarkLogo();
        mStreamer.hideWaterMarkTime();
    }

    protected void enableBeautyFilter() {
        // 设置美颜滤镜的错误回调，当前机型不支持该滤镜时禁用美颜
        mStreamer.getImgTexFilterMgt().setOnErrorListener(new ImgTexFilterBase.OnErrorListener() {
            @Override
            public void onError(ImgTexFilterBase filter, int errno) {
                Toast.makeText(getApplicationContext(), "当前机型不支持该滤镜",
                        Toast.LENGTH_SHORT).show();
                mStreamer.getImgTexFilterMgt().setFilter(mStreamer.getGLRender(),
                        ImgTexFilterMgt.KSY_FILTER_BEAUTY_DISABLE);
            }
        });
        // 设置美颜滤镜，关于美颜滤镜的具体说明请参见专题说明以及完整版demo
        mStreamer.getImgTexFilterMgt().setFilter(mStreamer.getGLRender(),
                ImgTexFilterMgt.KSY_FILTER_BEAUTY_PRO3);
    }

    // update debug info
    private void updateDebugInfo() {
        if (mStreamer == null) return;
        String encodeMethod;
        switch (mStreamer.getVideoEncodeMethod()) {
            case StreamerConstants.ENCODE_METHOD_HARDWARE:
                encodeMethod = "HW";
                break;
            case StreamerConstants.ENCODE_METHOD_SOFTWARE:
                encodeMethod = "SW";
                break;
            default:
                encodeMethod = "SW1";
                break;
        }
        mDebugInfo = String.format(Locale.getDefault(), " " +
                        "EncodeMethod=%s PreviewFps=%.2f \n " +
                        "RtmpHostIP()=%s DroppedFrameCount()=%d \n " +
                        "ConnectTime()=%dms DnsParseTime()=%dms \n " +
                        "UploadedKB()=%d EncodedFrames()=%d \n " +
                        "CurrentKBitrate=%d Version()=%s",
                encodeMethod, mStreamer.getCurrentPreviewFps(),
                mStreamer.getRtmpHostIP(), mStreamer.getDroppedFrameCount(),
                mStreamer.getConnectTime(), mStreamer.getDnsParseTime(),
                mStreamer.getUploadedKBytes(), mStreamer.getEncodedFrames(),
                mStreamer.getCurrentUploadKBitrate(), KSYStreamer.getVersion());

    }

    protected void onStartStreamClick() {
        mConfig.mOrientation = mConfig.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        if (mConfig.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mIsLandscape = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mStreamer.setRotateDegrees(0);
        } else if (mConfig.mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mIsLandscape = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mStreamer.setRotateDegrees(90);
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchCamera(CameraEvent event) {
        Log.e(TAG, "onSwitchCamera: ");
        // 切换前后摄像头
        mStreamer.switchCamera();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onFlashClick(FlashEvent event) {
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

    protected void onStreamerInfo(int what, int msg1, int msg2) {
        Log.d(TAG, "OnInfo: " + what + " msg1: " + msg1 + " msg2: " + msg2);
        switch (what) {
            case StreamerConstants.KSY_STREAMER_CAMERA_INIT_DONE:
                Log.d(TAG, "KSY_STREAMER_CAMERA_INIT_DONE");
                break;
            case StreamerConstants.KSY_STREAMER_CAMERA_FACING_CHANGED:
                Log.d(TAG, "KSY_STREAMER_CAMERA_FACING_CHANGED");
                // check is flash torch mode supported
                mFlashView.setEnabled(mStreamer.getCameraCapture().isTorchSupported());
                break;
            case StreamerConstants.KSY_STREAMER_OPEN_STREAM_SUCCESS:
                Log.d(TAG, "KSY_STREAMER_OPEN_STREAM_SUCCESS");
                break;
            case StreamerConstants.KSY_STREAMER_FRAME_SEND_SLOW:
                Log.d(TAG, "KSY_STREAMER_FRAME_SEND_SLOW " + msg1 + "ms");
//                Toast.makeText(getApplicationContext(), "",
//                        Toast.LENGTH_SHORT).show();
                break;
            case StreamerConstants.KSY_STREAMER_EST_BW_RAISE:
                Log.d(TAG, "BW raise to " + msg1 / 1000 + "kbps");
                break;
            case StreamerConstants.KSY_STREAMER_EST_BW_DROP:
                Log.d(TAG, "BW drop to " + msg1 / 1000 + "kpbs");
                break;
            default:
                break;
        }
    }

    protected void onStreamerError(int what, int msg1, int msg2) {
        Log.e(TAG, "streaming error: what=" + what + " msg1=" + msg1 + " msg2=" + msg2);
        switch (what) {
            case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
            case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                break;
            case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
            case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
            case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_EVICTED:
            case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                mStreamer.stopCameraPreview();
                break;
            case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED:
            case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN:
                handleEncodeError();
            default:
                reStreaming(what);
                break;
        }
    }

    protected void reStreaming(int err) {
        stopStream();
        mMainHandler.postDelayed(() -> startStream(), 3000);
    }

    protected void handleEncodeError() {
        int encodeMethod = mStreamer.getVideoEncodeMethod();
        if (encodeMethod == StreamerConstants.ENCODE_METHOD_HARDWARE) {
            mHWEncoderUnsupported = true;
            if (mSWEncoderUnsupported) {
                mStreamer.setEncodeMethod(
                        StreamerConstants.ENCODE_METHOD_SOFTWARE_COMPAT);
                Log.e(TAG, "Got HW encoder error, switch to SOFTWARE_COMPAT mode");
            } else {
                mStreamer.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE);
                Log.e(TAG, "Got HW encoder error, switch to SOFTWARE mode");
            }
        } else if (encodeMethod == StreamerConstants.ENCODE_METHOD_SOFTWARE) {
            mSWEncoderUnsupported = true;
            mStreamer.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE_COMPAT);
            Log.e(TAG, "Got SW encoder error, switch to SOFTWARE_COMPAT mode");
        }
    }

    private KSYStreamer.OnInfoListener mOnInfoListener = new KSYStreamer.OnInfoListener() {
        @Override
        public void onInfo(int what, int msg1, int msg2) {
            onStreamerInfo(what, msg1, msg2);
        }
    };

    private KSYStreamer.OnErrorListener mOnErrorListener = new KSYStreamer.OnErrorListener() {
        @Override
        public void onError(int what, int msg1, int msg2) {
            onStreamerError(what, msg1, msg2);
        }
    };

    protected void startCameraPreviewWithPermCheck() {
        int cameraPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int audioPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (cameraPerm != PackageManager.PERMISSION_GRANTED ||
                audioPerm != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Log.e(TAG, "No CAMERA or AudioRecord permission, please check");
                ToastUtil.show("没有权限 无法开播");
            } else {
                String[] permissions = {Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO};
                ActivityCompat.requestPermissions(this, permissions,
                        PERMISSION_REQUEST_CAMERA_AUDIOREC);
            }
        } else {
            mStreamer.startCameraPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA_AUDIOREC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStreamer.startCameraPreview();
                } else {
                    Log.e(TAG, "No CAMERA or AudioRecord permission");
                    ToastUtil.show("未获得设备权限无法开播!");
                    finish();
                }
                break;
            }
        }
    }


}
