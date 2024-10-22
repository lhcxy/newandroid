package com.yunbao.phonelive.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by cxf on 2017/9/23.
 */

public class FrameAnimImageView extends ImageView {

    private Paint mPaint;
    private List<Integer> mImgList;
    private BitmapFactory.Options mOptions;
    private int mPosition;
    private Bitmap mCurBitmap;
    private int mWidth;
    private int mHeight;
    private Runnable mCompelete;
    private boolean isStarted;
    private int mDuration = 150;
    private Rect mSrc;
    private Rect mDst;
    public static final int FIT_WIDTH = 0;
    public static final int FIT_HEIGHT = 1;
    private int mScaleType = FIT_WIDTH;
    private Handler mHandler;


    public FrameAnimImageView(Context context) {
        this(context, null);
    }

    public FrameAnimImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameAnimImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mOptions = new BitmapFactory.Options();
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inSampleSize = 1;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mSrc = new Rect();
        mDst = new Rect();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                isStarted = false;
                if (mCurBitmap != null) {
                    mCurBitmap.recycle();
                }
                if (mCompelete != null) {
                    mCompelete.run();
                }
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (isStarted) {
            if (mCurBitmap != null) {
                mCurBitmap.recycle();
            }
            getNextBitmap();
            if (mCurBitmap != null) {
                int w = mCurBitmap.getWidth();
                int h = mCurBitmap.getHeight();
                mSrc.left = 0;
                mSrc.right = w;
                mSrc.top = 0;
                mSrc.bottom = h;
                if (mScaleType == FIT_WIDTH) {
                    int targetH = (int) ((mWidth / (float) w) * h);
                    int y = (mHeight - targetH) / 2;
                    mDst.left = 0;
                    mDst.right = mWidth;
                    mDst.top = y;
                    mDst.bottom = y + targetH;
                } else if (mScaleType == FIT_HEIGHT) {
                    int targetW = (int) ((mHeight / (float) h) * w);
                    int x = (mWidth - targetW) / 2;
                    mDst.left = x;
                    mDst.right = x + targetW;
                    mDst.top = 0;
                    mDst.bottom = mHeight;
                }
                canvas.drawBitmap(mCurBitmap, mSrc, mDst, mPaint);
                postInvalidateDelayed(mDuration);
            } else {
                mHandler.sendEmptyMessage(0);
            }
        }
    }

    private void getNextBitmap() {
        if (mPosition < mImgList.size()) {
            try {
                byte[] bytes = IOUtils.toByteArray(getResources().openRawResource(mImgList.get(mPosition)));
                mCurBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, mOptions);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPosition++;
        } else {
            mCurBitmap = null;
        }
    }

    public void startAnim() {
        if (isStarted) {
            return;
        }
        isStarted = true;
        mPosition = 0;
        invalidate();
    }

    public FrameAnimImageView setSource(List<Integer> list) {
        mImgList = list;
        return this;
    }

    public FrameAnimImageView setScaleType(int scaleType) {
        mScaleType = scaleType;
        return this;
    }

    public FrameAnimImageView setDuration(int duration) {
        mDuration = duration;
        return this;
    }

    public FrameAnimImageView setComplete(Runnable compelete) {
        mCompelete = compelete;
        return this;
    }

    public void stop() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mCurBitmap != null) {
            mCurBitmap.recycle();
        }
        isStarted = false;
    }
}
