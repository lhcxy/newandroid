package com.yunbao.phonelive.http;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.activity.LoginInvalidActivity;
import com.yunbao.phonelive.event.RefreshUserInfoEvent;
import com.yunbao.phonelive.receiver.ConnectivityReceiver;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.LoginUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

/**
 * Created by cxf on 2017/8/7.
 */

public abstract class HttpCallback extends AbsCallback<JsonBean> {

    private Dialog mLoadingDialog;

    @Override
    public JsonBean convertResponse(okhttp3.Response response) throws Throwable {
        JsonBean bean = JSON.parseObject(response.body().string(), JsonBean.class);
        response.close();
        return bean;
    }

    @Override
    public void onSuccess(Response<JsonBean> response) {
        JsonBean bean = response.body();
        L.e("onSuccess--->ret: " + bean.getData());
        if (200 == bean.getRet()) {
            Data data = bean.getData();
            if (data != null) {
                if (700 == data.getCode()) {
                    //token过期，重新登录
//                    Intent intent = new Intent(AppContext.sInstance, LoginInvalidActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("msg", data.getMsg());
//                    AppContext.sInstance.startActivity(intent);
                    LoginUtil.logout();
                    ToastUtil.show("用户信息已过期,请重新登陆");
                    EventBus.getDefault().post(new RefreshUserInfoEvent());
                } else {
                    onSuccess(data.getCode(), data.getMsg(), data.getInfo());
                    onSuccessStr(data.getCode(), data.getMsg(), data);
                }
            }
        } else {
            L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
            onError(bean.getMsg());
        }
    }

    @Override
    public void onError(Response<JsonBean> response) {
        Throwable t = response.getException();
        L.e("网络请求错误---->" + t.getClass() + " : " + t.getMessage());
        if (t instanceof ConnectException || t instanceof UnknownHostException || t instanceof UnknownServiceException || t instanceof SocketException) {
            ToastUtil.show("无网络连接");
            ConnectivityReceiver.sNetWorkBroken = true;
        } else {
            onError(t.getMessage());
        }
        if (showLoadingDialog() && mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public void onError(String msg) {

    }

    public abstract void onSuccess(int code, String msg, String[] info);

    public void onSuccessStr(int code, String msg, Data data) {

    }

    @Override
    public void onStart(Request<JsonBean, ? extends Request> request) {
        onStart();
    }

    public void onStart() {
        if (showLoadingDialog()) {
            if (mLoadingDialog == null) {
                mLoadingDialog = createLoadingDialog();
            }
            if (mLoadingDialog != null) {
                mLoadingDialog.show();
            }
        }
    }

    @Override
    public void onFinish() {
        if (showLoadingDialog() && mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public Dialog createLoadingDialog() {
        return null;
    }

    public boolean showLoadingDialog() {
        return false;
    }

}
