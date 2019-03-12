package com.yunbao.phonelive.custom.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ReceiveGiftBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.utils.DpUtil;

/**
 * Created by cxf on 2017/8/23.
 * 送普通礼物的小动画的辅助类
 */

public class GiftAnimHolder {

    private View mView;
    private ImageView mHeadImg;
    private TextView mUname;
    private TextView mGiftName;
    private ImageView mGiftIcon;
    private TextView mCountTextView;
    private int mViewStartX;
    private int mGiftIconStartX;
    private ObjectAnimator mAnimator1;
    private ObjectAnimator mAnimator2;
    private ValueAnimator mAnimator3;
    private int mCount;
    private Runnable mEndRunnable;
    private String mUid = "";
    private String mGiftId = "";
    private boolean idle;


    public GiftAnimHolder(ViewGroup parent, int x, int y) {
        mView = LayoutInflater.from(AppContext.sInstance).inflate(R.layout.view_gift_anim_normal, parent, false);
        mView.setX(x);
        mView.setY(y);
        parent.addView(mView);
        mHeadImg = (ImageView) mView.findViewById(R.id.uhead);
        mUname = (TextView) mView.findViewById(R.id.uname);
        mGiftName = (TextView) mView.findViewById(R.id.gift_name);
        mGiftIcon = (ImageView) mView.findViewById(R.id.gift_icon);
        mCountTextView = (TextView) mView.findViewById(R.id.count);
        mViewStartX = x;
        mGiftIconStartX = -DpUtil.dp2px(50);
        AccelerateDecelerateInterpolator adi = new AccelerateDecelerateInterpolator();
        mAnimator1 = ObjectAnimator.ofFloat(mView, "x", mViewStartX, DpUtil.dp2px(5));
        mAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator2.start();
                mAnimator3.start();
            }
        });
        mAnimator1.setDuration(500);
        mAnimator1.setInterpolator(adi);
        mAnimator2 = ObjectAnimator.ofFloat(mGiftIcon, "translationX", mGiftIconStartX, DpUtil.dp2px(130));
        mAnimator2.setInterpolator(adi);
        mAnimator2.setDuration(500);
        mAnimator3 = ValueAnimator.ofFloat(1.5f, 0.2f, 1f);
        mAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mCountTextView.setScaleX(v);
                mCountTextView.setScaleY(v);
            }
        });
        mAnimator3.setDuration(200);
        mAnimator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mEndRunnable != null) {
                    mEndRunnable.run();
                }
            }
        });
    }


    public void startAnim(ReceiveGiftBean bean) {
        if (mUid.equals(bean.getUid())) {
            if (!TextUtils.isEmpty(mGiftId)&&mGiftId.equals(bean.getGiftid())) {
                mCount += bean.getCount();
                mCountTextView.setText(("x" + mCount));
                mAnimator3.start();
            } else {
                mCount = bean.getCount();
                mGiftId = bean.getGiftid();
                mCountTextView.setText(("x" + mCount));
                ImgLoader.displayCircle(bean.getGifticon(), mGiftIcon);
                mGiftName.setText(bean.getGiftname());
                mAnimator3.start();
            }
        } else {
            mUid = bean.getUid();
            mGiftId = bean.getGiftid();
            ImgLoader.displayCircleOrangeBorder(bean.getUhead(), mHeadImg);
            mUname.setText(bean.getUname());
            mGiftName.setText(bean.getGiftname());
            mCount = bean.getCount();
            mCountTextView.setText(("x" + mCount));
            ImgLoader.displayCircle(bean.getGifticon(), mGiftIcon);
            mAnimator1.start();
        }
    }

    public void setEndRunnable(Runnable endRunnable) {
        mEndRunnable = endRunnable;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public boolean isIdle() {
        return idle;
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }

    public void dismiss() {
        mView.setX(mViewStartX);
        mGiftIcon.setTranslationX(mGiftIconStartX);
        mCount = 0;
        mUid = "";
        mGiftId = "";
        idle = true;
    }
}
