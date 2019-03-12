package com.yunbao.phonelive.ui.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.player.KSYTextureView;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.ui.views.video.FloatingPlayer;

import java.io.IOException;

public class VodDisplayActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ImageView back, full_screen;
    private RelativeLayout panel, content;
    private RelativeLayout video;
    private int video_height;
    private RelativeLayout landscape_content, portrait_content;
    private RelativeLayout landscape_top_panel, landscape_bottom_panel;
    private ImageView back_landscape;
    private TextView video_name_landscape;
    private TextView clarity_landscape;
    private Boolean toFloatingWindow = false;
    private Boolean comeBackFromRestart = false;
    private Boolean isPalying = true;
    private long quit_time;
    private int clarityId;
    //0 横屏播放 1 竖屏播放
    private int currentState;

    private RelativeLayout  clarity_content;

    private Boolean isPanelShowing_Portrait = true;
    private Boolean isPanelShowing_Landscape = true;
    private Boolean mTouching;
    private Context context;
    private SharedPreferences mSettings;
    private SharedPreferences.Editor editor;
    private String mDataSource="http://p3gl6e2wo.bkt.clouddn.com/18.3.mp4";
    private String playingTitle = "";
    private Handler mHandler;



    //清晰度
    private RadioGroup clarity;
    private RadioButton clarity_super, clarity_high, clarity_normal;
    private Boolean isChangingClarity = false;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            FloatingPlayer.getInstance().getKSYTextureView().setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            Log.i("buffer", "视频加载结束");
            if (comeBackFromRestart) {
                FloatingPlayer.getInstance().getKSYTextureView().seekTo(quit_time);
                FloatingPlayer.getInstance().getKSYTextureView().start();
                if (!isPalying) {
                    FloatingPlayer.getInstance().getKSYTextureView().pause();
                }
                isPalying = true;
                comeBackFromRestart = false;
            } else if (isChangingClarity) {
                FloatingPlayer.getInstance().getKSYTextureView().start();
                isChangingClarity = false;
            } else {
                FloatingPlayer.getInstance().getKSYTextureView().start();
            }
