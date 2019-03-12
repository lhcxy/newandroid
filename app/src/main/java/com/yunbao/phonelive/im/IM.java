package com.yunbao.phonelive.im;

import java.util.List;

/**
 * Created by cxf on 2017/12/13.
 *
 * @param <C> 会话对象的类型
 * @param <M> 消息对象的类型
 */

public interface IM<C, M> {

    /**
     * 获取所有的会话列表
     */
    List<C> getAllConversations();

    /**
     * 根据id获取单个会话
     *
     * @param id
     * @return
     */
    C getConversation(String id);

    /**
     * 获取会话对象中的消息列表
     *
     * @param conversation 会话对象
     * @return
     */
    List<M> getAllMessages(C conversation);

    /**
     * 发送消息
     *
     * @param content 消息的内容
     * @param id      对方的id
     */
    M sendMessage(String content, String id);

    /**
     * 获取某条消息中的内容
     *
     * @param message
     * @return
     */
    String getContent(M message);

    /**
     * 获取收到消息的时间
     *
     * @param message
     * @return
     */
    long getMessageTime(M message);

    /**
     * 获取消息的来源
     *
     * @return
     */
    String getFrom(M message);

    /**
     * 获取指定会话中的最后一条消息
     *
     * @param conversation
     * @return
     */
    String getLastMsg(C conversation);

    /**
     * 获取指定会话中最后一条消息对象
     *
     * @return
     */
    M getLatestMessage(C conversation);

    /**
     * 获取指定会话中未读消息的数量
     *
     * @param conversation
     * @return
     */
    int getUnReadCount(C conversation);

    /**
     * 获取所有未读消息的数量
     *
     * @return
     */
    int getAllUnReadCount();

    /**
     * 标记所有消息为已读
     *
     * @param conversation
     */
    void markAllMessagesAsRead(C conversation);

    /**
     * 获取某个会话的历史记录,起始位置为最后一条消息
     *
     * @param conversation 会话对象
     * @param limit        获取消息的条数
     */
    List<M> loadHistoryMessage(C conversation, int limit);


    /**
     * 根据本地的会话列表，获取到会话的人的id，逗号分隔
     *
     * @return
     */
    String getUids();

    /**
     * 删除会话对象
     *
     * @param id
     */
    void deleteConversation(String id);


    boolean isMsgSendError(M message);

}
