package com.yunbao.phonelive.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.EggKnockBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.ReceiveDanMuBean;
import com.yunbao.phonelive.bean.ReceiveGiftBean;
import com.yunbao.phonelive.bean.StackGiftBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.FrameAnimImageView;
import com.yunbao.phonelive.custom.HeartTextView;
import com.yunbao.phonelive.custom.HeartView;
import com.yunbao.phonelive.custom.gift.CarAnimHolder;
import com.yunbao.phonelive.custom.gift.DanmuHolder;
import com.yunbao.phonelive.custom.gift.GiftAnimHolder;
import com.yunbao.phonelive.custom.gift.GiftDanmuHolder;
import com.yunbao.phonelive.custom.gift.PlaneAnimHolder;
import com.yunbao.phonelive.custom.gift.ShipAnimHolder;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.interfaces.DownloadCallback;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.utils.DownloadUtil;
import com.yunbao.phonelive.utils.DpUtil;
//import com.yunbao.phonelive.utils.FrameAnimUtil;
import com.yunbao.phonelive.utils.IconUitl;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.TextRender;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2017/8/3.
 * 直播间动画
 */

public class LiveAnimPresenter {

    private Context mContext;
    private Handler mHandler;
    private Handler mHandler2;
    private int mWidth;//屏幕的宽度
    private int mHeight;//屏幕高度
    private ObjectAnimator mEnterRoomAnimator;
    private float mEnterRoomStartX;
//    private int mEnterRoomLevel;
    private static final int ENTER_ROOM_TIME = 2500;
    private boolean mEnterRoomStarted;
    private ConcurrentLinkedQueue<UserBean> mEnterRoomQueue;
    private TextView mEnterRoomView;
    //存放各种动画的容器
    private ViewGroup mAnimContainer;
    private List<HeartView> mHeartViewList;
    private int mHeartStartX;
    private int mHeartStartY;
    private PathMeasure[] mPathMeasures;
    private Random mRandom;
    private boolean lighted;//是否点亮了
    private boolean canSendFloatHeartMsg = true;//能否发送飘心socket的标识符

    private GiftAnimHolder[] mGiftAnimHolders;
    private ConcurrentLinkedQueue<ReceiveGiftBean> mNormalGiftQueue;//普通礼物队列
    private ConcurrentLinkedQueue<ReceiveGiftBean> mHaohuaGiftQueue;//豪华礼物队列
    private ConcurrentLinkedQueue<ReceiveDanMuBean> mDanmuGiftQueue;//弹幕队列
    private ConcurrentLinkedQueue<StackGiftBean> mDanmuStackGiftQueue;//全站礼物弹幕队列
    private ConcurrentLinkedQueue<EggKnockBean> mDanmuEggQueue;//全站礼物弹幕队列
    private boolean[] lines;//弹幕的轨道
    private HashMap<String, ReceiveGiftBean> mGiftBeanMap;
    private boolean canPlayHaohuaGiftAnim = true;//是否能播放豪华礼物动画的标识符

    private ShipAnimHolder mShipAnimHolder;
    private PlaneAnimHolder mPlaneAnimHolder;
    private CarAnimHolder mCarAnimHolder;
    private FrameAnimImageView mGiftAnimView;//播放烟花帧动画的ImageView
    private FrameAnimImageView mEnterRoomAnimView1;//进场坐骑帧动画的ImageView
    private GifImageView mEnterRoomAnimView2;//进场坐骑帧动画的ImageView
    private GifDrawable mEnterRoomGifDrawable;//进场坐骑帧动画的GifDrawable
    private MediaController mMediaController;//koral--/android-gif-drawable 这个库用来播放gif动画的
    private TextView mEnterRoomWords;//进场动画的喊话
    private TextView mLiveStartTextView;
    private int mLiveStartCountDown;

    private List<DanmuHolder> mDanmuHolderList;
    private List<GiftDanmuHolder> mGiftDanmuHolderList;

