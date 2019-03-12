package com.phonelive.game.game.ebb;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.ChargeActivity;
import com.yunbao.phonelive.fragment.AbsFragment;
import com.phonelive.game.game.LastCoinEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2017/10/15.
 */

public class GameEbbFragment extends AbsFragment implements View.OnClickListener {

    private GameEbbPresenter mGamePresenter;
    private TextView mTip;//提示的横条
    private TextView mReadyCountDown;//准备开始倒计时的TextView
    private static final int MAX_REPEAT_COUNT = 7;
    private int mRepeatCount = MAX_REPEAT_COUNT;
    private Animation mReadyAnim;
    private Animation mTipHideAnim;//提示横条隐藏的动画
    private Animation mTipShowAnim;//提示横条显示的动画
    private GameEbbView[] mRoles;
    private TextView mBetCountDown;//下注倒计时的TextView
    private View mCoverImg;

    public static final int STATUS_READY = 0;//准备阶段
    public static final int STATUS_SEND_CARD = 1;//发牌阶段
    public static final int STATUS_SEND_CARD_END = 2;//发牌后和收到下注socket前的空档期
    public static final int STATUS_BET = 3;//下注阶段
    public static final int STATUS_RESULT = 4;//揭晓结果阶段
    public int mStatus = STATUS_READY;//游戏阶段状态

    private Handler mHandler;
    public static final int TIME_BET_COUNT_DOWN = 1;//下注倒计时
    public static final int TIME_SHOW_RESULT = 2;//显示结果
    public static final int TIME_HIDE_TIP = 3;//隐藏tips
    public static final int TIME_NEXT_READY = 4;//下一次准备开始

