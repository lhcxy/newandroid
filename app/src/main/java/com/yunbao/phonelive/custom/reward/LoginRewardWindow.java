package com.yunbao.phonelive.custom.reward;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.RewardAdapter;
import com.yunbao.phonelive.bean.RewardBean;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.List;

/**
 * Created by cxf on 2017/9/23.
 */

public class LoginRewardWindow implements View.OnClickListener {

    private Context mContext;
    private ViewGroup mParent;
    private View mView;
    private GridView mGridView;
    private ImageView mLast;
    private TextView mBanner;
    private RewardAdapter mAdapter;
    private Target mTarget;
    private int mStartPosition;


    public LoginRewardWindow(Context context, ViewGroup parent) {
        mContext = context;
        mParent = parent;
        LayoutInflater inflater = LayoutInflater.from(AppContext.sInstance);
        mView = inflater.inflate(R.layout.fragment_login_reward, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DpUtil.dp2px(300), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mView.setLayoutParams(params);
        mBanner = (TextView) inflater.inflate(R.layout.view_login_reward_banner, null);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(DpUtil.dp2px(150), DpUtil.dp2px(36));
        params2.addRule(RelativeLayout.CENTER_IN_PARENT);
        mBanner.setLayoutParams(params2);
        mGridView = (GridView) mView.findViewById(R.id.gridView);
        mView.findViewById(R.id.btn_get).setOnClickListener(this);
        mLast = (ImageView) mView.findViewById(R.id.last);
    }

    public void show(List<RewardBean> list, int startPosition, Target target) {
        mStartPosition = startPosition;
        mBanner.setText("x" + list.get(startPosition).getCoin());
        list.remove(list.size() - 1);
        mAdapter = new RewardAdapter(mContext, list, startPosition);
        mGridView.setAdapter(mAdapter);
        mParent.addView(mView);
        mTarget = target;
    }

    private void showAnim(int startX, int startY, int targetX, int targetY) {
        final ImageView star = new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DpUtil.dp2px(60), ViewGroup.LayoutParams.WRAP_CONTENT);
        star.setLayoutParams(params);
        star.setAdjustViewBounds(true);
        star.setImageResource(R.mipmap.icon_login_reward_star2);
        star.setTranslationX(startX);
        star.setTranslationY(startY);
        mParent.addView(star);
        Path path = new Path();
        path.lineTo(0, 0);
        path.moveTo(startX, startY);
        path.quadTo(targetX, 200, targetX, targetY);
        final PathMeasure pathMeasure = new PathMeasure(path, false);
        float length = pathMeasure.getLength();
        final float[] pos = new float[2];
        ValueAnimator animator = ValueAnimator.ofFloat(0, length);
        animator.addUpdateListener(animation -> {
            float v = (float) animation.getAnimatedValue();
            pathMeasure.getPosTan(v, pos, null);
            star.setTranslationX(pos[0]);
            star.setTranslationY(pos[1]);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mParent.removeView(star);
                mContext = null;
            }
        });
        animator.setDuration(1200);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    @Override
    public void onClick(View v) {
        if (mAdapter != null) {
            mAdapter.cancelAnimation();
            int startX = 0;
            int startY = 0;
            if (mStartPosition == 6) {
                startX = DpUtil.dp2px(180);
                startY = DpUtil.dp2px(390);
                mLast.setImageResource(R.mipmap.icon_login_reward_last_2);
            } else {
                int[] p = mAdapter.getStartLocation();
                startX = p[0];
                startY = p[1] - DpUtil.dp2px(20);
            }
            int[] target = mTarget.getPosition();
            showAnim(startX, startY, target[0], target[1]);
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mParent.removeView(mView);
                    showResult();
                }
            }, 500);
        }
    }

    private void showResult() {
        if (mParent.findViewWithTag("mBanner") != null) {
            return;
        }
        mBanner.setTag("mBanner");
        mParent.addView(mBanner);
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0.2f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mBanner.setScaleX(v);
                mBanner.setScaleY(v);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mParent.removeView(mBanner);
            }
        });
        animator.setDuration(1000);
        animator.start();
    }

    public interface Target {
        int[] getPosition();
    }
}
