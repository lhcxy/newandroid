package com.yunbao.phonelive.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.EggKnockBean;
import com.yunbao.phonelive.bean.GiftBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.LiveBeerBean;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.bean.LiveRedPaperBean;
import com.yunbao.phonelive.bean.LiveUpdateInfoBean;
import com.yunbao.phonelive.bean.ReceiveGiftBean;
import com.yunbao.phonelive.bean.StackGiftBean;
import com.yunbao.phonelive.event.JPushEvent;
import com.yunbao.phonelive.event.LoginSuccessEvent;
import com.yunbao.phonelive.event.RefreshUserInfoEvent;
import com.yunbao.phonelive.fragment.LiveAudienceBottomFragment;
import com.yunbao.phonelive.fragment.LiveAudienceEndFragment;
import com.yunbao.phonelive.fragment.LiveBottomFragment;
import com.yunbao.phonelive.fragment.LiveGiftFragment;
import com.yunbao.phonelive.fragment.LivePullStreamFragment;
import com.yunbao.phonelive.fragment.LiveShareFragment;
import com.yunbao.phonelive.fragment.PullStreamPlayer;
import com.yunbao.phonelive.fragment.TimeChargeFragment;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.ChoseCallback;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.presenter.CheckLivePresenter;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.dialog.LiveEggEventDF;
import com.yunbao.phonelive.ui.dialog.LiveGetCommandRpDF;
import com.yunbao.phonelive.ui.dialog.LiveGetRandomRpDF;
import com.yunbao.phonelive.ui.dialog.LiveRpResultDF;
import com.yunbao.phonelive.ui.dialog.LiveWatchBeerBucketDF;
import com.yunbao.phonelive.ui.views.BindMobileActivity;
import com.yunbao.phonelive.ui.views.LotteryActivity;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.SharedSdkUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.eventbus.EventBus;
import cn.sharesdk.framework.Platform;

/**
 * Created by cxf on 2017/8/19.
 * 观众直播间
 */

public class LiveAudienceActivity extends LiveActivity {

    private LiveGiftFragment mGiftFragment;
    private GiftBean mTempGiftBean;//当前选择的礼物

    private PullStreamPlayer mPullStreamPlayer;
    private int mLiveType;//直播间的类型  普通 密码 门票 计时等
    private int mLiveTypeVal;//收费价格,计时收费每次扣费的值
    private int mTimeChargeInterval = 60000;//计时收费每次扣费的时间间隔，毫秒
    private TimeChargeFragment mTimeChargeFragment;
    private final int CHARGE_CODE = 1000;
    private LiveShareFragment mShareFragment;
    private boolean mPaused;
    private boolean mVideoLoadSucceed;
    private LiveWatchBeerBucketDF liveWatchBeerBucketDF;
    private ConfigBean mConfigBean;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mHandler.removeCallbacksAndMessages(null);
        SocketUtil.getInstance().close();
        mChatListAdapter.clear();
        initRoomParams(intent);
    }


    @Override
    protected void main() {
        super.main();
        mRoot.setScrollView(mWrap);
        mConfigBean = AppConfig.getInstance().getConfig();
        mPullStreamPlayer = new LivePullStreamFragment();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.video_place, mPullStreamPlayer).commit();
