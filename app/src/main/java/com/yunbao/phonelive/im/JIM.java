package com.yunbao.phonelive.im;

import com.yunbao.phonelive.utils.L;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;

import static com.yunbao.phonelive.im.JIMUtil.PREFIX;

/**
 * Created by cxf on 2017/12/13.
 * IM接口的极光IM实现类
 */

public class JIM implements IM<Conversation, Message> {

    //针对消息发送动作的控制选项
    private MessageSendingOptions mOptions;

    public JIM() {
        mOptions = new MessageSendingOptions();
        mOptions.setShowNotification(false);//设置针对本次消息发送，是否需要在消息接收方的通知栏上展示通知

    }

    @Override
    public List<Conversation> getAllConversations() {
        return JMessageClient.getConversationList();
    }

    @Override
    public Conversation getConversation(String id) {
        return JMessageClient.getSingleConversation(PREFIX + id);
    }

    @Override
    public List<Message> getAllMessages(Conversation conversation) {
        return conversation.getAllMessage();
    }

    @Override
    public Message sendMessage(String content, String id) {
        Message message = JMessageClient.createSingleTextMessage(PREFIX + id, content);
        JMessageClient.sendMessage(message, mOptions);
        return message;
    }

    @Override
    public String getContent(Message message) {
        if (message == null) {
            return "";
        }
        return ((TextContent) message.getContent()).getText();
    }

    @Override
    public long getMessageTime(Message message) {
        return message.getCreateTime();
    }

    @Override
    public String getFrom(Message message) {
        return message.getFromUser().getUserName().substring(JIMUtil.PREFIX.length());
//        return message.getFromUser().getUserName();
    }

    @Override
    public String getLastMsg(Conversation conversation) {
        return getContent(getLatestMessage(conversation));
    }

    @Override
    public Message getLatestMessage(Conversation conversation) {
        if (conversation == null) {
            return null;
        }
        return conversation.getLatestMessage();
    }

    @Override
    public int getUnReadCount(Conversation conversation) {
        return conversation.getUnReadMsgCnt();
    }

    @Override
    public int getAllUnReadCount() {
        int count = 0;
        List<Conversation> list = getAllConversations();
        if (list != null) {
            for (Conversation conversation : list) {
                count += conversation.getUnReadMsgCnt();
            }
        }
        return count;
    }

    @Override
    public void markAllMessagesAsRead(Conversation conversation) {
        conversation.resetUnreadCount();
    }

    @Override
    public List<Message> loadHistoryMessage(Conversation conversation, int limit) {
        return conversation.getMessagesFromNewest(conversation.getLatestMessage().getId(), limit);
    }

    @Override
    public String getUids() {
        List<Conversation> conversations = getAllConversations();
        String uids = "";
        if (conversations != null) {
            for (int i = 0; i < conversations.size(); i++) {
                String userName = ((UserInfo) (conversations.get(i).getTargetInfo())).getUserName();
                if (userName.startsWith(PREFIX) && PREFIX.length() < userName.length())
                    userName = userName.substring(PREFIX.length());
                uids += userName;
                if (i < conversations.size() - 1) {
                    uids += ",";
                }
            }
        }
        return uids;
    }

    @Override
    public void deleteConversation(String id) {
        JMessageClient.deleteSingleConversation(PREFIX + id);
        JMessageClient.deleteSingleConversation("ayx_admin");
    }

    @Override
    public boolean isMsgSendError(Message message) {
        return message.getStatus() == MessageStatus.send_fail;
    }


}
