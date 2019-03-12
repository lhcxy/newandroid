package com.yunbao.phonelive.ui.views.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.fragment.AbsFragment;
import com.yunbao.phonelive.ui.views.LiveCameraActivity;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;

public class PreLiveSettingFragment extends AbsFragment {

    private CompositeDisposable disposable;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_prelive_setting;
    }

    @Override
    protected void main() {
        disposable = new CompositeDisposable();
        initView();
    }

    private TextView pixelChostTv, netNodeChoseTv;
    private LinearLayout pixelGroupLl, netNodeGroupLl;

    private void initView() {
        pixelChostTv = findView(R.id.prelive_setting_pixel_tv);
        netNodeChoseTv = findView(R.id.prelive_setting_net_tv);
        pixelGroupLl = findView(R.id.prelive_setting_pixel_gp_ll);
        netNodeGroupLl = findView(R.id.prelive_setting_net_gp_ll);
        initClick();
        choseNode(SharedPreferencesUtil.getInstance().getInt(SharedPreferencesUtil.LIVE_SETTING_NODE));
        chosePixel(SharedPreferencesUtil.getInstance().getInt(SharedPreferencesUtil.LIVE_SETTING_PIXEL, 1));

    }

    private void initClick() {
        disposable.add(RxView.clicks(findView(R.id.prelive_setting_pixel_xxxh_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> chosePixel(0)));
        disposable.add(RxView.clicks(findView(R.id.prelive_setting_pixel_xxh_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> chosePixel(1)));
        disposable.add(RxView.clicks(findView(R.id.prelive_setting_pixel_xh_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> chosePixel(2)));
        disposable.add(RxView.clicks(findView(R.id.prelive_setting_pixel_h_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> chosePixel(3)));
        disposable.add(RxView.clicks(findView(R.id.prelive_setting_pixel_title_rl)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> pixelGroupLl.setVisibility(pixelGroupLl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE)));
        disposable.add(RxView.clicks(findView(R.id.prelive_setting_net_title_rl)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> netNodeGroupLl.setVisibility(netNodeGroupLl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE)));

        disposable.add(RxView.clicks(findView(R.id.prelive_setting_net_def_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> choseNode(0)));
        disposable.add(RxView.clicks(findView(R.id.prelive_setting_net_outside_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> choseNode(1)));

        disposable.add(RxView.clicks(findView(R.id.prelive_setting_back_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (getActivity() != null) {
                if (getActivity() instanceof LiveCameraActivity) {
                    ((LiveCameraActivity) getActivity()).backReadyView();
                }
            }
        }));

    }

    /**
     * 选择画质
     *
     * @param pixelType 0=高清 1=标清 2=流畅 3=低清
     */
    private void chosePixel(int pixelType) {
        SharedPreferencesUtil.getInstance().saveInt(SharedPreferencesUtil.LIVE_SETTING_PIXEL, pixelType);
        switch (pixelType) {
            case 0:
                pixelChostTv.setText("高清");
                break;
            case 1:
                pixelChostTv.setText("标清");
                break;
            case 2:
                pixelChostTv.setText("流畅");
                break;
            case 3:
                pixelChostTv.setText("低清");

                break;
        }
        pixelGroupLl.setVisibility(View.GONE);
    }

    /**
     * 选择上传节点
     *
     * @param type 0=默认  1=海外
     */
    private void choseNode(int type) {
        SharedPreferencesUtil.getInstance().saveInt(SharedPreferencesUtil.LIVE_SETTING_NODE, type);
        switch (type) {
            case 0:
                netNodeChoseTv.setText("默认");
                break;
            case 1:
                netNodeChoseTv.setText("海外");
                break;
        }
        netNodeGroupLl.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
        super.onDestroy();
    }
}
