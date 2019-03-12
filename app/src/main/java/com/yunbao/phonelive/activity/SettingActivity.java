package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.event.RefreshUserInfoEvent;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.ui.views.AboutUsActivity;
import com.yunbao.phonelive.ui.views.CommonSettingActivity;
import com.yunbao.phonelive.ui.views.FeedBackActivity;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.GlideCatchUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.LoginUtil;
import com.yunbao.phonelive.utils.TimeOutCountDown;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.VersionUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/8/18.
 */

public class SettingActivity extends AbsActivity {

    private Dialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void main() {
        setTitle(getString(R.string.setting));

        if (AppConfig.isUnlogin()) {
            findViewById(R.id.btn_logout).setVisibility(View.GONE);
        }
    }

    public void settingClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset_pwd:
//                startActivity(new Intent(mContext, ResetPwdActivity.class)); //充值密码
                startActivity(new Intent(mContext, CommonSettingActivity.class)); //重置密码
                break;
            case R.id.btn_aboutus:
                startActivity(new Intent(mContext, AboutUsActivity.class));
                break;
            case R.id.btn_feedback:
                startActivity(new Intent(this, FeedBackActivity.class));
                break;
            case R.id.btn_check_update:
                checkVersion();
                break;
            case R.id.btn_clear_cache:
                clearCache();
                break;
            case R.id.btn_logout:
                LoginUtil.logout();
                EventBus.getDefault().post(new RefreshUserInfoEvent());
                finish();
                break;
        }
    }


//    private String getCacheSize() {
//        String cache = GlideCatchUtil.getInstance().getCacheSize();
//        L.e("缓存大小--->" + cache);
//        if ("0.0Byte".equalsIgnoreCase(cache)) {
//            cache = getString(R.string.no_cache);
//        }
//        return cache;
//    }

    private void clearCache() {
        if (mDialog == null) {
            mDialog = DialogUitl.loadingDialog(mContext, getString(R.string.clear_ing));
        }
        mDialog.show();
        GlideCatchUtil.getInstance().clearImageAllCache();
        new TimeOutCountDown(2000, new TimeOutCountDown.Callback() {
            @Override
            public void callback() {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
//                mCache.setText(getCacheSize());
            }
        }).start();

    }

    private void checkVersion() {
        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(final ConfigBean bean) {
                VersionUtil.checkVersion(bean, mContext, new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(WordUtil.getString(R.string.cur_version_newset));
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_CONFIG);
        super.onDestroy();

    }
}
