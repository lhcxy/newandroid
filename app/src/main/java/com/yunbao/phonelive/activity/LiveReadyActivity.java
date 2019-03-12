package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.LiveTypeAdapter;
import com.yunbao.phonelive.adapter.SharedSdkAdapter;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.SharedSdkBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.DrawableTextView;
import com.yunbao.phonelive.fragment.ChooseImgFragment;
import com.yunbao.phonelive.fragment.LiveTimeChargeFragment;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.SharedSdkUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import java.io.File;

import cn.sharesdk.framework.Platform;

/**
 * Created by cxf on 2017/8/29.
 * 准备开播的activity
 */

public class LiveReadyActivity extends AbsActivity {

    private ChooseImgFragment mFragment;
    private ImageView mBackground;
    private EditText mEditTitle;
    private ImageView mImg;//开播的封面图片
    private File mTempFile;//临时图片文件
    private RecyclerView mRecyclerView;
    private SharedSdkAdapter mAdapter;
    private DrawableTextView mTypeTextView;
    private PopupWindow mLiveTypeWindow;
    private ListView mListView;
    private LiveTypeAdapter mLiveTypeAdapter;
    private String mLiveType = "0";//直播类型
    private String mTypeVal = "";
    private Drawable mNormalTypeDrawable;
    private Drawable mOtherTypeDrawable;
    private LiveTimeChargeFragment mTimeChargeFragment;
    private UserBean mUser;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_ready;
    }

    @Override
    protected void main() {
        initView();
    }

    private void initView() {
        mFragment = new ChooseImgFragment();
        mFragment.setOnCompleted(new CommonCallback<File>() {
            @Override
            public void callback(File file) {
                mTempFile = file;
                ImgLoader.display(file, mImg);
            }
        });
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(mFragment, "ChooseImgFragment").commit();
        mBackground = (ImageView) findViewById(R.id.bg);
        mEditTitle = (EditText) findViewById(R.id.edit_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new SharedSdkAdapter(AppConfig.getInstance().getConfig().getShare_type(), true, true);
        mRecyclerView.setAdapter(mAdapter);
        mImg = (ImageView) findViewById(R.id.img);
        mTypeTextView = (DrawableTextView) findViewById(R.id.live_type);
        mUser = AppConfig.getInstance().getUserBean();
        if (mUser != null) {
            ImgLoader.displayBlur(mUser.getAvatar(), mBackground);
        }
    }


    public void readyLiveClick(View v) {
        switch (v.getId()) {
            case R.id.img://选择直播封面图片
                chooseImg();
                break;
            case R.id.live_type://选择直播类型
                chooseLiveType(v);
                break;
            case R.id.btn_start_live://检查权限，开始直播
                mFragment.checkLivePermission();
                break;
        }
    }


    /**
     * 选择图片
     */
    private void chooseImg() {
        DialogUitl.chooseImageDialog(mContext, new DialogUitl.Callback2() {
            @Override
            public void confirm(Dialog dialog) {
                dialog.dismiss();
                mFragment.forwardCamera();
            }
        }, new DialogUitl.Callback2() {
            @Override
            public void confirm(Dialog dialog) {
                dialog.dismiss();
                mFragment.forwardAlumb();
            }
        }).show();
    }

    /**
     * 选择直播类型
     */
    private void chooseLiveType(final View parent) {
        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (mLiveTypeWindow == null) {
                    View contentView = LayoutInflater.from(mContext).inflate(R.layout.view_pop_live_type, null);
                    mLiveTypeWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    mLiveTypeWindow.setBackgroundDrawable(new ColorDrawable());
                    mLiveTypeWindow.setAnimationStyle(R.style.bottomToTopAnim);
                    mListView = (ListView) contentView.findViewById(R.id.listView);
                    mListView.setOnItemClickListener(mChooseLiveTypeListener);
                    contentView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mLiveTypeWindow.dismiss();
                        }
                    });
                }
                if (mLiveTypeAdapter == null) {
                    mLiveTypeAdapter = new LiveTypeAdapter(configBean.getLive_type());
                    mListView.setAdapter(mLiveTypeAdapter);
                } else {
                    mLiveTypeAdapter.setData(configBean.getLive_type());
                }
                mLiveTypeWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            }
        });
    }

    private AdapterView.OnItemClickListener mChooseLiveTypeListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mLiveTypeWindow.dismiss();
            String[] type = mLiveTypeAdapter.getItem(position);
            switch (type[0]) {
                case "0":
                    mLiveType = "0";
                    mTypeVal = "";
                    mTypeTextView.setText(getString(R.string.normal));
                    changeIcon();
                    break;
                case "1":
                    DialogUitl.inputDialog(mContext, getString(R.string.please_set_room_password), new DialogUitl.Callback3() {
                        @Override
                        public void confirm(Dialog dialog, String text) {
                            if ("".equals(text)) {
                                ToastUtil.show(getString(R.string.please_input_password));
                            } else {
                                dialog.dismiss();
                                mLiveType = "1";
                                mTypeVal = text;
                                mTypeTextView.setText(getString(R.string.password));
                                changeIcon();
                            }
                        }
                    }).show();
                    break;
                case "2":
                    DialogUitl.inputDialog(mContext, getString(R.string.please_set_charge_amount), new DialogUitl.Callback3() {
                        @Override
                        public void confirm(Dialog dialog, String text) {
                            if ("".equals(text)) {
                                ToastUtil.show(getString(R.string.please_input_money));
                            } else {
                                dialog.dismiss();
                                mLiveType = "2";
                                mTypeVal = text;
                                mTypeTextView.setText(getString(R.string.ticket));
                                changeIcon();
                            }
                        }
                    }).show();
                    break;
                case "3":
                    if (mTimeChargeFragment == null) {
                        mTimeChargeFragment = new LiveTimeChargeFragment();
                        mTimeChargeFragment.setOnConfrimClick(new CommonCallback<String>() {
                            @Override
                            public void callback(String typeVal) {
                                setTimeCharge(typeVal);
                            }
                        });
                    }
                    if (!mTimeChargeFragment.isAdded()) {
                        mTimeChargeFragment.show(getSupportFragmentManager(), "LiveTimeChargeFragment");
                    }
                    break;
            }
        }
    };


    public void setTimeCharge(String typeVal) {
        mLiveType = "3";
        mTypeVal = typeVal;
        mTypeTextView.setText(getString(R.string.time_charge));
        changeIcon();
    }

    /**
     * 改变房间类型的图标
     */
    private void changeIcon() {
        if ("0".equals(mLiveType)) {
            if (mNormalTypeDrawable == null) {
                mNormalTypeDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_lock2);
            }
            if (mTypeTextView.getLeftDrawable() != mNormalTypeDrawable) {
                mTypeTextView.setLeftDrawable(mNormalTypeDrawable);
            }
        } else {
            if (mOtherTypeDrawable == null) {
                mOtherTypeDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_lock1);
            }
            if (mTypeTextView.getLeftDrawable() != mOtherTypeDrawable) {
                mTypeTextView.setLeftDrawable(mOtherTypeDrawable);
            }
        }
    }


    public void startLive() {
        String type = mAdapter.getType();
        if (type != null) {
            ConfigBean bean = AppConfig.getInstance().getConfig();
            if (mUser != null && bean != null) {
//                String url = bean.getApp_android();
//                if (type.equals(SharedSdkBean.WX) || type.equals(SharedSdkBean.WX_PYQ)) {
                    String  url = bean.getWx_siteurl() + mUser.getId();
//                }
                SharedSdkUitl.getInstance().share(type,
                        bean.getShare_title(),
                        mUser.getUser_nicename() + bean.getShare_des(),
                        mUser.getAvatar(),
                        url,
                        mShareListener);
            }
        } else {
            forwardLiveActivity();
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
            forwardLiveActivity();
        }

        @Override
        public void onError(Platform platform) {
            ToastUtil.show(getString(R.string.share_error));
            forwardLiveActivity();
        }

        @Override
        public void onCancel(Platform platform) {
            ToastUtil.show(getString(R.string.share_cancel));
            forwardLiveActivity();
        }
    };

    private void forwardLiveActivity() {
        String title = mEditTitle.getText().toString();
        HttpUtil.createRoom(title, mLiveType, mTypeVal, mTempFile, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    Intent intent = new Intent(mContext, LiveAnchorActivity.class);
                    intent.putExtra("data", info[0]);
                    intent.putExtra("type", mLiveType);
                    intent.putExtra("typeVal", mTypeVal);
                    L.e("data------>" + info[0]);
                    startActivity(intent);
                    finish();
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
        });
    }

    @Override
    protected void onDestroy() {
        SharedSdkUitl.getInstance().releaseShareListener();
        HttpUtil.cancel(HttpUtil.GET_CONFIG);
        HttpUtil.cancel(HttpUtil.CREATE_ROOM);
        super.onDestroy();
    }

}
