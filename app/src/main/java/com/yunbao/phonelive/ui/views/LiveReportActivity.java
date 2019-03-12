package com.yunbao.phonelive.ui.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.ChoseCallback;
import com.yunbao.phonelive.ui.dialog.ImageChooseDF;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LiveReportActivity extends AbsActivity {
    private TextView roomIdTv, nickNameTv, contentCountTv;
    private EditText contentEt, qqNumEt;
    private ImageView picIv;
    private RadioGroup rgOne, rgTwo;
    private RadioGroup.OnCheckedChangeListener oneChangeListener, towChangeListener;
    private RadioButton eroticismRb, violenceRb, gambleRb, otherRb;

    private String reportType = "色情低俗";
    private ImageChooseDF imageChooseDF;
    private Uri imageUri;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_live_report;
    }

    @Override
    protected void main() {
        qqNumEt = findViewById(R.id.report_qq_num_et);
        contentEt = findViewById(R.id.report_reason_content_et);
        picIv = findViewById(R.id.report_pic_iv);
        nickNameTv = findViewById(R.id.report_nickname_tv);
        roomIdTv = findViewById(R.id.report_reason_room_id_tv);
        contentCountTv = findViewById(R.id.report_reason_content_count_tv);
        eroticismRb = findViewById(R.id.report_reason_eroticism_rb);
        violenceRb = findViewById(R.id.report_reason_violence_rb);
        gambleRb = findViewById(R.id.report_reason_gamble_rb);
        otherRb = findViewById(R.id.report_reason_other_rb);
        rgOne = findViewById(R.id.report_reason_rg_one);
        rgTwo = findViewById(R.id.report_reason_rg_two);
        setListener();
        setData();
    }


    private void setListener() {
        disposable.add(RxView.clicks(findViewById(R.id.live_report_back_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> finish()));
        oneChangeListener = (group, checkedId) -> {
            switch (checkedId) {
                case R.id.report_reason_eroticism_rb:
                    if (eroticismRb.isChecked())
                        rgTwo.clearCheck();
                    reportType = "色情低俗";
                    break;
                case R.id.report_reason_violence_rb:
                    if (violenceRb.isChecked())
                        rgTwo.clearCheck();
                    reportType = "政治暴力";
                    break;
                case R.id.report_reason_gamble_rb:
                    if (gambleRb.isChecked())
                        rgTwo.clearCheck();
                    reportType = "赌博";
                    break;
            }

        };
        towChangeListener = (group, checkedId) -> {
            switch (checkedId) {
                case R.id.report_reason_other_rb:
                    if (otherRb.isChecked())
                        rgOne.clearCheck();
                    reportType = "其他";
                    break;
            }

        };
        rgOne.setOnCheckedChangeListener(oneChangeListener);
        rgTwo.setOnCheckedChangeListener(towChangeListener);
        disposable.add(RxTextView.afterTextChangeEvents(contentEt).subscribe(textViewAfterTextChangeEvent ->
                contentCountTv.setText(String.format(getResources().getString(R.string.txt_report_content_count), 200 - textViewAfterTextChangeEvent.editable().length()))));

        disposable.add(RxView.clicks(findViewById(R.id.live_report_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> submitReport()));

        disposable.add(RxView.clicks(findViewById(R.id.report_pic_rl)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> chooseImg()));
    }


    private String roomName, liveId, roomId;

    private void setData() {
        if (getIntent() != null) {
            ForegroundColorSpan textColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.mainBodyTextColor));
            liveId = getIntent().getStringExtra("liveId");
            roomId = getIntent().getStringExtra("roomId");
            roomName = getIntent().getStringExtra("roomName");


            nickNameTv.setText("昵称:\t\t" + AppConfig.getInstance().getUserBean().getUser_nicename());
            SpannableString nickStr = new SpannableString("昵称:\t\t" + AppConfig.getInstance().getUserBean().getUser_nicename());
            nickStr.setSpan(textColorSpan, nickStr.length() - AppConfig.getInstance().getUserBean().getUser_nicename().length(), nickStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            nickNameTv.setText(nickStr);
            SpannableString roomIdStr = new SpannableString("房间ID:\t\t" + roomId);
            roomIdStr.setSpan(textColorSpan, roomIdStr.length() - roomId.length(), roomIdStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            roomIdTv.setText(roomIdStr);
        } else finish();


    }


    private void submitReport() {
        if (TextUtils.isEmpty(contentEt.getText().toString().trim())) {
            ToastUtil.show("请输入举报内容");
            return;
        }
        if (TextUtils.isEmpty(qqNumEt.getText().toString().trim())) {
            ToastUtil.show("请输入QQ号码");
            return;
        }
        uploadImage();

    }

    /**
     * 现在图片
     */
    private void chooseImg() {
        imageChooseDF = ImageChooseDF.newInstance();
        imageChooseDF.setCallback(new ChoseCallback() {
            @Override
            public void onChose(int choseIndex) {
                if (choseIndex == 0) {
                    forwardCamera();
                } else {
                    forwardAlumb();
                }
            }
        });
        imageChooseDF.show(getSupportFragmentManager(), "img");
    }

    /**
     * 检查文件读写的权限,这是在选择本地图片或文件时候用
     */
    private void checkFilePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_FILE_PERMISSION);
            } else {
                chooseFile();
            }
        } else {
            chooseFile();
        }
    }

    /**
     * 检查并申请文件读写，拍照的权限，这是在拍照上传图片时候用
     */
    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        },
                        REQUEST_CAMERA_PERMISSION);
            } else {
                takePhoto();
            }
        } else {
            takePhoto();
        }
    }

    /**
     * 打开相册，选择文件
     */
    private void chooseFile() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_flie)), REQUEST_CODE_CHOOSE);
    }

    private File getNewFile() {
        if (mSdf == null) {
            mSdf = new SimpleDateFormat("yyyyMMddHHmmss");
        }
        // 裁剪头像的绝对路径
        File dir = new File(Environment.getExternalStorageDirectory(), "phonelive");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new File(dir, mSdf.format(new Date()) + ".png");
    }

    private SimpleDateFormat mSdf;
    private File mCameraResult;//拍照后得到的图片
    private String FILE_PROVIDER = "com.yunbao.phonelive.fileprovider";

    /**
     * 开启摄像头，执行照相
     */
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCameraResult = getNewFile();
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
//            uri = FileProvider.getUriForFile(mContext, FILE_PROVIDER, mCameraResult);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            mCameraResult = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/phonelive/" + System.currentTimeMillis() + ".jpg");
            mCameraResult.getParentFile().mkdirs();

            //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
            uri = FileProvider.getUriForFile(this, FILE_PROVIDER, mCameraResult);
            //添加权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(mCameraResult);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_PHOTO);

    }

    /**
     * 选择后裁剪
     */
    private void cropOnChoose(Uri uri) {
        mCameraResult = getNewFile();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraResult));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 800);// 输出图片大小
        intent.putExtra("outputY", 1000);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }

    /**
     * 从相册中获取图片
     */
    public void forwardAlumb() {
        checkFilePermission();
    }

    /**
     * 拍照获取图片
     */
    public void forwardCamera() {
        checkCameraPermission();
    }

    private final int REQUEST_CODE_CHOOSE = 100;//选择文件
    private final int REQUEST_CODE_CROP = 101;//裁剪
    private final int REQUEST_CODE_PHOTO = 102;//拍照
    private final int REQUEST_FILE_PERMISSION = 201;//文件读写权限的请求码，用在选择本地图片或文件上传的场景
    private final int REQUEST_CAMERA_PERMISSION = 202;//同时请求摄像头和文件读写权限的请求码，用在拍照上传图片的场景
    private final int REQUEST_LIVE_PERMISSION = 203;//同时请求摄像头和录音权限的请求码，用在开播的场景

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        L.e("resultCode---->" + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE:
                    imageUri = intent.getData();
                    cropOnChoose(imageUri);
//                    picIv.setImageURI(imageUri);
//                    try {
//                        mCameraResult=   new File(new URI(imageUri.toString()));
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                    }
                    break;
                case REQUEST_CODE_PHOTO:
                    if (Build.VERSION.SDK_INT >= 24) {
                        imageUri = FileProvider.getUriForFile(mContext, FILE_PROVIDER, mCameraResult);
                        picIv.setImageURI(imageUri);
                    } else {
                        imageUri = Uri.fromFile(mCameraResult);
                        picIv.setImageURI(imageUri);
                    }
                    break;
                case REQUEST_CODE_CROP:
                    //裁剪结束
                    picIv.setImageURI(imageUri);
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE:
                    ToastUtil.show(getString(R.string.cancel_choose));
                    break;
                case REQUEST_CODE_PHOTO:
                    ToastUtil.show(getString(R.string.cancel_photo));
                    break;
                case REQUEST_CODE_CROP:
                    ToastUtil.show(getString(R.string.cancel_crop));
                    break;
            }
        }
    }

    private String imageUrl = "";

    private void uploadImage() {
        if (mCameraResult != null) {
            HttpUtil.uploadImage(mCameraResult, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    imageUrl = obj.getString("avatar");
                    commitContent();
                }

                @Override
                public boolean showLoadingDialog() {
                    return true;
                }

                @Override
                public Dialog createLoadingDialog() {
                    return DialogUitl.loadingDialog(LiveReportActivity.this);
                }
            });
        } else {
            commitContent();
        }
    }

    private void commitContent() {
        HttpUtil.setNewReport(liveId, roomId, roomName, contentEt.getText().toString().trim(), reportType, qqNumEt.getText().toString().trim(), imageUrl, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("举报成功!");
                    finish();
                }
            }
            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(LiveReportActivity.this);
            }

        });
    }

}
