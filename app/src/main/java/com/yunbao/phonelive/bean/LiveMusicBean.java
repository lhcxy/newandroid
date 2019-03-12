package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by cxf on 2017/9/2.
 */

public class LiveMusicBean  implements Serializable {
    public static final int PRO_NOT = 0;//未开始,PRO是progress的缩写,PRO_NOT表示进度未开始，即下载之前
    public static final int PRO_ING = 1;//进行中，下载中
    public static final int PRO_END = 2;//结束了，下载结束
    private String audio_id;
    private String audio_name;
    private String artist_name;
    private int status = PRO_NOT;//是否开始下载的状态
    private int progress;//下载进度


    public String getAudio_id() {
        return audio_id;
    }

    public void setAudio_id(String audio_id) {
        this.audio_id = audio_id;
    }

    public String getAudio_name() {
        return audio_name;
    }

    public void setAudio_name(String audio_name) {
        this.audio_name = audio_name;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
