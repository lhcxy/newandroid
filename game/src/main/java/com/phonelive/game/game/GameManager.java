package com.phonelive.game.game;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.bean.UserBean;
import com.phonelive.game.game.ebb.GameEbbPresenter;
import com.phonelive.game.game.hd.GameHaiDaoPresenter;
import com.phonelive.game.game.luckpan.GameLuckPanFragment;
import com.phonelive.game.game.luckpan.GameLuckPanPresenter;
import com.phonelive.game.game.nz.GameNiuZaiPresenter;
import com.phonelive.game.game.zjh.GameJinHuaPresenter;
import com.yunbao.phonelive.socket.SendSocketBean;
import com.yunbao.phonelive.socket.SocketUtil;

/**
 * Created by cxf on 2017/10/7.
 */

public class GameManager {

    private AbsGamePresenter mJinHuaPresenter;
    private AbsGamePresenter mHaiDaoPresenter;
    private GameNiuZaiPresenter mNiuZaiPresenter;
    private GameLuckPanPresenter mLuckPanPresenter;
    private AbsGamePresenter mEbbPresenter;
    private AbsGamePresenter mCurGamePresenter;//当前正在执行的游戏
    protected Context mContext;
    protected FragmentManager mFragmentManager;
    protected String mStream;
    protected String mLiveuid;
    protected boolean mAnchorClosedGame;//主播关闭了游戏

    //进入直播间的时候，开心牛仔游戏的上庄信息
    private ViewGroup mBankerGroup;
    private String mBankerId;
    private String mBankerName;
    private String mBankerAvatar;
    private String mBankerCoin;
    private String mBankerLimit;

    public GameManager(Context context, FragmentManager fragmentManager, String stream, String liveuid) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mStream = stream;
        mLiveuid = liveuid;
    }

    public void processGame(JSONObject obj) {
        int action = obj.getIntValue("action");
        String method = obj.getString("_method_");
        if (action == 1) {
            if (!mAnchorClosedGame && mCurGamePresenter != null) {
                mCurGamePresenter.gameReplaced();
            }
            setCurGamePresenter(method);
            mAnchorClosedGame = false;
        } else if (action == 2 || action == 4 && method.equals(GameConst.SOKCET_GAME_LUCK_PAN)) {
            if (mCurGamePresenter == null) {
                setCurGamePresenter(method);
            }
        }
        if (mCurGamePresenter != null) {
            mCurGamePresenter.onGameMessage(obj);
        }
        if (action == 3) {//如果是3，则表示要关闭游戏,此时要对mCurGamePresenter置空
            mCurGamePresenter = null;
        }
    }

    /**
     * 设置当前的GamePresenter
     */
    private void setCurGamePresenter(String socketMethod) {
        switch (socketMethod) {
            case GameConst.SOKCET_GAME_NIU_ZAI:
                mNiuZaiPresenter = new GameNiuZaiPresenter(mContext, mFragmentManager, mStream, mLiveuid);
                mNiuZaiPresenter.setBankerGroup(mBankerGroup);
                mNiuZaiPresenter.setBankerInfo(mBankerId, mBankerName, mBankerAvatar, mBankerCoin, mBankerLimit);
                mCurGamePresenter = mNiuZaiPresenter;
                break;
            case GameConst.SOKCET_GAME_JIN_HUA:
                mJinHuaPresenter = new GameJinHuaPresenter(mContext, mFragmentManager, mStream, mLiveuid);
                mCurGamePresenter = mJinHuaPresenter;
                break;
            case GameConst.SOKCET_GAME_HAI_DAO:
                mHaiDaoPresenter = new GameHaiDaoPresenter(mContext, mFragmentManager, mStream, mLiveuid);
                mCurGamePresenter = mHaiDaoPresenter;
                break;
            case GameConst.SOKCET_GAME_ER_BA_BEI:
                mEbbPresenter = new GameEbbPresenter(mContext, mFragmentManager, mStream, mLiveuid);
                mCurGamePresenter = mEbbPresenter;
                break;
            case GameConst.SOKCET_GAME_LUCK_PAN:
                mLuckPanPresenter = new GameLuckPanPresenter(mContext, mFragmentManager, mStream, mLiveuid);
                mLuckPanPresenter.setResultGroup(mBankerGroup);
                mCurGamePresenter = mLuckPanPresenter;
                break;
        }
    }


    /**
     * 主播通过发送socket打开游戏窗口
     */
    public void openGame(String method) {
        if (mAnchorClosedGame || mCurGamePresenter == null || mCurGamePresenter.anchorCanReplaceGame()) {
            UserBean u = AppConfig.getInstance().getUserBean();
            SocketUtil.getInstance().sendSocketMessage(new SendSocketBean()
                    .param("_method_", method)
                    .param("action", 1)
                    .param("msgtype", 15)
                    .param("level", u.getLevel())
                    .param("uname", u.getUser_nicename())
                    .param("uid", u.getId())
                    .param("ct", "")
                    .create());
        }
    }

    public void closeGame() {
        if (mCurGamePresenter != null) {
            mCurGamePresenter.closeGame();
        }
        mContext = null;
        mFragmentManager = null;
        mJinHuaPresenter = null;
        mHaiDaoPresenter = null;
        mNiuZaiPresenter = null;
        mLuckPanPresenter = null;
        mEbbPresenter = null;
        mCurGamePresenter = null;
    }

    public void anchorCloseGame() {
        if (mCurGamePresenter != null && mCurGamePresenter.anchorCloseGame()) {
            mAnchorClosedGame = true;
        }
    }

    public void enterRoomStartGame(int gameAction, String gameid, int betTime, int[] totalBet, int[] myBet) {
        switch (gameAction) {
            case GameConst.GAME_ACTION_JIN_HUA:
                setCurGamePresenter(GameConst.SOKCET_GAME_JIN_HUA);
                break;
            case GameConst.GAME_ACTION_HAI_DAO:
                setCurGamePresenter(GameConst.SOKCET_GAME_HAI_DAO);
                break;
            case GameConst.GAME_ACTION_NIU_ZAI:
                setCurGamePresenter(GameConst.SOKCET_GAME_NIU_ZAI);
                break;
            case GameConst.GAME_ACTION_ER_BA_BEI:
                setCurGamePresenter(GameConst.SOKCET_GAME_ER_BA_BEI);
                break;
            case GameConst.GAME_ACTION_LUCK_PAN:
                setCurGamePresenter(GameConst.SOKCET_GAME_LUCK_PAN);
                break;
        }
        mCurGamePresenter.enterRoomStartGame(gameid, betTime, totalBet, myBet);
    }

    public void setBankerGroup(ViewGroup group) {
        mBankerGroup = group;
    }

    public void setBankerInfo(String bankerId, String bankerName, String bankerAvatar, String bankerCoin, String bankerLimit) {
        mBankerId = bankerId;
        mBankerName = bankerName;
        mBankerAvatar = bankerAvatar;
        mBankerCoin = bankerCoin;
        mBankerLimit = bankerLimit;
    }


    public void setSocketConn(boolean conn) {
        if (mCurGamePresenter != null) {
            mCurGamePresenter.setSocketConn(conn);
        }
    }
}
