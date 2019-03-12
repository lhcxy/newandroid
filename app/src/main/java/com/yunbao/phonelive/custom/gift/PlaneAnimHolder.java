package com.yunbao.phonelive.custom.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
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

public class PlaneAnimHolder {

    private ViewGroup mParent;
    private View mView;
    private View mPlane;
    private TextView mName;
    private ImageView mAvatar;
    private Flower[] mFlowers;
    private Handler mHandler;
    private ObjectAnimator mAnimator1;
    private ObjectAnimator mAnimator2;
    private Runnable mOnComplete;

    public PlaneAnimHolder(ViewGroup parent, Runnable onComplete) {
        mParent = parent;
        mView = LayoutInflater.from(AppContext.sInstance).inflate(R.layout.view_gift_anim_plane, parent, false);
        mPlane = mView.findViewById(R.id.plane);
        mName = (TextView) mView.findViewById(R.id.uname);
        mAvatar = (ImageView) mView.findViewById(R.id.uhead);
        mFlowers = new Flower[10];
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        mAnimator2.start();
                        break;
                    case 1:
                        mFlowers[msg.arg1].floatFlower();
                        break;
                }

            }
        };
        mAnimator1 = ObjectAnimator.ofFloat(mPlane, "translationX", DpUtil.dp2px(380), DpUtil.dp2px(50));
        mAnimator1.setDuration(1500);
        mAnimator1.setInterpolator(new DecelerateInterpolator());
        mAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessageDelayed(0, 4000);
                for (int i = 0; i < mFlowers.length; i++) {
                    Message message = Message.obtain();
                    message.what = 1;
                    message.arg1 = i;
                    mHandler.sendMessageDelayed(message, i * 150);
                    message = Message.obtain();
                    message.what = 1;
                    message.arg1 = i;
                    mHandler.sendMessageDelayed(message, i * 150 + 1500);
                    message = Message.obtain();
                    message.what = 1;
                    message.arg1 = i;
                    mHandler.sendMessageDelayed(message, i * 150 + 3000);
                }
            }
        });
        mAnimator2 = ObjectAnimator.ofFloat(mPlane, "translationX", DpUtil.dp2px(50), -DpUtil.dp2px(290));
        mAnimator2.setDuration(1500);
        mAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mParent.removeView(mView);
                if (mOnComplete != null) {
                    mOnComplete.run();
                }
            }
        });
        mOnComplete = onComplete;
        initFlowers();
    }

    public void start(ReceiveGiftBean bean) {
        mParent.addView(mView);
        mName.setText(bean.getUname());
        ImgLoader.displayCircle(bean.getUhead(), mAvatar);
        mAnimator1.start();
    }

    private void initFlowers() {
        int startX = mParent.getWidth() / 2;
        int startY = DpUtil.dp2px(150);
        int dp25 = DpUtil.dp2px(25);
        int dp50 = DpUtil.dp2px(50);
        int dp75 = DpUtil.dp2px(75);
        int dp100 = DpUtil.dp2px(100);
        int dp150 = DpUtil.dp2px(150);
        int dp200 = DpUtil.dp2px(200);
        //第1条轨迹
        Path path = new Path();
        path.lineTo(0, 0);
        path.moveTo(startX, startY);
        path.rCubicTo(-dp100, dp100, dp25, dp100, -dp75, dp200);
        mFlowers[0] = new Flower(mView.findViewById(R.id.flower0), new PathMeasure(path, false));

        //第2条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(startX, startY);
        path.rCubicTo(-dp50, dp50, -dp100, dp100, -dp100, dp200);
        mFlowers[1] = new Flower(mView.findViewById(R.id.flower1), new PathMeasure(path, false));

        //第3条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(startX, startY);
        path.rCubicTo(0, dp100, dp50, dp100, dp50, dp200);
        mFlowers[2] = new Flower(mView.findViewById(R.id.flower2), new PathMeasure(path, false));

        //第4条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(startX, startY);
        path.rCubicTo(0, dp100, dp50, dp100, -dp25, dp200);
        mFlowers[3] = new Flower(mView.findViewById(R.id.flower3), new PathMeasure(path, false));

        //第5条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(startX, startY);
        path.rCubicTo(dp150, dp100, dp50, dp100, -dp100, dp200);
        mFlowers[4] = new Flower(mView.findViewById(R.id.flower4), new PathMeasure(path, false));

        //第6条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(startX, startY);
        path.rCubicTo(dp50, dp50, dp100, dp100, dp100, dp200);
        mFlowers[5] = new Flower(mView.findViewById(R.id.flower5), new PathMeasure(path, false));

        //第7条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(startX, startY);
        path.rCubicTo(0, dp100, -dp50, dp100, -dp50, dp200);
        mFlowers[6] = new Flower(mView.findViewById(R.id.flower6), new PathMeasure(path, false));

        //第8条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(startX, startY);
        path.rCubicTo(0, dp100, -dp50, dp100, dp25, dp200);
        mFlowers[7] = new Flower(mView.findViewById(R.id.flower7), new PathMeasure(path, false));

        //第9条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(startX, startY);
        path.rCubicTo(-dp150, dp100, -dp50, dp100, dp100, dp200);
        mFlowers[8] = new Flower(mView.findViewById(R.id.flower8), new PathMeasure(path, false));

        //第10条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(startX, startY);
        path.rCubicTo(dp100, dp100, -dp25, dp100, dp75, dp200);
        mFlowers[9] = new Flower(mView.findViewById(R.id.flower9), new PathMeasure(path, false));
    }


    private static class Flower {
        private View mView;
        private PathMeasure mPathMeasure;
        private ValueAnimator mAnimator;
        private float[] mPos;
        private static Interpolator interpolator = new AccelerateDecelerateInterpolator();

        public Flower(View view, PathMeasure pathMeasure) {
            mView = view;
            mPathMeasure = pathMeasure;
            mPos = new float[2];
            final float length = mPathMeasure.getLength();
            mAnimator = ValueAnimator.ofFloat(0, length);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (float) animation.getAnimatedValue();
                    float scale = v / length;
                    mPathMeasure.getPosTan(v, mPos, null);
                    mView.setX(mPos[0]);
                    mView.setY(mPos[1]);
                    mView.setAlpha(1 - scale);
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mView.getVisibility() == View.INVISIBLE) {
                        mView.setVisibility(View.VISIBLE);
                    }
                    mView.setAlpha(1f);
                }
            });
            mAnimator.setInterpolator(interpolator);
            mAnimator.setDuration(1500);
        }


        public void floatFlower() {
            mAnimator.start();
        }
    }
}
