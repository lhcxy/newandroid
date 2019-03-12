package com.yunbao.phonelive.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SingleLineTextView extends android.support.v7.widget.AppCompatTextView {
    public static final String TAG = SingleLineTextView.class.getCanonicalName();

    public SingleLineTextView(Context context) {
        this(context, null);
    }

    public SingleLineTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleLineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec); //获取宽的尺寸
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.UNSPECIFIED),heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的尺寸
//        setMeasuredDimension(4000, heightSize);
        Log.e(TAG, "onMeasure: " + widthSize);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e(TAG, "onLayout: changed=" + changed + "//");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "onDraw: " + canvas.getClipBounds().width() + "//" + canvas.getClipBounds().height());
    }

    @Override
    public boolean onPreDraw() {
        Log.e(TAG, "onPreDraw: ");
        return super.onPreDraw();
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        Log.e(TAG, "onDrawForeground: " + canvas.getClipBounds().width());
    }
}
