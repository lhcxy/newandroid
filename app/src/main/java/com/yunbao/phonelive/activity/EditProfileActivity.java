package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.RefreshUserInfoEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.JIMUtil;
import com.yunbao.phonelive.interfaces.ChoseCallback;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by cxf on 2017/8/16.
 * 修改个人资料页面
 */

public class EditProfileActivity extends AbsActivity {

    private ImageView mHeadImg;
    private TextView mName;
    //    private TextView mSignature;
    private TextView mBirthday;
    private TextView mSex;
    private UserBean mUserBean;
    private final int SET_AVATAR = 100;
    private final int SET_NICKNAME = 200;
    private String mBirthdayVal;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void main() {
        setTitle(getString(R.string.edit_profile));
        mHeadImg = (ImageView) findViewById(R.id.headImg);
        mName = (TextView) findViewById(R.id.name);
        mBirthday = (TextView) findViewById(R.id.birthday);
        mSex = findViewById(R.id.sex);
        if (AppConfig.getInstance() != null && AppConfig.getInstance().getUserBean() != null) {
            mUserBean = AppConfig.getInstance().getUserBean();
        } else {
            return;
        }
        ImgLoader.displayCircle(mUserBean.getAvatar(), mHeadImg);
        mName.setText(mUserBean.getUser_nicename());
        mBirthday.setText(mUserBean.getBirthday());
        mSex.setText(mUserBean.getSex() == 1 ? "男" : "女");
    }


    public void editClick(View v) {
        switch (v.getId()) {
            case R.id.btn_head:
                setAvatar();
                break;
            case R.id.btn_name:
                setNickName();
                break;
            case R.id.btn_birthday:
                setBirthday();
                break;
            case R.id.btn_sex:
                setSex();
                break;
        }
    }

    private void setNickName() {
        Intent intent = new Intent(mContext, SetNickNameActivity.class);
        startActivityForResult(intent, SET_NICKNAME);
    }

    public static final int REQUEST_CODE = 0x01;

    private void setAvatar() {
        DialogUitl.chooseDialog(this, "上传头像", getResources().getString(R.string.album), getResources().getString(R.string.camera), new ChoseCallback() {
            @Override
            public void onChose(int choseIndex) {
                if (choseIndex == 0) {
                    PictureSelector.create(EditProfileActivity.this)
                            .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                            .imageSpanCount(4)// 每行显示个数 int
                            .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                            .previewImage(true)// 是否可预览图片 true or false
                            .previewVideo(false)// 是否可预览视频 true or false
                            .enablePreviewAudio(false) // 是否可播放音频 true or false
                            .isCamera(false)// 是否显示拍照按钮 true or false
                            .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                            .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                            .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                            .enableCrop(true)// 是否裁剪 true or false
                            .compress(false)// 是否压缩 true or false
                            .forResult(REQUEST_CODE);//结果回调onActivityResult code
                } else {
                    PictureSelector.create(EditProfileActivity.this)
                            .openCamera(PictureMimeType.ofImage())
                            .forResult(REQUEST_CODE);
                }
            }
        }).show();
    }

    private void setSex() {
        DialogUitl.chooseDialog(this, "选择性别", "男", "女", new ChoseCallback() {
            @Override
            public void onChose(int choseIndex) {
                int sex = choseIndex + 1;
                HttpUtil.updateFields("{\"sex\":\"" + sex + "\"}", new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        ToastUtil.show(obj.getString("msg"));
                        if (code == 0) {
                            onSetSexResult(sex);
                        }
                    }
                });
            }
        }).show();
    }

    private void setBirthday() {
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 1);
        Calendar endDate = Calendar.getInstance();
        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(EditProfileActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mBirthdayVal = DateFormat.format("yyyy-MM-dd", date.getTime()).toString();
                updateBirthday();
            }
        })
                .setTitleSize(16)//标题文字大小
                .setTitleText("出生日期")//标题文字
                .setContentTextSize(22)//滚轮文字大小
                .setTitleColor(getResources().getColor(R.color.text_color_333))//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.text_color_333))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.text_color_333))//取消按钮文字颜色
                .setRangDate(startDate, endDate)
                .setDate(endDate)
                .isDialog(true)
                .build();

        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
        pvTime.show();
    }

    /**
     * 修改生日
     */
    private void updateBirthday() {
        HttpUtil.updateFields("{\"birthday\":\"" + mBirthdayVal + "\"}", mCallback);
    }

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                mBirthday.setText(mBirthdayVal);
                AppConfig.getInstance().getUserBean().setBirthday(mBirthdayVal);
            } else {
                ToastUtil.show(msg);
            }
        }
    };
    private LocalMedia headerImg;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SET_AVATAR:
                    onSetAvatarResult(data);
                    break;
                case SET_NICKNAME:
                    onSetNameResult(data);
                    break;
                case REQUEST_CODE:
                    List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
                    if (result != null && result.size() > 0) {
                        headerImg = result.get(0);
                        updateAvatar();
                    }
                    break;
            }
        } else {
            switch (requestCode) {
                case SET_AVATAR:
                    ToastUtil.show(getString(R.string.cancel_set_avatar));
                    break;
                case SET_NICKNAME:
                    ToastUtil.show(getString(R.string.cancel_set_nickname));
                    break;
            }
        }
    }


    private void onSetAvatarResult(Intent data) {
        ImgLoader.displayCircle(new File(data.getStringExtra("path")), mHeadImg);
        AppConfig.getInstance().getUserBean().setAvatar(data.getStringExtra("avatar"));
        AppConfig.getInstance().getUserBean().setAvatar_thumb(data.getStringExtra("avatar_thumb"));
        ToastUtil.show(getString(R.string.success_set_avatar));
        EventBus.getDefault().post(new RefreshUserInfoEvent());
    }

    private void onSetSexResult(int sex) {
        mSex.setText(sex == 1 ? "男" : "女");
        AppConfig.getInstance().getUserBean().setSex(sex);
        EventBus.getDefault().post(new RefreshUserInfoEvent());
    }

    private void onSetNameResult(Intent data) {
        String name = data.getStringExtra("name");
        mName.setText(name);
        AppConfig.getInstance().getUserBean().setUser_nicename(name);
        EventBus.getDefault().post(new RefreshUserInfoEvent());
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.UPDATE_FIELDS);
        super.onDestroy();
    }

    private void updateAvatar() {
        if (headerImg != null) {
            File file = new File(headerImg.getPath());
            JIMUtil.getInstance().updateAvatar(file);
            HttpUtil.updateAvatar(file, new HttpCallback() {

                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        String mNewAvatar = obj.getString("avatar");
                        String mNewAvatarThumb = obj.getString("avatar_thumb");
                        ImgLoader.displayCircle(mNewAvatar, mHeadImg);
                        if (!TextUtils.isEmpty(mNewAvatar)) {
                            AppConfig.getInstance().getUserBean().setAvatar(mNewAvatar);
                        }
                        if (!TextUtils.isEmpty(mNewAvatarThumb)) {
                            AppConfig.getInstance().getUserBean().setAvatar_thumb(mNewAvatarThumb);
                        }
                        EventBus.getDefault().post(new RefreshUserInfoEvent());
                        ToastUtil.show("头像更新成功!");
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
    }
}
