package com.yunbao.phonelive.event;

/**
 * Created by cxf on 2017/8/11.
 */

public class AttentionEvent {
    private int mIsAttention;
    private String mTouid;

    public AttentionEvent(String touid, int isAttention) {
        mTouid = touid;
        mIsAttention = isAttention;
    }

    public int getIsAttention() {
        return mIsAttention;
    }

    public String getTouid() {
        return mTouid;
    }

}
