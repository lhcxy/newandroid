package com.yunbao.phonelive.event;

public class LiveTypeEvent {

    private int position;

    public LiveTypeEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
