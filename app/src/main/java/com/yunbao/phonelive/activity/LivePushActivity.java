package com.yunbao.phonelive.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.LiveChatListAdapter;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.bean.ReceiveDanMuBean;
import com.yunbao.phonelive.bean.ReceiveGiftBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.DragLayout;
import com.yunbao.phonelive.custom.FrameAnimImageView;
import com.yunbao.phonelive.custom.MyLinearLayoutManger;
import com.yunbao.phonelive.custom.NoAlphaItemAnimator;
import com.yunbao.phonelive.event.AttentionEvent;
import com.yunbao.phonelive.event.ConnEvent;
import com.yunbao.phonelive.event.LiveRoomCloseEvent;
import com.yunbao.phonelive.event.LivingEvent;
import com.yunbao.phonelive.event.NetKBEvent;
import com.yunbao.phonelive.fragment.LiveOrderFragment;
import com.yunbao.phonelive.fragment.NetWorkErrorFragment;
import com.yunbao.phonelive.glide.TopGradual;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.JIM;
import com.yunbao.phonelive.presenter.LiveAnimPresenter;
import com.yunbao.phonelive.socket.SocketMsgListener;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.tools.NetworkUtil;
import com.yunbao.phonelive.ui.tools.StringUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.ValidateUitl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2017/8/15.
 */

public abstract class LivePushActivity extends AbsActivity implements SocketMsgListener {

