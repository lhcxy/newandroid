package com.phonelive.game.game.nz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.phonelive.game.GameIconUitl;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.ChargeActivity;
import com.yunbao.phonelive.fragment.AbsFragment;
import com.phonelive.game.game.GameBetCoinView;
import com.phonelive.game.game.LastCoinEvent;
import com.phonelive.game.game.PokerView;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2017/10/15.
 */

public class GameNiuZaiFragment extends AbsFragment implements View.OnClickListener {

    private GameNiuZaiPresenter mGamePresenter;
    private TextView mTip;//提示的横条
    private TextView mReadyCountDown;//准备开始倒计时的TextView
    private View mRoleGroup;
    private static final int MAX_REPEAT_COUNT = 7;
    private int mRepeatCount = MAX_REPEAT_COUNT;
    private Animation mReadyAnim;
    private Animation mTipHideAnim;//提示横条隐藏的动画
    private Animation mTipShowAnim;//提示横条显示的动画
    private Animation mResultAnim;
    private ValueAnimator mRoleScaleAnim;//角色缩小的动画
    private Animation mRoleIdleAnim; //角色待机动画
    private int mHeight;//场景高度
    private int mRoleHeight;//角色高度
    private View mPokerGroup;
    private PokerView[] mPokerViews;
    private View[] mRoles;
    private View[] mRoleNames;
    private TextView mBetCountDown;//下注倒计时的TextView
    private GameBetCoinView[] mBetCoinViews;
    private ImageView[] mResults;

    public static final int STATUS_READY = 0;//准备阶段
    public static final int STATUS_SEND_CARD = 1;//发牌阶段
    public static final int STATUS_SEND_CARD_END = 2;//发牌后和收到下注socket前的空档期
    public static final int STATUS_BET = 3;//下注阶段
    public static final int STATUS_RESULT = 4;//揭晓结果阶段
    public int mStatus = STATUS_READY;//游戏阶段状态

    private Handler mHandler;
    public static final int TIME_BET_COUNT_DOWN = 1;//下注倒计时
    public static final int TIME_SEND_CARD = 2;//模拟发牌动画延时
    public static final int TIME_SHOW_RESULT = 3;//显示结果
    public static final int TIME_HIDE_TIP = 4;//隐藏tips
    public static final int TIME_NEXT_READY = 5;//下一次准备开始
    private int mBetCount;
    private int mWinIndex = -1;//第几个中奖
    private boolean mClosed;
    private TextView mCoinTextView;//显示钻石数量的TextView
    private String mMoney;//每次下注的数量
    private String mCoinName;//货币的名字

    @Override
    protected int getLayoutId() {
        return R.layout.game_fragment_nz;
    }

    @Override
    protected void main() {
        initView();
        initAnim();
        initData();
        for (PokerView pokerView : mPokerViews) {
            pokerView.sendCard();
        }
    }

    private void initData() {
        Bundle bundle = getArguments();
        int startType = bundle.getInt("startType");
        boolean enterRoom = bundle.getBoolean("enterRoom");
        if (enterRoom) {
            enterRoomStartGame(bundle.getIntArray("totalBet"), bundle.getIntArray("myBet"));
            startBet(bundle.getInt("betCount"));
        } else {
            if (startType == 1) {
                startReady();
            } else {
                startNoReady();
            }
        }
    }