//            Dialog.dismiss();

        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            Toast.makeText(VodDisplayActivity.this, "播放器遇到错误，播放已退出，错误码:" + i, Toast.LENGTH_SHORT).show();
            videoPlayEnd();
            return false;
        }
    };

    private void videoPlayEnd() {
        if (FloatingPlayer.getInstance().getKSYTextureView() != null) {
            FloatingPlayer.getInstance().destroy();
        }
        editor.putBoolean("isPlaying", false);
        editor.commit();
    }
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            KSYTextureView mVideoView = FloatingPlayer.getInstance().getKSYTextureView();
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mTouching = false;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mTouching = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (!mTouching) {
                        dealTouchEvent();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (landscape_content.getVisibility() == View.VISIBLE) {
                        landscape_content.setVisibility(View.GONE);
                        hideStatusBar();
                        isPanelShowing_Landscape = false;
                    }
                }
            }, 3000);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(params);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            showLandscapePanel();
            ViewTreeObserver vto2 = content.getViewTreeObserver();
            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            currentState = 1;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            showPortraitPanel();
            currentState = 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.include_video_view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mHandler = new Handler();
        context = this.getApplicationContext();
        mSettings = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        editor = mSettings.edit();
        currentState = 0;
        initData();
        initView();
        if (FloatingPlayer.getInstance().getKSYTextureView() == null) {
            startToPlay();
        } else {
            resumeToPlay();
        }

    }

    private void initData() {
//        videoList = (List<VodBean.DataBean.DetailBean>) getIntent().getSerializableExtra("videoList");
//        playingId = getIntent().getIntExtra("playingId", 0);
//        if (playingId >= 0)
//            playingTitle = videoList.get(playingId).getVideoTitle();
//        else
//        mDataSource = getIntent().getStringExtra(Ids.PLAY_URL);
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.vod_display_back_portrait);
        back.setOnClickListener(this);
        full_screen = (ImageView) findViewById(R.id.vod_display_full_screen);
        full_screen.setOnClickListener(this);
        panel = (RelativeLayout) findViewById(R.id.vod_controller_bar);
        panel.setOnClickListener(this);
        video = (RelativeLayout) findViewById(R.id.vod_main_video);
        content = (RelativeLayout) findViewById(R.id.vod_content);
        //保存横屏是播放器的高度
        ViewTreeObserver vto2 = content.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                video_height = content.getHeight();
            }
        });

        landscape_content = (RelativeLayout) findViewById(R.id.landscape_controller);
        portrait_content = (RelativeLayout) findViewById(R.id.portrait_controller);
        landscape_top_panel = (RelativeLayout) findViewById(R.id.landscape_top_panel);
        landscape_top_panel.setOnClickListener(this);
        landscape_bottom_panel = (RelativeLayout) findViewById(R.id.landscape_bottom_panel);
        landscape_bottom_panel.setOnClickListener(this);
        back_landscape = (ImageView) findViewById(R.id.vod_display_back_landscape);
        back_landscape.setOnClickListener(this);
        video_name_landscape = (TextView) findViewById(R.id.video_name_landscape);
        video_name_landscape.setText(playingTitle);
        clarity_landscape = (TextView) findViewById(R.id.clarity_landscape);
        clarity_landscape.setOnClickListener(this);

        clarity_content = (RelativeLayout) findViewById(R.id.content_clarity);

        //清晰度
        clarity = (RadioGroup) findViewById(R.id.clarity);
        clarity_high = (RadioButton) findViewById(R.id.clarity_high);
        clarity_super = (RadioButton) findViewById(R.id.clarity_super);
        clarity_normal = (RadioButton) findViewById(R.id.clarity_normal);
        clarity.setOnCheckedChangeListener(this);
        changeClarityId();
    }

    private void startToPlay() {
//            mDataSource = videoList.get(playingId).getPlayURL().get(clarityId).trim();
        FloatingPlayer.getInstance().init(context);
        video.addView(FloatingPlayer.getInstance().getKSYTextureView());
        FloatingPlayer.getInstance().getKSYTextureView().setOnPreparedListener(mOnPreparedListener);
        FloatingPlayer.getInstance().getKSYTextureView().setOnErrorListener(mOnErrorListener);
        FloatingPlayer.getInstance().getKSYTextureView().setOnTouchListener(mTouchListener);
        FloatingPlayer.getInstance().getKSYTextureView().setVolume(1.0f, 1.0f);
//        changeSettings();
        try {
            FloatingPlayer.getInstance().getKSYTextureView().setDataSource(mDataSource);
            FloatingPlayer.getInstance().getKSYTextureView().shouldAutoPlay(true);
            FloatingPlayer.getInstance().getKSYTextureView().prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.putBoolean("isPlaying", true);
        editor.commit();
    }

    private void resumeToPlay() {
        mSettings = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        editor = mSettings.edit();
//        changeSettings();
        video.addView(FloatingPlayer.getInstance().getKSYTextureView());
        FloatingPlayer.getInstance().getKSYTextureView().setOnPreparedListener(mOnPreparedListener);
        FloatingPlayer.getInstance().getKSYTextureView().setOnErrorListener(mOnErrorListener);
        FloatingPlayer.getInstance().getKSYTextureView().setOnTouchListener(mTouchListener);
        FloatingPlayer.getInstance().getKSYTextureView().setVisibility(View.VISIBLE);
        FloatingPlayer.getInstance().getKSYTextureView().setComeBackFromShare(true);
        editor.putBoolean("isPlaying", true);
        editor.commit();
    }

//    private void changeSettings() {
//        chooseDecode = mSettings.getString("choose_decode", "undefined");
//        bufferTime = mSettings.getInt("buffertime", 2);
//        bufferSize = mSettings.getInt("buffersize", 15);
//        prepareTimeout = mSettings.getInt("preparetimeout", 5);
//        readTimeout = mSettings.getInt("readtimeout", 30);
//        isLooping = mSettings.getBoolean("isLooping", false);
//        if (bufferTime > 0) {
//            FloatingPlayer.getInstance().getKSYTextureView().setBufferTimeMax(bufferTime);
//        }
//
//        if (bufferSize > 0) {
//            FloatingPlayer.getInstance().getKSYTextureView().setBufferSize(bufferSize);
//        }
//        if (prepareTimeout > 0 && readTimeout > 0) {
//            FloatingPlayer.getInstance().getKSYTextureView().setTimeout(prepareTimeout, readTimeout);
//        }
//        if (chooseDecode.equals(Setting.USEHARD)) {
//            useHwDecoder = true;
//        } else {
//            useHwDecoder = false;
//        }
//        FloatingPlayer.getInstance().getKSYTextureView().setLooping(isLooping);
//        if (useHwDecoder) {
//            if (KSYHardwareDecodeWhiteList.getInstance().getCurrentStatus() == KSYHardwareDecodeWhiteList.KSY_STATUS_OK) {
//                if (KSYHardwareDecodeWhiteList.getInstance().supportHardwareDecodeH264()
//                        || KSYHardwareDecodeWhiteList.getInstance().supportHardwareDecodeH265())
//                    FloatingPlayer.getInstance().getKSYTextureView().setDecodeMode(KSYMediaPlayer.KSYDecodeMode.KSY_DECODE_MODE_AUTO);
//            }
//        }
//        FloatingPlayer.getInstance().getKSYTextureView().setSpeed(mSettings.getFloat("times", 1.0f));
//    }

    //选择不同码率的视频源
    private void changeClarityId() {
//        if (playingId == 1 || playingId == 3 || playingId == 4) {
//            clarity_super.setVisibility(View.GONE);
//            if (mSettings.getString("clarity", Setting.CLARITY_HIGH).equals(Setting.CLARITY_SUPER)) {
//                editor.putString("clarity", Setting.CLARITY_HIGH);
//                editor.commit();
//            }
//        } else {
//            clarity_super.setVisibility(View.VISIBLE);
//        }
//        switch (mSettings.getString("clarity", Setting.CLARITY_HIGH)) {
//            case Setting.CLARITY_NORMAL:
//                clarityId = 2;
//                clarity.check(clarity_normal.getId());
//                clarity_landscape.setText("标清");
//                if (playingId == 1 || playingId == 3 || playingId == 4) {
//                    clarityId--;
//                }
//                break;
//            case Setting.CLARITY_HIGH:
//                clarityId = 1;
//                clarity.check(clarity_high.getId());
//                clarity_landscape.setText("高清");
//                if (playingId == 1 || playingId == 3 || playingId == 4) {
//                    clarityId--;
//                }
//                break;
//            case Setting.CLARITY_SUPER:
//                clarityId = 0;
//                clarity.check(clarity_super.getId());
//                clarity_landscape.setText("超清");
//                break;
//        }
    }

    //改变清晰度
    private void changeClarity() {
        isChangingClarity = true;
        changeClarityId();
        if (FloatingPlayer.getInstance().getKSYTextureView() != null) {
//            mDataSource = videoList.get(playingId).getPlayURL().get(clarityId).trim();
            editor.commit();
            FloatingPlayer.getInstance().getKSYTextureView().softReset();
//            changeSettings();
            try {
                FloatingPlayer.getInstance().getKSYTextureView().setDataSource(mDataSource);
                FloatingPlayer.getInstance().getKSYTextureView().shouldAutoPlay(false);
                FloatingPlayer.getInstance().getKSYTextureView().prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void dealTouchEvent() {
        if (portrait_content.getVisibility() == View.VISIBLE) {
            if (isPanelShowing_Portrait) {
                panel.setVisibility(View.GONE);
            } else {
                panel.setVisibility(View.VISIBLE);
            }
        } else {
            hideShade();
            showLandscapePanel();
            if (isPanelShowing_Landscape) {
                landscape_content.setVisibility(View.GONE);
                hideStatusBar();
            } else {
                landscape_content.setVisibility(View.VISIBLE);
                showStatusBar();
            }
        }
        isPanelShowing_Landscape = !isPanelShowing_Landscape;
        isPanelShowing_Portrait = !isPanelShowing_Portrait;
    }

    private void showLandscapePanel() {
        showStatusBar();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.MATCH_PARENT);
        content.setLayoutParams(layoutParams);
        video.setLayoutParams(videoParams);
        portrait_content.setVisibility(View.GONE);
        landscape_content.setVisibility(View.VISIBLE);
    }

    private void hideLandscapePanel() {
        hideStatusBar();
        landscape_content.setVisibility(View.GONE);
        isPanelShowing_Landscape = false;
    }

    private void showPortraitPanel() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, video_height);
        RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, video_height);
        content.setLayoutParams(layoutParams);
        video.setLayoutParams(videoParams);
        landscape_content.setVisibility(View.GONE);
        portrait_content.setVisibility(View.VISIBLE);
    }

    private void showStatusBar() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void hideStatusBar() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void hideShade() {
        clarity_content.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.vod_display_back_portrait) {
            toFloatingWindow = true;
            finish();
        } else if (id == R.id.vod_display_back_landscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (id == R.id.vod_display_full_screen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (id == R.id.clarity_landscape) {
            hideLandscapePanel();
            clarity_content.setVisibility(View.VISIBLE);
        } else if (id == R.id.vod_controller_bar || id == R.id.landscape_bottom_panel || id == R.id.landscape_top_panel) {
            return;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
//        //清晰度
//        if (i == R.id.clarity_normal) {
//            editor.putString("clarity", Setting.CLARITY_NORMAL);
//            clarity_landscape.setText("标清");
//        } else if (i == R.id.clarity_high) {
//            editor.putString("clarity", Setting.CLARITY_HIGH);
//            clarity_landscape.setText("高清");
//        } else if (i == R.id.clarity_super) {
//            editor.putString("clarity", Setting.CLARITY_SUPER);
//            clarity_landscape.setText("超清");
//        }
//        editor.commit();
//        if (clarity_content.getVisibility() == View.VISIBLE) {
//            if (playingId >= 0)
//                changeClarity();
//        }
//        if (FloatingPlayer.getInstance().getKSYTextureView() != null) {
//            FloatingPlayer.getInstance().getKSYTextureView().setSpeed(mSettings.getFloat("times", 1.0f));
//        }
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                hideShade();
//            }
//        }, 1000);
    }

    @Override
    public void onBackPressed() {
        if (currentState == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            toFloatingWindow = true;
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        comeBackFromRestart = true;
        startToPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (toFloatingWindow) {
            video.removeView(FloatingPlayer.getInstance().getKSYTextureView());
            FloatingPlayer.getInstance().getKSYTextureView().setOnTouchListener(null);
            FloatingPlayer.getInstance().getKSYTextureView().setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            if (!FloatingPlayer.getInstance().getKSYTextureView().isPlaying() && toFloatingWindow) {
                videoPlayEnd();
            }
        } else {
            if (FloatingPlayer.getInstance().getKSYTextureView().isPlaying()) {
                isPalying = true;
            } else {
                isPalying = false;
            }
            quit_time = FloatingPlayer.getInstance().getKSYTextureView().getCurrentPosition();
            FloatingPlayer.getInstance().getKSYTextureView().pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
