package com.phonelive.game.game;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import com.yunbao.phonelive.AppContext;

/**
 * Created by cxf on 2017/10/16.
 */

public class GameSoundPool {
    private SoundPool mSoundPool;
    private SparseIntArray mSparseIntArray;

    public GameSoundPool() {
        mSparseIntArray = new SparseIntArray();
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSparseIntArray.put(1, mSoundPool.load(AppContext.sInstance, com.phonelive.game.R.raw.game_bet_begin, 1));
        mSparseIntArray.put(2, mSoundPool.load(AppContext.sInstance, com.phonelive.game.R.raw.game_bet_success, 1));
        mSparseIntArray.put(3, mSoundPool.load(AppContext.sInstance, com.phonelive.game.R.raw.game_show_result, 1));
        mSparseIntArray.put(4, mSoundPool.load(AppContext.sInstance, com.phonelive.game.R.raw.game_bet_check, 1));
    }

    public void play(int key) {
        mSoundPool.play(mSparseIntArray.get(key), 1f, 1f, 0, 0, 1);
    }

    public void release() {
        mSoundPool.release();
    }

}
