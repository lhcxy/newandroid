package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.EggKnockBean;
import com.yunbao.phonelive.bean.FunctionBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.LiveBeerBean;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.bean.LiveLrcBean;
import com.yunbao.phonelive.bean.LiveRedPaperBean;
import com.yunbao.phonelive.bean.LiveUpdateInfoBean;
import com.yunbao.phonelive.bean.ReceiveGiftBean;
import com.yunbao.phonelive.bean.StackGiftBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.fragment.LiveAnchorBottomFragment;
import com.yunbao.phonelive.fragment.LiveAnchorEndFragment;
import com.yunbao.phonelive.fragment.LiveAuctionFragment;
import com.yunbao.phonelive.fragment.LiveBottomFragment;
import com.yunbao.phonelive.fragment.LivePushStreamFragment;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by cxf on 2017/8/19.
 * 横屏   主播直播间
 */

public class LiveAnchorHorizontalActivity extends LivePushHorizontalActivity {

    //    private LiveFunctionFragment mFunctionFragment;//功能弹窗
    public LivePushStreamFragment mPushStreamFragment;//推流
    //private LiveEffectFragment mLiveEffectFragment;//美颜滤镜
//    private LiveMusicFragment mMusicFragment;//音乐
//    private LiveTimeChargeFragment mTimeChargeFragment;//计时收费弹窗
    private String mTypeVal;//计时收费的value
    private JSONObject mLiveData;//开播后返回的数据
    private boolean mPaused;

    @Override
    protected void main() {
        super.main();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        mActivityType = ACTIVITYTYPE_ANCHOR;
        showData();
        mLiveAnimPresenter.playLiveStartAnim();
        getUserList();
        getLiveInfo();
//        foreachGetLiveInfo();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_push_horizontal;
    }

    @Override
    protected LiveBottomFragment getBottomFragment() {
        return new LiveAnchorBottomFragment();
    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what) {
//            case USER_LIST:
//                getUserList();
//                break;
            case LIVE_PAUSE:
                endLive();
                break;
            case 0x111:
                hintChatRv();
                break;
        }
    }


    public void liveAnchorClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_function://功能
//                break;
            case R.id.btn_close://关闭
                showCloseDilaog();
                break;
        }
    }

    String pull = "";
    int orientation = 0, cameraType = 1;

    private void showData() {
        UserBean u = AppConfig.getInstance().getUserBean();
        mLiveUid = u.getId();
        Intent intent = getIntent();
        if (intent != null) {
            mTypeVal = intent.getStringExtra("typeVal");
            String title = intent.getStringExtra("title");
            if (!TextUtils.isEmpty(title)) {
                roomNameTv.setText(title);
            }
            mLiveData = JSON.parseObject(intent.getStringExtra("data"));
            mStream = mLiveData.getString("stream");
            pull = mLiveData.getString("pull_wheat");
            orientation = intent.getIntExtra("orientation", 0);
            cameraType = intent.getIntExtra("cameraType", 1);
            if (orientation == 1) {
                // 设置视频方向（横屏、竖屏）
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        mLiveBean = new LiveBean();
        mLiveBean.setUid(mLiveUid);
        mLiveBean.setUser_nicename(u.getUser_nicename());
        mLiveBean.setAvatar(u.getAvatar());
        mLiveBean.setThumb(u.getAvatar_thumb());
//        mLiveBean.setNums(mNumsVal + "");
        mLiveBean.setCity(u.getCity());
        mLiveBean.setPull(pull + mStream);
        mLiveBean.setStream(mStream);
        mBarrageFee = mLiveData.getString("barrage_fee");
        mVotestotal = mLiveData.getString("votestotal");
        mUserlistRefreshTime = mLiveData.getIntValue("userlist_time") * 1000;
        mKickTime = mLiveData.getString("kick_time");
        mShutTime = mLiveData.getString("shut_time");

        //显示主播头头像，观看人数，房间号等
//        ImgLoader.display(mLiveBean.getAvatar(), mAnchorAvatar);
//        mAnchorLevel.setImageResource(IconUitl.getAnchorLiveDrawable(u.getLevel_anchor()));
//        mNums.setText(String.valueOf(mNumsVal));
        //连接socket
//        AppConfig.getInstance().setSocketServer(mLiveData.getString("chatserver"));
        connectSocket();
        //加载推流fragment
        mPushStreamFragment = new LivePushStreamFragment();
        Bundle bundle = new Bundle();
        bundle.putString("streamUrl", mLiveData.getString("push"));
        bundle.putInt("orientation", orientation);
        bundle.putInt("cameraType", cameraType);
        mPushStreamFragment.setArguments(bundle);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.video_place, mPushStreamFragment).commitNow();


    }

    public void changeLive() {
        HttpUtil.changeLive(mStream, "1", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.e("开播--->" + info[0]);
            }
        });
    }

    /**
     * 功能按钮点击事件
     *
     * @param id
     */
    public void functionClick(int id) {
        switch (id) {
            case FunctionBean.TIME://计时收费
//                timeCharge();
                break;
            case FunctionBean.MEI_YAN://美颜
                openBeautyWindow();
                break;
            case FunctionBean.CAMERA://切换摄像头
                switchCamera();
                break;
            case FunctionBean.MUSIC://音乐伴奏
//                openMusicWindow();
                break;
            case FunctionBean.AUCTION://竞拍
                openAuctionWindow();
                break;
            case FunctionBean.FLASH://闪光灯
                toggleFlash();
                break;
        }
    }


