package com.yunbao.phonelive.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveReadyActivity;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by cxf on 2017/8/29.
 */

public class ChooseImgFragment extends Fragment {

    private final int REQUEST_CODE_CHOOSE = 100;//选择文件
    private final int REQUEST_CODE_CROP = 101;//裁剪
    private final int REQUEST_CODE_PHOTO = 102;//拍照
    private final int REQUEST_FILE_PERMISSION = 201;//文件读写权限的请求码，用在选择本地图片或文件上传的场景
    private final int REQUEST_CAMERA_PERMISSION = 202;//同时请求摄像头和文件读写权限的请求码，用在拍照上传图片的场景
    private final int REQUEST_LIVE_PERMISSION = 203;//同时请求摄像头和录音权限的请求码，用在开播的场景
    private SimpleDateFormat mSdf;
    private CommonCallback<File> mOnCompleted;
    private Context mContext;
    private File mCameraResult;//拍照后得到的图片
    private File mCorpResult;//裁剪后得到的图片
    private String FILE_PROVIDER = "com.yunbao.phonelive.fileprovider";

    public void setOnCompleted(CommonCallback<File> onCompleted) {
        mOnCompleted = onCompleted;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
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
            uri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER, mCameraResult);
            //添加权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(mCameraResult);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_PHOTO);

    }

    /**
     * 开播
     */
    private void live() {
        ((LiveReadyActivity) getActivity()).startLive();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        L.e("resultCode---->" + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE:
                    //进入裁剪
                    cropOnChoose(intent.getData());
                    break;
                case REQUEST_CODE_PHOTO:
                    if (Build.VERSION.SDK_INT >= 24) {
                        cropOnPhoto(FileProvider.getUriForFile(mContext, FILE_PROVIDER, mCameraResult));
                    } else {
                        cropOnPhoto(Uri.fromFile(mCameraResult));
                    }
                    break;
                case REQUEST_CODE_CROP:
                    //裁剪结束
                    if (mOnCompleted != null) {
                        mOnCompleted.callback(mCorpResult);
                    }
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


    /**
     * 拍照后裁剪
     */
    private void cropOnPhoto(Uri uri) {
        mCorpResult = getNewFile();
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCorpResult));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 400);// 输出图片大小
        intent.putExtra("outputY", 400);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }


    /**
     * 选择后裁剪
     */
    private void cropOnChoose(Uri uri) {
        mCorpResult = getNewFile();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCorpResult));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 400);// 输出图片大小
        intent.putExtra("outputY", 400);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent, REQUEST_CODE_CROP);
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


    /**
     * 检查文件读写的权限,这是在选择本地图片或文件时候用
     */
    private void checkFilePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
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
     * 检查并申请拍照和录音,文件读写的权限，这是在开播的时候用
     */
    public void checkLivePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FILE_PERMISSION:
                if (isAllGranted(permissions, grantResults)) {
                    chooseFile();
                }
                break;
            case REQUEST_CAMERA_PERMISSION:
                if (isAllGranted(permissions, grantResults)) {
                    takePhoto();
                }
                break;
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