    public LiveAnimPresenter(Context context) {
        mContext = context;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0://重置第一条送普通礼物的动画
                        mGiftAnimHolders[0].dismiss();
                        getNextGiftBean();
                        break;
                    case 1://重置第二条送普通礼物的动画
                        mGiftAnimHolders[1].dismiss();
                        getNextGiftBean();
                        break;
                    case 2://下一个用户进房间动画
                        getNextEnterRoom();
                        break;
                    case 3://重置发送飘心sokcet的标识符
                        canSendFloatHeartMsg = true;
                        break;
                }
            }
        };
        mHandler2 = new Handler();
        mEnterRoomQueue = new ConcurrentLinkedQueue<>();
        mHeartViewList = new ArrayList<>();
        mRandom = new Random();
        mGiftAnimHolders = new GiftAnimHolder[2];
        mNormalGiftQueue = new ConcurrentLinkedQueue<>();
        mHaohuaGiftQueue = new ConcurrentLinkedQueue<>();
        mDanmuGiftQueue = new ConcurrentLinkedQueue<>();
        mDanmuStackGiftQueue = new ConcurrentLinkedQueue<>();
        mDanmuEggQueue = new ConcurrentLinkedQueue<>();
        lines = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true};
        mGiftBeanMap = new LinkedHashMap<>();
        mDanmuHolderList = new ArrayList<>();
        mGiftDanmuHolderList = new ArrayList<>();
