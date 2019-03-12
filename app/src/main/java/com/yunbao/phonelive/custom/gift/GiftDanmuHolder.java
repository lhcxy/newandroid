package com.yunbao.phonelive.custom.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.bean.EggKnockBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.StackGiftBean;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.ui.views.LiveWatcherActivity;
import com.yunbao.phonelive.utils.DpUtil;

/**
 * Created by cxf on 2017/8/25.
 */

public class GiftDanmuHolder {
    private static Interpolator sInterpolator = new LinearInterpolator();
    private static final float SPEED = 0.5f;//弹幕的速度，这个值越小，弹幕走的越慢
    private static final int MARGIN_TOP = DpUtil.dp2px(24);
    private static final int SPACE = DpUtil.dp2px(24);
    private ViewGroup mParent;
    private View mView;
    private ImageView mGift;
    private TextView mContent;
    private int mStartX;
    private int mWidth;
    private boolean canNext;//是否可以有下一个
    private boolean idle = true;//是否空闲
    private CommonCallback<Integer> mNextCallback;
    private int mLineNum;
    private StackGiftBean bean;
    private LiveBean liveBean;
    private RelativeLayout rootRl;

    public GiftDanmuHolder(ViewGroup parent, CommonCallback<Integer> nextCallback) {
        mParent = parent;
        mView = LayoutInflater.from(AppContext.sInstance).inflate(R.layout.view_gift_stack_danmu, null);
        mGift = (ImageView) mView.findViewById(R.id.gift_iv);
        mContent = (TextView) mView.findViewById(R.id.content_tv);
        mNextCallback = nextCallback;
        mView.setOnClickListener(v -> {
            Intent intent;
            boolean needFinish = false;
            if ((bean != null && (bean.getGiftid() == 46 || bean.getGiftid() == 44||bean.getGiftid() == 22 || bean.getGiftid() == 19)) || eggInfo != null) {
                if ("0".equals(liveBean.getAnyway())) {
                    intent = new Intent(mView.getContext(), LiveAudienceActivity.class);
                    if (mView.getContext() instanceof LiveWatcherActivity) {
                        needFinish = true;
                    }
                } else {
                    intent = new Intent(mView.getContext(), LiveWatcherActivity.class);
                    if (mView.getContext() instanceof LiveAudienceActivity) {
                        needFinish = true;
                    }
                }
                intent.putExtra("liveBean", liveBean);
                mView.getContext().startActivity(intent);
                if (needFinish) {
                    if (mView.getContext() instanceof Activity) {
                        ((Activity) mView.getContext()).finish();
                    }
                }
            }
        });
    }

//    public static final int NORMAL = 0;
//    public static final int SYSTEM = 1;
//    public static final int GIFT = 2;
//    public static final int ENTER_ROOM = 3;

    public void show(StackGiftBean bean, LiveBean liveBean) {
        mStartX = mParent.getWidth();
        this.bean = bean;
        this.liveBean = liveBean;
        ForegroundColorSpan textColorSpan = new ForegroundColorSpan(mParent.getResources().getColor(R.color.text_color_ff326c));
        ForegroundColorSpan textColorSpan1 = new ForegroundColorSpan(mParent.getResources().getColor(R.color.text_color_ff326c));
        SpannableString nickStr = new SpannableString(bean.getUname() + "送给" + liveBean.getUser_nicename() + "一个" + bean.getGiftname());
        nickStr.setSpan(textColorSpan, 0, bean.getUname().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        nickStr.setSpan(textColorSpan1, bean.getUname().length() + 2, bean.getUname().length() + liveBean.getUser_nicename().length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        if (bean.getGiftid() == 46) {  //游艇
            mGift.setImageDrawable(mParent.getResources().getDrawable(R.mipmap.ic_gift_danmu_ship));
        } else if (44 == bean.getGiftid()) { //火箭
            mGift.setImageDrawable(mParent.getResources().getDrawable(R.mipmap.ic_gift_danmu_rocket));
        } else if (19 == bean.getGiftid()) { //跑车
            mGift.setImageDrawable(mParent.getResources().getDrawable(R.mipmap.ic_gift_danmu_car));
        } else if (22 == bean.getGiftid()) { //飞机
            mGift.setImageDrawable(mParent.getResources().getDrawable(R.mipmap.ic_gift_danmu_plane));
        }
        mContent.setText(nickStr);
        mParent.addView(mView);
        mView.measure(0, 0);
        mWidth = mView.getMeasuredWidth();
        int duration = (int) ((mStartX + mWidth) / SPEED);
        canNext = false;
        idle = false;
        mView.setY(MARGIN_TOP);
        ValueAnimator a = ValueAnimator.ofFloat(mStartX, 0);
        a.addUpdateListener(mUpdateListener);
        a.setInterpolator(sInterpolator);
        a.setDuration(duration);
        a.addListener(mAdapter);
        a.start();
    }

    private EggKnockBean eggInfo;

    public void show(String uName, EggKnockBean bean, LiveBean liveBean) {

        mStartX = mParent.getWidth();
        this.eggInfo = bean;
        this.liveBean = liveBean;
        ForegroundColorSpan textColorSpan = new ForegroundColorSpan(mParent.getResources().getColor(R.color.text_color_ff326c));
        SpannableString nickStr = new SpannableString(uName + "在房间内砸到了" + eggInfo.getLevel() + "级蛋！！！福星降临，收获" + eggInfo.getCoin() + "探球币，大家快来围观呀~");
        nickStr.setSpan(textColorSpan, 0, uName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mContent.setText(nickStr);
        setEggImg(eggInfo.getLevel());
        mParent.addView(mView);
        mView.measure(0,0);
        mWidth = mView.getMeasuredWidth();
        int duration = (int) ((mStartX + mWidth) / SPEED);
        canNext = false;
        idle = false;
        mView.setY(MARGIN_TOP);
        ValueAnimator a = ValueAnimator.ofFloat(0F, 100F);
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


    private void setEggImg(int level) {
        if (mGift != null) {
            int imageResId;
            switch (level) {
                case 1:
                    imageResId = R.mipmap.ic_egg_level_1;
                    break;
                case 2:
                    imageResId = R.mipmap.ic_egg_level_2;
                    break;
                case 3:
                    imageResId = R.mipmap.ic_egg_level_3;
                    break;
                case 4:
                    imageResId = R.mipmap.ic_egg_level_4;
                    break;
                case 5:
                    imageResId = R.mipmap.ic_egg_level_5;
                    break;
                case 6:
                    imageResId = R.mipmap.ic_egg_level_6;
                    break;
                case 7:
                    imageResId = R.mipmap.ic_egg_level_7;
                    break;
                case 8:
                    imageResId = R.mipmap.ic_egg_level_8;
                    break;
                case 9:
                    imageResId = R.mipmap.ic_egg_level_9;
                    break;
                default:
                    imageResId = R.mipmap.ic_egg_level_0;
                    break;
            }
            mGift.setImageDrawable(mGift.getResources().getDrawable(imageResId));
        }
    }
}
