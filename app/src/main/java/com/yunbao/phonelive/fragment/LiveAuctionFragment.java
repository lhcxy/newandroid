package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAnchorActivity;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.utils.L;

import static android.app.Activity.RESULT_OK;

/**
 * Created by cxf on 2017/9/26.
 * 我的竞拍
 */

public class LiveAuctionFragment extends DialogFragment implements View.OnClickListener, View.OnTouchListener {

    private Context mContext;
    private RelativeLayout mRootView;
    private final int CHOOSE = 100;//Android 5.0以下的
    private final int CHOOSE_ANDROID_5 = 200;//Android 5.0以上的
    private ValueCallback<Uri> mValueCallback;
    private ValueCallback<Uri[]> mValueCallback2;
    private ProgressBar mProgressBar;
    private int mWidth;
    private int mHeight;
    private GLSurfaceView mGLSurfaceView;
    private int mSurfaceViewWidth;
    private int mSurfaceViewHeight;
    private float mLastX;
    private float mLastY;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.fragment_auction, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.leftToRightAnim);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressbar);
        mRootView.findViewById(R.id.btn_back).setOnClickListener(this);
        String url = AppConfig.HOST + "/index.php?g=Appapi&m=Auction&a=index&uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken() + "&addr=" + AppConfig.getInstance().getProvince() + AppConfig.getInstance().getCity() + "&stream=" + getArguments().getString("stream");
        mRootView.addView(createWebView(url));
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                mWidth = mRootView.getWidth();
                mHeight = mRootView.getHeight();
                mGLSurfaceView = new GLSurfaceView(mContext);
                ((LiveAnchorActivity) mContext).setCameraPreView(mGLSurfaceView);
                mSurfaceViewWidth = mWidth / 4;
                mSurfaceViewHeight = mHeight / 4;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mSurfaceViewWidth, mSurfaceViewHeight);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mGLSurfaceView.setLayoutParams(params);
                mGLSurfaceView.setOnTouchListener(LiveAuctionFragment.this);
                mRootView.addView(mGLSurfaceView);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((LiveAnchorActivity) mContext).setOriginCameraPreView();
    }

    private WebView createWebView(String url) {
        WebView webView = new WebView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, R.id.progressbar);
        webView.setLayoutParams(params);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String s = "phonelive://auction/";
                if (url.startsWith(s)) {
                    String auctionId = url.substring(s.length());
                    L.e("auctionId----->" + auctionId);
                    SocketUtil.getInstance().auctionStart(auctionId);
                    dismiss();
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }

            //以下是在各个Android版本中 WebView调用文件选择器的方法
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                openImageChooserActivity(valueCallback);
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                openImageChooserActivity(valueCallback);
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback,
                                        String acceptType, String capture) {
                openImageChooserActivity(valueCallback);
            }

            // For Android >= 5.0
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                mValueCallback2 = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                startActivityForResult(intent, CHOOSE_ANDROID_5);
                L.e("startActivityForResult---->");
                return true;
            }

        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        return webView;
    }

    private void openImageChooserActivity(ValueCallback<Uri> valueCallback) {
        mValueCallback = valueCallback;
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_flie)), CHOOSE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case CHOOSE://5.0以下选择图片后的回调
                processResult(resultCode, intent);
                break;
            case CHOOSE_ANDROID_5://5.0以上选择图片后的回调
                processResultAndroid5(resultCode, intent);
                break;
        }
    }


    private void processResult(int resultCode, Intent intent) {
        if (mValueCallback == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            Uri result = intent.getData();
            mValueCallback.onReceiveValue(result);
        } else {
            mValueCallback.onReceiveValue(null);
        }
        mValueCallback = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void processResultAndroid5(int resultCode, Intent intent) {
        if (mValueCallback2 == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            mValueCallback2.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
        } else {
            mValueCallback2.onReceiveValue(null);
        }
        mValueCallback2 = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                dismiss();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        float x = e.getRawX();
        float y = e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (dx != 0) {
                    float targetX = mGLSurfaceView.getX() + dx;
                    if (targetX < 0) {
                        targetX = 0;
                    }
                    int rightLimit = mWidth - mSurfaceViewWidth;
                    if (targetX > rightLimit) {
                        targetX = rightLimit;
                    }
                    mGLSurfaceView.setX(targetX);
                }
                if (dy != 0) {
                    float targetY = mGLSurfaceView.getY() + dy;
                    if (targetY < 0) {
                        targetY = 0;
                    }
                    int bottomLimit = mHeight - mSurfaceViewHeight;
                    if (targetY > bottomLimit) {
                        targetY = bottomLimit;
                    }
                    mGLSurfaceView.setY(targetY);
                }
        }
        mLastX = x;
        mLastY = y;
        return true;
    }
}
