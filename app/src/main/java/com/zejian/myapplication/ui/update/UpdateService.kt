package com.zejian.myapplication.ui.update

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.zejian.myapplication.NotificationUtil
import org.greenrobot.eventbus.EventBus
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class UpdateService: Service() {

    var mNotificationManager: NotificationManager? = null
    var mBuilder: NotificationCompat.Builder? = null
    var downLoadTask: DownLoadTask? = null
    val apkDir = UpdateManager.apkDir
    var downLoadFile: File? = null
    var showNotify = true


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        downLoadFile = UpdateManager.getInstallApkPath(this)
        downLoadFile?.let {
            if(it.exists()){
                FileUtils.deleteAllInDir(apkDir)
                try {
                    downLoadFile = UpdateManager.getTempApkPath(this)
                    val res = downLoadFile?.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                downLoadTask = DownLoadTask(this)
                downLoadTask?.execute(UpdateManager.getUpdateAddress())
            }else {
                installApp(it)
            }
        }?:run {
            ToastUtils.showShort("更新文件下载失败")
        }

        return super.onStartCommand(intent, flags, startId)
    }


    fun installApp(file: File) {
        //下载完成
        val res = UpdateManager.installApp(this, file)
        if (res) {
            if (showNotify && mNotificationManager != null) {
                mNotificationManager!!.cancel(NotificationUtil.NOTIFICATION_ID_UPDATE)
            }
            stopSelf()
        } else if (showNotify) {
            val installAppIntent = UpdateManager.getInstallAppIntent(this, file)
            val contentIntent = PendingIntent.getActivity(this, 0,
                installAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            mBuilder!!.setContentIntent(contentIntent)
                .setContentTitle("音麦")
                .setContentText("下载完成，点击安装更新")
                .setProgress(0, 0, false)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
            val notification = mBuilder!!.build()
            notification.flags = Notification.FLAG_AUTO_CANCEL
            mNotificationManager!!.notify(NotificationUtil.NOTIFICATION_ID_UPDATE, notification)
        }
    }


    @SuppressLint("StaticFieldLeak")
    inner class DownLoadTask(private val context: Context) : AsyncTask<String?, Int?, Int?>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        protected override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            //下载进度提示
            if (values[0] == 100) {
                val realFile = UpdateManager.getInstallApkPath(context)
                realFile?.let {
                    downLoadFile?.renameTo(realFile)
                    downLoadFile?.setReadable(true)
                    EventBus.getDefault().post(DownLoadProgress(DownLoadProgress.STATE_FINISH, 0))
                    installApp(realFile)
                }
            }
        }


        override fun doInBackground(vararg params: String?): Int? {
            var inputStream: InputStream? = null
            var os: OutputStream? = null
            var connection: HttpURLConnection? = null
            var totalLength = 0
            try {
                val url1 = URL(params[0])
                connection = url1.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.readTimeout = 50000
                connection.connect()
                if (connection.responseCode == 200) {
                    inputStream = connection.inputStream
                    os = FileOutputStream(downLoadFile)
                    val buf = ByteArray(1024)
                    var len: Int
                    var pro1 = 0
                    var pro2 = 0
                    // 获取文件流大小，用于更新进度
                    val fileLength = connection.contentLength.toLong()
                    while (inputStream.read(buf).also { len = it } != -1) {
                        totalLength += len
                        if (fileLength > 0) {
                            pro1 = (totalLength / fileLength.toFloat() * 100).toInt() //传递进度（注意顺序）
                        }
                        if (pro1 != pro2) {
                            publishProgress(pro1.also { pro2 = it })
                            EventBus.getDefault().post(DownLoadProgress(DownLoadProgress.STATE_NORMAL, pro1))
                        }
                        os.write(buf, 0, len)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                EventBus.getDefault().post(DownLoadProgress(DownLoadProgress.STATE_DOWNLOAD_ERROR, 0))
                stopSelf()
            } finally {
                try {
                    inputStream?.close()
                    os?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                connection?.disconnect()
            }
            return totalLength
        }

    }
}