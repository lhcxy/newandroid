package com.yunbao.phonelive.pay.wx;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.bean.ChargeBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.pay.PayCallback;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.yunbao.phonelive.AppConfig.TQB_RATE;

/**
 * Created by cxf on 2017/9/22.
 */

public class WxPayTask {

    public static String sAppId;
    private IWXAPI mApi;
    private Context mContext;
    private PayCallback mPayCallback;
    private String payMoney = "";

    public WxPayTask(Context context, String payMoney, PayCallback callback) {
        mContext = context;
        this.payMoney = payMoney;
        mPayCallback = callback;
        mApi = WXAPIFactory.createWXAPI(AppContext.sInstance, sAppId);
        mApi.registerApp(sAppId);
        EventBus.getDefault().register(this);
    }


    public void getOrder() {
        if (TextUtils.isEmpty(sAppId)) {
            ToastUtil.show("微信支付未接入");
            return;
        }
        HttpUtil.getWxOrder(payMoney, String.valueOf(Double.valueOf(payMoney) * TQB_RATE), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    PayReq req = new PayReq();
                    req.appId = sAppId;
                    req.partnerId = obj.getString("partnerid");
                    req.prepayId = obj.getString("prepayid");
                    req.packageValue = "Sign=WXPay";
                    req.nonceStr = obj.getString("noncestr");
                    req.timeStamp = obj.getString("timestamp");
                    req.sign = obj.getString("sign");
                    boolean result = mApi.sendReq(req);
                    if (!result) {
                        ToastUtil.show("充值失败");
                    }
                } else {
                    ToastUtil.show("充值失败");
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayResponse(BaseResp resp) {
        L.e("resp---微信支付回调---->" + resp.errCode);
        if (mPayCallback != null) {
            if (0 == resp.errCode) {//支付成功
                mPayCallback.onSuccess((int) (Double.valueOf(payMoney) * TQB_RATE));
//                mPayCallback.onSuccess(Integer.valueOf(String.valueOf(Double.valueOf(payMoney) * TQB_RATE)));
            } else {//支付失败
                mPayCallback.onFailuer();
            }
        }
        mContext = null;
        mPayCallback = null;
        EventBus.getDefault().unregister(this);
    }

}
