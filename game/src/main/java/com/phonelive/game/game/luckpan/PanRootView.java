package com.phonelive.game.game.luckpan;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/10/22.
 */

public class PanRootView extends FrameLayout {

    private int mDeltaHeight;

    public PanRootView(Context context) {
        this(context, null);
    }

    public PanRootView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanRootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PanRootView);
        mDeltaHeight = (int) ta.getDimension(R.styleable.PanRootView_deltaHeight, 0);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(size / 2 + mDeltaHeight, mode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setDeltaHeight(int deltaHeight) {
        mDeltaHeight = deltaHeight;
    }
}
