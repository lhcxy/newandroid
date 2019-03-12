package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.EMChatRoomActivity;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.bean.LiveUserBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.UserLevelView;
import com.yunbao.phonelive.event.LiveSettingCloseEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.views.LiveReportActivity;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2017/8/25.
 */

public class LiveUserInfoFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mID;
    private TextView mFans, fireNum, focusNum, descTv, levelTv, levelHintTv;
    private TextView mBtnAttention;
    //    private View mBtnSetting;
    private View mBtnReport;
    private int mAction;
    private boolean mIsAttention;
    private LiveUserBean data;
    private UserLevelView userLevelView;
    private ProgressBar levelPb;

    public static LiveUserInfoFragment newInstance(LiveUserBean data, boolean b) {

        Bundle args = new Bundle();
        args.putSerializable("data", data);
        args.putBoolean("isAttention", b);
        LiveUserInfoFragment fragment = new LiveUserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_live_user, null, false);
        dialog.setContentView(mRootView);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        mAvatar = (ImageView) mRootView.findViewById(R.id.avatar);
        levelTv = (TextView) mRootView.findViewById(R.id.level_tv);
        levelHintTv = (TextView) mRootView.findViewById(R.id.level_hint_tv);
        mName = (TextView) mRootView.findViewById(R.id.name);
        mID = (TextView) mRootView.findViewById(R.id.id_value);
        mFans = (TextView) mRootView.findViewById(R.id.fans);
        fireNum = (TextView) mRootView.findViewById(R.id.fire_num_tv);
        focusNum = (TextView) mRootView.findViewById(R.id.focus_num_tv);
        mBtnAttention = (TextView) mRootView.findViewById(R.id.btn_attention);
        descTv = (TextView) mRootView.findViewById(R.id.liver_desc_tv);
        userLevelView = mRootView.findViewById(R.id.user_level);
        mBtnReport = mRootView.findViewById(R.id.btn_report);
        levelPb = mRootView.findViewById(R.id.progress_pb);
        mBtnAttention.setOnClickListener(this);
        mBtnReport.setOnClickListener(this);
        mRootView.findViewById(R.id.btn_msg).setOnClickListener(this::onClick);
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            data = (LiveUserBean) bundle.getSerializable("data");
            mIsAttention = bundle.getBoolean("isAttention");
            initData();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initData() {
        if (isAdded()) {
            ImgLoader.displayCircle(data.getAvatar_thumb(), mAvatar);
            mName.setText(data.getUser_nicename());
            mID.setText("探球直播ID:" + data.getId());
            fireNum.setText(data.getFireNumStr());
            focusNum.setText(data.getFans());
            descTv.setText(data.getRoom_notice());
            userLevelView.setLevel(data.getLevel_anchor(), 1);
            levelPb.setProgress((int) data.getExp_anchor());
            levelTv.setText("Lv." + data.getLevel_anchor());
            ForegroundColorSpan giftNameSp = new ForegroundColorSpan(getResources().getColor(R.color.app_selected_color));
            SpannableString spannableString = new SpannableString(String.format(getResources().getString(R.string.anchor_exp_up_hint), data.getExp_up()));
            spannableString.setSpan(giftNameSp, 2, String.valueOf(data.getExp_up()).length() + 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            levelHintTv.setText(spannableString);
            setAttentionView();
        }
//        if (data. == 1) {
//            mBtnAttention.setText(getString(R.string.attention));
//            SocketUtil.getInstance().sendSystemMessage(AppConfig.getInstance().getUserBean().getUser_nicename() + getString(R.string.attention_anchor));
//        } else if (isAttention == 0) {
//            mBtnAttention.setText(getString(R.string.attention2));
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_attention:
                setAttention();
                break;
//            case R.id.btn_home:
//                forwardHomePage();
//                break;
            case R.id.btn_report:
                report();
                break;
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_msg:
                toMsgView();
                break;
        }
    }

    private void toMsgView() {
        UserBean userBean = new UserBean();
        userBean.setId(data.getId());
        userBean.setAvatar(data.getAvatar());
        userBean.setUser_nicename(data.getUser_nicename());
//        UserBean.Liang liang = new UserBean.Liang();
//        liang.setName("");
//        UserBean.Vip vip = new UserBean.Vip();
//        vip.setType(0);
//        userBean.setLiang(liang);
//        userBean.setVip(vip);
//        userBean.setLiang(new UserBean.Liang());
        Intent intent = new Intent(getContext(), EMChatRoomActivity.class);
        intent.putExtra("from", 0);
        intent.putExtra("touser", userBean);
        intent.putExtra("isAttention", 0);
        startActivity(intent);
    }


    private void setAttention() {
        HttpUtil.setAttention(data.getId(), mAttentionCallback);
    }

    private CommonCallback<Integer> mAttentionCallback = new CommonCallback<Integer>() {
        @Override
        public void callback(Integer isAttention) {
            mIsAttention = isAttention == 1;
            if (getActivity() != null && getActivity() instanceof LiveAudienceActivity) {
                if (mIsAttention && ((LiveAudienceActivity) getActivity()).isShowAttentionMsg == 0) {
                    SocketUtil.getInstance().sendSystemMessage(AppConfig.getInstance().getUserBean().getUser_nicename() + getString(R.string.attention_anchor));
                    ((LiveAudienceActivity) getActivity()).isShowAttentionMsg = 1;
                }
                ((LiveAudienceActivity) getActivity()).mIsAttention = isAttention;
            }
            setAttentionView();
        }
    };

    private void setAttentionView() {
        if (isAdded()) {
            if (mIsAttention) {
                mBtnAttention.setText(getString(R.string.attention));

                mBtnAttention.setBackground(getResources().getDrawable(R.drawable.bg_full_white_circle_line_0b56));
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_liver_focused);
                mBtnAttention.setTextColor(getResources().getColor(R.color.app_selected_color));
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mBtnAttention.setCompoundDrawables(drawable, null, null, null);
            } else {
                mBtnAttention.setText(getString(R.string.attention2));
                mBtnAttention.setBackground(getResources().getDrawable(R.drawable.bg_circle_ff6490));
                mBtnAttention.setTextColor(getResources().getColor(R.color.white));
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_liver_unfocused);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mBtnAttention.setCompoundDrawables(drawable, null, null, null);
            }
        }
    }

//    private void forwardHomePage() {
//        dismiss();
//        Intent intent = new Intent(mContext, UserInfoActivity.class);
//        intent.putExtra("touid", mTouid);
//        mContext.startActivity(intent);
//    }

//    private void openMsgWindow() {
//        if (mUserBean != null) {
//            dismiss();
//            ((LiveActivity) mContext).openChatRoomWindow(mUserBean, mIsAttention);
//        }
//    }
//
//    private void openSettingWindow() {
//        if (mUserBean != null) {
//            ((LiveActivity) mContext).openSettingWindow(mAction, mUserBean);
//        }
//    }

    private void report() {

        if (AppConfig.isUnlogin()) {
            ToastUtil.showUnLoginHint();
            dismiss();
            return;
        }

        if (data != null) {
            dismiss();
            Intent intent = new Intent(getContext(), LiveReportActivity.class);
            intent.putExtra("liveId", data.getId());
            intent.putExtra("roomId", data.getId());
            intent.putExtra("roomName", data.getRoom_title());
            startActivity(intent);
        }
    }


    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.SET_ATTENTION);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingDismiss(LiveSettingCloseEvent e) {
        dismiss();
    }
}
