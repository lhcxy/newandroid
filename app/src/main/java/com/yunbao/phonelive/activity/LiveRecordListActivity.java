package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.fragment.LiveRecordFragment;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DialogUitl;

import java.util.Arrays;

/**
 * Created by cxf on 2017/8/16.
 * 直播回放列表
 */

public class LiveRecordListActivity extends AbsActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_fragment2;
    }

    @Override
    protected void main() {
        setTitle(getString(R.string.live_record));
        String touid=getIntent().getStringExtra("touid");
        HttpUtil.getLiverecord(touid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                LiveRecordFragment fragment = new LiveRecordFragment();
                Bundle bundle = new Bundle();
                bundle.putString("liverecord", Arrays.toString(info));
                fragment.setArguments(bundle);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.replaced, fragment);
                ft.commit();
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

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_LIVERECORD);
        super.onDestroy();
    }
}
