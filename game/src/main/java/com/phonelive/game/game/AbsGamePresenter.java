package com.phonelive.game.game;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;

/**
 * Created by cxf on 2017/10/15.
 */

public abstract class AbsGamePresenter {

    protected Context mContext;
    protected FragmentManager mFragmentManager;
    protected String mStream;
    protected String mLiveuid;
    protected boolean mIsAnchor;
    protected String mGameId;
    protected String mGameToken;
    protected int mBetTime;//下注持续时间
    protected boolean mGameClosed;//是否关闭了
    private GameSoundPool mGameSoundPool;
    protected boolean mSocketConn = true;//socket是否连接了
    protected boolean mCanCloseGame;


    public AbsGamePresenter(Context context, FragmentManager fragmentManager, String stream, String liveuid) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mStream = stream;
        mLiveuid = liveuid;
        mIsAnchor = liveuid.equals(AppConfig.getInstance().getUid());
        mGameSoundPool = new GameSoundPool();
    }

    public boolean isAnchor() {
        return mIsAnchor;
    }

    public String getStream() {
        return mStream;
    }

    public String getLiveuid() {
        return mLiveuid;
    }

    public abstract void onGameMessage(JSONObject obj);

    public abstract void closeGame();

    public abstract boolean anchorCloseGame();

    public void releaseGamePool() {
        mGameSoundPool.release();
    }

    public void playMusic(int key) {
        if (!mIsAnchor) {
            mGameSoundPool.play(key);
        }
    }

    public void enterRoomStartGame(String gameId, int betTime, int[] gameTotalBet, int[] gameMyBet) {
        mGameId = gameId;
        mBetTime = betTime;
        onEnterRoomStartGame(gameTotalBet, gameMyBet);
    }

    protected abstract void onEnterRoomStartGame(int[] gameTotalBet, int[] gameMyBet);

    protected abstract void gameReplaced();

    protected abstract boolean anchorCanReplaceGame();

    public void setSocketConn(boolean socketConn) {
        mSocketConn = socketConn;
        if (!socketConn) {
            mCanCloseGame = true;
        }
    }


}
