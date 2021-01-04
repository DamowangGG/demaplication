package com.zejian.myapplication.pagerRefresh;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zejian.myapplication.NotificationChannelUtils;
import com.zejian.myapplication.NotificationService;
import com.zejian.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

import static android.media.RingtoneManager.TYPE_NOTIFICATION;


public class NotificationSimple {


    private NotificationManagerCompat nm;
    private boolean showRing = true;
    private boolean showVibrate = true;
    private boolean showNotify = true;
    private boolean showDetail = true;
    private Context mContext;
    private final HashMap<String,UnRead> msgs = new HashMap<>();


    @SuppressLint("StaticFieldLeak")
    private static volatile NotificationSimple sInstance = null;


    private NotificationSimple() { }


    public void initUserConfig(Context context){
        showNotify = true;
        showDetail = true;
        showRing = true;
        showVibrate = true;
        mContext = context;
        nm = NotificationManagerCompat.from(context);
    }


    public static NotificationSimple getInstance() {
        if(sInstance == null) {
            synchronized (NotificationSimple.class) {
                if(sInstance == null) {
                    sInstance = new NotificationSimple();
                }
            }
        }
        return sInstance;
    }


    public void send(String peer, String name, Bitmap resource) {
        MessageInfo lastMessage = generateMessageInfo(peer);
        if(lastMessage.getExtra() == null || peer == null || mContext == null)
            return;
        UnRead unRead = msgs.get(peer);
        if(unRead == null){
            unRead = new UnRead(new ArrayList<MessageInfo>(),System.currentTimeMillis());
            unRead.setUserName(name);
            msgs.put(peer,unRead);
        }
        unRead.addSuccess(lastMessage);
        //统计未读数量
        int totalUnread = 0;
        synchronized (msgs){
            for (String peerID : msgs.keySet()) {
                UnRead unReadX = msgs.get(peerID);
                if (unReadX != null) {
                    totalUnread += unReadX.getCount();
                }
            }
        }
        String content =  lastMessage.getExtra().toString();
        if(!showDetail){
            content = "发来" + unRead.getCount() + "条消息";
        } else {
            if (unRead.getCount() > 1) {
                content = ("[" + unRead.getCount() + "条] ") + content;
            }
        }
        sendNotification(peer, unRead.getUserName(), content, resource, totalUnread, lastMessage.getMsgTime());
        Log.d("sendSimpleNotification",peer);
    }


