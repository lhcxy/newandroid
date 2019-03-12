package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.ChargeActivity;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.adapter.GiftListAdapter;
import com.yunbao.phonelive.bean.GiftBean;
import com.yunbao.phonelive.custom.NoAlphaItemAnimator;
import com.yunbao.phonelive.event.LiveRoomCloseEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.GiftListener;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/8/19.
 * 观众直播间送礼物弹窗
 */

public class LiveGiftFragment extends DialogFragment implements OnItemClickListener<GiftBean>, View.OnClickListener {

    protected Context mContext;
    private View mRootView;
    private ViewPager mViewPager;
    private TextView mCoin;
    private TextView mSendBtn;
    private RelativeLayout mSendBtnLian;
    private View mLoadingView;
    //    private LayoutInflater mInflater;
    private List<List<GiftBean>> mList;

    //显示ViewPager小圆点
//    private RadioGroup mIndicatorGroup;
    private GiftListAdapter[] mAdapters;
    private GiftBean mSelectBean;
    private TextView mLianText;

    private Handler mHandler;
    private int mCount;//倒计时的数字
    private ImageView selectedIv;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_gift, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        EventBus.getDefault().register(this);
    }

    private TextView giftListTv, giftPackageTv;
    private EditText giftCountEt;

    private void initView() {
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
//        mInflater = LayoutInflater.from(mContext);
        mCoin = (TextView) mRootView.findViewById(R.id.coin);
        mLoadingView = mRootView.findViewById(R.id.loading);
//        mIndicatorGroup = (RadioGroup) mRootView.findViewById(R.id.indicator_group);
        mSendBtn = (TextView) mRootView.findViewById(R.id.btn_send_gift);
        selectedIv = mRootView.findViewById(R.id.gift_selected_iv);
        giftListTv = mRootView.findViewById(R.id.gift_list_tv);
        giftPackageTv = mRootView.findViewById(R.id.gift_my_package_tv);
        giftCountEt = mRootView.findViewById(R.id.gift_count_et);
        giftListTv.setOnClickListener(this);
        giftPackageTv.setOnClickListener(this);

//        if (mSelectBean != null) {
//            mSendBtn.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_btn_send_gift_checked));
//        } else {
//            mSendBtn.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_btn_send_gift_unchecked));
//        }
        mSendBtn.setOnClickListener(this);
        mSendBtnLian = (RelativeLayout) mRootView.findViewById(R.id.btn_send_gift_lian);
        mSendBtnLian.setOnClickListener(this);
        mLianText = (TextView) mRootView.findViewById(R.id.lian_text);
        mRootView.findViewById(R.id.btn_charge).setOnClickListener(this);
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    mCount--;
                    if (mCount == 0) {
                        mSendBtnLian.setVisibility(View.GONE);
                        mSendBtn.setVisibility(View.VISIBLE);
                    } else {
                        mLianText.setText(String.valueOf(mCount));
                        sendEmptyMessageDelayed(0, 1000);
                    }
                }
            };
        }

        giftCountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("0")) {
                    giftCountEt.setText("");
                }
            }
        });


        if (mList != null) {
            showGiftList();
        }
        HttpUtil.getGiftList(mCallback);

    }

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            JSONObject obj = JSON.parseObject(info[0]);
            mCoin.setText(obj.getString("coin"));
            List<GiftBean> list = JSON.parseArray(obj.getString("giftlist"), GiftBean.class);
            if (mList == null) {
                mList = new ArrayList<>();
                mList.add(list);
//                mList.add(new ArrayList<>());
                if (list != null && list.size() > 0) {
                    mSelectBean = list.get(0);
                    ImgLoader.display(mSelectBean.getGifticon(), selectedIv);
                }
            }
            HttpUtil.getGiftPackage(mPackageCallback);
        }

        @Override
        public void onFinish() {
//            mLoadingView.setVisibility(View.GONE);
        }
    };
    private HttpCallback mPackageCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
//            JSONObject obj = JSON.parseObject(info[0]);
            List<GiftBean> list = JSON.parseArray(Arrays.toString(info), GiftBean.class);
            if (mList == null) {
                mList = new ArrayList<>();
            }
            if (list != null) {
                for (GiftBean giftBean : list) {
                    giftBean.setPackageGift(true);
                }
            }
            mList.add(list);

            showGiftList();
        }

        @Override
        public void onFinish() {
            mLoadingView.setVisibility(View.GONE);
        }
    };


//    private void addIndicator() {
//        View v = mInflater.inflate(R.layout.view_gift_indicator, mIndicatorGroup, false);
//        mIndicatorGroup.addView(v);
//    }

