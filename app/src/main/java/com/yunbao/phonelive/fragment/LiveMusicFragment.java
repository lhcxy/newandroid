package com.yunbao.phonelive.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAnchorActivity;
import com.yunbao.phonelive.adapter.LiveMusicAdapter;
import com.yunbao.phonelive.bean.LiveLrcBean;
import com.yunbao.phonelive.bean.LiveMusicBean;
import com.yunbao.phonelive.custom.NoAlphaItemAnimator;
import com.yunbao.phonelive.custom.ProgressTextView;
import com.yunbao.phonelive.db.MusicDbManager;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.DownloadCallback;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.LrcParser;
import com.yunbao.phonelive.utils.MusicFileUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/9/2.
 */

public class LiveMusicFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private EditText mInput;
    private RecyclerView mRecyclerView;
    private LiveMusicAdapter mAdapter;
    private View mLoading;
    private View mNoLocation;//没有本地歌曲
    private DownLoadActionListener mActionListener;
    private final int REQUEST_FILE_PERMISSION = 100;//文件读写权限的请求码，用在歌曲文件下载

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_live_music, null, false);
        dialog.setContentView(mRootView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(300);
        params.height = DpUtil.dp2px(400);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInput = (EditText) mRootView.findViewById(R.id.input);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    readLocationMusic();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());
        if (mAdapter != null) {
            mRecyclerView.setAdapter(mAdapter);
        }
        mLoading = mRootView.findViewById(R.id.loading);
        mNoLocation = mRootView.findViewById(R.id.no_location);
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_search).setOnClickListener(this);
        readLocationMusic();
    }

    /**
     * 读取本地存放的音乐
     */
    private void readLocationMusic() {
        List<LiveMusicBean> list = MusicDbManager.getInstace().queryList();
        if (list.size() > 0) {
            if (mNoLocation.getVisibility() == View.VISIBLE) {
                mNoLocation.setVisibility(View.GONE);
            }
            if (mAdapter == null) {
                initAdapter(list);
            } else {
                mAdapter.setList(list);
            }
        } else {
            if (mNoLocation.getVisibility() == View.GONE) {
                mNoLocation.setVisibility(View.VISIBLE);
            }
            if (mAdapter != null) {
                mAdapter.clear();
            }
        }
    }

    private void initAdapter(List<LiveMusicBean> list) {
        mAdapter = new LiveMusicAdapter(mContext, list);
        mActionListener = new DownLoadActionListener();
        mAdapter.setActionListener(mActionListener);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_search:
                search();
                break;
        }
    }

    private void search() {
        String key = mInput.getText().toString();
        if ("".equals(key)) {
            ToastUtil.show(getString(R.string.content_empty));
            return;
        }
        HttpUtil.searchMusic(key, mCallback);
    }

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
                    List<LiveMusicBean> list = JSONArray.parseArray(Arrays.toString(info), LiveMusicBean.class);
                    if (mAdapter == null) {
                        initAdapter(list);
                    } else {
                        mAdapter.setList(list);
                    }
                } else {
                    ToastUtil.show(getString(R.string.no_music));
                }
            } else {
                ToastUtil.show(msg);
            }
        }

        @Override
        public void onStart() {
            if (mLoading.getVisibility() == View.GONE) {
                mLoading.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFinish() {
            if (mLoading.getVisibility() == View.VISIBLE) {
                mLoading.setVisibility(View.GONE);
            }
        }
    };

    private class DownLoadActionListener implements LiveMusicAdapter.ActionListener {

        private LiveMusicBean mBean;
        private int mPosition;

        @Override
        public void onDownLoad(ProgressTextView textView, LiveMusicBean bean, int position) {
            mBean = bean;
            mPosition = position;
            switch (bean.getStatus()) {
                case LiveMusicBean.PRO_NOT://下载
                    checkFilePermission();
                    break;
//                case LiveMusicBean.PRO_END://播放
//                    LiveLrcBean lrcBean = LrcParser.getInstance().parse(bean.getAudio_id() + ".lrc");
//                    ((LiveAnchorActivity) mContext).playMusic(bean.getAudio_id(), lrcBean);
//                    break;
            }
        }

        /**
         * 下载歌曲的方法
         */
        public void downLoadMusic() {
            HttpUtil.getDownMusicUrl(mBean.getAudio_id(), mCallback);
        }

        private HttpCallback mCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        mBean.setStatus(LiveMusicBean.PRO_ING);
                        mAdapter.notifyItemChanged(mPosition);
                        JSONObject obj = JSON.parseObject(info[0]);
                        String mp3Url = obj.getString("audio_link");
                        String lrcContent = obj.getString("lrc_content");
                        MusicFileUtil.save(mBean.getAudio_id(), mp3Url, lrcContent, mDownloadCallback);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        };

        private DownloadCallback mDownloadCallback = new DownloadCallback() {
            @Override
            public void onSuccess() {
                MusicDbManager.getInstace().save(mBean);
                ToastUtil.show(mContext.getString(R.string.download_success));
            }

            @Override
            public void onProgress(int progress) {
                mBean.setProgress(progress);
                mAdapter.notifyItemChanged(mPosition);
                if (progress == 100) {
                    mBean.setStatus(LiveMusicBean.PRO_END);
                }
            }

            @Override
            public void onError() {
                ToastUtil.show(mContext.getString(R.string.download_failure));
            }
        };


        @Override
        public void onRemove(LiveMusicBean bean) {
            MusicFileUtil.delete(bean.getAudio_id());
        }
    }


    /**
     * 检查文件读写的权限
     */
    private void checkFilePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_FILE_PERMISSION);
            } else {
                mActionListener.downLoadMusic();
            }
        } else {
            mActionListener.downLoadMusic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FILE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mActionListener.downLoadMusic();
                } else {
                    ToastUtil.show(getString(R.string.storage_permission_refused));
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.SEARCH_MUSIC);
        HttpUtil.cancel(HttpUtil.GET_DOWN_MUSIC_URL);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
