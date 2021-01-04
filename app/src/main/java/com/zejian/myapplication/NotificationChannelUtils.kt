package com.zejian.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat


object NotificationChannelUtils {
    const val GROUP_NEW_MESSAGE = "group_message"
    // 新消息
    const val CHANNEL_ID_NEW_MESSAGE = "channel_id_new_message"
    //下载
    const val CHANNEL_UPDATE = "update"

    private const val CHANNEL_NAME_NEW_MESSAGE = "新消息提醒"
    private const val CHANNEL_DESC_NEW_MESSAGE = "当收到新消息发出提醒"

    /**
     * 创建通知渠道
     */
    fun initChannel(context: Context) {
        createNotificationChannel(context, CHANNEL_ID_NEW_MESSAGE, CHANNEL_NAME_NEW_MESSAGE, CHANNEL_DESC_NEW_MESSAGE)
        createNotificationChannel(context, CHANNEL_UPDATE, "更新", "下载新版本")
    }

    /**
     * 是否Android8.0及以后
     */
    fun isAndroid8Upper(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    /**
     * 创建通知渠道
     * @param context 上下文
     * @param channelId 渠道Id
     * @param channelName 渠道名称
     * @param channelDesc 渠道描述
     */
    private fun createNotificationChannel(context: Context, channelId: String, channelName: String, channelDesc: String) {
        val mn = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var mChannel: NotificationChannel? = mn.getNotificationChannel(channelId)
            if (mChannel == null) {
                mChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                mChannel.description = channelDesc
                mChannel.setShowBadge(false)
                mChannel.lockscreenVisibility = VISIBILITY_PUBLIC
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                if(channelId == CHANNEL_UPDATE) {
                    mChannel.setSound(null, null)
                    mChannel.enableLights(false)
                    mChannel.enableVibration(false)
                    mChannel.vibrationPattern = null
                }else{
                    mChannel.setSound(uri, Notification.AUDIO_ATTRIBUTES_DEFAULT)
                    mChannel.enableLights(true)
                    mChannel.enableVibration(true)
                    mChannel.vibrationPattern = longArrayOf(100, 500)
                }
                mn.createNotificationChannel(mChannel)
            }
        }
    }

    /**
     * 检查通知渠道是否有正确的重要级别
     *
     * @param context 上下文
     * @param channelId 渠道Id
     */
    fun checkNotificationNormal(context: Context, channelId: String) {
        val mn = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = mn.getNotificationChannel(channelId)
            if (mChannel.importance == NotificationManager.IMPORTANCE_NONE) {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, mChannel.id)
                context.startActivity(intent)
            }
        }
    }

    /**
     * 新消息通知渠道设置
     *
     * @param context Activity上下文
     * @param channelId 渠道Id
     */
    fun setNotificationChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelSetting = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            channelSetting.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            channelSetting.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
            val componentName = channelSetting.resolveActivity(context.packageManager)

            val appDetail = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            appDetail.data = Uri.fromParts("package", context.packageName, null)
            val appDetailCN = appDetail.resolveActivity(context.packageManager)
            if (componentName != null) {
                context.startActivity(channelSetting)
            } else if (appDetailCN != null) {
                context.startActivity(appDetail)
            } else {
            }
        }
    }
}