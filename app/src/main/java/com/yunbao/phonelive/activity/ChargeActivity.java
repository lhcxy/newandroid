package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.ChoseCallback;
import com.yunbao.phonelive.pay.PayCallback;
import com.yunbao.phonelive.pay.ali.AliPayTask;
import com.yunbao.phonelive.pay.wx.WxPayTask;
import com.yunbao.phonelive.ui.dialog.ChargeTypeBsF;
import com.yunbao.phonelive.ui.helper.GridSpacingItemDecoration;
import com.yunbao.phonelive.ui.views.ChargeHistoryActivity;
import com.yunbao.phonelive.ui.views.adapter.RechargeRvAdapter;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.yunbao.phonelive.AppConfig.TQB_RATE;

/**
 * Created by cxf on 2017/9/19.
 * 充值页面
 */

public class ChargeActivity extends AbsActivity {
    private TextView mCoin, subTitleTv, tqbTv, bmhTv, etHintTv;
    //            bottomMoneyTv;
    private String mTotalCoin;
    private String mFrom;
    private int mAliSwitch;//后台控制支付宝开启的开关  1开启 0 关闭
    private int mWxSwitch;//后台控制微信支付开启的开关
    //    private CheckBox chargeZfbCb, chargeWxCb;
    private String payMoney = "";
    private RecyclerView chargeRv;
    private RechargeRvAdapter rvAdapter;
    private EditText moneyEt;
    private int rechargeType = 0; //支付渠道 0=支付宝 1=微信
    private String moneyHint;
    private ImageView etCheckIv;
    private RelativeLayout etRl;
    private ChargeTypeBsF chargeTypeBsF;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_charge;
    }

    @Override
    protected void main() {
        setTitle(getResources().getString(R.string.recharge_txt));
        moneyHint = getResources().getString(R.string.txt_charge_bottom_money);
        mCoin = findViewById(R.id.charge_coin_num_tv);
        mFrom = getIntent().getStringExtra("from");
        subTitleTv = findViewById(R.id.subtitle_tv);
//        chargeZfbCb = findViewById(R.id.charge_zfb_cb);
//        chargeWxCb = findViewById(R.id.charge_wx_cb);
        chargeRv = findViewById(R.id.recharge_num_rv);
        moneyEt = findViewById(R.id.charge_money_et);
        tqbTv = findViewById(R.id.charge_coin_tqb_tv);
        etRl = findViewById(R.id.charge_money_rl);
        etCheckIv = findViewById(R.id.charge_money_check_iv);
        etHintTv = findViewById(R.id.charge_money_hint_tv);
        bmhTv = findViewById(R.id.charge_coin_bmh_tv);
//        bottomMoneyTv = findViewById(R.id.charge_bottom_pay_num_tv);
        subTitleTv.setVisibility(View.VISIBLE);
        subTitleTv.setText("充值记录");
        chargeTypeBsF = ChargeTypeBsF.newInstance();
        chargeTypeBsF.setCallback(choseIndex -> {
            if (choseIndex == 0) {
                aliPay();
            } else {
                wxPay();
            }
        });
        initPayTypeView();
        initListener();
        initData();
    }


    private void initListener() {
        disposable.add(RxView.clicks(subTitleTv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(
                o -> startActivity(new Intent(ChargeActivity.this, ChargeHistoryActivity.class))
        ));
        disposable.add(RxTextView.afterTextChangeEvents(moneyEt).subscribe(textViewAfterTextChangeEvent -> {
            if (TextUtils.isEmpty(textViewAfterTextChangeEvent.editable().toString().trim())) {
                payMoney = "";
            } else {
                payMoney = textViewAfterTextChangeEvent.editable().toString().trim();
                payMoney = String.valueOf(new BigDecimal(Double.valueOf(payMoney) / TQB_RATE).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            setBottomMoney();
        }));
        moneyEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                rvAdapter.setSelectedIndex(-1);
                payMoney = "";
                setBottomMoney();
                etRl.setBackground(getResources().getDrawable(R.drawable.bg_line_square_0b56_3));
                etCheckIv.setVisibility(View.VISIBLE);
            } else {
                etCheckIv.setVisibility(View.INVISIBLE);
                etRl.setBackground(getResources().getDrawable(R.drawable.bg_line_square_cc_3));
            }
        });
        disposable.add(RxView.clicks(findViewById(R.id.charge_btn)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (TextUtils.isEmpty(payMoney) || "0".equals(payMoney)) {
                ToastUtil.show("请选择充值金额");
                return;
            }
            if (Double.valueOf(payMoney) < 5) {
                ToastUtil.show("最少充值50探球币，请检测充值金额");
                return;
            }
            chargeTypeBsF.show(getSupportFragmentManager(), "chargeType");

        }));

    }

    private void initPayTypeView() {
//        if (rechargeType == 0) {
//            chargeWxCb.setChecked(false);
//            chargeZfbCb.setChecked(true);
//        } else {
//            chargeZfbCb.setChecked(false);
//            chargeWxCb.setChecked(true);
//        }
    }

    private void initData() {
        HttpUtil.getBalance(mCallback);
        chargeRv.setLayoutManager(new GridLayoutManager(this, 3));
        chargeRv.addItemDecoration(new GridSpacingItemDecoration(3, DpUtil.dp2px(8), true));
        rvAdapter = new RechargeRvAdapter((item, position) -> {
            moneyEt.setText("");
            moneyEt.clearFocus();
            payMoney = rvAdapter.get(position);
            rvAdapter.setSelectedIndex(position);
//            setBottomMoney();
        });
        chargeRv.setAdapter(rvAdapter);
        payMoney = rvAdapter.get(0);
        rvAdapter.setSelectedIndex(0);
//        setBottomMoney();
    }

    private void setBottomMoney() {
        if (TextUtils.isEmpty(payMoney)) {
            etHintTv.setText("探球币");
        } else
            etHintTv.setText(String.format(moneyHint, payMoney));

    }

    private String mTotalCarrot = "";
    private String mTotalVotes = "";
    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            JSONObject obj = JSON.parseObject(info[0]);
            mTotalCoin = obj.getString("coin");
            mTotalCarrot = obj.getString("carrot");
            mTotalVotes = obj.getString("votes");
            AppConfig.getInstance().getUserBean().setCoin(mTotalCoin);
            AppConfig.getInstance().getUserBean().setCarrot(mTotalCarrot);
            AppConfig.getInstance().getUserBean().setVotes(mTotalVotes);
            mCoin.setText(mTotalVotes);
            tqbTv.setText(mTotalCoin);
            bmhTv.setText(mTotalCarrot);
            mAliSwitch = obj.getIntValue("aliapp_switch");
            mWxSwitch = obj.getIntValue("wx_switch");
            AliPayTask.sPartner = obj.getString("aliapp_partner");
            AliPayTask.sSellerId = obj.getString("aliapp_seller_id");
            AliPayTask.sPrivateKey = obj.getString("aliapp_key_android");
            WxPayTask.sAppId = obj.getString("wx_appid");
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


    /**
     * 支付宝支付
     */
    public void aliPay() {
        new AliPayTask(this, payMoney, mPayCallback).getOrder();
    }

    /**
     * 微信支付
     */
    public void wxPay() {
        new WxPayTask(this, payMoney, mPayCallback).getOrder();
    }

    private PayCallback mPayCallback = new PayCallback() {
        @Override
        public void onSuccess(int coin) {
            ToastUtil.show(getString(R.string.charge_success));

            HttpUtil.getCoin(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        String coin1 = obj.getString("coin");
                        String carrot = obj.getString("carrot");
                        if (!TextUtils.isEmpty(coin1)) {
                            tqbTv.setText(coin1);
                            AppConfig.getInstance().getUserBean().setCoin(coin1);
                        }
                        if (!TextUtils.isEmpty(carrot)) {
                            bmhTv.setText(carrot);
                            AppConfig.getInstance().getUserBean().setCarrot(carrot);
                        }
                    }
                }
            });
            if ("live".equals(mFrom)) {//这是从直播间进来的充值的
                setResult(RESULT_OK);
            }
        }

        @Override
        public void onFailuer() {
            ToastUtil.show(getString(R.string.charge_failure));
        }
    };

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_ALI_ORDER);
        HttpUtil.cancel(HttpUtil.GET_WX_ORDER);
        HttpUtil.cancel(HttpUtil.GET_BALANCE);
        mPayCallback = null;
        super.onDestroy();
    }
}
