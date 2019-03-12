package com.yunbao.phonelive.ui.views.fragment;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.views.AnchorCertificateActivity;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CertificatePhotoFragment extends BaseLazyFragment {
    private EditText nameEt, idNumEt, bankCardEt, wechatEt;
    private ImageView idCardFrontIv, idCardBackIv, idCardHoldIv;
    private TextView photoCommitTv, bankNameTv;
    private LocalMedia frontImg, backImg, holdImg;
    private BankNameBSFragment bsFragment;
    private String bankName = "";

    public static CertificatePhotoFragment newInstance() {
        CertificatePhotoFragment fragment = new CertificatePhotoFragment();
        return fragment;
    }

    private int imgSelectType = 0;  //0=正面  1=背面 2=手持

    @Override
    protected int getLayoutId() {
        return R.layout.fragmetn_certificate_photo;
    }

    @Override
    protected void initView() {
        nameEt = findView(R.id.bind_photo_name_et);
        idNumEt = findView(R.id.bind_photo_id_et);
        bankCardEt = findView(R.id.bind_photo_bank_card_et);
        wechatEt = findView(R.id.bind_photo_wechat_et);
        idCardFrontIv = findView(R.id.idcard_front_iv);
        idCardBackIv = findView(R.id.idcard_back_iv);
        idCardHoldIv = findView(R.id.idcard_hold_iv);
        bankNameTv = findView(R.id.bind_photo_bank_name_tv);
        photoCommitTv = findView(R.id.bind_photo_action_tv);
        bsFragment = BankNameBSFragment.newInstance();
        bsFragment.setListener((item, position) -> {
            bankName = item;
            bankNameTv.setText(bankName);
        });
        initListener();
    }

    private void initListener() {
        disposable.add(RxView.clicks(idCardFrontIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            imgSelectType = 0;
            selectImg();
        }));
        disposable.add(RxView.clicks(idCardBackIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            imgSelectType = 1;
            selectImg();
        }));
        disposable.add(RxView.clicks(idCardHoldIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            imgSelectType = 2;
            selectImg();
        }));
        disposable.add(RxView.clicks(photoCommitTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> commit()));

        /**
         * 开卡银行选择
         */
        disposable.add(RxView.clicks(findView(R.id.bind_photo_bank_name_ll)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (bsFragment != null) {
                bsFragment.show(getChildFragmentManager(), "bankNameChoose");
            }
        }));

    }

    public static final int REQUEST_CODE = 0x01;

    private void selectImg() {
//        Phoenix.with()
//                .theme(PhoenixOption.THEME_DEFAULT)// 主题
//                .fileType(MimeType.ofAll())//显示的文件类型图片、视频、图片和视频
//                .maxPickNumber(1)// 最大选择数量
//                .minPickNumber(0)// 最小选择数量
//                .spanCount(4)// 每行显示个数
//                .enablePreview(true)// 是否开启预览
//                .enableCamera(true)// 是否开启拍照
//                .enableAnimation(true)// 选择界面图片点击效果
//                .enableCompress(true)// 是否开启压缩
//                .compressPictureFilterSize(1024)//多少kb以下的图片不压缩
//                .compressVideoFilterSize(2018)//多少kb以下的视频不压缩
//                .thumbnailHeight(160)// 选择界面图片高度
//                .thumbnailWidth(160)// 选择界面图片宽度
//                .enableClickSound(false)// 是否开启点击声音
//                .videoFilterTime(0)//显示多少秒以内的视频
//                .mediaFilterSize(10000)//显示多少kb以下的图片/视频，默认为0，表示不限制
//                //如果是在Activity里使用就传Activity，如果是在Fragment里使用就传Fragment
//                .start(this, PhoenixOption.TYPE_PICK_MEDIA, REQUEST_CODE);
        PictureSelector.create(CertificatePhotoFragment.this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode( PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(false)// 是否可预览视频 true or false
                .enablePreviewAudio(false) // 是否可播放音频 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(true)// 是否裁剪 true or false
                .compress(false)// 是否压缩 true or false
                .forResult(REQUEST_CODE);//结果回调onActivityResult code
    }

    private void commit() {
        if (TextUtils.isEmpty(nameEt.getText().toString().trim())) {
            ToastUtil.show("请输入真实姓名");
            return;
        }
        if (TextUtils.isEmpty(idNumEt.getText().toString().trim())) {
            ToastUtil.show("请输入身份证号");
            return;
        }
        if (TextUtils.isEmpty(bankCardEt.getText().toString().trim())) {
            ToastUtil.show("请输入银行卡号");
            return;
        }
        if (TextUtils.isEmpty(bankName)) {
            ToastUtil.show("请选择开卡银行");
            return;
        }
        if (TextUtils.isEmpty(wechatEt.getText().toString().trim())) {
            ToastUtil.show("请输入QQ/微信号");
            return;
        }
        if (frontImg == null) {
            ToastUtil.show("请选取身份证正面照片");
            return;
        }
        if (backImg == null) {
            ToastUtil.show("请选取身份证背面照片");
            return;
        }
        if (holdImg == null) {
            ToastUtil.show("请选取手持身份证照片");
            return;
        }
        if (getActivity() != null && getActivity() instanceof AnchorCertificateActivity) {
            ((AnchorCertificateActivity) getActivity()).jump2TakeDesc(nameEt.getText().toString().trim(),
                    idNumEt.getText().toString().trim(), bankCardEt.getText().toString().trim(),
                    bankName, wechatEt.getText().toString().trim(), frontImg, backImg, holdImg
            );
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //返回的数据
//            List<MediaEntity> result = Phoenix.result(data);
            List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
            if (result != null && result.size() > 0) {
                if (imgSelectType == 0) {
                    frontImg = result.get(0);
                    ImgLoader.display(result.get(0).getPath(), idCardFrontIv);
                } else if (imgSelectType == 1) {
                    backImg = result.get(0);
                    ImgLoader.display(result.get(0).getPath(), idCardBackIv);
                } else {
                    holdImg = result.get(0);
                    ImgLoader.display(result.get(0).getPath(), idCardHoldIv);
                }

            }

        }
    }
}
