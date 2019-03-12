package com.yunbao.phonelive.ui.views.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.EMChatRoomActivity;
import com.yunbao.phonelive.activity.UserInfoActivity;
import com.yunbao.phonelive.bean.LiveUserBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.UserLevelView;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.LiveInfoListener;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LiveOwnerFragment extends BaseLazyFragment {


    private String mTouid = "";

    public static LiveOwnerFragment newInstance(String touid) {
        Bundle bundle = new Bundle();
        bundle.putString("touid", touid);
        LiveOwnerFragment fragment = new LiveOwnerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_live_owner;
    }

    private ImageView avatarIv;
    private TextView uNameTv, uIdTv, uMoondsTv, uFansTv, uTypeTv, uNoticeTv;
    private UserLevelView userLevelView;

    @Override
    protected void initView() {
        avatarIv = findView(R.id.anchor_avatar_iv);
        uNameTv = findView(R.id.anchor_name_tv);
        uIdTv = findView(R.id.anchor_id_tv);
        uMoondsTv = findView(R.id.anchor_moods_tv);
        uFansTv = findView(R.id.anchor_fans_tv);
        uTypeTv = findView(R.id.anchor_type_tv);
        uNoticeTv = findView(R.id.anchor_notice_tv);
        userLevelView = findView(R.id.user_level);

        disposable.add(RxView.clicks(findView(R.id.live_msg_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
                    if (bean != null) {
                        UserBean userBean = new UserBean();
                        userBean.setId(bean.getId());
                        userBean.setAvatar(bean.getAvatar());
                        userBean.setUser_nicename(bean.getUser_nicename());
                        UserBean.Liang liang = new UserBean.Liang();
                        liang.setName("");
//                        UserBean.Vip vip = new UserBean.Vip();
//                        vip.setType(0);
//                        userBean.setLiang(liang);
//                        userBean.setVip(vip);
//                        userBean.setLiang(new UserBean.Liang());
                        Intent intent = new Intent(getContext(), EMChatRoomActivity.class);
                        intent.putExtra("from", 0);
                        intent.putExtra("touser", userBean);
                        intent.putExtra("isAttention", 0);
                        startActivity(intent);
                    }
                }));
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            mTouid = getArguments().getString("touid", "");
        }
//        HttpUtil.getLiveInfo(mTouid, new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                JSONObject obj = JSON.parseObject(info[0]);
//                LiveUserBean bean = JSON.toJavaObject(obj, LiveUserBean.class);
//                if (bean != null) {
//                    ImgLoader.displayCircle(bean.getAvatar_thumb(), avatarIv);
//                    uNameTv.setText(bean.getUser_nicename());
//                    uIdTv.setText("主播ID:" + bean.getId());
//                    uFansTv.setText(bean.getFans());
//                    uNoticeTv.setText(bean.getRoom_notice());
//                    uTypeTv.setText("节目类型:");
//                    if (listener != null) {
//                        listener.getLiveInfo(bean);
//                    }
//                }
//
//            }
//        });
    }

    private LiveUserBean bean;

    @Override
    public void onResume() {
        super.onResume();
        if (bean != null) {
            if (avatarIv != null)
                ImgLoader.displayCircleWhiteBorder(bean.getAvatar_thumb(), avatarIv);
            if (uNameTv != null)
                uNameTv.setText(bean.getUser_nicename());
            if (null != uFansTv)
                uFansTv.setText(bean.getFans());
            if (null != uNoticeTv)
                uNoticeTv.setText(bean.getRoom_notice());
            if (null != uIdTv)
                uIdTv.setText("主播ID:" + bean.getId());
            if (null != uTypeTv)
                uTypeTv.setText("节目类型: \n" + bean.getLivetag());
            if (null != uMoondsTv)
                uMoondsTv.setText(bean.getFireNumStr());
            if (userLevelView != null) {
                userLevelView.setLevel(bean.getLevel_anchor());
            }
        }
    }

    public void setData(LiveUserBean bean) {
        if (bean != null) {
            this.bean = bean;
            if (avatarIv != null)
                ImgLoader.displayCircleWhiteBorder(bean.getAvatar_thumb(), avatarIv);
            if (uNameTv != null)
                uNameTv.setText(bean.getUser_nicename());
            if (null != uFansTv)
                uFansTv.setText(bean.getFans());
            if (null != uNoticeTv)
                uNoticeTv.setText(bean.getRoom_notice());
            if (null != uIdTv)
                uIdTv.setText("主播ID:" + bean.getId());
            if (null != uTypeTv)
                uTypeTv.setText("节目类型:");
            if (null != uMoondsTv)
                uMoondsTv.setText(bean.getFireNumStr());
            if (userLevelView != null) {
                userLevelView.setLevel(bean.getLevel_anchor());
            }
        }
    }

}
