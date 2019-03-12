package com.yunbao.phonelive.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.player.KSYTextureView;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.activity.LauncherActivity;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.EggKnockBean;
import com.yunbao.phonelive.bean.GiftBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.LiveBeerBean;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.bean.LiveUpdateInfoBean;
import com.yunbao.phonelive.bean.LiveUserBean;
import com.yunbao.phonelive.bean.ReceiveDanMuBean;
import com.yunbao.phonelive.bean.ReceiveGiftBean;
import com.yunbao.phonelive.bean.StackGiftBean;
import com.yunbao.phonelive.event.LiveRoomCloseEvent;
import com.yunbao.phonelive.event.LoginSuccessEvent;
import com.yunbao.phonelive.fragment.LiveAudienceEndFragment;
import com.yunbao.phonelive.fragment.LiveGiftFragment;
import com.yunbao.phonelive.fragment.LiveShareFragment;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.interfaces.ChatRoomListener;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.presenter.LiveAnimPresenter;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.dialog.LiveChatInputDF;
import com.yunbao.phonelive.ui.dialog.LiveWatchBeerBucketDF;
import com.yunbao.phonelive.ui.tools.KeyboardUtil;
import com.yunbao.phonelive.ui.views.adapter.LiveWatcherVPAdapter;
import com.yunbao.phonelive.ui.views.fragment.LiveChatFragment;
import com.yunbao.phonelive.ui.views.fragment.LiveContributionFragment;
import com.yunbao.phonelive.ui.views.fragment.LiveOwnerFragment;
import com.yunbao.phonelive.ui.views.pop.LiveWatchMenusPop;
import com.yunbao.phonelive.ui.views.video.FloatingPlayer;
import com.yunbao.phonelive.ui.widget.NoScrollViewPager;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.SharedSdkUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.framework.Platform;
import io.reactivex.disposables.CompositeDisposable;

import static com.yunbao.phonelive.bean.LiveChatBean.GIFT;

/**
 * 横屏播放界面
 */
