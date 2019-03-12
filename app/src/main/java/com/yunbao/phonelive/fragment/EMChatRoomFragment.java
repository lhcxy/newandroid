package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;
import com.yunbao.phonelive.adapter.EMChatRoomAdapter;
import com.yunbao.phonelive.bean.ChatUserBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.MyLinearLayoutManger;
import com.yunbao.phonelive.custom.NoAlphaItemAnimator;
import com.yunbao.phonelive.event.EMChatExitEvent;
import com.yunbao.phonelive.event.VisibleHeightEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.JIM;
import com.yunbao.phonelive.im.JIMUtil;
import com.yunbao.phonelive.ui.views.viewholder.ChatRoomSettingActivity;
import com.yunbao.phonelive.utils.DateUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;


/**
 * Created by cxf on 2017/8/10.
 */

public class EMChatRoomFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private TextView mTitleView;
    private EditText mEditText;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private int mFrom = 0;//0 是直播间外打开聊天页面 1是直播间内打开聊天页面
    private boolean mFromPop;//是否是用弹窗打开的
    private UserBean mUser; //自己的个人信息
    private UserBean mToUser; //对方的个人信息
    private int mIsAttention;//1  已关注 0 未关注
    private EMChatRoomAdapter<Conversation, Message, JIM> mAdapter;
    private List<Message> mList;
    private long mLastTime = 0;
    private String mCurMsg = "";
    private int mOriginHeight;//原始高度
    private int mCurHeight;//当前高度
    private JIM mIM;

    public static EMChatRoomFragment newInstance(UserBean bean) {

        Bundle args = new Bundle();
        args.putParcelable("touser", bean);
        args.putInt("from", 0);
        args.putInt("isAttention", 0);
        EMChatRoomFragment fragment = new EMChatRoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    public static EMChatRoomFragment newInstance(UserBean bean) {
//        Bundle args = new Bundle();
//        args.putParcelable("touser", bean);
//        args.putInt("from", 0);
//        args.putInt("isAttention", 0);
//        EMChatRoomFragment fragment = new EMChatRoomFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_chat_room, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        mOriginHeight = DpUtil.dp2px(300);
//        mCurHeight = mOriginHeight;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = inflater.inflate(R.layout.fragment_chat_room, container, false);
        return mRootView;
    }

    public void setIM(JIM im) {
        mIM = im;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        EventBus.getDefault().register(this);
    }

    List<String> blackList = new ArrayList<>();

    public void initView() {
        mTitleView = (TextView) mRootView.findViewById(R.id.title);
        mEditText = (EditText) mRootView.findViewById(R.id.input);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recylcerView);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressbar);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new MyLinearLayoutManger(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());
        mRootView.findViewById(R.id.btn_send).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_back).setOnClickListener(this);


//        JMessageClient.addUsersToBlacklist(blackList, new BasicCallback() {
//            @Override
//            public void gotResult(int i, String s) {
//                Log.e(TAG, i + "addUsersToBlacklist Result: " + s);
//            }
//        });
//        JMessageClient.delUsersFromBlacklist(blackList, new BasicCallback() {
//            @Override
//            public void gotResult(int i, String s) {
//                Log.e(TAG, i + "delUsersFromBlacklist gotResult: " + s);
//            }
//        });
//
//        JMessageClient.getBlacklist(new GetBlacklistCallback() {
//            @Override
//            public void gotResult(int i, String s, List<UserInfo> list) {
//                if (list != null) {
//                    for (UserInfo userInfo : list) {
//                        if (userInfo.getUserName().equals(JIMUtil.PREFIX + mToUser.getId())) {
//                            return;
//                        }
//                    }
//                }
//            }
//        });

        mRootView.findViewById(R.id.chat_room_setting_iv).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChatRoomSettingActivity.class);
            intent.putExtra("userInfo", mToUser);
            startActivity(intent);
        });


    }

    public static final String TAG = "//IM";

    public void initData() {
        mUser = AppConfig.getInstance().getUserBean();
        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.e("//mUser", "initData: EMChatRoomFragment " + bundle);
//            bundle.setClassLoader(getClass().getClassLoader());
            mFrom = bundle.getInt("from", 0);
//            if (mFrom == 1) {
//                ((LiveActivity) mContext).addLayoutListener();
//                ((LiveActivity) mContext).setChatRoomFragmentShowed(true);
//            }
            mToUser = bundle.getParcelable("touser");
            mIsAttention = bundle.getInt("isAttention");
            mFromPop = bundle.getBoolean("fromPop", false);
            mTitleView.setText(mToUser.getUser_nicename());
            Conversation conversation = mIM.getConversation(mToUser.getId());
            mProgressBar.setVisibility(View.GONE);
            if (conversation == null) {
                return;
            }
            List<Message> currentList = mIM.getAllMessages(conversation);
            int size = currentList.size();
            if (size < 20) {
                mList = mIM.loadHistoryMessage(conversation, 20 - size);
                mList.addAll(currentList);
            } else {
                mList = currentList.subList(size - 20, size);
            }
            mAdapter = new EMChatRoomAdapter<Conversation, Message, JIM>(mContext, mList, mToUser, mUser, mIM);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.smoothScrollToPosition(mList.size() - 1);

            blackList.add(JIMUtil.PREFIX + mToUser.getId());


        }
    }