//        mEnterRoomLevel = AppConfig.getInstance().getConfig().getEnter_tip_level();
    }


    public void setScreenDimens(int width, int height) {
        mWidth = width;
        mHeight = height;
        mHeartStartX = mWidth - DpUtil.dp2px(50);
        mHeartStartY = mHeight - DpUtil.dp2px(50);
        initPath();
    }

    /**
     * 初始化飘心的轨迹
     */
    private void initPath() {
        mPathMeasures = new PathMeasure[6];
        int dp50 = DpUtil.dp2px(50);
        int dp100 = DpUtil.dp2px(100);
        int dp200 = DpUtil.dp2px(200);
        int dp300 = DpUtil.dp2px(300);
        //第1条轨迹
        Path path = new Path();
        path.lineTo(0, 0);
        path.moveTo(mHeartStartX, mHeartStartY);
        path.rCubicTo(0, -dp100, -dp100, -dp200, -dp100, -dp300);
        mPathMeasures[0] = new PathMeasure(path, false);

        //第2条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(mHeartStartX, mHeartStartY);
        path.rCubicTo(0, -dp200, -dp50, -dp200, -dp50, -dp300);
        mPathMeasures[1] = new PathMeasure(path, false);

        //第3条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(mHeartStartX, mHeartStartY);
        path.rCubicTo(dp100, -dp100, 0, -dp300, -dp50, -dp300);
        mPathMeasures[2] = new PathMeasure(path, false);

        //第4条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(mHeartStartX, mHeartStartY);
        path.rCubicTo(0, -dp100, dp100, -dp200, 0, -dp300);
        mPathMeasures[3] = new PathMeasure(path, false);

        //第5条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(mHeartStartX, mHeartStartY);
        path.rCubicTo(0, -dp200, dp50, -dp200, dp50, -dp300);
        mPathMeasures[4] = new PathMeasure(path, false);

        //第6条轨迹
        path = new Path();
        path.lineTo(0, 0);
        path.moveTo(mHeartStartX, mHeartStartY);
        path.rCubicTo(-dp100, -dp100, 0, -dp300, dp50, -dp300);
        mPathMeasures[5] = new PathMeasure(path, false);
    }

    public void setEnterRoomView(TextView v) {
        mEnterRoomView = v;
        if (mEnterRoomAnimator == null) {
            mEnterRoomStartX = mEnterRoomView.getTranslationX();
            mEnterRoomAnimator = ObjectAnimator.ofFloat(mEnterRoomView, "translationX", mEnterRoomStartX, DpUtil.dp2px(10));
            mEnterRoomAnimator.setDuration(1000);
        }
    }

    public void setAnimContainer(ViewGroup animContainer) {
        mAnimContainer = animContainer;
    }

    public void setGiftAnimView(FrameAnimImageView giftAnimView) {
        mGiftAnimView = giftAnimView;
    }

    public void setEnterRoomAnimView(FrameAnimImageView enterRoomAnimView, GifImageView imageView) {
        mEnterRoomAnimView1 = enterRoomAnimView;
        mEnterRoomAnimView2 = imageView;
    }

    public void setEnterRoomWords(TextView textView) {
        mEnterRoomWords = textView;
    }


    /**
     * 播放入场动画
     *
     * @param bean
     */
    public void playEnterRoomAnim(UserBean bean) {
        if (mHandler == null) {
            return;
        }
        if (!mEnterRoomStarted) {
            mEnterRoomStarted = true;
//            if (bean.getLevel() >= mEnterRoomLevel) {
////                int vipType = bean.getVip().getType();
////                int carId = bean.getCar().getId();
//                Drawable drawable = null;
//                if (vipType == 0) {//没有vip
//                    if (carId == 0) {
//                        drawable = ContextCompat.getDrawable(mContext, R.mipmap.bg_enter_room_anim_normal);
//                    } else {
//                        drawable = ContextCompat.getDrawable(mContext, R.mipmap.bg_enter_room_anim_car);
//                    }
//                } else {
//                    if (carId == 0) {
//                        drawable = ContextCompat.getDrawable(mContext, R.mipmap.bg_enter_room_anim_vip);
//                    } else {
//                        drawable = ContextCompat.getDrawable(mContext, R.mipmap.bg_enter_room_anim_all);
//                    }
//                }
//                mEnterRoomView.setBackground(drawable);
//                mEnterRoomView.setText(TextRender.createEnterRoom(bean));
//                mEnterRoomAnimator.start();
//            }
//            UserBean.Car car = bean.getCar();
//            if (car.getId() != 0) {
////                playEnterRoomCarAnim(bean.getUser_nicename(), car);
//            } else {
//                if (bean.getLevel() >= mEnterRoomLevel) {
//                    mHandler.sendEmptyMessageDelayed(2, ENTER_ROOM_TIME);
//                } else {
//                    mEnterRoomStarted = false;
//                }
//            }
        } else {
            mEnterRoomQueue.offer(bean);
        }
    }


    private void playGif(File file) {
        try {
            mEnterRoomGifDrawable = new GifDrawable(file);
            mEnterRoomGifDrawable.setLoopCount(1);
            mEnterRoomAnimView2.setImageDrawable(mEnterRoomGifDrawable);
            if (mMediaController == null) {
                mMediaController = new MediaController(mContext);
                mMediaController.setVisibility(View.GONE);
            }
            mMediaController.setMediaPlayer((GifDrawable) mEnterRoomAnimView2.getDrawable());
            mMediaController.setAnchorView(mEnterRoomAnimView2);
            int duration = mEnterRoomGifDrawable.getDuration();
            if (duration < 1500) {
                duration = 1500;
            }
            mMediaController.show(duration);
            mHandler2.postDelayed(mEnterRoomAnimComplete, duration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable mEnterRoomAnimComplete = new Runnable() {
        @Override
        public void run() {
            getNextEnterRoom();
        }
    };


    /**
     * 看队列里又没有其他人，如果有，继续播放入场动画
     */
    private void getNextEnterRoom() {
        if (mEnterRoomGifDrawable != null && !mEnterRoomGifDrawable.isRecycled()) {
            mEnterRoomGifDrawable.stop();
            mEnterRoomGifDrawable.recycle();
        }
        if (mMediaController != null) {
            mMediaController.hide();
            mMediaController.setAnchorView(null);
        }
        if (mEnterRoomAnimView2 != null) {
            mEnterRoomAnimView2.setImageDrawable(null);
        }
        mEnterRoomStarted = false;
        mEnterRoomView.setTranslationX(mEnterRoomStartX);
        mEnterRoomWords.setText("");
        if (mEnterRoomAnimView2.getVisibility() == View.VISIBLE) {
            mEnterRoomAnimView2.setVisibility(View.INVISIBLE);
        }
        UserBean bean = mEnterRoomQueue.poll();
        if (bean != null) {
            playEnterRoomAnim(bean);
        }
    }

    /**
     * 点击屏幕 飘心
     */
    public void floatHeart() {
        if (mHandler == null) {
            return;
        }
        //发送点亮信息
        if (!lighted) {
            lighted = true;
//            SocketUtil.getInstance().sendLightMsg();
        }
        //5之内 最多秒发一次飘心socket
        if (canSendFloatHeartMsg) {
            canSendFloatHeartMsg = false;
            SocketUtil.getInstance().sendFloatHeart();
            mHandler.sendEmptyMessageDelayed(3, 5000);
        }
        playFloatHeartAnim();
    }

    /**
     * 播放飘心动画，每次飘一个心
     */
    public void playFloatHeartAnim() {

        final HeartView heartView = getIdleImageView();
        if (heartView == null) {
            return;
        }
        heartView.setImageResource(IconUitl.getFloatHeartDrawable(1 + mRandom.nextInt(5)));
        final PathMeasure pathMeasure = mPathMeasures[mRandom.nextInt(6)];
        final float[] pos = new float[2];
        final float length = pathMeasure.getLength();
        ValueAnimator a = ValueAnimator.ofFloat(0, length);
        a.setDuration(3000);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float v = (float) animator.getAnimatedValue();
                float scale = v / length;
                if (scale < 0.08f) {
                    heartView.setScaleX(scale * 12.5f);
                    heartView.setScaleY(scale * 12.5f);
                } else {
                    heartView.setScaleX(1 + 0.5f * scale);
                    heartView.setScaleY(1 + 0.5f * scale);
                }
                if (scale > 0.5) {
                    heartView.setAlpha((1 - scale) * 2);
                }
                pathMeasure.getPosTan(v, pos, null);
                heartView.setX(pos[0]);
                heartView.setY(pos[1]);

            }
        });
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                heartView.setX(mHeartStartX);
                heartView.setY(mHeartStartY);
                heartView.setIdle(true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                heartView.setAlpha(1f);
                heartView.setScaleX(1);
                heartView.setScaleY(1);
            }
        });
        a.start();
    }

    /**
     * 获得空闲的ImageView
     *
     * @return
     */
    private HeartView getIdleImageView() {
        HeartView heartView = null;
        for (int i = 0; i < mHeartViewList.size(); i++) {
            heartView = mHeartViewList.get(i);
            if (heartView.isIdle()) {
                heartView.setIdle(false);
                break;
            } else {
                heartView = null;
            }
        }
        if (heartView == null) {
            if (mHeartViewList.size() < 10) {
                heartView = new HeartView(mContext);
                heartView.setLayoutParams(new ViewGroup.LayoutParams(DpUtil.dp2px(30), DpUtil.dp2px(30)));
                heartView.setX(mHeartStartX);
                heartView.setX(mHeartStartY);
                mAnimContainer.addView(heartView);
                heartView.setIdle(false);
                mHeartViewList.add(heartView);
            }
        }
        return heartView;
    }

    private ArrayList<HeartTextView> freeHeartTvs = new ArrayList<>();


    private TranslateAnimation tranAnim;
    HeartTextView heartView;

    public void showHeartNumAdd() {

//        final HeartTextView  = getHeartTextView();
        if (heartView == null) {
            heartView = new HeartTextView(mContext);
            heartView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            heartView.setX(mHeartStartX);
            heartView.setY(mHeartStartY);
            heartView.setText("人气+1");
            heartView.setTextColor(mContext.getResources().getColor(R.color.global));
            mAnimContainer.addView(heartView);
            freeHeartTvs.add(heartView);
        }
        if (heartView == null) {
            return;
        }
        tranAnim = new TranslateAnimation(0, 0, 0, -DpUtil.dp2px(120));
        tranAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                heartView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                heartView.setX(mHeartStartX);
                heartView.setY(mHeartStartY);
                heartView.setVisibility(View.INVISIBLE);
                heartView.setAlpha(1.0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.3f);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(tranAnim);
        animationSet.setDuration(800);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        if (tranAnim != null) {
            heartView.startAnimation(animationSet);
        }
    }


    /**
     * 播放送礼物动画
     *
     * @param bean
     */
    public void playGiftAnim(ReceiveGiftBean bean) {
//        String giftId = bean.getGiftid();
//        if ("21".equals(giftId) || "22".equals(giftId) || "9".equals(giftId) || "19".equals(giftId)) {
//            if (canPlayHaohuaGiftAnim) {
//                playHaohuaGift(bean);
//            } else {
//                mHaohuaGiftQueue.offer(bean);
//            }
//        } else {
        playNormalGift(bean);
//        }
    }

