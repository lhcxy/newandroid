package com.yunbao.phonelive.ui.views.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAnchorActivity;
import com.yunbao.phonelive.activity.LiveAnchorHorizontalActivity;
import com.yunbao.phonelive.adapter.SharedSdkAdapter;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.SharedSdkBean;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.CameraEvent;
import com.yunbao.phonelive.event.LiveBsEvent;
import com.yunbao.phonelive.event.LiveTypeEvent;
import com.yunbao.phonelive.fragment.AbsFragment;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.ui.helper.CommonItemDecoration;
import com.yunbao.phonelive.ui.views.AnchorCertificateActivity;
import com.yunbao.phonelive.ui.views.LiveCameraActivity;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;
import com.yunbao.phonelive.utils.SharedSdkUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.framework.Platform;
import io.reactivex.disposables.CompositeDisposable;

public class PreLiveReadyFragment extends AbsFragment {
    private ImageView titleClearIv, avatarIv;
    private EditText titleEt;
    private UserBean userBean;
    private TextView uNameTv, liveHintTv;
    private RecyclerView shareRv;
    private SharedSdkAdapter mAdapter;
    private TabLayout liveTypeTl, liveChannelTl;
    String liveTypeId;
    private LiveTypeBSFragment liveTypeBSFragment;
    private LiveTypeChannelBSFragment liveTypeChannelBSFragment;
    private LinearLayout liveBottomLl;
//    private CompositeDisposable disposable;
    private LayoutInflater layoutInflater;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_prelive_ready;
    }

    @Override
    protected void main() {
        EventBus.getDefault().register(this);
//        disposable = new CompositeDisposable();
        initView();
        initClick();
        initData();
    }

    private void initData() {
        userBean = AppConfig.getInstance().getUserBean();
        if (userBean != null) {
            ImgLoader.displayCircleWhiteBorder(userBean.getAvatar(), avatarIv);
            uNameTv.setText(userBean.getUser_nicename());
        }
        getTabData();
        titleEt.setText(SharedPreferencesUtil.getInstance().getString(SharedPreferencesUtil.LIVE_ROOM_NAME));

    }

    private void initView() {
        layoutInflater = LayoutInflater.from(getContext());
        titleEt = findView(R.id.prelive_title_et);
        titleClearIv = findView(R.id.prelive_clear_iv);
        avatarIv = findView(R.id.prelive_avatar_iv);
        uNameTv = findView(R.id.prelive_uname_tv);
        shareRv = findView(R.id.prelive_share_rv);
        liveTypeTl = findView(R.id.live_type_tl);
        liveChannelTl = findView(R.id.live_type_channel_tl);
        liveHintTv = findView(R.id.live_type_hint_tv);
        liveBottomLl = findView(R.id.live_bottom_ll);
        shareRv.setHasFixedSize(true);
        shareRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        shareRv.addItemDecoration(new CommonItemDecoration(DpUtil.dp2px(32), DpUtil.dp2px(2)));
        mAdapter = new SharedSdkAdapter(AppConfig.getInstance().getConfig().getShare_type(), true, true);
        shareRv.setAdapter(mAdapter);
        liveChannelTl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (liveChannelTl.getChildCount() > 0) {
                    channelIndex = tab.getPosition();
                    if (tabDatas.get(typeIndex).getSub() != null || tabDatas.get(typeIndex).getSub().size() > 0) {
                        liveTypeId = tabDatas.get(typeIndex).getSub().get(channelIndex).getId();
                    }
                    if (tab.getCustomView() != null) {
                        TextView title = tab.getCustomView().findViewById(R.id.item_tv);
                        if (title != null) {
                            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            title.setTextColor(getResources().getColor(R.color.app_selected_color));
                        }
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab != null && tab.getCustomView() != null) {
                    TextView title = tab.getCustomView().findViewById(R.id.item_tv);
                    if (title != null) {
                        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        title.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        liveTypeTl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (liveTypeTl.getChildCount() > 0) {
                    typeIndex = tab.getPosition();
                    if (tab.getCustomView() != null) {
                        TextView title = tab.getCustomView().findViewById(R.id.item_tv);
                        if (title != null) {
                            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            title.setTextColor(getResources().getColor(R.color.app_selected_color));
                        }
                    }
                    if (tabDatas != null) {
                        List<TabBean> sub = tabDatas.get(typeIndex).getSub();
                        if (liveChannelTl != null) {
                            liveChannelTl.removeAllTabs();
                            if (sub != null && sub.size() > 0) {
                                for (TabBean tabBean : sub) {
                                    addChannelChildTable(tabBean.getName());
                                }
                                Objects.requireNonNull(liveChannelTl.getTabAt(0)).select();
                            }
                        }
                        if (tabDatas.get(typeIndex).getSub() == null || tabDatas.get(typeIndex).getSub().size() == 0) {
                            liveTypeId = tabDatas.get(typeIndex).getId();
                        }
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab != null && tab.getCustomView() != null) {
                    TextView title = tab.getCustomView().findViewById(R.id.item_tv);
                    if (title != null) {
                        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        title.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    private void addChannelChildTable(String str) {
        View inflate = layoutInflater.inflate(R.layout.item_tab_tv, null);
        TextView textView = inflate.findViewById(R.id.item_tv);
        textView.setText(str);
        liveChannelTl.addTab(liveChannelTl.newTab().setCustomView(inflate));
    }

    private void addTypeChildTable(String str) {
        View inflate = layoutInflater.inflate(R.layout.item_tab_tv, null);
        TextView textView = inflate.findViewById(R.id.item_tv);
        textView.setText(str);
        liveTypeTl.addTab(liveTypeTl.newTab().setCustomView(inflate));
    }

    private void initClick() {
        liveTypeBSFragment = LiveTypeBSFragment.newInstance();
        liveTypeChannelBSFragment = LiveTypeChannelBSFragment.newInstance();
        disposable.add(RxView.clicks(findView(R.id.prelive_close_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> onLiveCancel()));
        disposable.add(RxView.clicks(titleClearIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> titleEt.setText("")));
        disposable.add(RxTextView.afterTextChangeEvents(titleEt).subscribe(textViewAfterTextChangeEvent -> {
            if (textViewAfterTextChangeEvent.editable().length() > 0) {
                titleClearIv.setVisibility(View.VISIBLE);
            } else {
                titleClearIv.setVisibility(View.INVISIBLE);
            }
        }));
        disposable.add(RxView.clicks(findView(R.id.live_type_add_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    liveTypeBSFragment.show(getChildFragmentManager(), "liveTypeDialog");
                    liveTypeBSFragment.setDatas(tabDatas);
                    liveTypeBSFragment.setIndex(typeIndex);
                }));
        disposable.add(RxView.clicks(findView(R.id.live_type_channel_add_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    liveTypeChannelBSFragment.show(getChildFragmentManager(), "liveTypeChannelDialog");
                    if (tabDatas != null && tabDatas.size() > typeIndex)
                        liveTypeChannelBSFragment.setDatas(tabDatas.get(typeIndex).getSub());
                    liveTypeChannelBSFragment.setIndex(channelIndex);
                }));
        disposable.add(RxView.clicks(findView(R.id.prelive_camera_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe((o -> {
                    cameraType = cameraType == 1 ? 0 : 1;
                    Log.e("//", "initClick: "+cameraType );
//                    EventBus.getDefault().post(new CameraEvent());
                    if (getContext() instanceof LiveCameraActivity) {
                        ((LiveCameraActivity) getContext()).onSwitchCamera(new CameraEvent());
                    }
                })));
        disposable.add(RxView.clicks(findView(R.id.live_start_vertical_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    startType = VERTICAL_START;
                    startLiveClick();
                }));
        disposable.add(RxView.clicks(findView(R.id.live_start_horizontal_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    startType = HORIZONTAL_START;
                    startLiveClick();
                }));
        disposable.add(RxView.clicks(findView(R.id.prelive_setting_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (getActivity() != null) {
                        if (getActivity() instanceof LiveCameraActivity) {
                            ((LiveCameraActivity) getActivity()).showSettingView();
                        }
                    }
                }));
    }

    public static final int VERTICAL_START = 0;
    public static final int HORIZONTAL_START = 1;
    int startType = 0, cameraType = 1;

    /**
     * 开始直播前的检查
     */
    private void startLiveClick() {
//        liveTypeId = "8";
        if (titleEt.getText().toString().trim().length() <= 0) {
            ToastUtil.show("请输入直播间名称");
            return;
        }
        if (TextUtils.isEmpty(liveTypeId)) {
            ToastUtil.show("请选择直播类型");
            return;
        }

        String type = mAdapter.getType();
        if (type != null) {
            ConfigBean bean = AppConfig.getInstance().getConfig();
            if (userBean != null && bean != null) {
                String url;
                    url = bean.getWx_siteurl() + userBean.getId();
                SharedSdkUitl.getInstance().share(type,
                        bean.getShare_title(),
                        userBean.getUser_nicename() + bean.getShare_des(),
                        userBean.getAvatar(),
                        url,
                        mShareListener);
            }
        } else {
            live();
        }
    }

    //分享的回调
    private SharedSdkUitl.ShareListener mShareListener = new SharedSdkUitl.ShareListener() {
        @Override
        public void onSuccess(Platform platform) {
            ToastUtil.show(getString(R.string.share_success));
            if (!AppConfig.isUnlogin()) {
                HttpUtil.onFinishShare();
            }
            live();
        }

        @Override
        public void onError(Platform platform) {
            ToastUtil.show(getString(R.string.share_error));
        }

        @Override
        public void onCancel(Platform platform) {
            ToastUtil.show(getString(R.string.share_cancel));
        }
    };

    private void live() {
        String title = titleEt.getText().toString().trim();
        SharedPreferencesUtil.getInstance().saveString(SharedPreferencesUtil.LIVE_ROOM_NAME, title);
        HttpUtil.createRoomByTag(title, liveTypeId, startType, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    Intent intent;
                    if (startType == VERTICAL_START) {
                        intent = new Intent(mContext, LiveAnchorActivity.class);
                    } else {
                        intent = new Intent(mContext, LiveAnchorHorizontalActivity.class);
                    }
                    intent.putExtra("orientation", startType);
                    intent.putExtra("data", info[0]);
                    intent.putExtra("type", liveTypeId);
                    intent.putExtra("title", title);
                    intent.putExtra("cameraType", cameraType);
//                    startLiveClick();
                    startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                } else {
                    ToastUtil.show(msg);
                    if (code == 1002) {
                        startActivity(new Intent(getContext(), AnchorCertificateActivity.class));
                    }
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
        });
    }

    /**
     * 取消开播
     */
    private void onLiveCancel() {
        if (getContext() != null && getContext() instanceof LiveCameraActivity) {
            ((LiveCameraActivity) getContext()).finish();
        }
    }

    private int typeIndex = 0, channelIndex = 0;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getLiveTypeEvent(LiveTypeEvent event) {
        if (event != null) {
            typeIndex = event.getPosition();
            liveTypeTl.getTabAt(typeIndex).select();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getLiveTypeEvent(LiveBsEvent event) {
        if (event != null) {
            channelIndex = event.getPosition();
            liveChannelTl.getTabAt(channelIndex).select();
        }
    }

    @Override
    public void onDestroyView() {
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void getTabData() {
        HttpUtil.getAllLiveTagSub(mCallback);
    }

    private List<TabBean> tabDatas;
    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null && info.length > 0) {
                tabDatas = JSON.parseArray(Arrays.toString(info), TabBean.class);
                if (tabDatas != null && liveTypeTl != null) {
                    for (TabBean tabData : tabDatas) {
                        addTypeChildTable(tabData.getName());
                    }
                    if (tabDatas.size() > 0) {
                        Objects.requireNonNull(liveTypeTl.getTabAt(0)).select();
                    }
                }
            }
        }
    };


}
