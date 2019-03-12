package com.yunbao.phonelive.presenter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.tools.NetworkUtil;
import com.yunbao.phonelive.ui.views.LiveWatcherActivity;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.MD5Util;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;
import com.yunbao.phonelive.utils.ToastUtil;

/**
 * Created by cxf on 2017/9/29.
 */

public class CheckLivePresenter {

    private Context mContext;
    private LiveBean mSelectLiveBean;//选中的直播间信息
    private int mLiveType;//直播间的类型  普通 密码 门票 计时等
    private int mLiveTypeVal;//收费价格等
    private String mLiveTypeMsg;//直播间提示信息或房间密码

    public CheckLivePresenter(Context context) {
        mContext = context;
    }

    public void setSelectLiveBean(LiveBean bean) {
        mSelectLiveBean = bean;
    }

    /**
     * 观众 观看直播
     */
    public void watchLive() {
        //请求接口验证 是否是加密房间 或者收费房间 或者普通房间
        if (AppConfig.getInstance().isOnlyWifi()) {
            if (NetworkUtil.isWifi(mContext)) {
                HttpUtil.checkLive(mSelectLiveBean.getUid(), mSelectLiveBean.getStream(), mCheckLiveCallback);
            } else {
                ToastUtil.show("您正在使用数据流量观看，请注意资费");
            }
        } else
            HttpUtil.checkLive(mSelectLiveBean.getUid(), mSelectLiveBean.getStream(), mCheckLiveCallback);
//        forwardLiveAudienceActivity();
    }

    private HttpCallback mCheckLiveCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info != null && info.length > 0) {
                    LiveBean liveBean = JSON.parseObject(info[0], LiveBean.class);
                    if (liveBean != null) {
                        forwardLiveAudienceActivity(liveBean);
                        return;
                    }
                }
                forwardLiveAudienceActivity();
            } else {
                ToastUtil.show(msg);
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
    };

    public void roomCharge() {
        HttpUtil.roomCharge(mSelectLiveBean.getUid(), mSelectLiveBean.getStream(), mRoomChargeCallback);
    }

    private HttpCallback mRoomChargeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                forwardLiveAudienceActivity();
            } else {
                ToastUtil.show(msg);
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
    };

    /**
     * 跳转到直播间
     */
    private int enterTimes = 0;

    private void forwardLiveAudienceActivity() {
        Intent intent;
        if ("0".equals(mSelectLiveBean.getAnyway())) {
            intent = new Intent(mContext, LiveAudienceActivity.class);
        } else {
            intent = new Intent(mContext, LiveWatcherActivity.class);
        }
        intent.putExtra("liveBean", mSelectLiveBean);
        intent.putExtra("type", mLiveType);
        intent.putExtra("typeVal", mLiveTypeVal);
        mContext.startActivity(intent);
        enterTimes++;
    }
    private void forwardLiveAudienceActivity(LiveBean mSelectLiveBean) {
        Intent intent;
        if ("0".equals(mSelectLiveBean.getAnyway())) {
            intent = new Intent(mContext, LiveAudienceActivity.class);
        } else {
            intent = new Intent(mContext, LiveWatcherActivity.class);
        }
        intent.putExtra("liveBean", mSelectLiveBean);
        intent.putExtra("type", 0);
        intent.putExtra("typeVal", 0);
        mContext.startActivity(intent);
        enterTimes++;
    }
}