//    /**
//     * 播放豪华礼物动画
//     */
//    private void playHaohuaGift(ReceiveGiftBean bean) {
//        canPlayHaohuaGiftAnim = false;
//        switch (bean.getGiftid()) {
//            case "21":
//                playShipAnim(bean);
//                break;
//            case "22":
//                playLoveAnim();
//                break;
//            case "9":
//                playSeaAnim();
//                break;
//            case "19":
//                playMeteorAnim();
//                break;
//        }
//    }

//    private void getNextHaohuaGift() {
//        ReceiveGiftBean bean = mHaohuaGiftQueue.poll();
//        if (bean != null) {
//            playHaohuaGift(bean);
//        } else {
//            canPlayHaohuaGiftAnim = true;
//        }
//    }

//    /**
//     * 播放游轮动画
//     */
//    private void playShipAnim(ReceiveGiftBean bean) {
//        if (mShipAnimHolder == null) {
//            mShipAnimHolder = new ShipAnimHolder(mAnimContainer, new Runnable() {
//                @Override
//                public void run() {
//                    L.e("游轮动画结束---->");
//                    getNextHaohuaGift();
//                }
//            });
//        }
//        mShipAnimHolder.start(bean);
//        mGiftAnimView
//                .setSource(FrameAnimUtil.getShipAnim())
//                .setScaleType(FrameAnimImageView.FIT_WIDTH)
//                .setDuration(80)
//                .setComplete(mFrameAnimComplete).startAnim();

