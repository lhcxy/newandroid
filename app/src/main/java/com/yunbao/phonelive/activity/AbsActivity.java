package com.yunbao.phonelive.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.event.JPushEvent;
import com.yunbao.phonelive.presenter.CheckLivePresenter;
import com.yunbao.phonelive.utils.FixFocusedViewLeakUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by cxf on 2017/8/3.
 */

public abstract class AbsActivity extends AppCompatActivity {

    protected static final int ACTIVITYTYPE_ORTHER = 0;
    protected static final int ACTIVITYTYPE_ANCHOR = 1;
    protected static final int ACTIVITYTYPE_AUDIENCE = 2;

    protected Context mContext;
    private boolean isRegistered;
    protected int mActivityType;
    protected CompositeDisposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        preOnCreate();
        super.onCreate(savedInstanceState);
        disposable = new CompositeDisposable();
        setContentView(getLayoutId());
        mContext = this;
        if (savedInstanceState != null) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (fragments != null && fragments.size() > 0) {
                for (Fragment fragment : fragments) {
                    ft.remove(fragment);
                }
                ft.commitNow();
            }
        }
        main();
        isRegistered = EventBus.getDefault().isRegistered(this);
        if (!isRegistered) {
            EventBus.getDefault().register(this);
        }
    }

    public void preOnCreate() {

    }


    protected abstract int getLayoutId();

    protected abstract void main();

    protected void setTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    protected void setTitleRightBtn(String title) {
        TextView subTitle = (TextView) findViewById(R.id.subtitle_tv);
        if (subTitle != null) {
            subTitle.setText(title);
            subTitle.setVisibility(View.VISIBLE);
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    protected void addDisposable(@NonNull Disposable d) {
        if (disposable != null) {
            disposable.add(d);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FixFocusedViewLeakUtil.fixFocusedViewLeak();
        if (!isRegistered) {
            EventBus.getDefault().unregister(this);
        }
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
        //AppContext.sRefWatcher.watch(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void jpushEvent(JPushEvent e) {
        if (!isTopActivity(this)) {
            return;
        }
        switch (mActivityType) {
            case ACTIVITYTYPE_ORTHER:
                CheckLivePresenter mCheckLivePresenter = new CheckLivePresenter(mContext);
                mCheckLivePresenter.setSelectLiveBean(e);
                mCheckLivePresenter.watchLive();
                break;
            case ACTIVITYTYPE_ANCHOR:
                ToastUtil.show(getString(R.string.live_tips));
                break;
            case ACTIVITYTYPE_AUDIENCE:

                break;

        }
//        L.e(">>>>收到推送----" + e.toString());
    }

    private boolean isTopActivity(Activity activity) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//        L.e(">>>>>>>>最上层的activity-----" + cn.getClassName() + "---" + activity.getLocalClassName());
        return cn.getClassName().contains(activity.getLocalClassName());
    }

    /**
     * 未登录
     */
    protected void unloginHint(View view) {
        Snackbar make = Snackbar.make(view, getResources().getString(R.string.user_unlogin_hint_txt), Snackbar.LENGTH_SHORT);
        make.setAction("登陆", v -> {
            startActivity(new Intent(AbsActivity.this, LoginActivity.class));
        });
        make.show();
    }

}
