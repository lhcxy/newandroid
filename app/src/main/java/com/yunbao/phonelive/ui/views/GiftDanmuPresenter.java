package com.yunbao.phonelive.ui.views;

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
import com.yunbao.phonelive.bean.GiftDanmuDataBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.StackGiftBean;

import java.util.concurrent.ConcurrentLinkedQueue;

public class GiftDanmuPresenter {
    private ConcurrentLinkedQueue<GiftDanmuDataBean> mDanmuGiftQueue;//弹幕队列
    private ViewGroup mParent;
    private View mView;
    private ImageView mGift;
    private TextView mContent;
    private StackGiftBean bean;
    private LiveBean liveBean;
    private static final float SPEED = 0.2f;//弹幕的速度，这个值越小，弹幕走的越慢
    private int mStartX;
    private int mWidth;
    private boolean canNext = true;//是否可以有下一个
    private static Interpolator sInterpolator = new LinearInterpolator();
    private ForegroundColorSpan textColorSpan, textColorSpan1;

    public GiftDanmuPresenter(ViewGroup mAnimContainer) {
        this.mParent = mAnimContainer;
        mParent.removeAllViews();
        mDanmuGiftQueue = new ConcurrentLinkedQueue<>();
        initDanmuView();
        textColorSpan = new ForegroundColorSpan(mParent.getResources().getColor(R.color.text_color_ff326c));
        textColorSpan1 = new ForegroundColorSpan(mParent.getResources().getColor(R.color.text_color_ff326c));
    }
    private void initDanmuView() {
        mView = LayoutInflater.from(AppContext.sInstance).inflate(R.layout.view_gift_stack_danmu, null);
        mGift = (ImageView) mView.findViewById(R.id.gift_iv);
        mContent = (TextView) mView.findViewById(R.id.content_tv);
        mContent.setBackground(null);
        mView.setOnClickListener(v -> {
            if (liveBean == null) {
                return;
            }
            if ((bean != null && (bean.getGiftid() == 46 || bean.getGiftid() == 44)) || eggInfo != null) {
                Intent intent;
                boolean needFinish = false;
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

    public void addGiftDanmu(StackGiftBean bean, LiveBean liveBean) {
        mDanmuGiftQueue.offer(new GiftDanmuDataBean(bean, liveBean));
        if (canNext)
            showDanmu();
    }

    public void addEggDanmu(String uName, EggKnockBean bean, LiveBean liveBean) {
        mDanmuGiftQueue.offer(new GiftDanmuDataBean(uName, bean, liveBean));
        if (canNext)
            showDanmu();
    }

    private void showDanmu() {
        GiftDanmuDataBean bean = mDanmuGiftQueue.poll();
        if (bean != null) {
            show(bean);
        }
    }

    private EggKnockBean eggInfo;

    private void show(GiftDanmuDataBean data) {
        liveBean = data.getLiveBean();
        mStartX = mParent.getWidth();
        if (mView == null || mGift == null || mContent == null) {
            initDanmuView();
            show(data);
            return;
        }
        if (data.getBean() != null) {
            bean = data.getBean();
            SpannableString nickStr = new SpannableString(this.bean.getUname() + "送给" + this.liveBean.getUser_nicename() + "一个" + this.bean.getGiftname());
            nickStr.setSpan(textColorSpan, 0, this.bean.getUname().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            nickStr.setSpan(textColorSpan1, this.bean.getUname().length() + 2, this.bean.getUname().length() + this.liveBean.getUser_nicename().length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            if (this.bean.getGiftid() == 46) {  //游艇
                mGift.setImageDrawable(mParent.getResources().getDrawable(R.mipmap.ic_gift_danmu_ship));
            } else if (44 == this.bean.getGiftid()) { //火箭
                mGift.setImageDrawable(mParent.getResources().getDrawable(R.mipmap.ic_gift_danmu_rocket));
            } else if (19 == this.bean.getGiftid()) { //跑车
                mGift.setImageDrawable(mParent.getResources().getDrawable(R.mipmap.ic_gift_danmu_car));
            } else if (22 == this.bean.getGiftid()) { //飞机
                mGift.setImageDrawable(mParent.getResources().getDrawable(R.mipmap.ic_gift_danmu_plane));
            }
            mContent.setText(nickStr);
        } else if (data.getEggInfo() != null) {
            eggInfo = data.getEggInfo();
            SpannableString nickStr = new SpannableString(data.getuName() + "在房间内砸到了" + eggInfo.getLevel() + "级蛋！！！福星降临，收获" + eggInfo.getCoin() + "探球币，大家快来围观呀~");
            nickStr.setSpan(textColorSpan, 0, data.getuName().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            setEggImg(eggInfo.getLevel());
            mContent.setText(nickStr);
        }
        mParent.addView(mView);
        mView.measure(0, 0);
        mWidth = mView.getMeasuredWidth();
        int duration = (int) ((mStartX + mWidth) / SPEED);
        canNext = false;
//        mView.setY(mParent.getHeight() - mView.getHeight());
        ValueAnimator a = ValueAnimator.ofFloat(mStartX, -mWidth);
        a.addUpdateListener(mUpdateListener);
        a.setInterpolator(sInterpolator);
        a.setDuration(duration);
        a.addListener(mAdapter);
        a.start();
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

    private ValueAnimator.AnimatorUpdateListener mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float v = (float) animation.getAnimatedValue();
            mView.setX(v);
        }
    };

    private AnimatorListenerAdapter mAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            mParent.removeView(mView);
            canNext = true;
            showDanmu();
        }
    };
}
