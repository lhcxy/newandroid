package com.yunbao.phonelive.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/8/11.
 * drawableLeft与文本一起居中显示
 */

public class LeftDrawableTextView extends TextView {

    private int mDrawableWidth;
    private int mDrawableHeight;

    public LeftDrawableTextView(Context context) {
        this(context, null);
    }

    public LeftDrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftDrawableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LeftDrawableTextView);
        mDrawableWidth = (int) ta.getDimension(R.styleable.LeftDrawableTextView_drawableWidth, 0);
        mDrawableHeight = (int) ta.getDimension(R.styleable.LeftDrawableTextView_drawableHeight, 0);
        ta.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableLeft = drawables[0];
            if (drawableLeft != null) {
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                float bodyWidth = textWidth + mDrawableWidth + drawablePadding;
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
        setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (left != null) {
            left.setBounds(0, 0, mDrawableWidth, mDrawableHeight);
        }
        setCompoundDrawables(left, top, right, bottom);
    }
}
