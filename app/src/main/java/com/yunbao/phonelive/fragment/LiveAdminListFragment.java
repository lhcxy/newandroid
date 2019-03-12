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

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.LiveAdminListAdapter;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/8/26.
 * 直播间管理员列表
 */

public class LiveAdminListFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private LiveAdminListAdapter mAdapter;
    private View mLoading;
    private View mNoResult;
    private UserBean mToUser;
    private String mLiveuid;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_live_admin_list, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(250);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mLoading = mRootView.findViewById(R.id.loading);
        mNoResult = mRootView.findViewById(R.id.no_result);
        mRootView.findViewById(R.id.back).setOnClickListener(this);
        mLiveuid = getArguments().getString("liveuid");
        HttpUtil.getAdminList(mLiveuid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<UserBean> list = JSON.parseArray(Arrays.toString(info), UserBean.class);
                    if (list.size() > 0) {
                        mAdapter = new LiveAdminListAdapter(mContext, list);
                        mAdapter.setOnItemRemoveListener(new OnItemClickListener<UserBean>() {
                            @Override
                            public void onItemClick(UserBean item, int position) {
                                mToUser = item;
                                HttpUtil.setAdmin(item.getId(), mLiveuid, mCallback);
                            }
                        });
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        mNoResult.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onFinish() {
                mLoading.setVisibility(View.GONE);
            }
        });
    }


    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (JSON.parseObject(info[0]).getIntValue("isadmin") == 0) {
                    String touid = mToUser.getId();
                    String uname = mToUser.getUser_nicename();
                    if (mAdapter != null) {
                        mAdapter.removeItem(mToUser.getId());
                        SocketUtil.getInstance().sendSystemMessage(touid, uname, uname + mContext.getString(R.string.admin_cancel));
                    }
                }
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
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_ADMIN_LIST);
        HttpUtil.cancel(HttpUtil.SET_ADMIN);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
