package com.yunbao.phonelive.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.LiveChatListAdapter;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.LiveBeerBean;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.bean.LiveUserBean;
import com.yunbao.phonelive.bean.ReceiveDanMuBean;
import com.yunbao.phonelive.bean.ReceiveGiftBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.DragLayout;
import com.yunbao.phonelive.custom.MyLinearLayoutManger;
import com.yunbao.phonelive.custom.NoAlphaItemAnimator;
import com.yunbao.phonelive.custom.auction.AuctionWindow;
import com.yunbao.phonelive.event.AttentionEvent;
import com.yunbao.phonelive.event.ConnEvent;
import com.yunbao.phonelive.event.IgnoreUnReadEvent;
import com.yunbao.phonelive.event.LiveRoomCloseEvent;
import com.yunbao.phonelive.event.VisibleHeightEvent;
import com.yunbao.phonelive.fragment.EMChatFragment;
import com.yunbao.phonelive.fragment.EMChatRoomFragment;
import com.yunbao.phonelive.fragment.LiveAdminListFragment;
import com.yunbao.phonelive.fragment.LiveBottomFragment;
import com.yunbao.phonelive.fragment.LiveInputFragment;
import com.yunbao.phonelive.fragment.LiveSettingFragment;
import com.yunbao.phonelive.fragment.LiveUserFragment;
import com.yunbao.phonelive.fragment.LiveUserInfoFragment;
import com.yunbao.phonelive.fragment.NetWorkErrorFragment;
import com.yunbao.phonelive.glide.TopGradual;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.JIM;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.presenter.LiveAnimPresenter;
import com.yunbao.phonelive.socket.SocketMsgListener;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.dialog.LiveChatInputDF;
import com.yunbao.phonelive.ui.dialog.LiveContributionDF;
import com.yunbao.phonelive.ui.views.adapter.LiveAudienceChatContentAdapter;
import com.yunbao.phonelive.ui.views.adapter.LiveChatContentAdapter;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.yunbao.phonelive.bean.LiveChatBean.GIFT;

/**
 * Created by cxf on 2017/8/15.
 */

public abstract class LiveActivity extends AbsActivity implements SocketMsgListener, OnItemClickListener<String> {

    private View mDecorView;
    protected DragLayout mRoot;//根布局
    protected ImageView mBg;//背景图
    protected ViewGroup mWrap;//包裹内容区域的外层布局,里面可以动态添加动画等
    protected ViewGroup mContent;//内容区域，包含常规界面聊天等 和游戏区域
    private View mGamePlaceholder;//游戏区域
    private RecyclerView mChatView;//聊天栏
    private View mBottom;//底部栏
    protected LiveAudienceChatContentAdapter mChatListAdapter;
    protected FragmentManager mFragmentManager;
    protected LiveBottomFragment mBottomFragment;//底部菜单
    private int mUnReadCount;//环信未读消息数量
    private int mScreenHeight;//屏幕的高度
    private LiveInputFragment mInputFragment;//输入框
    private boolean mSoftInputShowed;//软键盘是否开启的标识符
    private boolean mChatRoomFragmentShowed;//私信聊天窗口是否打开的标识符
    private int mVisibleHeight;//可视区的高度
    private Rect mRect = new Rect();

    protected ImageView mAnchorAvatar;//主播头像
    //    protected ImageView mAnchorLevel;//主播等级
    protected TextView mNums;//观看人数
    protected TextView mRoomName;//房间名字，默认是"房间"二字，如果有靓号，则是"房间：靓"
    protected TextView mBtnAttention;//关注按钮
    protected LiveBean mLiveBean;//主播的信息
    protected String mBarrageFee;//弹幕价格
    protected int mUserlistRefreshTime;//刷新用户列表的间隔时间
    protected String mKickTime;//踢人时间
    protected String mShutTime;//禁言时间
    //    protected int mNumsVal;//观众数量
    //    protected List<UserBean> mUserList;//观众列表
    protected LiveAnimPresenter mLiveAnimPresenter;//各种动画的Presenter
    protected String mLiveUid;//主播uid
    protected String mStream;//当前直播间的stream
    private AuctionWindow mAuctionWindow;//竞拍窗口
    //计时收费的message的what
    protected static final int TIME_CHARGE = 0;
    //定时刷新用户列表的message的what
    protected static final int USER_LIST = 1;
    //直播暂停（主播直播暂停50秒后关闭直播）
    protected static final int LIVE_PAUSE = 2;
    protected boolean mEnd;
    protected Handler mHandler;
    private boolean mFirstConn;//是否是第一次连接socket
    private boolean mNetWorkBroken;//网络是否断开了
    private JIM mIM;
    protected TextView liveNameTv, liveHbTv;
    LiveUserBean mLiveinfo;
    public int mIsAttention;//是否关注了主播

