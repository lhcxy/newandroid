package com.yunbao.phonelive.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class DanmuRL extends RelativeLayout {
    public DanmuRL(Context context) {
        super(context);
    }

    public DanmuRL(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DanmuRL(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DanmuRL(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec); //获取宽的尺寸
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.UNSPECIFIED),heightMeasureSpec);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
}
