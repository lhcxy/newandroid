package com.yunbao.phonelive.ui.views.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.EMChatRoomActivity;
import com.yunbao.phonelive.adapter.EMChatListAdapter;
import com.yunbao.phonelive.bean.ChatUserBean;
import com.yunbao.phonelive.bean.MsgCenterListBean;
import com.yunbao.phonelive.custom.NoAlphaItemAnimator;
import com.yunbao.phonelive.event.EMChatExitEvent;
import com.yunbao.phonelive.glide.ItemDecoration;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.JIM;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.utils.DateUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

public class MsgCenterListFragment extends BaseLazyFragment {

    //    private MsgCenterRvAdapter adapter;
    private RecyclerView mRecyclerView;
    private TextView titleTv, contentTv, timeTv, unReadTv;
    private MsgCenterListBean msgCenterListBean;
    private JIM mIM;
    private Map<String, ChatUserBean> mMap;
    private List<ChatUserBean> list;

    public static MsgCenterListFragment newInstance() {
        MsgCenterListFragment fragment = new MsgCenterListFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_recyclerview;
    }

    @Override
    protected void initView() {

        titleTv = findView(R.id.item_title_tv);
        contentTv = findView(R.id.item_content_tv);
        timeTv = findView(R.id.item_time_tv);
        unReadTv = findView(R.id.item_unread_tv);
        findView(R.id.msg_list_sys).setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(null, -1);
                msgCenterListBean.setSnum(0);
                onBindData();
            }
        });
        EventBus.getDefault().register(this);
        initImList();
        bindDatas();
    }


    @Override
    protected void initData() {
        HttpUtil.getMsgList(callback);
    }

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null && info.length > 0) {
                msgCenterListBean = JSON.parseObject(info[0], MsgCenterListBean.class);
                onBindData();
            } else ToastUtil.show(msg);
        }
    };
    private OnItemClickListener listener;

    public void setItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void onBindData() {
        if (msgCenterListBean != null) {
            if (!TextUtils.isEmpty(msgCenterListBean.getStime()) && !msgCenterListBean.getStime().equals("0")) {
                timeTv.setText(DateUtil.getYMDHMByTimeStamp(msgCenterListBean.getStime()));
            }
            contentTv.setText(msgCenterListBean.getScont());
            titleTv.setText("系统消息");
            if (msgCenterListBean.getSnum() <= 0) {
                unReadTv.setVisibility(View.INVISIBLE);
            } else {
                unReadTv.setVisibility(View.VISIBLE);
                unReadTv.setText(String.valueOf(msgCenterListBean.getSnum()));
            }
        }
    }


    private void initImList() {
        mIM = new JIM();
        mRecyclerView = findView(R.id.root_rv);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new ItemDecoration(getContext(), 0x00EBEBF2, 0, 1));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());
        mMap = new HashMap<>();
        list = new ArrayList<>();
        initAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private EMChatListAdapter mAdapter;

    //初始化adapter
    private void initAdapter() {
        mAdapter = new EMChatListAdapter(getContext(), list);
        mAdapter.setOnItemClickListener(new OnItemClickListener<ChatUserBean>() {
            @Override
            public void onItemClick(ChatUserBean item, int position) {
                openChatRoom(item);
            }
        });
        mAdapter.setOnItemRemoveListener(new OnItemClickListener<ChatUserBean>() {
            @Override
            public void onItemClick(ChatUserBean bean, int position) {
                mIM.deleteConversation(bean.getId());
                mMap.remove(bean.getId());
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }


    private void openChatRoom(ChatUserBean chatUserBean) {
        Intent intent = new Intent(getContext(), EMChatRoomActivity.class);
        intent.putExtra("from", 0);
        intent.putExtra("touser", chatUserBean);
        intent.putExtra("isAttention", 0);
        startActivity(intent);
    }

    //接收新消息事件,显示红点
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(Message message) {
        mNewMessage = message;
//        if (mFragment.onNewMessage(message)) {
//            refreshRedPoint(true, false);
//            return;
//        }
//        if (mNoFragment.onNewMessage(message)) {
//            refreshRedPoint(false, true);
//            return;
//        }
        String from = mIM.getFrom(message);
//        if (from.startsWith("web1008")) {
//            from = from.replace("web1008", "");
//        }
        HttpUtil.getPmUserInfo(from, mPCallback);
    }

    private Message mNewMessage;
    private HttpCallback mPCallback = new HttpCallback() {
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
//            //我有没有关注对方
//            int isAttention = obj.getIntValue("isattention");
//            if (isAttention == 1) {
//                mFragment.insertItem(bean);
//            } else {
//                mNoFragment.insertItem(bean);
//            }
            insertItem(bean);
        }
    };

    //如果在Map里能找到这个人，即成功更新了这条消息，则返回true,
    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onNewMessage(Message message) {
        String from = mIM.getFrom(message);

        ChatUserBean bean = mMap.get(from);
        if (bean != null) {
            bean.setLastMessage(mIM.getContent(message));
            bean.setLastTime(DateUtil.getDateString(mIM.getMessageTime(message)));
            bean.setUnReadCount(bean.getUnReadCount() + 1);
            if (mAdapter != null) {
                mAdapter.updateItem(bean);
            }
            return true;
        }
        return false;
    }


    //插入一条新的会话
    public void insertItem(ChatUserBean bean) {
        if (mMap.containsKey(bean.getId())) {
            if (mAdapter != null) {
                mAdapter.updateItem(bean);
            }
        } else {
            mMap.put(bean.getId(), bean);
            if (mAdapter == null) {
                list = new ArrayList<>();
                list.add(bean);
                initAdapter();
            } else {
                mAdapter.insertItem(bean);
            }
        }

    }

    //接收聊天结束返回事件，这里面要把红点去掉
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatExitEvent(EMChatExitEvent event) {
        onChatBack(event);
//        onChatBack(event.getTouid(), event.getLastMsg(), event.getLastTime());
    }


    //聊天结束返回调用
    public void onChatBack(String touid, String lastMessage, String lastTime) {
        ChatUserBean bean = mMap.get(touid);
        if (bean != null) {
            bean.setLastMessage(lastMessage);
            bean.setLastTime(lastTime);
            bean.setUnReadCount(0);
            if (mAdapter != null) {
                mAdapter.updateItem(bean);
            }
        }
    }

    public void onChatBack(EMChatExitEvent event) {

        ChatUserBean bean = mMap.get(event.getTouid());
        if (bean != null) {
            bean.setLastMessage(event.getLastMsg());
            bean.setLastTime(event.getLastTime());
            bean.setUnReadCount(0);
            if (mAdapter != null) {
                mAdapter.updateItem(bean);
            }
        } else {
            if (event.getUserBean() != null) {
                bean = JSON.parseObject(JSON.toJSONString(event.getUserBean()), ChatUserBean.class);
                bean.setLastMessage(event.getLastMsg());
                bean.setLastTime(event.getLastTime());
                bean.setUnReadCount(0);
                if (mAdapter != null) {
                    mAdapter.insertItem(bean);
                }
            }
        }
    }

    //删除item
    public ChatUserBean removeItem(String touid) {
        if (mMap.containsKey(touid)) {
            mMap.remove(touid);
            ChatUserBean bean = mAdapter.removeItem(touid);
            return bean;
        }
        return null;
    }

    private void bindDatas() {
        String uids = mIM.getUids();
        HttpUtil.getMultiInfo(uids, 2 + "", mCallback);
//        HttpUtil.getMultiInfo(uids, 1 + "", mCallback);
    }

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            List<ChatUserBean> list = JSON.parseArray(Arrays.toString(info), ChatUserBean.class);
            if (list.size() > 0) {
                for (ChatUserBean bean : list) {
                    Conversation conversation = mIM.getConversation(bean.getId());
                    if (conversation != null && mIM.getLatestMessage(conversation) != null) {
                        bean.setLastMessage(mIM.getLastMsg(conversation));
                        int count = mIM.getUnReadCount(conversation);
                        bean.setUnReadCount(count);
                        bean.setLastTime(DateUtil.getDateString(mIM.getMessageTime(mIM.getLatestMessage(conversation))));
                        mMap.put(bean.getId(), bean);
                    }
                }
                if (mAdapter == null) {
                    initAdapter();
                } else {
                    mAdapter.addList(list);
                }
            }
        }
    };


}