//    abstract class SuperMsg extends Message {
//        private boolean isBlackError = false;
//
//        public boolean isBlackError() {
//            return isBlackError;
//        }
//
//        public void setBlackError(boolean blackError) {
//            isBlackError = blackError;
//        }
//    }

    /**
     * 当可视区高度变化时执行
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVisibleHeightChanged(VisibleHeightEvent event) {
        int visibleHeight = event.getVisibleHeight();
        if (mCurHeight > visibleHeight) {
            resize(visibleHeight);
        } else if (mCurHeight < visibleHeight && mCurHeight != mOriginHeight) {
            resize(mOriginHeight);
        }
    }

    private void resize(int targetHeight) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            mCurHeight = targetHeight;
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.height = mCurHeight;
            window.setAttributes(params);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                sendMessage();
                break;
            case R.id.btn_back:
                if (mFrom == 0) {
                    Objects.requireNonNull(getActivity()).onBackPressed();
                } else {
                    dismiss();
                }
                break;
        }
    }


    //发送私信
    private void sendMessage() {
        if (!AppConfig.getInstance().isIMLogined()) {
            ToastUtil.show("聊天服务器未接入");
            return;
        }
        //判断是否操作频繁
        if ((System.currentTimeMillis() - mLastTime) < 1500 && mLastTime != 0) {
            ToastUtil.show(getString(R.string.option_too_much));
            return;
        }
        mLastTime = System.currentTimeMillis();
        mCurMsg = mEditText.getText().toString();
        if ("".equals(mCurMsg)) {
            ToastUtil.show(getString(R.string.content_empty));
            return;
        }
//        HttpUtil.checkBlack(mToUser.getId(), mCallback);
        Message message = mIM.sendMessage(mCurMsg, mToUser.getId());
        message.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.e("//", i + "gotResult: " + s);
                if (i == 0) {
                    mEditText.setText("");
                    insertItem(message);
                } else if (i == 803008) {
//                                    被对方拉入黑名单
                    mEditText.setText("");
                    insertItem(message);
//                    ToastUtil.show("您已在对方黑名单中");
                } else if (i == 803009) {
//                                    敏感字
                    ToastUtil.show("内容存在敏感文字,请检查后重新编辑!");
                }
            }
        });
    }

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
                    int t2u = JSON.parseObject(info[0]).getIntValue("t2u");
                    if (1 == t2u) {
                        ToastUtil.show(getString(R.string.you_are_blacked));
                    } else {


                    }
                }
            } else {
                ToastUtil.show(msg);
            }
        }

        @Override
        public void onStart() {
            if (mProgressBar.getVisibility() == View.GONE) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFinish() {
            if (mProgressBar.getVisibility() == View.VISIBLE) {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    };

    private void insertItem(Message message) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(message);
        int position = mList.size() - 1;
        if (mAdapter == null) {
            mAdapter = new EMChatRoomAdapter<Conversation, Message, JIM>(mContext, mList, mToUser, mUser, mIM);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyItemInserted(position);
        }
        mRecyclerView.smoothScrollToPosition(position);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(Message message) {
        if (mIM.getFrom(message).equals(mToUser.getId())) {
            insertItem(message);
        }
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.CHECK_BLACK);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFrom == 1) {
            if (mContext instanceof LiveActivity) {
                ((LiveActivity) mContext).removeLayoutListener();
                ((LiveActivity) mContext).setChatRoomFragmentShowed(false);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        String touid = mToUser.getId();
        Conversation conversation = mIM.getConversation(touid);
        if (conversation != null) {
            mIM.markAllMessagesAsRead(conversation);
        }
        if (mFromPop) {
//            if (mContext instanceof LiveActivity) {
//                ((LiveActivity) mContext).showUnReadCount();
//            }
        } else {
            onBack(touid);
        }
    }

    private void onBack(String touid) {
        if (mList != null && mList.size() > 0) {
            Message message = mList.get(mList.size() - 1);
            String lastMsg = mIM.getContent(message);
            String lastTime = DateUtil.getDateString(mIM.getMessageTime(message));
            EMChatExitEvent e = new EMChatExitEvent(lastMsg, lastTime, touid, mIsAttention);
            e.setUserBean(mToUser);
            EventBus.getDefault().post(e);
        }
    }

}
