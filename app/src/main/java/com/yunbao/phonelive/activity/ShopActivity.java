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
 * 在线商城
 */

public class ShopActivity extends AbsActivity {

    private WebView mWebView;
    private String mUrl1;
    private String mUrl2;
    private String mUrl3;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shop;
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
        mUrl1 = host + "/index.php?g=Appapi&m=Vip&a=index&uid=" + uid + "&token=" + token;
        mUrl2 = host + "/index.php?g=Appapi&m=Liang&a=index&uid=" + uid + "&token=" + token;
        mUrl3 = host + "/index.php?g=Appapi&m=Car&a=index&uid=" + uid + "&token=" + token;
        mWebView.loadUrl(mUrl1);
    }


    public void shopClick(View v) {
        switch (v.getId()) {
            case R.id.btn_huiyuan:
                mWebView.loadUrl(mUrl1);
                break;
            case R.id.btn_lianghao:
                mWebView.loadUrl(mUrl2);
                break;
            case R.id.btn_zuoqi:
                mWebView.loadUrl(mUrl3);
                break;
        }
    }

}
