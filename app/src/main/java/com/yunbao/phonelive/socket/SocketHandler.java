package com.yunbao.phonelive.socket;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import java.util.List;

/**
 * Created by cxf on 2017/8/22.
 */

public class SocketHandler extends Handler {

    private SocketMsgListener mListener;

    @Override
    public void handleMessage(Message msg) {
        if (mListener == null) {
            return;
        }
        Log.e("收到socket--->", "handleMessage: " + msg.what + "//" + msg.obj);
        switch (msg.what) {
            case SocketUtil.WHAT_CONN:
                mListener.onConnect((Boolean) msg.obj);
                break;
            case SocketUtil.WHAT_BROADCAST:
                processBroadcast((String) msg.obj);
                break;
            case SocketUtil.WHAT_DISCONN:
                mListener.onDisConnect();
                break;
        }

    }


    private void processBroadcast(String socketMsg) {
        L.e("收到socket--->" + socketMsg);
        if (mListener == null) {
            return;
        }
        if (SocketUtil.STOP_PLAY.equals(socketMsg)) {
            mListener.onSuperCloseLive();//超管关闭房间
            return;
        }
        ReceiveSocketBean received = JSON.parseObject(socketMsg, ReceiveSocketBean.class);
        JSONObject map = received.getMsg().getJSONObject(0);
        switch (map.getString("_method_")) {
            case SocketUtil.SYSTEM://系统消息
                systemChatMessage(map.getString("ct"));
                break;
            case SocketUtil.KICK://踢人
                systemChatMessage(map.getString("ct"));
                mListener.onKick(map.getString("touid"));
                break;
            case SocketUtil.SHUT_UP://禁言
                String ct = map.getString("ct");
                systemChatMessage(ct);
                mListener.onShutUp(map.getString("touid"), ct);
                break;
            case SocketUtil.SEND_MSG://文字消息，点亮，用户进房间
                if (map.containsKey("msgtype")) {
                    String msgtype = map.getString("msgtype");
                    if ("2".equals(msgtype)) {//发言，点亮
                        if ("409002".equals(received.getRetcode())) {
                            ToastUtil.show(WordUtil.getString(R.string.you_are_shut));
                            return;
                        }
                        LiveChatBean chatBean = new LiveChatBean();
                        chatBean.setId(map.getString("uid"));
                        chatBean.setUser_nicename(map.getString("uname"));
                        chatBean.setLevel(map.getIntValue("level"));
                        chatBean.setContent(map.getString("ct"));
                        chatBean.setHeart(map.getIntValue("heart"));
                        chatBean.setLiangName(map.getString("liangname"));
                        chatBean.setVipType(map.getIntValue("vip_type"));
                        mListener.onChat(chatBean);
                    } else if ("0".equals(msgtype)) {//用户进入房间
                        JSONObject obj = JSON.parseObject(map.getString("ct"));
                        UserBean u = JSON.toJavaObject(obj, UserBean.class);
                        UserBean.Vip vip = new UserBean.Vip();
                        vip.setType(obj.getIntValue("vip_type"));
                        u.setFtype(map.getInteger("ftype"));
//                        u.setVip(vip);
//                    UserBean.Car car = new UserBean.Car();
//                    car.setId(obj.getIntValue("car_id"));
//                    car.setSwf(obj.getString("car_swf"));
//                    car.setSwftime(obj.getFloatValue("car_swftime"));
//                    car.setWords(obj.getString("car_words"));
//                    u.setCar(car);
                        mListener.onEnterRoom(u);
                    }
                } else if (map.containsKey("hottype")) {
                    String hottype = map.getString("hottype");
                    if ("1".equals(hottype)) {
                        LiveUpdateInfoBean u = JSON.toJavaObject(map.getJSONObject("ct"), LiveUpdateInfoBean.class);
                        if (u != null) {
                            mListener.onUpdateRoomInfo(u);
                        }
                    }
                }

                break;
            case SocketUtil.LIGHT://飘心
                LiveChatBean chatBean = new LiveChatBean();
                chatBean.setId(map.getString("uid"));
                chatBean.setUser_nicename(map.getString("uname"));
                chatBean.setContent(map.getString("ct"));
                chatBean.setLiangName(map.getString("liangname"));
                chatBean.setVipType(map.getIntValue("vip_type"));
                chatBean.setLevel(map.getIntValue("level"));
//                mListener.onChat(chatBean);
                mListener.onLight(chatBean);
                break;
            case SocketUtil.SEND_GIFT://送礼物
                ReceiveGiftBean receiveGiftBean = JSON.parseObject(map.getString("ct"), ReceiveGiftBean.class);
                receiveGiftBean.setUhead(map.getString("uhead"));
                receiveGiftBean.setUname(map.getString("uname"));
                receiveGiftBean.setEvensend(map.getString("evensend"));
                receiveGiftBean.setLiangName(map.getString("liangname"));
                receiveGiftBean.setVipType(map.getIntValue("vip_type"));
                if (receiveGiftBean.getCask() == null) {
                    if (map.containsKey("casktoken") && !TextUtils.isEmpty(map.getString("casktoken"))) {
                        receiveGiftBean.setCask(JSON.parseObject(map.getString("casktoken"), LiveBeerBean.class));
                    }
                }
                mListener.onSendGift(receiveGiftBean);
                break;
            case SocketUtil.SEND_BARRAGE://发弹幕
                ReceiveDanMuBean receiveDanMuBean = JSON.parseObject(map.getString("ct"), ReceiveDanMuBean.class);
                receiveDanMuBean.setUhead(map.getString("uhead"));
                receiveDanMuBean.setUname(map.getString("uname"));
                mListener.onSendDanMu(receiveDanMuBean);
                break;
            case SocketUtil.LEAVE_ROOM://离开房间
                UserBean u = JSON.parseObject(map.getString("ct"), UserBean.class);
                mListener.onLeaveRoom(u);
                break;
            case SocketUtil.LIVE_END://主播关闭直播
                mListener.onLiveEnd();
                break;
            case SocketUtil.CHANGE_LIVE://主播切换计时收费类型
                mListener.onChangeTimeCharge(map.getIntValue("type_val"));
                break;
            case SocketUtil.UPDATE_VOTES:
                mListener.updateVotes(map.getString("uid"), map.getIntValue("isfirst"), map.getIntValue("votes"));
                break;
            case SocketUtil.AUCTION://竞拍
                processAuction(map);
                break;
            case SocketUtil.FAKE_FANS:
                JSONObject obj = map.getJSONObject("ct");
                String s = obj.getJSONObject("data").getJSONArray("info").getJSONObject(0).getString("list");
                List<UserBean> list = JSON.parseArray(s, UserBean.class);
                mListener.addFakeFans(list);
                break;

            case SocketUtil.TASK_BEER_START:  // 开启酒桶任务
                LiveBeerBean beerBean = JSON.parseObject(map.getString("casktoken"), LiveBeerBean.class);
                mListener.onStartTaskBeer(beerBean);
                break;
            case SocketUtil.TASK_BEER_COMPLETE: //酒桶任务完成
                mListener.onCompleteTaskBeer(map.getIntValue("ctype"), map.getIntValue("num"));
                break;
            case SocketUtil.BEER_CASK_THANK: //获得主播答谢
                mListener.onGetRecognition();
                break;
            case SocketUtil.REDPAPER:
                LiveRedPaperBean liveRedPaperBean = JSON.parseObject(JSON.toJSONString(map), LiveRedPaperBean.class);
                mListener.onGetRedPaper(liveRedPaperBean);
                break;

            case SocketUtil.SEND_SYSTEM_ALL:
                if (map.containsKey("msgtype")) {
                    String msgtype = map.getString("msgtype");
                    if ("0".equals(msgtype)) {//送豪华礼物全站广播协议

                        StackGiftBean bean = JSON.parseObject(map.getString("ct"), StackGiftBean.class);
                        bean.setUid(map.getString("uid"));
                        bean.setUname(map.getString("uname"));
                        mListener.onShowStackGift(bean, JSON.parseObject(map.getString("liveInfo"), LiveBean.class));
                    } else if ("1".equals(msgtype)) { //砸金蛋大于6级全站广播协议
                        mListener.onTakeEggDanmu(map.getString("uname"), JSON.parseObject(map.getString("eggInfo"), EggKnockBean.class), JSON.parseObject(map.getString("liveInfo"), LiveBean.class));
                    }
                }
                break;
            case "EndRedp":
                mListener.onShowRpResult(map.getString("uid"), map.getString("name"), map.getString("thumb"), map.getString("total"));
                break;
        }
    }


