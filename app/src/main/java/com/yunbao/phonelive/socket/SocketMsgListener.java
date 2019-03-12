package com.yunbao.phonelive.socket;

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

import java.util.List;

/**
 * Created by cxf on 2017/8/22.
 */

public interface SocketMsgListener {

    //连接成功socket后调用
    void onConnect(boolean successConn);

    //socket断开
    void onDisConnect();

    //收到聊天消息，分为系统消息和用户消息，要在聊天栏里显示
    void onChat(LiveChatBean bean);

    //收到飘心消息
    void onLight(LiveChatBean bean);

    //收到用户进房间消息
    void onEnterRoom(UserBean bean);

    //收到用户离开房间消息
    void onLeaveRoom(UserBean bean);

    //收到送礼物消息
    void onSendGift(ReceiveGiftBean bean);

    //收到弹幕消息
    void onSendDanMu(ReceiveDanMuBean bean);

    //观众收到直播结束消息
    void onLiveEnd();

    //超管关闭直播间
    void onSuperCloseLive();

    //踢人
    void onKick(String touid);

    //禁言
    void onShutUp(String touid, String content);

    //主播切换计时收费或更改计时收费价格的时候执行
    void onChangeTimeCharge(int typeVal);

    //主播切换计时收费或更改计时收费价格的时候，更新主播映票数
    void updateVotes(String uid, int frist, int votes);

    /**
     * 发起竞拍
     *
     * @param isAnchor   自己是不是主播
     * @param auctionId  竞拍的id
     * @param startPrice 起拍价格
     */
    void auctionStart(boolean isAnchor, String auctionId, String thumb, String title, String startPrice, int duration);

    /**
     * 竞拍加价
     *
     * @param uhead     出价最多的人的头像
     * @param uname     出价最多的人的昵称
     * @param mostPrice 最高价格
     */
    void auctionAddMoney(String uhead, String uname, String mostPrice);

    /**
     * 流拍
     */
    void auctionFailure();

    /**
     * 竞拍成功
     *
     * @param uhead     出价最多的人的头像
     * @param uname     出价最多的人的昵称
     * @param mostPrice 最高价格
     */
    void auctionSuccess(String bidUid, String uhead, String uname, String mostPrice);

    //添加僵尸粉
    void addFakeFans(List<UserBean> list);


    /**
     * 开启啤酒任务
     *
     * @param data 啤酒任务数据
     */
    void onStartTaskBeer(LiveBeerBean data);


    /**
     * 完成接口
     *
     * @param cType 酒桶任务 类型 1青铜2白银3黄金
     * @param num
     */
    void onCompleteTaskBeer(int cType, int num);

    /**
     * 完成接口
     */
    void onGetRecognition();

    /**
     * 更新直播间信息
     *
     * @param liveUpdateInfoBean
     */
    void onUpdateRoomInfo(LiveUpdateInfoBean liveUpdateInfoBean);

    void onGetRedPaper(LiveRedPaperBean bean);

    /**
     * 接收到全站礼物广播
     *
     * @param bean
     */
    void onShowStackGift(StackGiftBean bean, LiveBean liveBean);

    /**
     * 口令红包中奖结果通知
     *
     * @param uid   中奖人id
     * @param name  用户名称
     * @param thumb 用户头像
     * @param total 中奖内容
     */
    void onShowRpResult(String uid, String name, String thumb, String total);

    /**
     * 砸蛋信息弹幕
     *
     * @param uName    砸蛋用户名
     * @param eggInfo  砸中等级
     * @param liveInfo 直播间信息
     */
    void onTakeEggDanmu(String uName, EggKnockBean eggInfo, LiveBean liveInfo);
}
