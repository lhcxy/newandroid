package com.yunbao.phonelive.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by cxf on 2017/8/26.
 * 底部带有白线的TextView,用在权限管理 操作列表中
 */

public class TextView2 extends TextView {

    private Paint mPaint;
    private float mScale;
    private int mWidth;
    private int mHeight;
    private int mLineHeight;

    public TextView2(Context context) {
        this(context, null);
    }

    public TextView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScale = context.getResources().getDisplayMetrics().density;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xffffffff);
        mLineHeight = dp2px(1);
        mPaint.setStrokeWidth(mLineHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, mHeight - mLineHeight, mWidth, mHeight - mLineHeight, mPaint);
    }

    private int dp2px(int dpVal) {
        return (int) (mScale * dpVal + 0.5f);
    }
}
