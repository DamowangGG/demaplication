package com.zejian.myapplication.pagerRefresh

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zejian.myapplication.NotificationChannelUtils
import com.zejian.myapplication.NotificationService
import com.zejian.myapplication.R
import java.util.*

object NotifyManager {

    private var nm: NotificationManagerCompat? = null
    private var showRing = true
    private var showVibrate = true
    private var showNotify = true
    private var showDetail = true
    private var mContext: Context? = null
    private val msgs = HashMap<String, UnRead>()


    @SuppressLint("StaticFieldLeak")
    @Volatile
    private var sInstance: NotificationSimple? = null


    fun initUserConfig(context: Context) {
        showNotify = true
        showDetail = true
        showRing = true
        showVibrate = true
        mContext = context
        nm = NotificationManagerCompat.from(context)
    }


    fun send(peer: String?, name: String?, resource: Bitmap) {
        val lastMessage = generateMessageInfo(peer)
        if (lastMessage.getExtra() == null || peer == null || mContext == null) return
        var unRead = msgs[peer]
        if (unRead == null) {
            unRead =
                UnRead(ArrayList(), System.currentTimeMillis())
            unRead.userName = name
            msgs[peer] = unRead
        }
        unRead.addSuccess(lastMessage)
        //统计未读数量
        var totalUnread = 0
        synchronized(msgs) {
            for (peerID in msgs.keys) {
                val unReadX = msgs[peerID]
                if (unReadX != null) {
                    totalUnread += unReadX.count
                }
            }
        }
        var content = lastMessage.getExtra().toString()
        if (!showDetail) {
            content = "发来" + unRead.count + "条消息"
        } else {
            if (unRead.count > 1) {
                content = "[" + unRead.count + "条] " + content
            }
        }
        sendNotification(
            peer,
            unRead.userName,
            content,
            resource,
            totalUnread,
            lastMessage.msgTime
        )
        Log.d("sendSimpleNotification", peer)
    }


