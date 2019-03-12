package com.yunbao.phonelive.custom.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ReceiveGiftBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.utils.DpUtil;

/**
 * Created by cxf on 2017/8/24.
 */

public class ShipAnimHolder {

    private ViewGroup mParent;
    private View mView;
    private TextView mName;
    private ImageView mAvatar;
    private View mSea1;
    private View mSea2;
    private View mShip;
    private ObjectAnimator mAnimator1;
    private ObjectAnimator mAnimator2;
    private ObjectAnimator mAnimator3;
    private ObjectAnimator mAnimator4;
    private int mStartX;
    private int mMiddleX;
    private int mEndX;
    private Handler mHandler;
    private Runnable mOnComplete;

    public ShipAnimHolder(ViewGroup parent, Runnable onComplete) {
        mParent = parent;
        mView = LayoutInflater.from(AppContext.sInstance).inflate(R.layout.view_gift_anim_ship, parent, false);
        mName = (TextView) mView.findViewById(R.id.uname);
        mAvatar = (ImageView) mView.findViewById(R.id.uhead);
        mSea1 = mView.findViewById(R.id.sea1);
        mSea2 = mView.findViewById(R.id.sea2);
        mShip = mView.findViewById(R.id.ship);
        mAnimator1 = ObjectAnimator.ofFloat(mSea1, "translationX", -50, 50);
        mAnimator1.setDuration(1000);
        mAnimator1.setRepeatCount(-1);
        mAnimator1.setRepeatMode(ObjectAnimator.REVERSE);
        mAnimator2 = ObjectAnimator.ofFloat(mSea2, "translationX", 50, -50);
        mAnimator2.setDuration(1000);
        mAnimator2.setRepeatCount(-1);
        mAnimator2.setRepeatMode(ObjectAnimator.REVERSE);
        mStartX = -DpUtil.dp2px(280);
        mMiddleX = DpUtil.dp2px(30);
        mEndX = DpUtil.dp2px(385);
        mAnimator3 = ObjectAnimator.ofFloat(mShip, "translationX", mStartX, mMiddleX);
        mAnimator3.setDuration(2000);
        mAnimator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessageDelayed(0, 2000);
            }
        });
        mAnimator4 = ObjectAnimator.ofFloat(mShip, "translationX", mMiddleX, mEndX);
        mAnimator4.setDuration(3000);
        mAnimator4.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator1.cancel();
                mAnimator2.cancel();
                mParent.removeView(mView);
                if (mOnComplete != null) {
                    mOnComplete.run();
                }
            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mAnimator4.start();
            }
        };
        mOnComplete = onComplete;
    }

    public void start(ReceiveGiftBean bean) {
        mParent.addView(mView);
        mName.setText(bean.getUname());
        ImgLoader.displayCircle(bean.getUhead(), mAvatar);
        mAnimator1.start();
        mAnimator2.start();
        mAnimator3.start();
    }
}
