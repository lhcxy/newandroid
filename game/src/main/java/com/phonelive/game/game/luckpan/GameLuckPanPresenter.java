package com.phonelive.game.game.luckpan;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.phonelive.game.game.AbsGamePresenter;
import com.phonelive.game.game.GameConst;
import com.phonelive.game.game.GameEvent;
import com.phonelive.game.game.zjh.GameJinHuaFragment;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.socket.SendSocketBean;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/10/15.
 */

public class GameLuckPanPresenter extends AbsGamePresenter {

    private GameLuckPanFragment mZpFragment;
    private UserBean mUser;
    private ViewGroup mResultGroup;
    private ZpResultView mResultView;

    public GameLuckPanPresenter(Context context, FragmentManager fragmentManager, String stream, String liveuid) {
        super(context, fragmentManager, stream, liveuid);
        mUser = AppConfig.getInstance().getUserBean();
    }

    @Override
    public void onGameMessage(JSONObject obj) {
        int action = obj.getIntValue("action");
        switch (action) {
            case 1:
                onOpenGameWindow(1, false, 0, null, null);
                break;
            case 3:
                onCloseGame();
                break;
            case 4:
                onStartBet(obj);
                break;
            case 5:
                onBet(obj);
                break;
            case 6:
                onShowResult(JSON.parseObject(obj.getString("ct"), int[].class));
                break;
        }
    }