    private View mDecorView;
    protected DragLayout mRoot;//根布局
//    protected ImageView mBg;//背景图
    protected ViewGroup mWrap;//包裹内容区域的外层布局,里面可以动态添加动画等
//    protected ViewGroup mContent;//内容区域，包含常规界面聊天等 和游戏区域
    private RecyclerView mChatView;//聊天栏
    protected LiveChatListAdapter mChatListAdapter;
    protected FragmentManager mFragmentManager;
//    private int mScreenHeight;//屏幕的高度
    private boolean mSoftInputShowed;//软键盘是否开启的标识符
    private int mVisibleHeight;//可视区的高度
    private Rect mRect = new Rect();
    protected int beerTime = 0;
    protected ImageView mAnchorAvatar;//主播头像
    protected TextView mNums;//观看人数
    //    protected TextView mCoinName;//映票名字
//    protected TextView mVotes;//映票数
//    protected TextView mRoomName;//房间名字，默认是"房间"二字，如果有靓号，则是"房间：靓"
//    protected TextView mRoomNum;//房间号
    protected View mBtnAttention;//关注按钮
    protected LiveAnimPresenter mLiveAnimPresenter;//各种动画的Presenter
    protected LiveBean mLiveBean;//主播的信息
    protected String mBarrageFee;//弹幕价格
    protected String mVotestotal;//主播映票数量
    protected int mUserlistRefreshTime;//刷新用户列表的间隔时间
    protected String mKickTime;//踢人时间
    protected String mShutTime;//禁言时间
    protected int mNumsVal;//观众数量
    protected String mLiveUid;//主播uid
    protected String mStream;//当前直播间的stream
    //    private AuctionWindow mAuctionWindow;//竞拍窗口
    private boolean mMaxUserNum;//用户最大数量是否超过了20个
    //计时收费的message的what
    //定时刷新用户列表的message的what
    //直播暂停（主播直播暂停50秒后关闭直播）
    protected static final int LIVE_PAUSE = 2;
    protected boolean mEnd;
    protected Handler mHandler;
    //    protected GameManager mGameManager;
    private boolean mFirstConn;//是否是第一次连接socket
    private boolean mNetWorkBroken;//网络是否断开了
    private JIM mIM;
    protected Chronometer mChronometer;
    private TextView netStatusTv;
    private ImageView netStatus;
    private TextView wifiUploadSpeed, wifiDownloadSpeedTv;
    protected TextView roomNameTv;
    protected RelativeLayout beerRl, thankRl;
    protected TextView beerTimeTv, beerNumTv, thankTv, beerTypeTv;
    //加载底部布局的抽象方法
//    protected abstract LiveBottomFragment getBottomFragment();

//    private TextView wifiSpeed;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_push;
    }

    @Override
    public void preOnCreate() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.preOnCreate();
    }

    protected abstract void handleMsg(Message msg);

    @Override
    protected void main() {
        mDecorView = getWindow().getDecorView();
        mRoot = (DragLayout) findViewById(R.id.root);
        mRoot.post(new Runnable() {
            @Override
            public void run() {
//                mScreenHeight = mRoot.getHeight();
                if (mLiveAnimPresenter != null) {
                    mLiveAnimPresenter.setScreenDimens(mRoot.getWidth(), mRoot.getHeight());
                }
            }
        });
//        mBg = (ImageView) findViewById(R.id.bg);
        mWrap = (ViewGroup) findViewById(R.id.wrap);
        beerRl = findViewById(R.id.live_beer_rl);
        thankRl = findViewById(R.id.live_beer_thank_rl);
        beerTimeTv = findViewById(R.id.live_beer_time_tv);
        beerNumTv = findViewById(R.id.live_beer_num_tv);
        thankTv = findViewById(R.id.live_beer_thank_tv);
//        mGamePlaceholder = findViewById(R.id.repalced_game);
        wifiUploadSpeed = (TextView) findViewById(R.id.live_network_upload_tv);
        wifiDownloadSpeedTv = (TextView) findViewById(R.id.live_network_download_tv);
        mChatView = (RecyclerView) findViewById(R.id.chat_list);
        mChronometer = findViewById(R.id.live_timer_chr);
        netStatus = findViewById(R.id.live_network_status_iv);
        netStatusTv = findViewById(R.id.live_network_status_tv);
        roomNameTv = findViewById(R.id.anchor_uname_tv);
        beerTypeTv = findViewById(R.id.live_beer_type_tv);
        mChatView.setHasFixedSize(true);
        mChatView.setLayoutManager(new MyLinearLayoutManger(mContext, LinearLayoutManager.VERTICAL, false));
        mChatView.setItemAnimator(new NoAlphaItemAnimator());
        mChatView.addItemDecoration(new TopGradual());
        mChatListAdapter = new LiveChatListAdapter(mContext);

        mChatView.setAdapter(mChatListAdapter);
        mAnchorAvatar = (ImageView) findViewById(R.id.anchor_avatar);
        mNums = (TextView) findViewById(R.id.nums);
        mBtnAttention = findViewById(R.id.btn_attention);
        mLiveAnimPresenter = new LiveAnimPresenter(mContext);
        mLiveAnimPresenter.setEnterRoomView((TextView) findViewById(R.id.enter_room_anim));
        mLiveAnimPresenter.setAnimContainer(mWrap);
        mLiveAnimPresenter.setGiftAnimView((FrameAnimImageView) findViewById(R.id.gift_frame_anim));
        mLiveAnimPresenter.setEnterRoomAnimView(
                (FrameAnimImageView) findViewById(R.id.enter_room_frame_anim_1),
                (GifImageView) findViewById(R.id.enter_room_frame_anim_2));
        mLiveAnimPresenter.setEnterRoomWords((TextView) findViewById(R.id.enter_room_frame_words));
        mFragmentManager = getSupportFragmentManager();
        EventBus.getDefault().register(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                handleMsg(msg);
            }
        };
        mIM = new JIM();
        disposable.add(RxView.clicks(findViewById(R.id.prelive_close_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> showCloseDilaog()));

        disposable.add(RxView.clicks(findViewById(R.id.live_beer_thank_rl)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> beerThank2Server()));


    }

    protected abstract void beerThank2Server();


    /**
     * 打开排行榜窗口
     */
    private void openOrderListWindow() {
        LiveOrderFragment fragment = new LiveOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("touid", mLiveUid);
        bundle.putInt("type", 1);
        fragment.setArguments(bundle);
        fragment.show(mFragmentManager, "LiveOrderListFragment");
    }




    //接收已关注 未关注的切换事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAttentionEvent(AttentionEvent event) {
        if (event.getTouid().equals(mLiveUid)) {
            int isAttention = event.getIsAttention();
            if (isAttention == 1) {
                if (mBtnAttention.getVisibility() == View.VISIBLE) {
                    mBtnAttention.setVisibility(View.GONE);
                }
            } else {
                if (mBtnAttention.getVisibility() == View.GONE) {
                    mBtnAttention.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.SEND_BARRAGE);
        HttpUtil.cancel(HttpUtil.SET_AUCTION);
        HttpUtil.cancel(HttpUtil.AUCTION_END);
        HttpUtil.cancel(HttpUtil.SET_BID_PRICE);
        HttpUtil.cancel(HttpUtil.GET_COIN);
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mLiveAnimPresenter = null;
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
        super.onDestroy();

    }

    /**
     * 连接socket
     */
    protected void connectSocket() {
        SocketUtil.getInstance().connect(mLiveUid, mStream).setMessageListener(this);
    }

    @Override
    public void onConnect(boolean successConn) {
        L.e("连接socket------->" + successConn);
        //获取僵尸粉
        if (successConn) {
            if (!mFirstConn) {
                mFirstConn = true;
                SocketUtil.getInstance().getFakeFans();
            }
        }
    }


    /**
     * 发送聊天消息
     */
    public void sendChatMessage(String content) {
        SocketUtil.getInstance().sendChatMsg(content);
    }

    /**
     * 发送弹幕消息
     */
    public void sendDanmuMessage(String content) {
        HttpUtil.sendBarrage(content, mLiveUid, mStream, mDanmuCallback);
    }

    private HttpCallback mDanmuCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                AppConfig.getInstance().getUserBean().setLevel(obj.getIntValue("level"));
                SocketUtil.getInstance().sendDanmu(obj.getString("barragetoken"));
            } else {
                ToastUtil.show(msg);
            }
        }
    };


    //收到聊天消息，分为系统消息和用户消息，要在聊天栏里显示
    public void onChat(LiveChatBean bean) {
        L.e("收到聊天消息--->" + bean.getContent());
        mChatListAdapter.insertItem(bean);
//        if (bean.getType() == LiveChatBean.SYSTEM && bean.getContent().endsWith("关注了主播")) {
//            focusNum++;
//            refreshWatch();
//        }
    }


    //收到用户进房间消息
    public void onEnterRoom(UserBean bean) {
        if (this == null || isFinishing() || isDestroyed()) {
            return;
        }
        if (bean.getFtype() == 1) {
            LiveChatBean chatBean = new LiveChatBean();
            chatBean.setType(LiveChatBean.ENTER_ROOM);
            chatBean.setId(bean.getId());
            chatBean.setUser_nicename(bean.getUser_nicename());
            chatBean.setLevel(bean.getLevel());
            mChatListAdapter.insertItem(chatBean);
        }
        mNumsVal++;
        refreshWatch();
    }


    protected void refreshWatch() {
//        mNums.setText("观众: "+String.valueOf(mNumsVal));
        mNums.setText("观众:" + mNumsVal + "人   关注:" + focusNum + "人");
    }

    //收到用户离开房间消息
    public void onLeaveRoom(UserBean bean) {
        mNumsVal--;
        refreshWatch();
    }

    //往用户列表中添加僵尸粉
    @Override
    public void addFakeFans(List<UserBean> list) {
        mNumsVal += list.size();
        refreshWatch();
    }

    protected Disposable timerSubscribe;

    //收到送礼物消息
    public void onSendGift(ReceiveGiftBean bean) {
        L.e("收到送礼物消息--->" + bean.getGiftname());
        LiveChatBean chatBean = new LiveChatBean();
        chatBean.setContent("送了" + bean.getGiftcount() + "个" + bean.getGiftname());
        chatBean.setLevel(bean.getLevel());
        chatBean.setUser_nicename(bean.getUname());
        chatBean.setId(bean.getUid());
        chatBean.setType(LiveChatBean.GIFT);
        chatBean.setLiangName(bean.getLiangName());
        chatBean.setVipType(bean.getVipType());
        bean.setCount(Integer.valueOf(bean.getGiftcount()));
        mChatListAdapter.insertItem(chatBean);
        mLiveAnimPresenter.playGiftAnim(bean);
        if (bean.getCask() != null) {
            if (bean.getCask().getCask() == 2) {
                if (timerSubscribe != null) {
                    timerSubscribe.dispose();
                }
                beerTime = 0;
                String content = "";
                beerTimeTv.setVisibility(View.INVISIBLE);
                beerNumTv.setVisibility(View.INVISIBLE);
                if (bean.getCask().getCtype() == 1) {
                    beerTypeTv.setText("银酒桶");
                    content = "恭喜您完成了铜酒桶任务";
                } else if (bean.getCask().getCtype() == 2) {
                    beerTypeTv.setText("金酒桶");
                    content = "恭喜您完成了银酒桶任务";
                } else {
                    beerTypeTv.setText("铜酒桶");
                    content = "恭喜您完成了金酒桶任务";
                }
                thankTv.setVisibility(View.VISIBLE);
                DialogUitl.completeBeer(this, content).show();
            } else if (bean.getCask().getCask() == 1) {
                if (bean.getCask().getPnum() > 0) {
                    beerNumTv.setVisibility(View.VISIBLE);
                    thankTv.setVisibility(View.VISIBLE);
                    beerNumTv.setText(String.valueOf(bean.getCask().getPnum()));
                } else {
                    beerNumTv.setVisibility(View.INVISIBLE);
                }
            } else {
                beerNumTv.setVisibility(View.INVISIBLE);
                thankTv.setVisibility(View.INVISIBLE);
            }
        }
    }

    //收到弹幕消息
    public void onSendDanMu(ReceiveDanMuBean bean) {
//        mLiveAnimPresenter.addDanmu(bean);
//        mVotestotal = bean.getVotestotal();
//        mVotes.setText(mVotestotal);
    }

    /**
     * 主播关闭直播的时候
     */
    @Override
    public void onLiveEnd() {
        //子类各自实现自己的逻辑
    }

    /**
     * 超管关闭直播间
     */
    @Override
    public void onSuperCloseLive() {
        //子类各自实现自己的逻辑
    }

    /**
     * 观众被踢的时候
     */
    @Override
    public void onKick(String touid) {
        //子类各自实现自己的逻辑
    }

    /**
     * 观众被禁言的时候
     */
    @Override
    public void onShutUp(String touid, String content) {
        //子类各自实现自己的逻辑
    }

    /**
     * 主播切换计时收费的时候
     */
    @Override
    public void onChangeTimeCharge(int typeVal) {
        //子类各自实现自己的逻辑
    }

    @Override
    public void onDisConnect() {
        //子类各自实现自己的逻辑
    }

    /**
     * 计时收费的时候更新主播映票数
     */
    @Override
    public void updateVotes(String uid, int frist, int votes) {
        if (frist != 1 || !uid.equals(AppConfig.getInstance().getUid())) {
            mVotestotal = String.valueOf(Integer.parseInt(mVotestotal) + votes);
//            mVotes.setText(mVotestotal);
        }
    }

    /**
     * 主播发起竞拍
     */
    @Override
    public void auctionStart(boolean isAnchor, String auctionId, String thumb, String title, String startPrice, int duration) {
//        mAuctionWindow = new AuctionWindow(mContext, mWrap, isAnchor);
//        mAuctionWindow.setData(auctionId, thumb, title, startPrice, duration)
//                .show();
    }

    /**
     * 竞拍加价
     *
     * @param uhead     出价最多的人的头像
     * @param uname     出价最多的人的昵称
     * @param mostPrice 最高价格
     */
    @Override
    public void auctionAddMoney(String uhead, String uname, String mostPrice) {
//        if (mAuctionWindow != null) {
//            mAuctionWindow.showMostUser(uhead, uname, mostPrice);
//        }
    }

    /**
     * 流拍
     */
    @Override
    public void auctionFailure() {
//        if (mAuctionWindow != null) {
//            mAuctionWindow.auctionFailure();
//        }
    }

    /**
     * 竞拍成功
     *
     * @param uhead     出价最多的人的头像
     * @param uname     出价最多的人的昵称
     * @param mostPrice 最高价格
     */
    @Override
    public void auctionSuccess(String bidUid, String uhead, String uname, String mostPrice) {
//        if (mAuctionWindow != null) {
//            mAuctionWindow.auctionSuccess(bidUid, uhead, uname, mostPrice);
//        }
    }

    /**
     * 切断socket
     */
    protected void closeSocket() {
        SocketUtil.getInstance().close();
    }

    /**
     * 把新进来的观众添加到观众列表中
     */


    /**
     * 把离开房间的观众从观众列表中删除
     */


    /**
     * 当前直播间用户数大于20个后用这个刷新用户列表
     */
    protected void getUserList() {
        HttpUtil.getUserList(mLiveUid, mStream, mUserListCallback);
    }

    protected int focusNum = 0;
    private HttpCallback mUserListCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                mNumsVal = obj.getIntValue("nums");
                if (StringUtil.isNumeric(AppConfig.getInstance().getUserBean().getFans())) {
                    focusNum = Integer.valueOf(AppConfig.getInstance().getUserBean().getFans());
                }
                refreshWatch();
            }
        }
    };

    /**
     * 房间关闭的时候执行一些关闭操作
     */
    protected void onClose() {
        mEnd = true;
        EventBus.getDefault().post(new LiveRoomCloseEvent());
//        mGameManager.closeGame();//关闭游戏
        mLiveAnimPresenter.clearAnimQueue();//清空动画队列
        mRoot.setScrollView(null);
        //切断socket
        closeSocket();
    }

    public abstract void showCloseDilaog();

    //网络差，关闭直播间
    public abstract void onNetWorkErrorCloseRoom();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkEvent(ConnEvent e) {
        if (e.getCode() == ConnEvent.CONN_ERROR) {
            if (!mNetWorkBroken) {
                mNetWorkBroken = true;
                NetWorkErrorFragment fragment = new NetWorkErrorFragment();
                fragment.show(mFragmentManager, "NetWorkErrorFragment");
            }
        } else if (e.getCode() == ConnEvent.CONN_OK) {
            mNetWorkBroken = false;
        }
    }

    protected void switchCamera() {
    }

    protected void switchFlash() {
    }



    Disposable subscribe, pingSub;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(LivingEvent event) {
        if (event != null) {
            if (event.getType() == 0) {
//                start
                startChronometer();
                pingSub = Observable.interval(1, 1, TimeUnit.MINUTES)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            try {
                                doPing();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } else {
//                end
                stopChronometer();
                if (subscribe != null) {
                    subscribe.dispose();
                    subscribe = null;
                }
                if (pingSub != null) {
                    pingSub.dispose();
                    pingSub = null;
                }
            }
        }
    }

    protected void startChronometer() {
        if (mChronometer != null) {
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
        }
    }

    protected void stopChronometer() {
        if (mChronometer != null) {
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.stop();
        }
    }


    private void doPing() throws IOException {
        String delay;
        Process p = Runtime.getRuntime().exec("ping -c 4 " + "www.baidu.com");
        BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String str;
        while ((str = buf.readLine()) != null) {
            if (str.contains("avg")) {
                int i = str.indexOf("/", 20);
                int j = str.indexOf(".", i);
//                System.out.println("延迟:" + str.substring(i + 1, j));
                delay = str.substring(i + 1, j);
                if (netStatus != null && !TextUtils.isEmpty(delay) && ValidateUitl.isNumeric(delay)) {
                    int del = Integer.parseInt(delay);
                    if (del < 30) {
                        netStatus.setImageDrawable(getResources().getDrawable(R.mipmap.ic_network_status_xxxx));
                        netStatusTv.setText("优");
                    } else if (del < 50) {
                        netStatus.setImageDrawable(getResources().getDrawable(R.mipmap.ic_network_status_xxx));
                        netStatusTv.setText("良");
                    } else if (del < 100) {
                        netStatus.setImageDrawable(getResources().getDrawable(R.mipmap.ic_network_status_xx));
                        netStatusTv.setText("中");
                    } else {
                        netStatus.setImageDrawable(getResources().getDrawable(R.mipmap.ic_network_status_x));
                        netStatusTv.setText("超时");
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getNetKB(NetKBEvent event) {
        if (event != null) {
            wifiUploadSpeed.setText(NetworkUtil.getFormatSpeed(event.getUploadKb()));
            wifiDownloadSpeedTv.setText(getDownloadSpeed());
        }
    }

    /*** 获取网速大小 ***/
    private long lastDwonTotalRxBytes = 0;
    /*** 获取最后的时间戳 ***/
    private long lastDwonTimeStamp = 0;

    /****
     * 获取当前网速
     *
     * @return String 返回当前网速字符
     **/
    public String getDownloadSpeed() {
        String netSpeed;
        long nowTotalRxBytes = NetworkUtil.getDownloadRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long calculationTime = (nowTimeStamp - lastDwonTimeStamp);
        if (calculationTime == 0) {
            netSpeed = String.valueOf(1) + " kb/s";
            return netSpeed;
        }
        //毫秒转换
        long speed = ((nowTotalRxBytes - lastDwonTotalRxBytes) * 1000 / calculationTime);
        lastDwonTimeStamp = nowTimeStamp;
        lastDwonTotalRxBytes = nowTotalRxBytes;
        if (speed > 1024) {
            DecimalFormat df = new DecimalFormat("######0.0");
            netSpeed = String.valueOf(df.format(NetworkUtil.getM(speed))) + " MB/s";
        } else {
            netSpeed = String.valueOf(speed) + " kb/s";
        }
        return netSpeed;
    }


}
