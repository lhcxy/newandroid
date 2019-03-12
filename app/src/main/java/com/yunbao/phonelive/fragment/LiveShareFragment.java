package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.SharedSdkAdapter;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.SharedSdkBean;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.helper.CommonItemDecoration;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.SharedSdkUitl;

import cn.sharesdk.framework.Platform;

/**
 * Created by cxf on 2017/9/28.
 */

public class LiveShareFragment extends DialogFragment implements OnItemClickListener<SharedSdkBean> {

    private Context mContext;
    private View mRootView;
    private ConfigBean mConfigBean;
    private LiveBean mLiveBean;
    private SharedSdkUitl.ShareListener shareListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = LayoutInflater.from(AppContext.sInstance).inflate(R.layout.fragment_share, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.bottomToTopAnim);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mLiveBean == null) {
            mLiveBean = getArguments().getParcelable("live");
        }
        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new CommonItemDecoration(DpUtil.dp2px(32), DpUtil.dp2px(2)));
        mConfigBean = AppConfig.getInstance().getConfig();
        SharedSdkAdapter adapter = new SharedSdkAdapter(mConfigBean.getShare_type(), true, false);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        shareListener = new SharedSdkUitl.ShareListener() {
            @Override
            public void onSuccess(Platform platform) {
                if (!AppConfig.isUnlogin()) {
                    HttpUtil.onFinishShare();
                }
                dismiss();
            }

            @Override
            public void onError(Platform platform) {
                dismiss();
            }

            @Override
            public void onCancel(Platform platform) {
                dismiss();
            }
        };
    }

    @Override
    public void onItemClick(SharedSdkBean item, int position) {
        if (listener != null) {
            listener.onItemClick(item, position);
            dismiss();
        } else {
            String url = mConfigBean.getWx_siteurl() + mLiveBean.getUid();
            SharedSdkUitl.getInstance().share(item.getType(),
                    mConfigBean.getShare_title(),
                    mLiveBean.getUser_nicename() + mConfigBean.getShare_des(),
                    mLiveBean.getAvatar(), url, shareListener);
            dismiss();
        }
    }

    private OnItemClickListener<SharedSdkBean> listener;

    public void setListener(OnItemClickListener<SharedSdkBean> listener) {
        this.listener = listener;
    }

}
