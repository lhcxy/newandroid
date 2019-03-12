package com.yunbao.phonelive.event;

/**
 *
 */
public class LivingEvent {

    private int type;

    public LivingEvent(int position) {
        this.type = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