    /**
     * 主播选择游戏后，发送开启游戏窗口的socket,所有人收到这个socket后，开启游戏窗口
     * 或者观众中途进房间的时候，房间内正在进行游戏，则主动开启游戏窗口
     */
    private void onOpenGameWindow(int startType, boolean enterRoom, int betCount, int[] gameTotalBet, int[] gameMyBet) {
        if (mZpFragment != null) {
            mZpFragment.closeGame();
            mZpFragment = null;
            if (mResultView != null) {
                mResultView.onClose();
                mResultView = null;
            }
        }
        mZpFragment = new GameLuckPanFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("startType", startType);
        bundle.putBoolean("enterRoom", enterRoom);
        bundle.putInt("betCount", betCount);
        bundle.putIntArray("totalBet", gameTotalBet);
        bundle.putIntArray("myBet", gameMyBet);
        mZpFragment.setArguments(bundle);
        mZpFragment.setGamePresenter(this);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.repalced_game, mZpFragment).commit();
        EventBus.getDefault().post(new GameEvent(GameEvent.OPEN));
        mGameClosed = false;
        playMusic(1);
    }

    /**
     * 主播在8秒准备时间结束后，请求接口，创建游戏记录
     */
    public void gameCreate() {
        if (mIsAnchor && !mGameClosed) {
            if (mSocketConn) {
                HttpUtil.gameLuckPanCreate(mStream, mGameCreateCallback);
            } else {
                ToastUtil.show(WordUtil.getString(R.string.game_socket_conn_error));
            }
        }
    }

    private HttpCallback mGameCreateCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                mGameId = obj.getString("gameid");
                mGameToken = obj.getString("token");
                mBetTime = obj.getIntValue("time");
                startBet();
            } else {
                ToastUtil.show(msg);
            }
        }
    };


    /**
     * 主播的创建游戏成功后，主播发送socket通知观众 开始下注
     * 此时服务器收到该socket后，自动计时，30秒后自动发送显示游戏结果的socket
     */
    public void startBet() {
        if (mGameClosed) {
            return;
        }
        SocketUtil.getInstance().sendSocketMessage(new SendSocketBean()
                .param("_method_", GameConst.SOKCET_GAME_LUCK_PAN)
                .param("action", 4)
                .param("msgtype", 16)
                .param("level", mUser.getLevel())
                .param("uname", mUser.getUser_nicename())
                .param("uid", mUser.getId())
                .param("liveuid", mLiveuid)
                .param("gameid", mGameId)
                .param("token", mGameToken)
                .param("time", mBetTime)
                .param("ct", "")
                .create());
    }

    /**
     * 观众收到开始下注的socket后，启动下注倒计时
     */
    private void onStartBet(JSONObject obj) {
        if (mGameClosed) {
            return;
        }
        mGameId = obj.getString("gameid");
        mGameToken = obj.getString("token");
        mBetTime = obj.getIntValue("time");
        if (mZpFragment == null) {
            onOpenGameWindow(2, true, mBetTime - 1, new int[]{0, 0, 0, 0}, new int[]{0, 0, 0, 0});
        } else {
            mZpFragment.startBet(mBetTime - 1);
        }
        playMusic(1);
    }

    /**
     * 观众请求接口，执行下注
     */
    public void gameBet(final String coin, final String grade) {
        if (!mGameClosed) {
            HttpUtil.gameLuckPanBet(mGameId, coin, grade, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mZpFragment.setLastCoin(obj.getString("coin"));
                        sendBet(coin, grade);
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            });
        }
    }

    /**
     * 观众下注成功后，发送socket把下注的金额显示给所有人
     */
    public void sendBet(String coin, String grade) {
        if (mGameClosed) {
            return;
        }
        if (mIsAnchor) {
            return;
        }
        SocketUtil.getInstance().sendSocketMessage(new SendSocketBean()
                .param("_method_", GameConst.SOKCET_GAME_LUCK_PAN)
                .param("action", 5)
                .param("msgtype", 16)
                .param("level", mUser.getLevel())
                .param("uname", mUser.getUser_nicename())
                .param("uid", mUser.getId())
                .param("money", coin)
                .param("type", grade)
                .param("ct", "")
                .create());
    }


    /**
     * 所有人收到某人下注的socket后，显示下注的金额
     */
    private void onBet(JSONObject obj) {
        String uid = obj.getString("uid");
        int money = obj.getIntValue("money");
        int index = obj.getIntValue("type") - 1;
        boolean isSelf = uid.equals(mUser.getId());
        if (isSelf) {//自己下的注
            playMusic(2);
        }
        mZpFragment.onBet(index, money, isSelf);
    }

    /**
     * 下注倒计时结束后，所有人收到服务器端发的游戏结果的socket，显示游戏结果
     */
    private void onShowResult(int[] result) {
        if (mGameClosed) {
            return;
        }
        mZpFragment.showResult(result);
    }


    private HttpCallback mSettleCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                int winCoin = obj.getIntValue("gamecoin");
                if (winCoin > 0) {
                    mZpFragment.onSettle(obj.getString("coin"), winCoin);
                }
            } else {
                ToastUtil.show(msg);
            }
        }
    };

    /**
     * 游戏结果显示完毕后，观众请求接口，获取自己赢到的金额
     */
    public void showResult(int index) {
        if (mResultView == null) {
            mResultView = new ZpResultView(mResultGroup);
            mResultView.setAnimCallback(new ZpResultView.AnimCallback() {

                @Override
                public void onShow() {
                    HttpUtil.gameSettle(mGameId, mSettleCallback);
                }

                @Override
                public void onHide() {
                    mZpFragment.nextGame();
                }
            });
        }
        mResultView.show(index);
    }

    /**
     * 主播发送socket停止游戏,只有在准备阶段才能停止游戏
     */
    @Override
    public boolean anchorCloseGame() {
        if (mIsAnchor) {
            if (mSocketConn) {
                if (mZpFragment.getStatus() == GameJinHuaFragment.STATUS_READY || mCanCloseGame && mZpFragment.getBetCount() == 0) {
                    SocketUtil.getInstance().sendSocketMessage(new SendSocketBean()
                            .param("_method_", GameConst.SOKCET_GAME_LUCK_PAN)
                            .param("action", 3)
                            .param("msgtype", 16)
                            .param("level", mUser.getLevel())
                            .param("uname", mUser.getUser_nicename())
                            .param("uid", mUser.getId())
                            .param("ct", "")
                            .create());
                    return true;
                } else {
                    ToastUtil.show(WordUtil.getString(R.string.game_please_wait_game_end));
                }
            } else {
                if (mZpFragment.getBetCount() == 0) {
                    onCloseGame();
                    return true;
                } else {
                    ToastUtil.show(WordUtil.getString(R.string.game_please_wait_game_end));
                }
            }
        }
        return false;
    }

    /**
     * 所有人收到关闭游戏的socket后，关闭游戏窗口，释放资源
     */
    private void onCloseGame() {
        if (mGameClosed) {
            return;
        }
        mGameClosed = true;
        if (mResultView != null) {
            mResultView.onClose();
            mResultView = null;
        }
        mZpFragment.closeGame();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.remove(mZpFragment).commit();
        mZpFragment = null;
        EventBus.getDefault().post(new GameEvent(GameEvent.CLOSE));
        releaseGamePool();
    }

    /**
     * 主播替换正在进行中的游戏
     */
    @Override
    public void gameReplaced() {
        closeGame();
    }


    /**
     * 主播能否替换游戏
     */
    public boolean anchorCanReplaceGame() {
        if (mIsAnchor) {
            if (mZpFragment.getStatus() == GameJinHuaFragment.STATUS_READY) {
                return true;
            } else {
                ToastUtil.show(WordUtil.getString(R.string.game_please_wait_game_end));
            }
        }
        return false;
    }

    /**
     * 主播或观众关闭直播间的时候，如果正在游戏，则关闭游戏窗口，释放资源
     */
    @Override
    public void closeGame() {
        mGameClosed = true;
        if (mResultView != null) {
            mResultView.onClose();
            mResultView = null;
        }
        if (mZpFragment != null) {
            mZpFragment.closeGame();
            releaseGamePool();
        }
    }

    /**
     * 观众中途进房间的时候，如果直播间正在进行游戏，打开游戏窗口，现在当前下注金额等
     *
     * @param gameTotalBet 下注总金额
     * @param gameMyBet    观众自己下注的金额
     */
    @Override
    protected void onEnterRoomStartGame(int[] gameTotalBet, int[] gameMyBet) {
        onOpenGameWindow(0, true, mBetTime - 1, gameTotalBet, gameMyBet);
    }

    public void setResultGroup(ViewGroup viewGroup) {
        mResultGroup = viewGroup;
    }

}
