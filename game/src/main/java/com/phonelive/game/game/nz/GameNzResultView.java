package com.phonelive.game.game.nz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.WordUtil;

/**
 * Created by cxf on 2017/10/19.
 * 游戏胜负记录弹窗
 */

public class GameNzResultView {

    private Context mContext;
    private ViewGroup mParent;
    private View mRootView;
    private TextView mBen;
    private TextView mZhuang;
    private String mCoinName;
    private ObjectAnimator mShowAnim;
    private ObjectAnimator mHideAnim;
    private float mEndY;
    private Handler mHandler;


    public GameNzResultView(Context context, ViewGroup parent) {
        mContext = context;
        mParent = parent;
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.game_view_nz_result, parent, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DpUtil.dp2px(200), DpUtil.dp2px(120));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.setMargins(0, 0, 0, DpUtil.dp2px(40));
        mRootView.setLayoutParams(params);
        mParent.addView(mRootView);
        mEndY = DpUtil.dp2px(180);
        mRootView.setTranslationY(mEndY);
        mShowAnim = ObjectAnimator.ofFloat(mRootView, "translationY", mEndY, 0);
        mShowAnim.setDuration(300);
        mShowAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessageDelayed(0, 4000);
            }
        });
        mHideAnim = ObjectAnimator.ofFloat(mRootView, "translationY", 0, mEndY);
        mHideAnim.setDuration(300);
        mBen = (TextView) mRootView.findViewById(R.id.ben);
        mZhuang = (TextView) mRootView.findViewById(R.id.zhuang);
        mCoinName = AppConfig.getInstance().getConfig().getName_coin();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mHideAnim.start();
            }
        };
    }

    public void show(String winCoin, String bankerWinCoin) {
        mBen.setText(WordUtil.getString(R.string.game_nz_ben) + winCoin + mCoinName);
        mZhuang.setText(WordUtil.getString(R.string.game_nz_zhuang_2) + bankerWinCoin + mCoinName);
        mShowAnim.start();
    }

    public void onClose() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mParent != null && mRootView != null && mRootView.getParent() != null) {
            mParent.removeView(mRootView);
            mRootView = null;
        }
    }

}
