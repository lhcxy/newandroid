package com.yunbao.phonelive.ui.views.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.TabBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.helper.GridSpacingItemDecoration;
import com.yunbao.phonelive.ui.helper.TabChannelHelper;
import com.yunbao.phonelive.ui.views.AnchorCertificateActivity;
import com.yunbao.phonelive.ui.views.adapter.CertificateDescRvAdapter;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

public class CertificateDescFragment extends BaseLazyFragment {
    private EditText descEt;
    private RecyclerView descTypeRv;
    private TextView descNumTv;
    private ArrayList<TabBean> fullData;
    private CertificateDescRvAdapter rvAdapter;
    private String selectedName = "";
    private String etNumStr;

    public static CertificateDescFragment newInstance() {
        CertificateDescFragment fragment = new CertificateDescFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_certificate_desc;
    }

    @Override
    protected void initView() {
        etNumStr = getResources().getString(R.string.txt_num_persent200);
        descEt = findView(R.id.certificate_recommend_et);
        descNumTv = findView(R.id.certificate_recommend_num_tv);
        descTypeRv = findView(R.id.live_type_rv);
        disposable.add(RxView.clicks(findView(R.id.bind_photo_action_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> commit()));

        rvAdapter = new CertificateDescRvAdapter();
        rvAdapter.setListener((item, position) -> {
            selectedName = fullData.get(position).getName();
            rvAdapter.setSelectedIndex(position);
        });
        descTypeRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        descTypeRv.addItemDecoration(new GridSpacingItemDecoration(4, DpUtil.dp2px(16), true, true));
        descTypeRv.setAdapter(rvAdapter);
        descNumTv.setText(getResources().getString(R.string.txt_harvest_coin_money));
        disposable.add(RxTextView.afterTextChangeEvents(descEt).subscribe(textViewAfterTextChangeEvent ->
                descNumTv.setText(String.format(etNumStr, textViewAfterTextChangeEvent.editable().length()))
        ));
    }

    private void commit() {

        if (TextUtils.isEmpty(descEt.getText().toString().trim())) {
            ToastUtil.show("请输入自我介绍");
            return;
        }
        String trim = descEt.getText().toString().trim();
        if (trim.length() < 20) {
            ToastUtil.show("请输入自我介绍,字数在20-200之间");
            return;
        }
        if (TextUtils.isEmpty(selectedName)) {
            ToastUtil.show("请选择直播分类");
            return;
        }


        if (getActivity() != null && getActivity() instanceof AnchorCertificateActivity) {
            ((AnchorCertificateActivity) getActivity()).commit(trim, selectedName);
        }
    }

    @Override
    protected void initData() {
        fullData = TabChannelHelper.getInstance().getFullData(getContext());
        if (rvAdapter != null) {
            rvAdapter.setDatas(fullData);
        }
    }
}
