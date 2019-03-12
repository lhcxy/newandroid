package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;
import com.yunbao.phonelive.activity.LivePushActivity;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

/**
 * Created by cxf on 2017/8/21.
 * 直播间发言框
 */

public class LiveInputFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private EditText mInput;
    private CheckBox mDanmu;
    private InputMethodManager imm;
    private int mY;
    private String mBarrageFee;//弹幕价格
    private boolean mHided;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mY = bundle.getInt("y");
        mBarrageFee = bundle.getString("barrage_fee");
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_live_input, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(40);
        params.gravity = Gravity.TOP;
        params.y = mY;
        window.setAttributes(params);
        return dialog;
    }


    /**
     * y轴的移动
     *
     * @param dY y轴的偏移量
     */
    public void translateY(int dY) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.y = mY + dY;
            window.setAttributes(params);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInput = (EditText) mRootView.findViewById(R.id.input);
        mInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
        mInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                //软键盘弹出
                imm.showSoftInput(mInput, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
        mDanmu = (CheckBox) mRootView.findViewById(R.id.danmu);
        mDanmu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked) {
                    mInput.setHint(mContext.getString(R.string.open_alba) + mBarrageFee + AppConfig.getInstance().getConfig().getName_coin() + "/" + mContext.getString(R.string.tiao));
                } else {
                    mInput.setHint("");
                }
            }
        });
        mRootView.findViewById(R.id.btn_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        String content = mInput.getText().toString().trim();
        if (content.length() > 50) {
            ToastUtil.show(WordUtil.getString(R.string.content_too_long));
            return;
        }
        if (!"".equals(content)) {
            if (mDanmu.isChecked()) {
                if (mContext instanceof LiveActivity) {
                    ((LiveActivity) mContext).sendDanmuMessage(content);
                } else if (mContext instanceof LivePushActivity) {
                    ((LivePushActivity) mContext).sendDanmuMessage(content);
                }
            } else {
                if (mContext instanceof LiveActivity) {
                    ((LiveActivity) mContext).sendChatMessage(content);
                } else if (mContext instanceof LivePushActivity) {
                    ((LivePushActivity) mContext).sendChatMessage(content);
                }
            }
            mInput.setText("");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHided = true;
        if (mContext instanceof LiveActivity) {
            ((LiveActivity) mContext).showBottom();
        }
    }

    public boolean isHided() {
        return mHided;
    }
}
