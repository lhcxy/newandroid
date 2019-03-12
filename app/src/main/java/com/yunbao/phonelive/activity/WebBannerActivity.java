package com.yunbao.phonelive.activity;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.L;

/**
 * Created by cxf on 2017/8/9.
 * 通用的WebView页面
 */

public class WebBannerActivity extends AbsActivity {

    private WebView mWebView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_banner;
    }

    @Override
    protected void main() {
        String url = getIntent().getStringExtra("url");
        L.e("H5--->" + url);
        FrameLayout rootView = (FrameLayout) findViewById(R.id.content);
        mWebView = new WebView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(params);
        rootView.addView(mWebView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e("H5------------>" + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(view.getTitle());
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.loadUrl(url);
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }
}
