package com.yunbao.phonelive.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.interfaces.ChoseCallback;


/**
 * Created by cxf on 2017/8/8.
 */

public class DialogUitl {
    //第三方登录的时候用显示的dialog
    public static Dialog loginAuthDialog(Context context) {
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_login_auth);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static Dialog confirmDialog(Context context, String title, String message, final Callback callback) {
        return confirmDialog(context, title, message, "", "", true, callback);
    }

    public static Dialog confirmDialog(Context context, String title, String message, boolean cancel, final Callback callback) {
        return confirmDialog(context, title, message, "", "", cancel, callback);
    }

    public static Dialog confirmDialog(Context context, String title, String message, String confirmText, String cancelText, boolean cancel, final Callback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.setCancelable(cancel);
        dialog.setCanceledOnTouchOutside(cancel);
        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setText(title);
        TextView content = (TextView) dialog.findViewById(R.id.content);
        content.setText(message);
        TextView cancelBtn = (TextView) dialog.findViewById(R.id.cancel_btn);
        if (!"".equals(cancelText)) {
            cancelBtn.setText(cancelText);
        }
        TextView confirmBtn = (TextView) dialog.findViewById(R.id.confirm_btn);
        if (!"".equals(confirmText)) {
            confirmBtn.setText(confirmText);
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.cancel_btn:
                        if (callback != null) {
                            callback.cancel(dialog);
                        }
                        break;
                    case R.id.confirm_btn:
                        if (callback != null) {
                            callback.confirm(dialog);
                        }
                        break;
                }
            }
        };
        cancelBtn.setOnClickListener(listener);
        confirmBtn.setOnClickListener(listener);
        return dialog;
    }

    public static Dialog inputDialog(Context context, String title, final Callback3 callback) {
        return inputDialog(context, title, "", "", "", callback);
    }

    public static Dialog inputDialog(Context context, String title, String hint, String confirmText, String cancelText, final Callback3 callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_input);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setText(title);
        final EditText input = (EditText) dialog.findViewById(R.id.input);
        if (!"".equals(hint)) {
            input.setHint(hint);
        }
        TextView cancelBtn = (TextView) dialog.findViewById(R.id.cancel_btn);
        if (!"".equals(cancelText)) {
            cancelBtn.setText(cancelText);
        }
        TextView confirmBtn = (TextView) dialog.findViewById(R.id.confirm_btn);
        if (!"".equals(confirmText)) {
            confirmBtn.setText(confirmText);
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.cancel_btn:
                        dialog.dismiss();
                        break;
                    case R.id.confirm_btn:
                        if (callback != null) {
                            String text = input.getText().toString();
                            callback.confirm(dialog, text);
                        }
                        break;
                }
            }
        };
        cancelBtn.setOnClickListener(listener);
        confirmBtn.setOnClickListener(listener);
        return dialog;
    }

    public static Dialog messageDialog(Context context, String title, String message, String confirmText, final Callback2 callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_message);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setText(title);
        TextView content = (TextView) dialog.findViewById(R.id.content);
        content.setText(message);
        TextView confirmBtn = (TextView) dialog.findViewById(R.id.confirm_btn);
        if (!"".equals(confirmText)) {
            confirmBtn.setText(confirmText);
        }
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.confirm(dialog);
                }
            }
        });
        return dialog;
    }

    public static Dialog messageDialog(Context context, String title, String message, final Callback2 callback) {
        return messageDialog(context, title, message, "", callback);
    }


    /**
     * 用于网络请求等耗时操作的LoadingDialog
     */
    public static Dialog loadingDialog(Context context, String text) {
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_system_loading);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (!"".equals(text)) {
            TextView titleView = (TextView) dialog.findViewById(R.id.text);
            titleView.setText(text);
        }
        return dialog;
    }

    public static Dialog chooseImageDialog(Context context, final Callback2 onCameraClick, final Callback2 onAlbumClick) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_choose_img);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.bottomToTopAnim);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_camera:
                        if (onCameraClick != null) {
                            onCameraClick.confirm(dialog);
                        }
                        break;
                    case R.id.btn_album:
                        if (onAlbumClick != null) {
                            onAlbumClick.confirm(dialog);
                        }
                        break;
                    case R.id.btn_cancel:
                        dialog.dismiss();
                        break;
                }
            }
        };
        dialog.findViewById(R.id.btn_camera).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_album).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        return dialog;
    }

    public static Dialog chooseDialog(Context context, String title, String stepOne, String stepTwo, final ChoseCallback onAlbumClick) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_choose_img);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.bottomToTopAnim);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_camera:
                        if (onAlbumClick != null) {
                            onAlbumClick.onChose(1);
                        }
                        break;
                    case R.id.btn_album:
                        if (onAlbumClick != null) {
                            onAlbumClick.onChose(0);
                        }
                        break;
                }
                dialog.dismiss();
            }
        };
        ((TextView) dialog.findViewById(R.id.tv_title)).setText(title);
        ((TextView) dialog.findViewById(R.id.btn_album)).setText(stepOne);
        ((TextView) dialog.findViewById(R.id.btn_camera)).setText(stepTwo);
        dialog.findViewById(R.id.btn_camera).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_album).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        return dialog;
    }

    public static Dialog loadingDialog(Context context) {
        return loadingDialog(context, "");
    }

    public interface Callback {
        void confirm(Dialog dialog);

        void cancel(Dialog dialog);
    }

    public interface Callback2 {
        void confirm(Dialog dialog);
    }

    public interface Callback3 {
        void confirm(Dialog dialog, String text);
    }

    public static Dialog confirmNoTitleDialog(Context context, boolean cancel, final Callback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_confirm_no_title);
        dialog.setCancelable(cancel);
        dialog.setCanceledOnTouchOutside(cancel);
        View.OnClickListener listener = v -> {
            switch (v.getId()) {
                case R.id.cancel_btn:
                    if (callback != null) {
                        callback.cancel(dialog);
                    }
                    break;
                case R.id.confirm_btn:
                    if (callback != null) {
                        callback.confirm(dialog);
                    }
                    break;
            }
        };
        dialog.findViewById(R.id.cancel_btn).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.confirm_btn).setOnClickListener(listener);
        return dialog;
    }

    public static Dialog confirmNoTitleDialog(Context context, String content, String comfirmStr, boolean cancel, final Callback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_confirm_no_title);
        dialog.setCancelable(cancel);
        dialog.setCanceledOnTouchOutside(cancel);
        ((TextView) dialog.findViewById(R.id.content)).setText(content);
        ((TextView) dialog.findViewById(R.id.confirm_btn)).setText(comfirmStr);
        View.OnClickListener listener = v -> {
            switch (v.getId()) {
                case R.id.cancel_btn:
                    if (callback != null) {
                        callback.cancel(dialog);
                    }
                    dialog.dismiss();
                    break;
                case R.id.confirm_btn:
                    if (callback != null) {
                        callback.confirm(dialog);
                    }
                    dialog.dismiss();
                    break;
            }
        };
        dialog.findViewById(R.id.cancel_btn).setOnClickListener(listener);
        dialog.findViewById(R.id.confirm_btn).setOnClickListener(listener);
        return dialog;
    }

    public static Dialog completeBeer(Context context, String content) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_complete_beer);
        dialog.setCancelable(true);
        ((TextView) dialog.findViewById(R.id.content)).setText(content);
        dialog.setCanceledOnTouchOutside(true);
        View.OnClickListener listener = v -> {
            switch (v.getId()) {
                case R.id.confirm_btn:
                    dialog.dismiss();
                    break;
            }
        };
        dialog.findViewById(R.id.confirm_btn).setOnClickListener(listener);
        return dialog;
    }
}
