package com.yunbao.phonelive.ui.views.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.DpUtil;

public class SeekPop extends PopupWindow {

    private Context context;
    private View ll_chat, ll_friend;
    private OnRangeChangedListener listener;
    VerticalRangeSeekBar zoomSeek;

    public SeekPop(Context context) {
        super(context);
        this.context = context;
        initalize();
    }


    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_seek_pop, null);
        zoomSeek = view.findViewById(R.id.zoom_seek);
        if (listener != null) {
            zoomSeek.setOnRangeChangedListener(listener);
        }
        if (maxValue != 0) {
            zoomSeek.setRange(0, maxValue);
        }
//        zoomSeek.setOnRangeChangedListener(new OnRangeChangedListener() {
//            @Override
//            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
//                Log.e("//", "onRangeChanged:leftValue= " + leftValue + "//rightValue=" + rightValue + "//isFromUser=" + isFromUser);
//            }
//
//            @Override
//            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
//                Log.e("//", "onStartTrackingTouch: isLeft=" + isLeft);
//            }
//
//            @Override
//            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
//                Log.e("//", "onStopTrackingTouch: isLeft=" + isLeft);
//            }
//        });

        setContentView(view);
        initWindow();
    }

    public void setListener(OnRangeChangedListener listener) {
        this.listener = listener;
        if (zoomSeek != null) {
            zoomSeek.setOnRangeChangedListener(this.listener);
        }
    }

    private void initWindow() {
//        DisplayMetrics d = context.getResources().getDisplayMetrics();
//        this.setWidth((int) (d.widthPixels * 0.35));
//        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.update();
        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
//        backgroundAlpha((Activity) context, 0.9f);//0.0-1.0
//        this.setOnDismissListener(() -> {
//                backgroundAlpha((Activity) context, 1f);
//        });
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
//        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void showAtBottom(View view) {
        //弹窗位置设置
        showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2), 10);
        //showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 10, 110);//有偏差
    }

    public void showAtTop(View view) {
        //弹窗位置设置
//        showAsDropDown(view, );

//        sh(view, Math.abs((view.getWidth() - getWidth()) / 2), -10);
        showAtLocation(view, Gravity.RIGHT, DpUtil.dp2px(4), DpUtil.dp2px(56));//有偏差
    }

    private float seekIndex = 0;
    private int maxValue = 0;

    /**
     * 设置进度
     *
     * @param seekIndex
     */
    public void setSeek(float seekIndex) {
        this.seekIndex = seekIndex;
        if (zoomSeek != null) {
            zoomSeek.setValue(seekIndex);
        }
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        if (zoomSeek != null && maxValue != 0) {
            zoomSeek.setRange(0, maxValue);
        }
    }
}