    //加载底部布局的抽象方法
    protected abstract LiveBottomFragment getBottomFragment();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live;
    }

    protected abstract void handleMsg(Message msg);

    private String mineId = "";

    @Override
    protected void main() {
        mDecorView = getWindow().getDecorView();
        mRoot = (DragLayout) findViewById(R.id.root);
        mRoot.post(new Runnable() {
            @Override
            public void run() {
                mScreenHeight = mRoot.getHeight();
                mLiveAnimPresenter.setScreenDimens(mRoot.getWidth(), mScreenHeight);
            }
        });

        if (!AppConfig.isUnlogin()) {
            mineId = AppConfig.getInstance().getUid();
        }

        mBg = (ImageView) findViewById(R.id.bg);
        mWrap = (ViewGroup) findViewById(R.id.wrap);
        mContent = (ViewGroup) findViewById(R.id.content);
        mBottom = findViewById(R.id.repalced_bottom);
        mGamePlaceholder = findViewById(R.id.repalced_game);
        liveNameTv = findViewById(R.id.live_name_tv);
        mChatView = findViewById(R.id.chat_list);
        liveHbTv = findViewById(R.id.live_beer_thank_hb_tv);
        mChatView.setHasFixedSize(true);
        mChatView.setLayoutManager(new MyLinearLayoutManger(mContext, LinearLayoutManager.VERTICAL, false));
        mChatView.setItemAnimator(new NoAlphaItemAnimator());
        mChatView.addItemDecoration(new TopGradual());
//        mChatListAdapter = new LiveChatListAdapter(mContext);
        mChatListAdapter = new LiveAudienceChatContentAdapter();
        mChatListAdapter.setItemClickListener(this::onItemClick);
//        mChatListAdapter.setOnItemClickListener((item, position) -> {
//            if (item.getType() != LiveChatBean.SYSTEM) {//如果不是系统消息，点击打开弹窗
//                openUserInfoDialog(item.getId());
//            }
//        });
        mChatView.setAdapter(mChatListAdapter);
        mAnchorAvatar = (ImageView) findViewById(R.id.anchor_avatar);
//        mAnchorLevel = (ImageView) findViewById(R.id.anchor_level);
        mNums = (TextView) findViewById(R.id.nums);
        mRoomName = (TextView) findViewById(R.id.room_name);
        mBtnAttention = findViewById(R.id.btn_attention);
        mLiveAnimPresenter = new LiveAnimPresenter(mContext);
        mLiveAnimPresenter.setAnimContainer(mWrap);
        mBottomFragment = getBottomFragment();
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.repalced_bottom, mBottomFragment).commit();
        EventBus.getDefault().register(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                handleMsg(msg);
            }
        };
        mIM = new JIM();

    }

    private float mainViewY = 0;
    /**
     * 添加布局变化的监听器,用来监听键盘弹出和收回
     */
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            //获取当前界面可视部分
            mDecorView.getWindowVisibleDisplayFrame(mRect);
            mainViewY = mContent.getY();
            int visibleHeight = mRect.height();
            if (mVisibleHeight == visibleHeight) {
                return;
            }
            mVisibleHeight = visibleHeight;

            L.e("onGlobalLayout-----mVisibleHeight----->" + mVisibleHeight);

            //情况① ：可视区高度发生变化时，要通知EMChatRoomFragment，使其调整自身大小
            if (mChatRoomFragmentShowed) {
                EventBus.getDefault().post(new VisibleHeightEvent(mVisibleHeight));
                return;
            }

            //情况② 可视区高度发生变化时，要使聊天栏上移
            //具体逻辑如下
            //先计算出 软键盘的高度 =屏幕的高度- 可视区的高度
            int softInputHeight = mScreenHeight - mVisibleHeight;
            if (softInputHeight > 0) {//软键盘弹出
                mSoftInputShowed = true;
                //游戏区域的高度
                int gameHeight = mGamePlaceholder.getHeight();
                //算出差值
                int dY = softInputHeight - gameHeight;
                if (dY > 0) {//软键盘比游戏区域高，产生了遮挡
                    if (mInputFragment != null) {
                        mContent.setY(-dY);
                        mInputFragment.translateY(-dY);
                    }
                }
            } else {//软键盘收回
                if (mSoftInputShowed) {
                    mSoftInputShowed = false;
//                    mContent.translateY(0);
                    mContent.setTranslationY(mainViewY + DpUtil.dp2px(20));
                    if (mInputFragment != null && !mInputFragment.isHided()) {
                        mInputFragment.translateY(0);
                    } else {
                        removeLayoutListener();
                    }
                }
            }
        }
    };

    /**
     * 添加布局变化的监听器
     */
    public void addLayoutListener() {
        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
//        L.e("onGlobalLayout-----添加onGlobalLayout--->");
    }

    /**
     * 移除布局变化的监听器
     */
    public void removeLayoutListener() {
        mRoot.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
//        L.e("onGlobalLayout-----移除onGlobalLayout--->");
    }

    public void setChatRoomFragmentShowed(boolean showed) {
        mChatRoomFragmentShowed = showed;
    }

    public void showBottom() {
        if (mBottom != null && mBottom.getVisibility() != View.VISIBLE) {
            mBottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        showUnReadCount();
    }

    public void liveClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chat://聊天发言
                openChatWindow();
                break;
            case R.id.btn_msg://私信
                openChatListWindow();
                break;
            case R.id.room_num://点击映票排行榜
                openOrderListWindow();
                break;
            case R.id.anchor_avatar://点击主播头像
                openUserInfoDialog(mLiveUid);
                break;
        }
    }

    protected LiveChatInputDF liveChatInputDF;

    /**
     * 打开聊天输入框
     */
    public void openChatWindow() {
        addLayoutListener();
//        if (mBottom.getVisibility() == View.VISIBLE) {
//            mBottom.setVisibility(View.INVISIBLE);
//        }
//        mInputFragment = new LiveInputFragment();
//        int y = mScreenHeight - mGamePlaceholder.getHeight() - DpUtil.dp2px(40);
//        Bundle bundle = new Bundle();
//        bundle.putInt("y", y);
//        bundle.putString("barrage_fee", mBarrageFee);
//        mInputFragment.setArguments(bundle);
//        mInputFragment.show(mFragmentManager, "LiveInputFragment");
        if (liveChatInputDF == null) {
            liveChatInputDF = LiveChatInputDF.newInstance();
            liveChatInputDF.setViewType(1);
        }
        liveChatInputDF.show(getSupportFragmentManager(), "input");
    }

    /**
     * 打开私信聊天列表
     */
    public void openChatListWindow() {
        EMChatFragment chatFragment = new EMChatFragment();
        chatFragment.setIM(mIM);
        Bundle bundle = new Bundle();
        bundle.putInt("from", 1);
        chatFragment.setArguments(bundle);
        chatFragment.show(mFragmentManager, "EMChatFragment");
    }

    /**
     * 打开私信聊天窗口
     */
    public void openChatRoomWindow(UserBean bean, int isAttention) {
//        EMChatRoomFragment chatRoomFragment = new EMChatRoomFragment();
        EMChatRoomFragment chatRoomFragment = EMChatRoomFragment.newInstance(bean);
        chatRoomFragment.setIM(mIM);
        Bundle bundle = new Bundle();
        bundle.putInt("from", 1);
        bundle.putParcelable("touser", bean);
        bundle.putInt("isAttention", isAttention);
        bundle.putBoolean("fromPop", true);
        chatRoomFragment.setArguments(bundle);
        chatRoomFragment.show(mFragmentManager, "EMChatRoomFragment");
    }

    /**
     * 打开设置窗口,踢人,禁言，设置管理员等
     */
    public void openSettingWindow(int action, UserBean bean) {
        LiveSettingFragment fragment = new LiveSettingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("action", action);
        bundle.putParcelable("user", bean);
        bundle.putString("liveuid", mLiveUid);
        bundle.putString("kick_time", mKickTime);
        bundle.putString("shut_time", mShutTime);
        fragment.setArguments(bundle);
        fragment.show(mFragmentManager, "LiveSettingFragment");
    }

    /**
     * 打开管理员列表窗口
     */
    public void openAdminListWindow() {
        LiveAdminListFragment fragment = new LiveAdminListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("liveuid", mLiveUid);
        fragment.setArguments(bundle);
        fragment.show(mFragmentManager, "LiveAdminListFragment");
    }

    /**
     * 打开排行榜窗口
     */
    private void openOrderListWindow() {
//        LiveOrderFragment fragment = new LiveOrderFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("touid", mLiveUid);
//        bundle.putInt("type", 1);
//        fragment.setArguments(bundle);
//        fragment.show(mFragmentManager, "LiveOrderListFragment");

        LiveContributionDF liveContributionDF = LiveContributionDF.newInstance(mLiveUid);
        liveContributionDF.show(mFragmentManager, "LiveOrderListFragment");
    }


    /**
     * 打开个人信息弹窗
     */
    private void openUserInfoDialog(String touid) {
        LiveUserInfoFragment fragment = LiveUserInfoFragment.newInstance(mLiveinfo, mIsAttention == 1);
//        Bundle bundle = new Bundle();
//        bundle.putString("touid", touid);
//        bundle.putString("liveuid", mLiveUid);
//        bundle.putString("roomName", mLiveBean.getTitle());
//        fragment.setArguments(bundle);
        fragment.show(mFragmentManager, "LiveUserInfoFragment");
    }