    private int mBetCount;
    private int mWinIndex = -1;//第几个中奖
    private boolean mClosed;
    private TextView mCoinTextView;//显示钻石数量的TextView
    private String mMoney;//每次下注的数量
    private String mCoinName;//货币的名字
    private Dialog mMessageDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.game_fragment_ebb;
    }

    @Override
    protected void main() {
        initView();
        initAnim();
        initData();
    }

    private void initData() {
        Bundle bundle = getArguments();
        boolean enterRoom = bundle.getBoolean("enterRoom");
        if (enterRoom) {
            enterRoomStartGame(bundle.getIntArray("totalBet"), bundle.getIntArray("myBet"));
            startBet(bundle.getInt("betCount"));
        } else {
            startReady();
        }
    }

    private void initView() {
        mTip = (TextView) mRootView.findViewById(R.id.tip);
        mReadyCountDown = (TextView) mRootView.findViewById(R.id.count_down_1);
        mReadyCountDown.setText(String.valueOf(mRepeatCount + 1));
        mRoles = new GameEbbView[3];
        mRoles[0] = (GameEbbView) mRootView.findViewById(R.id.role_1);
        mRoles[1] = (GameEbbView) mRootView.findViewById(R.id.role_2);
        mRoles[2] = (GameEbbView) mRootView.findViewById(R.id.role_3);
        mBetCountDown = (TextView) mRootView.findViewById(R.id.count_down_2);
        for (View v : mRoles) {
            v.setOnClickListener(this);
        }
        mCoverImg = mRootView.findViewById(R.id.cover);
        if (!mGamePresenter.isAnchor()) {
            ViewStub viewStub = (ViewStub) mRootView.findViewById(R.id.view_stub);
            View view = viewStub.inflate();
            view.findViewById(R.id.btn_bet_shi).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_bai).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_qian).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_wan).setOnClickListener(this);
            mCoinTextView = (TextView) view.findViewById(R.id.coin);
            mCoinTextView.setOnClickListener(this);
            mMoney = "10";
            mCoinName = AppConfig.getInstance().getConfig().getName_coin();
            HttpUtil.getCoin(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        setLastCoin(JSONObject.parseObject(info[0]).getString("coin"));
                    }
                }
            });
            EventBus.getDefault().register(this);
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mClosed) {
                    return;
                }
                switch (msg.what) {
                    case TIME_BET_COUNT_DOWN:
                        mBetCount--;
                        if (mBetCount > 0) {
                            mBetCountDown.setText(String.valueOf(mBetCount));
                            if (mHandler != null) {
                                mHandler.sendEmptyMessageDelayed(TIME_BET_COUNT_DOWN, 1000);
                            }
                        } else {
                            mBetCountDown.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case TIME_SHOW_RESULT:
                        showEveryResult(msg.arg1, (String[]) msg.obj);
                        break;
                    case TIME_HIDE_TIP:
                        mTip.startAnimation(mTipHideAnim);
                        break;
                    case TIME_NEXT_READY:
                        nextGame();
                        break;
                }
            }
        };
    }


    private void initAnim() {
        //准备开始 倒计时
        mReadyAnim = new ScaleAnimation(4, 1, 4, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mReadyAnim.setDuration(1000);
        mReadyAnim.setRepeatCount(mRepeatCount);
        mReadyAnim.setRepeatMode(Animation.RESTART);
        mReadyAnim.setInterpolator(new AccelerateInterpolator());
        mReadyAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mReadyCountDown.setVisibility(View.INVISIBLE);//隐藏准备倒计时
                //主播请求接口，创建游戏
                if(!mClosed){
                    mGamePresenter.gameCreate();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                mReadyCountDown.setText(String.valueOf(mRepeatCount));
                mRepeatCount--;
            }
        });
        mTipShowAnim = new ScaleAnimation(0.2f, 1, 0.2f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mTipShowAnim.setDuration(500);
        mTipShowAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(TIME_HIDE_TIP, 1000);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mTipHideAnim = new ScaleAnimation(1, 0.2f, 1, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mTipHideAnim.setDuration(500);
        mTipHideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTip.setVisibility(View.INVISIBLE);
                if (mStatus == STATUS_SEND_CARD) {//准备状态中，动画结束的时候，主播发送socket执行下注
                    if (mGamePresenter.isAnchor()) {
                        mGamePresenter.startBet();
                    }
                    mStatus = STATUS_SEND_CARD_END;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    public void setGamePresenter(GameEbbPresenter presenter) {
        mGamePresenter = presenter;
    }


    /**
     * 开始准备时间的8秒倒计时
     */
    private void startReady() {
        if (mClosed) {
            return;
        }
        mStatus = STATUS_READY;
        mReadyCountDown.startAnimation(mReadyAnim);
    }


    /**
     * 主播成功创建一个游戏记录，发送socket进行发牌，执行一些动画
     */
    public void sendCard() {
        if (mClosed) {
            return;
        }
        mStatus = STATUS_SEND_CARD;
        mTip.startAnimation(mTipHideAnim);//横条消失
    }


    /**
     * 进入直播间的时候已经有游戏进行，则直接开始
     */
    private void enterRoomStartGame(int[] totalBet, int[] myBet) {
        if (mClosed) {
            return;
        }
        mReadyCountDown.setVisibility(View.INVISIBLE);
        mTip.setVisibility(View.INVISIBLE);
        //显示已经下的注
        for (int i = 0; i < mRoles.length; i++) {
            mRoles[i].setBetVal(totalBet[i], myBet[i]);
        }
    }


    /**
     * 开始下注
     */
    public void startBet(int betCount) {
        if (mClosed) {
            return;
        }
        mStatus = STATUS_BET;
        mBetCount = betCount;
        mBetCountDown.setVisibility(View.VISIBLE);
        mBetCountDown.setText(String.valueOf(mBetCount));
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(TIME_BET_COUNT_DOWN, 1000);
        }
    }


    /**
     * 显示结果
     */
    public void showResult(String[][] result) {
        if (mClosed) {
            return;
        }
        mStatus = STATUS_RESULT;
        for (int i = 0; i < result.length; i++) {
            Message msg = Message.obtain();
            msg.what = TIME_SHOW_RESULT;
            msg.arg1 = i;
            msg.obj = result[i];
            if (mHandler != null) {
                mHandler.sendMessageDelayed(msg, i * 2000);
            }
        }
    }


    private void showEveryResult(int i, String[] result) {
        if (i == 0) {
            mTip.setVisibility(View.VISIBLE);
            mTip.setText(WordUtil.getString(R.string.game_zjh_show_result));//揭晓结果
            mTip.startAnimation(mTipShowAnim);
        }
        mRoles[i].showResult(result[0], result[1], result[2]);
        if (mWinIndex == -1) {
            if ("1".equals(result[4])) {
                mWinIndex = i;
            }
        }
        if (i == 2) {
            mCoverImg.setVisibility(View.VISIBLE);
            if (mWinIndex >= 0) {
                for (int j = 0; j < mRoles.length; j++) {
                    if (j != mWinIndex) {
                        mRoles[j].showCover();
                    }
                }
            }
            mGamePresenter.gameSettle();
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(TIME_NEXT_READY, 10000);
            }
        }
        mGamePresenter.playMusic(3);
    }

    /**
     * 开始下一次游戏
     */
    private void nextGame() {
        if (mClosed) {
            return;
        }
        if (mMessageDialog != null && mMessageDialog.isShowing()) {
            mMessageDialog.dismiss();
        }
        mWinIndex = -1;
        mRepeatCount = MAX_REPEAT_COUNT;
        for (GameEbbView view : mRoles) {
            view.reset();
        }
        mCoverImg.setVisibility(View.INVISIBLE);
        mTip.setVisibility(View.VISIBLE);
        mTip.setText(WordUtil.getString(R.string.game_wait_start));
        mReadyCountDown.setVisibility(View.VISIBLE);
        mReadyCountDown.setText(String.valueOf(mRepeatCount + 1));
        mReadyCountDown.startAnimation(mReadyAnim);
        mStatus = STATUS_READY;
    }

    public void closeGame() {
        if (mClosed) {
            return;
        }
        mClosed = true;
        mRepeatCount = MAX_REPEAT_COUNT;
        mWinIndex = -1;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mReadyAnim.cancel();
        mTipHideAnim.cancel();
        mTipShowAnim.cancel();
        mReadyCountDown.clearAnimation();
        mTip.clearAnimation();
        for (GameEbbView view : mRoles) {
            view.clearAnim();
        }
        if (!mGamePresenter.isAnchor()) {
            EventBus.getDefault().unregister(this);
        }

    }

    /**
     * 使用EventBus 送礼物和发弹幕的时候更新剩余金额
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLastCoinEvent(LastCoinEvent e) {
        setLastCoin(e.getCoin());
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_COIN);
        HttpUtil.cancel(HttpUtil.GAME_EBB_CREATE);
        HttpUtil.cancel(HttpUtil.GAME_EBB_BET);
        HttpUtil.cancel(HttpUtil.GAME_SETTLE);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeGame();
    }

    public int getStatus() {
        return mStatus;
    }


    private void gameBet(String index) {
        if(!mGamePresenter.isAnchor()){
            if (mStatus == STATUS_RESULT) {
                ToastUtil.show(WordUtil.getString(R.string.game_this_end));
            } else {
                mGamePresenter.gameBet(mMoney, index);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.role_1:
                gameBet("1");
                break;
            case R.id.role_2:
                gameBet("2");
                break;
            case R.id.role_3:
                gameBet("3");
                break;
            case R.id.btn_bet_shi:
                mMoney = "10";
                break;
            case R.id.btn_bet_bai:
                mMoney = "100";
                break;
            case R.id.btn_bet_qian:
                mMoney = "1000";
                break;
            case R.id.btn_bet_wan:
                mMoney = "10000";
                break;
            case R.id.coin://跳转到充值页面
                startActivity(new Intent(mContext, ChargeActivity.class));
                break;
        }
    }

    public void setLastCoin(String lastCoin) {
        mCoinTextView.setText(lastCoin + " " + WordUtil.getString(R.string.charge));
    }

    /**
     * 收到有人下注的时候显示
     */
    public void onBet(int index, int money, boolean isSelf) {
        mRoles[index].updateBetVal(money, isSelf);
    }

    /**
     * 游戏结算
     *
     * @param coin    当前所有的钱
     * @param winCoin 赢了的钱
     */
    public void onSettle(String coin, int winCoin) {
        setLastCoin(coin);
        mMessageDialog = DialogUitl.messageDialog(mContext, WordUtil.getString(R.string.game_win), winCoin + mCoinName, null);
        mMessageDialog.show();
    }

    public int getBetCount() {
        return mBetCount;
    }
}