    public void cancel(String peer){
        if(TextUtils.isEmpty(peer)){
            return;
        }
        //cancel id
        try {
            int id = Integer.parseInt(peer);
            nm.cancel(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        UnRead unRead = msgs.remove(peer);
        //统计未读数量
        int count = 0;
        synchronized (msgs){
            for (String peerID : msgs.keySet()) {
                UnRead unReadX = msgs.get(peerID);
                if (unReadX != null) {
                    count += unReadX.getCount();
                }
            }
        }
        if(unRead != null){
            //更新summary
            if(count > 0 ){
                Notification summary;
                if(isAndroid7Lower()) {
                    summary = buildM2Kitkat(count, true);
                }else {
                    summary = buildSummary(NotificationChannelUtils.GROUP_NEW_MESSAGE, count, true);
                }
                if(summary != null && mContext != null) {
                    //创建点击通知时发送的广播
                    Intent intentM2Kitkat = new Intent(mContext, NotificationService.class);
                    intentM2Kitkat.setAction(NotificationService.ACTION_CONVERSATION_LIST);
                    summary.contentIntent = PendingIntent.getService(mContext, 0, intentM2Kitkat, PendingIntent.FLAG_UPDATE_CURRENT);
                    nm.notify(0, summary);
                }
            }
        }

        //如果没有未读取消所有
        if(msgs.isEmpty() || count == 0){
            cancelAll();
        }
    }


    private void cancelAll(){
        if(nm != null) {
            nm.cancelAll();
        }
        msgs.clear();
    }


    private void sendNotification(String peer, String title, String content,
                                  Bitmap bitmapLarge, int totalUnread, long messageTime) {
        if(mContext == null) {
            return;
        }
        // android N lower
        if(isAndroid7Lower()){
            Notification ntf = buildM2Kitkat(totalUnread, false);
            if(ntf == null){
                return;
            }
            // 创建点击通知时发送的广播
            Intent intentSummary = new Intent(mContext, NotificationService.class);
            intentSummary.setAction(NotificationService.ACTION_CONVERSATION_LIST);
            ntf.contentIntent = PendingIntent.getService(mContext, 0, intentSummary, PendingIntent.FLAG_UPDATE_CURRENT);
            nm.notify(0, ntf);
        }else {
            int id;
            try {
                id = Integer.parseInt(peer);
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
            if(totalUnread == 1){
                // children notification
                nm.cancel(0);
                Notification notification = buildNotification(title,content,bitmapLarge, NotificationChannelUtils.GROUP_NEW_MESSAGE,messageTime);
                if(notification == null){
                    return;
                }
                // 创建点击通知时发送的广播
                Intent intent = new Intent(mContext, NotificationService.class);
                intent.setAction(NotificationService.ACTION_CONVERSATION);
                notification.contentIntent = PendingIntent.getService(mContext,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                nm.notify(id, notification);
            }else {
                //summary
                Notification summary = buildSummary(NotificationChannelUtils.GROUP_NEW_MESSAGE, totalUnread, false);
                if(summary == null){
                    return;
                }
                Intent intentSummary = new Intent(mContext, NotificationService.class);
                intentSummary.setAction(NotificationService.ACTION_CONVERSATION_LIST);
                summary.contentIntent = PendingIntent.getService(mContext, 0, intentSummary, PendingIntent.FLAG_UPDATE_CURRENT);
                //child
                Notification notification = buildNotification(title,content,bitmapLarge, NotificationChannelUtils.GROUP_NEW_MESSAGE,messageTime);
                if(notification == null){
                    return;
                }
                Intent intent = new Intent(mContext, NotificationService.class);
                intent.setAction(NotificationService.ACTION_CONVERSATION);
                notification.contentIntent = PendingIntent.getService(mContext,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                nm.notify(0, summary);
                nm.notify(id, notification);
            }
        }

    }


    private Notification buildNotification(String title, String content, Bitmap bitmapLarge, String groupKey, long messageTime) {
        if(mContext == null) {
            return null;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,NotificationChannelUtils.CHANNEL_ID_NEW_MESSAGE)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(messageTime)
                .setWhen(messageTime)
                .setShowWhen(true)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(groupKey);
        mBuilder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN);
        if (bitmapLarge != null) {
            //设置通知右侧的大图标
            mBuilder.setLargeIcon(bitmapLarge);
        }
        //静默
        if(groupKey != null){
            mBuilder.setSound(null);
            mBuilder.setVibrate(null);
        }else {
            if (!showRing && !showVibrate) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
                mBuilder.setSound(null);
                mBuilder.setVibrate(null);
            } else if (!showRing) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE);
                mBuilder.setSound(null);
            } else if (!showVibrate) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND);
                mBuilder.setVibrate(null);
            } else {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }
        return mBuilder.build();
    }


