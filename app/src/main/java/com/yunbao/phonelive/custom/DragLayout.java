package com.yunbao.phonelive.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import com.yunbao.phonelive.utils.DpUtil;

/**
 * Created by cxf on 2017/9/20.
 */

public class DragLayout extends RelativeLayout {

    private View mScrollView;
    private float mLastX;
    private float mLastY;
    private float mStartX;
    private float mStartY;
    private int mWidth;
    private final int SCROLL_NOT = 0;
    private final int SCROLL_ING = 1;
    private final int SCROLL_END = 2;
    private final int SCROLL_ANIM = 3;
    private int mStatus = SCROLL_NOT;
    private final int LEFT_TO_RIGHT = 10;
    private final int RIGHT_TO_LEFT = 20;
    private int mDirection = LEFT_TO_RIGHT;
    private final float SPEED = 2.5f;
    private final int mTouchY = DpUtil.dp2px(90);

    private TimeInterpolator mTimeInterpolator = new AccelerateInterpolator();

    public DragLayout(Context context) {
        super(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollView(View scrollView) {
        mScrollView = scrollView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        float x = e.getRawX();
        float y = e.getRawY();
        boolean intercepted = false;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - mStartX) > 20) {
                    float dx = x - mLastX;
                    float dy = y - mLastY;
                    if (Math.abs(dx) > Math.abs(dy)) {
                        if (mScrollView != null) {
                            if (mStatus == SCROLL_NOT && dx > 0 && y > mTouchY || mStatus == SCROLL_END && dx < 0) {
                                return true;
                            }
                        }
                    }
                }
                break;
        }
        return intercepted;
    }

    private void scroll(float dx, float dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            if (mScrollView != null) {
                if (dx > 0) {
                    mDirection = LEFT_TO_RIGHT;
                    if (mStatus == SCROLL_NOT) {
                        mStatus = SCROLL_ING;
                    }
                }
                if (dx < 0) {
                    mDirection = RIGHT_TO_LEFT;
                    if (mStatus == SCROLL_END) {
                        mStatus = SCROLL_ING;
                    }
                }
            }
            if (mStatus == SCROLL_ING) {
                float targetX = mScrollView.getX() + dx;
                if (targetX >= mWidth) {
                    targetX = mWidth;
                    mStatus = SCROLL_END;
                }
                if (targetX <= 0) {
                    targetX = 0;
                    mStatus = SCROLL_NOT;
                }
                mScrollView.setX(targetX);
            }
        }
    }

    private void up() {
        if (mStatus != SCROLL_ING) {
            return;
        }
        if (mDirection == LEFT_TO_RIGHT) {
            if (mScrollView.getX() >= mWidth / 4) {
                leftToRight();
            } else {
                rightToLeft();
            }
        } else if (mDirection == RIGHT_TO_LEFT) {
            if (mScrollView.getX() <= mWidth * 3 / 4) {
                rightToLeft();
            } else {
                leftToRight();
            }
        }
    }

    private void leftToRight() {
        ObjectAnimator a = ObjectAnimator.ofFloat(mScrollView, "x", mWidth);
        int duration = (int) ((mWidth - mScrollView.getX()) / SPEED);
        a.setDuration(duration);
        a.setInterpolator(mTimeInterpolator);
        a.addListener(mLeftToRight);
        a.start();
        mStatus = SCROLL_ANIM;
    }

    private void rightToLeft() {
        ObjectAnimator a = ObjectAnimator.ofFloat(mScrollView, "x", 0);
        int duration = (int) ((mScrollView.getX()) / SPEED);
        a.setDuration(duration);
        a.setInterpolator(mTimeInterpolator);
        a.addListener(mRightToLeft);
        a.start();
        mStatus = SCROLL_ANIM;
    }

    private AnimatorListenerAdapter mLeftToRight = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            mStatus = SCROLL_END;
        }
    };

    private AnimatorListenerAdapter mRightToLeft = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            mStatus = SCROLL_NOT;
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getRawX();
        float y = e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                scroll(x - mLastX, y - mLastY);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                up();
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }
}
