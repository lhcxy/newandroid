package com.phonelive.game.game.luckpan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.DpUtil;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * Created by cxf on 2017/10/21.
 */

public class PanView extends View {

    private Paint mPaint;
    private BitmapFactory.Options mOptions;
    private int mWidth;
    private int mR;
    private Bitmap mBgBitmap;
    private ImgHolder[] mHolders;
    private int mImgWidth;
    private int mImgHeight;
    private static final int COUNT = 20;

    public PanView(Context context) {
        this(context, null);
    }

    public PanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mImgWidth = DpUtil.dp2px(40);
        mImgHeight = DpUtil.dp2px(45);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mOptions = new BitmapFactory.Options();
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inSampleSize = 1;
        mBgBitmap = getBitmap(R.mipmap.bg_zp);
        mHolders = new ImgHolder[4];
        mHolders[0] = new ImgHolder(getBitmap(R.mipmap.icon_zp_1));
        mHolders[1] = new ImgHolder(getBitmap(R.mipmap.icon_zp_2));
        mHolders[2] = new ImgHolder(getBitmap(R.mipmap.icon_zp_3));
        mHolders[3] = new ImgHolder(getBitmap(R.mipmap.icon_zp_4));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mR = w / 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBgBitmap,
                new Rect(0, 0, mBgBitmap.getWidth(), mBgBitmap.getHeight()),
                new Rect(0, 0, mWidth, mWidth), mPaint
        );
        int y = (int) (mR * 0.6f);
        Rect dst = new Rect(-mImgWidth / 2, -y - mImgHeight, mImgWidth / 2, -y);
        for (int i = 0; i < COUNT; i++) {
            canvas.save();
            canvas.translate(mR, mR); //将坐标中心平移到中心点
            int index = i % 4;
            canvas.rotate(i * 18, 0, 0);
            canvas.drawBitmap(mHolders[index].mBitmap, mHolders[index].mRect, dst, mPaint);
            canvas.restore();
        }
    }

    private Bitmap getBitmap(int resId) {
        Bitmap bitmap = null;
        try {
            byte[] bytes = IOUtils.toByteArray(getResources().openRawResource(resId));
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, mOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private class ImgHolder {
        private Bitmap mBitmap;
        private Rect mRect;

        public ImgHolder(Bitmap bitmap) {
            mBitmap = bitmap;
            mRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
    }


    public void recycleBitmap() {
        if (mHolders != null) {
            for (ImgHolder h : mHolders) {
                h.mBitmap.recycle();
            }
        }
    }
}