    /**
     * 处理竞拍的逻辑
     */
    private void processAuction(JSONObject map) {
        if (mListener == null) {
            return;
        }
        switch (map.getIntValue("action")) {
            case 1://主播发起竞拍
                JSONObject auction = JSON.parseObject(map.getString("ct"));
                mListener.auctionStart(
                        AppConfig.getInstance().getUid().equals(auction.getString("uid")),
                        auction.getString("id"),
                        auction.getString("thumb"),
                        auction.getString("title"),
                        auction.getString("price_start"),
                        auction.getIntValue("long"));
                break;
            case 2://竞拍加价
                mListener.auctionAddMoney(map.getString("uhead"), map.getString("uname"), map.getString("money"));
                break;
            case 3://流拍
                mListener.auctionFailure();
                break;
            case 4://竞拍成功
                mListener.auctionSuccess(map.getString("bid_uid"), map.getString("touhead"), map.getString("toname"), map.getString("money"));
                break;
        }
    }

    /**
     * 接收到系统消息，显示在聊天栏中
     *
     * @param content
     * @return
     */
    private LiveChatBean systemChatMessage(String content) {

        LiveChatBean bean = new LiveChatBean();
        bean.setContent(content);
        bean.setType(LiveChatBean.SYSTEM);
        if (mListener != null) {
            mListener.onChat(bean);
        }
        return bean;
    }

    public void setSocketMsgListener(SocketMsgListener listener) {
        mListener = listener;
    }

//    public void setGameManager(GameManager gameManager) {
//        mGameManager = gameManager;
//    }
}
