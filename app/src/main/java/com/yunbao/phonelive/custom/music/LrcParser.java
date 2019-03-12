package com.yunbao.phonelive.custom.music;


import android.os.Environment;

import com.yunbao.phonelive.bean.LiveLrcBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by cxf on 2017/9/10.
 * 歌词解析器
 */

public class LrcParser {

    private static LrcParser sInstance;
    //歌词文件的存放路径
//    private static String sFileDir = AppConfig.getInstance().MUSIC_PATH;
    private static String sFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/phoneLive/music/";
    //解析歌词用的正则表达式
    private static Pattern sPattern = Pattern.compile("\\[(\\d{2}:\\d{2}\\.\\d{2})\\]");

    private LrcParser() {

    }

    public static LrcParser getInstance() {
        if (sInstance == null) {
            synchronized (LrcParser.class) {
                if (sInstance == null) {
                    sInstance = new LrcParser();
                }
            }
        }
        return sInstance;
    }

    /**
     * @param fileName
     * @return
     */
    public LiveLrcBean parse(String fileName) {
        LiveLrcBean bean = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sFileDir, fileName)), "utf-8"));
            bean = new LiveLrcBean();
            // 一行一行的读，每读一行，解析一行
            String line = null;
            while ((line = br.readLine()) != null) {
                parserLine(bean, line);
            }
            bean.parseDuartion();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            ToastUtil.show(WordUtil.getString(R.string.lrc_not_found));
//            Toast.makeText()
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 利用正则表达式解析每行具体语句
     * 并在解析完该语句后，将解析出来的信息设置在LrcInfo对象中
     *
     * @param str
     */
    private void parserLine(LiveLrcBean bean, String str) {
        // 取得歌曲名信息
        if (str.startsWith("[ti:")) {
            String name = str.substring(4, str.length() - 1);
            bean.setName(name);
          // L.e("歌词", "name--->" + name);

        }// 取得歌手信息
        else if (str.startsWith("[ar:")) {
            String artist = str.substring(4, str.length() - 1);
            bean.setArtist(artist);
           // L.e("歌词", "artist--->" + artist);

        }// 取得专辑信息
        else if (str.startsWith("[al:")) {
            String album = str.substring(4, str.length() - 1);
            bean.setAlbum(album);
            //L.e("歌词", "album--->" + album);

        }
        // 通过正则取得每句歌词信息
        else {
            Matcher matcher = sPattern.matcher(str);
            // 如果存在匹配项，则执行以下操作
            while (matcher.find()) {
                int time = strToLong(matcher.group(1));
               // L.e("歌词", "时长--->" + time);
                // 得到时间点后的歌词内容
                String content = "";
                String[] arr = sPattern.split(str);
                if (arr.length > 1) {
                    content = arr[1];
                }
               // L.e("歌词", "歌词--->" + content);
                bean.putLrc(time,content);
            }
        }
    }

    /**
     * 将解析得到的表示时间的字符转化为int型
     *
     * @return int形式的时间毫秒数
     */
    private int strToLong(String timeStr) {
        // 因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单位
        // 1:使用：分割 2：使用.分割
        String[] s = timeStr.split(":");
        int min = Integer.parseInt(s[0]);
        String[] ss = s[1].split("\\.");
        int sec = Integer.parseInt(ss[0]);
        int mill = Integer.parseInt(ss[1]);
        return min * 60 * 1000 + sec * 1000 + mill * 10;
    }

}
