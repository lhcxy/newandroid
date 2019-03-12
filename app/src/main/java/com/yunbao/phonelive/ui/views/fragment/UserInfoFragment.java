package com.yunbao.phonelive.ui.views.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AttentionActivity;
import com.yunbao.phonelive.activity.ChargeActivity;
import com.yunbao.phonelive.activity.EditProfileActivity;
import com.yunbao.phonelive.activity.HarvestActivity;
import com.yunbao.phonelive.activity.LiveRecordListActivity;
import com.yunbao.phonelive.activity.LoginActivity;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.activity.SettingActivity;
import com.yunbao.phonelive.bean.SharedSdkBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.LoginSuccessEvent;
import com.yunbao.phonelive.event.RefreshUserInfoEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.views.BindMobileActivity;
import com.yunbao.phonelive.ui.views.BindWechatActivity;
import com.yunbao.phonelive.ui.views.ChangeMobileActivity;
import com.yunbao.phonelive.ui.views.DailyTaskActivity;
import com.yunbao.phonelive.ui.views.LiveCameraActivity;
import com.yunbao.phonelive.ui.views.MinePackageActivity;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.SharedSdkUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.wechat.friends.Wechat;
import io.reactivex.disposables.CompositeDisposable;

public class UserInfoFragment extends BaseLazyFragment {
    private ImageView userAvatar;
    private TextView userName, userUid, userCoin, menuRecord, menuFollow, menuRecharge, menuExtract, levelTv, carrotTv, mobileTv;
    private CompositeDisposable disposable;

    private RelativeLayout userInfoRl;
    private LinearLayout userLoginLL;

    public static UserInfoFragment newInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_user_info;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        disposable = new CompositeDisposable();

        userAvatar = findView(R.id.user_avatar_iv);
        userName = findView(R.id.user_name_tv);
        userCoin = findView(R.id.user_coin_tv);
        userUid = findView(R.id.user_uid_tv);
        menuRecord = findView(R.id.user_menu_record);
        menuRecharge = findView(R.id.user_menu_recharge);
        menuFollow = findView(R.id.user_menu_follow);
        menuExtract = findView(R.id.user_menu_extract);
        mobileTv = findView(R.id.user_info_mobile_tv);

        userInfoRl = findView(R.id.userinfo_rl);
        userLoginLL = findView(R.id.user_login_ll);
        levelTv = findView(R.id.user_level_tv);
        carrotTv = findView(R.id.user_carrot_tv);
        initClick();
        initData();
    }

    private void initClick() {
        disposable.add(RxView.clicks(findView(R.id.user_setting_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> startActivity(new Intent(getContext(), SettingActivity.class))));
        disposable.add(RxView.clicks(userAvatar).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (AppConfig.isUnlogin()) {
                        unloginHint(userAvatar);
                        return;
                    }
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }));
        disposable.add(RxView.clicks(userName).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (AppConfig.isUnlogin()) {
                        unloginHint(userAvatar);
                        return;
                    }
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }));
        //提款
        disposable.add(RxView.clicks(menuExtract).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (AppConfig.isUnlogin()) {
                        unloginHint(userAvatar);
                        return;
                    }
                    startActivity(new Intent(getContext(), HarvestActivity.class));
                }));
        //充值
        disposable.add(RxView.clicks(menuRecharge).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (AppConfig.isUnlogin()) {
                        unloginHint(userAvatar);
                        return;
                    }
                    startActivity(new Intent(getContext(), ChargeActivity.class));
                }));
        // 记录
//        disposable.add(RxView.clicks(menuRecord).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
//                .subscribe(o -> forwardRecord()));
        //直播
        disposable.add(RxView.clicks(menuRecord).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (getActivity() != null) {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).getLiveAuth();
                            return;
                        }
                    }

                    if (AppConfig.isUnlogin()) {
                        unloginHint(userAvatar);
                        return;
                    }
                    if (AppConfig.getInstance() == null || TextUtils.isEmpty(AppConfig.getInstance().getUid())) {
                        AppConfig.getInstance().reset();
                        AppConfig.getInstance().getUserBean();
                        return;
                    }
                    Intent intent = new Intent(getContext(), LiveCameraActivity.class);
                    startActivity(intent);
//                    startActivity(new Intent(getContext(), EMChatActivity.class));
                }));
        // 关注
        disposable.add(RxView.clicks(menuFollow).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (AppConfig.isUnlogin()) {
                        unloginHint(userAvatar);
                        return;
                    }
                    forwardAttention("0");
                }));
        //我的包裹
        disposable.add(RxView.clicks(findView(R.id.user_info_package_ll)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (AppConfig.isUnlogin()) {
                        unloginHint(userAvatar);
                        return;
                    }
                    startActivity(new Intent(getContext(), MinePackageActivity.class));
                }));
        //绑定手机
        disposable.add(RxView.clicks(findView(R.id.user_info_mobile_ll)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (AppConfig.isUnlogin()) {
                        unloginHint(userAvatar);
                        return;
                    }
                    if (AppConfig.getInstance().getUserBean().getIsbind() == 1) {
                        startActivity(new Intent(getContext(), ChangeMobileActivity.class));
                    } else {
                        startActivity(new Intent(getContext(), BindMobileActivity.class));
                    }
                }));
        //绑定微信
        disposable.add(RxView.clicks(findView(R.id.user_info_wechat_ll)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (AppConfig.isUnlogin()) {
                        unloginHint(userAvatar);
                        return;
                    }
                    if (AppConfig.getInstance().getUserBean().getWxtype() == 0) {
                        onBindWx();
                    } else startActivity(new Intent(getContext(), BindWechatActivity.class));
                }));
        //每日任务
        disposable.add(RxView.clicks(findView(R.id.user_info_daily_ll)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (AppConfig.isUnlogin()) {
                        unloginHint(userAvatar);
                        return;
                    }
                    startActivity(new Intent(getContext(), DailyTaskActivity.class));
                }));
        //赛程
        disposable.add(RxView.clicks(findView(R.id.user_info_schedule_ll)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> ToastUtil.show("敬请期待!")));