//    /**
//     * 计时收费
//     */
//    private void timeCharge() {
//        if (mTimeChargeFragment == null) {
//            mTimeChargeFragment = new LiveTimeChargeFragment();
//            mTimeChargeFragment.setOnConfrimClick(new CommonCallback<String>() {
//                @Override
//                public void callback(String typeVal) {
//                    mTypeVal = typeVal;
//                    //切换收费模式
//                    HttpUtil.changeLiveType(mStream, mTypeVal, mChangeTypeCallback);
//                }
//            });
//        }
//        if (!mTimeChargeFragment.isAdded()) {
//            mTimeChargeFragment.show(getSupportFragmentManager(), "LiveTimeChargeFragment");
//        }
//    }

//
//    private HttpCallback mChangeTypeCallback = new HttpCallback() {
//        @Override
//        public void onSuccess(int code, String msg, String[] info) {
//            if (code == 0) {
//                ToastUtil.show(getString(R.string.change_time_charge));
//                SocketUtil.getInstance().changeTimeCharge(mTypeVal);
//            }
//        }
//    };


    /**
     * 切换摄像头
     */
    @Override
    protected void switchCamera() {
        if (mPushStreamFragment != null) {
            mPushStreamFragment.switchCamera();
        }
    }

    @Override
    protected void switchFlash() {
        if (mPushStreamFragment != null) {
            mPushStreamFragment.toggleFlash();
        }
    }

    /**
     * 开启或关闭闪光灯
     */
    private void toggleFlash() {
        if (mPushStreamFragment != null) {
            mPushStreamFragment.toggleFlash();
        }
    }

    /**
     * 打开美颜窗口
     */
    private void openBeautyWindow() {
//        //金山自带的旧版美颜
//        if(mLiveEffectFragment==null){
//            mLiveEffectFragment=new LiveEffectFragment();
//        }
//        mLiveEffectFragment.show(mFragmentManager, "LiveEffectFragment");

        /************分割线************/
    }

    /**
     * 各种美颜效果的回调
     */

    /**
     * 获取美颜数值
     *
     * @return
     */
    public int[] getBeautyData() {
        if (mPushStreamFragment != null) {
//            return mPushStreamFragment.getBeautyData();
        }
        return null;
    }

    /**
     * 设置美颜数值
     *
     * @param type
     * @param val
     */
    public void setBeautyData(int type, float val) {
//        if (mPushStreamFragment != null) {
//            mPushStreamFragment.setBeautyData(type, val);
//        }
    }

    /**
     * 设置特殊滤镜
     *
     * @param id
     */
    public void setSpecialFilter(int id) {
//        if (mPushStreamFragment != null) {
//            mPushStreamFragment.setSpecialFilter(id);
//        }
    }

