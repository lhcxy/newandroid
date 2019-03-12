package com.yunbao.phonelive.ui.views.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.EggKnockBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.LiveBeerBean;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.bean.LiveRedPaperBean;
import com.yunbao.phonelive.bean.LiveUpdateInfoBean;
import com.yunbao.phonelive.bean.ReceiveDanMuBean;
import com.yunbao.phonelive.bean.ReceiveGiftBean;
import com.yunbao.phonelive.bean.StackGiftBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.fragment.LiveUserFragment;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.ChatRoomListener;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.presenter.LiveAnimPresenter;
import com.yunbao.phonelive.socket.SocketMsgListener;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.dialog.LiveEggEventDF;
import com.yunbao.phonelive.ui.dialog.LiveGetCommandRpDF;
import com.yunbao.phonelive.ui.dialog.LiveGetRandomRpDF;
import com.yunbao.phonelive.ui.dialog.LiveRpResultDF;
import com.yunbao.phonelive.ui.tools.StringUtil;
import com.yunbao.phonelive.ui.views.GiftDanmuPresenter;
import com.yunbao.phonelive.ui.views.adapter.LiveChatContentAdapter;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.yunbao.phonelive.bean.LiveChatBean.GIFT;

public class LiveChatFragment extends BaseLazyFragment implements SocketMsgListener, OnItemClickListener<String> {
    public static final String TAG = "LiveChat";
    private RecyclerView chatRv;
    private LiveChatContentAdapter adapter;
    private String liverId, stream;
    private TextView liveHbTv;
    private CompositeDisposable disposable;
    protected LiveAnimPresenter mLiveAnimPresenter;//各种动画的Presenter
    private RelativeLayout rootRl;

    private GiftDanmuPresenter danmuPresenter;

    public static LiveChatFragment newInstance(String LiverId, String stream) {
        Bundle bundle = new Bundle();
        bundle.putString("liverId", LiverId);
        bundle.putString("stream", stream);
        LiveChatFragment fragment = new LiveChatFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_live_chat;
    }

    private int mScreenHeight;
    private String mineId = "";

    @Override
    protected void initView() {
        if (!AppConfig.isUnlogin()) {
            mineId = AppConfig.getInstance().getUid();
        }
        disposable = new CompositeDisposable();
        chatRv = findView(R.id.live_chat_rv);
        rootRl = findView(R.id.live_chat_root_rl);
        liveHbTv = findView(R.id.live_beer_thank_hb_tv);
//        convertView.post(() -> {
//            mScreenHeight = convertView.getHeight();
//            if (mLiveAnimPresenter == null) {
//                mLiveAnimPresenter = new LiveAnimPresenter(getContext());
//                mLiveAnimPresenter.setAnimContainer(rootRl);
//            }
//            mLiveAnimPresenter.setScreenDimens(convertView.getWidth(), mScreenHeight);
//        });
        chatRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new LiveChatContentAdapter();
        adapter.setItemClickListener(this);
        chatRv.setAdapter(adapter);
        Bundle arguments = getArguments();
        if (arguments != null) {
            liverId = arguments.getString("liverId", "");
            stream = arguments.getString("stream", "");
        }

        disposable.add(RxView.clicks(findView(R.id.live_chat_egg_iv))
                .subscribe(o -> {
                    LiveEggEventDF liveEggEventDF = LiveEggEventDF.newInstance(0);
                    liveEggEventDF.setLiveInfo(mLiveBean);
                    liveEggEventDF.show(getChildFragmentManager(), LiveEggEventDF.class.getCanonicalName());
                }));
//        HttpUtil
//        HttpUtil.getFreeGift(getGiftCallback);

//        mLiveAnimPresenter = new LiveAnimPresenter(getContext());
//        mLiveAnimPresenter.setAnimContainer(rootRl);
        //danmuPresenter = new GiftDanmuPresenter(findView(R.id.chat_danmu_rl));
    }


//    protected LiveAnimPresenter mLiveAnimPresenter;//各种动画的Presenter


