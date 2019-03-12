package com.phonelive.game.game.nz;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/10/19.
 * 庄家流水的弹窗
 */

public class GameNzLsFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private ListView mListView;
    private ProgressBar mProgressBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.game_fragment_nz_ls, null);
        dialog.setContentView(mRootView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(300);
        params.height = DpUtil.dp2px(400);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressbar);
        mListView = (ListView) mRootView.findViewById(R.id.listView);
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
    }

    private void initData() {
        String stream = getArguments().getString("stream");
        String bankerId = getArguments().getString("bankerId");
        HttpUtil.gameNiuBankerWater(bankerId, stream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(code==0){
                    List<GameNzLsBean> list = JSON.parseArray(Arrays.toString(info), GameNzLsBean.class);
                    if (list.size() > 0) {
                        GameNzLsAdapter adapter = new GameNzLsAdapter(list);
                        mListView.setAdapter(adapter);
                    }
                }else{
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onFinish() {
                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GAME_NIU_BANKER_WATER);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