//    private void changeIndicatorColor(int position) {
//        for (int i = 0; i < mIndicatorGroup.getChildCount(); i++) {
//            RadioButton button = (RadioButton) mIndicatorGroup.getChildAt(i);
//            if (i == position) {
//                button.setChecked(true);
//            } else {
//                button.setChecked(false);
//            }
//        }
//    }

    private RecyclerView getRecyclerView(int i) {
        RecyclerView recyclerView = new RecyclerView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(params);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new NoAlphaItemAnimator());
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mAdapters[i] = new GiftListAdapter(mContext, mList.get(i));
        mAdapters[i].setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapters[i]);
        return recyclerView;
    }

    /**
     * 更新剩余的钱
     *
     * @param coin
     */
    public void updateCoin(String coin) {
        mCoin.setText(coin);
    }

    private void showGiftList() {
        int size = mList.size();
        final View[] views = new View[size];
        mAdapters = new GiftListAdapter[size];

        for (int i = 0; i < size; i++) {
            views[i] = getRecyclerView(i);
//            addIndicator();
        }
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View v = views[position];
                container.addView(v);
                return v;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views[position]);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                changeIndicatorColor(position);
                if (position == 0) {
                    giftListTv.setTextColor(getResources().getColor(R.color.footLightGreyTextColor));
                    giftPackageTv.setTextColor(getResources().getColor(R.color.footNotHintTextColor));
                } else {
                    giftPackageTv.setTextColor(getResources().getColor(R.color.footLightGreyTextColor));
                    giftListTv.setTextColor(getResources().getColor(R.color.footNotHintTextColor));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        changeIndicatorColor(0);
    }

    @Override
    public void onItemClick(GiftBean bean, int position) {
        bean.setChecked(true);
        if (mSelectBean != null) {
            if (bean.getId().equals(mSelectBean.getId())) {
                return;
            }
            mSelectBean.setChecked(false);
            mAdapters[mSelectBean.getPage()].notifyItemChanged(mSelectBean.getPosition(), GiftListAdapter.UNCHECKED);
            mAdapters[bean.getPage()].notifyItemChanged(bean.getPosition(), GiftListAdapter.CHECKED);
        } else {
            mSendBtn.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_btn_send_gift_checked));
            mAdapters[bean.getPage()].notifyItemChanged(bean.getPosition(), GiftListAdapter.CHECKED);
        }
        mSelectBean = bean;
        if (mSendBtn.getVisibility() == View.GONE) {
            mSendBtn.setVisibility(View.VISIBLE);
        }
        if (mSendBtnLian.getVisibility() == View.VISIBLE) {
            mSendBtnLian.setVisibility(View.GONE);
        }
        mHandler.removeMessages(0);
        ImgLoader.display(mSelectBean.getGifticon(), selectedIv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_gift:
                sendGift();
                break;
//            case R.id.btn_send_gift_lian:
//                sendGiftLian();
//                break;
            case R.id.btn_charge://跳转到充值页面
                dismiss();
                startActivity(new Intent(mContext, ChargeActivity.class));
                break;
            case R.id.gift_list_tv:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.gift_my_package_tv:
                mViewPager.setCurrentItem(1);
                break;
        }

    }

    private void sendGift() {
        if (mSelectBean == null) {
            return;
        }
        if (TextUtils.isEmpty(giftCountEt.getText().toString().trim())) {
            ToastUtil.show("请输入礼物数量!");
            return;
        }

//        if (mSelectBean.getType() == 1) {
//            mSendBtn.setVisibility(View.GONE);
//            mSendBtnLian.setVisibility(View.VISIBLE);
//            mCount = 5;
//            mLianText.setText(String.valueOf(mCount));
//            mHandler.sendEmptyMessageDelayed(0, 1000);
//        }
        mSelectBean.setCount(giftCountEt.getText().toString().trim());
        mSelectBean.setEvensend("n");
        doSendGift();
    }

//    private void sendGiftLian() {
//        if (mCount != 5) {
//            mCount = 5;
//            mLianText.setText(String.valueOf(mCount));
//        }
//        mHandler.removeMessages(0);
//        if (mSelectBean.getType() == 1) {
//            mHandler.sendEmptyMessageDelayed(0, 1000);
//        }
//        mSelectBean.setEvensend("y");
//        doSendGift();
//    }

    GiftListener listener;

    public void setListener(GiftListener listener) {
        this.listener = listener;
    }

    private void doSendGift() {
        if (listener != null) {
            listener.sendGift(mSelectBean);
        } else if (mContext instanceof LiveAudienceActivity)
            ((LiveAudienceActivity) mContext).sendGift(mSelectBean);
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_GIFT_LIST);
        HttpUtil.cancel(HttpUtil.GET_GIFT_PACKAGE);
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mList = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //接收直播间关闭事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveRoomCloseEvent(LiveRoomCloseEvent e) {
        dismiss();
    }

    public void updateData(GiftBean mTempGiftBean) {
        if (mList != null && mTempGiftBean != null && mList.size() > 1 && mList.get(1) != null) {
            for (GiftBean giftBean : mList.get(1)) {
                if (giftBean.getId().equals(mTempGiftBean.getId())) {
                    giftBean.setNum(mTempGiftBean.getNum());
                    if (TextUtils.isEmpty(giftBean.getNum()) || giftBean.getNum().equals("0")) {
                        mList.get(1).remove(giftBean);
                    }
                    if (mAdapters != null && mAdapters.length > 1) {
                        mAdapters[1].notifyDataSetChanged();
                    }
                    break;
                }
            }
        }
    }
}