    private long ANIMATION_DURATION = 400L;

    /**
     * 人气+1 动画效果 位移+透明度
     */
    private void startAnimation() {

    }

    public void connectionSocket() {
//        AppConfig.getInstance().setSocketServer(mLiveData.getString("chatserver"));
        SocketUtil.getInstance().close();
        SocketUtil.getInstance().connect(liverId, stream).setMessageListener(this);
    }


    @Override
    protected void initData() {

        disposable.add(RxView.clicks(liveHbTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> HttpUtil.getBeerCaskThank(liverId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0 && !TextUtils.isEmpty(info[0])) {
                    JSONObject jsonObject = JSONObject.parseObject(info[0]);
                    if (jsonObject.containsKey("coin")) {
                        int coin = jsonObject.getIntValue("coin");
                        DialogUitl.completeBeer(getContext(), String.format(getResources().getString(R.string.txt_beer_bucket_get_recognition), coin)).show();
                    }
                }
                liveHbTv.setVisibility(View.INVISIBLE);
            }
        })));
        HttpUtil.getLivRpInfo(mLiveBean.getUid(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    LiveRedPaperBean liveRedPaperBean = JSON.parseObject(info[0], LiveRedPaperBean.class);
                    if (liveRedPaperBean != null) {
                        liveRedPaperBean.setTitle(liveRedPaperBean.getName());
                        liveRedPaperBean.setUid(liverId);
                        onGetRedPaper(liveRedPaperBean);
                    }
                }
            }
        });
    }

    @Override
    public void onConnect(boolean successConn) {
        Log.e(TAG, "onConnect: " + successConn);
    }

    @Override
    public void onDisConnect() {
        Log.e(TAG, "onDisConnect: ");
    }

    @Override
    public void onChat(LiveChatBean bean) {
        Log.e(TAG, "onChat: " + bean.toString());
        if (adapter != null) {
            adapter.addData(bean);
            chatRv.scrollToPosition(adapter.getItemCount() - 1);
        }
        if (listener != null) {
            listener.onSendChat(bean);
        }
    }

    private ChatRoomListener listener;

    @Override
    public void onLight(LiveChatBean bean) {
        adapter.addData(bean);
    }

    @Override
    public void onEnterRoom(UserBean bean) {
        Log.e(TAG, "onEnterRoom: " + bean.getUser_nicename());
        if (bean.getFtype() == 1) {
            LiveChatBean chatBean = new LiveChatBean();
            chatBean.setType(LiveChatBean.ENTER_ROOM);
            chatBean.setId(bean.getId());
            chatBean.setUser_nicename(bean.getUser_nicename());
            chatBean.setLevel(bean.getLevel());
            adapter.addData(chatBean);
            chatRv.scrollToPosition(adapter.getItemCount() - 1);
        }
        if (listener != null) {
            listener.onEnterRoom();
        }
    }

    @Override
    public void onLeaveRoom(UserBean bean) {
        if (listener != null) {
            listener.onLeaveRoom();
        }
    }

    @Override
    public void onSendGift(ReceiveGiftBean bean) {
        LiveChatBean data = new LiveChatBean();
        data.setType(GIFT);
        data.setUser_nicename(bean.getUname());
        data.setId(bean.getUid());
        data.setGiftcount(Integer.valueOf(bean.getGiftcount()));
        data.setGifticon(bean.getGifticon());
        data.setGiftid(bean.getGiftid());
        data.setLevel(bean.getLevel());
        bean.setCount(Integer.valueOf(bean.getGiftcount()));
        adapter.addData(data);
        chatRv.scrollToPosition(adapter.getItemCount() - 1);
        if (listener != null) {
            listener.onSenfGift(data);
            listener.onTakeGift(bean);
        }
        //关闭动画
       // mLiveAnimPresenter.playGiftAnim(bean);
    }

    @Override
    public void onSendDanMu(ReceiveDanMuBean bean) {

    }

    @Override
    public void onLiveEnd() {
        if (listener != null) {
            listener.onLiveEnd();
        }

    }

    @Override
    public void onSuperCloseLive() {

    }

    @Override
    public void onKick(String touid) {

    }

    @Override
    public void onShutUp(String touid, String content) {

    }

    @Override
    public void onChangeTimeCharge(int typeVal) {

    }

    @Override
    public void updateVotes(String uid, int frist, int votes) {

    }

    @Override
    public void auctionStart(boolean isAnchor, String auctionId, String thumb, String title, String startPrice, int duration) {

    }

    @Override
    public void auctionAddMoney(String uhead, String uname, String mostPrice) {

    }

    @Override
    public void auctionFailure() {

    }

    @Override
    public void auctionSuccess(String bidUid, String uhead, String uname, String mostPrice) {

    }

    @Override
    public void addFakeFans(List<UserBean> list) {
        if (listener != null && list != null) {
            listener.addFakeFans(list.size());
        }
    }

    @Override
    public void onStartTaskBeer(LiveBeerBean data) {
        if (listener != null) {
            listener.onStartBeer(data);
        }
    }

    @Override
    public void onCompleteTaskBeer(int cType, int num) {
        if (listener != null) {
            listener.onCompleteTaskBeer(cType, num);
        }
    }

    @Override
    public void onGetRecognition() {
        liveHbTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUpdateRoomInfo(LiveUpdateInfoBean liveUpdateInfoBean) {
        listener.onUpdateRoomInfo(liveUpdateInfoBean);
    }

    @Override
    public void onGetRedPaper(LiveRedPaperBean bean) {
        if (bean != null) {
            if (bean.getType() == 1) {
                LiveGetRandomRpDF liveGetRandomRpDF = LiveGetRandomRpDF.newInstance(mIsAttention, bean);
                liveGetRandomRpDF.show(getChildFragmentManager(), "randomRp");
            } else {
                LiveGetCommandRpDF liveGetCommandRpDF = LiveGetCommandRpDF.newInstance(mIsAttention, bean);
                liveGetCommandRpDF.show(getChildFragmentManager(), "commandRp");
            }
        }
    }

    @Override
    public void onShowStackGift(StackGiftBean bean, LiveBean liveBean) {
     //   danmuPresenter.addGiftDanmu(bean, liveBean);
        if (listener != null) {
            listener.onShowStackGift(bean, liveBean);
        }
//        danmuPresenter.addGiftDanmu();
    }

    @Override
    public void onShowRpResult(String uid, String name, String thumb, String total) {
        LiveRpResultDF liveRpResultDF = LiveRpResultDF.newInstance(name, thumb, total);
        liveRpResultDF.show(getChildFragmentManager(), "resultRp");
    }


    private int mIsAttention = 0;

    public void setIsAttention(int isAttention) {
        mIsAttention = isAttention;
    }


    @Override
    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
        closeSocket();
       // mLiveAnimPresenter = null;
        popInfo = null;
        super.onDestroy();
    }

    /**
     * 切断socket
     */
    protected void closeSocket() {
        SocketUtil.getInstance().close();
    }

    public void setListener(ChatRoomListener listener) {
        this.listener = listener;
    }

    private LiveBean mLiveBean;

    public void setLiveInfo(LiveBean mLiveBean) {
        this.mLiveBean = mLiveBean;
    }


    @Override
    public void onTakeEggDanmu(String uName, EggKnockBean eggInfo, LiveBean liveInfo) {
      //  danmuPresenter.addEggDanmu(uName, eggInfo, liveInfo);
        if (listener != null) {
            listener.onTakeEggDanmu(uName, eggInfo, liveInfo);
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
                    liveUserFragment.show(getChildFragmentManager(), LiveUserFragment.class.getCanonicalName());
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
