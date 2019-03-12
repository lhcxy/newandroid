package com.yunbao.phonelive.fragment;

import android.view.View;
import android.widget.TextView;

import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/8/21.
 * 直播间底部按钮栏
 */

public abstract class LiveBottomFragment extends AbsFragment {

    protected TextView mRedPoint;//显示未读消息的小红点
    private int mUnReadCount;

    @Override
    protected void main() {
        mRedPoint = (TextView) mRootView.findViewById(R.id.red_point);
    }

    public void setUnReadCount(int unReadCount) {
//        if (unReadCount == mUnReadCount) {
//            return;
//        }
//        mUnReadCount = unReadCount;
//        if (mRedPoint != null) {
//            if (mUnReadCount > 0) {
//                if (mRedPoint.getVisibility() == View.GONE) {
//                    mRedPoint.setVisibility(View.VISIBLE);
//                }
//                mRedPoint.setText(String.valueOf(mUnReadCount));
//            } else {
//                if (mRedPoint.getVisibility() == View.VISIBLE) {
//                    mRedPoint.setVisibility(View.GONE);
//                }
//            }
//        }
    }

    public void setStarNum(String starNum) {

    }
}
