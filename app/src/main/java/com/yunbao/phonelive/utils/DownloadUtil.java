package com.yunbao.phonelive.utils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.interfaces.DownloadCallback;

import java.io.File;

/**
 * Created by cxf on 2017/9/4.
 */

public class DownloadUtil {

    /**
     * 下载文件的方法
     *
     * @param fileDir  保存文件的文件夹路径
     * @param fileName 文件名
     * @param url      url
     * @param callback 回调
     */
    public static void download(String fileDir, String fileName, String url, final DownloadCallback callback) {
        OkGo.<File>get(url).execute(new FileCallback(fileDir, fileName) {
            @Override
            public void onSuccess(Response<File> response) {
                //下载成功结束后的回调
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void downloadProgress(Progress progress) {
                if (callback != null) {
                    int val = (int) (progress.currentSize * 100 / progress.totalSize);
                    L.e("下载进度--->" + val);
                    callback.onProgress(val);
                }
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                L.e("下载失败--->" + response.getException());
                if (callback != null) {
                    callback.onError();
                }
            }
        });
    }
}
