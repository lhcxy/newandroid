package com.phonelive.game.game.luckpan;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by cxf on 2017/10/21.
 */

public class PanWrap extends RelativeLayout {
    public PanWrap(Context context) {
        super(context);
    }

    public PanWrap(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PanWrap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
