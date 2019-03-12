package com.yunbao.phonelive.ui.tools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.ToastUtil;

import java.io.File;

public class DownloadUtil {
    private Context mContext;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private boolean flag = false; //进度框消失标示 之后发送通知
    private static final int PUSH_NOTIFICATION_ID = (0x001);
    private static final String PUSH_CHANNEL_ID = "TAN_QIU";
    private static final String PUSH_CHANNEL_NAME = "PUSH_TAN_QIU";

    public DownloadUtil(Context context) {
        this.mContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        builder = new NotificationCompat.Builder(mContext, PUSH_CHANNEL_ID);
        builder.setContentTitle("下载更新...") //设置通知标题
                .setDefaults(NotificationCompat.FLAG_ONGOING_EVENT)
                .setSmallIcon(R.drawable.jmessage_notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher)) //设置通知的大图标
//                .setDefaults(Notification.DEFAULT_LIGHTS) //设置通知的提醒方式： 呼吸灯
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                .setAutoCancel(false)//设置通知被点击一次是否自动取消
                .setContentText("下载进度:" + "0%")
//                .setVisibility(Notification.VISIBILITY_PRIVATE)
                .setProgress(100, 0, false);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x11) {
                int arg1 = msg.arg1;
                Log.e(TAG, (Looper.myLooper() == Looper.getMainLooper()) + "handleMessage: " + arg1);
                builder.setProgress(100, arg1, false);
                builder.setContentText("下载进度:" + arg1 + "%");
//                        notification = builder.build();
                notificationManager.notify(PUSH_NOTIFICATION_ID, builder.build());
            }
        }
    };

    public void download(String fileUrl) {
        OkGo.<File>get(fileUrl)
                .tag(fileUrl)
                .execute(new FileCallback() {
                    @Override
                    public void onSuccess(Response<File> response) {
                        ToastUtil.show("下载完成!");
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);
                        if (progress.fraction == 1f) {  //下载完成后点击安装
                            Intent it = new Intent(Intent.ACTION_VIEW);
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            File apkFile = new File(progress.filePath);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                it.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Uri contentUri = FileProvider.getUriForFile(
                                        mContext
                                        , "cm.aiyouxi.live.fileprovider"
                                        , apkFile);
                                it.setDataAndType(contentUri, "application/vnd.android.package-archive");
                            } else {
                                it.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                            }
                            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentTitle("下载完成")
                                    .setProgress(100, 100, false)
                                    .setContentText("点击安装")
                                    .setContentInfo("下载完成")
                                    .setContentIntent(pendingIntent);
                            notificationManager.notify(PUSH_NOTIFICATION_ID, builder.build());
                        } else {
                            builder.setProgress(100, (int) (progress.fraction * 100), false);
                            builder.setContentText("下载进度:" + (int) (progress.fraction * 100) + "%");
                            notificationManager.notify(PUSH_NOTIFICATION_ID, builder.build());
                        }
                    }

                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                    }

                });
    }

    public static final String TAG = "///";
}
