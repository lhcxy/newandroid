package com.yunbao.phonelive.custom.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/8/13.
 */

public class VideoController extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Context mContext;
    private ImageView mPlayBtn;
    private SeekBar mSeekBar;
    private TextView mCurDuration;
    private TextView mTotalDuration;
    private boolean mPause;
    private VideoControllerListener mControllerListener;

    public VideoController(Context context) {
        this(context, null);
    }

    public VideoController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_video_controller, this, false);
        addView(view);
        mPlayBtn = (ImageView) view.findViewById(R.id.btn_play);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mCurDuration = (TextView) view.findViewById(R.id.curDuration);
        mTotalDuration = (TextView) view.findViewById(R.id.totalDuration);
        mPlayBtn.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    public void setControllerListener(VideoControllerListener listener) {
        mControllerListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mPause) {
            if (mControllerListener != null) {
                mControllerListener.play();
                mPlayBtn.setImageResource(R.mipmap.icon_video_pause);
            }
        } else {
            if (mControllerListener != null) {
                mControllerListener.pausePlay();
                mPlayBtn.setImageResource(R.mipmap.icon_video_play);
            }
        }
        mPause = !mPause;
    }

    public void setProgress(int progress) {
        mSeekBar.setProgress(progress);
    }

    public void setSecondaryProgress(int progress) {
        mSeekBar.setSecondaryProgress(progress);
    }

    public void setCurDuration(String curDuration) {
        mCurDuration.setText(curDuration);
    }

    public void setTotalDuration(String totalDuration) {
        mTotalDuration.setText(totalDuration);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mControllerListener != null) {
            mControllerListener.seekTo(seekBar.getProgress());
        }
    }

    public interface VideoControllerListener {
        void play();

        void pausePlay();

        void seekTo(int progress);
    }
}
