package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.fragment.ChooseImgFragment;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.JIM;
import com.yunbao.phonelive.im.JIMUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.DialogUitl;

import java.io.File;

/**
 * Created by cxf on 2017/8/16.
 * 设头像的activity
 */

public class SetAvatarActivity extends AbsActivity {

    private ImageView mHeadImg;
    private String mNewAvatar = "";
    private String mNewAvatarThumb = "";
    private ChooseImgFragment mFragment;
    private File mTempFile;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_avatar;
    }

    @Override
    protected void main() {
        mFragment = new ChooseImgFragment();
        mFragment.setOnCompleted(new CommonCallback<File>() {
            @Override
            public void callback(File file) {
                mTempFile = file;
                JIMUtil.getInstance().updateAvatar(mTempFile);
                HttpUtil.updateAvatar(mTempFile, mCallback);
            }
        });
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(mFragment, "ChooseImgFragment").commit();
        setTitle(getString(R.string.select_head_img));
        String avatar = getIntent().getStringExtra("avatar");
        mHeadImg = (ImageView) findViewById(R.id.headImg);
        ImgLoader.displaySource(avatar, mHeadImg);
    }

    public void selectAvatarClick(View v) {
        switch (v.getId()) {
            case R.id.btn_album:
                mFragment.forwardAlumb();
                break;
            case R.id.btn_photo:
                mFragment.forwardCamera();
                break;
        }
    }

    HttpCallback mCallback = new HttpCallback() {

        @Override
        public void onSuccess(int code, String msg, String[] info) {

            ImgLoader.display(mTempFile, mHeadImg);
            JSONObject obj = JSON.parseObject(info[0]);
            mNewAvatar = obj.getString("avatar");
            mNewAvatarThumb = obj.getString("avatar_thumb");
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUitl.loadingDialog(mContext);
        }
    };

    @Override
    public void onBackPressed() {
        if (!"".equals(mNewAvatar) && !"".equals(mNewAvatarThumb)) {
            Intent intent = new Intent();
            intent.putExtra("path", mTempFile.getAbsolutePath());
            intent.putExtra("avatar", mNewAvatar);
            intent.putExtra("avatar_thumb", mNewAvatarThumb);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.UPDATE_AVATAR);
        super.onDestroy();

    }
}