//    }

//    /**
//     * 播放流星动画
//     */
//    private void playMeteorAnim() {
//        mGiftAnimView
//                .setSource(FrameAnimUtil.getMeteorAnim())
//                .setScaleType(FrameAnimImageView.FIT_WIDTH)
//                .setDuration(90)
//                .setComplete(mFrameAnimComplete).startAnim();
//    }
//
//    /**
//     * 播放海天动画
//     */
//    private void playSeaAnim() {
//        mGiftAnimView
//                .setSource(FrameAnimUtil.getSeaAnim())
//                .setScaleType(FrameAnimImageView.FIT_WIDTH)
//                .setDuration(80)
//                .setComplete(mFrameAnimComplete).startAnim();
//    }
//
//    /**
//     * 播放钻石动画
//     */
//    private void playLoveAnim() {
//        mGiftAnimView
//                .setSource(FrameAnimUtil.getLoveAnim())
//                .setScaleType(FrameAnimImageView.FIT_WIDTH)
//                .setDuration(90)
//                .setComplete(mFrameAnimComplete).startAnim();
//    }


//    /**
//     * 播放烟花动画
//     */
//    private void playFireFlowerAnim() {
//        mGiftAnimView
//                .setSource(FrameAnimUtil.getFireFlowersAnim())
//                .setScaleType(FrameAnimImageView.FIT_HEIGHT)
//                .setDuration(100)
//                .setComplete(mFrameAnimComplete).startAnim();
//    }

    //烟花动画结束后执行
//    private Runnable mFrameAnimComplete = new Runnable() {
//        @Override
//        public void run() {
//            L.e("烟花动画结束---->");
////            getNextHaohuaGift();
//        }
//    };

