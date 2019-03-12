package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;
import com.yunbao.phonelive.bean.ChatUserBean;
import com.yunbao.phonelive.event.AttentionEvent;
import com.yunbao.phonelive.event.EMChatExitEvent;
import com.yunbao.phonelive.event.IgnoreUnReadEvent;
import com.yunbao.phonelive.event.LiveRoomCloseEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.JIM;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.DateUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.jpush.im.android.api.model.Message;


/**
 * Created by cxf on 2017/8/10.
 */

public class EMChatFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private int mFrom;//0 是直播间外打开聊天页面 1是直播间内打开聊天页面
    private EMChatListFragment mFragment;//已关注的fragment
    private EMChatListFragment mNoFragment;//未关注的fragment
    private FragmentManager mFragmentManager;
    private TextView mRedPoint;//已关注的红点
    private TextView mNoRedPoint;//未关注的红点
    private Message mNewMessage;
    private JIM mIM;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_chat, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(300);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = inflater.inflate(R.layout.fragment_chat, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        EventBus.getDefault().register(this);
    }

    public void setIM(JIM im) {
        mIM = im;
    }

    private void initView() {
        mFrom = getArguments().getInt("from");
        mRootView.findViewById(R.id.back).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_attention).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_no_attention).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_ignore_message).setOnClickListener(this);
        mRedPoint = (TextView) mRootView.findViewById(R.id.red_point);
        mNoRedPoint = (TextView) mRootView.findViewById(R.id.no_red_point);
        String uids = getUids();
        //已关注
        mFragment = new EMChatListFragment();
        mFragment.setIM(mIM);
        Bundle bundle = new Bundle();
        bundle.putInt("from", mFrom);
        bundle.putInt("isAttention", 1);
        bundle.putString("uids", uids);
        mFragment.setArguments(bundle);
        mFragment.setUnReadCallback(new CommonCallback<Integer>() {
            @Override
            public void callback(Integer unReadCount) {
                refreshRedPoint(true, false);
            }
        });
        //未关注
        mNoFragment = new EMChatListFragment();
        mNoFragment.setIM(mIM);
        bundle = new Bundle();
        bundle.putInt("from", mFrom);
        bundle.putInt("isAttention", 0);
        bundle.putString("uids", uids);
        mNoFragment.setArguments(bundle);
        mNoFragment.setUnReadCallback(new CommonCallback<Integer>() {
            @Override
            public void callback(Integer unReadCount) {
                refreshRedPoint(false, true);
            }
        });

        mFragmentManager = getChildFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.replaced, mFragment).show(mFragment);
        ft.add(R.id.replaced, mNoFragment).hide(mNoFragment);
        ft.commit();
    }

    private void toggleFragment(int id) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (id == R.id.btn_attention) {
            ft.show(mFragment).hide(mNoFragment);
        } else {
            ft.show(mNoFragment).hide(mFragment);
        }
        ft.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                back();
                break;
            case R.id.btn_attention:
                toggleFragment(R.id.btn_attention);
                break;
            case R.id.btn_no_attention:
                toggleFragment(R.id.btn_no_attention);
                break;
            case R.id.btn_ignore_message:
                ignoreMessage();
                break;
        }
    }

    private void ignoreMessage() {
        AppConfig.getInstance().setIgnoreMessage(true);
        mFragment.ignoreUnReadMessage();
        mNoFragment.ignoreUnReadMessage();
        EventBus.getDefault().post(new IgnoreUnReadEvent());
        ToastUtil.show(getString(R.string.ignore_unread2));
    }

    private void back() {
        if (mFrom == 0) {
            getActivity().onBackPressed();
        } else if (mFrom == 1) {
            dismiss();
        }
    }

    private String getUids() {
        return mIM.getUids();
    }

    //接收新消息事件,显示红点
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(Message message) {
        mNewMessage = message;
        if (mFragment.onNewMessage(message)) {
            refreshRedPoint(true, false);
            return;
        }
        if (mNoFragment.onNewMessage(message)) {
            refreshRedPoint(false, true);
            return;
        }
        String from = mIM.getFrom(message);
//        if (from.startsWith("web1008")) {
//            from = from.replace("web1008", "");
//        }
        HttpUtil.getPmUserInfo(from, mCallback);
    }

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            JSONObject obj = JSON.parseObject(info[0]);
            if (obj == null) {
                return;
            }
            ChatUserBean bean = JSON.toJavaObject(obj, ChatUserBean.class);
            bean.setLastMessage(mIM.getContent(mNewMessage));
            bean.setLastTime(DateUtil.getDateString(mIM.getMessageTime(mNewMessage)));
            bean.setUnReadCount(1);
            //我有没有关注对方
            int isAttention = obj.getIntValue("isattention");
            if (isAttention == 1) {
                mFragment.insertItem(bean);
            } else {
                mNoFragment.insertItem(bean);
            }
        }
    };

    //接收聊天结束返回事件，这里面要把红点去掉
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatExitEvent(EMChatExitEvent event) {
        if (event.getIsAttention() == 1) {
            mFragment.onChatBack(event.getTouid(), event.getLastMsg(), event.getLastTime());
            refreshRedPoint(true, false);
        } else {
            mNoFragment.onChatBack(event.getTouid(), event.getLastMsg(), event.getLastTime());
            refreshRedPoint(false, true);
        }
    }

    //接收直播间关闭事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveRoomCloseEvent(LiveRoomCloseEvent e) {
        dismiss();
    }

    //接收已关注 未关注的切换事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAttentionEvent(AttentionEvent event) {
        int isAttention = event.getIsAttention();
        ChatUserBean bean = null;
        if (isAttention == 1) {
            bean = mNoFragment.removeItem(event.getTouid());
            if (bean != null) {
                mFragment.insertItem(bean);
            }
        } else {
            bean = mFragment.removeItem(event.getTouid());
            if (bean != null) {
                mNoFragment.insertItem(bean);
            }
        }
        refreshRedPoint(true, true);
    }

    /**
     * 刷新红点
     *
     * @param arg1 是否刷新已关注的红点
     * @param arg2 是否刷新未关注的红点
     */
    public void refreshRedPoint(boolean arg1, boolean arg2) {
        if (arg1) {
            int attentionCount = mFragment.getUnReadCount();
            if (attentionCount > 0) {
                if (mRedPoint.getVisibility() == View.GONE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
                mRedPoint.setText(String.valueOf(attentionCount));
            } else {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.GONE);
                }
            }
        }
        if (arg2) {
            int noAttentionCount = mNoFragment.getUnReadCount();
            if (noAttentionCount > 0) {
                if (mNoRedPoint.getVisibility() == View.GONE) {
                    mNoRedPoint.setVisibility(View.VISIBLE);
                }
                mNoRedPoint.setText(String.valueOf(noAttentionCount));
            } else {
                if (mNoRedPoint.getVisibility() == View.VISIBLE) {
                    mNoRedPoint.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_PM_USER_INFO);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mContext instanceof LiveActivity) {
//            ((LiveActivity) mContext).showUnReadCount();
//        }
    }
}
