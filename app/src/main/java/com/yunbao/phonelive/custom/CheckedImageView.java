package com.yunbao.phonelive.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/9/28.
 */

public class CheckedImageView extends ImageView {

    private boolean mChecked;
    private int mRadius;
    private Paint mPaint;

    public CheckedImageView(Context context) {
        this(context, null);
    }

    public CheckedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0x80000000);
        mPaint.setStyle(Paint.Style.FILL);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CheckedImageView);
        mChecked = ta.getBoolean(R.styleable.CheckedImageView_checked, false);
        ta.recycle();
    }

    public void setImageResource(int resId,boolean checked) {
        mChecked = checked;
        super.setImageResource(resId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        mRadius = getMeasuredWidth() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mChecked) {
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        }
    }
}
