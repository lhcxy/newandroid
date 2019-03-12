package com.yunbao.phonelive.im;

import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2017/12/13.
 * IM接口的环信实现类
 */

public class EMIM implements IM<EMConversation, EMMessage> {

    private EMChatManager mChatManager;

    public EMIM() {
        mChatManager = EMClient.getInstance().chatManager();
    }

    @Override
    public List<EMConversation> getAllConversations() {
        return null;
    }

    @Override
    public EMConversation getConversation(String id) {
        return mChatManager.getConversation(id);
    }

    @Override
    public List<EMMessage> getAllMessages(EMConversation conversation) {
        return conversation.getAllMessages();
    }

    @Override
    public EMMessage sendMessage(String content, String id) {
        EMMessage message = EMMessage.createTxtSendMessage(content, id);
        mChatManager.sendMessage(message);
        return message;
    }

    @Override
    public String getContent(EMMessage message) {
        return ((EMTextMessageBody) message.getBody()).getMessage();
    }

    @Override
    public long getMessageTime(EMMessage message) {
        return message.getMsgTime();
    }

    @Override
    public String getFrom(EMMessage message) {
        return message.getFrom();
    }

    @Override
    public String getLastMsg(EMConversation conversation) {
        return getContent(conversation.getLastMessage());
    }

    @Override
    public EMMessage getLatestMessage(EMConversation conversation) {
        return conversation.getLastMessage();
    }

    @Override
    public int getUnReadCount(EMConversation conversation) {
        return conversation.getUnreadMsgCount();
    }

    @Override
    public int getAllUnReadCount() {
        return mChatManager.getUnreadMessageCount();
    }

    @Override
    public void markAllMessagesAsRead(EMConversation conversation) {
        conversation.markAllMessagesAsRead();
    }

    @Override
    public List<EMMessage> loadHistoryMessage(EMConversation conversation, int limit) {
        return conversation.loadMoreMsgFromDB(conversation.getAllMessages().get(0).getMsgId(), limit);
    }

    @Override
    public String getUids() {
        Map<String, EMConversation> map = mChatManager.getAllConversations();
        String uids = "";
        for (String key : map.keySet()) {
            uids += key + ",";
        }
        if (uids.length() > 0) {
            uids = uids.substring(0, uids.length() - 1);
        }
        return uids;
    }

    @Override
    public void deleteConversation(String id) {
        mChatManager.deleteConversation(id, true);
    }

    @Override
    public boolean isMsgSendError(EMMessage message) {
        return false;
    }


}
