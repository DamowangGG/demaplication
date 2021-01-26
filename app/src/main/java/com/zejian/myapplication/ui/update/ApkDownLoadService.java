package com.zejian.myapplication.ui.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.yinmai.yidui.R;
import me.yinmai.yidui.core.AppManager;
import me.yinmai.yidui.util.FileUtil;
import me.yinmai.yidui.util.NotificationChannelUtils;
import me.yinmai.yidui.util.NotificationUtil;
import me.yinmai.yidui.util.Tips;

/**
 * Created by cxr
 * 2018/8/27 0027.
 */

public class ApkDownLoadService extends Service {

    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    Notification notification;
    DownLoadTask downLoadTask;

    File downLoadFile;
    boolean showNotify = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downLoadFile = UpdateManager.getInstallApkPath(this);
        if(downLoadFile == null){
            Tips.toast("下载失败请稍后重试");
        }else {
            if(!downLoadFile.exists()) {
                //没下载的情况
                FileUtil.deleteAllInDir(TUIKitConstants.APK_DIR);
                try {
                    downLoadFile = PackageUtil.getTempApkPath(this);
                    boolean res = downLoadFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                downLoadTask = new DownLoadTask(this);
                downLoadTask.execute(PackageUtil.getUpdateAddress());
            }else {
                //下载完成
                installApp(downLoadFile);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    public void installApp(File file){
        //下载完成
        boolean res = PackageUtil.installApp(this,file);
        if(res){
            if(showNotify && mNotificationManager != null) {
                mNotificationManager.cancel(NotificationUtil.NOTIFICATION_ID_UPDATE);
            }
            stopSelf();
        }else if(showNotify){
            Intent installAppIntent = PackageUtil.getInstallAppIntent(ApkDownLoadService.this, file);
            PendingIntent contentIntent = PendingIntent.getActivity(ApkDownLoadService.this, 0, installAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent)
                    .setContentTitle("音麦")
                    .setContentText("下载完成，点击安装更新")
                    .setProgress(0, 0, false)
                    .setAutoCancel(true)
                    .setDefaults((Notification.DEFAULT_ALL));
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(NotificationUtil.NOTIFICATION_ID_UPDATE, notification);
        }
    }


    public class DownLoadTask extends AsyncTask<String,Integer,Integer>{
        private Context context;
        RemoteViews remoteViews;
        public DownLoadTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(showNotify) {
                mNotificationManager = (NotificationManager) AppManager.INSTANCE.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(context, NotificationChannelUtils.CHANNEL_UPDATE);
                mBuilder.setContentTitle("开始下载")
                        .setContentText("正在连接服务器")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setOngoing(true)
                        .setAutoCancel(false)
                        .setWhen(System.currentTimeMillis());
                mNotificationManager.notify(NotificationUtil.NOTIFICATION_ID_UPDATE, mBuilder.build());
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            InputStream is = null;
            OutputStream os = null;
            HttpURLConnection connection = null;
            int total_length = 0;
            try {
                URL url1 = new URL(params[0]);
                connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(50000);
                connection.connect();
                if(connection.getResponseCode() == 200){
                    is = connection.getInputStream();
                    os = new FileOutputStream(downLoadFile);
                    byte [] buf = new byte[1024];
                    int len;
                    int pro1=0;
                    int pro2=0;
                    // 获取文件流大小，用于更新进度
                    long file_length = connection.getContentLength();
                    while((len = is.read(buf))!=-1){
                        total_length += len;
                        if(file_length>0) {
                            pro1 = (int) ((total_length / (float) file_length) * 100);//传递进度（注意顺序）
                        }
                        if(pro1!=pro2) {
                            // 调用update函数，更新进度
                            publishProgress(pro2=pro1);
                            EventBus.getDefault().post(new DownLoadProgress(DownLoadProgress.STATE_NORMAL,pro1));
                        }
                        os.write(buf, 0, len);
                    }
                }
            } catch (Exception e) {
                 e.printStackTrace();
                EventBus.getDefault().post(new DownLoadProgress(DownLoadProgress.STATE_DOWNLOAD_ERROR,0));
                stopSelf();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return total_length;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (showNotify && mBuilder != null) {
                mBuilder.setContentTitle("正在下载：").setContentText(values[0] + "%")
                        .setProgress(100, values[0], false)
                        .setWhen(System.currentTimeMillis());
                Notification notification = mBuilder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
                mNotificationManager.notify(NotificationUtil.NOTIFICATION_ID_UPDATE, notification);
            }
            //下载进度提示
            if(values[0]==100) {    //下载完成后点击安装
                File realFile = PackageUtil.getInstallApkPath(context);
                downLoadFile.renameTo(realFile);
                downLoadFile.setReadable(true);
                EventBus.getDefault().post(new DownLoadProgress(DownLoadProgress.STATE_FINISH,0));
                installApp(realFile);
            }
        }
    }



}
