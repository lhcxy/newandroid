package com.yunbao.phonelive.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.RewardBean;
import com.yunbao.phonelive.custom.reward.LoginRewardWindow;
import com.yunbao.phonelive.fragment.ExitFragment;
import com.yunbao.phonelive.fragment.InviteFragment;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.IM;
import com.yunbao.phonelive.im.JIM;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.interfaces.MainEventListener;
import com.yunbao.phonelive.presenter.CheckLivePresenter;
import com.yunbao.phonelive.ui.dialog.LiveGetCommandRpDF;
import com.yunbao.phonelive.ui.dialog.LiveGetRandomRpDF;
import com.yunbao.phonelive.ui.dialog.LiveSendRpDF;
import com.yunbao.phonelive.ui.views.AnchorCertificateActivity;
import com.yunbao.phonelive.ui.views.LiveCameraActivity;
import com.yunbao.phonelive.ui.views.fragment.NewHomeFragment;
import com.yunbao.phonelive.ui.views.fragment.RankListFragment;
import com.yunbao.phonelive.ui.views.fragment.RecreationFragment;
import com.yunbao.phonelive.ui.views.fragment.UserInfoFragment;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.JPushUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.VersionUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/8/8.
 * 主页面
 */

public class MainActivity extends AbsActivity {

    public static final int HOME = 0;
    //    public static final int NEAR = 1;
//    public static final int LIST = 2;
//    public static final int USER = 3;
    public static final int USER = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 100;//请求定位权限的请求码
    private static final int REQUEST_READ_PERMISSION = 101;//请求文件读写权限的请求码
    private static final int MOUNT_UNMOUNT_FILESYSTEMS = 102;//请求文件读写权限的请求码
    private RelativeLayout mRootViewGroup;
    private View mBtnMe;
    private FragmentManager mFragmentManager;
    private SparseArray<Fragment> mMap;
    private int mCurFragmentKey;
    //    private HomeFragment mHomeFragment;
//    private HomeNearFragment mNearFragment;
//    private HomeListFragment mListFragment;
//    private UserFragment mUserFragment;
    private int mUnReadCount;//未读消息数量
    private CheckLivePresenter mCheckLivePresenter;
    private IM mIM;

    public static void startMainActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("jpusheventBundle", bundle);
        context.startActivity(intent);
    }

    public static void startMainActivity(Context context, String roomId, String stream) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("stream", stream);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void main() {
        mRootViewGroup = (RelativeLayout) findViewById(R.id.rootLayout);
        mBtnMe = findViewById(R.id.btn_me);
        mMap = new SparseArray<>();
//        mHomeFragment = new HomeFragment();
//        mNearFragment = new HomeNearFragment();
//        mListFragment = new HomeListFragment();
//        mUserFragment = new UserFragment();
//        mMap.put(HOME, HomeRecommendFragment.newInstance());
        mMap.put(HOME, NewHomeFragment.newInstance());
//        mMap.put(NEAR, RecreationFragment.newInstance());
//        mMap.put(LIST, new UserFragment());
//        mMap.put(LIST, RankListFragment.newInstance());
        mMap.put(USER, UserInfoFragment.newInstance());
        mCurFragmentKey = 0;
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for (int i = 0; i < mMap.size(); i++) {
            Fragment f = mMap.valueAt(i);
            ft.add(R.id.replaced, f);
            if (mCurFragmentKey == mMap.keyAt(i)) {
                ft.show(f);
            } else {
                ft.hide(f);
            }
        }
        ft.commitNow();

        mIM = new JIM();

        EventBus.getDefault().register(this);
//        startLocation();

//        getLoginReward();
        AppConfig.getInstance().refreshUserInfo();
//        邀请信息
//        String isReg = getIntent().getStringExtra("isreg");
//        if ("1".equals(isReg)) {
//            showInviteDialog();
//        }
        initJPushMsg();
//        mainClick();
        getConfig();

    }

    private long startTime = 0L;
    private static final long GET_DURATION = 60 * 1000L;

    public void mainClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home://主页
                toggleFragment(HOME);
                break;
