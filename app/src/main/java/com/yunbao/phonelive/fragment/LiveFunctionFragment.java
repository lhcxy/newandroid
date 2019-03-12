package com.yunbao.phonelive.fragment;

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
import android.widget.AdapterView;
import android.widget.GridView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAnchorActivity;
import com.yunbao.phonelive.adapter.LiveFunctionAdapter;
import com.yunbao.phonelive.bean.FunctionBean;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2017/8/19.
 * 主播直播间底部功能弹窗
 */

public class LiveFunctionFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private Context mContext;
    private View mRootView;
    private List<FunctionBean> mList;
    private int mAuctionSwitch;
    private int[] mGameSwitch;
    private String mLiveType;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_live_anchor_function, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(200);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        mAuctionSwitch = bundle.getInt("auction_switch");
        mGameSwitch = JSON.parseObject(bundle.getString("game_switch"), int[].class);
        mLiveType = bundle.getString("type");
        initView();
    }

    /**
     * 检查一下计时收费是否开启
     */
    private boolean timeChargeEnable() {
        boolean res = false;
        if ("1".equals(mLiveType)) {//密码房间
            return res;
        }
        for (String[] arr : AppConfig.getInstance().getConfig().getLive_type()) {
            if ("3".equals(arr[0])) {
                res = true;
                break;
            }
        }
        return res;
    }


    private void initView() {
        mList = new ArrayList<>();
        if (timeChargeEnable()) {
            mList.add(new FunctionBean(FunctionBean.TIME, R.mipmap.icon_live_function_time));
        }
        mList.add(new FunctionBean(FunctionBean.MEI_YAN, R.mipmap.icon_live_function_meiyan));
        mList.add(new FunctionBean(FunctionBean.CAMERA, R.mipmap.icon_live_function_camera));
        mList.add(new FunctionBean(FunctionBean.MUSIC, R.mipmap.icon_live_function_music));
//        if (mGameSwitch.length > 0) {
//            mList.add(new FunctionBean(FunctionBean.GAME, R.mipmap.icon_live_function_game));
//        }
        if (mAuctionSwitch == 1) {
            mList.add(new FunctionBean(FunctionBean.AUCTION, R.mipmap.icon_live_function_auction));
        }
        mList.add(new FunctionBean(FunctionBean.FLASH, R.mipmap.icon_live_function_flash));
        GridView gridView = (GridView) mRootView.findViewById(R.id.gridView);
        gridView.setAdapter(new LiveFunctionAdapter(mContext, mList));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((LiveAnchorActivity) mContext).functionClick(mList.get(position).getId());
        dismiss();
    }
}
