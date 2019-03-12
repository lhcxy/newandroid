package com.yunbao.phonelive.socket;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.EggKnockBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.RandomUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by cxf on 2017/8/18.
 */

public class SocketUtil {
    private final String TAG = "socekt";
    private static SocketUtil sInstance;
    private SocketHandler mSocketHandler;
    private Socket mSocket;
    private String mLiveuid;
    private String mStream;
    private final String CONN = "conn";//连接
    private final String SEND = "broadcast";//发送
    private final String BROADCAST = "broadcastingListen";//socket广播
    private final String TASKBEERCOMPLETE = "TaskBeerComplete";//socket广播
    private final String SEND_MSGTOUID = "sendMsgToUid";//socket广播
    public static final int WHAT_CONN = 0;
    public static final int WHAT_DISCONN = 2;
    public static final int WHAT_BROADCAST = 1;

    public static final String STOP_PLAY = "stopplay";//超管关闭直播间
    public static final String STOP_LIVE = "stopLive";//超管关闭直播间
    public static final String SEND_MSG = "SendMsg";//发送文字消息，点亮，用户进房间  PS:这种混乱的设计是因为服务器端逻辑就是这样设计的,客户端无法自行修改
    public static final String LIGHT = "light";//飘心
    public static final String SEND_GIFT = "SendGift";//送礼物
    public static final String SEND_BARRAGE = "SendBarrage";//发弹幕
    public static final String LEAVE_ROOM = "disconnect";//用户离开房间
    public static final String LIVE_END = "StartEndLive";//主播关闭直播
    public static final String SYSTEM = "SystemNot";//系统消息
    public static final String KICK = "KickUser";//踢人
    public static final String SHUT_UP = "ShutUpUser";//禁言
    public static final String CHANGE_LIVE = "changeLive";//切换计时收费类型
    public static final String UPDATE_VOTES = "updateVotes";//计时收费或门票收费的时候更新主播的映票数
    public static final String AUCTION = "auction";//竞拍
    public static final String FAKE_FANS = "requestFans";//僵尸粉
    public static final String TASK_BEER_START = "TaskBeerStart";//开启酒桶任务
    public static final String TASK_BEER_COMPLETE = "TaskBeerComplete";//酒桶任务完成
    public static final String BEER_CASK_THANK = "Thanks";//主播答谢
    public static final String REDPAPER = "RedPaper";//主播发送红包
    public static final String SYSTEM_ALL = "systemall";//主播发送红包
    public static final String SEND_SYSTEM_ALL = "sendMsgAll";//主播发送红包


    private SocketUtil() {
        try {
            IO.Options option = new IO.Options();
            option.forceNew = true;
            option.reconnection = true;
            option.reconnectionDelay = 2000;
            mSocket = IO.socket(AppConfig.getInstance().getSocketServer(), option);
            mSocketHandler = new SocketHandler();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            L.e(TAG, "socket异常--->" + e.getMessage());
        }
    }

    public static SocketUtil getInstance() {
        if (sInstance == null) {
            synchronized (SocketUtil.class) {
                if (sInstance == null) {
                    sInstance = new SocketUtil();
                }
            }
        }
        return sInstance;
    }

    public void sendSocketMessage(JSONObject json) {
        if (mSocket != null) {
            mSocket.emit(SEND, json);
        }
    }


