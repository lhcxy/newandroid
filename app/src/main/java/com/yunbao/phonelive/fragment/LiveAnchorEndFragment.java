package com.yunbao.phonelive.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

/**
 * Created by cxf on 2017/10/9.
 */

public class LiveAnchorEndFragment extends AbsFragment implements View.OnClickListener {

//    private ImageView mBg;
    private ImageView mAvatar;
    private TextView mVotes;
    private TextView mDuration;
    private TextView mNums;
    private TextView uname;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_anchor_end;
    }

    @Override
    protected void main() {
//        mBg = (ImageView) mRootView.findViewById(R.id.bg);
        mAvatar = (ImageView) mRootView.findViewById(R.id.anchor_avatar);
        mVotes = (TextView) mRootView.findViewById(R.id.votes);
        mDuration = (TextView) mRootView.findViewById(R.id.duration);
        mNums = (TextView) mRootView.findViewById(R.id.nums);
        uname = (TextView) mRootView.findViewById(R.id.text1);
        TextView votesTv = (TextView) mRootView.findViewById(R.id.get_votes);
        votesTv.setText(AppConfig.getInstance().getConfig().getName_votes());
        mRootView.findViewById(R.id.btn_confirm).setOnClickListener(this);
        Bundle bundle = getArguments();
        String stream = bundle.getString("stream");
        String avatar = AppConfig.getInstance().getUserBean().getAvatar();
//        ImgLoader.displayBlur(avatar, mBg);
        ImgLoader.display(avatar, mAvatar);
        uname.setText(AppConfig.getInstance().getUserBean().getUser_nicename());
        HttpUtil.stopLiveInfo(stream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mVotes.setText(obj.getString("total"));
                    mDuration.setText(obj.getString("length"));
                    mNums.setText(obj.getString("nums"));
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
        boolean isSuperClose = bundle.getBoolean("isSuperClose", false);
        if (isSuperClose) {
            DialogUitl.messageDialog(mContext, getString(R.string.tip), getString(R.string.content_illegal), new DialogUitl.Callback2() {

                @Override
                public void confirm(Dialog dialog) {
                    ((Activity) mContext).finish();
                }
            }).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                ((Activity) mContext).finish();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.STOP_LIVE_INFO);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
