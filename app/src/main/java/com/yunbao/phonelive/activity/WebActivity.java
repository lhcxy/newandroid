package com.yunbao.phonelive.activity;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.L;

/**
 * Created by cxf on 2017/8/9.
 * 通用的WebView页面
 */

public class WebActivity extends AbsActivity {

    private ProgressBar mProgressBar;
    private WebView mWebView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected void main() {
        String url = getIntent().getStringExtra("url");
        L.e("H5--->" + url);
        LinearLayout rootView = (LinearLayout) findViewById(R.id.rootView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mWebView = new WebView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(params);
        rootView.addView(mWebView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e("H5-------->" + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(view.getTitle());
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.loadUrl(url);
    }

    private boolean canGoBack() {
        String url = mWebView.getUrl();
        if(TextUtils.isEmpty(url)||"about:blank".equals(url)){
            return false;
        }
        boolean canGoBack = mWebView.canGoBack();
        Uri uri = null;
        try {
            uri = Uri.parse(url);
            String m = uri.getQueryParameter("m");
            if ("family".equalsIgnoreCase(m)) {
                String a = uri.getQueryParameter("a");
                if ("home".equalsIgnoreCase(a)) {
                    canGoBack = false;
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            canGoBack = false;
        }
        return canGoBack;
    }

    @Override
    public void onBackPressed() {
        if (canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }
}
