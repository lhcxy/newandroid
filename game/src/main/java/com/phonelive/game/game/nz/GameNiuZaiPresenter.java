package com.phonelive.game.game.nz;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.phonelive.game.game.AbsGamePresenter;
import com.phonelive.game.game.GameConst;
import com.phonelive.game.game.GameEvent;
import com.phonelive.game.game.PokerView;
import com.phonelive.game.game.zjh.GameJinHuaFragment;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.socket.SendSocketBean;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/10/15.
 */

public class GameNiuZaiPresenter extends AbsGamePresenter implements View.OnClickListener {


    private GameNiuZaiFragment mNiuZaiFragment;
    private UserBean mUser;

    //牛仔游戏的庄家布局放在这里面
    private ViewGroup mBankerGroup;
    //庄家布局
    private View mBankerView;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mCoin;
    private PokerView mPokerView;
    //游戏结果的弹窗
    private ImageView mResult;
    //当前庄家信息
    private String mBankerId;
    private String mBankerName;
    private String mBankerAvatar;
    private String mBankerCoin;
    private String mBankerLimit;
    //上庄列表弹窗
    private GameNzSzFragment mNzSzFragment;
    //游戏结果弹窗
    private GameNzResultView mResultView;


    public GameNiuZaiPresenter(Context context, FragmentManager fragmentManager, String stream, String liveuid) {
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
            case 2:
                onSendCard(obj);
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
                onShowResult(JSON.parseObject(obj.getString("ct"), String[][].class));
                break;
        }
    }


    /**
     * 主播选择游戏后，发送开启游戏窗口的socket,所有人收到这个socket后，开启游戏窗口
     * 或者观众中途进房间的时候，房间内正在进行游戏，则主动开启游戏窗口
     */
    private void onOpenGameWindow(int startType, boolean enterRoom, int betCount, int[] gameTotalBet, int[] gameMyBet) {
        if (mNiuZaiFragment != null) {
            mNiuZaiFragment.closeGame();
            mNiuZaiFragment = null;
            if (mBankerView != null) {
                removeBankerView();
            }
        }
        initBankerView();
        showBankerInfo();
        mNiuZaiFragment = new GameNiuZaiFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("startType", startType);
        bundle.putBoolean("enterRoom", enterRoom);
        bundle.putInt("betCount", betCount);
        bundle.putIntArray("totalBet", gameTotalBet);
        bundle.putIntArray("myBet", gameMyBet);
        mNiuZaiFragment.setArguments(bundle);
        mNiuZaiFragment.setGamePresenter(this);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.repalced_game, mNiuZaiFragment).commit();
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
                HttpUtil.gameNiuzaiCreate(mStream, mBankerId, mGameCreateCallback);
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
                sendCard(obj.getString("bankerlist"));
            } else {
                ToastUtil.show(msg);
            }
        }
    };

    /**
     * 主播创建游戏记录成功后，发送socket通知所有人，开始发牌
     *
     * @param bankerlist 本局的庄家信息  服务器用"bankerlist" 这个字段表示 ，其实是一个对象，是一个人的信息
     */
    private void sendCard(String bankerlist) {
        if (mGameClosed) {
            return;
        }
        SocketUtil.getInstance().sendSocketMessage(new SendSocketBean()
                .param("_method_", GameConst.SOKCET_GAME_NIU_ZAI)
                .param("action", 2)
                .param("msgtype", 17)
                .param("level", mUser.getLevel())
                .param("uname", mUser.getUser_nicename())
                .param("uid", mUser.getId())
                .param("gameid", mGameId)
                .param("ct", "")
                .jsonObjectParam("bankerlist", bankerlist)
                .create());
    }

    /**
     * 所有人收到开始发牌的socket后，执行发牌动画
     */
    private void onSendCard(JSONObject obj) {
        if (mGameClosed) {
            return;
        }
        JSONObject bankerInfo = obj.getJSONObject("bankerlist");
        String bankerid = bankerInfo.getString("id");
        mBankerName = bankerInfo.getString("user_nicename");
        if (!bankerid.equals(mBankerId)) {
            ToastUtil.show(mBankerName + WordUtil.getString(R.string.game_nz_sz));
        }
        mBankerId = bankerid;
        mBankerAvatar = bankerInfo.getString("avatar");
        if ("0".equals(mBankerId)) {
            mBankerCoin = bankerInfo.getString("coin");
        } else {
            mBankerCoin = bankerInfo.getString("deposit");
        }
        if (mNiuZaiFragment == null) {
            onOpenGameWindow(2, false, 0, null, null);
        } else {
            mNiuZaiFragment.sendCard();
            showBankerInfo();
            ToastUtil.show(mBankerName + WordUtil.getString(R.string.game_nz_sz));
        }
        playMusic(1);
    }

    /**
     * 主播的发牌动画完成后，主播发送socket通知观众 开始下注
     * 此时服务器收到该socket后，自动计时，30秒后自动发送显示游戏结果的socket
     */
    public void startBet() {
        if (mGameClosed) {
            return;
        }
        if (!mIsAnchor) {
            return;
        }
        SocketUtil.getInstance().sendSocketMessage(new SendSocketBean()
                .param("_method_", GameConst.SOKCET_GAME_NIU_ZAI)
                .param("action", 4)
                .param("msgtype", 17)
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
        mNiuZaiFragment.startBet(mBetTime - 1);
    }

    /**
     * 观众请求接口，执行下注
     */
    public void gameBet(final String coin, final String grade) {
        if (!mGameClosed) {
            HttpUtil.gameNiuzaiBet(mGameId, coin, grade, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mNiuZaiFragment.setLastCoin(obj.getString("coin"));
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
                .param("_method_", GameConst.SOKCET_GAME_NIU_ZAI)
                .param("action", 5)
                .param("msgtype", 17)
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
        mNiuZaiFragment.onBet(index, money, isSelf);
    }

    /**
     * 下注倒计时结束后，所有人收到服务器端发的游戏结果的socket，显示游戏结果
     */
    private void onShowResult(String[][] result) {
        if (mGameClosed) {
            return;
        }
        mNiuZaiFragment.showResult(result);
    }

    /**
     * 游戏结果显示完毕后，所有人请求接口，获取自己赢到的金额
     */
    public void gameSettle() {
        HttpUtil.gameSettle(mGameId, mSettleCallback);
    }

    private HttpCallback mSettleCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                if (!mIsAnchor) {
                    mNiuZaiFragment.setLastCoin(obj.getString("coin"));
                }
                if (mResultView == null) {
                    mResultView = new GameNzResultView(mContext, mBankerGroup);
                }
                mResultView.show(obj.getString("gamecoin"), obj.getString("banker_profit"));
                if (obj.getIntValue("isshow") == 1) {
                    //只有自己是当前庄家的时候才会收到是1，1表示自己余额不够自动下庄了
                    ToastUtil.show(WordUtil.getString(R.string.game_nz_sz_xz));
                }
            } else {
                ToastUtil.show(msg);
            }
        }
    };

    /**
     * 主播发送socket停止游戏,只有在准备阶段才能停止游戏
     */
    @Override
    public boolean anchorCloseGame() {
        if (mIsAnchor) {
            if (mSocketConn) {
                if (mNiuZaiFragment.getStatus() == GameJinHuaFragment.STATUS_READY || mCanCloseGame && mNiuZaiFragment.getBetCount() == 0) {
                    SocketUtil.getInstance().sendSocketMessage(new SendSocketBean()
                            .param("_method_", GameConst.SOKCET_GAME_JIN_HUA)
                            .param("action", 3)
                            .param("msgtype", 15)
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
                if (mNiuZaiFragment.getBetCount() == 0) {
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
        mNiuZaiFragment.closeGame();
        if (mResultView != null) {
            mResultView.onClose();
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.remove(mNiuZaiFragment).commit();
        EventBus.getDefault().post(new GameEvent(GameEvent.CLOSE));
        mNzSzFragment = null;
        releaseGamePool();
    }

    /**
     * 主播或观众关闭直播间的时候，如果正在游戏，则关闭游戏窗口，释放资源
     */
    @Override
    public void closeGame() {
        mGameClosed = true;
        if (mNiuZaiFragment != null) {
            mNiuZaiFragment.closeGame();
            releaseGamePool();
        }
        if (mResultView != null) {
            mResultView.onClose();
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
            if (mNiuZaiFragment.getStatus() == GameNiuZaiFragment.STATUS_READY) {
                return true;
            } else {
                ToastUtil.show(WordUtil.getString(R.string.game_please_wait_game_end));
            }
        }
        return false;
    }


    /**
     * 初始化上庄控件
     */
    public void initBankerView() {
        mBankerView = LayoutInflater.from(mContext).inflate(R.layout.game_view_nz_sz, mBankerGroup, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DpUtil.dp2px(120), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(DpUtil.dp2px(10), DpUtil.dp2px(80), 0, 0);
        mBankerView.setLayoutParams(params);
        mAvatar = (ImageView) mBankerView.findViewById(R.id.avatar);
        mName = (TextView) mBankerView.findViewById(R.id.name);
        mCoin = (TextView) mBankerView.findViewById(R.id.coin);
        mPokerView = (PokerView) mBankerView.findViewById(R.id.pokerView);
        mResult = (ImageView) mBankerView.findViewById(R.id.result);
        mBankerView.findViewById(R.id.group_water).setOnClickListener(this);
        mBankerView.findViewById(R.id.btn_sz).setOnClickListener(this);
        mBankerGroup.addView(mBankerView);
    }

    public void showBankerInfo() {
        if ("0".equals(mBankerId) || "".equals(mBankerAvatar)) {
            mAvatar.setImageResource(R.mipmap.icon_nz_sz_default_head);
        } else {
            ImgLoader.displayCircle(mBankerAvatar, mAvatar);
        }
        mName.setText(mBankerName);
        mCoin.setText(mBankerCoin);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sz:
                showSzDialog();
                break;
            case R.id.group_water:
                showLsDialog();
                break;
        }
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

    public PokerView getPokerView() {
        return mPokerView;
    }

    public ImageView getResultView() {
        return mResult;
    }

    public void removeBankerView() {
        if (mBankerView.getParent() != null) {
            mBankerGroup.removeView(mBankerView);
        }
    }

    /**
     * 显示上庄列表的弹窗
     */
    private void showSzDialog() {
        if (mNzSzFragment == null) {
            mNzSzFragment = new GameNzSzFragment();
            Bundle bundle = new Bundle();
            bundle.putString("stream", mStream);
            bundle.putString("bankerLimit", mBankerLimit);
            mNzSzFragment.setArguments(bundle);
        }
        mNzSzFragment.show(mFragmentManager, "GameNzSzFragment");
    }

    /**
     * 显示庄家流水的弹窗
     */
    private void showLsDialog() {
        GameNzLsFragment fragment = new GameNzLsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stream", mStream);
        bundle.putString("bankerId", mBankerId);
        fragment.setArguments(bundle);
        fragment.show(mFragmentManager, "GameNzLsFragment");
    }


    /**
     * 显示游戏记录的弹窗
     */
    public void showRecord() {
        GameNzSfFragment fragment = new GameNzSfFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stream", mStream);
        fragment.setArguments(bundle);
        fragment.show(mFragmentManager, "GameNzSfFragment");
    }

    public String getBankerId() {
        return mBankerId;
    }


}
