package com.yunbao.phonelive.event;

/**
 * Created by cxf on 2017/8/28.
 */

public class VisibleHeightEvent {
    private int mVisibleHeight;
    public VisibleHeightEvent(int visibleHeight){
        mVisibleHeight=visibleHeight;
    }

    public int getVisibleHeight() {
        return mVisibleHeight;
    }

    public void setVisibleHeight(int visibleHeight) {
        mVisibleHeight = visibleHeight;
    }
}
