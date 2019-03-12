package com.yunbao.phonelive.custom.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ReceiveDanMuBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.Random;

/**
 * Created by cxf on 2017/8/25.
 */

public class DanmuHolder {
    private static Interpolator sInterpolator = new LinearInterpolator();
    private static final float SPEED = 0.3f;//弹幕的速度，这个值越小，弹幕走的越慢
    private static final int MARGIN_TOP = DpUtil.dp2px(16);
    private static final int SPACE = DpUtil.dp2px(24);
    private ViewGroup mParent;
    private View mView;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mContent;
    private int mStartX;
    private int mWidth;
    private boolean canNext;//是否可以有下一个
    private boolean idle = true;//是否空闲
    private CommonCallback<Integer> mNextCallback;
    private int mLineNum;


    public DanmuHolder(ViewGroup parent, CommonCallback<Integer> nextCallback) {
        mParent = parent;
        mView = LayoutInflater.from(AppContext.sInstance).inflate(R.layout.view_gift_anim_danmu, parent, false);
        mAvatar = (ImageView) mView.findViewById(R.id.uhead);
        mName = (TextView) mView.findViewById(R.id.uname);
        mContent = (TextView) mView.findViewById(R.id.content);

        mNextCallback = nextCallback;
    }

//    public static final int NORMAL = 0;
//    public static final int SYSTEM = 1;
//    public static final int GIFT = 2;
//    public static final int ENTER_ROOM = 3;

    public void show(ReceiveDanMuBean bean, int lineNum) {
        mStartX = mParent.getWidth();
        if (bean.getDanmuType() == 0) {
            mContent.setText(bean.getContent());
            if (AppConfig.getInstance().getUserBean() != null && bean.getUid().equals(AppConfig.getInstance().getUserBean().getId())) {
                mContent.setBackground(mContent.getResources().getDrawable(R.drawable.bg_network_ll));
            } else {
                mContent.setBackground(null);
            }
        } else {
            if (!TextUtils.isEmpty(bean.getUname())) {
                mContent.setText(bean.getUname() + "\t:\t" + bean.getContent());
            } else mContent.setText(bean.getContent());
        }
        mParent.addView(mView);
        mView.measure(0, 0);
        mWidth = mView.getMeasuredWidth();
        int duration = (int) ((mStartX + mWidth) / SPEED);
        canNext = false;
        idle = false;
        mLineNum = lineNum;
        int i = new Random(25).nextInt(60);
        mView.setY(MARGIN_TOP + i + lineNum * SPACE);
        ValueAnimator a = ValueAnimator.ofFloat(mStartX, -mWidth);
        a.addUpdateListener(mUpdateListener);
        a.setInterpolator(sInterpolator);
        a.setDuration(duration);
        a.addListener(mAdapter);
        a.start();
    }

    private ValueAnimator.AnimatorUpdateListener mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float v = (float) animation.getAnimatedValue();
            mView.setX(v);
            if (!canNext && v <= mStartX - mWidth - 30) {
                canNext = true;
                if (mNextCallback != null) {
                    mNextCallback.callback(mLineNum);
                }
            }
        }
    };

    private AnimatorListenerAdapter mAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            mParent.removeView(mView);
            idle = true;
        }
    };

    public boolean isIdle() {
        return idle;
    }

}
