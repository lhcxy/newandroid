package com.yunbao.phonelive.utils;

import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/8/3.
 */

public class ToastUtil {

    private static Toast sToast;

    static {
        sToast = Toast.makeText(AppContext.sInstance, "", Toast.LENGTH_SHORT);
    }

    public static void show(String s) {
        if (!TextUtils.isEmpty(s)) {
            if (sToast == null) {
                sToast = Toast.makeText(AppContext.sInstance, s, Toast.LENGTH_SHORT);
            } else {
                sToast.setText(s);
            }
            sToast.setGravity(Gravity.CENTER, 0, 0);
            sToast.show();
        }


    }

    public static void showUnLoginHint() {
        if (sToast == null) {
            sToast = Toast.makeText(AppContext.sInstance, "", Toast.LENGTH_SHORT);
        }
        sToast.setText(AppContext.sInstance.getResources().getString(R.string.user_unlogin_hint_txt));
        sToast.show();
    }
}
