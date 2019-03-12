package com.yunbao.phonelive.im;

import android.text.TextUtils;
import android.util.Log;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.JIMLoginEvent;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.RegisterOptionalUserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by cxf on 2017/8/10.
 * 极光IM注册、登陆等功能
 */

public class JIMUtil {

    private static final String TAG = "极光IM";
    //    private static final String PWD_SUFFIX = "PUSH";//注册极光IM的时候，密码是用户id+"PUSH"这个常量构成的
    public static final String PWD_SUFFIX = "123456";//注册极光IM的时候，密码是用户id+"PUSH"这个常量构成的
    //前缀，当uid不够长的时候无法注册
    public static final String PREFIX = "web1008";
    private Map<String, Long> mMap;

    private static JIMUtil sInstance;

    private JIMUtil() {
        mMap = new HashMap<>();
    }

    public static JIMUtil getInstance() {
        if (sInstance == null) {
            synchronized (JIMUtil.class) {
                if (sInstance == null) {
                    sInstance = new JIMUtil();
                }
            }
        }
        return sInstance;
    }


    public void init() {
//        JMessageClient.setDebugMode(true);
//        JMessageClient.init(AppContext.sInstance, true);
        if (!AppConfig.isUnlogin()) {
            loginEMClient(AppConfig.getInstance().getUid());
        }
    }

    /**
     * 登出极光IM
     */
    public void logoutEMClient() {
        JMessageClient.logout();
        SharedPreferencesUtil.getInstance().saveEMLoginStatus(false);
        AppConfig.getInstance().setIMLogined(false);
//        L.e(TAG, "极光IM登出");
    }

    /**
     * 登录极光IM
     */
    public void loginEMClient(final String uid) {
        if (SharedPreferencesUtil.getInstance().readEMLoginStatus()) {
//            L.e(TAG, "极光IM已经登录了");
            JMessageClient.registerEventReceiver(JIMUtil.this);
            AppConfig.getInstance().setIMLogined(true);
            return;
        }
        JMessageClient.login(PREFIX + uid, PWD_SUFFIX, new BasicCallback() {

            @Override
            public void gotResult(int code, String msg) {
                L.e(TAG, "登录极光回调---gotResult--->code: " + code + " msg: " + msg);
                if (code == 801003) {//用户不存在
//                    L.e(TAG, "未注册，用户不存在");
                    registerAndLoginEMClient(uid);
                } else if (code == 0) {
                    L.e(TAG, "极光IM登录成功");
                    SharedPreferencesUtil.getInstance().saveEMLoginStatus(true);
                    AppConfig.getInstance().getIgnoreMessage();
                    EventBus.getDefault().post(new JIMLoginEvent());
                    JMessageClient.registerEventReceiver(JIMUtil.this);
                    AppConfig.getInstance().setIMLogined(true);
                }
            }
        });
    }

    //注册并登录极光IM
    private void registerAndLoginEMClient(final String uid) {
        RegisterOptionalUserInfo optionalUserInfo = new RegisterOptionalUserInfo();
        UserBean userBean = AppConfig.getInstance().getUserBean();
        if (userBean != null) {
            if (!TextUtils.isEmpty(userBean.getAvatar()))
                optionalUserInfo.setAvatar(userBean.getAvatar());
            optionalUserInfo.setGender(userBean.getSex() == 0 ? UserInfo.Gender.unknown : userBean.getSex() == 1 ? UserInfo.Gender.male : UserInfo.Gender.female);
            optionalUserInfo.setNickname(userBean.getUser_nicename());
            if (!TextUtils.isEmpty(userBean.getSignature()))
                optionalUserInfo.setSignature(userBean.getSignature());
        }
        JMessageClient.register(PREFIX + uid, PWD_SUFFIX, optionalUserInfo, new BasicCallback() {
            @Override
            public void gotResult(int code, String msg) {
                L.e(TAG, "注册极光回调---gotResult--->code: " + code + " msg: " + msg);
                if (code == 0) {
                    L.e(TAG, "极光IM注册成功");
                    loginEMClient(uid);
                }
            }
        });
    }

    /**
     * 接收消息 目前是在子线程接收的
     *
     * @param event
     */
    public void onEvent(MessageEvent event) {
        //收到消息
        boolean canShow = true;
        Message msg = event.getMessage();
        String from = msg.getFromUser().getUserName().substring(JIMUtil.PREFIX.length());
        //L.e(TAG, "onEvent--->来自：" + from + "---内容--> " + ((TextContent) msg.getContent()).getText());
        Object lastTime = mMap.get(from);
        if (lastTime != null) {
            if (System.currentTimeMillis() - (long) lastTime < 1500) {
                //同一个人，上条消息距离这条消息间隔不到1秒，则不显示这条消息
                canShow = false;
            } else {
                mMap.put(from, System.currentTimeMillis());
            }
        } else {
            //说明sMap内没有保存这个人的信息，则是首次收到这人的信息，可以显示
            mMap.put(from, System.currentTimeMillis());
        }
        if (canShow) {
//            L.e(TAG, "显示消息--->");
            AppConfig.getInstance().setIgnoreMessage(false);
            EventBus.getDefault().post(msg);
        }
    }

//    /**
//     * 接收离线消息 目前是在子线程接收的
//     *
//     * @param event
//     */
//    public void onEvent(OfflineMessageEvent event) {
//        List<Message> list = event.getOfflineMessageList();
//        for (Message msg : list) {
//            if (!msg.getFromUser().getUserName().equals(AppConfig.getInstance().getUid())) {
//                L.e(TAG, "显示离线消息--->");
//                AppConfig.getInstance().setIgnoreMessage(false);
//                EventBus.getDefault().post(msg);
//            }
//        }
//    }

    /**
     * 忽略未读消息
     */
    public void ignoreUnReadMessage() {
        List<Conversation> list = JMessageClient.getConversationList();
        for (Conversation conversation : list) {
            conversation.resetUnreadCount();
        }
    }

    public boolean hasUnreade() {
        return JMessageClient.getAllUnReadMsgCount() > 0;
    }

    public void updateAvatar(File file) {
        JMessageClient.updateUserAvatar(file, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.e(TAG, "gotResult: code=" + i + ";smg=" + s);
            }
        });
    }

    public void addBlackList(String uId) {
        ArrayList<String> blackList = new ArrayList<>();
        blackList.add(JIMUtil.PREFIX + uId);
        JMessageClient.addUsersToBlacklist(blackList, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
//                ToastUtil.show("屏蔽用户私信成功!");
            }
        });
    }

    public void removeBlackList(String uId) {
        ArrayList<String> blackList = new ArrayList<>();
        blackList.add(JIMUtil.PREFIX + uId);
        JMessageClient.addUsersToBlacklist(blackList, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
            }
        });
    }

    public void updateNickName(String name) {

        UserInfo myInfo = JMessageClient.getMyInfo();
        myInfo.setNickname(name);
        JMessageClient.updateMyInfo(UserInfo.Field.nickname, myInfo, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.e(TAG, "gotResult: code=" + i + ";smg=" + s);
            }
        });
    }


}