//            case R.id.btn_near://娱乐
//                toggleFragment(NEAR);
//                break;
//            case R.id.btn_list://排行榜
//                toggleFragment(LIST);
//                break;
            case R.id.btn_me://个人页
                toggleFragment(USER);
                if (System.currentTimeMillis() - startTime > GET_DURATION) {
                    AppConfig.getInstance().refreshUserInfo();
                    startTime = System.currentTimeMillis();
                }
                break;
            case R.id.live_btn://开始直播
                getLiveAuth();
//                startActivity(new Intent(mContext, EMChatActivity.class));
//                LiveGetRandomRpDF liveGetRandomRpDF = new LiveGetRandomRpDF();
//                liveGetRandomRpDF.show(getSupportFragmentManager(), "sdf");
                break;
            case R.id.btn_search://搜索
                startActivity(new Intent(mContext, SearchActivity.class));
                break;
            case R.id.btn_message://私信
                startActivity(new Intent(mContext, EMChatActivity.class));
                break;

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("//", "onSaveInstanceState: ");
    }

    /**
     * 切换fragment
     */
    private void toggleFragment(int key) {
        try {
            if (mCurFragmentKey == key) {
                return;
            }
            mCurFragmentKey = key;
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            for (int i = 0; i < mMap.size(); i++) {
                Fragment f = mMap.valueAt(i);
                if (mCurFragmentKey == mMap.keyAt(i)) {
                    ft.show(f);
                } else {
                    ft.hide(f);
                }
            }
            ft.commitNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void onChildResume(int key) {
        if (mMap != null && key == mCurFragmentKey && null != mMap.get(key)) {
            ((MainEventListener) mMap.get(mCurFragmentKey)).loadData();
        }

    }


    private void getConfig() {
        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                checkMaintain();
                checkVersion();
            }
        });
    }


    private void checkVersion() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MOUNT_UNMOUNT_FILESYSTEMS);
            } else {
                VersionUtil.checkVersion(AppConfig.getInstance().getConfig(), mContext, null);
            }
        } else {
            VersionUtil.checkVersion(AppConfig.getInstance().getConfig(), mContext, null);
        }
    }

    /**
     * 是否维护
     */
    private void checkMaintain() {
        ConfigBean bean = AppConfig.getInstance().getConfig();
        if ("1".equals(bean.getMaintain_switch())) {
            DialogUitl.messageDialog(mContext, getString(R.string.maintain_tip), bean.getMaintain_tips(), null).show();
        }
    }

    /**
     * 显示邀请码信息弹窗
     */
    private void showInviteDialog() {
        InviteFragment fragment = new InviteFragment();
        Bundle bundle = getIntent().getExtras();
        fragment.setArguments(bundle);
        fragment.show(mFragmentManager, "InviteFragment");
    }


    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_CONFIG);
        HttpUtil.cancel(HttpUtil.CHECK_LIVE);
        HttpUtil.cancel(HttpUtil.ROOM_CHARGE);
        HttpUtil.cancel(HttpUtil.GET_BONUS);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        AppConfig.getInstance().setLaunched(false);
        AppConfig.getInstance().setConfig(null);
//        LocationUtil.getInstance().stopLocation();
    }

    /**
     * 主播 开启直播
     */
    private void startLive() {
        if (AppConfig.getInstance() == null || TextUtils.isEmpty(AppConfig.getInstance().getUid())) {
            AppConfig.getInstance().reset();
            getConfig();
            AppConfig.getInstance().getUserBean();
            return;
        }
        Intent intent = new Intent(mContext, LiveCameraActivity.class);
//        Intent intent = new Intent(mContext, AnchorCertificateActivity.class);
        startActivity(intent);
    }

    private LiveBean bean;

    /**
     * 观众 观看直播
     */
    public void watchLive(LiveBean bean) {
        this.bean = bean;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
            } else {
                forwardLiveActivity(bean);
            }
        } else {
            forwardLiveActivity(bean);
        }
    }

    private void forwardLiveActivity(LiveBean bean) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.setSelectLiveBean(bean);
        mCheckLivePresenter.watchLive();
    }

    //    /**