//    /**
//     * 打开音乐搜索窗口
//     */
//    private void openMusicWindow() {
//        if (mMusicFragment == null) {
//            mMusicFragment = new LiveMusicFragment();
//        }
//        mMusicFragment.show(mFragmentManager, "LiveMusicFragment");
//    }

    /**
     * 播放音乐
     */
    public void playMusic(String musicId, LiveLrcBean bean) {
//        if (mPushStreamFragment != null) {
//            mPushStreamFragment.playMusic(mWrap, musicId, bean);
//        }
    }

    /**
     * 打开竞拍窗口
     */
    public void openAuctionWindow() {
        LiveAuctionFragment fragment = new LiveAuctionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stream", mStream);
        fragment.setArguments(bundle);
        fragment.show(mFragmentManager, "LiveAuctionFragment");
    }


//    /**
//     * 竞拍的时候显示小窗口
//     */
//    public void setCameraPreView(GLSurfaceView surfaceView) {
//        mPushStreamFragment.setCameraPreView(surfaceView);
//    }
//
//    /**
//     * 竞拍页面返回的时候恢复大窗口
//     */
//    public void setOriginCameraPreView() {
//        mPushStreamFragment.setOriginCameraPreView();
//    }


    @Override
    public void onBackPressed() {
        if (mEnd) {
            super.onBackPressed();
        } else {
            showCloseDilaog();
        }
    }

    /**
     * 显示关闭直播间的弹窗
     */
    @Override
    public void showCloseDilaog() {
        DialogUitl.confirmDialog(mContext,
                getString(R.string.tip),
                getString(R.string.are_you_end_live),
                new DialogUitl.Callback() {
                    @Override
                    public void confirm(Dialog dialog) {
                        dialog.dismiss();
                        endLive();
                    }

                    @Override
                    public void cancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                }
        ).show();
    }


    /**
     * 结束直播
     */
    private void endLive() {
        HttpUtil.stopRoom(mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    closeRoom();
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext, getString(R.string.closing_live));
            }
        });
    }

    /**
     * @param isSuperClose 是否是超管关闭的，如果是超管关闭的，要显示弹窗
     */
    private void closeLiveRoom(boolean isSuperClose) {
        onClose();
        //停止推流
        mPushStreamFragment.stopPushStream();
        //显示结束的fragment
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LiveAnchorEndFragment fragment = new LiveAnchorEndFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stream", mStream);
        bundle.putBoolean("isSuperClose", isSuperClose);
        fragment.setArguments(bundle);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.wrap, fragment);
        if (mPaused) {
            ft.commitAllowingStateLoss();
        } else {
            ft.commitNow();
        }
    }

    public void closeRoom() {
        closeLiveRoom(false);
    }

    /**
     * 超管关闭直播间
     */
    @Override
    public void onSuperCloseLive() {
        closeLiveRoom(true);
    }

    @Override
    public void onStartTaskBeer(LiveBeerBean data) {

    }

    @Override
    public void onCompleteTaskBeer(int cType, int num) {

    }

    @Override
    public void onGetRecognition() {
    }

    @Override
    public void onUpdateRoomInfo(LiveUpdateInfoBean liveUpdateInfoBean) {
        if (liveUpdateInfoBean != null) {
            AppConfig.getInstance().getUserBean().setFans(liveUpdateInfoBean.getFans());
            refreshWatch();
        }
    }

    @Override
    public void onGetRedPaper(LiveRedPaperBean bean) {
        // TODO: 2018/11/14 获取主播发送的红包
    }

    @Override
    public void onShowStackGift(StackGiftBean bean, LiveBean liveBean) {
        LiveChatBean chatBean = new LiveChatBean();
        chatBean.setContent("送了" + bean.getGiftcount() + "个" + bean.getGiftname());
        chatBean.setLevel(bean.getLevel());
        chatBean.setUser_nicename(bean.getUname());
        chatBean.setId(bean.getUid());
        chatBean.setType(LiveChatBean.GIFT);
        chatBean.setGiftcount(Integer.valueOf(bean.getGiftcount()));
        ReceiveGiftBean receiveGiftBean = new ReceiveGiftBean();
        receiveGiftBean.setLevel(bean.getLevel());
        receiveGiftBean.setUname(bean.getUname());
        receiveGiftBean.setGifticon(bean.getGifticon());
        receiveGiftBean.setGiftname(bean.getGiftname());
        receiveGiftBean.setGiftid(String.valueOf(bean.getGiftid()));
        receiveGiftBean.setGiftcount(String.valueOf(bean.getGiftcount()));
        receiveGiftBean.setUid(bean.getUid());
        receiveGiftBean.setCount(Integer.valueOf(bean.getGiftcount()));
        mChatListAdapter.insertItem(chatBean);
        mLiveAnimPresenter.playGiftAnim(receiveGiftBean);
    }

    @Override
    public void onShowRpResult(String uid, String name, String thumb, String total) {
    }

    @Override
    public void onTakeEggDanmu(String uName, EggKnockBean eggInfo, LiveBean liveInfo) {

    }

    @Override
    public void onNetWorkErrorCloseRoom() {
        closeRoom();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //主播离开50秒后关闭直播间
        mHandler.sendEmptyMessageDelayed(LIVE_PAUSE, 50 * 1000);
        mPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //主播在50秒之内回来了
        mHandler.removeMessages(LIVE_PAUSE);
        mPaused = false;
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.CHANGE_LIVE);
        HttpUtil.cancel(HttpUtil.STOP_ROOM);
        HttpUtil.cancel(HttpUtil.CHANGE_LIVE_TYPE);
        HttpUtil.cancel(HttpUtil.KICKING);
        HttpUtil.cancel(HttpUtil.SET_SHUT_UP);
        HttpUtil.cancel(HttpUtil.SUPER_STOP_ROOM);
        super.onDestroy();
    }

    @Override
    public void onDisConnect() {
    }

    @Override
    public void onLight(LiveChatBean bean) {

    }

    @Override
    public void onConnect(boolean successConn) {
        super.onConnect(successConn);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // 4.4以上系统，自动隐藏导航栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    @Override
    protected void beerThank2Server() {

        HttpUtil.beerCaskThank(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("答谢成功!");
                    SocketUtil.getInstance().beerCaskThank(info, mLiveUid);
                } else {
                    ToastUtil.show(msg);
                }
                thankTv.setVisibility(View.INVISIBLE);
            }
        });
    }


