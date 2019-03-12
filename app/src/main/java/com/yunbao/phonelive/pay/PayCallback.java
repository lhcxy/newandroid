package com.yunbao.phonelive.pay;

/**
 * Created by cxf on 2017/9/21.
 */

public interface PayCallback {
    /**
     * 充值成功
     *
     * @param coin 充值后增加的钻石数量
     */
    void onSuccess(int coin);


    /**
     * 充值失败
     */
    void onFailuer();
}