public class LiveWatcherActivity extends AbsActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private CompositeDisposable disposable;
    private List<Fragment> fragments;
    private String mDataSource = "";

    private ImageView sendIv;
    private TextView chatEt;
    private NoScrollViewPager liveVp;
    private RadioGroup menusRg;
    private RadioButton menusChatRb, menusOwnerRb, menusRankRb;
    private LinearLayout chatGroupLl;
    private String mLiveUid = "", mStream = "";
    private LiveChatFragment liveChatFragment;
    private LiveChatInputDF liveChatInputDF;
    private LiveOwnerFragment liveOwnerFragment;
    private View lineOne, lineTwo, lineThree, chatLineV;
    private LiveWatchBeerBucketDF beerBucketDF;
    private int widthPixels, heightPixels;

    @Override
    protected void onNewIntent(Intent intent) {
        SocketUtil.getInstance().close();
        super.onNewIntent(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_live_watcher;
    }

    @Override
    protected void main() {
        mConfigBean = AppConfig.getInstance().getConfig();
        widthPixels = getResources().getDisplayMetrics().widthPixels;
        heightPixels = widthPixels * 9 / 16;
        getIntentData();
        disposable = new CompositeDisposable();
        fragments = new ArrayList<>();
        liveChatFragment = LiveChatFragment.newInstance(mLiveUid, mStream);
        liveChatFragment.setLiveInfo(mLiveBean);
        liveChatFragment.setListener(chatRoomListener);
        liveOwnerFragment = LiveOwnerFragment.newInstance(mLiveUid);
        fragments.add(liveChatFragment);
        fragments.add(liveOwnerFragment);
        fragments.add(LiveContributionFragment.newInstance(mLiveUid));
        liveChatInputDF = LiveChatInputDF.newInstance();
        initView();
        getEnterRoomInfo();
        initDanmu();
        initClarityUrl();
    }

    LiveBean mLiveBean;

    private void getIntentData() {
        if (getIntent() != null) {
            Intent intent = getIntent();
            mLiveBean = intent.getParcelableExtra("liveBean");
            if (mLiveBean != null) {
                mDataSource = mLiveBean.getPull();
                mLiveUid = mLiveBean.getUid();
                mStream = mLiveBean.getStream();
            } else finish();
        } else finish();
    }


    private void getEnterRoomInfo() {
        HttpUtil.enterRoom(mLiveUid, mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
//                    JSONObject obj = JSON.parseObject(info[0]);
//                    AppConfig.getInstance().setSocketServer(obj.getString("chatserver"));
                    if (liveChatFragment != null) {
                        liveChatFragment.connectionSocket();
                    }
                }
            }
        });
    }

    private boolean isInitFinish = false;
    private TextView attentionTv;
    private RelativeLayout attentionRl;

    private void initView() {
        sendIv = findViewById(R.id.live_watcher_send_iv);
        chatEt = findViewById(R.id.live_watcher_chat_et);
        liveVp = findViewById(R.id.live_watcher_vp);
        menusRg = findViewById(R.id.live_watcher_menus_rg);
        menusChatRb = findViewById(R.id.live_watcher_menus_chat_rb);
        menusOwnerRb = findViewById(R.id.live_watcher_menus_owner_rb);
        menusRankRb = findViewById(R.id.live_watcher_menus_rank_rb);
        chatGroupLl = findViewById(R.id.live_watcher_chat_ll);
        attentionTv = findViewById(R.id.live_watcher_focus_tv);
        attentionRl = findViewById(R.id.live_watcher_focus_rl);
        chatLineV = findViewById(R.id.chat_view_line);
        lineOne = findViewById(R.id.live_watcher_menus_line_one);
        lineTwo = findViewById(R.id.live_watcher_menus_line_two);
        lineThree = findViewById(R.id.live_watcher_menus_line_three);
        initVideoView();
        setListener();
        liveVp.setNoScroll(false);
  //      liveVp.setHasScrollAnimation(false);
        liveVp.setOffscreenPageLimit(fragments.size());
        liveVp.setAdapter(new LiveWatcherVPAdapter(getSupportFragmentManager(), fragments));

        /*video view start*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        context = this.getApplicationContext();
        mSettings = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        editor = mSettings.edit();
        currentState = 0;
        if (FloatingPlayer.getInstance().getKSYTextureView() == null) {
            startToPlay();
            isInitFinish = true;
        } else {
            resumeToPlay();
        }
        /*video view end*/
        getIsAttention();
        getLiveInfo();
        getUserList();
        EventBus.getDefault().register(this);
    }

    private boolean isAttent = false; //是否已关注

    private void getIsAttention() {
        HttpUtil.getIsAttent(mLiveUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    JSONObject jsonObject = JSON.parseObject(info[0]);
                    if (jsonObject != null) {
                        if (jsonObject.containsKey("isattent")) {
                            String isattent = jsonObject.getString("isattent");
                            if (!TextUtils.isEmpty(isattent)) {
                                isAttent = "1".equals(isattent);
                                if (liveChatFragment != null) {
                                    liveChatFragment.setIsAttention(isAttent ? 1 : 0);
                                }
                            }
                        }
                    }
                }
                setIsAttention();
            }
        });
    }

    private void setIsAttention() {
        if (isAttent) {
            attentionRl.setBackgroundColor(getResources().getColor(R.color.app_unselected_bg_color));
            attentionTv.setTextColor(getResources().getColor(R.color.footNotTextColor));
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_liver_focused);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            attentionTv.setCompoundDrawables(drawable, null, null, null);
            attentionTv.setText("已关注");
            focusIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_liver_focused_full));
        } else {
            attentionRl.setBackgroundColor(getResources().getColor(R.color.app_selected_color));
            attentionTv.setTextColor(getResources().getColor(R.color.white));
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_liver_unfocused);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            attentionTv.setCompoundDrawables(drawable, null, null, null);
            attentionTv.setText("关注");
            focusIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_liver_unfocused_full));
        }
    }

    private int isShowAttentionMsg = 0;

    /**
     * 关注 、取消关注
     */
    private void setAttention() {
        if (AppConfig.isUnlogin()) {
            unloginHint(back);
            return;
        }
        HttpUtil.setAttention(mLiveUid, new CommonCallback<Integer>() {
            @Override
            public void callback(Integer obj) {
                isAttent = obj == 1;
                setIsAttention();
                if (isAttent && isShowAttentionMsg == 0) {
                    SocketUtil.getInstance().sendSystemMessage(AppConfig.getInstance().getUserBean().getUser_nicename() + getString(R.string.attention_anchor));
                    isShowAttentionMsg = 1;
                }
            }
        });
    }

    LiveUserBean liveUserBean;

    private void getLiveInfo() {
        HttpUtil.getLiveInfo(mLiveUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                liveUserBean = JSON.toJavaObject(obj, LiveUserBean.class);
                if (liveUserBean != null) {
                    if (null != liveOwnerFragment)
                        liveOwnerFragment.setData(liveUserBean);
                    fireNum = liveUserBean.getFirenum();
                    mLiveBean.setFirenum(liveUserBean.getFirenum());
                    if (avatarIv != null) {
                        ImgLoader.displayCircle(mLiveBean.getAvatar_thumb(), avatarIv);
                    }
                    if (titleTv != null) {
                        titleTv.setText(mLiveBean.getTitle());
                    }
                    if (video_name_landscape != null) {
                        video_name_landscape.setText(mLiveBean.getTitle());
                    }
                    refreshFireNum();
                }
            }
        });
    }

    private int fireNum = 0;
    private boolean hasContent = false;

    private void setListener() {
        liveVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                KeyboardUtil.hideSoftInput(liveVp);
            }

            @Override
            public void onPageSelected(int i) {
                KeyboardUtil.hideSoftInput(liveVp);
                switch (i) {
                    case 0:
                        menusChatRb.setChecked(true);
                        chatGroupLl.setVisibility(View.VISIBLE);
                        chatLineV.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        menusOwnerRb.setChecked(true);
                        chatGroupLl.setVisibility(View.GONE);
                        chatLineV.setVisibility(View.GONE);
                        break;
                    case 2:
                        menusRankRb.setChecked(true);
                        chatGroupLl.setVisibility(View.GONE);
                        chatLineV.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        menusRg.setOnCheckedChangeListener((group, checkedId) -> {
            KeyboardUtil.hideSoftInput(liveVp);
            switch (checkedId) {
                case R.id.live_watcher_menus_chat_rb:
                    liveVp.setCurrentItem(0);
                    lineOne.setVisibility(View.VISIBLE);
                    lineTwo.setVisibility(View.INVISIBLE);
                    lineThree.setVisibility(View.INVISIBLE);
                    chatGroupLl.setVisibility(View.VISIBLE);
                    chatLineV.setVisibility(View.VISIBLE);
                    break;
                case R.id.live_watcher_menus_owner_rb:
                    liveVp.setCurrentItem(1);
                    lineOne.setVisibility(View.INVISIBLE);
                    lineTwo.setVisibility(View.VISIBLE);
                    lineThree.setVisibility(View.INVISIBLE);
                    chatGroupLl.setVisibility(View.GONE);
                    chatLineV.setVisibility(View.GONE);
                    break;
                case R.id.live_watcher_menus_rank_rb:
                    liveVp.setCurrentItem(2);
                    lineOne.setVisibility(View.INVISIBLE);
                    lineTwo.setVisibility(View.INVISIBLE);
                    lineThree.setVisibility(View.VISIBLE);
                    chatGroupLl.setVisibility(View.GONE);
                    chatLineV.setVisibility(View.GONE);
                    break;
            }
        });
        disposable.add(RxView.clicks(sendIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (hasContent) {
                sendMsg();
            } else {
                showGiftView();
            }
        }));

        disposable.add(RxView.clicks(chatEt).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (!"快来调侃一下主播吧!".contentEquals(chatEt.getText())) {
                liveChatInputDF.setEditerContent(chatEt.getText().toString());
            }
            liveChatInputDF.show(getSupportFragmentManager(), "inputDialog");
        }));

        disposable.add(RxView.clicks(attentionRl).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> setAttention()));

        liveChatInputDF.setListener(str -> {
            KeyboardUtil.hideSoftInput(chatEt);
            if (TextUtils.isEmpty(str)) {
                chatEt.setText("快来调侃一下主播吧!");
                hasContent = false;
                sendIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_liver_chat_gift));
            } else {
                sendIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_liver_chat_send));
                hasContent = true;
                chatEt.setText(str);
            }
        });

        disposable.add(RxView.clicks(findViewById(R.id.live_watcher_bucket_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            showBeerBucketView();
        }));
    }

    public void showBeerBucketView() {
        KeyboardUtil.hindKeyBoard(this);
        if (beerBucketDF == null) {
            beerBucketDF = LiveWatchBeerBucketDF.newInstance(mLiveUid);
            beerBucketDF.setListener(choseIndex -> {
                if (choseIndex == 0) {
                    showGiftView();
                } else {
                    showLotteryView();
                }
            });
        }
        beerBucketDF.show(getSupportFragmentManager(), "beer");
    }

    private void showLotteryView() {
        if (AppConfig.isUnlogin()) {
            unloginHint(back);
            return;
        }
        startActivity(new Intent(this, LotteryActivity.class));
    }


    public void sendMsg() {
        if (AppConfig.isUnlogin()) {
            unloginHint(back);
            return;
        }
        if (AppConfig.getInstance().getUserBean() != null && AppConfig.getInstance().getUserBean().getIsbind() == 1) {
            HttpUtil.sendTmsg(mLiveUid, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (info != null && info.length > 0) {
                        JSONObject jsonObject = JSON.parseObject(info[0]);
                        if (jsonObject.containsKey("type") && jsonObject.getIntValue("type") == 0) {
                            hasContent = false;
                            String trim = chatEt.getText().toString().trim();
                            SocketUtil.getInstance().sendChatMsg(trim);
                            chatEt.setText("快来调侃一下主播吧!");
                            KeyboardUtil.hideSoftInput(chatEt);
                            sendIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_liver_chat_gift));
                            if (liveChatInputDF != null) {
                                liveChatInputDF.clearEt();
                            }
                        } else ToastUtil.show(msg);
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            });
        } else {
            DialogUitl.confirmNoTitleDialog(this, "为了您的账号安全请前往绑定手机!", "去绑定", true, new DialogUitl.Callback() {
                @Override
                public void confirm(Dialog dialog) {
                    Intent intent = new Intent(LiveWatcherActivity.this, BindMobileActivity.class);
                    intent.putExtra("intentType", 1);
                    startActivity(intent);
                }

                @Override
                public void cancel(Dialog dialog) {
                }
            }).show();
        }
    }

    public void showGiftView() {
        KeyboardUtil.hideSoftInput(back);
        if (AppConfig.isUnlogin()) {
            unloginHint(back);
            return;
        }
        openGiftWindow();
    }

    private int mNumsVal = 0;// 观众数量

    /**
     * 当前直播间用户数大于20个后用这个刷新用户列表
     */
    protected void getUserList() {
//        HttpUtil.getUserList(mLiveUid, mStream, mUserListCallback);
    }

    private HttpCallback mUserListCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                mNumsVal = obj.getIntValue("nums");
