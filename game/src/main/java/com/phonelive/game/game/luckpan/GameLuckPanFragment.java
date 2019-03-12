package com.phonelive.game.game.luckpan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
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

public class GameLuckPanFragment extends AbsFragment implements View.OnClickListener {

    private GameLuckPanPresenter mGamePresenter;
    private PanRootView mPanRootView;
    private ImageView mLight;
    private TextView mTip;//提示的横条
    private TextView mReadyCountDown;//准备开始倒计时的TextView
    private static final int MAX_REPEAT_COUNT = 7;
    private int mRepeatCount = MAX_REPEAT_COUNT;
    private Animation mReadyAnim;
    private Animation mTipHideAnim;//提示横条隐藏的动画
    private Animation mTipShowAnim;//提示横条显示的动画
    private ObjectAnimator mPanIdleAnim;//转盘待机动画
    private ObjectAnimator mPanResultAnim;//转盘展示结果的动画
    private PanView mPan;//转盘
    private ZpBetView[] mRoles;
    private TextView mBetCountDown;//下注倒计时的TextView

    public static final int STATUS_READY = 0;//准备阶段
    public static final int STATUS_BET = 1;//下注阶段
    public static final int STATUS_RESULT = 2;//揭晓结果阶段
    public int mStatus = STATUS_READY;//游戏阶段状态
    private boolean mLightFlag;
    private Handler mHandler;
    public static final int TIME_BET_COUNT_DOWN = 1;//下注倒计时
    public static final int TIME_HIDE_TIP = 4;//隐藏tips
    public static final int TIME_LIGHT = 3;//灯
    private int mBetCount;
    private boolean mClosed;
    private TextView mCoinTextView;//显示钻石数量的TextView
    private String mMoney;//每次下注的数量
    private String mCoinName;//货币的名字
    private Dialog mMessageDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.game_fragment_zp;
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
        mPanRootView = (PanRootView) mRootView.findViewById(R.id.root);
        if (!mGamePresenter.isAnchor()) {
            mPanRootView.setDeltaHeight(DpUtil.dp2px(70));
            mRootView.findViewById(R.id.go).setVisibility(View.INVISIBLE);
        }
        mLight = (ImageView) mRootView.findViewById(R.id.light);
        mTip = (TextView) mRootView.findViewById(R.id.tip);
        mReadyCountDown = (TextView) mRootView.findViewById(R.id.count_down_1);
        mReadyCountDown.setText(String.valueOf(mRepeatCount + 1));
        mPan = (PanView) mRootView.findViewById(R.id.pan);
        mRoles = new ZpBetView[4];
        mRoles[0] = (ZpBetView) mRootView.findViewById(R.id.role_1);
        mRoles[1] = (ZpBetView) mRootView.findViewById(R.id.role_2);
        mRoles[2] = (ZpBetView) mRootView.findViewById(R.id.role_3);
        mRoles[3] = (ZpBetView) mRootView.findViewById(R.id.role_4);
        mBetCountDown = (TextView) mRootView.findViewById(R.id.count_down_2);
        for (View v : mRoles) {
            v.setOnClickListener(this);
        }
        mPanIdleAnim = ObjectAnimator.ofFloat(mPan, "rotation", 0, 359);
        mPanIdleAnim.setRepeatCount(-1);
        mPanIdleAnim.setDuration(8000);
        mPanIdleAnim.setInterpolator(new LinearInterpolator());

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
                    case TIME_HIDE_TIP:
                        mTip.startAnimation(mTipHideAnim);
                        break;
                    case TIME_LIGHT:
                        light();
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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void startLight() {
        mHandler.sendEmptyMessageDelayed(TIME_LIGHT, 1000);
    }

    private void light() {
        if (mLightFlag) {
            mLight.setImageResource(R.mipmap.icon_zp_light_1);
        } else {
            mLight.setImageResource(R.mipmap.icon_zp_light_2);
        }
        mLightFlag = !mLightFlag;
        mHandler.sendEmptyMessageDelayed(TIME_LIGHT, 1000);
    }

    public void setGamePresenter(GameLuckPanPresenter presenter) {
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
        startLight();
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
        startLight();
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
        mPanIdleAnim.start();
        mStatus = STATUS_BET;
        mBetCount = betCount;
        mBetCountDown.setVisibility(View.VISIBLE);
        mBetCountDown.setText(String.valueOf(mBetCount));
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(TIME_BET_COUNT_DOWN, 1000);
        }
        mTip.setVisibility(View.VISIBLE);
        mTip.setText(WordUtil.getString(R.string.game_zjh_start_support));
        mTip.startAnimation(mTipShowAnim);
    }

    /**
     * 显示结果
     */
    public void showResult(final int[] result) {
        if (mClosed) {
            return;
        }
        mStatus = STATUS_RESULT;
        mTip.setVisibility(View.VISIBLE);
        mTip.setText(WordUtil.getString(R.string.game_zjh_show_result));//揭晓结果
        mTip.startAnimation(mTipShowAnim);
        mPanIdleAnim.cancel();
        mPan.setRotation(0);
        mPanResultAnim = ObjectAnimator.ofFloat(mPan, "rotation", (1 - result[0]) * 18 + 360 * 6);
        mPanResultAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mGamePresenter.playMusic(3);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mGamePresenter.showResult(result[0]);
            }
        });
        mPanResultAnim.setDuration(6000);
        mPanResultAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mPanResultAnim.start();
    }

    /**
     * 开始下一次游戏
     */
    public void nextGame() {
        if (mClosed) {
            return;
        }
        if (mMessageDialog != null && mMessageDialog.isShowing()) {
            mMessageDialog.dismiss();
        }
        for (ZpBetView view : mRoles) {
            view.reset();
        }
        mRepeatCount = MAX_REPEAT_COUNT;
        mTip.setVisibility(View.VISIBLE);
        mTip.setText(WordUtil.getString(R.string.game_wait_start));
        mReadyCountDown.setVisibility(View.VISIBLE);
        mReadyCountDown.setText(String.valueOf(mRepeatCount + 1));
        mReadyCountDown.startAnimation(mReadyAnim);
        mPanResultAnim = null;
        mStatus = STATUS_READY;
    }

    public void closeGame() {
        if (mClosed) {
            return;
        }
        mClosed = true;
        L.e("游戏---closeGame--转盘-->1");
        mRepeatCount = MAX_REPEAT_COUNT;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        for (View v : mRoles) {
            v.clearAnimation();
        }
        mReadyAnim.cancel();
        mTipHideAnim.cancel();
        mTipShowAnim.cancel();
        mPanIdleAnim.cancel();
        if (mPanResultAnim != null) {
            mPanResultAnim.cancel();
        }
        mReadyCountDown.clearAnimation();
        mPan.recycleBitmap();
        mTip.clearAnimation();
        if (!mGamePresenter.isAnchor()) {
            EventBus.getDefault().unregister(this);
        }
        L.e("游戏---closeGame--转盘-->2");

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
        HttpUtil.cancel(HttpUtil.GAME_LUCK_PAN_CREATE);
        HttpUtil.cancel(HttpUtil.GAME_LUCK_PAN_BET);
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
            case R.id.role_1:
                gameBet("1");
                break;
            case R.id.role_2:
                gameBet("2");
                break;
            case R.id.role_3:
                gameBet("3");
                break;
            case R.id.role_4:
                gameBet("4");
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