    private void sendData() {
        JSONObject data = new JSONObject();
        try {
            data.put("uid", AppConfig.getInstance().getUid());
            data.put("token", AppConfig.getInstance().getToken());
            data.put("liveuid", mLiveuid);
            data.put("roomnum", mLiveuid);
            data.put("stream", mStream);
            Log.e(TAG, "sendData: " + data);
            mSocket.emit("conn", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SocketUtil connect(String liveuid, String stream) {
        if (mSocket != null) {
            mSocket.on(Socket.EVENT_CONNECT, mConnectListener);//连接成功
            mSocket.on(Socket.EVENT_DISCONNECT, mDisConnectListener);//断开连接
            mSocket.on(Socket.EVENT_CONNECT_ERROR, mErrorListener);//连接错误
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, mTimeOutListener);//连接超时
            mSocket.on(Socket.EVENT_RECONNECT, mReConnectListener);//重连
            mSocket.on(CONN, onConn);//连接socket消息
            mSocket.on(BROADCAST, onBroadcast);//接收服务器广播的具体业务逻辑相关的消息
            mSocket.on(TASKBEERCOMPLETE, onBroadcast);
            mSocket.on(SEND_MSGTOUID, onBroadcast);
            mSocket.on(SYSTEM_ALL, onBroadcast);
//            mSocket.on("pushLiveOfThanks", onBroadcast);
            mSocket.connect();
            mLiveuid = liveuid;
            mStream = stream;
            sendData();
        }
        return this;
    }

    private Emitter.Listener mConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0)
                L.e(TAG, "--onConnect-->" + args[0]);
        }
    };

    private Emitter.Listener mReConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) L.e(TAG, "--reConnect-->" + args[0]);
            sendData();
        }
    };

    private Emitter.Listener mDisConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) L.e(TAG, "--onDisconnect-->" + args[0]);
            mSocketHandler.sendEmptyMessage(WHAT_DISCONN);
        }
    };
    private Emitter.Listener mErrorListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) L.e(TAG, "--onConnectError-->" + args[0]);
        }
    };

    private Emitter.Listener mTimeOutListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) L.e(TAG, "--onConnectError-->" + args[0]);
        }
    };

    private Emitter.Listener onBroadcast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONArray array = (JSONArray) args[0];
                for (int i = 0; i < array.length(); i++) {
                    Message msg = Message.obtain();
                    msg.what = WHAT_BROADCAST;
                    msg.obj = array.getString(i);
                    mSocketHandler.sendMessage(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onConn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                String s = ((JSONArray) args[0]).getString(0);
                L.e(TAG, "--onConn-->" + s);
                Message msg = Message.obtain();
                msg.what = WHAT_CONN;
                msg.obj = s.equals("ok");
                mSocketHandler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    public SocketUtil setMessageListener(SocketMsgListener listener) {
        mSocketHandler.setSocketMsgListener(listener);
        return this;
    }

//    public SocketUtil setGameManager(GameManager gameManager) {
//        mSocketHandler.setGameManager(gameManager);
//        return this;
//    }

    public void close() {
        if (mSocketHandler != null) {
            mSocketHandler.setSocketMsgListener(null);
            mSocketHandler.removeCallbacksAndMessages(null);
        }
        if (mSocket != null) {
            mSocket.off();
            mSocket.disconnect();
        }
    }

    /**
     * 发送普通发言消息
     */
    public void sendChatMsg(String content) {
        if (mSocket == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", SEND_MSG)
                        .param("action", 0)
                        .param("msgtype", 2)
                        .param("level", u.getLevel())
                        .param("uname", u.getUser_nicename())
                        .param("avatar", u.getAvatar_thumb())
                        .param("uid", u.getId())
                        .param("cnum", u.getCnum())
//                        .param("liangname", u.getLiang().getName())
//                        .param("vip_type", u.getVip().getType())
                        .param("ct", content)
                        .create()
        );
    }

    /**
     * 发送点亮消息
     */
    public void sendLightMsg() {
        if (mSocket == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", LIGHT)
                        .param("action", 0)
                        .param("msgtype", 2)
                        .param("level", u.getLevel())
                        .param("avatar", u.getAvatar_thumb())
                        .param("uname", u.getUser_nicename())
                        .param("uid", u.getId())
                        .param("cnum", u.getCnum())
                        .param("heart", RandomUtil.getRandom(1, 5))
//                        .param("liangname", u.getLiang().getName())
//                        .param("vip_type", u.getVip().getType())
                        .param("ct", WordUtil.getString(R.string.I_am_lighted_hlb))
                        .create()
        );
    }


    /**
     * 发送飘心消息
     */
    public void sendFloatHeart() {
        if (mSocket == null) {
            return;
        }
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", LIGHT)
                        .param("action", 2)
                        .param("msgtype", 0)
                        .param("ct", "")
                        .create()
        );
    }

    /**
     * 发送礼物消息
     *
     * @param evensend
     * @param giftToken
     */
    public void sendGift(String evensend, String casktoken, String giftToken) {
        if (mSocket == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        SendSocketBean param = new SendSocketBean()
                .param("_method_", SEND_GIFT)
                .param("action", 0)
                .param("msgtype", 1)
                .param("level", u.getLevel())
                .param("avatar", u.getAvatar_thumb())
                .param("uname", u.getUser_nicename())
                .param("uid", u.getId())
                .param("uhead", u.getAvatar())
                .param("evensend", evensend)
                .param("cnum", u.getCnum())
//                .param("liangname", u.getLiang().getName())
//                .param("vip_type", u.getVip().getType())
                .param("ct", giftToken);
        if (!TextUtils.isEmpty(casktoken)) {
            param.param("casktoken", casktoken);
        }
        mSocket.emit(SEND,
                param.create()
        );
    }

    /**
     * 发送弹幕消息
     */
    public void sendDanmu(String barragetoken) {
        if (mSocket == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", SEND_BARRAGE)
                        .param("action", 7)
                        .param("msgtype", 1)
                        .param("avatar", u.getAvatar_thumb())
                        .param("level", u.getLevel())
                        .param("uname", u.getUser_nicename())
                        .param("uid", u.getId())
                        .param("cnum", u.getCnum())
                        .param("uhead", u.getAvatar())
                        .param("ct", barragetoken)
                        .create()
        );
    }

    /**
     * 主播或管理员 踢人
     */
    public void kickUser(String touid, String toname) {
        if (mSocket == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", KICK)
                        .param("action", 2)
                        .param("msgtype", 4)
                        .param("level", u.getLevel())
                        .param("uname", u.getUser_nicename())
                        .param("avatar", u.getAvatar_thumb())
                        .param("uid", u.getId())
                        .param("touid", touid)
                        .param("toname", toname)
                        .param("cnum", u.getCnum())
                        .param("ct", toname + WordUtil.getString(R.string.be_kicked))
                        .create()
        );
    }

    /**
     * 主播或管理员 禁言
     */
    public void shutUpUser(String touid, String toname, String shut_time) {
        if (mSocket == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", SHUT_UP)
                        .param("action", 1)
                        .param("msgtype", 4)
                        .param("level", u.getLevel())
                        .param("uname", u.getUser_nicename())
                        .param("avatar", u.getAvatar_thumb())
                        .param("uid", u.getId())
                        .param("touid", touid)
                        .param("toname", toname)
                        .param("cnum", u.getCnum())
                        .param("ct", toname + WordUtil.getString(R.string.be_shut) + shut_time)
                        .create()
        );
    }

    /**
     * 发送系统消息
     */
    public void sendSystemMessage(String touid, String toname, String content) {
        if (mSocket == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", SYSTEM)
                        .param("action", 13)
                        .param("msgtype", 4)
                        .param("level", u.getLevel())
                        .param("avatar", u.getAvatar_thumb())
                        .param("uname", u.getUser_nicename())
                        .param("uid", u.getId())
                        .param("touid", touid)
                        .param("toname", toname)
                        .param("cnum", u.getCnum())
                        .param("ct", content)
                        .create()
        );
    }

    /**
     * 发送系统消息
     */
    public void sendSystemMessage(String content) {
        if (mSocket == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u != null) {
            mSocket.emit(SEND,
                    new SendSocketBean()
                            .param("_method_", SYSTEM)
                            .param("action", 13)
                            .param("msgtype", 4)
                            .param("level", u.getLevel())
                            .param("avatar", u.getAvatar_thumb())
                            .param("uname", u.getUser_nicename())
                            .param("uid", u.getId())
                            .param("cnum", u.getCnum())
                            .param("ct", content)
                            .create()
            );
        }

    }

    /**
     * 切换计时收费
     */
    public void changeTimeCharge(String typeVal) {
        if (mSocket == null) {
            return;
        }
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", CHANGE_LIVE)
                        .param("action", 1)
                        .param("msgtype", 27)
                        .param("type_val", typeVal)
                        .param("ct", "")
                        .create()
        );
    }

    /**
     * 计时收费或门票收费的时候更新主播的映票数
     *
     * @param isfirst 是否是在当前直播间第一次发这个socket，
     *                如果是第一发socket,则不更新映票了，因为在直播间外面已经扣费了，进来之后的映票数是真实的
     * @param typeVal 每次扣费的值，也就是给主播映票数加的值
     */
    public void updateVotes(String isfirst, int typeVal) {
        if (mSocket == null) {
            return;
        }
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", UPDATE_VOTES)
                        .param("action", 1)
                        .param("msgtype", 26)
                        .param("votes", typeVal)
                        .param("uid", AppConfig.getInstance().getUid())
                        .param("isfirst", isfirst)
                        .param("ct", "")
                        .create()
        );
    }

    /**
     * 发起竞拍
     */
    public void auctionStart(String auctionid) {
        if (mSocket == null) {
            return;
        }
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", AUCTION)
                        .param("action", 1)
                        .param("msgtype", 55)
                        .param("auctionid", auctionid)
                        .create()
        );
    }

    /**
     * 竞拍加价
     */
    public void auctionAddMoney(String money) {
        if (mSocket == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", AUCTION)
                        .param("action", 2)
                        .param("msgtype", 55)
                        .param("level", u.getLevel())
                        .param("avatar", u.getAvatar_thumb())
                        .param("uname", u.getUser_nicename())
                        .param("uid", u.getId())
                        .param("uhead", u.getAvatar())
                        .param("money", money)
                        .param("cnum", u.getCnum())
                        .param("ct", "")
                        .create()
        );
    }


    /**
     * 竞拍结束
     */
    public void auctionEnd(int action, String bidUid, String toname, String touhead, String money) {
        if (mSocket == null) {
            return;
        }
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", AUCTION)
                        .param("action", action)
                        .param("msgtype", 55)
                        .param("toname", toname)
                        .param("touhead", touhead)
                        .param("bid_uid", bidUid)
                        .param("money", money)
                        .param("ct", "")
                        .create()
        );
    }

    /**
     * 获取僵尸粉
     */
    public void getFakeFans() {
        if (mSocket == null) {
            return;
        }
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", FAKE_FANS)
                        .param("action", "")
                        .param("msgtype", "")
                        .create()
        );
    }

    /**
     * 超管关闭直播间
     */
    public void stopLive() {
        if (mSocket == null) {
            return;
        }
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", STOP_LIVE)
                        .param("action", 19)
                        .param("msgtype", 1)
                        .param("ct", "")
                        .create()
        );
    }

    /**
     * 开启酒桶任务
     *
     * @param casktoken
     */
    public void taskBeerStart(String casktoken) {
        if (mSocket == null) {
            return;
        }
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", TASK_BEER_START)
                        .param("action", 0)
                        .param("islive", 1)
                        .param("casktoken", casktoken)
                        .create()
        );
    }

    public void comTaskBeer(String casktoken, int cType) {
        if (mSocket == null) {
            return;
        }
        mSocket.emit(SEND,
                new SendSocketBean()
                        .param("_method_", TASK_BEER_COMPLETE)
                        .param("action", 0)
                        .param("ctype", cType)
                        .param("info", casktoken)
                        .create()
        );
    }

    public void beerCaskThank(String[] info, String liveid) {
        if (mSocket == null) {
            return;
        }
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("_method_", BEER_CASK_THANK);
        stringObjectHashMap.put("action", 0);
        stringObjectHashMap.put("uids", info);
        stringObjectHashMap.put("liveid", liveid);
        mSocket.emit(SEND,
                new SendSocketBean()
                        .create(stringObjectHashMap)
        );
    }


    /**
     * 主播发送红包
     *
     * @param type 红包类型  1-随机红包 2-口令兔尾巴红包 3-口令自定义红包
     * @param time 红包可领取时长 逾期不可领取
     */
    public void sendRedPaper(int type, int time, String commandStr, String title) {
        if (mSocket == null) {
            return;
        }
        SendSocketBean param = new SendSocketBean()
                .param("_method_", REDPAPER)
                .param("action", 0)
                .param("type", type)
                .param("time", time)
                .param("uid", AppConfig.getInstance().getUserBean().getId());
        if (type == 2) {
            param.param("title", title);
            param.param("koul", commandStr);
        }

        mSocket.emit(SEND,
                param.create()
        );
    }

    /**
     * 口令红包中奖结果通知
     *
     * @param uid   中奖人id
     * @param name  用户名称
     * @param thumb 用户头像
     * @param total 中奖内容
     */
    public void sendRedPaperResult(String uid, String name, String thumb, String total) {
        if (mSocket == null) {
            return;
        }
        SendSocketBean param = new SendSocketBean()
                .param("_method_", "EndRedp")
                .param("action", 0)
                .param("uid", uid)
                .param("thumb", thumb)
                .param("total", total)
                .param("name", name);

        mSocket.emit(SEND,
                param.create()
        );
    }

    public void sendStackGift(String giftToken, LiveBean liveBean) {
        if (mSocket == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("_method_", SEND_SYSTEM_ALL);
        stringObjectHashMap.put("action", 0);
        stringObjectHashMap.put("msgtype", "0");
        stringObjectHashMap.put("uname", u.getUser_nicename());
        stringObjectHashMap.put("uid", u.getId());
        stringObjectHashMap.put("uhead", u.getAvatar());
//        stringObjectHashMap.put("liangname", u.getLiang().getName());
//        stringObjectHashMap.put("vip_type", u.getVip().getType());
        stringObjectHashMap.put("ct", giftToken);
        stringObjectHashMap.put("liveInfo", JSON.parseObject(JSON.toJSONString(liveBean)));
        mSocket.emit(SYSTEM_ALL,
                new SendSocketBean()
                        .create(stringObjectHashMap)
        );
    }

    public void sendEggDanmu(EggKnockBean eggInfo, LiveBean liveBean) {
        if (mSocket == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("_method_", SEND_SYSTEM_ALL);
        stringObjectHashMap.put("action", 0);
        stringObjectHashMap.put("msgtype", "1");
        stringObjectHashMap.put("uname", u.getUser_nicename());
        stringObjectHashMap.put("uid", u.getId());
        stringObjectHashMap.put("uhead", u.getAvatar());
//        stringObjectHashMap.put("liangname", u.getLiang().getName());
//        stringObjectHashMap.put("vip_type", u.getVip().getType());
        stringObjectHashMap.put("eggInfo", JSON.parseObject(JSON.toJSONString(eggInfo)));
        stringObjectHashMap.put("liveInfo", JSON.parseObject(JSON.toJSONString(liveBean)));
        mSocket.emit(SYSTEM_ALL,
                new SendSocketBean()
                        .create(stringObjectHashMap)
        );
    }

}
