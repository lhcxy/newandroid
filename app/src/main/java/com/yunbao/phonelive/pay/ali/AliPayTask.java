package com.yunbao.phonelive.pay.ali;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.bean.ChargeBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.pay.PayCallback;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import static com.yunbao.phonelive.AppConfig.TQB_RATE;

/**
 * Created by cxf on 2017/9/21.
 */

public class AliPayTask {
    // 商户ID
    public static String sPartner;
    // 商户收款账号
    public static String sSellerId;
    // 商户私钥，pkcs8格式
    public static String sPrivateKey;

    private Activity mActivity;
    private String mCoinName;
    private String mPayInfo;//支付宝订单信息 包括 商品信息，订单签名，签名类型
    private Handler mHandler;
    private String payMoney = "";

    public AliPayTask(Activity activity, String payMoney, final PayCallback callback) {
        mActivity = activity;
        this.payMoney = payMoney;
        mCoinName = AppConfig.getInstance().getConfig().getName_coin();
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                @SuppressWarnings("unchecked")
                Map<String, String> result = (Map<String, String>) msg.obj;
                if ("9000".equals(result.get("resultStatus"))) {
                    if (callback != null) {
                        callback.onSuccess(Integer.valueOf(String.valueOf((int) (Double.valueOf(payMoney) * TQB_RATE))));
                    }
                } else {
                    if (callback != null) {
                        callback.onFailuer();
                    }
                }
                mActivity = null;
            }
        };
    }
    /**
     * 从服务器端获取订单号,即下单
     */
    public void getOrder() {
        if (TextUtils.isEmpty(sPartner) || TextUtils.isEmpty(sSellerId) || TextUtils.isEmpty(sPrivateKey)) {
            ToastUtil.show("支付宝未接入");
            return;
        }
        HttpUtil.getAliOrder(payMoney, String.valueOf(Double.valueOf(payMoney) * TQB_RATE), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String orderInfo = createAliOrder(obj.getString("orderid"));//商品信息
                    String sign = getOrderSign(orderInfo);//订单签名
                    sign = urlEncode(sign);
                    String signType = "sign_type=\"RSA\"";//签名类型
                    mPayInfo = orderInfo + "&sign=\"" + sign + "\"&" + signType;
//                L.e("支付宝订单信息----->" + mPayInfo);
                    invokeAliPay();
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
                return DialogUitl.loadingDialog(mActivity);
            }


        });
    }

    /**
     * 根据订单号和商品信息生成支付宝格式的订单信息
     *
     * @param orderId 服务器返回的订单号
     * @return
     */
    private String createAliOrder(String orderId) {
        // 合作者身份ID
        String orderInfo = "partner=" + "\"" + sPartner + "\"";

        // 卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + sSellerId + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + String.valueOf(Double.valueOf(payMoney) * TQB_RATE) + mCoinName + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + String.valueOf(Double.valueOf(payMoney) * TQB_RATE) + mCoinName + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + payMoney + "\"";

        // 服务器异步通知页面路径 //服务器异步通知页面路径  参数 notify_url，如果商户没设定，则不会进行该操作
        orderInfo += "&notify_url=" + "\"" + AppConfig.getInstance().ALI_PAY_NOTIFY_URL + "\"";

        // 接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m〜15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * 根据订单信息生成订单的签名
     *
     * @param orderInfo 订单信息
     * @return
     */
    private String getOrderSign(String orderInfo) {
        return SignUtils.sign(orderInfo, sPrivateKey);
    }

    /**
     * 对订单签名进行urlencode转码
     *
     * @param sign 签名
     * @return
     */
    private String urlEncode(String sign) {
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * 调用支付宝sdk
     */
    private void invokeAliPay() {
        new Thread(() -> {
            PayTask alipay = new PayTask(mActivity);
            //执行支付，这是一个耗时操作，最后返回支付的结果，用handler发送到主线程
            Map<String, String> result = alipay.payV2(mPayInfo, true);
//                L.e("支付宝返回结果----->" + result);
            Message msg = Message.obtain();
            msg.obj = result;
            mHandler.sendMessage(msg);
        }).start();
    }

}