//    public void showUnReadCount() {
//        mUnReadCount = mIM.getAllUnReadCount();
////        L.e("IM", "未读消息数量---->" + mUnReadCount);
//        mBottomFragment.setUnReadCount(mUnReadCount);
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void ignoreUnRead(IgnoreUnReadEvent e) {
//        mUnReadCount = 0;
//        mBottomFragment.setUnReadCount(mUnReadCount);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void receiveMessage(cn.jpush.im.android.api.model.Message message) {
//        mUnReadCount++;
//        mBottomFragment.setUnReadCount(mUnReadCount);
//    }

    //接收已关注 未关注的切换事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAttentionEvent(AttentionEvent event) {
        if (event.getTouid().equals(mLiveUid)) {
            int isAttention = event.getIsAttention();
            if (isAttention == 1) {
                if (mBtnAttention.getVisibility() == View.VISIBLE) {
                    mBtnAttention.setVisibility(View.GONE);
                }
//                mBtnAttention.setText("取消关注");
            } else {
                if (mBtnAttention.getVisibility() == View.GONE) {
                    mBtnAttention.setVisibility(View.VISIBLE);
                }
                mBtnAttention.setText("关注");
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
        HttpUtil.cancel(HttpUtil.GET_USER_LIST);
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mLiveAnimPresenter = null;
        super.onDestroy();

    }

    /**
     * 连接socket
     */
    protected void connectSocket() {
        SocketUtil.getInstance().close();
        SocketUtil.getInstance().connect(mLiveUid, mStream).setMessageListener(this);
//        SocketUtil.getInstance().connect(mLiveUid, mStream).setMessageListener(this).setGameManager(mGameManager);
    }

    @Override
    public void onConnect(boolean successConn) {
//        L.e("连接socket------->" + successConn);
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
//        L.e("收到聊天消息--->" + bean.getContent());
        mChatListAdapter.addData(bean);

    }

    //收到飘心消息
    public void onLight() {
        if (this == null || isFinishing() || isDestroyed() || mLiveAnimPresenter == null) {
            return;
        }
        mLiveAnimPresenter.playFloatHeartAnim();
    }

    //收到用户进房间消息
    public void onEnterRoom(UserBean bean) {
        if (this == null || isFinishing() || isDestroyed()) {
            return;
        }
//        if (mLiveAnimPresenter != null) {
//            mLiveAnimPresenter.playEnterRoomAnim(bean);
//        }
        if (bean.getFtype() == 1) {
            LiveChatBean chatBean = new LiveChatBean();
            chatBean.setType(LiveChatBean.ENTER_ROOM);
            chatBean.setId(bean.getId());
            chatBean.setUser_nicename(bean.getUser_nicename());
            chatBean.setLevel(bean.getLevel());
            mChatListAdapter.addData(chatBean);
        }
//        mNumsVal++;
//        refreshFireNum();
    }

    //收到用户离开房间消息
    public void onLeaveRoom(UserBean bean) {
//        mNumsVal--;
//        refreshFireNum();
    }

    private int fireNum = 0; //人气

    //往用户列表中添加僵尸粉
    @Override
    public void addFakeFans(List<UserBean> list) {
//        mNumsVal += list.size();
//        refreshFireNum();
    }

    //收到送礼物消息
    public void onSendGift(ReceiveGiftBean bean) {
//        L.e("收到送礼物消息--->" + bean.getGiftname());
        LiveChatBean chatBean = new LiveChatBean();
        chatBean.setLevel(bean.getLevel());
        chatBean.setUser_nicename(bean.getUname());
        chatBean.setId(bean.getUid());
        chatBean.setType(LiveChatBean.GIFT);
        chatBean.setLiangName(bean.getLiangName());
        chatBean.setVipType(bean.getVipType());
        chatBean.setGiftcount(Integer.valueOf(bean.getGiftcount()));
        chatBean.setGifticon(bean.getGifticon());
        chatBean.setGiftid(bean.getGiftid());
        chatBean.setLevel(bean.getLevel());

        bean.setCount(Integer.valueOf(bean.getGiftcount()));
        mChatListAdapter.addData(chatBean);
        mLiveAnimPresenter.playGiftAnim(bean);

        if (bean.getCask() != null) {
            taskBeerStep(bean.getCask());
            if (bean.getCask().getCask() == 2) {
                SocketUtil.getInstance().comTaskBeer(bean.getCask().getInfo(), bean.getCask().getCtype());
            }
        }

    }

    protected void taskBeerStep(LiveBeerBean data) {

    }

    //收到弹幕消息
    public void onSendDanMu(ReceiveDanMuBean bean) {
//        mLiveAnimPresenter.addDanmu(bean);
    }

    protected void refreshFireNum() {
//        mNums.setText("观众: " + mNumsVal + "  人气:" + mLiveBean.getFireNum());
        mNums.setText("热度:" + mLiveBean.getFireNums());
    }

    /**
     * 主播关闭直播的时候
     */
    @Override
    public void onLiveEnd() {
        //子类各自实现自己的逻辑
    }


    protected void getLiveInfo() {
        HttpUtil.getLiveInfo(mLiveUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                mLiveinfo = JSON.toJavaObject(obj, LiveUserBean.class);
                if (mLiveinfo != null) {
//                        liveChatFragment.setShowId(bean.getShowid());
                    fireNum = mLiveinfo.getFirenum();
                    mLiveBean.setFirenum(mLiveinfo.getFirenum());
                    mIsAttention = mLiveBean.getIsAttention();
                    refreshFireNum();
                }
            }
        });
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
    }

    /**
     * 主播发起竞拍
     */
    @Override
    public void auctionStart(boolean isAnchor, String auctionId, String thumb, String title, String startPrice, int duration) {
        mAuctionWindow = new AuctionWindow(mContext, mWrap, isAnchor);
        mAuctionWindow.setData(auctionId, thumb, title, startPrice, duration)
                .show();
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
        if (mAuctionWindow != null) {
            mAuctionWindow.showMostUser(uhead, uname, mostPrice);
        }
    }

    /**
     * 流拍
     */
    @Override
    public void auctionFailure() {
        if (mAuctionWindow != null) {
            mAuctionWindow.auctionFailure();
        }
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
        if (mAuctionWindow != null) {
            mAuctionWindow.auctionSuccess(bidUid, uhead, uname, mostPrice);
        }
    }

    /**
     * 切断socket
     */
    protected void closeSocket() {
        if (SocketUtil.getInstance() != null) {
            SocketUtil.getInstance().close();
        }
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
//        mHandler.sendEmptyMessageDelayed(USER_LIST, mUserlistRefreshTime);
    }

    private HttpCallback mUserListCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                JSONObject obj = JSON.parseObject(info[0]);
