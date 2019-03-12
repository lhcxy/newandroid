package com.yunbao.phonelive.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by cxf on 2017/8/23.
 * 飘心的ImageView
 */

public class HeartTextView extends android.support.v7.widget.AppCompatTextView {

    private boolean idle;

    public HeartTextView(Context context) {
        super(context);
    }

    public HeartTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isIdle() {
        return idle;
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }
}
