package com.phonelive.game.game;

/**
 * Created by cxf on 2017/10/17.
 */

public class LastCoinEvent {
    private String coin;

    public LastCoinEvent(String coin) {
        this.coin = coin;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }
}
