package com.yunbao.phonelive.bean;

import android.util.SparseArray;

import com.yunbao.phonelive.utils.L;

import java.io.Serializable;

/**
 * Created by cxf on 2017/9/10.
 * 解析歌词文件的实体类
 */

public class LiveLrcBean  implements Serializable {
    private String name;//歌曲名
    private String artist;//演唱者
    private String album;//专辑
    //保存歌词信息和时间点一一对应的Map
    private SparseArray<LrcItem> mMap;
    private int mCursor;

    public LiveLrcBean() {
        mMap = new SparseArray<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }


    public void putLrc(int startTime, String lrc) {
        LrcItem item = new LrcItem(startTime, lrc);
        if(mMap.size()==0){
            item.setFirstLrc(true);
        }
        mMap.put(startTime, item);
    }

    public boolean hasLrc() {
        return mMap.size() > 0;
    }

    /**
     * 获取每句歌词的时长
     */
    public void parseDuartion() {
        for (int i = 1; i < mMap.size(); i++) {
            LrcItem item = mMap.valueAt(i - 1);
            LrcItem nextItem = mMap.valueAt(i);
            item.setDuration(nextItem.getStartTime() - item.getStartTime());
            L.e("duration--->" + item.getDuration() + ":" + item.getLrc());
        }
    }

    public LrcItem getNextLrcItem() {
        mCursor++;
        if (mCursor >= mMap.size()) {
            mCursor = 0;
        }
        return mMap.valueAt(mCursor);
    }

    public LrcItem getFristLrcItem() {
        return mMap.valueAt(0);
    }

    public static class LrcItem {
        private int startTime;
        private int duration;
        private String lrc;
        private boolean isFirstLrc;

        public LrcItem() {

        }

        public LrcItem(int startTime, String lrc) {
            this.startTime = startTime;
            this.lrc = lrc;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getLrc() {
            return lrc;
        }

        public void setLrc(String lrc) {
            this.lrc = lrc;
        }

        public boolean isFirstLrc() {
            return isFirstLrc;
        }

        public void setFirstLrc(boolean firstLrc) {
            isFirstLrc = firstLrc;
        }
    }
}
