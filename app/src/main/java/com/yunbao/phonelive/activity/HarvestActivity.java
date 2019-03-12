package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.event.RefreshUserInfoEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.ui.tools.StringUtil;
import com.yunbao.phonelive.ui.views.BindMobileActivity;
import com.yunbao.phonelive.ui.views.BindWechatActivity;
import com.yunbao.phonelive.ui.views.HarvestHistoryActivity;
import com.yunbao.phonelive.ui.views.LiveWatcherActivity;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

/**
 * Created by cxf on 2017/9/22.
 * 提现界面
 */

public class HarvestActivity extends AbsActivity {

    private TextView mAllCoinNumTv, mMoneyHint;
    private EditText mMoneyEt;
    private String inputMoney;
    private float mMoney = 0;
    public static final String BUG_TXT = "%";
    private LinearLayout etLl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_harvest;
    }

    @Override
    protected void main() {
        setTitle(getString(R.string.my_harvest));
        setTitleRightBtn(getString(R.string.my_harvest_history));
        mAllCoinNumTv = findViewById(R.id.harvest_coin_num_tv);
        mMoneyHint = findViewById(R.id.harvest_money_hint);
        mMoneyEt = findViewById(R.id.harvest_num_et);
        etLl = findViewById(R.id.et_ll);
        findViewById(R.id.title_line_v).setVisibility(View.INVISIBLE);
        initListener();
        initData();
    }

    private boolean canHarvest = false;

    private void initListener() {
        disposable.add(RxTextView.afterTextChangeEvents(mMoneyEt).subscribe(textViewAfterTextChangeEvent -> {
            if (textViewAfterTextChangeEvent.editable() != null) {
                inputMoney = textViewAfterTextChangeEvent.editable().toString();
            }
            showMoneyTv();
        }));
        mMoneyEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etLl.setBackground(getResources().getDrawable(R.drawable.bg_line_square_0b56_3));
            } else {
                etLl.setBackground(getResources().getDrawable(R.drawable.bg_line_square_cc_3));
            }
        });
        disposable.add(RxView.clicks(findViewById(R.id.subtitle_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> startActivity(new Intent(HarvestActivity.this, HarvestHistoryActivity.class))));
    }

    private void showMoneyTv() {
        if (TextUtils.isEmpty(inputMoney) || !StringUtil.isNumeric(inputMoney)) {
            mMoney = 0;
            mMoneyHint.setVisibility(View.VISIBLE);
            canHarvest = false;
        } else {
            mMoney = Float.valueOf(inputMoney);
            if (mMoney >= 30 && mMoney <= 2000) {
                mMoneyHint.setVisibility(View.INVISIBLE);
                canHarvest = true;
            } else {
                mMoneyHint.setVisibility(View.VISIBLE);
                canHarvest = false;
            }

        }
    }

    public void harvestClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cash://提现
                getCash();
                break;
        }
    }

    private void initData() {
        if (AppConfig.getInstance().getUserBean() != null) {
            mAllCoinNumTv.setText(AppConfig.getInstance().getUserBean().getVotes());
        }
    }


    private void getCash() {
        if (!canHarvest) {
            ToastUtil.show("请输入正确的提现金额");
            return;
        }
        if (AppConfig.getInstance().getUserBean() != null && AppConfig.getInstance().getUserBean().getIsbind() == 1 && AppConfig.getInstance().getUserBean().getWxtype() == 1) {
            HttpUtil.getCash(inputMoney, mCashCallback);
        } else if (AppConfig.getInstance().getUserBean() != null && AppConfig.getInstance().getUserBean().getIsbind() == 0) {
            DialogUitl.confirmNoTitleDialog(this, "为了您的账号安全请前往绑定手机!", "去绑定", true, new DialogUitl.Callback() {
                @Override
                public void confirm(Dialog dialog) {
                    Intent intent = new Intent(HarvestActivity.this, BindMobileActivity.class);
                    intent.putExtra("intentType", 1);
                    startActivity(intent);
                }

                @Override
                public void cancel(Dialog dialog) {
                }
            }).show();
        } else if (AppConfig.getInstance().getUserBean() != null && AppConfig.getInstance().getUserBean().getWxtype() == 0) {
            DialogUitl.confirmNoTitleDialog(this, "请绑定提款到账的微信号!", "去绑定", true, new DialogUitl.Callback() {
                @Override
                public void confirm(Dialog dialog) {
                    Intent intent = new Intent(HarvestActivity.this, BindWechatActivity.class);
                    intent.putExtra("intentType", 1);
                    startActivity(intent);
                }

                @Override
                public void cancel(Dialog dialog) {
                }
            }).show();
        }


    }

    private HttpCallback mCashCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info != null && info.length > 0) {
                String mCoin = JSON.parseObject(info[0]).getString("coin");
                AppConfig.getInstance().getUserBean().setVotes(mCoin);
                mAllCoinNumTv.setText(mCoin);
                mMoneyEt.setText("");
            }
            DialogUitl.messageDialog(mContext, getString(R.string.tip), msg, null).show();
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
    protected void onDestroy() {
        EventBus.getDefault().post(new RefreshUserInfoEvent());
        HttpUtil.cancel(HttpUtil.GET_PROFIT);
        HttpUtil.cancel(HttpUtil.GET_CASH);
        super.onDestroy();
    }
}
