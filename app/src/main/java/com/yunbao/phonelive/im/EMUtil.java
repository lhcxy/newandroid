package com.yunbao.phonelive.im;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.receiver.ConnectivityReceiver;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2017/8/10.
 * 环信注册、登陆等功能
 */

public class EMUtil {

    private static final String PWD_PREFIX = "fmscms";//注册环信的时候，密码是"fmscms"这个常量+用户id构成的

    private Map<String, Long> mMap;

    private static EMUtil sInstance;

    private EMUtil() {
        mMap = new HashMap<>();
    }

    public static EMUtil getInstance() {
        if (sInstance == null) {
            synchronized (JIMUtil.class) {
                if (sInstance == null) {
                    sInstance = new EMUtil();
                }
            }
        }
        return sInstance;
    }


    public void init() {
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        EMClient client = EMClient.getInstance();
        client.init(AppContext.sInstance, options);
        client.setDebugMode(false);
        client.addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
//                L.e("环信", "环信已连接");
            }

            @Override
            public void onDisconnected(int errorCode) {
                if (errorCode == EMError.USER_REMOVED) {
                    // 帐号已经被移除
//                    L.e("环信", "帐号已经被移除");
                } else if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    //帐号在其他设备登陆
//                    L.e("环信", "帐号在其他设备登陆");
                    logoutEMClient();

                } else {
                    if (ConnectivityReceiver.isNetworkAvailable()) {
//                        L.e("环信", "无法连接到聊天服务器");
                    } else {
//                        L.e("环信", "当前网络不可用");
                    }
                }
            }
        });

        client.chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                boolean canShow = true;
                EMMessage msg = messages.get(0);
                String from = msg.getFrom();
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
//                    L.e("环信", "收到消息--->");
                    AppConfig.getInstance().setIgnoreMessage(false);
                    EventBus.getDefault().post(msg);
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
//                L.e("环信", "收到透传消息--->");
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
//                L.e("环信", "收到已读回执--->");
            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {
//                L.e("环信", "收到已送达回执--->");
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
//                L.e("环信", "消息状态变动--->");
            }
        });

    }

    /**
     * 登出环信
     */
    public void logoutEMClient() {
        EMClient.getInstance().logout(true);
        SharedPreferencesUtil.getInstance().saveEMLoginStatus(false);
    }

    /**
     * 登陆环信
     */
    public void loginEMClient(final String uid) {
        if (SharedPreferencesUtil.getInstance().readEMLoginStatus()) {
            return;
        }
        EMClient.getInstance().login(uid, PWD_PREFIX + uid, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
//                L.e("环信", "环信登录成功");
                SharedPreferencesUtil.getInstance().saveEMLoginStatus(true);
                AppConfig.getInstance().getIgnoreMessage();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
//                L.e("环信", "错误code---->" + code + " ：" + message);
                if (204 == code) {
//                    L.e("环信", "未注册，用户不存在");
                    registerAndLoginEMClient(uid);
                }
            }
        });

    }

    //注册并登陆环信
    private void registerAndLoginEMClient(String uid) {

        try {
            EMClient.getInstance().createAccount(uid, PWD_PREFIX + uid);
//            L.e("环信注册成功");
            loginEMClient(uid);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 忽略未读消息
     */
    public void ignoreUnReadMessage() {
        EMClient.getInstance().chatManager().markAllConversationsAsRead();
    }


}
