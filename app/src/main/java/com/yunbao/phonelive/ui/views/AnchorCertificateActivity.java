package com.yunbao.phonelive.ui.views;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.luck.picture.lib.entity.LocalMedia;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.views.adapter.LiveWatcherVPAdapter;
import com.yunbao.phonelive.ui.views.fragment.CertificateDescFragment;
import com.yunbao.phonelive.ui.views.fragment.CertificatePhoneFragment;
import com.yunbao.phonelive.ui.views.fragment.CertificatePhotoFragment;
import com.yunbao.phonelive.ui.widget.NoScrollViewPager;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 主播认证
 */
public class AnchorCertificateActivity extends AbsActivity {
    private CheckBox phoneCb, photoCb, descCb;
    private View oneV, twoV;
    private TextView phoneTv, photoTv, descTv;
    private NoScrollViewPager nsvp;
    private List<Fragment> fragments;
    private CertificatePhoneFragment phoneFragment;
    private CertificatePhotoFragment photoFragment;
    private CertificateDescFragment descFragment;
    private FragmentPagerAdapter vpAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_anchor_certificate;
    }

    @Override
    protected void main() {
        setTitle("绑定手机");
        phoneCb = findViewById(R.id.certificate_phone_cb);
        oneV = findViewById(R.id.certificate_one_v);
        photoCb = findViewById(R.id.certificate_photo_cb);
        twoV = findViewById(R.id.certificate_two_v);
        descCb = findViewById(R.id.certificate_desc_cb);
        phoneTv = findViewById(R.id.certificate_phone_tv);
        photoTv = findViewById(R.id.certificate_photo_tv);
        descTv = findViewById(R.id.certificate_desc_tv);
        nsvp = findViewById(R.id.certificate_nsvp);
        fragments = new ArrayList<>();
        phoneFragment = CertificatePhoneFragment.newInstance();
        photoFragment = CertificatePhotoFragment.newInstance();
        descFragment = CertificateDescFragment.newInstance();
        fragments.add(phoneFragment);
        fragments.add(photoFragment);
        fragments.add(descFragment);
//        vpAdapter = new LiveWatcherVPAdapter(getSupportFragmentManager(), fragments);
        vpAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        nsvp.setAdapter(vpAdapter);
        nsvp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                titleViewChange(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        initData();
    }

    private void titleViewChange(int i) {
        if (0 == i) {
            phoneCb.setChecked(true);
            photoCb.setChecked(false);
            descCb.setChecked(false);
            oneV.setBackgroundColor(getResources().getColor(R.color.text_color_b2));
            twoV.setBackgroundColor(getResources().getColor(R.color.text_color_b2));
            setTitle("绑定手机");
        } else if (1 == i) {
            phoneCb.setChecked(true);
            photoCb.setChecked(true);
            oneV.setBackgroundColor(getResources().getColor(R.color.app_selected_color));
            twoV.setBackgroundColor(getResources().getColor(R.color.text_color_b2));
            descCb.setChecked(false);
            setTitle("个人资料");
        } else {
            phoneCb.setChecked(true);
            photoCb.setChecked(true);
            descCb.setChecked(true);
            oneV.setBackgroundColor(getResources().getColor(R.color.app_selected_color));
            twoV.setBackgroundColor(getResources().getColor(R.color.app_selected_color));
            setTitle("自我介绍");
        }

    }

    private void initData() {

    }


    /**
     * 跳转证件照 界面
     */
    public void jump2TakePhoto() {
        nsvp.setCurrentItem(1);
    }

    /**
     * 跳转 自我介绍界面
     *
     * @param trim2    姓名
     * @param trim     身份证号码
     * @param s        银行卡号
     * @param trim1    开卡银行
     * @param s1       联系方式
     * @param frontImg 正面照
     * @param backImg  背面照
     * @param holdImg  手持照
     */
    public void jump2TakeDesc(String trim2, String trim, String s, String trim1, String s1, LocalMedia frontImg, LocalMedia backImg, LocalMedia holdImg) {
        name = trim2;
        idNum = trim;
        cardNum = s;
        bankName = trim1;
        wechaNum = s1;
        this.frontImg = frontImg;
        this.backImg = backImg;
        this.holdImg = holdImg;
        nsvp.setCurrentItem(2);
    }

    private String name, idNum, cardNum, bankName, wechaNum;
    private LocalMedia frontImg, backImg, holdImg;


    /**
     * 最终提交
     */
    public void commit(String descStr, String liveType) {
        if (frontImg != null && !TextUtils.isEmpty(frontImg.getPath())
                && backImg != null && !TextUtils.isEmpty(backImg.getPath())
                && holdImg != null && !TextUtils.isEmpty(holdImg.getPath())) {
            File file = new File(frontImg.getPath());
            File file1 = new File(backImg.getPath());
            File file2 = new File(holdImg.getPath());
            HttpUtil.setLiveApply(name, idNum, bankName, cardNum, AppConfig.getInstance().getUserBean().getMobile(), liveType, descStr,
                    file, file1, file2, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            Log.e("//", "onSuccess: " + Arrays.toString(info));
                            if (code == 0) {
                                ToastUtil.show("提交成功,正在审核中");
                                finish();
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