    private Notification buildSummary(String groupKey, int totalUnread, boolean update) {
        if(mContext == null) {
            return null;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,NotificationChannelUtils.CHANNEL_ID_NEW_MESSAGE);
        mBuilder.setContentTitle("App")
                .setContentText( (msgs.size()>0?msgs.size():1) + "个联系人发来" + totalUnread + "条消息")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowWhen(true)
                .setGroup(groupKey)
                .setGroupSummary(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setAutoCancel(true);

        if(update){
            mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
            mBuilder.setSound(null);
            mBuilder.setVibrate(null);
        }else {
            if(!showRing && !showVibrate) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
                mBuilder.setSound(null);
                mBuilder.setVibrate(null);
            }else if(!showRing){
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS| NotificationCompat.DEFAULT_VIBRATE);
                mBuilder.setSound(null);
            }else if(!showVibrate){
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS| NotificationCompat.DEFAULT_SOUND);
                mBuilder.setVibrate(null);
            }else {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher);
        if(bitmap != null){
            mBuilder.setLargeIcon(bitmap);
        }
        return mBuilder.build();
    }



    private Notification buildM2Kitkat(int totalUnread, boolean update) {
        if(mContext == null) {
            return null;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,NotificationChannelUtils.CHANNEL_ID_NEW_MESSAGE);
        mBuilder.setContentTitle("彼聊")
                .setContentText( (msgs.size()>0?msgs.size():1) + "个联系人发来" + totalUnread + "条消息")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowWhen(true)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setSound(RingtoneManager.getDefaultUri(TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[] {0,300})
                .setAutoCancel(true);
        if(update){
            mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
            mBuilder.setSound(null);
            mBuilder.setVibrate(null);
        }else {
            if(!showRing && !showVibrate) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
                mBuilder.setSound(null);
                mBuilder.setVibrate(null);
            }else if(!showRing){
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
                mBuilder.setVibrate(new long[] {0,300});
                mBuilder.setSound(null);
            }else {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
                mBuilder.setSound(RingtoneManager.getDefaultUri(TYPE_NOTIFICATION));
                mBuilder.setVibrate(null);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //VISIBILITY_PUBLIC: 任何情况的显示
            //VISIBILITY_PRIVATE: 只有在没有锁屏时显示
            //VISIBILITY_SECRET: 在安全锁下或者没锁屏下显示
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher);
        if(bitmap != null){
            mBuilder.setLargeIcon(bitmap);
        }
        return mBuilder.build();
    }



    public void clear(){
        msgs.clear();
        cancelAll();
    }


    public void setShowRing(boolean showRing) {
        this.showRing = showRing;
    }

    public void setShowVibrate(boolean showVibrate) {
        this.showVibrate = showVibrate;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }


    private static boolean isAndroid8Upper(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    private static boolean isAndroid7Lower(){
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N;
    }



    static class UnRead{

        private String lastId;
        private long time;
        private String iconUrl;
        private String userName;
        private ArrayList<MessageInfo> unReadList;


        public UnRead(ArrayList<MessageInfo> list, long msgTime) {
            this.unReadList = list;
            this.time = msgTime;
        }

        public String getLastId() {
            return lastId;
        }

        public void setLastId(String lastId) {
            this.lastId = lastId;
        }

        public int getCount() {
            if(unReadList != null) {
                return unReadList.size();
            }
            return 0;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public ArrayList<MessageInfo> getUnReadList() {
            return unReadList;
        }

        public void setUnReadList(ArrayList<MessageInfo> unReadList) {
            this.unReadList = unReadList;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public boolean addSuccess(MessageInfo messageInfo) {
            if (unReadList == null){
                unReadList = new ArrayList<>();
            }
            unReadList.add(messageInfo);
            return true;
        }
    }

    public static class MessageInfo{
        private String extra;
        private long msgTime;

        public MessageInfo(String extra, long msgTime) {
            this.extra = extra;
            this.msgTime = msgTime;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public long getMsgTime() {
            return msgTime;
        }

        public void setMsgTime(long msgTime) {
            this.msgTime = msgTime;
        }
    }



    private static String str = "据中国外交部,发言人赵立坚披露，加勒万河谷位,于中印边界,西段实际控制线中方一侧。多年来，中国边防部队一直在此,正常巡逻执勤。2020年4月以来，印度边防部队,单方面在加勒,万河谷地区持,续抵边修建道路、桥梁等设施。中方多次就此提出,交涉和抗议，但印方反而变,本加厉越线滋事。2020年5月6日凌晨，印度边防部队乘,夜色在加勒万河谷地区越,线进入中国领土、构工设障，阻拦中方边,防部队正常巡逻，蓄意挑起事端，试图单方面改变边境管控现状。中方边防部队不得不,采取必要措施，加强现场应对和边境地区管控。为缓和边境地区局势，中印双方通过军事和外,交渠道保持密切沟通。在中方强烈要求下，印方同意并撤出越线人员，拆除越线设施。6月6日，两国边防部队举行军长级会晤，就缓和边境地区局势达成共识。印方承诺不越过加勒,万河口巡逻和修建设施，双方通过现地指挥官会,晤商定分批撤军事";
    private static String[] strList = str.split(",");
    private static int index = 0;
    private MessageInfo generateMessageInfo(String peer){
        if(index >= strList.length){
            index = 0;
        }
        MessageInfo messageInfo = new MessageInfo(strList[index],System.currentTimeMillis());
        index++;
        return messageInfo;
    }



    public static boolean areNotificationEnabled(Context context){
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        boolean importanceOk = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = nm.getNotificationChannel(NotificationChannelUtils.CHANNEL_ID_NEW_MESSAGE);
            if (mChannel != null && mChannel.getImportance() == NotificationManagerCompat.IMPORTANCE_NONE) {
                importanceOk = false;
            }
        }
        return  NotificationManagerCompat.from(context).areNotificationsEnabled() && importanceOk;
    }



    /**
     * 跳转到权限设置界面
     */
    public static void open(Context context){

        // vivo 点击设置图标>加速白名单>我的app
        //      点击软件管理>软件管理权限>软件>我的app>信任该软件
        Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
        if(appIntent != null){
            context.startActivity(appIntent);
            return;
        }

        // oppo 点击设置图标>应用权限管理>按应用程序管理>我的app>我信任该应用
        //      点击权限隐私>自启动管理>我的app
        appIntent = context.getPackageManager().getLaunchIntentForPackage("com.oppo.safe");
        if(appIntent != null){
            context.startActivity(appIntent);
            return;
        }

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            NotificationManagerCompat mn = NotificationManagerCompat.from(context);
            NotificationChannel mChannel = mn.getNotificationChannel(NotificationChannelUtils.CHANNEL_ID_NEW_MESSAGE);
            if(mChannel != null) {
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, mChannel.getId());
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }



}