//    private void foreachGetLiveInfo() {
//        disposable.add(Flowable.interval(200, 180, TimeUnit.SECONDS).doOnNext(aLong ->
//                getLiveInfo()).subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe());
//    }

    Disposable rpSubscirbe;

    public void onIntervalRpDuration(int duration, int rpType, String rpId) {
        if (rpSubscirbe != null) {
            rpSubscirbe.dispose();
        }
        rpSubscirbe = Observable.intervalRange(0, duration, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aLong -> Log.e("//", "onIntervalRpDuration: " + aLong))
                .doOnComplete(() -> {
                    getCommandRpResult(rpType, rpId);
                    if (rpSubscirbe != null) {
                        rpSubscirbe.dispose();
                    }
                })
                .subscribe();
        disposable.add(rpSubscirbe);
    }

    private void getCommandRpResult(int rpType, String rpId) {
        HttpUtil.getLiveCommandResultRp(rpType, rpId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                Log.e("//", "onSuccess: " + info);
                if (info != null && info.length > 0) {
                    JSONObject object = JSON.parseObject(info[0]);
                    if (object != null && object.containsKey("uid") && object.containsKey("name") && object.containsKey("thumb") && object.containsKey("total")) {
                        SocketUtil.getInstance().sendRedPaperResult(object.getString("uid"), object.getString("name"), object.getString("thumb"), object.getString("total"));
                        SocketUtil.getInstance().sendSystemMessage(String.format(getString(R.string.live_redpaper_command_result_txt), object.getString("name"), object.getString("total")));
                    }
                }
            }
        });
    }
}