//    /**
//     * 播放跑车动画
//     */
//    private void playCarAnim(ReceiveGiftBean bean) {
//        if (mCarAnimHolder == null) {
//            mCarAnimHolder = new CarAnimHolder(mAnimContainer, new Runnable() {
//                @Override
//                public void run() {
//                    L.e("跑车动画结束---->");
//                    getNextHaohuaGift();
//                }
//            });
//        }
//        mCarAnimHolder.start(bean);
//    }
//
//    /**
//     * 播放飞机动画
//     */
//    private void playPlaneAnim(ReceiveGiftBean bean) {
//        if (mPlaneAnimHolder == null) {
//            mPlaneAnimHolder = new PlaneAnimHolder(mAnimContainer, new Runnable() {
//                @Override
//                public void run() {
//                    L.e("飞机动画结束---->");
//                    getNextHaohuaGift();
//                }
//            });
//        }
//        mPlaneAnimHolder.start(bean);
//    }


    /**
     * 播放普通礼物动画
     */
    private void playNormalGift(ReceiveGiftBean bean) {
        GiftAnimHolder giftAnimHolder = getIdleGiftAnimHolder(bean.getUid());
        if (giftAnimHolder == null) {
            String key = bean.getUid() + bean.getGiftid();
            ReceiveGiftBean b = mGiftBeanMap.get(key);
            if (b == null) {
                mNormalGiftQueue.offer(bean);
                mGiftBeanMap.put(key, bean);
            } else {
                b.setCount(b.getCount() + 1);
            }
        } else {
            giftAnimHolder.setIdle(false);
            giftAnimHolder.startAnim(bean);
        }
    }

    private GiftAnimHolder getIdleGiftAnimHolder(String uid) {
        if (mGiftAnimHolders[0] == null) {
            mGiftAnimHolders[0] = new GiftAnimHolder(mAnimContainer, -DpUtil.dp2px(250), DpUtil.dp2px(100));
            mGiftAnimHolders[0].setEndRunnable(new Runnable() {
                @Override
                public void run() {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 4000);
                    }
                }
            });
            return mGiftAnimHolders[0];
        }
        if (uid.equals(mGiftAnimHolders[0].getUid())) {
            if (mHandler != null) {
                mHandler.removeMessages(0);
            }
            return mGiftAnimHolders[0];
        }
        if (mGiftAnimHolders[0].isIdle()) {
            if (mGiftAnimHolders[1] != null && uid.equals(mGiftAnimHolders[1].getUid()) && mHandler != null) {
                mHandler.removeMessages(1);
                return mGiftAnimHolders[1];
            }
            if (mHandler != null) {
                mHandler.removeMessages(0);
            }
            return mGiftAnimHolders[0];
        }
        if (mGiftAnimHolders[1] == null) {
            mGiftAnimHolders[1] = new GiftAnimHolder(mAnimContainer, -DpUtil.dp2px(250), DpUtil.dp2px(130));
            mGiftAnimHolders[1].setEndRunnable(new Runnable() {
                @Override
                public void run() {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(1, 4000);
                    }
                }
            });
            return mGiftAnimHolders[1];
        }
        if (uid.equals(mGiftAnimHolders[1].getUid())) {
            if (mHandler != null) {
                mHandler.removeMessages(1);
            }
            return mGiftAnimHolders[1];
        }
        if (mGiftAnimHolders[1].isIdle()) {
            if (mHandler != null)
                mHandler.removeMessages(1);
            return mGiftAnimHolders[1];
        }
        return null;
    }

    public void getNextGiftBean() {
        ReceiveGiftBean bean = mNormalGiftQueue.poll();
        if (bean != null) {
            mGiftBeanMap.remove(bean.getUid() + bean.getGiftid());
            playGiftAnim(bean);
        }
    }

    /**
     * 显示弹幕的方法
     *
     * @param bean
     */
    public void addDanmu(ReceiveDanMuBean bean) {
        int lineNum = -1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i]) {
                lines[i] = false;
                lineNum = i;
                break;
            }
        }
        if (lineNum == -1) {
            mDanmuGiftQueue.offer(bean);
            return;
        }
        DanmuHolder danmuHolder = null;
        for (int i = 0; i < mDanmuHolderList.size(); i++) {
            if (mDanmuHolderList.get(i).isIdle()) {
                danmuHolder = mDanmuHolderList.get(i);
                break;
            }
        }
        if (danmuHolder == null) {
            danmuHolder = new DanmuHolder(mAnimContainer, mNextDanmu);
            if (mDanmuHolderList.size() < 5) {
                mDanmuHolderList.add(danmuHolder);
            }
        }
        danmuHolder.show(bean, lineNum);
    }

    /**
     * 显示弹幕的方法
     *
     * @param bean
     */
    public void addGiftDanmu(StackGiftBean bean, LiveBean liveBean) {
        int lineNum = -1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i]) {
                lines[i] = false;
                lineNum = i;
                break;
            }
        }
        if (lineNum == -1) {
            mDanmuStackGiftQueue.offer(bean);
            return;
        }
        GiftDanmuHolder danmuHolder = null;
        for (int i = 0; i < mGiftDanmuHolderList.size(); i++) {
            if (mGiftDanmuHolderList.get(i).isIdle()) {
                danmuHolder = mGiftDanmuHolderList.get(i);
                break;
            }
        }
        if (danmuHolder == null) {
            danmuHolder = new GiftDanmuHolder(mAnimContainer, mNextDanmu);
            if (mGiftDanmuHolderList.size() < 1) {
                mGiftDanmuHolderList.add(danmuHolder);
            }
        }
        danmuHolder.show(bean, liveBean);
    }

    public void addEggDanmu(String uName, EggKnockBean bean, LiveBean liveBean) {
        int lineNum = -1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i]) {
                lines[i] = false;
                lineNum = i;
                break;
            }
        }
        if (lineNum == -1) {
            mDanmuEggQueue.offer(bean);
            return;
        }
        GiftDanmuHolder danmuHolder = null;
        for (int i = 0; i < mGiftDanmuHolderList.size(); i++) {
            if (mGiftDanmuHolderList.get(i).isIdle()) {
                danmuHolder = mGiftDanmuHolderList.get(i);
                break;
            }
        }
        if (danmuHolder == null) {
            danmuHolder = new GiftDanmuHolder(mAnimContainer, mNextDanmu);
            if (mGiftDanmuHolderList.size() < 1) {
                mGiftDanmuHolderList.add(danmuHolder);
            }
        }
        danmuHolder.show(uName, bean, liveBean);
    }

    private CommonCallback<Integer> mNextDanmu = new CommonCallback<Integer>() {
        @Override
        public void callback(Integer lineNum) {
            lines[lineNum] = true;
            L.e("可用的弹幕轨道--->" + lineNum);
            getNextDanmu();
        }
    };

    public void getNextDanmu() {
        ReceiveDanMuBean bean = mDanmuGiftQueue.poll();
        if (bean != null) {
            addDanmu(bean);
        }
    }


    /**
     * 主播开播时候的倒计时动画
     */
    public void playLiveStartAnim() {
        mLiveStartCountDown = 3;
        mLiveStartTextView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.view_live_start_anim, mAnimContainer, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mLiveStartTextView.setText(String.valueOf(mLiveStartCountDown));
        mLiveStartTextView.setLayoutParams(params);
        if (mLiveStartTextView.getParent() == null) {
            mAnimContainer.addView(mLiveStartTextView);
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.5f, 0.5f);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(2);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mLiveStartTextView.setScaleX(v);
                mLiveStartTextView.setScaleY(v);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mLiveStartTextView.getParent() != null) {
                    mAnimContainer.removeView(mLiveStartTextView);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mLiveStartCountDown--;
                mLiveStartTextView.setText(String.valueOf(mLiveStartCountDown));
            }
        });
        valueAnimator.start();
    }

    public void clearAnimQueue() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mHandler2 != null) {
            mHandler2.removeCallbacksAndMessages(null);
        }
        if (mEnterRoomGifDrawable != null && !mEnterRoomGifDrawable.isRecycled()) {
            mEnterRoomGifDrawable.stop();
            mEnterRoomGifDrawable.recycle();
        }
        if (mMediaController != null) {
            mMediaController.hide();
            mMediaController.setAnchorView(null);
        }
        if (mEnterRoomAnimView2 != null) {
            mEnterRoomAnimView2.setImageDrawable(null);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mGiftAnimView != null) {
            mGiftAnimView.stop();
        }
        mEnterRoomQueue.clear();
        mNormalGiftQueue.clear();
        mHaohuaGiftQueue.clear();
        mDanmuGiftQueue.clear();
    }

}