//                refreshFireNum();
            }
        }
    };

    /*video view start */

    private ImageView back, full_screen;
    private RelativeLayout panel, titlePanel, content;
    private RelativeLayout video;
    private RelativeLayout landscape_content, portrait_content;
    private RelativeLayout landscape_top_panel, landscape_bottom_panel;
    private ImageView back_landscape;
    private TextView video_name_landscape;
    private TextView clarity_landscape;
    private ImageView chat_landscape;
    private CheckBox danmuCb;
    private Boolean comeBackFromRestart = false;
    private Boolean isPalying = true;
    private int clarityId;
    //0 横屏播放 1 竖屏播放
    private int currentState;

    private RelativeLayout clarity_content;
    private Boolean isPanelShowing_Portrait = true;
    private Boolean isPanelShowing_Landscape = true;
    private Boolean mTouching;
    private Context context;
    private SharedPreferences mSettings;
    private SharedPreferences.Editor editor;
    private String playingTitle = "";
    private Handler mHandler = new Handler();
    //清晰度
    private RadioGroup clarity;
    private RadioButton clarity_super, clarity_high, clarity_normal;
    private Boolean isChangingClarity = false;
    private ImageView avatarIv, focusIv, moreLandscapeIv;
    private TextView titleTv, subTitleTv;
    private RelativeLayout contentDanmu, loadingIv;
    private ImageView danmuIv;
    private int erroCount = 0;

    private void initVideoView() {
        back = (ImageView) findViewById(R.id.vod_display_back_portrait);
        back.setOnClickListener(this);
        full_screen = (ImageView) findViewById(R.id.vod_display_full_screen);
        full_screen.setOnClickListener(this);
        panel = (RelativeLayout) findViewById(R.id.vod_controller_bar);
        titlePanel = (RelativeLayout) findViewById(R.id.vod_display_title_portrait);
        panel.setOnClickListener(this);
        video = (RelativeLayout) findViewById(R.id.vod_main_video);
        content = (RelativeLayout) findViewById(R.id.vod_content);
        content.setGravity(RelativeLayout.CENTER_IN_PARENT);
        avatarIv = findViewById(R.id.live_avatar_iv);
        titleTv = findViewById(R.id.live_title_tv);
        subTitleTv = findViewById(R.id.live_watcher_num_tv);
        focusIv = findViewById(R.id.focus_landscape_cb);
        danmuIv = findViewById(R.id.danmu_landscape_iv);
        loadingIv = findViewById(R.id.iv_loading);
        contentDanmu = findViewById(R.id.vod_main_danmu);
        moreLandscapeIv = findViewById(R.id.vod_display_more_portrait);

        landscape_content = (RelativeLayout) findViewById(R.id.landscape_controller);
        portrait_content = (RelativeLayout) findViewById(R.id.portrait_controller);
        landscape_top_panel = (RelativeLayout) findViewById(R.id.landscape_top_panel);
        landscape_top_panel.setOnClickListener(this);
        landscape_bottom_panel = (RelativeLayout) findViewById(R.id.landscape_bottom_panel);
        landscape_bottom_panel.setOnClickListener(this);
        back_landscape = (ImageView) findViewById(R.id.vod_display_back_landscape);
        back_landscape.setOnClickListener(this);
        video_name_landscape = (TextView) findViewById(R.id.video_name_landscape);
        clarity_landscape = (TextView) findViewById(R.id.clarity_landscape);
        chat_landscape = findViewById(R.id.chat_landscape);
        danmuCb = findViewById(R.id.danmu_landscape_cb);
        clarity_landscape.setOnClickListener(this);
        chat_landscape.setOnClickListener(this);

        clarity_content = (RelativeLayout) findViewById(R.id.content_clarity);
        //清晰度
        clarity = (RadioGroup) findViewById(R.id.clarity);
        clarity_high = (RadioButton) findViewById(R.id.clarity_high);
        clarity_super = (RadioButton) findViewById(R.id.clarity_super);
        clarity_normal = (RadioButton) findViewById(R.id.clarity_normal);
        showPortraitPanel();
        clarity.setOnCheckedChangeListener(this);
//        changeClarityId();
        if (mLiveBean.getAvatar_thumb() != null) {
            ImgLoader.displayCircle(mLiveBean.getAvatar_thumb(), avatarIv);
        }
        titleTv.setText(mLiveBean.getTitle());
        video_name_landscape.setText(mLiveBean.getTitle());

        disposable.add(RxView.clicks(findViewById(R.id.scale_landscape)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            // TODO: 2018/9/19 比例缩放
            setVideoScale();
        }));
