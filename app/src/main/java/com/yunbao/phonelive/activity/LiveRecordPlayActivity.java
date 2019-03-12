package com.yunbao.phonelive.activity;

import android.content.Intent;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.custom.video.KsyVideoPlayer;

/**
 * Created by cxf on 2017/8/12.
 * 播放直播回放
 */

public class LiveRecordPlayActivity extends AbsActivity {

    private TextView mTitleView;
    private KsyVideoPlayer mPlayer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_record;
    }

    @Override
    protected void main() {
        mTitleView = (TextView) findViewById(R.id.title);
        mPlayer = (KsyVideoPlayer) findViewById(R.id.videoPlayer);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String url = intent.getStringExtra("url");
        mTitleView.setText(title);
        mPlayer.setDataSource(url);
        mPlayer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.pause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
        mPlayer = null;
    }


}
