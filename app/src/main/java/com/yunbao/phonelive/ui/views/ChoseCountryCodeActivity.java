package com.yunbao.phonelive.ui.views;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.bean.CountryCodeBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.adapter.ChoseCountryCodeAdapter;
import com.yunbao.phonelive.utils.AssetUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChoseCountryCodeActivity extends AbsActivity implements OnItemClickListener {
    public static final int CCC_SUCCESS_CODE = 0x121;
    private String json;
    private List<CountryCodeBean> datas;
    private RecyclerView countryCodeRv;
    private ChoseCountryCodeAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_chose_country_code;
    }

    @Override
    protected void main() {
        countryCodeRv = findViewById(R.id.mine_package_rv);
        countryCodeRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        json = AssetUtil.getJson("CountryCode.json", this);
        if (!TextUtils.isEmpty(json)) {
            datas = JSON.parseArray(json, CountryCodeBean.class);
            adapter = new ChoseCountryCodeAdapter(datas);
            adapter.setListener(this);
            countryCodeRv.setAdapter(adapter);
        } else finish();
        initListener();
    }

    private void initListener() {
        disposable.add(RxView.clicks(findViewById(R.id.title_back_iv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> finish()));
    }


    @Override
    public void onItemClick(Object item, int position) {
        Intent intent = new Intent();
        intent.putExtra("result", datas.get(position));
        //设置返回数据
        setResult(CCC_SUCCESS_CODE, intent);
        finish();
    }
}
