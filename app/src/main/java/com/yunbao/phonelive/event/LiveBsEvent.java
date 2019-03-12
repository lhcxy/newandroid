package com.yunbao.phonelive.event;

public class LiveBsEvent {
    private int position;

    public LiveBsEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
