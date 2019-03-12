package com.yunbao.phonelive.activity;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/8/9.
 * 装备中心
 */

public class EquipActivity extends AbsActivity {

    private WebView mWebView;
    private String mUrl1;
    private String mUrl2;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_equip;
    }

    @Override
    protected void main() {
        LinearLayout rootView = (LinearLayout) findViewById(R.id.rootView);
        mWebView = new WebView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(params);
        rootView.addView(mWebView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(view.getTitle());
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        String uid = AppConfig.getInstance().getUid();
        String token = AppConfig.getInstance().getToken();
        String host = AppConfig.HOST;
        mUrl1 = host + "/index.php?g=Appapi&m=Car&a=mycar&uid=" + uid + "&token=" + token;
        mUrl2 = host + "/index.php?g=Appapi&m=Liang&a=myliang&uid=" + uid + "&token=" + token;
        mWebView.loadUrl(mUrl1);
    }


    public void equipClick(View v) {
        switch (v.getId()) {
            case R.id.btn_zuoqi:
                mWebView.loadUrl(mUrl1);
                break;
            case R.id.btn_lianghao:
                mWebView.loadUrl(mUrl2);
                break;
        }
    }

}
