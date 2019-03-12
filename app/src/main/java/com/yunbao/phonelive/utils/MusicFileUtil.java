package com.yunbao.phonelive.utils;

import android.content.Context;
import android.text.TextUtils;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.db.MusicDbManager;
import com.yunbao.phonelive.interfaces.DownloadCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by cxf on 2017/9/10.
 * mp3 歌词 下载删除等
 */

public class MusicFileUtil {

    private static final String FILE_DIR = AppConfig.getInstance().MUSIC_PATH;

    public static void save(String fileName, String mp3Url, String lrcContent, DownloadCallback callback) {
        if ("".equals(mp3Url)) {
            ToastUtil.show(WordUtil.getString(R.string.mp3_not_found));
            return;
        } else {
            DownloadUtil.download(FILE_DIR, fileName + ".mp3", mp3Url, callback);
        }
        if ("".equals(lrcContent)) {
            ToastUtil.show(WordUtil.getString(R.string.lrc_not_found));
        } else {
            if (!TextUtils.isEmpty(lrcContent)) {
                L.e("发现歌词---->" + lrcContent);
                saveLrc(lrcContent, fileName + ".lrc");
            }
        }
    }


    public static void delete(String id) {
        MusicDbManager.getInstace().delete(id);
        File mp3File = new File(FILE_DIR, id + ".mp3");
        if (mp3File.exists()) {
            mp3File.delete();
        }
        File lrcFile = new File(FILE_DIR, id + ".lrc");
        if (lrcFile.exists()) {
            lrcFile.delete();
        }
    }

    /**
     * 把歌词保存到本地
     */
    private static void saveLrc(String lrcContent, String fileName) {
        File folder = new File(FILE_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        PrintWriter writer = null;
        try {
            FileOutputStream os = new FileOutputStream(new File(folder, fileName));
            writer = new PrintWriter(os);
            writer.write(lrcContent);
            writer.flush();
            L.e("-----saveLrc--->歌词保存成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}
