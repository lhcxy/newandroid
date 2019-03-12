package com.yunbao.phonelive.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.fragment.LiveRecordFragment;
import com.yunbao.phonelive.fragment.UserInfoHomeFragment;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.presenter.CheckLivePresenter;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.IconUitl;
import com.yunbao.phonelive.utils.ToastUtil;

/**
 * Created by cxf on 2017/8/11.
 * 他人主页
 */

public class UserInfoActivity extends AbsActivity {

    private static final int REQUEST_READ_PERMISSION = 101;//请求文件读写权限的请求码
    private String mTouid;
    private UserBean mToUser;
    private ImageView mHeadImg;
    private View mBtnLiving;
    private TextView mName;
    private ImageView mSex;
    private ImageView mAnchorLevel;
    private ImageView mLevel;
    private TextView mFollow;
    private TextView mFans;
    private TextView mSignature;
    private int mSexVal;
    private int mIsAttention;
    private TextView mAttentionText;
    private TextView mBlackText;
    private Dialog mLoadingDialog;
    private View mBottom;

    private FragmentManager mFragmentManager;
    private UserInfoHomeFragment mHomeFragment;
    private LiveRecordFragment mLiveRecordFragment;
    private LiveBean mLiveBean;
    private CheckLivePresenter mCheckLivePresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_userinfo;
    }

    @Override
    protected void main() {
        mTouid = getIntent().getStringExtra("touid");
        initView();
        initData();
    }

    private void initView() {
        mHeadImg = (ImageView) findViewById(R.id.headImg);
        mBtnLiving = findViewById(R.id.btn_living);
        mName = (TextView) findViewById(R.id.name);
        mSex = (ImageView) findViewById(R.id.sex);
        mAnchorLevel = (ImageView) findViewById(R.id.anchor_level);
        mLevel = (ImageView) findViewById(R.id.user_level);
        mFollow = (TextView) findViewById(R.id.follows);
        mFans = (TextView) findViewById(R.id.fans);
        mSignature = (TextView) findViewById(R.id.signature);
        mAttentionText = (TextView) findViewById(R.id.btn_attention);
        mBlackText = (TextView) findViewById(R.id.btn_pull_black);
        mBottom = findViewById(R.id.bottom);
        if (mTouid.equals(AppConfig.getInstance().getUid())) {
            mBottom.setVisibility(View.GONE);
        }
        mFragmentManager = getSupportFragmentManager();
    }

    private void initData() {
        HttpUtil.getUserHome(mTouid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                UserBean bean = JSON.toJavaObject(obj, UserBean.class);
                mToUser = bean;
                ImgLoader.display(bean.getAvatar(), mHeadImg);
                mName.setText(bean.getUser_nicename());
                mSexVal = bean.getSex();
                mSex.setImageResource(IconUitl.getSexDrawable(mSexVal));
                mAnchorLevel.setImageResource(IconUitl.getAnchorLiveDrawable(bean.getLevel_anchor()));
                mLevel.setImageResource(IconUitl.getAudienceDrawable(bean.getLevel()));
                mFollow.setText(getString(R.string.attention2) + "：" + bean.getFollows());
                mFans.setText(getString(R.string.fans) + "：" + bean.getFans());
                mSignature.setText(bean.getSignature());
                mIsAttention = obj.getIntValue("isattention");
                mAttentionText.setText(mIsAttention == 1 ? getString(R.string.attention) : getString(R.string.no_attention));
                mBlackText.setText(obj.getIntValue("isblack") == 1 ? getString(R.string.no_pull_black) : getString(R.string.pull_black));

                if (obj.getIntValue("islive") == 1) {
                    mLiveBean = JSON.parseObject(obj.getString("liveinfo"), LiveBean.class);
                    mBtnLiving.setVisibility(View.VISIBLE);
                }

                //添加fragment
                mHomeFragment = new UserInfoHomeFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", bean);
                bundle.putString("contribute", obj.getString("contribute"));
                mHomeFragment.setArguments(bundle);

                mLiveRecordFragment = new LiveRecordFragment();
                bundle = new Bundle();
                bundle.putString("liverecord", obj.getString("liverecord"));
                mLiveRecordFragment.setArguments(bundle);
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.add(R.id.replaced, mHomeFragment);
                ft.add(R.id.replaced, mLiveRecordFragment);
                ft.show(mHomeFragment).hide(mLiveRecordFragment);
                ft.commit();
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                if (mLoadingDialog == null) {
                    mLoadingDialog = DialogUitl.loadingDialog(mContext);
                }
                return mLoadingDialog;
            }
        });
    }

    public void userInfoClick(View v) {
        switch (v.getId()) {
            case R.id.follows:
                forwardAttention("0");
                break;
            case R.id.fans:
                forwardAttention("1");
                break;
            case R.id.btn_home:
                toggleFragment(R.id.btn_home);
                break;
            case R.id.btn_live:
                toggleFragment(R.id.btn_live);
                break;
            case R.id.btn_private_message://私信
                Intent intent = new Intent(mContext, EMChatRoomActivity.class);
                intent.putExtra("from", 0);
                intent.putExtra("touser", mToUser);
                intent.putExtra("isAttention", mIsAttention);
                startActivity(intent);
//                Intent intent = new Intent(mContext, EMChatRoomActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("from", 1);
//                bundle.putParcelable("touser", mToUser);
//                bundle.putInt("isAttention", mIsAttention);
//                intent.putExtra("data", bundle);
//                Log.e("//mUser", "initData: openChatRoom" + bundle);
//                startActivity(intent);
                break;
            case R.id.btn_attention://关注
                HttpUtil.setAttention(mTouid, attentionCallback);
                break;
            case R.id.btn_pull_black://拉黑
                HttpUtil.setBlack(mTouid, pullBlackCallback);
                break;
            case R.id.order://排行榜
                Intent intent2 = new Intent(mContext, OrderActivity.class);
                intent2.putExtra("touid", mTouid);
                intent2.putExtra("type", 2);
                startActivity(intent2);
                break;
            case R.id.btn_living://观看直播
                watchLive();
                break;
        }
    }

    private void watchLive() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
            } else {
                forwardLiveActivity();
            }
        } else {
            forwardLiveActivity();
        }
    }

    private void forwardLiveActivity() {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.setSelectLiveBean(mLiveBean);
        mCheckLivePresenter.watchLive();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.show(getString(R.string.storage_permission_refused));
                } else {
                    forwardLiveActivity();
                }
                break;
        }
    }


    private void forwardAttention(String type) {
        Intent intent = new Intent(mContext, AttentionActivity.class);
        intent.putExtra("touid", mTouid);
        intent.putExtra("type", type);
        intent.putExtra("sex", mSexVal);
        startActivity(intent);
    }

    private void toggleFragment(int id) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (id == R.id.btn_home) {
            ft.show(mHomeFragment).hide(mLiveRecordFragment);
        } else {
            ft.show(mLiveRecordFragment).hide(mHomeFragment);
        }
        ft.commit();
    }


    private CommonCallback<Integer> attentionCallback = new CommonCallback<Integer>() {
        @Override
        public void callback(Integer res) {
            mIsAttention = res;
            if (mIsAttention == 1) {//已关注
                mAttentionText.setText(getString(R.string.attention));
                //关注的时候把拉黑取消
                mBlackText.setText(getString(R.string.pull_black));
            } else if (mIsAttention == 0) {//未关注
                mAttentionText.setText(getString(R.string.no_attention));
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUitl.loadingDialog(mContext);
            }
            return mLoadingDialog;
        }
    };

    private HttpCallback pullBlackCallback = new HttpCallback() {

        @Override
        public void onSuccess(int code, String msg, String[] info) {
            int res = JSON.parseObject(info[0]).getIntValue("isblack");
            if (res == 1) {//已拉黑
                mBlackText.setText(getString(R.string.no_pull_black));
                //拉黑的时候把关注取消
                mAttentionText.setText(getString(R.string.no_attention));
                ToastUtil.show(getString(R.string.pull_black_success));
            } else if (res == 0) {//解除拉黑
                mBlackText.setText(getString(R.string.pull_black));
                ToastUtil.show(getString(R.string.no_pull_black));
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUitl.loadingDialog(mContext);
            }
            return mLoadingDialog;
        }
    };


    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.SET_ATTENTION);
        HttpUtil.cancel(HttpUtil.GET_USER_HOME);
        HttpUtil.cancel(HttpUtil.SET_BLACK);
        HttpUtil.cancel(HttpUtil.CHECK_LIVE);
        HttpUtil.cancel(HttpUtil.ROOM_CHARGE);
        super.onDestroy();

    }
}
