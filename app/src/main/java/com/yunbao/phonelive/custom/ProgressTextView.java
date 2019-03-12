package com.yunbao.phonelive.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/9/2.
 * 带有环形进度条的TextView,用在音乐下载中
 */

public class ProgressTextView extends TextView {

    public static final int PRO_NOT = 0;//未开始,PRO是progress的缩写,PRO_NOT表示进度未开始，即下载之前
    public static final int PRO_ING = 1;//进行中，下载中
    public static final int PRO_END = 2;//结束了，下载结束
    private int mStartColor;
    private int mEndColor;
    private int mProgressColor;
    private int mHintProgressColor;
    private Paint mPaint;
    private Paint mPaint2;
    private Paint mPaint3;
    private int mWidth;
    private int mStatus;//未开始，进行中 ，已结束，三种状态
    private float mScale;
    private int mProgressWidth;
    private int mCurProgress;
    private RectF mArcRectF;
    private Context mContext;

    public ProgressTextView(Context context) {
        this(context, null);
    }

    public ProgressTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        mScale = context.getResources().getDisplayMetrics().density;
        mStartColor = 0xff0099ff;
        mEndColor = 0xffffd350;
        mProgressColor = mStartColor;
        mHintProgressColor = 0xffcccccc;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);

        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setDither(true);
        mPaint2.setStyle(Paint.Style.STROKE);
        mProgressWidth = dp2px(3);
        mPaint2.setStrokeWidth(mProgressWidth);
        mPaint2.setColor(mHintProgressColor);

        mPaint3 = new Paint();
        mPaint3.setAntiAlias(true);
        mPaint3.setDither(true);
        mPaint3.setStyle(Paint.Style.STROKE);
        mPaint3.setStrokeWidth(mProgressWidth);
        mPaint3.setColor(mProgressColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        mWidth = getMeasuredWidth();
        mArcRectF = new RectF(mProgressWidth / 2, mProgressWidth / 2, mWidth - mProgressWidth / 2, mWidth - mProgressWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mStatus) {
            case PRO_NOT:
                drawBg(canvas, mStartColor);
                break;
            case PRO_ING:
                drawProgress(canvas);
                break;
            case PRO_END:
                drawBg(canvas, mEndColor);
                break;

        }
        super.onDraw(canvas);
    }


    private void drawBg(Canvas canvas, int color) {
        mPaint.setColor(color);
        int r = mWidth / 2;
        canvas.drawCircle(r, r, r, mPaint);
    }

    private void drawProgress(Canvas canvas) {
        canvas.drawOval(mArcRectF, mPaint2);
        canvas.drawArc(mArcRectF, -90, 360 * mCurProgress / 100f, false, mPaint3);
    }

    private int dp2px(int val) {
        return (int) (mScale * val + 0.5f);
    }


    /**
     * 下载开始前
     */
    private void setNotLoading() {
        mStatus = PRO_NOT;
        setText(mContext.getString(R.string.download));
    }

    /**
     * 下载完成
     */
    private void setLoadingEnd() {
        mStatus = PRO_END;
        setText(mContext.getString(R.string.choose));
    }

    /**
     * 刷新下载进度
     *
     * @param progress
     */
    private void setProgress(int progress) {
        if (mCurProgress == 100) {
            return;
        }
        if (progress >= 100) {
            mCurProgress = 100;
            mStatus = PRO_END;
            setText(mContext.getString(R.string.choose));
            return;
        }
        mStatus = PRO_ING;
        mCurProgress = progress;
        setText("");
    }


    public void setLoadingStatus(int status, int progress) {
        switch (status) {
            case PRO_NOT:
                setNotLoading();
                break;
            case PRO_ING:
                setProgress(progress);
                break;
            case PRO_END:
                setLoadingEnd();
                break;
        }
    }
}