//                mNumsVal = obj.getIntValue("nums");
                refreshFireNum();
            }
        }
    };

    /**
     * 房间关闭的时候执行一些关闭操作
     */
    protected void onClose() {
        mEnd = true;
        EventBus.getDefault().post(new LiveRoomCloseEvent());
        if (mLiveAnimPresenter != null) {
            mLiveAnimPresenter.clearAnimQueue();//清空动画队列
        }
        if (mRoot != null) {
            mRoot.setScrollView(null);
        }
        //切断socket
        closeSocket();
    }

    public abstract void closeRoom();

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


    private boolean canGetUserInfo = true;

    @Override
    public void onItemClick(String item, int position) {
        if (canGetUserInfo && !mineId.equals(item)) {
            canGetUserInfo = false;
            HttpUtil.getPop(item, mLiveBean.getUid(), popInfo);
        }

    }

    LiveUserFragment liveUserFragment;
    private HttpCallback popInfo = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info != null && info.length > 0) {
                UserBean userBean = JSON.parseObject(info[0], UserBean.class);
                if (userBean != null) {
                    liveUserFragment = LiveUserFragment.newInstance(userBean, 0);
                    liveUserFragment.show(getSupportFragmentManager(), LiveUserFragment.class.getCanonicalName());
                }
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            canGetUserInfo = true;
        }
    };

}
