package com.yunbao.phonelive.custom.music;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yunbao.phonelive.R;


/**
 * Created by cxf on 2017/9/15.
 * 显示歌词的控件，有字体颜色从左向右渐变的效果
 */

public class LrcTextView extends android.support.v7.widget.AppCompatTextView {
    private Paint mPaint;
    private int mWidth;
    private int mBaseLine;
    private float mProgress;
    private int mProgressColor;

    public LrcTextView(Context context) {
        this(context, null);
    }

    public LrcTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LrcTextView);
        mProgress = ta.getFloat(R.styleable.LrcTextView_progress, 0);
        mProgressColor=ta.getColor(R.styleable.LrcTextView_progressColor,0xff000000);
        ta.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mProgressColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != 0) {
            mWidth = w;
        } else {
            mWidth = getMeasuredWidth();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setTextSize(getTextSize());
        Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        mBaseLine = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom + getHeight() / 2
                + getPaddingTop() / 2 - getPaddingBottom() / 2;
        canvas.save();
        int end = (int) (mProgress * mWidth);
        canvas.clipRect(0, 0, end, getHeight());
        canvas.drawText(getText().toString(), 0, mBaseLine, mPaint);
        canvas.restore();
    }

    /**
     * @param progress 0~1之间的小数
     */
    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
    }

}
