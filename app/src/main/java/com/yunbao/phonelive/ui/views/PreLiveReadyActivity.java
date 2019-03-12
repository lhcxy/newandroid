package com.yunbao.phonelive.ui.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.activity.LiveAnchorActivity;
import com.yunbao.phonelive.adapter.SharedSdkAdapter;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.SharedSdkBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.SharedSdkUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import cn.sharesdk.framework.Platform;
import io.reactivex.disposables.CompositeDisposable;

public class PreLiveReadyActivity extends AbsActivity {
    private ImageView titleClearIv, avatarIv, bgIv;
    private EditText titleEt;
    private UserBean userBean;
    private TextView uNameTv, liveTypeTv;
    private RecyclerView shareRv;
    private SharedSdkAdapter mAdapter;
    private ViewGroup mContent;
    String liveTypeId;
    private View mDecorView;
    private int screenType = 0;  //0 =竖屏  1 = 横屏

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_prelive_ready;
    }

    @Override
    protected void main() {
        mDecorView = getWindow().getDecorView();
        EventBus.getDefault().register(this);

        initView();
        initClick();
        initData();
    }

    private void initData() {
        userBean = AppConfig.getInstance().getUserBean();
        if (userBean != null) {
            ImgLoader.displayCircleOrangeBorder(userBean.getAvatar(), avatarIv);
            uNameTv.setText(userBean.getUser_nicename());
            ImgLoader.displayBlur(userBean.getAvatar_thumb() + "?imageView2/1/w/100/h/100", bgIv);
        }
    }


    private void initView() {
        titleEt = findViewById(R.id.prelive_title_et);
        titleClearIv = findViewById(R.id.prelive_clear_iv);
        avatarIv = findViewById(R.id.prelive_avatar_iv);
        uNameTv = findViewById(R.id.prelive_uname_tv);
        shareRv = findViewById(R.id.prelive_share_rv);
//        liveTypeTv = findViewById(R.id.prelive_type_tv);
//        mContent = findViewById(R.id.root_rl);
        shareRv.setHasFixedSize(true);
        shareRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new SharedSdkAdapter(AppConfig.getInstance().getConfig().getShare_type(), true, true);
        shareRv.setAdapter(mAdapter);

    }

    private void initClick() {
        disposable.add(RxTextView.afterTextChangeEvents(titleEt).subscribe(textViewAfterTextChangeEvent -> {
            if (textViewAfterTextChangeEvent.editable().length() > 0) {
                titleClearIv.setVisibility(View.VISIBLE);
            } else {
                titleClearIv.setVisibility(View.INVISIBLE);
            }
        }));
        disposable.add(RxView.clicks(titleClearIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> titleEt.setText("")));
        disposable.add(RxView.clicks(findViewById(R.id.prelive_close_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> finish()));

//        RxView.clicks(findViewById(R.id.prelive_type_ll)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
//                .subscribe(o -> {
//                    LiveTypeDialogFragment.newInstance().showNow(getSupportFragmentManager(), "liveType");
//                });

//        RxView.clicks(findViewById(R.id.prelive_start_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
//                .subscribe(o -> startLive());
    }

    /**
     * 开始直播前的检查
     */
    private void startLive() {
        if (titleEt.getText().toString().trim().length() <= 0) {
            ToastUtil.show("请输入直播间名称");
            return;
        }
        if (TextUtils.isEmpty(liveTypeId)) {
            ToastUtil.show("请选择直播类型");
            return;
        }

        String type = mAdapter.getType();
        if (type != null) {
            ConfigBean bean = AppConfig.getInstance().getConfig();
            if (userBean != null && bean != null) {
//                String url = bean.getApp_android();
//                if (type.equals(SharedSdkBean.WX) || type.equals(SharedSdkBean.WX_PYQ)) {
                String url = bean.getWx_siteurl() + userBean.getId();
//                }
                SharedSdkUitl.getInstance().share(type,
                        bean.getShare_title(),
                        userBean.getUser_nicename() + bean.getShare_des(),
                        userBean.getAvatar(),
                        url,
                        mShareListener);
            }
        } else {
            checkPermissions();
        }
    }

    //分享的回调
    private SharedSdkUitl.ShareListener mShareListener = new SharedSdkUitl.ShareListener() {
        @Override
        public void onSuccess(Platform platform) {
            ToastUtil.show(getString(R.string.share_success));
            if (!AppConfig.isUnlogin()) {
                HttpUtil.onFinishShare();
            }
            checkPermissions();
        }

        @Override
        public void onError(Platform platform) {
            ToastUtil.show(getString(R.string.share_error));
            checkPermissions();
        }

        @Override
        public void onCancel(Platform platform) {
            ToastUtil.show(getString(R.string.share_cancel));
            checkPermissions();
        }
    };


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        REQUEST_LIVE_PERMISSION);
            } else {
                live();
            }
        } else {
            live();
        }
    }


    private final int REQUEST_LIVE_PERMISSION = 203;//同时请求摄像头和录音权限的请求码，用在开播的场景

    private void live() {
        String title = titleEt.getText().toString().trim();
        String mLiveType = "8";
        HttpUtil.createRoomByTag(title, mLiveType, screenType, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    Intent intent = new Intent(mContext, LiveAnchorActivity.class);
                    intent.putExtra("data", info[0]);
                    intent.putExtra("type", mLiveType);
//                    L.e("data------>" + info[0]);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtil.show(msg);
                }
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

    /**
     * 直播类型选择
     */
    private void showLiveTypePop() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LIVE_PERMISSION:
                if (isAllGranted(permissions, grantResults)) {
                    live();
                }
                break;
        }
    }

    //判断申请的权限有没有被允许
    private boolean isAllGranted(String[] permissions, int[] grantResults) {
        boolean isAllGranted = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                showTip(permissions[i]);
            }
        }
        return isAllGranted;
    }

    //拒绝某项权限时候的提示
    private void showTip(String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                ToastUtil.show(getString(R.string.storage_permission_refused));
                break;
            case Manifest.permission.CAMERA:
                ToastUtil.show(getString(R.string.camera_permission_refused));
                break;
            case Manifest.permission.RECORD_AUDIO:
                ToastUtil.show(getString(R.string.record_audio_permission_refused));
                break;
        }
    }
}
