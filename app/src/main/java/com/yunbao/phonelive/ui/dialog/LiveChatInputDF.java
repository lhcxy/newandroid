package com.yunbao.phonelive.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.interfaces.InputCallback;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.base.BaseDialogFragment;
import com.yunbao.phonelive.ui.tools.KeyboardUtil;
import com.yunbao.phonelive.ui.views.LiveWatcherActivity;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;

public class LiveChatInputDF extends BaseDialogFragment {

    private CompositeDisposable disposable;

    public static LiveChatInputDF newInstance() {
        Bundle args = new Bundle();
        LiveChatInputDF fragment = new LiveChatInputDF();
        fragment.setArguments(args);
        return fragment;
    }

    private Context mContext;
    private View mRootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_live_chat_input, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(dialog1 -> {
            try {
                InputMethodManager manager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                if (manager != null)
                    manager.hideSoftInputFromWindow(chatEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (listener != null && chatEt != null) {
                    listener.onSetText(chatEt.getText().toString());
                }
            } catch (Exception e) {
            }

        });
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        disposable = new CompositeDisposable();
//        Log.e("//", "onCreateView: ");
        initView();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private ImageView sendIv;
    private EditText chatEt;
    private boolean hasContent = false;


    private void initView() {
        sendIv = mRootView.findViewById(R.id.live_watcher_send_iv);
        chatEt = mRootView.findViewById(R.id.live_watcher_chat_et);

        disposable.add(RxTextView.afterTextChangeEvents(chatEt).subscribe(textViewAfterTextChangeEvent -> {
            if (textViewAfterTextChangeEvent.editable().length() > 0) {
                hasContent = true;
                sendIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_liver_chat_send));
            } else {
                hasContent = false;
                sendIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_liver_chat_gift));
            }
        }));

        disposable.add(RxView.clicks(sendIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (hasContent) {
                sendMsg();
            } else {
                showGiftView();
            }
        }));

        if (viewType == 1) {
            mRootView.findViewById(R.id.live_watcher_bucket_iv).setVisibility(View.GONE);
        }

        disposable.add(RxView.clicks(mRootView.findViewById(R.id.live_watcher_bucket_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            dismiss();
            showBeerBucketView();
        }));
        chatEt.requestFocus();
        Objects.requireNonNull(getDialog().getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (chatEt != null) {
            chatEt.setText(editerContent);
        }
    }

    private void sendMsg() {
//        SocketUtil.getInstance().sendChatMsg(trim);
        String trim = chatEt.getText().toString().trim();
        if (getActivity() instanceof LiveWatcherActivity) {
            ((LiveWatcherActivity) getActivity()).sendMsg();
        } else if (getActivity() instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) getActivity()).sendMsg(trim);
        }
        KeyboardUtil.hideSoftInput(chatEt);
        dismiss();
    }

    public void clearEt() {
        if (isAdded()) {
            editerContent = "";
            chatEt.setText(editerContent);
        }
    }

    private void showGiftView() {
        if (getActivity() instanceof LiveWatcherActivity) {
            ((LiveWatcherActivity) getActivity()).showGiftView();
        } else if (getActivity() instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) getActivity()).openGiftWindow();
        }
        dismiss();
    }

    private void showBeerBucketView() {
        if (getActivity() instanceof LiveWatcherActivity) {
            ((LiveWatcherActivity) getActivity()).showBeerBucketView();
        }

    }

    private InputCallback listener;

    public void setListener(InputCallback listener) {
        this.listener = listener;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (listener != null) {
            listener.onSetText(chatEt.getText().toString());
        }
        try {
            InputMethodManager manager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            if (null != manager)
                manager.hideSoftInputFromWindow(chatEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
        super.onCancel(dialog);
    }

    @Override
    public void dismiss() {
        if (listener != null) {
            listener.onSetText(chatEt.getText().toString());
        }
        try {
            InputMethodManager manager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            if (null != manager)
                manager.hideSoftInputFromWindow(chatEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
        super.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (listener != null) {
            listener.onSetText(chatEt.getText().toString());
        }
        try {
            InputMethodManager manager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            if (null != manager)
                manager.hideSoftInputFromWindow(chatEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
        super.onDismiss(dialog);
    }

    private String editerContent = "";

    public void setEditerContent(String editerContent) {
        this.editerContent = editerContent;
    }

    private int viewType = 0;

    public void setViewType(int i) {
        viewType = i;
    }
}
