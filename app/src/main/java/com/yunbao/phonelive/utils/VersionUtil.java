package com.yunbao.phonelive.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.ui.tools.DownloadUtil;

/**
 * Created by cxf on 2017/10/9.
 */

public class VersionUtil {
//    "https://dldir1.qq.com/weixin/android/weixin673android1360.apk"
    public static void checkVersion(ConfigBean configBean, final Context context, Runnable runnable) {
        if (!AppConfig.getInstance().getVersion().equals(configBean.getApk_ver())) {
            DialogUitl.confirmDialog(
                    context,
                    context.getString(R.string.tip),
                    configBean.getApk_des(),
                    context.getString(R.string.immediate_use),
                    context.getString(R.string.not_update), true,
                    new DialogUitl.Callback() {
                        @Override
                        public void confirm(Dialog dialog) {
                            dialog.dismiss();
                            String apkUrl = AppConfig.getInstance().getConfig().getApk_url();
                            if (TextUtils.isEmpty(apkUrl)) {
                                ToastUtil.show(context.getString(R.string.apk_url_not_exist));
                            } else {
                                if (apkUrl.startsWith("http") && apkUrl.endsWith("apk")) {
                                    DownloadUtil downloadUtil = new DownloadUtil(context);
                                    downloadUtil.download(apkUrl);
                                } else {
                                    try {
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        intent.setData(Uri.parse(apkUrl));
                                        context.startActivity(intent);
                                    } catch (Exception e) {
                                        ToastUtil.show(context.getString(R.string.apk_url_not_exist));
                                    }
                                }
                            }
                        }

                        @Override
                        public void cancel(Dialog dialog) {
                            dialog.dismiss();
                        }
                    }
            ).show();
        } else {
            if (runnable != null) {
                runnable.run();
            }
        }
    }
}
