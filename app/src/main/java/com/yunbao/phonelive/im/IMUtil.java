package com.yunbao.phonelive.im;

/**
 * Created by cxf on 2017/12/14.
 */

public class IMUtil {
    public static final int EMIM = 1;//环信
    public static final int JIM = 2;//极光IM
    private static IMUtil sInstance;
    private int mType;

    private IMUtil() {

    }

    public static IMUtil getInstance() {
        if (sInstance == null) {
            synchronized (IMUtil.class) {
                if (sInstance == null) {
                    sInstance = new IMUtil();
                }
            }
        }
        return sInstance;
    }

    public void init(int type) {
        mType = type;
        switch (mType) {
            case EMIM:
                EMUtil.getInstance().init();
                break;
            case JIM:
                JIMUtil.getInstance().init();
                break;
        }
    }

    /**
     * 登录
     *
     * @param uid
     */
//    public void loginClient(String uid) {
//        switch (mType) {
//            case EMIM:
//                EMUtil.getInstance().loginEMClient(uid);
//                break;
//            case JIM:
//                JIMUtil.getInstance().loginEMClient(JIMUtil.PREFIX+uid);
//                break;
//        }
//    }

    /**
     * 登出
     */
    public void logoutClient() {
        switch (mType) {
            case EMIM:
                EMUtil.getInstance().logoutEMClient();
                break;
            case JIM:
                JIMUtil.getInstance().logoutEMClient();
                break;
        }
    }

    /**
     * 忽略未读消息
     */
    public void ignoreUnReadMessage() {
        switch (mType) {
            case EMIM:
                EMUtil.getInstance().ignoreUnReadMessage();
                break;
            case JIM:
                JIMUtil.getInstance().ignoreUnReadMessage();
                break;
        }
    }

}
