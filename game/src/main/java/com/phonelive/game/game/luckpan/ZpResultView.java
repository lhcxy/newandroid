package com.phonelive.game.game.luckpan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.phonelive.game.GameIconUitl;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.DpUtil;

/**
 * Created by cxf on 2017/10/21.
 * 显示转盘结果的view
 */

public class ZpResultView {

    private ViewGroup mParent;
    private View mRootView;
    private View mFlash;
    private ImageView mImg;
    private Handler mHandler;
    private ObjectAnimator mShowAnim;
    private ObjectAnimator mHideAnim;
    private RotateAnimation mRotateAnimation;
    private AnimCallback mCallback;

    public ZpResultView(ViewGroup parent) {
        mParent = parent;
        mRootView = LayoutInflater.from(AppContext.sInstance).inflate(R.layout.game_view_zp_result, parent, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DpUtil.dp2px(200), DpUtil.dp2px(200));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRootView.setLayoutParams(params);
        parent.addView(mRootView);
        mFlash = mRootView.findViewById(R.id.flash);
        mImg = (ImageView) mRootView.findViewById(R.id.img);
        mRotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(5000);
        mRotateAnimation.setRepeatCount(-1);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        int y = parent.getHeight() / 2 + DpUtil.dp2px(100);
        mRootView.setTranslationY(y);
        mShowAnim = ObjectAnimator.ofFloat(mRootView, "translationY", y, 0);
        mShowAnim.setDuration(500);
        mShowAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessageDelayed(0, 15000);
                mFlash.startAnimation(mRotateAnimation);
                if(mCallback!=null){
                    mCallback.onShow();
                }
            }
        });
        mHideAnim = ObjectAnimator.ofFloat(mRootView, "translationY", 0, y);
        mHideAnim.setDuration(500);
        mHideAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFlash.clearAnimation();
                if(mCallback!=null){
                    mCallback.onHide();
                }
            }
        });
        Interpolator interpolator=new AccelerateInterpolator();
        mShowAnim.setInterpolator(interpolator);
        mHideAnim.setInterpolator(interpolator);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mHideAnim.start();
            }
        };
    }

    public void show(int index) {
        mImg.setImageResource(GameIconUitl.getLuckPanResult(index));
        mShowAnim.start();
    }

    public void setAnimCallback(AnimCallback callback) {
        mCallback = callback;
    }

    public void onClose() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mFlash.clearAnimation();
        mRotateAnimation.cancel();
        mShowAnim.cancel();
        mHideAnim.cancel();
        if (mParent != null && mRootView != null && mRootView.getParent() != null) {
            mParent.removeView(mRootView);
            mRootView = null;
        }
    }

    public interface AnimCallback{
        void onShow();
        void onHide();
    }


}
