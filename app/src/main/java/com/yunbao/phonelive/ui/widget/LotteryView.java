package com.yunbao.phonelive.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LotteryItemBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.ChoseCallback;

import java.util.List;

public class LotteryView extends ConstraintLayout {
    private Context mContext;
    private ImageView pointerIv, startIv, giftIv1, giftIv2, giftIv3, giftIv4, giftIv6, giftIv5, giftIv7, giftIv8;
    private TextView giftName1, giftName2, giftName3, giftName4, giftName5, giftName6, giftName7, giftName8;
    private ConstraintLayout rootView;
    public static final String TAG = "LotteryView";

    public LotteryView(Context context) {
        this(context, null);
    }

    public LotteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }


    public LotteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public LotteryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs);
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.view_lottery, this, true);
        pointerIv = findViewById(R.id.lottery_pointer_iv);
        startIv = findViewById(R.id.lottery_start_iv);
        rootView = findViewById(R.id.lottery_root_cl);
        giftIv1 = findViewById(R.id.gift_icon_1);
        giftIv2 = findViewById(R.id.gift_icon_2);
        giftIv3 = findViewById(R.id.gift_icon_3);
        giftIv4 = findViewById(R.id.gift_icon_4);
        giftIv5 = findViewById(R.id.gift_icon_5);
        giftIv6 = findViewById(R.id.gift_icon_6);
        giftIv7 = findViewById(R.id.gift_icon_7);
        giftIv8 = findViewById(R.id.gift_icon_8);
        giftName1 = findViewById(R.id.gift_name_1);
        giftName2 = findViewById(R.id.gift_name_2);
        giftName3 = findViewById(R.id.gift_name_3);
        giftName4 = findViewById(R.id.gift_name_4);
        giftName5 = findViewById(R.id.gift_name_5);
        giftName6 = findViewById(R.id.gift_name_6);
        giftName7 = findViewById(R.id.gift_name_7);
        giftName8 = findViewById(R.id.gift_name_8);
        startIv.setOnClickListener(v -> {
            if (canClick) {
                if (choseCallback != null) {
                    choseCallback.onChose(0);
                }

            }
        });
    }

    private List<LotteryItemBean> giftList;
    private boolean canClick = true;

    ObjectAnimator raAnim;

    public void startRotation() {
        canClick = false;
        raAnim = ObjectAnimator.ofFloat(pointerIv, "rotation", pointerIv.getRotation(), 360f);
        raAnim.setDuration(360);
        LinearInterpolator lin = new LinearInterpolator();
        raAnim.setInterpolator(lin);
        raAnim.setRepeatCount(-1);
        raAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                canClick = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        raAnim.start();
    }

    public void bindData(List<LotteryItemBean> giftDatas) {
        if (giftDatas != null) {
            giftList = giftDatas;
            for (int i = 0; i < giftList.size(); i++) {
                switch (i) {
                    case 0:
                        ImgLoader.display(giftList.get(i).getGifticon(), giftIv1);
                        giftName1.setText("x" + giftList.get(i).getNum());
                        break;
                    case 1:
                        ImgLoader.display(giftList.get(i).getGifticon(), giftIv2);
                        giftName2.setText("x" + giftList.get(i).getNum());
                        break;
                    case 2:
                        ImgLoader.display(giftList.get(i).getGifticon(), giftIv3);
                        giftName3.setText("x" + giftList.get(i).getNum());
                        break;
                    case 3:
                        ImgLoader.display(giftList.get(i).getGifticon(), giftIv4);
                        giftName4.setText("x" + giftList.get(i).getNum());
                        break;
                    case 4:
                        ImgLoader.display(giftList.get(i).getGifticon(), giftIv5);
                        giftName5.setText("x" + giftList.get(i).getNum());
                        break;
                    case 5:
                        ImgLoader.display(giftList.get(i).getGifticon(), giftIv6);
                        giftName6.setText("x" + giftList.get(i).getNum());
                        break;
                    case 6:
                        ImgLoader.display(giftList.get(i).getGifticon(), giftIv7);
                        giftName7.setText("x" + giftList.get(i).getNum());
                        break;
                    case 7:
                        ImgLoader.display(giftList.get(i).getGifticon(), giftIv8);
                        giftName8.setText("x" + giftList.get(i).getNum());
                        break;
                }
            }
        }
    }


    public void setResultById(@NonNull String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        float rotationNum = 0;
        for (int i = 0; i < giftList.size(); i++) {
            if (giftList.get(i).getId().equals(id)) {
                rotationNum = 22 + i * 45;
                Log.e(TAG, "setResultById: " + rotationNum);
                break;
            }
        }
        Log.e(TAG, "setResultById: start animation" + rotationNum);
        raAnim.cancel();
        float ro = 360 + rotationNum;
        ObjectAnimator ra = ObjectAnimator.ofFloat(pointerIv, "rotation", pointerIv.getRotation(), ro);
        float v = ro - pointerIv.getRotation();
        ra.setDuration(v < 180 ? 200 : v > 360 ? 380 : 300);
        LinearInterpolator lin = new LinearInterpolator();
        ra.setInterpolator(lin);
        ra.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                canClick = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                canClick = true;
            }
        });
        ra.start();
    }

    /**
     * 设置类型
     *
     * @param type 0=铜  1=银  2=金
     */
    private int type = 0;

    public void setType(int type) {
        this.type = type;
        if (type == 1) {
            rootView.setBackground(getResources().getDrawable(R.mipmap.bg_lottery_view_silver));
            pointerIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_lottery_pointer_silver));
        } else if (type == 2) {
            rootView.setBackground(getResources().getDrawable(R.mipmap.bg_lottery_view_gold));
            pointerIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_lottery_pointer_gold));
        } else {
            rootView.setBackground(getResources().getDrawable(R.mipmap.bg_lottery_view_cuprum));
            pointerIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_lottery_pointer_cuprum));
        }
    }

    private ChoseCallback choseCallback;

    public void setlistener(ChoseCallback choseCallback) {
        this.choseCallback = choseCallback;
    }
}
