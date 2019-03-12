package com.yunbao.phonelive.ui.views.viewholder;

import android.app.Dialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.UserLevelView;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.JIMUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ToastUtil;

public class ChatRoomSettingActivity extends AbsActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_room_setting;
    }

    UserBean userInfo;
    private ImageView avatarIv;
    private TextView nameTv;
    private UserLevelView userLevelView;
    private SwitchCompat switchCompat;

    @Override
    protected void main() {
        if (getIntent() != null) {
            userInfo = getIntent().getParcelableExtra("userInfo");
            if (userInfo == null) {
                finish();
            }
        } else finish();

        avatarIv = findViewById(R.id.chat_room_avatar_iv);
        nameTv = findViewById(R.id.chat_room_name_tv);
        userLevelView = findViewById(R.id.chat_room_level_ulv);
        switchCompat = findViewById(R.id.chat_room_black_switch);
        ImgLoader.displayCircleWhiteBorder(userInfo.getAvatar(), avatarIv);
        nameTv.setText(userInfo.getUser_nicename());
        userLevelView.setLevel(userInfo.getLevel());
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.e("//", "main: setOnCheckedChangeListener");
            if (!buttonView.isPressed()) {
                return;
            }

            HttpUtil.setBlack(userInfo.getId(), pullBlackCallback);
        });

        HttpUtil.checkBlack(userInfo.getId(), mCallback);
    }

    private Dialog mLoadingDialog;
    private HttpCallback pullBlackCallback = new HttpCallback() {

        @Override
        public void onSuccess(int code, String msg, String[] info) {
            int res = JSON.parseObject(info[0]).getIntValue("isblack");
            if (res == 1) {//已拉黑
                //拉黑的时候把关注取消
                switchCompat.setChecked(true);
                JIMUtil.getInstance().ignoreUnReadMessage();
                JIMUtil.getInstance().addBlackList(userInfo.getId());
                ToastUtil.show(getString(R.string.pull_black_success));
            } else if (res == 0) {//解除拉黑
                switchCompat.setChecked(false);
                ToastUtil.show(getString(R.string.no_pull_black));
                JIMUtil.getInstance().removeBlackList(userInfo.getId());
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

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
//                    int t2u = JSON.parseObject(info[0]).getIntValue("t2u");
                    int t2u = JSON.parseObject(info[0]).getIntValue("u2t");
                    if (1 == t2u) {
                        switchCompat.setChecked(true);
                        JIMUtil.getInstance().addBlackList(userInfo.getId());
//                        ToastUtil.show(getString(R.string.you_are_blacked));
                    } else {
                        switchCompat.setChecked(false);
                        JIMUtil.getInstance().removeBlackList(userInfo.getId());
                    }
                }
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
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUitl.loadingDialog(mContext);
            }
            return mLoadingDialog;
        }
    };
}
