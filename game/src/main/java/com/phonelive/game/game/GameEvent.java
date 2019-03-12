package com.phonelive.game.game;

/**
 * Created by cxf on 2017/10/16.
 */

public class GameEvent {
    public static final int OPEN = 1;
    public static final int CLOSE = 2;
    private int status;

    public GameEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
