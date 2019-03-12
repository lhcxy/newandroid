package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.LiveSettingCloseEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/8/26.
 * 主播、超管、管理员等对其他人进行操作时的弹窗
 */

public class LiveSettingFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private View mBtnKick;//踢人
    private View mBtnGap;//禁言
    private TextView mBtnSetAdmin;//设置或取消管理员
    private View mBtnAdminList;//管理员列表
    private View mBtnCloseLive;//超管关闭直播
    private View mBtnForbidAccount;//超管禁用账户
    private int mAction;
    private UserBean mUserBean;
    private String mLiveuid;
    private String mKickTime;//踢人时间
    private String mShutTime;//禁言时间


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_live_setting, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        mBtnKick = mRootView.findViewById(R.id.kick);
        mBtnGap = mRootView.findViewById(R.id.btn_gap);
        mBtnSetAdmin = (TextView) mRootView.findViewById(R.id.btn_set_admin);
        mBtnAdminList = mRootView.findViewById(R.id.btn_admin_list);
        mBtnCloseLive = mRootView.findViewById(R.id.btn_close_live);
        mBtnForbidAccount = mRootView.findViewById(R.id.btn_forbid_account);
        mBtnKick.setOnClickListener(this);
        mBtnGap.setOnClickListener(this);
        mBtnSetAdmin.setOnClickListener(this);
        mBtnAdminList.setOnClickListener(this);
        mBtnCloseLive.setOnClickListener(this);
        mBtnForbidAccount.setOnClickListener(this);
        mRootView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        Bundle bundle = getArguments();
        mUserBean = bundle.getParcelable("user");
        mLiveuid = bundle.getString("liveuid");
        mAction = bundle.getInt("action");
        mKickTime = bundle.getString("kick_time");
        mShutTime = bundle.getString("shut_time");
        switch (mAction) {
            case 40://自己是房间管理员
                mBtnSetAdmin.setVisibility(View.GONE);
                mBtnAdminList.setVisibility(View.GONE);
                break;
            case 60://超管管理主播
                mBtnKick.setVisibility(View.GONE);
                mBtnGap.setVisibility(View.GONE);
                mBtnSetAdmin.setVisibility(View.GONE);
                mBtnAdminList.setVisibility(View.GONE);
                mBtnCloseLive.setVisibility(View.VISIBLE);
                mBtnForbidAccount.setVisibility(View.VISIBLE);
                break;
            case 501://主播操作普通用户
                mBtnSetAdmin.setText(getString(R.string.set_admin));
                break;
            case 502://主播操作房间管理员
                mBtnSetAdmin.setText(getString(R.string.cancel_admin));
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kick://踢人
                kick();
                break;
            case R.id.btn_gap://禁言
                setShutUp();
                break;
            case R.id.btn_set_admin://设为管理
                setAdmin();
                break;
            case R.id.btn_admin_list://管理员列表
                openAdminList();
                break;
            case R.id.btn_close_live://超管关闭直播
                closeLive();
                break;
            case R.id.btn_forbid_account://超管禁用账户
                forbidAccount();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
        //dismiss();
    }

    /**
     * 踢人
     */
    private void kick() {
        HttpUtil.kicking(mUserBean.getId(), mLiveuid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    SocketUtil.getInstance().kickUser(mUserBean.getId(), mUserBean.getUser_nicename());
                    //发送这个消息是为了让个人信息弹窗关闭
                    EventBus.getDefault().post(new LiveSettingCloseEvent());
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 禁言
     */
    private void setShutUp() {
        HttpUtil.setShutUp(mUserBean.getId(), mLiveuid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    SocketUtil.getInstance().shutUpUser(mUserBean.getId(), mUserBean.getUser_nicename(), mShutTime);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 设置或取消管理员
     */
    private void setAdmin() {
        final String touid = mUserBean.getId();
        final String uname = mUserBean.getUser_nicename();
        HttpUtil.setAdmin(touid, mLiveuid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    EventBus.getDefault().post(new LiveSettingCloseEvent());
                    int isadmin = JSON.parseObject(info[0]).getIntValue("isadmin");
                    if (isadmin == 0) {
                        SocketUtil.getInstance().sendSystemMessage(touid, uname, uname + mContext.getString(R.string.admin_cancel));
                    } else if (isadmin == 1) {
                        SocketUtil.getInstance().sendSystemMessage(touid, uname, uname + mContext.getString(R.string.admin_set));
                    }
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
        });
    }

    /**
     * 打开管理员列表
     */
    private void openAdminList() {
        EventBus.getDefault().post(new LiveSettingCloseEvent());
        ((LiveActivity) mContext).openAdminListWindow();
    }


    /**
     * 超管关闭直播间
     */
    private void closeLive() {
        HttpUtil.superStopRoom(mLiveuid, "0", mColseCallback);
    }

    /**
     * 超管关闭直播间并禁用主播账户
     */
    private void forbidAccount() {
        HttpUtil.superStopRoom(mLiveuid, "1", mColseCallback);
    }

    private HttpCallback mColseCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                SocketUtil.getInstance().stopLive();
            } else {
                ToastUtil.show(msg);
            }
        }
    };

}
