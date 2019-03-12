package com.yunbao.phonelive.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.interfaces.InputCallback;
import com.yunbao.phonelive.ui.base.BaseDialogFragment;
import com.yunbao.phonelive.ui.tools.KeyboardUtil;
import com.yunbao.phonelive.ui.views.LiveWatcherActivity;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;

public class LotteryInputDF extends BaseDialogFragment {

    private CompositeDisposable disposable;
    private InputCallback listener;
    private ImageView titleIv;
    private EditText chatEt;
    private Context mContext;
    private View mRootView;

    public static LotteryInputDF newInstance(int viewType) {
        Bundle args = new Bundle();
        LotteryInputDF fragment = new LotteryInputDF();
        args.putInt("viewType", viewType);
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_lottery_input, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(dialog1 -> {
            try {
                InputMethodManager manager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                if (manager != null && chatEt != null)
                    manager.hideSoftInputFromWindow(chatEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
        initView();
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    private void initView() {
        titleIv = mRootView.findViewById(R.id.title_iv);
        chatEt = mRootView.findViewById(R.id.live_watcher_chat_et);
        if (getArguments() != null) {
            viewType = getArguments().getInt("viewType", 0);
        }
        if (viewType == 1) {
            titleIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_lottery_key_silver));
        } else if (viewType == 2) {
            titleIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_lottery_key_gold));
        } else {
            titleIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_lottery_key_cuprum));
        }
        disposable.add(RxView.clicks(mRootView.findViewById(R.id.lottery_start_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (TextUtils.isEmpty(chatEt.getText().toString().trim())) {
                ToastUtil.show("请输入抽奖次数!");
                return;
            }
            if (listener != null) {
                listener.onSetText(chatEt.getText().toString().trim());
            }
        }));
        chatEt.requestFocus();
        Objects.requireNonNull(getDialog().getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }


    public void setListener(InputCallback listener) {
        this.listener = listener;
    }

    private int viewType = 0;

    public void setViewType(int i) {
        viewType = i;
    }

    @Override
    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroy();

    }
}
