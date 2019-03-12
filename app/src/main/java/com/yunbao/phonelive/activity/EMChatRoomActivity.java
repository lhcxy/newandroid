package com.yunbao.phonelive.activity;

import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.fragment.EMChatRoomFragment;
import com.yunbao.phonelive.im.JIM;

/**
 * Created by cxf on 2017/8/14.
 * 环信聊天页面
 */

public class EMChatRoomActivity extends AbsActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_fragment;
    }

    @Override
    protected void main() {
        if (getIntent().getExtras() != null) {
            UserBean toUser = getIntent().getExtras().getParcelable("touser");
            EMChatRoomFragment fragment =  EMChatRoomFragment.newInstance(toUser);
            fragment.setIM(new JIM());
            fragment.setArguments(getIntent().getExtras());
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.replaced, fragment);
            ft.commit();
        }
    }

}
