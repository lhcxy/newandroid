package com.yunbao.phonelive.fragment;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ContributeBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.glide.ImgLoader;

import java.util.List;

/**
 * Created by cxf on 2017/8/12.
 * 他人主页 home页
 */

public class UserInfoHomeFragment extends AbsFragment {

    private TextView mOrderText;
    private ImageView mFirst;
    private ImageView mSecond;
    private ImageView mThrid;
    private TextView mIdName;
    private TextView mIdValue;
    private TextView mSignature;

    @Override

    protected int getLayoutId() {
        return R.layout.fragment_user_info_home;
    }

    @Override
    protected void main() {
        initView();
        initData();
    }

    private void initView() {
        mOrderText = (TextView) mRootView.findViewById(R.id.order_text);
        mFirst = (ImageView) mRootView.findViewById(R.id.first);
        mSecond = (ImageView) mRootView.findViewById(R.id.second);
        mThrid = (ImageView) mRootView.findViewById(R.id.thrid);
        mIdName = (TextView) mRootView.findViewById(R.id.id_name);
        mIdValue = (TextView) mRootView.findViewById(R.id.id_value);
        mSignature = (TextView) mRootView.findViewById(R.id.signature);
    }

    private void initData() {
        Bundle bundle = getArguments();
        UserBean bean = bundle.getParcelable("user");
        if(bean!=null){
//            String liangName = bean.getLiang().getName();
//            if (!"0".equals(liangName)) {
//                mIdName.setText(mContext.getString(R.string.liang));
//                mIdValue.setText(liangName);
//            } else {
//                mIdValue.setText(bean.getId());
//            }
            mSignature.setText(bean.getSignature());
        }
        String contribute = bundle.getString("contribute");
        mOrderText.setText(AppConfig.getInstance().getConfig().getName_votes() + mContext.getString(R.string.order_list));
        List<ContributeBean> list = JSON.parseArray(contribute, ContributeBean.class);
        if (list.size() > 0) {
            ImgLoader.displayCircle(list.get(0).getAvatar(), mFirst);
        }
        if (list.size() > 1) {
            ImgLoader.displayCircle(list.get(1).getAvatar(), mSecond);
        }
        if (list.size() > 2) {
            ImgLoader.displayCircle(list.get(2).getAvatar(), mThrid);
        }
    }
}
