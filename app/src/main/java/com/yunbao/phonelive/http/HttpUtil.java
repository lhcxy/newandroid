package com.yunbao.phonelive.http;

import android.app.Dialog;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.request.PostRequest;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.AttentionEvent;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;


/**
 * Created by cxf on 2017/8/4.
 */

public class HttpUtil {

    private static final String HTTP_URL = AppConfig.HOST + AppConfig.URI;
    private static OkHttpClient sOkHttpClient;

    public static void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //okGo默认的超时时间是60秒
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.retryOnConnectionFailure(true);

        //输出HTTP请求 响应信息
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("http");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BASIC);
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);

        sOkHttpClient = builder.build();

        OkGo.getInstance().init(AppContext.sInstance)
                .setOkHttpClient(sOkHttpClient)
                .setCacheMode(CacheMode.NO_CACHE)
                .setRetryCount(3);
    }

    public static void cancel(String tag) {
//        OkGo.cancelTag(sOkHttpClient, tag);
    }

    //刚好80个接口。。。
    public static final String IF_TOKEN = "ifToken";
    public static final String GET_CONFIG = "getConfig";
    public static final String GET_VALIDATE_CODE = "getValidateCode";
    public static final String GET_VALIDATE_CODE2 = "getValidateCode2";
    public static final String FIND_PWD = "findPwd";
    public static final String REGISTER = "register";
    public static final String LOGIN = "login";
    public static final String LOGIN_BY_THIRD = "loginByThird";
    public static final String GET_QQLOGIN_OPENID = "getQQLoginOpenid";
    public static final String GET_HOT = "getHot";
    public static final String GET_NEW = "getNew";
    public static final String GET_NEAR = "getNear";
    public static final String GET_FOLLOW = "getFollow";
    public static final String SET_ATTENTION = "setAttention";
    public static final String SEARCH = "search";
    public static final String GET_USER_HOME = "getUserHome";
    public static final String GET_FOLLOWS_LIST = "getFollowsList";
    public static final String GET_FANS_LIST = "getFansList";
    public static final String GET_ALI_CDN_RECORD = "getAliCdnRecord";
    public static final String SET_BLACK = "setBlack";
    public static final String CHECK_BLACK = "checkBlack";
    public static final String GET_MULTI_INFO = "getMultiInfo";
    public static final String GET_PM_USER_INFO = "getPmUserInfo";
    public static final String GET_BASE_INFO = "getBaseInfo";
    public static final String GET_LIVERECORD = "getLiverecord";
    public static final String UPDATE_AVATAR = "updateAvatar";
    public static final String UPDATE_FIELDS = "updateFields";
    public static final String UPDATE_PASS = "updatePass";
    public static final String GET_GIFT_LIST = "getGiftList";
    public static final String GET_GIFT_PACKAGE = "getGiftPackage";
    public static final String SEND_GIFT = "sendGift";
    public static final String CHECK_LIVE = "checkLive";
    public static final String ENTER_ROOM = "enterRoom";
    public static final String SEND_BARRAGE = "sendBarrage";
    public static final String GET_POP = "getPop";
    public static final String GET_ADMIN_LIST = "getAdminList";
    public static final String SET_ADMIN = "setAdmin";
    public static final String KICKING = "kicking";
    public static final String SET_SHUT_UP = "setShutUp";
    public static final String SUPER_STOP_ROOM = "superStopRoom";
    public static final String SET_REPORT = "setReport";
    public static final String CREATE_ROOM = "createRoom";
    public static final String CHANGE_LIVE = "changeLive";
    public static final String STOP_ROOM = "stopRoom";
    public static final String STOP_LIVE_INFO = "stopLiveInfo";
    public static final String SEARCH_MUSIC = "searchMusic";
    public static final String GET_DOWN_MUSIC_URL = "getDownMusicUrl";
    public static final String CHANGE_LIVE_TYPE = "changeLiveType";
    public static final String ROOM_CHARGE = "roomCharge";
    public static final String TIME_CHARGE = "timeCharge";
    public static final String GET_BALANCE = "getBalance";
    public static final String GET_ALI_ORDER = "getAliOrder";
    public static final String GET_WX_ORDER = "getWxOrder";
    public static final String GET_PROFIT = "getProfit";
    public static final String GET_CASH = "getCash";
    public static final String GET_BONUS = "getBonus";
    public static final String SET_AUCTION = "setAuction";
    public static final String AUCTION_END = "auctionEnd";
    public static final String SET_BID_PRICE = "setBidPrice";
    public static final String GET_COIN = "getCoin";
    public static final String GET_USER_LIST = "getUserList";
    public static final String SET_DISTRIBUT = "setDistribut";
    public static final String GET_RECOMMEND = "getRecommend";
    public static final String ATTENT_RECOMMEND = "attentRecommend";
    public static final String PROFIT_LIST = "profitList";
    public static final String CONSUME_LIST = "consumeList";
    public static final String GAME_JINHUA_CREATE = "gameJinhuaCreate";
    public static final String GAME_JINHUA_BET = "gameJinhuaBet";
    public static final String GAME_SETTLE = "gameSettle";
    public static final String GAME_HAIDAO_CREATE = "gameHaidaoCreate";
    public static final String GAME_HAIDAO_BET = "gameHaidaoBet";
    public static final String GAME_NIUZAI_CREATE = "gameNiuzaiCreate";
    public static final String GAME_NIUZAI_BET = "gameNiuzaiBet";
    public static final String GAME_NIU_GET_BANKER = "gameNiuGetBanker";
    public static final String GAME_NIU_SET_BANKER = "gameNiuSetBanker";
    public static final String GAME_NIU_QUIT_BANKER = "gameNiuQuitBanker";
    public static final String GAME_NIU_BANKER_WATER = "gameNiuBankerWater";
    public static final String GAME_NIU_RECORD = "gameNiuRecord";
    public static final String GAME_EBB_CREATE = "gameEbbCreate";
    public static final String GAME_EBB_BET = "gameEbbBet";
    public static final String GAME_LUCK_PAN_CREATE = "gameLuckPanCreate";
    public static final String GAME_LUCK_PAN_BET = "gameLuckPanBet";
    public static final String GET_BANNER_LIST = "getbannerlist";
    public static final String GET_RANK_BANNER_LIST = "getrankbannerlist";
    public static final String GET_LIVE_TAG = "get_live_tag";
    public static final String GET_LIVE_BY_TAG = "getLiveByTag";
    public static final String GET_PLEASURE_LIVE = "Home.PleasureLive"; //娱乐主播
    public static final String GET_FREE_GIFT = "Live.AddStar";
    public static final String SEND_FREE_GIFT = "Live.SendStar";
    public static final String GET_USER_ISATTENT = "get_user_isattention";
    public static final String GET_CHARGE_HISTORY = "get_charge_history";
    public static final String GET_DAILY_TASK = "get_daily_task";
    public static final String EXCUTE_DAILY_TASK = "excute_daily_task";

    /**
     * 验证token是否过期
     *
     * @param uid
     * @param token
     * @param callback
     */
    public static void ifToken(String uid, String token, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.iftoken")
                .params("uid", uid)
                .params("token", token)
                .tag(IF_TOKEN)
                .execute(callback);
    }

    /**
     * 获取config
     */
    public static void getConfig(final CommonCallback<ConfigBean> commonCallback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.getConfig")
                .tag(GET_CONFIG)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info != null && info.length > 0) {
                            ConfigBean bean = JSON.parseObject(info[0], ConfigBean.class);
                            if (bean != null) {
                                AppConfig.getInstance().setConfig(bean);
                                AppConfig.getInstance().setSocketServer(bean.getIm_server());
                                SharedPreferencesUtil.getInstance().saveConfig(info[0]);
                                if (commonCallback != null) {
                                    commonCallback.callback(bean);
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 获取验证码接口 注册用
     */
    public static void getValidateCode(String mobile, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Login.getCode")
                .params("mobile", mobile)
                .tag(GET_VALIDATE_CODE)
                .execute(callback);
    }

    /**
     * 获取验证码接口 找回密码用
     */
    public static void getValidateCode2(String mobile, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Login.getForgetCode")
                .params("mobile", mobile)
                .tag(GET_VALIDATE_CODE2)
                .execute(callback);
    }

    /**
     * 找回密码接口
     */
    public static void findPwd(String user_login, String pass, String pass2, String code, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Login.userFindPass")
                .params("user_login", user_login)
                .params("user_pass", pass)
                .params("user_pass2", pass2)
                .params("code", code)
                .tag(FIND_PWD)
                .execute(callback);
    }


    /**
     * 手机注册接口
     */
    public static void register(String user_login, String pass, String pass2, String code, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Login.userReg")
                .params("user_login", user_login)
                .params("user_pass", pass)
                .params("user_pass2", pass2)
                .params("code", code)
                .tag(REGISTER)
                .execute(callback);
    }


    /**
     * 手机号 密码登录
     *
     * @param phoneNum
     * @param pwd
     */
    public static void login(String phoneNum, String pwd, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Login.userLogin")
                .params("user_login", phoneNum)
                .params("user_pass", pwd)
                .tag(LOGIN)
                .execute(callback);
    }

    /**
     * 第三方登录
     */
    public static void loginByThird(String openid, String nicename, String type, String avatar, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Login.userLoginByThird")
                .params("openid", openid)
                .params("nicename", nicename)
                .params("type", type)
                .params("avatar", avatar)
                .tag(LOGIN_BY_THIRD)
                .execute(callback);
    }

    /**
     * QQ登录的时候 获取openid
     */
    public static void getQQLoginOpenid(String access_token, StringCallback callback) {
        OkGo.<String>get("https://graph.qq.com/oauth2.0/me?access_token=" + access_token)
                .params("unionid", 1 + "")
                .tag(GET_QQLOGIN_OPENID)
                .execute(callback);
    }

    /**
     * 首页 热门标签
     */
    public static void getHot(HttpCallback callback) {
//        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.getHot")
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.GetHot")
                .tag(GET_HOT)
                .execute(callback);
    }

    /**
     * 首页 最新标签
     */
    public static void getNew(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.getNew")
                .tag(GET_NEW)
                .execute(callback);
    }

    /**
     * 首页 附近标签
     *
     * @param lng 经度
     * @param lat 纬度
     */
    public static void getNear(String lng, String lat, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.getNearby")
                .params("lng", lng)
                .params("lat", lat)
                .tag(GET_NEAR)
                .execute(callback);
    }

    /**
     * 首页 关注标签
     */
    public static void getFollow(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.getFollow")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag(GET_FOLLOW)
                .execute(callback);
    }


    /**
     * 关注别人 或 取消对别人的关注的接口
     */
    public static void setAttention(final String touid, final CommonCallback<Integer> callback) {
        if (AppConfig.isUnlogin()) {
            ToastUtil.show(WordUtil.getString(R.string.user_unlogin_hint_txt));
            return;
        }
        if (AppConfig.getInstance().getUid().equals(touid)) {
            ToastUtil.show(WordUtil.getString(R.string.cannot_follow_self));
            return;
        }
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.setAttent")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .tag(SET_ATTENTION)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        int isAttention = JSON.parseObject(info[0]).getIntValue("isattent");//1是 关注  0是未关注
                        EventBus.getDefault().post(new AttentionEvent(touid, isAttention));
                        if (callback != null) {
                            callback.callback(isAttention);
                        }
                    }

                    @Override
                    public boolean showLoadingDialog() {
                        if (callback != null) {
                            return callback.showLoadingDialog();
                        }
                        return false;
                    }

                    @Override
                    public Dialog createLoadingDialog() {
                        if (callback != null) {
                            return callback.createLoadingDialog();
                        }
                        return null;
                    }
                });
    }

    /**
     * 搜索
     */
    public static void search(String key, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.search")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("key", key)
                .tag(SEARCH)
                .execute(callback);
    }

    public static void searchBody(String key, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.search")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("key", key)
                .params("type", 2)
                .tag(SEARCH)
                .execute(callback);
    }

    /**
     * 他人个人主页信息
     */
    public static void getUserHome(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.getUserHome")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("touid", touid)
                .tag(GET_USER_HOME)
                .execute(callback);
    }

    /**
     * 获取对方的关注列表
     *
     * @param touid    对方的uid
     * @param callback
     */
    public static void getFollowsList(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.getFollowsList")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("touid", touid)
                .tag(GET_FOLLOWS_LIST)
                .execute(callback);
    }

    /**
     * 获取对方的粉丝列表
     *
     * @param touid    对方的uid
     * @param callback
     */
    public static void getFansList(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.getFansList")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("touid", touid)
                .tag(GET_FANS_LIST)
                .execute(callback);
    }


    /**
     * 获取直播回放url
     *
     * @param recordId 视频的id
     * @param callback
     */
    public static void getAliCdnRecord(String recordId, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.getAliCdnRecord")
                .params("id", recordId)
                .tag(GET_ALI_CDN_RECORD)
                .execute(callback);
    }

    /**
     * 拉黑对方， 解除拉黑
     *
     * @param touid    对方的uid
     * @param callback
     */
    public static void setBlack(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.setBlack")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .tag(SET_BLACK)
                .execute(callback);
    }

    /**
     * 判断自己有没有被对方拉黑，环信聊天的时候用到
     *
     * @param touid    对方的uid
     * @param callback
     */
    public static void checkBlack(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.checkBlack")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("touid", touid)
                .tag(CHECK_BLACK)
                .execute(callback);
    }

    /**
     * 获取环信聊天列表用户的信息 uids是多个用户的id,以逗号分隔
     *
     * @param uids
     * @param type     1是已关注  0是未关注
     * @param callback
     */
    public static void getMultiInfo(String uids, String type, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.getMultiInfo")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("type", type)
                .params("uids", uids)
                .tag(GET_MULTI_INFO)
                .execute(callback);
    }

    /**
     * 用来确定 聊天页面 陌生人是关注的还是未关注的
     *
     * @param touid
     * @param callback
     */
    public static void getPmUserInfo(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.getPmUserInfo")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("touid", touid)
                .tag(GET_PM_USER_INFO)
                .execute(callback);
    }

    /**
     * 个人页面获取用户自己的信息
     *
     * @param callback
     */
    public static void getBaseInfo(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.getBaseInfo")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(GET_BASE_INFO)
                .execute(callback);
    }

    /**
     * 获取用户的直播记录，个人页面用的时候 touid传自己的uid
     *
     * @param callback
     */
    public static void getLiverecord(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.getLiverecord")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("touid", touid)
                .tag(GET_LIVERECORD)
                .execute(callback);
    }

    /**
     * 上传头像，用post
     */
    public static void updateAvatar(File file, HttpCallback callback) {
        OkGo.<JsonBean>post(HTTP_URL + "/?service=User.updateAvatar")
                .isMultipart(true)
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("file", file)
                .tag(UPDATE_AVATAR)
                .execute(callback);
    }


    /**
     * 更新用户资料
     *
     * @param fields   用户资料 ,以json形式出现
     * @param callback
     */
    public static void updateFields(String fields, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.updateFields")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("fields", fields)
                .tag(UPDATE_FIELDS)
                .execute(callback);
    }

    /**
     * 修改密码
     *
     * @param oldpass  旧密码
     * @param pass     新密码
     * @param pass2    新密码
     * @param callback
     */
    public static void updatePass(String oldpass, String pass, String pass2, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.updatePass")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("oldpass", oldpass)
                .params("pass", pass)
                .params("pass2", pass2)
                .tag(UPDATE_PASS)
                .execute(callback);
    }

    /**
     * 获取礼物列表，同时会返回剩余的钱
     *
     * @param callback
     */
    public static void getGiftList(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.getGiftList")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(GET_GIFT_LIST)
                .execute(callback);
    }

    /**
     * 获取礼物包裹
     *
     * @param callback
     */
    public static void getGiftPackage(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.GetPackages")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(GET_GIFT_PACKAGE)
                .execute(callback);
    }

    /**
     * 观众给主播送礼物
     *
     * @param liveuid       主播的uid
     * @param giftid        礼物的id
     * @param giftcount     礼物的数量，默认是1
     * @param stream        主播直播间的stream
     * @param isPackageGift 礼物是否是包裹中的礼物
     * @param callback
     */
    public static void sendGift(String liveuid, String giftid, String giftcount, String stream, boolean isPackageGift, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + (isPackageGift ? "/?service=Live.SendPackage" : "/?service=Live.sendGift"))
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveuid)
                .params("giftid", giftid)
                .params("giftcount", giftcount)
                .params("stream", stream)
                .tag(SEND_GIFT)
                .execute(callback);
    }


    /**
     * 检查直播间状态，是否收费 是否有密码等
     *
     * @param liveuid  主播的uid
     * @param stream   主播的stream
     * @param callback
     */
    public static void checkLive(String liveuid, String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.checkLive")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveuid)
                .params("stream", stream)
                .tag(CHECK_LIVE)
                .execute(callback);
    }

    public static void enterRoom(String liveuid, String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.enterRoom")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveuid)
                .params("city", AppConfig.getInstance().getCity())
                .params("stream", stream)
                .tag(ENTER_ROOM)
                .execute(callback);
    }

    /**
     * 发送弹幕
     *
     * @param liveuid  主播的uid
     * @param stream   主播直播间的stream
     * @param callback
     */
    public static void sendBarrage(String content, String liveuid, String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.sendBarrage")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveuid)
                .params("stream", stream)
                .params("giftid", "1")
                .params("giftcount", "1")
                .params("content", content)
                .tag(SEND_BARRAGE)
                .execute(callback);
    }

    /**
     * 直播间点击聊天列表出现的弹窗
     */
    public static void getPop(String touid, String liveuid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.getPop")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("liveuid", liveuid)
                .tag(GET_POP)
                .execute(callback);
    }

    /**
     * 主播查看当前直播间的管理员列表
     */
    public static void getAdminList(String liveuid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.getAdminList")
                .params("liveuid", liveuid)
                .tag(GET_ADMIN_LIST)
                .execute(callback);
    }

    /**
     * 主播设置或删除直播间的管理员
     */
    public static void setAdmin(String touid, String liveuid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.setAdmin")
                .params("liveuid", liveuid)
                .params("touid", touid)
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(SET_ADMIN)
                .execute(callback);
    }

    /**
     * 主播或管理员踢人
     */
    public static void kicking(String touid, String liveuid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.kicking")
                .params("liveuid", liveuid)
                .params("touid", touid)
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(KICKING)
                .execute(callback);
    }


    /**
     * 主播或管理员禁言
     */
    public static void setShutUp(String touid, String liveuid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.setShutUp")
                .params("liveuid", liveuid)
                .params("touid", touid)
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(SET_SHUT_UP)
                .execute(callback);
    }

    /**
     * 超管关闭直播间或禁用账户
     */
    public static void superStopRoom(String liveuid, String type, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.superStopRoom")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveuid)
                .params("type", type)
                .tag(SUPER_STOP_ROOM)
                .execute(callback);
    }

    /**
     * 举报
     */
    public static void setReport(String touid, String content, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.setReport")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("content", content)
                .tag(SET_REPORT)
                .execute(callback);
    }


    /**
     * 主播开播
     *
     * @param title    直播标题
     * @param type     直播类型 普通 密码 收费等
     * @param type_val 密码 价格等
     * @param file     封面图片文件
     * @param callback
     */
    public static void createRoom(String title, String type, String type_val, File file, HttpCallback callback) {
        UserBean u = AppConfig.getInstance().getUserBean();
        PostRequest<JsonBean> request = OkGo.<JsonBean>post(HTTP_URL + "/?service=Live.createRoom")
                .params("title", title)
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("user_nicename", u.getUser_nicename())
                .params("avatar", u.getAvatar())
                .params("avatar_thumb", u.getAvatar_thumb())
                .params("city", AppConfig.getInstance().getCity())
                .params("province", u.getProvince())
                .params("lat", AppConfig.getInstance().getLat())
                .params("lng", AppConfig.getInstance().getLng())
                .params("type", type)
                .params("type_val", type_val)
                .tag(CREATE_ROOM);
//        if (file != null) {
//            request.params("file", file);
//        }
        request.execute(callback);
    }

    /**
     * 修改直播状态，把开播信息显示在主页上
     *
     * @param stream
     * @param status
     * @param callback
     */
    public static void changeLive(String stream, String status, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.changeLive")
                .params("stream", stream)
                .params("status", status)
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(CHANGE_LIVE)
                .execute(callback);
    }


    /**
     * 修改直播状态，告诉服务器说，我要结束直播了
     *
     * @param stream
     * @param callback
     */
    public static void stopRoom(String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.stopRoom")
                .params("stream", stream)
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(STOP_ROOM)
                .execute(callback);
    }


    /**
     * 直播结束后，获取直播收益，观看人数，时长等信息
     *
     * @param stream
     * @param callback
     */
    public static void stopLiveInfo(String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.stopInfo")
                .params("stream", stream)
                .tag(STOP_LIVE_INFO)
                .execute(callback);
    }

    /**
     * 主播添加背景音乐时，搜索歌曲
     *
     * @param key      关键字
     * @param callback
     */
    public static void searchMusic(String key, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Music.searchMusic")
                .params("key", key)
                .tag(SEARCH_MUSIC)
                .execute(callback);
    }

    /**
     * 获取歌曲的地址 和歌词的地址
     *
     * @param audio_id
     * @param callback
     */
    public static void getDownMusicUrl(String audio_id, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Music.getDownurl")
                .params("audio_id", audio_id)
                .tag(GET_DOWN_MUSIC_URL)
                .execute(callback);
    }

    /**
     * 主播切换计时收费模式
     *
     * @param stream
     * @param typeVal
     * @param callback
     */
    public static void changeLiveType(String stream, String typeVal, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.changeLiveType")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("type", "3")
                .params("type_val", typeVal)
                .tag(CHANGE_LIVE_TYPE)
                .execute(callback);
    }


    /**
     * 当直播间是门票收费，计时收费或切换成计时收费的时候，观众请求这个接口
     *
     * @param liveuid
     * @param stream
     * @param callback
     */
    public static void roomCharge(String liveuid, String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.roomCharge")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("liveuid", liveuid)
                .tag(ROOM_CHARGE)
                .execute(callback);
    }

    /**
     * 当直播间是计时收费的时候，观众每隔一段时间请求这个接口
     *
     * @param liveuid
     * @param stream
     * @param callback
     */
    public static void timeCharge(String liveuid, String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.timeCharge")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("liveuid", liveuid)
                .tag(TIME_CHARGE)
                .execute(callback);
    }

    /**
     * 充值页面，我的钻石
     */
    public static void getBalance(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.getBalance")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(GET_BALANCE)
                .execute(callback);
    }


    /**
     * 用支付宝充值 的时候在服务端生成订单号
     *
     * @param money    RMB价格
     * @param callback
     */
    public static void getAliOrder(String money, String coin, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Charge.getAliOrder")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("money", money)
                .params("changeid", 0)
                .params("ambient", 2)
                .params("coin", coin)
                .tag(GET_ALI_ORDER)
                .execute(callback);
    }

    /**
     * 用微信支付充值 的时候在服务端生成订单号
     *
     * @param money    RMB价格
     * @param callback
     */
    public static void getWxOrder(String money, String coin, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Charge.getWxOrder")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("money", money)
                .params("changeid", 0)
                .params("coin", coin)
                .params("ambient", 2)
                .tag(GET_WX_ORDER)
                .execute(callback);
    }


    /**
     * 我的收益
     */
    public static void getProfit(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.getProfit")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(GET_PROFIT)
                .execute(callback);
    }

    /**
     * 我的收益,提现
     */
    public static void getCash(String coin, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.setCash")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("coin", coin)
                .tag(GET_CASH)
                .execute(callback);
    }


    /**
     * 首页登录奖励
     */
    public static void getBonus(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.Bonus")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(GET_BONUS)
                .execute(callback);
    }

    /**
     * 竞拍加价
     */
    public static void setAuction(String auctionid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.setAuction")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("auctionid", auctionid)
                .tag(SET_AUCTION)
                .execute(callback);
    }

    /**
     * 竞拍结束
     */
    public static void auctionEnd(String auctionid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.auctionEnd")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("auctionid", auctionid)
                .tag(AUCTION_END)
                .execute(callback);
    }

    /**
     * 竞拍成功后付款
     */
    public static void setBidPrice(String auctionid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.setBidPrice")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("auctionid", auctionid)
                .tag(SET_BID_PRICE)
                .execute(callback);
    }

    /**
     * 获取用户钻石余额
     */
    public static void getCoin(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.getCoin")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(GET_COIN)
                .execute(callback);
    }

    /**
     * 获取当前直播间的用户列表
     */
    public static void getUserList(String liveuid, String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.getUserLists")
                .params("liveuid", liveuid)
                .params("stream", stream)
                .tag(GET_USER_LIST)
                .execute(callback);
    }


    //用于用户首次登录设置分销关系
    public static void setDistribut(String code, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.setDistribut")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("code", code)
                .tag(SET_DISTRIBUT)
                .execute(callback);
    }

    //用于用户首次登录推荐
    public static void getRecommend(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.getRecommend")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag(GET_RECOMMEND)
                .execute(callback);
    }

    //用于用户首次登录推荐,关注主播
    public static void attentRecommend(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.attentRecommend")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("touid", touid)
                .tag(ATTENT_RECOMMEND)
                .execute(callback);
    }


    // TODO: 2018/8/21 排行接口
    //排行榜  收益榜
    public static void getMoreRankList(int dataType, int rankType, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.GetOneBdList")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("dtype", dataType == 0 ? "week" : "month")
                .params("ktype", rankType == 0 ? "mxwlist" : rankType == 1 ? "fhwlist" : "egglist")
                .tag(PROFIT_LIST)
                .execute(callback);
    }

    public static void getHomeBdList(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.GetBdList")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag(PROFIT_LIST)
                .execute(callback);
    }

    //创建炸金花游戏
    public static void gameJinhuaCreate(String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.Jinhua")
                .params("liveuid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .tag(GAME_JINHUA_CREATE)
                .execute(callback);
    }

    //炸金花游戏下注
    public static void gameJinhuaBet(String gameid, String coin, String grade, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.JinhuaBet")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("gameid", gameid)
                .params("coin", coin)
                .params("grade", grade)
                .tag(GAME_JINHUA_BET)
                .execute(callback);
    }

    //游戏结果出来后，观众获取自己赢到的金额
    public static void gameSettle(String gameid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.settleGame")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("gameid", gameid)
                .tag(GAME_SETTLE)
                .execute(callback);
    }

    //创建海盗船长游戏
    public static void gameHaidaoCreate(String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.Taurus")
                .params("liveuid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .tag(GAME_HAIDAO_CREATE)
                .execute(callback);
    }

    //海盗船长游戏下注
    public static void gameHaidaoBet(String gameid, String coin, String grade, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.Taurus_Bet")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("gameid", gameid)
                .params("coin", coin)
                .params("grade", grade)
                .tag(GAME_HAIDAO_BET)
                .execute(callback);
    }


    //创建开心牛仔游戏
    public static void gameNiuzaiCreate(String stream, String bankerid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.Cowboy")
                .params("liveuid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("bankerid", bankerid)
                .tag(GAME_NIUZAI_CREATE)
                .execute(callback);
    }

    //开心牛仔游戏下注
    public static void gameNiuzaiBet(String gameid, String coin, String grade, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.Cowboy_Bet")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("gameid", gameid)
                .params("coin", coin)
                .params("grade", grade)
                .tag(GAME_NIUZAI_BET)
                .execute(callback);
    }


    //开心牛仔获取庄家列表,列表第一个为当前庄家
    public static void gameNiuGetBanker(String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.getBanker")
                .params("stream", stream)
                .tag(GAME_NIU_GET_BANKER)
                .execute(callback);
    }

    /**
     * 开心牛仔申请上庄
     *
     * @param stream 押金金额
     */
    public static void gameNiuSetBanker(String stream, String deposit, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.setBanker")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("deposit", deposit)
                .tag(GAME_NIU_SET_BANKER)
                .execute(callback);
    }

    //开心牛仔申请下庄
    public static void gameNiuQuitBanker(String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.quietBanker")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .tag(GAME_NIU_QUIT_BANKER)
                .execute(callback);
    }


    //开心牛仔庄家流水
    public static void gameNiuBankerWater(String bankerId, String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.getBankerProfit")
                .params("uid", bankerId)
                .params("stream", stream)
                .tag(GAME_NIU_BANKER_WATER)
                .execute(callback);
    }

    //开心牛仔游戏胜负记录
    public static void gameNiuRecord(String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.getGameRecord")
                .params("action", "4")
                .params("stream", stream)
                .tag(GAME_NIU_RECORD)
                .execute(callback);
    }

    //创建二八贝游戏
    public static void gameEbbCreate(String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.Cowry")
                .params("liveuid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .tag(GAME_EBB_CREATE)
                .execute(callback);
    }

    //二八贝下注
    public static void gameEbbBet(String gameid, String coin, String grade, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.Cowry_Bet")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("gameid", gameid)
                .params("coin", coin)
                .params("grade", grade)
                .tag(GAME_EBB_BET)
                .execute(callback);
    }

    //创建转盘游戏
    public static void gameLuckPanCreate(String stream, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.Dial")
                .params("liveuid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .tag(GAME_LUCK_PAN_CREATE)
                .execute(callback);
    }

    //转盘游戏下注
    public static void gameLuckPanBet(String gameid, String coin, String grade, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Game.Dial_Bet")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("gameid", gameid)
                .params("coin", coin)
                .params("grade", grade)
                .tag(GAME_LUCK_PAN_BET)
                .execute(callback);
    }

    /**
     * 获取banner 数据
     *
     * @param cId      banner类型  2=首页 3=娱乐
     * @param callback 请求回调
     */
    public static void getBannerList(int cId, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.GetSlideById")
                .params("cid", cId)
                .tag(GET_BANNER_LIST)
                .execute(callback);
    }

    /**
     * 获取榜单头部 banner 数据
     *
     * @param cId      banner类型 3 = 榜单广告
     * @param callback 请求回调
     */
    public static void getRankBannerList(int cId, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.GetAdsById")
                .params("cid", cId)
                .tag(GET_RANK_BANNER_LIST)
                .execute(callback);
    }

    public static void getAllLiveTag(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.GetAllLiveTag")
                .tag(GET_LIVE_TAG)
                .execute(callback);
    }

    public static void getAllLiveTagSub(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.GetAllLiveTag&type=2")
                .tag(GET_LIVE_TAG)
                .execute(callback);
    }

    /**
     * 根据tag 获取直播数据
     *
     * @param tagId    tagid
     * @param callback 回调
     */
    public static void getLiveByTag(String tagId, int pageNum, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.GetLiveByTagId")
                .params("live_tag_id", tagId)
                .params("p", pageNum)
                .tag(GET_LIVE_BY_TAG)
                .execute(callback);
    }

    /**
     * 获取娱乐主播
     *
     * @param callback
     */
    public static void getPleasureLive(int pageNum, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.PleasureLive")
                .params("p", pageNum)
                .tag(GET_PLEASURE_LIVE)
                .execute(callback);
    }

    /**
     * 创建直播间
     *
     * @param title      直播间名称
     * @param livetag_id 直播间类型id
     * @param callback   回调
     */
    public static void createRoomByTag(String title, String livetag_id, int screenType, HttpCallback callback) {
        UserBean u = AppConfig.getInstance().getUserBean();
        PostRequest<JsonBean> request = OkGo.<JsonBean>post(HTTP_URL + "/?service=Live.CreateRoomNew")
                .params("title", title)
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("user_nicename", u.getUser_nicename())
                .params("avatar", u.getAvatar())
                .params("avatar_thumb", u.getAvatar_thumb())
                .params("livetag_id", livetag_id)
                .params("anyway", screenType)
                .tag(CREATE_ROOM);
        request.execute(callback);
    }

    /**
     * 获得海星
     *
     * @param callback
     */
    public static void getFreeGift(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.AddStar")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(GET_FREE_GIFT)
                .execute(callback);
    }

    /**
     * 赠送海星
     *
     * @param liveUid  主播id
     * @param callback
     */
    public static void sendFreeGift(String liveUid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.SendStar")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", liveUid)
                .tag(SEND_FREE_GIFT)
                .execute(callback);
    }


    /**
     * 判断是否关注主播
     *
     * @param touid    主播id
     * @param callback 回调   0表示未关注，1表示已关注
     *                 "info": [
     *                 {
     *                 "isattent": "0"
     *                 }
     *                 ]
     */
    public static void getIsAttent(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.IsAttent")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("touid", touid)
                .tag(GET_USER_ISATTENT)
                .execute(callback);
    }

    public static void getLiveInfo(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.LiveInfo")
                .params("liveuid", touid)
                .tag("getLiveInfo")
                .execute(callback);
    }

    /**
     * 获取直播贡献周榜
     *
     * @param touid    直播id
     * @param callback
     */
    public static void getWeekRank(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.WeekRank")
                .params("touid", touid)
                .tag("getWeekRank")
                .execute(callback);
    }

    /**
     * 获取直播贡献日榜
     *
     * @param touid    直播id
     * @param callback
     */
    public static void getDayRank(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.DayRank")
                .params("touid", touid)
                .tag("getDayRank")
                .execute(callback);
    }

    public static void getAllLiveByPage(int pageNum, HttpCallback callback) {
//        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.getHot")
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Home.GetAllLive")
                .params("p", pageNum)
                .tag(GET_HOT)
                .execute(callback);
    }

    public static void getChargeHistory(int pageNum, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Charge.ChargeLog")
                .params("p", pageNum)
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag(GET_CHARGE_HISTORY)
                .execute(callback);
    }

    /**
     * 获取每日任务
     *
     * @param callback
     */
    public static void getDailyTaskList(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.GetTaskList")
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag(GET_DAILY_TASK)
                .execute(callback);
    }

    public static void getDailyTaskGift(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.ExcuteTaskall")
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag(GET_DAILY_TASK)
                .execute(callback);
    }

    public static void excuteDailyTask(String tId, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.ExcuteTask")
                .params("task_id", tId)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag(EXCUTE_DAILY_TASK)
                .execute(callback);
    }

    public static void getBindSms(String bindPhone, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.GetBindCode")
                .params("mobile", bindPhone)
//                .params("token", AppConfig.getInstance().getToken())
//                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid())?"1":AppConfig.getInstance().getUid())
                .tag("User.GetBindCode")
                .execute(callback);
    }

    public static void getBindSms2(String bindPhone, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.GetBindCode")
                .params("mobile", bindPhone)
                .params("type","bindPhone")
//                .params("token", AppConfig.getInstance().getToken())
//                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid())?"1":AppConfig.getInstance().getUid())
                .tag("User.GetBindCode")
                .execute(callback);
    }

    public static void bindPhone(String bindPhone, String smsCode, int type, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.SetMobile")
                .params("mobile", bindPhone)
                .params("code", smsCode)
                .params("type", type)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("User.SetMobile")
                .execute(callback);
    }

    /**
     * 举报
     *
     * @param liveUserId 主播id
     * @param roomId     直播间id
     * @param roomname   直播间名称
     * @param content    举报内容
     * @param reason     举报原因
     * @param contact    联系方式
     * @param imageUrl   截图地址
     * @param callback   回调
     */
    public static void setNewReport(String liveUserId, String roomId, String roomname, String content,
                                    String reason, String contact, String imageUrl, HttpCallback callback) {
        OkGo.<JsonBean>post(HTTP_URL + "/?service=Live.SetReport")
                .params("touid", liveUserId)
                .params("roomid", roomId)
                .params("roomname", roomname)
                .params("content", content)
                .params("reason", reason)
                .params("contact", contact) //联系方式
                .params("pic1", imageUrl) //联系方式
                .params("informant", AppConfig.getInstance().getUserBean().getUser_nicename())
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("User.SetMobile")
                .execute(callback);
    }

    /**
     * 上传头像，用post
     */
    public static void uploadImage(File file, HttpCallback callback) {
        OkGo.<JsonBean>post(HTTP_URL + "/?service=User.UploadPic")
                .isMultipart(true)
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("file", file)
                .tag(UPDATE_AVATAR)
                .execute(callback);
    }

    public static void commitFeedback(String remark, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.Advise")
                .params("remark", remark)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("User.SetMobile")
                .execute(callback);
    }

    /**
     * 开启 酒桶任务
     *
     * @param callback
     */
    public static void AddCask(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.AddCask")
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("User.SetMobile")
                .execute(callback);
    }

    public static void beerCaskThank(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.ThankCask")
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("User.SetMobile")
                .execute(callback);
    }

    /**
     * 观众送礼获得主播答谢
     *
     * @param liveuid  主播id
     * @param callback 回调
     */
    public static void getBeerCaskThank(String liveuid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.DrawCask")
                .params("liveuid", liveuid)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("Live.DrawCask")
                .execute(callback);
    }

    public static void getHarvestHistory(int pageNum, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.GetCashLog")
                .params("p", pageNum)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("getHarvestHistory")
                .execute(callback);
    }

    public static void setLiveApply(String real_name, String card_no, String bank_name,
                                    String bank_card, String mobile, String livetag, String introduction,
                                    File file, File file1, File file2,
                                    HttpCallback callback) {

        OkGo.<JsonBean>post(HTTP_URL + "/?service=Live.LiveApply")
                .isMultipart(true)
                .params("real_name", real_name)
                .params("card_no", card_no)
                .params("bank_name", bank_name)
                .params("bank_card", bank_card)
                .params("mobile", mobile)
                .params("livetag", livetag)
                .params("pic1", file)
                .params("pic2", file1)
                .params("pic3", file2)
                .params("introduction", introduction)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("setLiveApply")
                .execute(callback);
    }

    /**
     * 获取主播资质
     */
    public static void getLiveAuth(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.LiveAuth")
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("getHarvestHistory")
                .execute(callback);
    }

    /**
     * 判断用户是否被禁言
     */
    public static void sendTmsg(String liveUid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.SendTmsg")
                .params("liveuid", liveUid)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("getHarvestHistory")
                .execute(callback);
    }

    /**
     * 主播发送直播随机红包
     * <p>
     * 红包类型 1-随机红包 2-口令兔尾巴红包 3-口令自定义红包
     *
     * @param coinNum   随机红包总金额
     * @param personNum 随机红包个数
     * @param callback  回调
     */
    public static void sendRandomRP(long coinNum, long personNum, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.AddRedp")
                .params("type", 1) //
                .params("coin", coinNum)
                .params("num", personNum)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("sendRandomRP")
                .execute(callback);
    }

    /**
     * 口令红包
     *
     * @param type     红包类型 1-随机红包 2-口令兔尾巴红包 3-口令自定义红包
     * @param name     奖品名称  如果是兔尾巴 则  xx个兔尾巴
     * @param koul     口令名称
     * @param tnum     时长
     * @param callback
     */
    public static void sendCommandRp(int type, String name, String koul, int tnum, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.AddRedp")
                .params("type", type) //
                .params("name", name)
                .params("paper", name)
                .params("koul", koul)
                .params("tnum", tnum)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("sendRandomRP")
                .execute(callback);
    }

    public static void getLiveRp(int type, String liveuid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.DrawRedp")
                .params("type", type) //
                .params("liveuid", liveuid)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("getLiveRp")
                .execute(callback);
    }

    public static void getLiveCommandResultRp(int type, String rpId, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.EndRedp")
                .params("type", type) //
                .params("rid", rpId)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("getLiveRp")
                .execute(callback);
    }

    public static void getLivRpInfo(String liveId, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.RedpInfo")
                .params("liveuid", liveId)
                .params("token", AppConfig.getInstance().getToken())
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("getLivRpInfo")
                .execute(callback);
    }

    /**
     * 获取消息中心列表  系统消息、个人消息
     *
     * @param callback
     */
    public static void getMsgList(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.GetMsglist")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .tag("getMsgList")
                .execute(callback);
    }

    /**
     * 消息中心详情列表
     *
     * @param callback
     */
    public static void getMsgDetailList(int type, int pageNum, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.GetMsginfo")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", pageNum)
                .tag("getMsgList")
                .execute(callback);
    }

    public static void getInitEggInfo(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.EggData")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag("getInitEggInfo")
                .execute(callback);
    }

    public static void onKnockEgg(int level, int amount, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.SendEgg")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("level", level)
                .params("amount", amount)
                .tag("onKnockEgg")
                .execute(callback);
    }

    public static void getEggResult(int coin, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.GetEgg")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("coin", coin)
                .tag("onKnockEgg")
                .execute(callback);
    }

    /**
     * 账号绑定解绑微信
     *
     * @param openId 微信授权id
     * @param type   1=绑定 2=解绑
     */
    public static void onBindWx(String openId, int type, String wxName, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.BindWX")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("openid", openId)
                .params("type", type)
                .params("weixin", wxName)
                .tag("BindWX")
                .execute(callback);
    }

    public static void onFinishShare() {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.IsShare")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("task_id", 2)
                .tag("Live.IsShare")
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                    }
                });
    }

    public static void getChangeNameConfig(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=User.GetNamecoin")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag("User.GetNamecoin")
                .execute(callback);
    }

    /**
     * 获取抽奖信息， 礼品列表 钥匙数量
     *
     * @param ctype    类型 1=铜 2=银 3=金
     * @param callback
     */
    public static void getLotteryInfo(int ctype, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.ProCask")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("ctype", ctype)
                .tag("User.GetNamecoin")
                .execute(callback);
    }

    public static void getLotteryResult(int ctype, String num, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.SendCask")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("ctype", ctype)
                .params("num", num)
                .tag("User.GetNamecoin")
                .execute(callback);
    }

    public static void getLotteryHistory(int pageNum, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "/?service=Live.ListCask")
                .params("uid", TextUtils.isEmpty(AppConfig.getInstance().getUid()) ? "1" : AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", pageNum)
                .tag("User.GetNamecoin")
                .execute(callback);
    }
}