//        danmuIv.setImageDrawable(getResources().getDrawable(isDanmuShow ? R.mipmap.icon_heart_red : R.mipmap.icon_heart_cyan));
//        disposable.add(RxView.clicks(danmuIv).subscribe(o -> {
//            isDanmuShow = !isDanmuShow;
//            if (contentDanmu != null) {
//                contentDanmu.setVisibility(isDanmuShow ? View.VISIBLE : View.GONE);
//            }
//            danmuIv.setImageDrawable(getResources().getDrawable(isDanmuShow ? R.mipmap.icon_heart_red : R.mipmap.icon_heart_cyan));
//        }));
//        danmuCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            isDanmuShow = isChecked;
//            if (contentDanmu != null) {
//                contentDanmu.setVisibility(isDanmuShow ? View.VISIBLE : View.GONE);
//            }
//        });

        disposable.add(RxView.clicks(focusIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            setAttention();
        }));

        disposable.add(RxView.clicks(moreLandscapeIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            showMenusPop();
        }));

        disposable.add(RxView.clicks(findViewById(R.id.share_landscape_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> openShareWindow()));

        disposable.add(RxView.clicks(findViewById(R.id.gift_landscape)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> openGiftWindow()));
    }


    //    4:3   原始比例   全屏   16:9   4:3
    private int scaleType = 0;

    private void setVideoScale() {
        KSYTextureView mVideoView = FloatingPlayer.getInstance().getKSYTextureView();
        if (scaleType == 0) { //原始比例 =》4：3
            //缩放
            scaleType = 1;
            ToastUtil.show("显示比例 4:3");
            mVideoView.setScaleX(0.65f);
            mVideoView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mVideoView.start();
        } else if (scaleType == 1) { //4:3  => 16:9
            scaleType = 2;
            ToastUtil.show("显示比例 16:9");
            mVideoView.setScaleX(1f);
            mVideoView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mVideoView.start();
        } else if (scaleType == 2) { //16:9 =》全屏
            scaleType = 3;
            ToastUtil.show("全屏显示");
            mVideoView.setScaleX(1f);
            mVideoView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_NOSCALE_TO_FIT);
            mVideoView.start();
        } else {
            scaleType = 0;
            mVideoView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mVideoView.start();
            ToastUtil.show("原始比例");
            mVideoView.setScaleX(1f);
        }

    }

    private void showMenusPop() {
        LiveWatchMenusPop liveWatchMenusPop = new LiveWatchMenusPop(this);
        liveWatchMenusPop.setCallback(index -> {
            if (index == 0) {
                openShareWindow();
            } else {
                if (AppConfig.isUnlogin()) {
                    unloginHint(moreLandscapeIv);
                    return;
                }
                Intent intent = new Intent(this, LiveReportActivity.class);
                intent.putExtra("liveId", mLiveUid);
                intent.putExtra("roomId", mLiveUid);
                intent.putExtra("roomName", mLiveBean.getTitle());
                startActivity(intent);
            }
        });
        liveWatchMenusPop.showLeft(moreLandscapeIv);
    }

    private boolean isDanmuShow = true;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener = iMediaPlayer -> {
        FloatingPlayer.getInstance().getKSYTextureView().setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        FloatingPlayer.getInstance().getKSYTextureView().setLooping(true);
        Log.i("buffer", "视频加载结束");
        if (comeBackFromRestart) {
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
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = (iMediaPlayer, i, i1) -> {
        if (erroCount == 0) {
            Toast.makeText(LiveWatcherActivity.this, "播放遇到错误，播放已退出", Toast.LENGTH_SHORT).show();
            erroCount = 1;
        }
        videoPlayEnd();
        startToPlay();
        return false;
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

            mHandler.postDelayed(() -> {
                if (landscape_content.getVisibility() == View.VISIBLE) {
                    landscape_content.setVisibility(View.GONE);
                    hideStatusBar();
                    isPanelShowing_Landscape = false;
                }
            }, 2500);
//            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
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
            chatGroupLl.setVisibility(View.GONE);
            chatLineV.setVisibility(View.GONE);
//            if (isDanmuShow)
//                contentDanmu.setVisibility(isDanmuShow ? View.VISIBLE : View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            showPortraitPanel();
            currentState = 0;
            chatGroupLl.setVisibility(View.VISIBLE);
            chatLineV.setVisibility(View.VISIBLE);
        //    contentDanmu.setVisibility(View.GONE);
        }
    }

    private void startToPlay() {
//            mDataSource = videoList.get(playingId).getPlayURL().get(clarityId).trim();
        FloatingPlayer.getInstance().init(context);
        video.addView(FloatingPlayer.getInstance().getKSYTextureView());
        FloatingPlayer.getInstance().getKSYTextureView().setOnPreparedListener(mOnPreparedListener);
        FloatingPlayer.getInstance().getKSYTextureView().setOnErrorListener(mOnErrorListener);
        FloatingPlayer.getInstance().getKSYTextureView().setOnTouchListener(mTouchListener);
        FloatingPlayer.getInstance().getKSYTextureView().setVolume(1.0f, 1.0f);
        FloatingPlayer.getInstance().getKSYTextureView().setComeBackFromShare(true);
//        changeSettings();
        try {
            FloatingPlayer.getInstance().getKSYTextureView().setDataSource(mDataSource);
            FloatingPlayer.getInstance().getKSYTextureView().shouldAutoPlay(true);
            FloatingPlayer.getInstance().getKSYTextureView().setBufferTimeMax(5.0f);
            FloatingPlayer.getInstance().getKSYTextureView().prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.putBoolean("isPlaying", true);
        editor.commit();
        erroCount = 1;
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

    private void dealTouchEvent() {
        if (portrait_content.getVisibility() == View.VISIBLE) {
            if (isPanelShowing_Portrait) {
                panel.setVisibility(View.GONE);
                titlePanel.setVisibility(View.GONE);
            } else {
                panel.setVisibility(View.VISIBLE);
                titlePanel.setVisibility(View.VISIBLE);
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
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        content.setLayoutParams(layoutParams);
        portrait_content.setVisibility(View.GONE);
        landscape_content.setVisibility(View.VISIBLE);
    }

    private void hideLandscapePanel() {
        hideStatusBar();
        landscape_content.setVisibility(View.GONE);
        isPanelShowing_Landscape = false;
    }

    private void showPortraitPanel() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPixels);
        content.setLayoutParams(layoutParams);
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
            finish();
        } else if (id == R.id.vod_display_back_landscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (id == R.id.vod_display_full_screen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else if (id == R.id.clarity_landscape) {
            hideLandscapePanel();
            clarity_content.setVisibility(View.VISIBLE);
        } else if (id == R.id.chat_landscape) {
            if (liveChatInputDF != null) {
                liveChatInputDF.show(getSupportFragmentManager(), "input");
            }
        } else if (id == R.id.vod_controller_bar || id == R.id.landscape_bottom_panel || id == R.id.landscape_top_panel) {
            return;
        }

    }

    @Override
    public void onBackPressed() {
        if (currentState == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInitFinish) {
            if (FloatingPlayer.getInstance() != null && FloatingPlayer.getInstance().getKSYTextureView() != null) {
                FloatingPlayer.getInstance().getKSYTextureView().runInForeground();
                FloatingPlayer.getInstance().getKSYTextureView().start();
            }
        }
        mPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (FloatingPlayer.getInstance().getKSYTextureView() != null) {
            isPalying = false;
            FloatingPlayer.getInstance().getKSYTextureView().runInBackground(false);
//            FloatingPlayer.getInstance().getKSYTextureView().pause();
        }
        mPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataSource = null;
        if (fragments != null) {
            fragments.clear();
            fragments = null;
        }
        EventBus.getDefault().unregister(this);
        if (FloatingPlayer.getInstance() != null && FloatingPlayer.getInstance().getKSYTextureView() != null) {
            FloatingPlayer.getInstance().getKSYTextureView().runInBackground(true);
            FloatingPlayer.getInstance().getKSYTextureView().release();
            FloatingPlayer.getInstance().destroy();
        }
    }

    /**
     * 清晰度
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        hideShade();
        loadingIv.setVisibility(View.VISIBLE);
        if (checkedId == R.id.clarity_normal) {
            clarityType = 2;
            clarity_landscape.setText("标清");
        } else if (checkedId == R.id.clarity_high) {
            clarityType = 1;
            clarity_landscape.setText("高清");
        } else if (checkedId == R.id.clarity_super) {
            clarityType = 0;
            clarity_landscape.setText("原画");
        }
        setClarity();
    }

    /*video view end */
    private int clarityType = 0;  // 0=原画 1=高清 2=标清
    private String pullUrl = "", pullUrlHight = "", pullUrlNormal = "";

    private void setClarity() {
        if (clarityType == 0) {
            mDataSource = pullUrl;
        } else if (clarityType == 1) {
            mDataSource = pullUrlHight;
        } else {
            mDataSource = pullUrlNormal;
        }
//        FloatingPlayer.getInstance().getKSYTextureView().softReset();
        try {
//            FloatingPlayer.getInstance().getKSYTextureView().setDataSource(mDataSource);
//            FloatingPlayer.getInstance().getKSYTextureView().shouldAutoPlay(true);
//            FloatingPlayer.getInstance().getKSYTextureView().setBufferTimeMax(5.0f);
//            FloatingPlayer.getInstance().getKSYTextureView().prepareAsync();
            FloatingPlayer.getInstance().getKSYTextureView().reload(mDataSource, true, KSYMediaPlayer.KSYReloadMode.KSY_RELOAD_MODE_FAST);
            mHandler.postDelayed(() -> {
                if (loadingIv != null) {
                    loadingIv.setVisibility(View.GONE);
                }
            }, 2500);
        } catch (Exception e) {
            e.printStackTrace();
        }


// reload成功后会有消息回调
//        public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(IMediaPlayer iMediaPlayer, int info, int extra) {
//                if (info == IMediaPlayer.MEDIA_INFO_RELOADED)
//                    Log.d(TAG, "Succeed to reload video.");
//                return false;
//            }
//        };
    }

    private void initClarityUrl() {
        pullUrl = mLiveBean.getPull();
        if (pullUrl.endsWith(".flv")) {
            String pullUrls = pullUrl.substring(0, this.pullUrl.lastIndexOf(".flv"));
            pullUrlHight = pullUrls + "_900.flv";
            pullUrlNormal = pullUrls + "_550.flv";
//            Log.e("//", "initClarityUrl: ===" + pullUrls);
        } else if (pullUrl.endsWith(".m3u8")) {
            String pullUrls = pullUrl.substring(0, this.pullUrl.lastIndexOf(".m3u8"));
            pullUrlHight = pullUrls + "_900.m3u8";
            pullUrlNormal = pullUrls + "_550.m3u8";
        } else if (pullUrl.endsWith(".mp4")) {
            pullUrlHight = pullUrl;
            pullUrlNormal = pullUrl;
        } else {
            pullUrlHight = pullUrl + "_900";
            pullUrlNormal = pullUrl + "_550";
        }
    }


    LiveGiftFragment mGiftFragment;
    private String giftId = "";

    /*gift start*/
    private void openGiftWindow() {
        if (mGiftFragment == null) {
            mGiftFragment = new LiveGiftFragment();
            mGiftFragment.setListener(bean -> {
                if (AppConfig.getInstance().getUserBean() != null && AppConfig.getInstance().getUserBean().getIsbind() == 1) {
                    Log.e("//", "sendGift: " + bean.getId());
                    mTempGiftBean = bean;
                    giftId = bean.getId();
                    HttpUtil.sendGift(mLiveUid, mTempGiftBean.isPackageGift() ? mTempGiftBean.getGiftid() : mTempGiftBean.getId(), TextUtils.isEmpty(mTempGiftBean.getCount()) ? "1" : mTempGiftBean.getCount(), mStream, mTempGiftBean.isPackageGift(), mSendGiftCallback);
                } else {
                    DialogUitl.confirmNoTitleDialog(this, "为了您的账号安全请前往绑定手机!", "去绑定", true, new DialogUitl.Callback() {
                        @Override
                        public void confirm(Dialog dialog) {
                            Intent intent = new Intent(LiveWatcherActivity.this, BindMobileActivity.class);
                            intent.putExtra("intentType", 1);
                            startActivity(intent);
                        }

                        @Override
                        public void cancel(Dialog dialog) {
                        }
                    }).show();
                }
//                Log.e("//", "sendGift: " + bean.getId());
//                mTempGiftBean = bean;
//                giftId = bean.getId();
//                HttpUtil.sendGift(mLiveUid, mTempGiftBean.isPackageGift() ? mTempGiftBean.getGiftid() : mTempGiftBean.getId(), TextUtils.isEmpty(mTempGiftBean.getCount()) ? "1" : mTempGiftBean.getCount(), mStream, mTempGiftBean.isPackageGift(), mSendGiftCallback);
            });
        }
        if (!mGiftFragment.isAdded()) {
            mGiftFragment.show(getSupportFragmentManager(), "GiftFragment");
        }

    }

    private GiftBean mTempGiftBean;//当前选择的礼物

    private HttpCallback mSendGiftCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                String lastCoin = obj.getString("coin");
                String carrot = obj.getString("carrot");
                if (!TextUtils.isEmpty(lastCoin)) {
                    AppConfig.getInstance().getUserBean().setCoin(lastCoin);
                    mGiftFragment.updateCoin(lastCoin);
                }
                if (!TextUtils.isEmpty(carrot))
                    AppConfig.getInstance().getUserBean().setCarrot(carrot);

                if (mTempGiftBean.isPackageGift()) {
                    mTempGiftBean.setNum(obj.getString("gnum"));
                    mGiftFragment.updateData(mTempGiftBean);
                }
                if (obj.containsKey("level")) {
                    AppConfig.getInstance().getUserBean().setLevel(obj.getIntValue("level"));
                }
                String casktoken = "";
                if (obj.containsKey("casktoken")) {
                    casktoken = obj.getString("casktoken");
                }
                if ("46".equals(giftId) || "44".equals(giftId) || "19".equals(giftId) || "22".equals(giftId)) {
                    SocketUtil.getInstance().sendStackGift(obj.getString("gifttoken"), mLiveBean);
                    if (liveChatFragment != null) {
//                        LiveChatBean data = new LiveChatBean();
//                        data.setType(GIFT);
//                        data.setUser_nicename(AppConfig.getInstance().getUserBean().getUser_nicename());
//                        data.setId(AppConfig.getInstance().getUserBean().getUser_nicename());
//                        data.setGiftcount(Integer.valueOf(mTempGiftBean.getCount()));
//                        data.setGifticon(mTempGiftBean.getGifticon());
//                        data.setGiftid(mTempGiftBean.getGiftid());
//                        data.setLevel(AppConfig.getInstance().getUserBean().getLevel());
//                    liveChatFragment.onChat(data);
                        ReceiveGiftBean chatBean = new ReceiveGiftBean();
                        chatBean.setLevel(AppConfig.getInstance().getUserBean().getLevel());
                        chatBean.setUname(AppConfig.getInstance().getUserBean().getUser_nicename());
                        chatBean.setUhead(AppConfig.getInstance().getUserBean().getAvatar_thumb());
                        chatBean.setUid(AppConfig.getInstance().getUserBean().getId());
                        chatBean.setGiftcount(mTempGiftBean.getCount());
                        chatBean.setGifticon(mTempGiftBean.getGifticon());
                        chatBean.setGiftid(mTempGiftBean.getId());
                        chatBean.setLevel(AppConfig.getInstance().getUserBean().getLevel());
                        liveChatFragment.onSendGift(chatBean);

                    }
                } else
                    SocketUtil.getInstance().sendGift(mTempGiftBean.getEvensend(), casktoken, obj.getString("gifttoken"));
            } else {
                ToastUtil.show(msg);
            }
        }

        @Override
        public void onError(String msg) {
            ToastUtil.show(msg);
        }
    };

    /*gift end*/

    /*danmu start*/
    protected LiveAnimPresenter mLiveAnimPresenter;//各种动画的Presenter

    private void initDanmu() {
        mLiveAnimPresenter = new LiveAnimPresenter(this);
        mLiveAnimPresenter.setAnimContainer(contentDanmu);
//        mLiveAnimPresenter.addDanmu(new ReceiveDanMuBean());
    }

    private ChatRoomListener chatRoomListener = new ChatRoomListener() {
        @Override
        public void onSendChat(LiveChatBean bean) {
            ReceiveDanMuBean receiveDanMuBean = new ReceiveDanMuBean();
            receiveDanMuBean.setUname(bean.getUser_nicename());
            receiveDanMuBean.setUid(bean.getId());
            receiveDanMuBean.setContent(bean.getContent());
            receiveDanMuBean.setDanmuType(bean.getType());
            receiveDanMuBean.setUhead(bean.getContent());
            mLiveAnimPresenter.addDanmu(receiveDanMuBean);
        }

        @Override
        public void onSenfGift(LiveChatBean bean) {

        }

        @Override
        public void onTakeGift(ReceiveGiftBean bean) {
            if (mLiveAnimPresenter != null) {
                mLiveAnimPresenter.playGiftAnim(bean);
            }
            if (bean.getCask() != null) {
                taskBeerStep(bean.getCask());
                if (bean.getCask().getCask() == 2) {
                    SocketUtil.getInstance().comTaskBeer(bean.getCask().getInfo(), bean.getCask().getCtype());
                }
            }
//            ReceiveDanMuBean receiveDanMuBean = new ReceiveDanMuBean();
//            receiveDanMuBean.setUname(bean.getUname());
//            receiveDanMuBean.setUid(bean.getUid());
//            receiveDanMuBean.setGiftname(bean.getGiftname());
//            receiveDanMuBean.setDanmuType(bean.getVipType());
//            receiveDanMuBean.setUhead(bean.getUhead());
//            receiveDanMuBean.setLiveName(bean.getUname());
//            mLiveAnimPresenter.addGiftDanmu(receiveDanMuBean);
        }

        @Override
        public void onStartBeer(LiveBeerBean bean) {
            if (beerBucketDF != null) {
                if (beerBucketDF.getShowsDialog()) {
                    beerBucketDF.setStepData(bean);
                }
            }
        }

        @Override
        public void onEnterRoom() {
            mNumsVal++;
//            refreshFireNum();
        }

        @Override
        public void onLeaveRoom() {
            mNumsVal--;
            if (mNumsVal < 0) mNumsVal = 0;
//            refreshFireNum();
        }

        @Override
        public void addFakeFans(int size) {
            mNumsVal += size;
//            refreshFireNum();
        }

        @Override
        public void onCompleteTaskBeer(int cType, int num) {
            String str = "";
            if (num <= 0) {
                return;
            }
            switch (cType) {
                case 1:
                    str = "恭喜您获得" + num + "把铜钥匙";
                    break;
                case 2:
                    str = "恭喜您获得" + num + "把银钥匙";
                    break;
                case 3:
                    str = "恭喜您获得" + num + "把金钥匙";
                    break;
            }

            Dialog dialog = DialogUitl.completeBeer(LiveWatcherActivity.this, str);
            if (dialog != null) {
                dialog.show();
            }
        }

        @Override
        public void onLiveEnd() {
            onClose();
            if (FloatingPlayer.getInstance() != null && FloatingPlayer.getInstance().getKSYTextureView() != null) {
                FloatingPlayer.getInstance().getKSYTextureView().pause();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            LiveAudienceEndFragment fragment = new LiveAudienceEndFragment();
            Bundle bundle = new Bundle();
            bundle.putString("anchorAvatar", mLiveBean.getAvatar());
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.wrap, fragment);
            if (mPaused) {
                ft.commitAllowingStateLoss();
            } else {
                ft.commit();
            }
        }

        @Override
        public void onUpdateRoomInfo(LiveUpdateInfoBean liveUpdateInfoBean) {
            if (liveUpdateInfoBean != null) {
                if (mLiveBean != null) {
                    mLiveBean.setFirenum(liveUpdateInfoBean.getTotal());
                }
                refreshFireNum();
                if (liveUserBean != null) {
                    liveUserBean.setFirenum(liveUpdateInfoBean.getTotal());
                    liveUserBean.setFans(liveUpdateInfoBean.getFans());
                    if (liveOwnerFragment != null) {
                        liveOwnerFragment.setData(liveUserBean);
                    }
                }
            }
        }

        @Override
        public void onShowStackGift(StackGiftBean bean, LiveBean liveBean) {
            if (mLiveAnimPresenter != null) {
                mLiveAnimPresenter.addGiftDanmu(bean, liveBean);
            }
        }

        @Override
        public void onTakeEggDanmu(String uName, EggKnockBean eggInfo, LiveBean liveInfo) {
            if (mLiveAnimPresenter != null) {
                mLiveAnimPresenter.addEggDanmu(uName, eggInfo, liveInfo);
            }
        }

    };

    protected void onClose() {

        EventBus.getDefault().post(new LiveRoomCloseEvent());
        if (mLiveAnimPresenter != null) {
            mLiveAnimPresenter.clearAnimQueue();//清空动画队列
        }
        //切断socket
        if (SocketUtil.getInstance() != null) {
            SocketUtil.getInstance().close();
        }
    }

    private boolean mPaused;

    private void refreshFireNum() {
        if (subTitleTv != null) {
            subTitleTv.setText("热度:" + mLiveBean.getFireNums());
        }
    }

    private LiveShareFragment mShareFragment;
    private ConfigBean mConfigBean;
    private String url;

    private void openShareWindow() {
        if (mShareFragment == null) {
            mShareFragment = new LiveShareFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("live", mLiveBean);
            url = mConfigBean.getWx_siteurl() + mLiveBean.getUid();
            mShareFragment.setArguments(bundle);
            mShareFragment.setListener((item, position) -> {

//                if (FloatingPlayer.getInstance().getKSYTextureView() != null) {
//                    FloatingPlayer.getInstance().getKSYTextureView().setComeBackFromShare(true);
//                }
                SharedSdkUitl.getInstance().share(item.getType(),
                        mConfigBean.getShare_title(),
                        mLiveBean.getUser_nicename() + mConfigBean.getShare_des(),
                        mLiveBean.getAvatar(), url, shareListener);
            });
        }
        if (!mShareFragment.isAdded()) {
            mShareFragment.show(getSupportFragmentManager(), "LiveShareFragment");
        }
    }

    private SharedSdkUitl.ShareListener shareListener = new SharedSdkUitl.ShareListener() {
        @Override
        public void onSuccess(Platform platform) {
            if (!AppConfig.isUnlogin()) {
                HttpUtil.onFinishShare();
            }
        }

        @Override
        public void onError(Platform platform) {
        }

        @Override
        public void onCancel(Platform platform) {
        }
    };

    /*danmu end*/
    protected void taskBeerStep(LiveBeerBean data) {
        if (beerBucketDF != null) {
            if (!beerBucketDF.isHidden()) {
                beerBucketDF.setStepData(data);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCatchLogin(LoginSuccessEvent event) {
        Log.e("//", "onCatchLogin: ");
        getEnterRoomInfo();
    }
}

