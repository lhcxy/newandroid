package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveRecordPlayActivity;
import com.yunbao.phonelive.adapter.LiveRecordAdapter;
import com.yunbao.phonelive.bean.LiveRecordBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.List;

/**
 * Created by cxf on 2017/8/12.
 * 直播回放
 */

public class LiveRecordFragment extends AbsFragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private List<LiveRecordBean> mList;
    private Dialog mLoadingDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_record;
    }

    @Override
    protected void main() {
        Bundle bundle = getArguments();
        String data = bundle.getString("liverecord");
        mList = JSON.parseArray(data, LiveRecordBean.class);
        if (mList.size() > 0) {
            mListView = (ListView) mRootView.findViewById(R.id.listView);
            mListView.setAdapter(new LiveRecordAdapter(mContext, mList));
            mListView.setOnItemClickListener(this);
        } else {
            View v = mRootView.findViewById(R.id.no_record);
            v.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LiveRecordBean bean = mList.get(position);
        final String title = bean.getTitle();
        HttpUtil.getAliCdnRecord(bean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info.length > 0) {
                    JSONObject object = JSON.parseObject(info[0]);
                    String url = object.getString("url");
                    L.e("直播回放的url--->" + url);
                    Intent intent = new Intent(mContext, LiveRecordPlayActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("title", title);
                    startActivity(intent);
                } else {
                    ToastUtil.show(getString(R.string.live_record_not_find));
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                if (mLoadingDialog == null) {
                    mLoadingDialog = DialogUitl.loadingDialog(mContext);
                }
                return mLoadingDialog;
            }
        });
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_ALI_CDN_RECORD);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