    fun cancel(peer: String) {
        if (TextUtils.isEmpty(peer)) {
            return
        }
        //cancel id
        try {
            val id = peer.toInt()
            nm!!.cancel(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val unRead = msgs.remove(peer)
        //统计未读数量
        var count = 0
        synchronized(msgs) {
            for (peerID in msgs.keys) {
                val unReadX = msgs[peerID]
                if (unReadX != null) {
                    count += unReadX.count
                }
            }
        }
        if (unRead != null) { //更新summary
            if (count > 0) {
                val summary: Notification?
                summary = if (isAndroid7Lower()) {
                    buildM2Kitkat(count, true)
                } else {
                    buildSummary(NotificationChannelUtils.GROUP_NEW_MESSAGE, count, true)
                }
                if (summary != null && mContext != null) { //创建点击通知时发送的广播
                    val intentM2Kitkat = Intent(
                        mContext,
                        NotificationService::class.java
                    )
                    intentM2Kitkat.action = NotificationService.ACTION_CONVERSATION_LIST
                    summary.contentIntent = PendingIntent.getService(
                        mContext,
                        0,
                        intentM2Kitkat,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    nm!!.notify(0, summary)
                }
            }
        }
        //如果没有未读取消所有
        if (msgs.isEmpty() || count == 0) {
            cancelAll()
        }
    }


    private fun cancelAll() {
        if (nm != null) {
            nm!!.cancelAll()
        }
        msgs.clear()
    }


    private fun sendNotification(
        peer: String, title: String?, content: String,
        bitmapLarge: Bitmap, totalUnread: Int, messageTime: Long
    ) {
        if (mContext == null) {
            return
        }
        // android N lower
        if (isAndroid7Lower()) {
            val ntf = buildM2Kitkat(totalUnread, false) ?: return
            // 创建点击通知时发送的广播
            val intentSummary = Intent(
                mContext,
                NotificationService::class.java
            )
            intentSummary.action = NotificationService.ACTION_CONVERSATION_LIST
            ntf.contentIntent = PendingIntent.getService(
                mContext,
                0,
                intentSummary,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            nm!!.notify(0, ntf)
        } else {
            val id: Int
            id = try {
                peer.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
            if (totalUnread == 1) { // children notification
                nm!!.cancel(0)
                val notification = buildNotification(
                    title,
                    content,
                    bitmapLarge,
                    NotificationChannelUtils.GROUP_NEW_MESSAGE,
                    messageTime
                )
                    ?: return
                // 创建点击通知时发送的广播
                val intent = Intent(
                    mContext,
                    NotificationService::class.java
                )
                intent.action = NotificationService.ACTION_CONVERSATION
                notification.contentIntent = PendingIntent.getService(
                    mContext,
                    id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                nm!!.notify(id, notification)
            } else { //summary
                val summary = buildSummary(
                    NotificationChannelUtils.GROUP_NEW_MESSAGE, totalUnread, false
                )
                    ?: return
                val intentSummary = Intent(
                    mContext,
                    NotificationService::class.java
                )
                intentSummary.action = NotificationService.ACTION_CONVERSATION_LIST
                summary.contentIntent = PendingIntent.getService(
                    mContext,
                    0,
                    intentSummary,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                //child
                val notification = buildNotification(
                    title,
                    content,
                    bitmapLarge,
                    NotificationChannelUtils.GROUP_NEW_MESSAGE,
                    messageTime
                )
                    ?: return
                val intent = Intent(
                    mContext,
                    NotificationService::class.java
                )
                intent.action = NotificationService.ACTION_CONVERSATION
                notification.contentIntent = PendingIntent.getService(
                    mContext,
                    id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                nm!!.notify(0, summary)
                nm!!.notify(id, notification)
            }
        }
    }


    private fun buildNotification(
        title: String?,
        content: String,
        bitmapLarge: Bitmap?,
        groupKey: String?,
        messageTime: Long
    ): Notification? {
        if (mContext == null) {
            return null
        }
        val mBuilder =
            NotificationCompat.Builder(
                mContext!!,
                NotificationChannelUtils.CHANNEL_ID_NEW_MESSAGE
            )
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(messageTime)
                .setWhen(messageTime)
                .setShowWhen(true)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(groupKey)
        mBuilder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
        if (bitmapLarge != null) { //设置通知右侧的大图标
            mBuilder.setLargeIcon(bitmapLarge)
        }
        //静默
        if (groupKey != null) {
            mBuilder.setSound(null)
            mBuilder.setVibrate(null)
        } else {
            if (!showRing && !showVibrate) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                mBuilder.setSound(null)
                mBuilder.setVibrate(null)
            } else if (!showRing) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_VIBRATE)
                mBuilder.setSound(null)
            } else if (!showVibrate) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_SOUND)
                mBuilder.setVibrate(null)
            } else {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }
        return mBuilder.build()
    }


    private fun buildSummary(
        groupKey: String,
        totalUnread: Int,
        update: Boolean
    ): Notification? {
        if (mContext == null) {
            return null
        }
        val mBuilder =
            NotificationCompat.Builder(
                mContext!!,
                NotificationChannelUtils.CHANNEL_ID_NEW_MESSAGE
            )
        mBuilder.setContentTitle("App")
            .setContentText((if (msgs.size > 0) msgs.size else 1).toString() + "个联系人发来" + totalUnread + "条消息")
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setShowWhen(true)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .setAutoCancel(true)
        if (update) {
            mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            mBuilder.setSound(null)
            mBuilder.setVibrate(null)
        } else {
            if (!showRing && !showVibrate) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                mBuilder.setSound(null)
                mBuilder.setVibrate(null)
            } else if (!showRing) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_VIBRATE)
                mBuilder.setSound(null)
            } else if (!showVibrate) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_SOUND)
                mBuilder.setVibrate(null)
            } else {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }
        val bitmap =
            BitmapFactory.decodeResource(mContext!!.resources, R.mipmap.ic_launcher)
        if (bitmap != null) {
            mBuilder.setLargeIcon(bitmap)
        }
        return mBuilder.build()
    }


