package com.yunbao.phonelive.ui.base;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LoginActivity;

public class BaseDialogFragment extends DialogFragment {


    /**
     * 未登录
     */
    protected void unloginHint(View view) {
        Snackbar make = Snackbar.make(view, getResources().getString(R.string.user_unlogin_hint_txt), Snackbar.LENGTH_SHORT);
        make.setAction("登陆", v -> {
            startActivity(new Intent(getContext(), LoginActivity.class));
        });
        make.show();
    }
}
