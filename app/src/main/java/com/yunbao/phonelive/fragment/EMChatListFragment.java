package com.yunbao.phonelive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.EMChatRoomActivity;
import com.yunbao.phonelive.adapter.EMChatListAdapter;
import com.yunbao.phonelive.bean.ChatUserBean;
import com.yunbao.phonelive.custom.NoAlphaItemAnimator;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.JIM;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

/**
 * Created by cxf on 2017/8/10.
 */

public class EMChatListFragment extends AbsFragment {

    private RecyclerView mRecyclerView;
    private EMChatListAdapter mAdapter;
    private int mFrom;//0 是直播间外打开聊天页面 1是直播间内打开聊天页面
    private int mIsAttention;//1  已关注 0 未关注
    private String mUids;
    private Map<String, ChatUserBean> mMap;
    private int mUnReadCount;//所有的未读消息数量
    private CommonCallback<Integer> mUnReadCallback;
    private JIM mIM;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_list;
    }

    @Override
    protected void main() {
        initView();
        initData();
    }

    public void setIM(JIM IM) {
        mIM = IM;
    }

    private void initView() {
        mRecyclerView = (RecyclerView) mRootView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());
        mMap = new HashMap<>();
    }

    private void initData() {
        Bundle bundle = getArguments();
        mFrom = bundle.getInt("from");
        mIsAttention = bundle.getInt("isAttention");
        mUids = bundle.getString("uids");
        if (!"".equals(mUids)) {
            HttpUtil.getMultiInfo(mUids, mIsAttention + "", mCallback);
        }
    }

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            List<ChatUserBean> list = JSON.parseArray(Arrays.toString(info), ChatUserBean.class);
            if (list.size() > 0) {
                for (ChatUserBean bean : list) {
                    Conversation conversation = mIM.getConversation(bean.getId());
                    if (conversation != null) {
                        bean.setLastMessage(mIM.getLastMsg(conversation));
                        int count = mIM.getUnReadCount(conversation);
                        bean.setUnReadCount(count);
                        mUnReadCount += count;
                        bean.setLastTime(DateUtil.getDateString(mIM.getMessageTime(mIM.getLatestMessage(conversation))));
                        mMap.put(bean.getId(), bean);
                    }
                }
                if (mAdapter == null) {
                    initAdapter(list);
                } else {
                    mAdapter.setList(list);
                }
            } else {
                mUnReadCount = 0;
                if (mAdapter != null) {
                    mAdapter.clear();
                }
            }
            if (mUnReadCallback != null) {
                mUnReadCallback.callback(mUnReadCount);
            }
        }
    };

    //初始化adapter
    private void initAdapter(List<ChatUserBean> list) {
        mAdapter = new EMChatListAdapter(mContext, list);
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
                mUnReadCount -= bean.getUnReadCount();
                if (mUnReadCallback != null) {
                    mUnReadCallback.callback(mUnReadCount);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setUnReadCallback(CommonCallback<Integer> callback) {
        mUnReadCallback = callback;
    }

    private void openChatRoom(ChatUserBean chatUserBean) {
//        if (mFrom == 0) {
            Intent intent = new Intent(mContext, EMChatRoomActivity.class);
            intent.putExtra("from", 0);
            intent.putExtra("touser", chatUserBean);
            intent.putExtra("isAttention", mIsAttention);
            startActivity(intent);
//        } else if (mFrom == 1) {
//        EMChatRoomFragment fragment = new EMChatRoomFragment();
//        fragment.setIM(mIM);
//        Bundle bundle = new Bundle();
//        bundle.putInt("from", 1);
//        bundle.putParcelable("touser", chatUserBean);
//        bundle.putInt("isAttention", mIsAttention);
//        fragment.setArguments(bundle);
//        fragment.show(getActivity().getSupportFragmentManager(), "EMChatRoomFragment");
//        }
    }

    //如果在Map里能找到这个人，即成功更新了这条消息，则返回true,
    public boolean onNewMessage(Message message) {
        String from = mIM.getFrom(message);
//        if (!TextUtils.isEmpty(from) && from.startsWith("web1008")) {
//            from = from.replace("web1008", "");
//        }
        ChatUserBean bean = mMap.get(from);
        if (bean != null) {
            mUnReadCount++;
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

    public int getUnReadCount() {
        return mUnReadCount;
    }

    //插入一条新的会话
    public void insertItem(ChatUserBean bean) {
        mMap.put(bean.getId(), bean);
        mUnReadCount += bean.getUnReadCount();
        if (mAdapter == null) {
            List<ChatUserBean> list = new ArrayList<>();
            list.add(bean);
            initAdapter(list);
        } else {
            mAdapter.insertItem(bean);
        }
        if (mUnReadCallback != null) {
            mUnReadCallback.callback(mUnReadCount);
        }
    }

    //聊天结束返回调用
    public void onChatBack(String touid, String lastMessage, String lastTime) {
        ChatUserBean bean = mMap.get(touid);
        if (bean != null) {
            mUnReadCount -= bean.getUnReadCount();
            bean.setLastMessage(lastMessage);
            bean.setLastTime(lastTime);
            bean.setUnReadCount(0);
            if (mAdapter != null) {
                mAdapter.updateItem(bean);
            }
        }
    }

    //删除item
    public ChatUserBean removeItem(String touid) {
        if (mMap.containsKey(touid)) {
            mMap.remove(touid);
            ChatUserBean bean = mAdapter.removeItem(touid);
            mUnReadCount -= bean.getUnReadCount();
            return bean;
        }
        return null;
    }

    /**
     * 忽略未读消息
     */
    public void ignoreUnReadMessage() {
        mUnReadCount = 0;
        if (mUnReadCallback != null) {
            mUnReadCallback.callback(mUnReadCount);
        }
        for (Map.Entry<String, ChatUserBean> entry : mMap.entrySet()) {
            entry.getValue().setUnReadCount(0);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_MULTI_INFO);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