//        mContent.setOnClickListener(v -> {
//            if (!mEnd) {
//                //点亮，飘心
//                mLiveAnimPresenter.floatHeart();
//            }
//        });
        initRoomParams(getIntent());

        findViewById(R.id.btn_close).setOnClickListener(v -> {
            onBackPressed();
        });
        disposable.add(RxView.clicks(liveHbTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> getLiveThankInfo()));
        HttpUtil.getLivRpInfo(mLiveBean.getUid(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    LiveRedPaperBean liveRedPaperBean = JSON.parseObject(info[0], LiveRedPaperBean.class);
                    if (liveRedPaperBean != null) {
                        liveRedPaperBean.setTitle(liveRedPaperBean.getName());
                        liveRedPaperBean.setUid(mLiveBean.getUid());
                        onGetRedPaper(liveRedPaperBean);
                    }
                }
            }
        });
    }

    private void getLiveThankInfo() {
        HttpUtil.getBeerCaskThank(mLiveUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0 && !TextUtils.isEmpty(info[0])) {
                    JSONObject jsonObject = JSONObject.parseObject(info[0]);
                    if (jsonObject.containsKey("coin")) {
                        int coin = jsonObject.getIntValue("coin");
                        DialogUitl.completeBeer(LiveAudienceActivity.this, String.format(getResources().getString(R.string.txt_beer_bucket_get_recognition), coin)).show();
                    }
                }
                liveHbTv.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * 初始化房间内的各种参数
     */
    private void initRoomParams(Intent intent) {
        if (mBg.getVisibility() == View.GONE) {
            mBg.setVisibility(View.VISIBLE);
        }
        mLiveBean = intent.getParcelableExtra("liveBean");
        mLiveType = intent.getIntExtra("type", 0);
        mLiveTypeVal = intent.getIntExtra("typeVal", 0);
        mLiveUid = mLiveBean.getUid();
        mStream = mLiveBean.getStream();
        if (mRoomName != null) {
            mRoomName.setText("房间号:" + mLiveUid);
        }
        if (liveNameTv != null) {
            liveNameTv.setText(mLiveBean.getUser_nicename());
        }
        ImgLoader.displayBitmap(mLiveBean.getThumb(), bitmap -> {
            if (!mVideoLoadSucceed && mBg != null) {
                mBg.setImageDrawable(bitmap);
            } else {
            }
        });
//        if (mLiveType == 3) {
//            mHandler.sendEmptyMessageDelayed(TIME_CHARGE, mTimeChargeInterval);
//        }
        enterRoom();
        getLiveInfo();
//        HttpUtil.getCoin(new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                if (code == 0) {
//                    JSONObject obj = JSON.parseObject(info[0]);
//                    starCount = TextUtils.isEmpty(obj.getString("carrot")) ? "0" : obj.getString("carrot");
//                    if (mBottomFragment != null)
//                        mBottomFragment.setStarNum(starCount);
//                }
//            }
//        });
    }

    @Override
    protected LiveBottomFragment getBottomFragment() {
        return new LiveAudienceBottomFragment();
    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what) {
            case TIME_CHARGE://计时收费的时候用，每隔一段时间扣一次费
                timeCharge();
                break;
            case USER_LIST:
                getUserList();
                break;
        }
    }

    /**
     * 请求enterRoom接口，告诉服务器我进了这个直播间,同时获取直播间的相关信息
     */
    private void enterRoom() {
        HttpUtil.enterRoom(mLiveUid, mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
//                    AppConfig.getInstance().setSocketServer(obj.getString("chatserver"));
                    connectSocket();
                    //弹幕价格
                    mBarrageFee = obj.getString("barrage_fee");
                    //主播映票数量
                    //刷新用户列表的间隔时间
                    mUserlistRefreshTime = obj.getIntValue("userlist_time") * 1000;
                    mKickTime = obj.getString("kick_time");
                    mShutTime = obj.getString("shut_time");
                    //观众数量
//                    mNumsVal = obj.getIntValue("nums");
                    //是否关注了主播 0未关注  1 已关注
                    mIsAttention = obj.getIntValue("isattention");
                    //观众列表
                    showData();
                    //显示竞拍
                    if (obj.containsKey("auction")) {
                        JSONObject auction = obj.getJSONObject("auction");
                        if (auction.getIntValue("isauction") != 0) {
                            showAuction(auction);
                        }
                    }
                }
            }
        });
    }

    /**
     * 视频显示出来，加载成功
     */
    public void videoLoadSucess() {
        mVideoLoadSucceed = true;
    }

    /**
     * 隐藏背景图片
     */
    public void hideBackgroundImage() {
        mBg.animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBg.setVisibility(View.GONE);
                mBg.setAlpha(1f);
            }
        }).start();
    }

    private void showData() {
        ImgLoader.display(mLiveBean.getAvatar(), mAnchorAvatar);
//        mAnchorLevel.setImageResource(IconUitl.getAnchorLiveDrawable(mLiveBean.getLevel_anchor()));
//        mNums.setText(String.valueOf(mNumsVal));
//        String coinName = AppConfig.getInstance().getConfig().getName_votes();
//        String liangNum = mLiveBean.getGoodnum();
//        if (!"0".equals(liangNum)) {
//            mRoomName.setText(getString(R.string.room) + getString(R.string.liang));
//            mRoomNum.setText(liangNum);
//        } else {
        mRoomName.setText(getString(R.string.room) + mLiveBean.getUid());
//        }
        if (mIsAttention == 0) {
            mBtnAttention.setVisibility(View.VISIBLE);
            mBtnAttention.setText("关注");
        } else {
//            mBtnAttention.setText("取消关注");
            mBtnAttention.setVisibility(View.GONE);
        }
        mPullStreamPlayer.play(mLiveBean.getPull());
    }

    /**
     * 显示正在进行的竞拍
     */
    private void showAuction(JSONObject auction) {
        auctionStart(false, auction.getString("id"),
                auction.getString("thumb"),
                auction.getString("title"),
                auction.getString("price_start"),
                auction.getIntValue("long"));
        String bidUid = auction.getString("bid_uid");
        if (!"0".equals(bidUid)) {
            auctionAddMoney(auction.getString("avatar"), auction.getString("user_nicename"), auction.getString("bid_price"));
        }
    }

    public void liveAudienceClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share://分享
                openShareWindow();
                break;
            case R.id.btn_gift://礼物
                openGiftWindow();
                break;
            case R.id.btn_attention:
                attentAnchor();
                break;
            case R.id.live_beer_bucket_iv:
                openBeerView();
                break;
            case R.id.btn_egg_event://分享
                openEggEventView();
                break;
        }
    }

    private void openEggEventView() {
        LiveEggEventDF liveEggEventDF = LiveEggEventDF.newInstance(1);
        liveEggEventDF.setLiveInfo(mLiveBean);
        liveEggEventDF.show(getSupportFragmentManager(), LiveEggEventDF.class.getCanonicalName());
    }

    private void openBeerView() {
        if (liveWatchBeerBucketDF == null) {
            liveWatchBeerBucketDF = LiveWatchBeerBucketDF.newInstance(mLiveUid);
            liveWatchBeerBucketDF.setListener(new ChoseCallback() {
                @Override
                public void onChose(int choseIndex) {
                    if (choseIndex == 0) {
                        //送礼
                        openGiftWindow();
                    } else {
                        //抽奖
                        showLotteryView();
                    }
                }
            });
        }
        liveWatchBeerBucketDF.show(getSupportFragmentManager(), "beerBucketView");

    }
    private void showLotteryView() {
        if (AppConfig.isUnlogin()) {
            unloginHint(mRoomName);
            return;
        }
        startActivity(new Intent(this, LotteryActivity.class));
    }
    @Override
    protected void taskBeerStep(LiveBeerBean data) {
        if (liveWatchBeerBucketDF != null) {
            if (!liveWatchBeerBucketDF.isHidden()) {
                liveWatchBeerBucketDF.setStepData(data);
            }
        }
    }

    /**
     * 关注主播
     */
    private void attentAnchor() {
        if (AppConfig.isUnlogin()) {
            unloginHint(mRoomName);
            return;
        }
        HttpUtil.setAttention(mLiveUid, mAttentionCallback);
    }

    public int isShowAttentionMsg = 0;
    private CommonCallback<Integer> mAttentionCallback = new CommonCallback<Integer>() {
        @Override
        public void callback(Integer isAttention) {
            mIsAttention = isAttention;
            if (isAttention == 1 && isShowAttentionMsg == 0) {
                SocketUtil.getInstance().sendSystemMessage(AppConfig.getInstance().getUserBean().getUser_nicename() + getString(R.string.attention_anchor));
                isShowAttentionMsg = 1;
            }
        }
    };
    private String url = "";

    /**
     * 打开分享窗口
     */
    private void openShareWindow() {
        if (mShareFragment == null) {
            mShareFragment = new LiveShareFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("live", mLiveBean);
            mShareFragment.setArguments(bundle);
            url = mConfigBean.getWx_siteurl() + mLiveBean.getUid();
            mShareFragment.setListener((item, position) -> {

                SharedSdkUitl.getInstance().share(item.getType(),
                        mConfigBean.getShare_title(),
                        mLiveBean.getUser_nicename() + mConfigBean.getShare_des(),
                        mLiveBean.getAvatar(), url, shareListener);
            });
        }
        mShareFragment.show(mFragmentManager, "LiveShareFragment");
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

    /**
     * 打开送礼物的窗口
     */
    public void openGiftWindow() {
        if (AppConfig.isUnlogin()) {
            unloginHint(mRoomName);
            return;
        }
        if (mGiftFragment == null) {
            mGiftFragment = new LiveGiftFragment();
        }
        mGiftFragment.show(mFragmentManager, "GiftFragment");
    }

    /**
     * 请求送礼物接口
     */
    public void sendGift(GiftBean bean) {
        mTempGiftBean = bean;
        giftId = bean.getId();
        if (AppConfig.getInstance().getUserBean() != null && AppConfig.getInstance().getUserBean().getIsbind() == 1) {
            HttpUtil.sendGift(mLiveUid, bean.getId(), TextUtils.isEmpty(mTempGiftBean.getCount()) ? "1" : mTempGiftBean.getCount(), mStream, mTempGiftBean.isPackageGift(), mSendGiftCallback);
        } else {
            DialogUitl.confirmNoTitleDialog(this, "为了您的账号安全请前往绑定手机!", "去绑定", true, new DialogUitl.Callback() {
                @Override
                public void confirm(Dialog dialog) {
                    Intent intent = new Intent(LiveAudienceActivity.this, BindMobileActivity.class);
                    intent.putExtra("intentType", 1);
                    startActivity(intent);
                }

                @Override
                public void cancel(Dialog dialog) {
                }
            }).show();
        }
    }

    private String giftId = "";
    private HttpCallback mSendGiftCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                String lastCoin = obj.getString("coin");
                String carrot = obj.getString("carrot");
                mGiftFragment.updateCoin(lastCoin);
                if (!TextUtils.isEmpty(lastCoin))
                    AppConfig.getInstance().getUserBean().setCoin(lastCoin);
                if (!TextUtils.isEmpty(carrot))
                    AppConfig.getInstance().getUserBean().setCarrot(carrot);

                if (obj.containsKey("level")) {
                    AppConfig.getInstance().getUserBean().setLevel(obj.getIntValue("level"));
                    EventBus.getDefault().post(new RefreshUserInfoEvent());
                }
//                AppConfig.getInstance().getUserBean().setLevel(obj.getIntValue("level"));
                String casktoken = "";
                if (obj.containsKey("casktoken")) {
                    casktoken = obj.getString("casktoken");
                }
                if ("46".equals(giftId) || "44".equals(giftId) || "19".equals(giftId) || "22".equals(giftId)) {
//                    LiveChatBean data = new LiveChatBean();
//                    data.setType(GIFT);
//                    data.setUser_nicename(AppConfig.getInstance().getUserBean().getUser_nicename());
//                    data.setId(AppConfig.getInstance().getUserBean().getUser_nicename());
//                    data.setGiftcount(Integer.valueOf(mTempGiftBean.getCount()));
//                    data.setGifticon(mTempGiftBean.getGifticon());
//                    data.setGiftid(mTempGiftBean.getGiftid());

//                    SocketUtil.getInstance().sendStackGift(obj.getString("gifttoken"), mLiveBean);
//                    LiveChatBean data = new LiveChatBean();
//                    data.setType(GIFT);
//                    data.setUser_nicename(AppConfig.getInstance().getUserBean().getUser_nicename());
//                    data.setId(AppConfig.getInstance().getUserBean().getUser_nicename());
//                    data.setGiftcount(Integer.valueOf(mTempGiftBean.getCount()));
//                    data.setGifticon(mTempGiftBean.getGifticon());
//                    data.setGiftid(mTempGiftBean.getId());
//                    data.setLevel(AppConfig.getInstance().getUserBean().getLevel());
//                    liveChatFragment.onChat(data);

//
//                    UserBean bean = AppConfig.getInstance().getUserBean();
//                    LiveChatBean chatBean = new LiveChatBean();
//                    chatBean.setContent("送了" + mTempGiftBean.getCount() + "个" + mTempGiftBean.getGiftname());
//                    chatBean.setLevel(bean.getLevel());
//                    chatBean.setUser_nicename(bean.getUser_nicename());
//                    chatBean.setId(bean.getId());
//                    chatBean.setType(LiveChatBean.GIFT);

//                    mChatListAdapter.addData(data);
                    SocketUtil.getInstance().sendStackGift(obj.getString("gifttoken"), mLiveBean);
                    ReceiveGiftBean chatBean = new ReceiveGiftBean();
                    chatBean.setLevel(AppConfig.getInstance().getUserBean().getLevel());
                    chatBean.setUname(AppConfig.getInstance().getUserBean().getUser_nicename());
                    chatBean.setUhead(AppConfig.getInstance().getUserBean().getAvatar_thumb());
                    chatBean.setUid(AppConfig.getInstance().getUserBean().getId());
                    chatBean.setGiftcount(mTempGiftBean.getCount());
                    chatBean.setGifticon(mTempGiftBean.getGifticon());
                    chatBean.setGiftid(mTempGiftBean.getId());
                    chatBean.setLevel(AppConfig.getInstance().getUserBean().getLevel());
                    onSendGift(chatBean);

//                    SocketUtil.getInstance().sendStackGift(obj.getString("gifttoken"), mLiveBean.getUid(), mLiveBean.getUser_nicename(), mLiveBean.getStream(), mLiveBean.getAnyway(), mLiveBean.getTitle(), mLiveBean.getPull());
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


    @Override
    public void onBackPressed() {
        onClose();
        //停止播放
        if (mPullStreamPlayer != null) {
            mPullStreamPlayer.pausePlay();
        }
        finish();
    }

    @Override
    public void onKick(String touid) {
        if (touid.equals(AppConfig.getInstance().getUid())) {//被踢的是自己
            onBackPressed();
            ToastUtil.show(getString(R.string.you_are_kicked));
        }
    }

    @Override
    public void onShutUp(String touid, String content) {
        if (touid.equals(AppConfig.getInstance().getUid())) {
            DialogUitl.messageDialog(mContext, getString(R.string.tip), content, null).show();
        }
    }


    @Override
    public void onConnect(boolean successConn) {
        super.onConnect(successConn);
        if (successConn) {
            if (mLiveType == 2 || mLiveType == 3) {
                //发送socket更新直播间的映票
                SocketUtil.getInstance().updateVotes("1", mLiveTypeVal);
            }
        }
    }

    @Override
    public void onLight(LiveChatBean bean) {
        mChatListAdapter.addData(bean);
    }

    /**
     * 主播切换计时收费或更改计时收费的价格
     */
    @Override
    public void onChangeTimeCharge(int typeVal) {
        mLiveTypeVal = typeVal;
        mHandler.removeMessages(0);
        if (mPullStreamPlayer != null) {
            mPullStreamPlayer.pausePlay();
        }
        if (mBg.getVisibility() == View.GONE) {
            mBg.setVisibility(View.VISIBLE);
        }
        showRoomChargeDialog(getString(R.string.room_charge) + mLiveTypeVal + AppConfig.getInstance().getConfig().getName_coin());
    }

    @Override
    public void onStartTaskBeer(LiveBeerBean data) {
        if (liveWatchBeerBucketDF != null) {
            if (liveWatchBeerBucketDF.getShowsDialog()) {
                liveWatchBeerBucketDF.setStepData(data);
            }
        }
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

        Dialog dialog = DialogUitl.completeBeer(this, str);
        if (dialog != null) {
            dialog.show();
        }
    }

    @Override
    public void onGetRecognition() {
        liveHbTv.setVisibility(View.VISIBLE);
    }


    @Override
    public void onUpdateRoomInfo(LiveUpdateInfoBean liveUpdateInfoBean) {
        mLiveBean.setFirenum(liveUpdateInfoBean.getTotal());
        refreshFireNum();
    }

    @Override
    public void onGetRedPaper(LiveRedPaperBean bean) {
        showRedPaperView(bean);
    }

    @Override
    public void onShowStackGift(StackGiftBean bean, LiveBean liveBean) {
        mLiveAnimPresenter.addGiftDanmu(bean, liveBean);
    }

    @Override
    public void onShowRpResult(String uid, String name, String thumb, String total) {
        LiveRpResultDF liveRpResultDF = LiveRpResultDF.newInstance(name, thumb, total);
        liveRpResultDF.show(getSupportFragmentManager(), "resultRp");
    }

    @Override
    public void onTakeEggDanmu(String uName, EggKnockBean eggInfo, LiveBean liveInfo) {
        mLiveAnimPresenter.addEggDanmu(uName, eggInfo, liveInfo);
    }


    private void showRoomChargeDialog(String msg) {
        if (this == null || isFinishing() || isDestroyed()) {
            return;
        }
        if (mTimeChargeFragment == null) {
            mTimeChargeFragment = new TimeChargeFragment();
        }
        mTimeChargeFragment.setMessage(msg);
        if (!mTimeChargeFragment.isAdded()) {
            mTimeChargeFragment.show(mFragmentManager, "TimeChargeFragment");
        } else {
            mTimeChargeFragment.showMessage();
        }
    }

    /**
     * 计时收费的时候向主播交钱
     */
    public void roomCharge() {
//        HttpUtil.roomCharge(mLiveUid, mStream, mRoomChargeCallback);
//        mHandler.sendEmptyMessageDelayed(TIME_CHARGE, mTimeChargeInterval);
    }

    private HttpCallback mRoomChargeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                //发送socket更新直播间的映票
                SocketUtil.getInstance().updateVotes("0", mLiveTypeVal);
                hideBackgroundImage();
                if (mPullStreamPlayer != null) {
                    mPullStreamPlayer.resumePlay();
                }
            } else {
                mHandler.removeMessages(TIME_CHARGE);
                if (mPullStreamPlayer != null) {
                    mPullStreamPlayer.pausePlay();
                }
                if (mBg.getVisibility() == View.GONE) {
                    mBg.setVisibility(View.VISIBLE);
                }
                if (code == 1008) {//如果是余额不足，显示充值弹窗
                    showMoneyNotEnoughDialog(getString(R.string.coin_not_enough));
                } else {
                    ToastUtil.show(msg);
                }
            }
        }
    };


    /**
     * 定时器计时收费
     */
    private void timeCharge() {
        HttpUtil.timeCharge(mLiveUid, mStream, mTimeChargeCallback);
//        mHandler.sendEmptyMessageDelayed(TIME_CHARGE, mTimeChargeInterval);
    }

    private HttpCallback mTimeChargeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                //发送socket更新直播间的映票
                SocketUtil.getInstance().updateVotes("0", mLiveTypeVal);
            } else {
                mHandler.removeMessages(TIME_CHARGE);
                if (mPullStreamPlayer != null) {
                    mPullStreamPlayer.pausePlay();
                }
                if (mBg.getVisibility() == View.GONE) {
                    mBg.setVisibility(View.VISIBLE);
                }
                if (code == 1008) {//如果是余额不足，显示充值弹窗
                    showMoneyNotEnoughDialog(getString(R.string.coin_not_enough));
                } else {
                    ToastUtil.show(msg);
                }
            }
        }
    };


    /**
     * 显示余额不足的充值弹窗
     */
    private void showMoneyNotEnoughDialog(String msg) {
        DialogUitl.confirmDialog(mContext, getString(R.string.tip), msg, false, new DialogUitl.Callback() {
            @Override
            public void confirm(Dialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(mContext, ChargeActivity.class);
                intent.putExtra("from", "live");
                startActivityForResult(intent, CHARGE_CODE);
            }

            @Override
            public void cancel(Dialog dialog) {
                dialog.dismiss();
                onBackPressed();
            }
        }).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHARGE_CODE) {
            if (resultCode == RESULT_OK) {
                showRoomChargeDialog(getString(R.string.contiune_watch_live));
            } else {
                showMoneyNotEnoughDialog(getString(R.string.charge_failed));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
    }

    /**
     * 直播被关闭
     */
    @Override
    public void onLiveEnd() {
        closeRoom();
    }

    @Override
    public void closeRoom() {
        onClose();
        //停止播放
        if (mPullStreamPlayer != null) {
            mPullStreamPlayer.pausePlay();
        }
        //显示结束的fragment
        LiveAudienceEndFragment fragment = new LiveAudienceEndFragment();
        Bundle bundle = new Bundle();
        bundle.putString("anchorAvatar", mLiveBean.getAvatar());
        fragment.setArguments(bundle);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.wrap, fragment);
        if (mPaused) {
            ft.commitAllowingStateLoss();
        } else {
            ft.commit();
        }
    }

    @Override
    public void onNetWorkErrorCloseRoom() {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.SET_ATTENTION);
        HttpUtil.cancel(HttpUtil.SEND_GIFT);
        HttpUtil.cancel(HttpUtil.ENTER_ROOM);
        HttpUtil.cancel(HttpUtil.ROOM_CHARGE);
        HttpUtil.cancel(HttpUtil.TIME_CHARGE);
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
        super.onDestroy();
    }

    /**
     * 点击推送消息  先关闭房间然后进入推送房间
     *
     * @param e
     */
    @Override
    public void jpushEvent(JPushEvent e) {
        super.jpushEvent(e);
        if (mPullStreamPlayer != null) {
            mPullStreamPlayer.pausePlay();
        }
        finish();
        CheckLivePresenter mCheckLivePresenter = new CheckLivePresenter(mContext);
        mCheckLivePresenter.setSelectLiveBean(e);
        mCheckLivePresenter.watchLive();
    }

    private String starCount = "";


    public void sendMsg(String trim) {
        if (AppConfig.isUnlogin()) {
            unloginHint(mRoomName);
            return;
        }
        HttpUtil.sendTmsg(mLiveUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    JSONObject jsonObject = JSON.parseObject(info[0]);
                    if (jsonObject.containsKey("type") && jsonObject.getIntValue("type") == 0) {
                        if (AppConfig.getInstance().getUserBean() != null && AppConfig.getInstance().getUserBean().getIsbind() == 1) {
                            SocketUtil.getInstance().sendChatMsg(trim);
                            if (liveChatInputDF != null) {
                                liveChatInputDF.clearEt();
                            }
                        } else {
                            DialogUitl.confirmNoTitleDialog(LiveAudienceActivity.this, "为了您的账号安全请前往绑定手机!", "去绑定", true, new DialogUitl.Callback() {
                                @Override
                                public void confirm(Dialog dialog) {
                                    Intent intent = new Intent(LiveAudienceActivity.this, BindMobileActivity.class);
                                    intent.putExtra("intentType", 1);
                                    startActivity(intent);
                                }

                                @Override
                                public void cancel(Dialog dialog) {
                                }
                            }).show();
                        }
                    } else ToastUtil.show(msg);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    public void showRedPaperView(LiveRedPaperBean bean) {
//        mIsAttention
//        0未关注  1 已关注
        if (bean != null) {
            if (bean.getType() == 1) {
                LiveGetRandomRpDF liveGetRandomRpDF = LiveGetRandomRpDF.newInstance(mIsAttention, bean);
                liveGetRandomRpDF.show(getSupportFragmentManager(), "randomRp");
            } else {
                LiveGetCommandRpDF liveGetCommandRpDF = LiveGetCommandRpDF.newInstance(mIsAttention, bean);
                liveGetCommandRpDF.show(getSupportFragmentManager(), "commandRp");
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCatchLogin(LoginSuccessEvent event) {
        enterRoom();
    }

}
