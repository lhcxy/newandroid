package com.phonelive.game.game.ebb;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.phonelive.game.GameIconUitl;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.WordUtil;

/**
 * Created by cxf on 2017/10/20.
 */

public class GameEbbView extends FrameLayout {

    private Context mContext;
    private ImageView mLeftImg;
    private ImageView mRightImg;
    private TextView mTotalBet;
    private TextView mMyBet;
    private ImageView mResult;
    private View mCover;
    private Animation mResultAnim;
    private int mTotalBetVal;
    private int mMyBetVal;
    private Handler mHandler;

    public GameEbbView(Context context) {
        this(context, null);
    }

    public GameEbbView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameEbbView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mResult.setVisibility(VISIBLE);
                mResult.startAnimation(mResultAnim);
            }
        };
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = LayoutInflater.from(mContext).inflate(R.layout.game_view_ebb_role, this, false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        addView(view);
        mLeftImg = (ImageView) view.findViewById(R.id.bk_left);
        mRightImg = (ImageView) view.findViewById(R.id.bk_right);
        mTotalBet = (TextView) view.findViewById(R.id.coin_total);
        mMyBet = (TextView) view.findViewById(R.id.coin_my);
        mResult = (ImageView) view.findViewById(R.id.result);
        mCover = view.findViewById(R.id.cover);
        mResult.setVisibility(INVISIBLE);
        mResultAnim = new ScaleAnimation(0.2f, 1, 0.2f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mResultAnim.setDuration(300);
    }

    public void updateBetVal(int betVal, boolean isSelf) {
        mTotalBetVal += betVal;
        showTotalBet();
        if (isSelf) {
            mMyBetVal += betVal;
            showSelfBet();
        }

    }

    public void setBetVal(int totalBet, int myBet) {
        mTotalBetVal = totalBet;
        mMyBetVal = myBet;
        if (mTotalBetVal > 0) {
            showTotalBet();
        }
        if (mMyBetVal > 0) {
            showSelfBet();
        }
    }

    /**
     * 显示下注总金额
     */
    private void showTotalBet() {
        String result;
        if (mTotalBetVal >= 10000) {
            if (mTotalBetVal % 10000 == 0) {
                result = String.valueOf(mTotalBetVal / 10000) + WordUtil.getString(R.string.game_bet_wan);
            } else {
                result = String.format("%.1f",mTotalBetVal / 10000f) + WordUtil.getString(R.string.game_bet_wan);
            }
        } else {
            result = String.valueOf(mTotalBetVal);
        }
        mTotalBet.setText(result);
    }

    /**
     * 显示自己的下注总金额
     */
    private void showSelfBet() {
        String result;
        if (mMyBetVal >= 10000) {
            if (mMyBetVal % 10000 == 0) {
                result = String.valueOf(mMyBetVal / 10000) + WordUtil.getString(R.string.game_bet_wan);
            } else {
                result = String.format("%.1f",mTotalBetVal / 10000f) + WordUtil.getString(R.string.game_bet_wan);
            }
        } else {
            result = String.valueOf(mMyBetVal);
        }
        mMyBet.setText(result);
    }

    public void reset() {
        mTotalBetVal = 0;
        mMyBetVal = 0;
        mTotalBet.setText("");
        mMyBet.setText("");
        mCover.setVisibility(INVISIBLE);
        mResult.setVisibility(INVISIBLE);
        mLeftImg.setImageResource(R.mipmap.icon_ebb_bk);
        mRightImg.setImageResource(R.mipmap.icon_ebb_bk);
    }

    public void showResult(String left, String right, String result) {
        mLeftImg.setImageResource(GameIconUitl.getEbbDianResult(left));
        mRightImg.setImageResource(GameIconUitl.getEbbDianResult(right));
        mResult.setImageResource(GameIconUitl.getEbbResult(result));
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    public void clearAnim() {
        mResultAnim.cancel();
        mResult.clearAnimation();
    }

    public void showCover() {
        mCover.setVisibility(VISIBLE);
    }

}