//        消息中心


//        登陆
        disposable.add(RxView.clicks(findView(R.id.user_login_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> startActivity(new Intent(getContext(), LoginActivity.class))));
//        注册
//        disposable.add(RxView.clicks(findView(R.id.user_regist_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
//                .subscribe(o -> startActivity(new Intent(getContext(), RegisterActivity.class))));
    }

    UserBean mUserBean;

    @Override
    protected void initData() {
        if (AppConfig.getInstance() != null && AppConfig.getInstance().getUserBean() != null) {
            mUserBean = AppConfig.getInstance().getUserBean();
            if (AppConfig.getInstance().getConfig() != null) {
                userCoin.setText(TextUtils.isEmpty(mUserBean.getCoin()) ? "0" : mUserBean.getCoin());
            }
            ImgLoader.displayCircleWhiteBorder(mUserBean.getAvatar(), userAvatar);
            userName.setText(mUserBean.getUser_nicename());
            levelTv.setText(String.valueOf(mUserBean.getLevel()));
            carrotTv.setText(TextUtils.isEmpty(mUserBean.getCarrot()) ? "0" : mUserBean.getCarrot());
            userUid.setText("uid: " + mUserBean.getId());
            userInfoRl.setVisibility(View.VISIBLE);
            userLoginLL.setVisibility(View.GONE);
            if (AppConfig.getInstance().getUserBean() != null && AppConfig.getInstance().getUserBean().getIsbind() == 1) {
                mobileTv.setText("更换绑定手机");
            }
        } else {
            ImgLoader.displayCircleWhiteBorder("", userAvatar);
            mobileTv.setText("绑定手机");
            userInfoRl.setVisibility(View.GONE);
            userLoginLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 跳转到关注  粉丝
     *
     * @param type 0是关注  1是粉丝
     */
    private void forwardAttention(String type) {
        if (AppConfig.isUnlogin()) {
            unloginHint(userAvatar);
            return;
        }
        Intent intent = new Intent(getContext(), AttentionActivity.class);
        intent.putExtra("touid", mUserBean.getId());
        intent.putExtra("type", type);
        intent.putExtra("sex", mUserBean.getSex());
        startActivity(intent);
    }

    /**
     * 跳转直播记录
     */
    private void forwardRecord() {
        Intent intent = new Intent(getContext(), LiveRecordListActivity.class);
        intent.putExtra("touid", mUserBean.getId());
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshUserInfo(RefreshUserInfoEvent event) {
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccessEvent(LoginSuccessEvent e) {
        initData();
    }


    //第三方登录回调
    private SharedSdkUitl.ShareListener mShareListener = new SharedSdkUitl.ShareListener() {
        @Override
        public void onSuccess(Platform platform) {
            ToastUtil.show(getString(R.string.auth_success));
            final PlatformDb platDB = platform.getDb();
            if (platDB.getPlatformNname().equals(Wechat.NAME)) {
                openId = platDB.get("unionid");
                wxName = platDB.get("nickname");
                HttpUtil.onBindWx(openId, 1, wxName, callback);
            }
        }

        @Override
        public void onError(Platform platform) {
            if (mLoginAuthDialog != null) {
                mLoginAuthDialog.dismiss();
            }
            ToastUtil.show(getString(R.string.auth_failure));
        }

        @Override
        public void onCancel(Platform platform) {
            if (mLoginAuthDialog != null) {
                mLoginAuthDialog.dismiss();
            }
            ToastUtil.show(getString(R.string.auth_cancle));
        }
    };
    private String openId = "", wxName;
    private Dialog mLoginAuthDialog;

    private void onBindWx() {
        if (mLoginAuthDialog == null) {
            mLoginAuthDialog = DialogUitl.loginAuthDialog(getContext());
        }
        mLoginAuthDialog.show();
        SharedSdkUitl.getInstance().login(SharedSdkBean.WX, mShareListener);
    }

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                AppConfig.getInstance().getUserBean().setWxtype(1);
                AppConfig.getInstance().getUserBean().setWxopenid(openId);
                AppConfig.getInstance().getUserBean().setWeixin(wxName);
            }
            ToastUtil.show(msg);
        }

        @Override
        public void onFinish() {
            if (mLoginAuthDialog != null) {
                mLoginAuthDialog.dismiss();
            }
            super.onFinish();
        }
    };
}
