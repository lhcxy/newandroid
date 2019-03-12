package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.EMChatRoomActivity;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.UserLevelView;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.im.IMUtil;
import com.yunbao.phonelive.im.JIM;
import com.yunbao.phonelive.im.JIMUtil;
import com.yunbao.phonelive.ui.views.fragment.LiveChatFragment;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by cxf on 2017/9/21.
 * 直播间普通用户信息
 */

public class LiveUserFragment extends DialogFragment {

    private Context mContext;
    private View mRootView;
    private CompositeDisposable disposable;

    /**
     * @param userInfo 用户信息
     * @param type     0=用户  1=主播
     * @return
     */
    public static LiveUserFragment newInstance(UserBean userInfo, int type) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userInfo", userInfo);
        bundle.putInt("type", type);
        LiveUserFragment fragment = new LiveUserFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_live_user_info, null);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }

    private ImageView avatarIv;
    private TextView nameTv, uidTv;
    private UserLevelView levelView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        disposable = new CompositeDisposable();
        avatarIv = mRootView.findViewById(R.id.live_user_avatar_iv);
        nameTv = mRootView.findViewById(R.id.live_user_name_tv);
        levelView = mRootView.findViewById(R.id.live_user_level_ulv);
        uidTv = mRootView.findViewById(R.id.live_user_id_tv);
        if (getArguments() != null) {
            UserBean userInfo = getArguments().getParcelable("userInfo");
            if (userInfo != null) {
                int type = getArguments().getInt("type", 0);
                ImgLoader.displayCircleWhiteBorder(userInfo.getAvatar(), avatarIv);
                nameTv.setText(userInfo.getUser_nicename());
                uidTv.setText("uid：" + userInfo.getId());
                levelView.setLevel(userInfo.getLevel(), type);
                List<String> blackList = new ArrayList<>();
                blackList.add(JIMUtil.PREFIX + userInfo.getId());
                disposable.add(RxView.clicks(mRootView.findViewById(R.id.live_user_close_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> dismiss()));
                disposable.add(RxView.clicks(mRootView.findViewById(R.id.live_user_talk_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
//                    EMChatRoomFragment fragment = new EMChatRoomFragment();
//                    fragment.setIM(new JIM());
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("from", 1);
//                    bundle.putParcelable("touser", userInfo);
//                    bundle.putInt("isAttention", 0);
//                    fragment.setArguments(bundle);
//                    fragment.show(getChildFragmentManager(), "EMChatRoomFragment");

                    Intent intent = new Intent(mContext, EMChatRoomActivity.class);
                    intent.putExtra("from", 0);
                    intent.putExtra("touser", userInfo);
                    intent.putExtra("isAttention", 0);
                    startActivity(intent);

                    dismiss();
                }));
                disposable.add(RxView.clicks(mRootView.findViewById(R.id.live_user_black_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
                    JMessageClient.addUsersToBlacklist(blackList, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            ToastUtil.show("屏蔽用户私信成功!");
                            dismiss();
                        }
                    });
                }));
            } else dismiss();

        } else dismiss();
    }


}
