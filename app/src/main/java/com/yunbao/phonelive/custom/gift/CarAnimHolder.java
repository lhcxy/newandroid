package com.yunbao.phonelive.custom.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ReceiveGiftBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.utils.DpUtil;

/**
 * Created by cxf on 2017/8/25.
 */

public class CarAnimHolder {
    private ViewGroup mParent;
    private View mView;
    private Runnable mOnComplete;
    private TextView mName;
    private ImageView mAvatar;
    private ImageView mCarImg;
    private View mCar;
    private Handler mHandler;
    private ValueAnimator mAnimator1;
    private ObjectAnimator mAnimator2;
    private ValueAnimator mAnimator3;
    private ValueAnimator mAnimator4;

    public CarAnimHolder(ViewGroup parent, Runnable onComplete) {
        mParent = parent;
        mOnComplete = onComplete;
        mView = LayoutInflater.from(AppContext.sInstance).inflate(R.layout.view_gift_anim_car, parent, false);
        mName = (TextView) mView.findViewById(R.id.uname);
        mAvatar = (ImageView) mView.findViewById(R.id.uhead);
        mCarImg = (ImageView) mView.findViewById(R.id.carImg);
        mCar = mView.findViewById(R.id.car);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        mAnimator2.start();
                        break;
                    case 1:
                        mAnimator3.start();
                        break;
                    case 2:
                        mAnimator4.start();
                        break;
                }
            }
        };
        final int startX = DpUtil.dp2px(370);
        final int startY1 = -DpUtil.dp2px(60);
        final int middleX1 = DpUtil.dp2px(120);
        final int middleY1 = DpUtil.dp2px(100);
        final int endX = -DpUtil.dp2px(240);
        final int startY2 = DpUtil.dp2px(350);
        final int middleX2 = DpUtil.dp2px(70);
        final int middleY2 = DpUtil.dp2px(120);
        final int endY = -DpUtil.dp2px(150);

        mAnimator1 = ValueAnimator.ofFloat(0, 1);
        mAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mCar.setTranslationX(startX + (middleX1 - startX) * v);
                mCar.setTranslationY(startY1 + (middleY1 - startY1) * v);
            }
        });
        mAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCarImg.setImageResource(R.mipmap.bg_gift_car2);
                mHandler.sendEmptyMessageDelayed(0, 200);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mCarImg.setImageResource(R.mipmap.bg_gift_car1);
            }
        });
        AccelerateDecelerateInterpolator adi = new AccelerateDecelerateInterpolator();
        AccelerateInterpolator ai = new AccelerateInterpolator();
        mAnimator1.setInterpolator(adi);
        mAnimator1.setDuration(1000);
        mAnimator2 = ObjectAnimator.ofFloat(mCar, "translationX", middleX1, endX);
        mAnimator2.setInterpolator(ai);
        mAnimator2.setDuration(1000);
        mAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCarImg.setImageResource(R.mipmap.bg_gift_car3);
                mHandler.sendEmptyMessageDelayed(1, 200);
            }
        });
        mAnimator3 = ValueAnimator.ofFloat(0, 1);
        mAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mCar.setTranslationX(startX + (middleX2 - startX) * v);
                mCar.setTranslationY(startY2 + (middleY2 - startY2) * v);
            }
        });
        mAnimator3.setInterpolator(adi);
        mAnimator3.setDuration(1500);
        mAnimator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCarImg.setImageResource(R.mipmap.bg_gift_car4);
                mHandler.sendEmptyMessageDelayed(2, 200);
            }
        });
        mAnimator4 = ValueAnimator.ofFloat(0, 1);
        mAnimator4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mCar.setTranslationX(middleX2 + (endX - middleX2) * v);
                mCar.setTranslationY(middleY2 + (endY - middleY2) * v);
            }
        });
        mAnimator4.setInterpolator(new DecelerateInterpolator());
        mAnimator4.setDuration(1500);
        mAnimator4.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mParent.removeView(mView);
                if (mOnComplete != null) {
                    mOnComplete.run();
                }
            }
        });
    }

    public void start(ReceiveGiftBean bean) {
        mParent.addView(mView);
        mName.setText(bean.getUname());
        ImgLoader.displayCircle(bean.getUhead(), mAvatar);
        mAnimator1.start();
    }
}