//     * 开启定位
//     */
//    private void startLocation() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
//            } else {
//                LocationUtil.getInstance().startLocation();
//            }
//        } else {
//            LocationUtil.getInstance().startLocation();
//        }
//    }

    public void getLiveAuth() {
        if (AppConfig.isUnlogin()) {
            unloginHint(mRootViewGroup);
            return;
        }
        HttpUtil.getLiveAuth(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && !TextUtils.isEmpty(Arrays.toString(info))) {
                    JSONObject object = JSON.parseObject(info[0]);
                    if (object.containsKey("errno") && object.getIntValue("errno") == 1001) {
                        startActivity(new Intent(MainActivity.this, AnchorCertificateActivity.class));
                        return;
                    }
                    if (object.containsKey("status")) {
                        int status = object.getIntValue("status");
                        if (status == 0) {
                            ToastUtil.show("资料审核中,请耐心等候");
                        } else if (status == 2) {
                            ToastUtil.show("资料审核失败,请重新提交资料");
                            startActivity(new Intent(MainActivity.this, AnchorCertificateActivity.class));
                        } else {
                            startLive();
                        }
                    }
                } else ToastUtil.show(msg);
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) {
            ToastUtil.show(getString(R.string.permission_refused));
            return;
        }
        switch (requestCode) {
//            case REQUEST_LOCATION_PERMISSION:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    LocationUtil.getInstance().startLocation();
//                } else {
//                    ToastUtil.show(getString(R.string.location_permission_refused));
//                }
//                break;
            case REQUEST_READ_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.show(getString(R.string.storage_permission_refused));
                } else {
                    if (bean != null)
                        forwardLiveActivity(bean);
                }
                break;
            case MOUNT_UNMOUNT_FILESYSTEMS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                } else {
                   checkVersion();
                }
                break;
        }
    }

    /**
     * 获取登录奖励
     */
    private void getLoginReward() {
        HttpUtil.getBonus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    int bonusDay = obj.getIntValue("bonus_day");
                    if (bonusDay > 0) {
                        List<RewardBean> list = JSON.parseArray(obj.getString("bonus_list"), RewardBean.class);
                        LoginRewardWindow window = new LoginRewardWindow(mContext, mRootViewGroup);
                        window.show(list, bonusDay - 1, () -> {
                            int[] p = new int[2];
                            mBtnMe.getLocationOnScreen(p);
                            return p;
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        ExitFragment fragment = new ExitFragment();
        fragment.show(mFragmentManager, "ExitFragment");
    }

    /**
     * 加载推送消息是否进入直播间
     */
    private void initJPushMsg() {
        if (getIntent() != null) {
            String roomId = getIntent().getStringExtra("roomId");
            String stream = getIntent().getStringExtra("stream");
            if (!TextUtils.isEmpty(roomId) && !TextUtils.isEmpty(stream)) {
                Log.e("//", "initJPushMsg: " + roomId);
                LiveBean liveBean = new LiveBean();
                liveBean.setUid(roomId);
                liveBean.setStream(stream);
                watchLive(liveBean);
            }
        }
        if (getIntent().getBundleExtra("jpusheventBundle") != null) {
            Bundle jpusheventBundle = getIntent().getBundleExtra("jpusheventBundle");
            LiveBean jPushEvent = jpusheventBundle.getParcelable("jpushevent");
            if (jPushEvent != null) {//判断是否是推送进入房间
                CheckLivePresenter mCheckLivePresenter = new CheckLivePresenter(mContext);
                mCheckLivePresenter.setSelectLiveBean(jPushEvent);
                mCheckLivePresenter.watchLive();
            }
        }
    }
}
