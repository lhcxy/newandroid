package com.yunbao.phonelive.interfaces;

import com.yunbao.phonelive.bean.EggKnockBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.LiveBeerBean;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.bean.LiveUpdateInfoBean;
import com.yunbao.phonelive.bean.ReceiveGiftBean;
import com.yunbao.phonelive.bean.StackGiftBean;

public interface ChatRoomListener {
    void onSendChat(LiveChatBean bean);

    void onSenfGift(LiveChatBean bean);

    void onTakeGift(ReceiveGiftBean bean);

    void onStartBeer(LiveBeerBean bean);

    void onEnterRoom();

    void onLeaveRoom();

    void addFakeFans(int size);

    void onCompleteTaskBeer(int cType, int num);

    void onLiveEnd();

    void onUpdateRoomInfo(LiveUpdateInfoBean liveUpdateInfoBean);

    void onShowStackGift(StackGiftBean bean, LiveBean liveBean);

    void onTakeEggDanmu(String uName, EggKnockBean eggInfo, LiveBean liveInfo);
}