    private void initView() {
        mTip = (TextView) mRootView.findViewById(R.id.tip);
        mReadyCountDown = (TextView) mRootView.findViewById(R.id.count_down_1);
        mReadyCountDown.setText(String.valueOf(mRepeatCount + 1));
        mRoleGroup = mRootView.findViewById(R.id.role_group);
        mHeight = DpUtil.dp2px(150);
        mRoleHeight = DpUtil.dp2px(90);
        mPokerGroup = mRootView.findViewById(R.id.pokers_group);
        mPokerViews = new PokerView[3];
        mPokerViews[0] = (PokerView) mRootView.findViewById(R.id.pokers_1);
        mPokerViews[1] = (PokerView) mRootView.findViewById(R.id.pokers_2);
        mPokerViews[2] = (PokerView) mRootView.findViewById(R.id.pokers_3);
        mRoles = new View[3];
        mRoles[0] = mRootView.findViewById(R.id.role_1);
        mRoles[1] = mRootView.findViewById(R.id.role_2);
        mRoles[2] = mRootView.findViewById(R.id.role_3);
        mRoleNames = new View[3];
        mRoleNames[0] = mRootView.findViewById(R.id.name_1);
        mRoleNames[1] = mRootView.findViewById(R.id.name_2);
        mRoleNames[2] = mRootView.findViewById(R.id.name_3);
        mBetCountDown = (TextView) mRootView.findViewById(R.id.count_down_2);
        mBetCoinViews = new GameBetCoinView[3];
        mBetCoinViews[0] = (GameBetCoinView) mRootView.findViewById(R.id.score_1);
        mBetCoinViews[1] = (GameBetCoinView) mRootView.findViewById(R.id.score_2);
        mBetCoinViews[2] = (GameBetCoinView) mRootView.findViewById(R.id.score_3);
        for (View v : mBetCoinViews) {
            v.setOnClickListener(this);
        }
        mResults = new ImageView[3];
        mResults[0] = (ImageView) mRootView.findViewById(R.id.result_1);
        mResults[1] = (ImageView) mRootView.findViewById(R.id.result_2);
        mResults[2] = (ImageView) mRootView.findViewById(R.id.result_3);
        if (!mGamePresenter.isAnchor()) {
            ViewStub viewStub = (ViewStub) mRootView.findViewById(R.id.view_stub);
            View view = viewStub.inflate();
            view.findViewById(R.id.btn_bet_shi).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_bai).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_qian).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_wan).setOnClickListener(this);
            view.findViewById(R.id.btn_record).setOnClickListener(this);
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
                    case TIME_SEND_CARD:
                        startSupportAnim();//开始支持的动画，结束后开始下注
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
                if (!mClosed) {
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
                if (mStatus == STATUS_SEND_CARD) {//发牌状态中，动画结束的时候，主播发送socket执行下注
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
        //角色缩小的动画
        mRoleScaleAnim = ValueAnimator.ofFloat(mHeight, mRoleHeight);
        mRoleScaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                changeRoleHeight((int) v);
            }
        });
        mRoleScaleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                playSendCardAnim();
            }
        });
        mRoleScaleAnim.setDuration(1000);
        mResultAnim = new ScaleAnimation(0.2f, 1, 0.2f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mResultAnim.setDuration(300);

        mRoleIdleAnim = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0, Animation.ABSOLUTE, DpUtil.dp2px(3));
        mRoleIdleAnim.setRepeatCount(-1);
        mRoleIdleAnim.setRepeatMode(Animation.REVERSE);
        mRoleIdleAnim.setDuration(800);
    }

    private void changeRoleHeight(int height) {
        ViewGroup.LayoutParams params = mRoleGroup.getLayoutParams();
        params.height = height;
        mRoleGroup.setLayoutParams(params);
    }

    public void setGamePresenter(GameNiuZaiPresenter presenter) {
        mGamePresenter = presenter;
    }

    /**
     * 启动角色待机动画开始
     */
    private void startRoleIdleAnim() {
        if (mClosed) {
            return;
        }
        for (View v : mRoles) {
            v.startAnimation(mRoleIdleAnim);
        }
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
        startRoleIdleAnim();
    }

    /**
     * 没有准备过程，直接开始发牌
     */
    private void startNoReady() {
        if (mClosed) {
            return;
        }
        mReadyCountDown.setVisibility(View.INVISIBLE);
        startRoleIdleAnim();
        sendCard();
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
        for (View name : mRoleNames) {//隐藏角色名字
            name.setVisibility(View.INVISIBLE);
        }
        mPokerGroup.setVisibility(View.VISIBLE);
        for (PokerView pokerView : mPokerViews) {
            pokerView.sendCard();
        }
        mGamePresenter.getPokerView().sendCard();
        changeRoleHeight(mRoleHeight);
        for (View v : mBetCoinViews) {
            v.setVisibility(View.VISIBLE);
        }
        startRoleIdleAnim();
        //显示已经下的注
        for (int i = 0; i < mBetCoinViews.length; i++) {
            mBetCoinViews[i].setBetVal(totalBet[i], myBet[i]);
        }
    }

    /**
     * 主播成功创建一个游戏记录，发送socket进行发牌，执行一些动画
     */
    public void sendCard() {
        if (mClosed) {
            return;
        }
        mStatus = STATUS_SEND_CARD;
        for (View name : mRoleNames) {//隐藏角色名字
            name.setVisibility(View.INVISIBLE);
        }
        mTip.startAnimation(mTipHideAnim);//横条消失
        mRoleScaleAnim.start();//执行角色缩小动画
    }

    /**
     * 角色缩小后播放发牌动画，进入发牌状态
     */
    private void playSendCardAnim() {
        if (mClosed) {
            return;
        }
        //角色靠右
        for (int i = 0; i < mBetCoinViews.length; i++) {
            if (mBetCoinViews[i].getVisibility() == View.GONE) {
                mBetCoinViews[i].setVisibility(View.INVISIBLE);
            }
        }
        //显示摆放扑克牌的外框,开始执行发牌动画
        mPokerGroup.setVisibility(View.VISIBLE);
        for (PokerView pokerView : mPokerViews) {
            pokerView.sendCard();
        }
        //庄家发牌
        mGamePresenter.getPokerView().sendCard();
        //使用handler发牌结束后执行开始下注提示的动画
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(TIME_SEND_CARD, PokerView.SEND_DURATION);
        }
    }

    /**
     * 发牌动画结束后 束后执行开始下注提示的动画
     */
    private void startSupportAnim() {
        if (mClosed) {
            return;
        }
        mTip.setVisibility(View.VISIBLE);
        mTip.setText(WordUtil.getString(R.string.game_zjh_start_support));
        mTip.startAnimation(mTipShowAnim);
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
        //显示记分牌
        for (View score : mBetCoinViews) {
            score.setVisibility(View.VISIBLE);
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
        for (int i = 0; i < 3; i++) {
            Message msg = Message.obtain();
            msg.what = TIME_SHOW_RESULT;
            msg.arg1 = i;
            msg.obj = result[i];
            if (mHandler != null) {
                mHandler.sendMessageDelayed(msg, i * 2000);
            }
        }
        setResult(mGamePresenter.getPokerView(), mGamePresenter.getResultView(), result[3]);
    }

    public void setResult(PokerView pokerView, ImageView imageView, String[] result) {
        pokerView.showResult(
                GameIconUitl.getPoker(result[0]),
                GameIconUitl.getPoker(result[1]),
                GameIconUitl.getPoker(result[2]),
                GameIconUitl.getPoker(result[2]),
                GameIconUitl.getPoker(result[2]));
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(GameIconUitl.getNiuZaiResult(result[7] + result[5]));
        imageView.startAnimation(mResultAnim);
    }


    private void showEveryResult(int i, String[] result) {
        if (i == 0) {
            mTip.setVisibility(View.VISIBLE);
            mTip.setText(WordUtil.getString(R.string.game_zjh_show_result));//揭晓结果
            mTip.startAnimation(mTipShowAnim);
        }
        setResult(mPokerViews[i], mResults[i], result);
        if (i == 2) {
            mGamePresenter.gameSettle();
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(TIME_NEXT_READY, 8000);
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
        mRepeatCount = MAX_REPEAT_COUNT;
        for (ImageView imageView : mResults) {
            imageView.setVisibility(View.INVISIBLE);
        }
        for (GameBetCoinView coinView : mBetCoinViews) {
            coinView.reset();
            coinView.setVisibility(View.GONE);
        }
        for (View name : mRoleNames) {
            name.setVisibility(View.VISIBLE);
        }
        //清空庄家的牌
        mGamePresenter.getPokerView().clearCard();
        mGamePresenter.getResultView().setVisibility(View.INVISIBLE);
        changeRoleHeight(mHeight);
        mPokerGroup.setVisibility(View.INVISIBLE);
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
        for (View v : mRoles) {
            v.clearAnimation();
        }
        mRoleIdleAnim.cancel();
        mReadyAnim.cancel();
        mRoleScaleAnim.cancel();
        mTipHideAnim.cancel();
        mTipShowAnim.cancel();
        mResultAnim.cancel();
        mReadyCountDown.clearAnimation();
        mTip.clearAnimation();
        for (ImageView imageView : mResults) {
            imageView.clearAnimation();
        }
        for (PokerView pokerView : mPokerViews) {
            pokerView.recycleBitmap();
        }
        mGamePresenter.getPokerView().recycleBitmap();
        mGamePresenter.removeBankerView();
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
        HttpUtil.cancel(HttpUtil.GAME_NIUZAI_CREATE);
        HttpUtil.cancel(HttpUtil.GAME_NIUZAI_BET);
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
        if (AppConfig.getInstance().getUid().equals(mGamePresenter.getBankerId())) {
            ToastUtil.show(WordUtil.getString(R.string.game_nz_cannot_bet));
            return;
        }
        if (!mGamePresenter.isAnchor()) {
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
            case R.id.score_1:
                gameBet("1");
                break;
            case R.id.score_2:
                gameBet("2");
                break;
            case R.id.score_3:
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
            case R.id.btn_record://胜负记录
                mGamePresenter.showRecord();
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
        mBetCoinViews[index].updateBetVal(money, isSelf);
    }

    public int getBetCount() {
        return mBetCount;
    }

}
