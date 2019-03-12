package com.yunbao.phonelive.fragment;

import android.widget.SeekBar;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAnchorActivity;

/**
 * Created by cxf on 2017/9/1.
 */

public class LiveBeautyFragment extends AbsFragment implements SeekBar.OnSeekBarChangeListener {

    private SeekBar mMopi;//磨皮
    private SeekBar mMeibai;//美白
    private SeekBar mHongrun;//红润

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_beauty;
    }

    @Override
    protected void main() {
        mMopi = (SeekBar) mRootView.findViewById(R.id.mopi);
        mMeibai = (SeekBar) mRootView.findViewById(R.id.meibai);
        mHongrun = (SeekBar) mRootView.findViewById(R.id.hongrun);
        mMopi.setOnSeekBarChangeListener(this);
        mMeibai.setOnSeekBarChangeListener(this);
        mHongrun.setOnSeekBarChangeListener(this);
        int[] data = ((LiveAnchorActivity) mContext).getBeautyData();
        mMopi.setProgress(data[0]);
        mMeibai.setProgress(data[1]);
        mHongrun.setProgress(data[2]);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float val = progress / 100f;
        int type = 0;
        switch (seekBar.getId()) {
            case R.id.mopi:
                type = 0;
                break;
            case R.id.meibai:
                type = 1;
                break;
            case R.id.hongrun:
                type = 2;
                break;
        }
        ((LiveAnchorActivity) mContext).setBeautyData(type, val);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