    private fun buildM2Kitkat(
        totalUnread: Int,
        update: Boolean
    ): Notification? {
        if (mContext == null) {
            return null
        }
        val mBuilder =
            NotificationCompat.Builder(
                mContext!!,
                NotificationChannelUtils.CHANNEL_ID_NEW_MESSAGE
            )
        mBuilder.setContentTitle("彼聊")
            .setContentText((if (msgs.size > 0) msgs.size else 1).toString() + "个联系人发来" + totalUnread + "条消息")
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setShowWhen(true)
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 300))
            .setAutoCancel(true)
        if (update) {
            mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            mBuilder.setSound(null)
            mBuilder.setVibrate(null)
        } else {
            if (!showRing && !showVibrate) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                mBuilder.setSound(null)
                mBuilder.setVibrate(null)
            } else if (!showRing) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                mBuilder.setVibrate(longArrayOf(0, 300))
                mBuilder.setSound(null)
            } else {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                mBuilder.setVibrate(null)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //VISIBILITY_PUBLIC: 任何情况的显示
//VISIBILITY_PRIVATE: 只有在没有锁屏时显示
//VISIBILITY_SECRET: 在安全锁下或者没锁屏下显示
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }
        val bitmap =
            BitmapFactory.decodeResource(mContext!!.resources, R.mipmap.ic_launcher)
        if (bitmap != null) {
            mBuilder.setLargeIcon(bitmap)
        }
        return mBuilder.build()
    }


    fun clear() {
        msgs.clear()
        cancelAll()
    }


    fun setShowRing(showRing: Boolean) {
        this.showRing = showRing
    }

    fun setShowVibrate(showVibrate: Boolean) {
        this.showVibrate = showVibrate
    }

    fun setShowNotify(showNotify: Boolean) {
        this.showNotify = showNotify
    }

    fun setShowDetail(showDetail: Boolean) {
        this.showDetail = showDetail
    }


    private fun isAndroid8Upper(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    private fun isAndroid7Lower(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N
    }


    internal class UnRead(
        var unReadList: ArrayList<MessageInfo?>?,
        var time: Long
    ) {
        var lastId: String? = null
        var iconUrl: String? = null
        var userName: String? = null

        val count: Int
            get() = if (unReadList != null) {
                unReadList!!.size
            } else 0

        fun addSuccess(messageInfo: MessageInfo?): Boolean {
            if (unReadList == null) {
                unReadList = ArrayList()
            }
            unReadList!!.add(messageInfo)
            return true
        }

    }

    class MessageInfo(private var extra: String, var msgTime: Long) {

        fun getExtra(): String? {
            return extra
        }

        fun setExtra(extra: String) {
            this.extra = extra
        }

    }


    private const val str =
        "据中国外交部,发言人赵立坚披露，加勒万河谷位,于中印边界,西段实际控制线中方一侧。多年来，中国边防部队一直在此,正常巡逻执勤。2020年4月以来，印度边防部队,单方面在加勒,万河谷地区持,续抵边修建道路、桥梁等设施。中方多次就此提出,交涉和抗议，但印方反而变,本加厉越线滋事。2020年5月6日凌晨，印度边防部队乘,夜色在加勒万河谷地区越,线进入中国领土、构工设障，阻拦中方边,防部队正常巡逻，蓄意挑起事端，试图单方面改变边境管控现状。中方边防部队不得不,采取必要措施，加强现场应对和边境地区管控。为缓和边境地区局势，中印双方通过军事和外,交渠道保持密切沟通。在中方强烈要求下，印方同意并撤出越线人员，拆除越线设施。6月6日，两国边防部队举行军长级会晤，就缓和边境地区局势达成共识。印方承诺不越过加勒,万河口巡逻和修建设施，双方通过现地指挥官会,晤商定分批撤军事"
    private val strList = str.split(",").toTypedArray()
    private var index = 0
    private fun generateMessageInfo(peer: String?): MessageInfo {
        if (index >= strList.size) {
            index = 0
        }
        val messageInfo =
            MessageInfo(strList[index], System.currentTimeMillis())
        index++
        return messageInfo
    }


    fun areNotificationEnabled(context: Context?): Boolean {
        val nm = NotificationManagerCompat.from(context!!)
        var importanceOk = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = nm.getNotificationChannel(
                NotificationChannelUtils.CHANNEL_ID_NEW_MESSAGE
            )
            if (mChannel != null && mChannel.importance == NotificationManagerCompat.IMPORTANCE_NONE) {
                importanceOk = false
            }
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled() && importanceOk
    }


    /**
     * 跳转到权限设置界面
     */
    fun open(context: Context) { // vivo 点击设置图标>加速白名单>我的app
//      点击软件管理>软件管理权限>软件>我的app>信任该软件
        var appIntent =
            context.packageManager.getLaunchIntentForPackage("com.iqoo.secure")
        if (appIntent != null) {
            context.startActivity(appIntent)
            return
        }
        // oppo 点击设置图标>应用权限管理>按应用程序管理>我的app>我信任该应用
//      点击权限隐私>自启动管理>我的app
        appIntent = context.packageManager.getLaunchIntentForPackage("com.oppo.safe")
        if (appIntent != null) {
            context.startActivity(appIntent)
            return
        }
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            val mn = NotificationManagerCompat.from(context)
            val mChannel = mn.getNotificationChannel(
                NotificationChannelUtils.CHANNEL_ID_NEW_MESSAGE
            )
            if (mChannel != null) {
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, mChannel.id)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", context.packageName, null)
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.action = Intent.ACTION_VIEW
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
            intent.putExtra("com.android.settings.ApplicationPkgName", context.packageName)
        }
        context.startActivity(intent)
    }

}